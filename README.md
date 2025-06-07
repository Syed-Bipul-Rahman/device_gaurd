Device Guard
Overview
Device Guard is an Android application designed to monitor and protect your device. It provides real-time insights into your device's battery and network status, ensuring you stay informed about its health and connectivity. In the future, Device Guard will expand to include robust anti-theft and security features, such as remote locking, sound playback to locate a lost device, and data erasure to safeguard sensitive information.
Features
Current Features

Battery Monitoring: Displays battery percentage, charging status (e.g., Charging, Full), and health (e.g., Good, Overheat) with color-coded indicators (green for good, orange/red for issues).
Network Information: Shows network type (WiFi, Mobile Data), WiFi SSID, mobile network type (e.g., 4G LTE, 3G), SIM operator, SIM state, and network status (green for connected, red for disconnected).
Device Information: Displays the device model and a "Last Seen" timestamp.
Real-Time Updates: Uses Android's BroadcastReceiver to provide live updates on battery and network conditions.
Permission Management: Requests necessary permissions (Network State, WiFi State, Phone State, and Location for Android 10+) to ensure accurate data retrieval.

Upcoming Features

Remote Lock: Secure your device by locking it remotely to prevent unauthorized access.
Locate Device: Play a sound to help find a lost or misplaced device, even if it’s on silent mode.
Data Erasure: Remotely wipe sensitive data to protect your information in case of theft or loss.

Roadmap
Device Guard is actively being developed to enhance its security capabilities. Planned features include:

Integration with a cloud service for remote device management.
Support for device administration permissions to enable locking and wiping functionality.
Enhanced user interface for managing security settings.

Prerequisites
To build and run Device Guard, you need:

Android Studio 4.2 or higher
Android SDK with API Level 21 (Lollipop) or higher
A device or emulator running Android 5.0+
Gradle (included with Android Studio)
(Future) Internet access for remote features (e.g., Firebase or custom server setup)

Installation

Clone the Repository:git clone https://github.com/Syed-Bipul-Rahman/device_gaurd.git


Open in Android Studio:
Launch Android Studio and select Open an existing project.
Navigate to the cloned device_gaurd directory and open it.


Sync and Build:
Click Sync Project with Gradle Files in Android Studio.
Build the project using Build > Make Project.


Run the App:
Connect an Android device or start an emulator.
Click Run > Run 'app' to install and launch the app.



Usage

Launch the App: Open Device Guard on your Android device.
Grant Permissions: Allow permissions for Network State, WiFi State, Phone State, and Location (for Android 10+). Future features may require Device Admin permissions.
View Device Information:
Device Card: Shows the device model, battery percentage, and network type.
Battery Details: Displays battery status and health with color indicators.
Network Details: Shows network type, WiFi SSID, SIM operator, SIM state, and network status.


Monitor in Real-Time: The app updates automatically as conditions change.
(Future) Security Features: Once implemented, access remote lock, sound playback, and data erase options via the app or a web interface.

Contributing
Contributions are welcome to enhance Device Guard’s functionality! To contribute:

Fork the repository.
Create a feature branch (git checkout -b feature/your-feature).
Commit your changes (git commit -m 'Add your feature').
Push to the branch (git push origin feature/your-feature).
Open a Pull Request.

Please follow Android coding standards (e.g., Kotlin style guide) and include tests for new features.
License
This project is licensed under the MIT License - see the LICENSE file for details.
Contact
For questions or feedback, please open an issue on the GitHub Issues page.
Acknowledgments

Built with Android Jetpack for view binding.
Thanks to the Android community for extensive documentation and support.
(Future) Acknowledgments for cloud services or libraries used in anti-theft features.

