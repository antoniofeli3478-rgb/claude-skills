import os
from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from copilotkit import LangGraphAgent, CopilotKitRemoteEndpoint
from copilotkit.integrations.fastapi import add_fastapi_endpoint
from contextlib import asynccontextmanager

from agent import graph
from utils.whatsapp import send_text_message

# Create a lifespan context manager
@asynccontextmanager
async def lifespan():
    # Startup: Check for required environment variables
    openai_api_key = os.getenv("OPENAI_API_KEY")
    groq_api_key = os.getenv('GROQ_API_KEY')
    sender_phone = os.getenv('SENDER_PHONE')
    facebook_auth_token = os.getenv("FACEBOOK_AUTH_TOKEN")

    if not openai_api_key:
        raise ValueError("OPENAI_API_KEY environment variable is not set.")
    
    if not groq_api_key:
        raise ValueError("GROQ_API_KEY environment variable is not set.")
    
    if not sender_phone:
        raise ValueError("SENDER_PHONE environment variable is not set.")

    if not facebook_auth_token:
        raise ValueError("FACEBOOK_AUTH_TOKEN environment variable is not set.")


    yield

app = FastAPI(lifespan=lifespan)

@app.on_event("startup")
async def startup_event():
    # Check for required environment variables
    openai_api_key = os.getenv("OPENAI_API_KEY")
    facebook_auth_token = os.getenv("FACEBOOK_AUTH_TOKEN")

    if not openai_api_key:
        raise ValueError("OPENAI_API_KEY environment variable is not set.")

    if not facebook_auth_token:
        raise ValueError("FACEBOOK_AUTH_TOKEN environment variable is not set.")

    # If you want to store these keys for later use within the application
    app.state.openai_api_key = openai_api_key
    app.state.facebook_auth_token = facebook_auth_token

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Allows all origins
    allow_credentials=True,
    allow_methods=["*"],  # Allows all methods
    allow_headers=["*"],  # Allows all headers
)

sdk = CopilotKitRemoteEndpoint(
    agents=[
        LangGraphAgent(
            name='agent',
            description='Appointment booking agent.',
            graph=graph
        )
    ]
)

add_fastapi_endpoint(app, sdk, '/copilotkit/')

@app.get('/')
def health():
    return JSONResponse({"message": "App is healthy!"})

@app.get('/webhook')
def facebook_webhook(request: Request):
    verificaton_token = request.query_params.get('hub.verify_token')
    challenge = request.query_params.get('hub.challenge')

    if verificaton_token and verificaton_token == 'aB3xY7pQ9rT2vF4sG6hK8jL0':
        return int(challenge)


@app.post('/webhook')
async def whatsapp_workflow(request: Request):
    body = await request.json()

    # print("BODY: ", body)
    entry = body.get('entry')[0]
    changes = entry.get('changes')[0]
    
    if 'messages' not in changes.get('value') or 'contacts' not in changes.get('value'):
        return JSONResponse({"message": "Not a valid message object"})
    
    contact = changes.get('value').get('contacts')[0]
    message = changes.get('value').get('messages')[0]

    content = None
    type = message.get('type')

    if type == 'image' or type == 'video':
        await send_text_message("Sorry, I can't process images and videos")
        return JSONResponse({"message": 'invalid message type'})
    
    if type == 'text':
        content = message.get('text').get('body')

    if type == 'audio':
        content = message.get('audio').get('id')
    
    if not content or content is None:
        return JSONResponse({'message': 'Invalid message'})
    config = {"configurable": {"thread_id": contact.get('wa_id')}}
    response = graph.invoke({"messages": [{"role": "user", "content": content}], "type": type}, config)
    ai_message = response['messages'][-1]
    
    await send_text_message(ai_message.content)
    return JSONResponse({"message": 'message sent'})

if __name__ == '__main__':
    import uvicorn
    uvicorn.run('main:app', host='127.0.0.1', port=5001, reload=True)