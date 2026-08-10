# Bluetooth Controller (Root Audio Router)

An Android application designed to split audio routes (forcing media stream to Bluetooth A2DP while routing system/voice/calls through the built-in speaker/earpiece). 

Targeted specifically at devices without native "Separate App Sound" features, requiring **Superuser (Root) Access**.

## How It Works (Root Level)
1. **TinyMix Direct Routing:** Controls the hardware mixers of Qualcomm/UNISOC soundcards to split routing paths directly in ALSA.
2. **AudioServer Intervention:** Forcefully sets persistent routing system properties and restarts the `audioserver` daemon.
3. **Service Calls:** Directly invokes internal binder service functions in `audio` (`IAudioService`) to manipulate routing logic.

## Project Structure
- `app/src/main/java/com/unisocsu/bluetoothcontroller/ShellUtils.java` - Core execution of root commands through `su`.
- `app/src/main/java/com/unisocsu/bluetoothcontroller/AudioRouter.java` - Implementation of low-level routing commands.
- `app/src/main/java/com/unisocsu/bluetoothcontroller/MainActivity.java` - UI with buttons to trigger routing commands and display shell outputs.

## Requirements
- Android SDK 26+
- Rooted Device with Magisk or SuperSU.
- Standard ALSA mixer/tinymix (typically present on Unisoc/Qualcomm devices).

## Building
Import this project in Android Studio, click **Build** -> **Make Project**, and run on a rooted device.
