# Empire-mobile-Android
An Android GUI for [Empire](https://github.com/EmpireProject/Empire/tree/3.0-Beta) post-exploitation framework via RESTful API. 

## Installing Application
The easiest way to install the Empire app is to email yourself the compiled/signed .apk and click on it.  You will need to go your devices Settings -> Security, and turn on the "Unknown Sources" option which allows you to install apps outside of the Google Play Store. 

Another option is to use Android Debug Bridge (ADB), which is an official Android SDK tool.  To connect to your device with ADB, you will need to turn on "USB Debugging" in the developer options.  To unlock developer options on your device, head to Settings -> About, and tap on Build 7 to 8 times until a toast pops up telling you that the options are unlocked.  

You can download the Android SDK platform-tools [here](https://developer.android.com/studio/releases/platform-tools).  ADB can also be installed by itself via "apt-get" on debian based systems.

Connect ADB to device by IP address and install .apk.
```
adb connect X.X.X.X
adb devices
adb install /path/to/empire/apk
```

## Install Empire phone certificate
Using the new cert.sh script included in this repo, an "empire.crt" cert file will be generated.  Much like installing the apk, the easiest way to install this cert, is to email it to yourself and tap it.  

Second option is to use ADB by pushing it to the device.
```
adb devices
adb push /path/to/empire/crt /sdcard/empire.crt
```

Once the device is pushed to the device, you will have to head to Settings -> Security -> Install from SDcard, and tap on the cert.  

## Roll your own apk.
If you desire to make alterations or simply compile your own apk, you will need to generate a self-signed cert and sign the apk before it can be installed on a device.  [Android studio](https://developer.android.com/studio/publish/app-signing) can help you generate a cert and sign the compiled app.  

### More info on the app usage in [this blog](https://pickles.xyz).
