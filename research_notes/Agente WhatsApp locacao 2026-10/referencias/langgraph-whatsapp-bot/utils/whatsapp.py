import os
import json
import aiohttp
import requests

FACEBOOK_AUTH_TOKEN = os.environ.get('FACEBOOK_AUTH_TOKEN')
AUTH_TOKEN = f'Bearer {FACEBOOK_AUTH_TOKEN}'

headers = {
    'Authorization': AUTH_TOKEN,
    'Content-Type': 'application/json'
}

async def send_text_message(message: str):
    SENDER_PHONE = os.environ.get('SENDER_PHONE')
    url = f"https://graph.facebook.com/v22.0/{SENDER_PHONE}/messages"

    payload = json.dumps({
        "messaging_product": "whatsapp",
        "to": "923016891361",  # This is a constant for development purposes
        "type": "text",
        "text": {
            "body": message
        }
    })

    async with aiohttp.ClientSession() as session:
        async with session.post(url, headers=headers, data=payload) as response:
            response_text = await response.text()
            print(response_text)



def get_media_url(media_id):
    url = f"https://graph.facebook.com/v22.0/{media_id}"
    response = requests.get(url, headers={'Authorization': AUTH_TOKEN})
    return response.json()

def download_audio_file(url, path='audio_file'):
    response = requests.get(url, headers={'Authorization': AUTH_TOKEN})
    with open(f"{path}.ogg", "wb") as audio_file:
        audio_file.write(response.content)

# media_id = "686071964344206"  # Replace with the actual media ID
# media_data = get_media_url(media_id)
# audio_url = media_data["url"]
# download_audio_file(audio_url)
