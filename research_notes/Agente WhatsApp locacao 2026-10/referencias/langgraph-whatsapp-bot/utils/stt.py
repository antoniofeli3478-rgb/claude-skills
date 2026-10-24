import os
from groq import Groq

GROQ_API_KEY = os.environ.get('GROQ_API_KEY')
client = Groq(api_key=GROQ_API_KEY)

def speech_to_text(file_path: str) -> str:
    with open(file_path, "rb") as file:
        transcription = client.audio.transcriptions.create(
        file=(file_path, file.read()),
        model="whisper-large-v3",
        response_format="verbose_json",
        )
        print(transcription.text)

        return transcription.text
      