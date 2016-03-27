# Led Controller

All-in-one Smartphone-controlled Led Controller for NodeMCU (ESP-8266).  


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
