# Led Controller

All-in-one Smartphone-controlled Led Controller for NodeMCU (ESP-8266).  

## Requirements

Requires NodeMCU 1.5+ with CJSON, PWM, mDNS, file, net, tmr, wifi

## Setup

Transfer the content using the upload.sh or upload.cmd scripts

## Setup Instructions

### 1. First setup
At first power-on, your LEDs will be blinking blue.  
Find the Wifi Access-Point named "led-SOMETHING", and connect to it from your laptop, your smartphone, or any Wireless device.  
Open your browser and access http://10.0.0.1  

Enter your Wifi credentials, and choose a personal password key for your LED controller. This password will be asked everytime you want to bind your controller to a smartphone.

When registered, your LED Controller will restart and blink yellow. Yellow means "I am trying to connect with the credentials you gave me".

If the LED Controller succeeds, the light will turn Green and stop blinking. That means your LED Controller setup is successful.

Otherwise, after 30 seconds, the LEDs will blink red. This means that your settings did not work correctly.
You can safely reboot (take off the plug, and plug it on again) your LED Controller and start over.

### 2. Updates
Any Wifi network/password change will require the LED Controller to be setup once again. This is a safety measure.

### 3. Android App
When you have installed the APK on your phone, simply launch it and click on "Re-Sync" in the settings tab.
Make sure you are connected on the same wifi network as your LED Controller.

When your Controller is successfully found, a Dialog will pop-up so you can enter your password key.

You're done! The Controller is now successfully paired to your Android App!
