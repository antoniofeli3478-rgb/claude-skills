import os
from typing import Annotated, TypedDict
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder

from langgraph.graph.message import add_messages
from langgraph.graph import START, END, StateGraph
from langgraph.prebuilt import ToolNode, tools_condition
from langgraph.checkpoint.memory import InMemorySaver

from tools.google_calender import (
    create_event,
    get_week_events, 
    get_today_events,
)
from utils.stt import speech_to_text
from utils.prompts import appointment_system_prompt
from utils.whatsapp import get_media_url, download_audio_file

tools = [get_today_events, get_week_events, create_event]

llm = ChatOpenAI(
    model='gpt-4o-mini',
    temperature=0.2,
    api_key=os.environ.get('OPENAI_API_KEY')
)

llm_with_tools = llm.bind_tools(tools)

prompt = ChatPromptTemplate([
    ('system', appointment_system_prompt),
    MessagesPlaceholder('messages')
])

chain = prompt | llm_with_tools

class State(TypedDict):
    type: str
    messages: Annotated[list, add_messages]

def determine_next_node(state: State):
    if state['type'] not in ['text', 'audio']:
        return END
    
    return state['type']

def router(state: State):
    return state

def chatbot(state: State) -> State:
    return {"messages": [chain.invoke(state['messages'])]}

def audio_to_text(state: State) -> State:
    message = state['messages'][0]

    audio_id = message.content
    media_data = get_media_url(audio_id)

    audio_url = media_data["url"]

    file_path = 'audio_file'
    download_audio_file(audio_url, file_path)

    text = speech_to_text(file_path=f'{file_path}.ogg')
    # media_id = "686071964344206"  

    updated_message = {"role": "user", "content": text}
    state['messages'][0] = updated_message
    state['type'] = 'text'
    return state

tool_node = ToolNode(tools)

graph_builder = StateGraph(State)

graph_builder.add_node('router', router)
graph_builder.add_node('chatbot', chatbot)
graph_builder.add_node('audio', audio_to_text)
graph_builder.add_node('tools', tool_node)

graph_builder.add_conditional_edges(
    'router',
    determine_next_node,
    {"text": "chatbot", "audio": "audio", END: END}
)
graph_builder.add_edge(START, 'router')
graph_builder.add_edge('audio', 'chatbot')
graph_builder.add_conditional_edges(
    'chatbot',
    tools_condition
)
graph_builder.add_edge('tools', 'chatbot')
graph_builder.add_edge('chatbot', END)

checkpointer = InMemorySaver()

graph = graph_builder.compile(checkpointer=checkpointer)
