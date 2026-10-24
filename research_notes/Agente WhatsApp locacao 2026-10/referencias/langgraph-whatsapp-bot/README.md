# 🦷 WhatsApp Dental Appointment Bot

This project implements a WhatsApp-based AI assistant for dental clinics, enabling patients to book appointments via text or voice messages. It leverages LangGraph for conversational flows, FastAPI for the backend, and integrates with Google Calendar for scheduling.

<center>
<img width="817" alt="Screenshot 2025-06-04 at 2 47 41 PM" src="https://github.com/user-attachments/assets/d1a25233-cf68-450e-b8e9-c43530d74f46" />
</center>

## 🧠 Features

* **Multi-Modal Communication**: Handles both text and voice messages.
* **Voice Transcription**: Transcribes audio messages using the Whisper model via Groq.
* **Conversational AI**: Processes user inputs through LangGraph-powered chatbot.
* **Google Calendar Integration**: Checks for available slots and creates appointments.
* **FastAPI Backend**: Provides API endpoints for WhatsApp webhook and other functionalities.
* **Project Management with `uv`**: Utilizes `uv` for dependency management and project setup.

## 🚀 Getting Started

### Prerequisites

* Python 3.12 or higher
* `uv` installed globally

### Installation

1. **Clone the repository**:

   ```bash
   git clone https://github.com/yourusername/whatsapp-dental-bot.git
   cd whatsapp-dental-bot
   ```



2. **Initialize the project with `uv`**:

   ```bash
   uv init --app
   ```



3. **Add dependencies**:

   ```bash
   uv add fastapi langgraph langchain langchain_groq google-api-python-client python-dotenv
   ```



4. **Set up environment variables**:

   Create a `.env` file in the root directory and add the following:

   ```env
   GROQ_API_KEY=your_groq_api_key
   OPENAI_API_KEY=oepnai_api_key
   FACEBOOK_AUTH_TOKEN=facebook_auth_token
   SENDER_PHONE=sender_phone
   ```



Ensure you have the necessary credentials for Google Calendar API access.

5. **Run the application**:

   ```bash
   uv run main.py
   ```



The application will be available at `http://127.0.0.1:5001`.

## 🛠️ LangGraph Flow

The conversational flow is managed by LangGraph with the following logic:

* **Text Message**: Directly processed by the `chatbot` node.
* **Audio Message**: Routed to the `audio` node for transcription, then passed to the `chatbot` node.
* **Chatbot Node**: Interacts with the `tools` node to perform actions like checking and creating appointments in Google Calendar.

This modular design allows for easy scalability and maintenance.

## 📅 Google Calendar Integration

The `tools` node utilizes the Google Calendar API to:

* **Check Availability**: Search for free time slots.
* **Create Appointments**: Schedule new appointments based on user input.

### Setting Up Google Calendar API Credentials

1. **Create a Google Cloud Project**:

   * Go to the [Google Cloud Console](https://console.cloud.google.com/).
   * Click on **Select a project** and then **New Project**.
   * Enter a project name and click **Create**.([support.google.com][2])

2. **Enable Google Calendar API**:

   * In the [APIs & Services Dashboard](https://console.cloud.google.com/apis/dashboard), click **+ ENABLE APIS AND SERVICES**.
   * Search for "Google Calendar API" and click on it.
   * Click **Enable** to activate the API for your project.([endgrate.com][3])

3. **Configure OAuth Consent Screen**:

   * Navigate to **APIs & Services > OAuth consent screen**.
   * Choose **External** or **Internal** based on your needs and click **Create**.
   * Fill out the required fields such as App name, User support email, and Developer contact information.
   * Click **Save and Continue** through the scopes and test users sections.([endgrate.com][3], [developers.google.com][4])

4. **Create OAuth 2.0 Credentials**:

   * Go to **APIs & Services > Credentials**.
   * Click **+ CREATE CREDENTIALS** and select **OAuth client ID**.
   * Choose **Desktop app** as the application type.
   * Enter a name and click **Create**.
   * Click **Download JSON** to obtain your `credentials.json` file.([developers.google.com][1], [support.google.com][2], [developers.google.com][4])

5. **Place `credentials.json` in Your Project**:

   * Move the downloaded `credentials.json` file to the root directory of your project.
   * Ensure this file is included in your `.gitignore` to prevent it from being committed to version control.

6. **Authenticate and Generate `token.json`**:

   The first time you run your application, it will prompt you to authenticate with your Google account. Upon successful authentication, a `token.json` file will be created, storing your access and refresh tokens.

   **Note**: The `token.json` file is user-specific and should also be excluded from version control.

For more detailed instructions, refer to the [Python Quickstart Guide for Google Calendar API](https://developers.google.com/calendar/api/quickstart/python).

## 🎤 Audio Transcription with Whisper

Audio messages received from WhatsApp are processed as follows:

1. **Reception**: Audio message is received via the FastAPI endpoint.
2. **Transcription**: The `audio` node uses the Whisper model via Groq to transcribe the audio into text.
3. **Processing**: Transcribed text is then passed to the `chatbot` node for further handling.

This ensures seamless handling of both text and voice inputs.

## 📞 WhatsApp Integration via Meta

To integrate WhatsApp with your application, follow these steps:

### 1. Create a Meta Developer Account

* Visit [Meta for Developers](https://developers.facebook.com/) and log in with your Facebook account.
* Accept the terms and set up your developer profile.

### 2. Create a Meta App

* Navigate to [My Apps](https://developers.facebook.com/apps) and click on **Create App**.
* Select **Other** as the app type and click **Next**.
* Choose **Business** as the app type and click **Next**.
* Enter an **App Name**, provide a **Contact Email**, and select your **Business Account** (if available). Click **Create App**.

### 3. Add WhatsApp Product to Your App

* In your app's dashboard, scroll down to the **Add Products to Your App** section.
* Find **WhatsApp** and click **Set Up**.
* Follow the on-screen instructions to set up the WhatsApp Business API.

### 4. Configure WhatsApp Business Account

* In the **WhatsApp** section of your app dashboard, navigate to **Getting Started**.
* Select or create a **WhatsApp Business Account (WABA)**.
* Add and verify a phone number to be used for sending messages.
* Generate a **Permanent Access Token** with the necessary permissions:

  * `whatsapp_business_messaging`
  * `whatsapp_business_management`

### 5. Set Up Webhooks

* In your app dashboard, go to **Webhooks** and click **Set Up Webhooks**.
* Select the **WhatsApp Business Account**.
* Provide a **Callback URL** (your FastAPI endpoint) and a **Verify Token**.
* Subscribe to the following fields:

  * `messages`
  * `message_deliveries`
  * `message_reads`

### 6. Test Your Setup

* Use the **Test Number** provided in the **Getting Started** section to send

[1]: https://developers.google.com/workspace/guides/create-credentials?utm_source=chatgpt.com "Create access credentials | Google Workspace"

[2]: https://support.google.com/googleapi/answer/6158849?hl=en&utm_source=chatgpt.com "Setting up OAuth 2.0 - API Console Help - Google Help"
[3]: https://endgrate.com/blog/how-to-get-calendar-events-with-the-google-calendar-api-in-python?utm_source=chatgpt.com "How to Get Calendar Events with the Google Calendar API in Python"
[4]: https://developers.google.com/workspace/calendar/api/quickstart/python?utm_source=chatgpt.com "Python quickstart | Google Calendar"
