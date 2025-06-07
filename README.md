# 🔐 Device Guard

## Overview

Device Guard is an Android application designed to monitor and protect your device. It provides real-time insights into your device's battery and network status, ensuring you stay informed about its health and connectivity. Device Guard will have robust anti-theft and security features, such as remote locking, sound playback to locate a lost device, and data erasure to safeguard sensitive information.

---

## 📋 Features

### ✅ Current Features

- **🔋 Battery Monitoring**:  
  Displays battery percentage, charging status (e.g., Charging, Full), and health (e.g., Good, Overheat) with color-coded indicators (green for good, orange/red for issues).

- **📶 Network Information**:  
  Shows network type (WiFi, Mobile Data), WiFi SSID, mobile network type (e.g., 4G LTE, 3G), SIM operator, SIM state, and network status (green for connected, red for disconnected).

- **📱 Device Information**:  
  Displays the device model and a "Last Seen" timestamp.

- **🔁 Real-Time Updates**:  
  Uses Android's `BroadcastReceiver` to provide live updates on battery and network conditions.

- **🔒 Permission Management**:  
  Requests necessary permissions (`Network State`, `WiFi State`, `Phone State`, and `Location` for Android 10+) to ensure accurate data retrieval.

- **🔐 Remote Lock**:  
  Secure your device by locking it remotely to prevent unauthorized access.

- **🔊 Locate Device**:  
  Play a sound to help find a lost or misplaced device, even if it’s on silent mode.

- **🧨 Data Erasure**:  
  Remotely wipe sensitive data to protect your information in case of theft or loss.

---

## 🚀 Roadmap

Device Guard is actively being developed to enhance its security capabilities. Planned features include:

- Integration with a cloud service for remote device management.
- Support for device administration permissions to enable locking and wiping functionality.
- Enhanced user interface for managing security settings.

---

## 🧪 Usage

### 1. **Launch the App**  
Open *Device Guard* on your Android device.

### 2. **Grant Permissions**  
Allow permissions for:
- Network State  
- WiFi State  
- Phone State  
- Location (for Android 10+)  

> Future features may require Device Admin permissions.

### 3. **View Device Information**

#### 📦 Device information  
Shows the device model, battery percentage, and network type.

#### ⚡ Battery Details  
Displays battery status and health with color indicators.

#### 🌐 Network Details  
Shows network type, WiFi SSID, SIM operator, SIM state, and network status.

### 4. **Monitor in Real-Time**  
The app updates automatically as conditions change.

### 5. **Security Features**  
Access remote lock, sound playback, and data erase options via the app or a web interface.

---

## 📬 Contact

For questions or feedback, please open an issue on the [GitHub Issues page](https://github.com/Syed-Bipul-Rahman/device_gaurd/issues). 
