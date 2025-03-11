# Scanned Notify

## Overview
This is a Burp Suite extension that notifies you via Gmail when a Task Scan is complete.

## Features
- Gmail sending: Send emails using an app password

## Installation
1. **Download Jar:** Get the latest version from the [release page](https://github.com/riiim400th/scanned_notify/releases). 
2. **Install in Burp Suite:**
   - Go to the "Extender" tab, then "Extensions".
   - Click "Add" and select the downloaded jar. 🔧

## Usage 
1. Install the entension to BurpSuite.

2. Generate the App Password  [here](https://myaccount.google.com/apppasswords)

   Please enable two-factor authentication for the Gmail account you want to use.

3. After generating the app password, fill in the input from the "Scanned Notify" tab in Burp Suite.
     ![image](https://github.com/user-attachments/assets/e470b069-bab6-4107-8467-8d6380b20f62)

4. Test whether the recipient receives the email by using `Send Test Mail` .
5. Start the scan in Burp Suite. A completion email will be sent after the scan finishes.

## Note
A completion email will also be sent for scans not created by the user (e.g., scans triggered by other extensions or default passive crawling). To avoid unnecessary emails, turn on the "Scanned Notify" tab's Enable option only when needed.

Additionally, if an upstream proxy is configured, the email sending request to Gmail may pass through the proxy. Please ensure proper handling of this.

## Contributing
Contributions are welcome! If you encounter any issues or have suggestions for improvements, please open an issue or submit a pull request on the project's GitHub repository.

