# Empire-mobile-iOS
An iOS GUI for [Empire](https://github.com/EmpireProject/Empire/tree/3.0-Beta) post-exploitation framework via RESTful API. 

## Compiling and signing the application
In order to install this app, the .ipa will need to be signed with a valid Apple developer ID.  You can sign up for a free Apple dev account, but Apple has limitations on free accounts.  This means that the provision file that is created when the app is signed, will have a 7 day life span.  So in 7 days, the app will just stop running.  You can always resign/reinstall every 7 days, or you can pony up the dough for a $99 a year [Apple Developer License](https://developer.apple.com/support/compare-memberships/) and not have to deal with this. 

With XCode open, head to the XCode tab and select Preferences.  Here you can add your Apple ID you want to use to sign apps/create provisioning files.

Open the Empire-Mobile.xcworkspace file which will launch the workspace and project in XCode.  This project makes use of a few CocoaPods (3rd party libraries), hence the use of a workspace instead of the project file.  I have the following CocoaPod libraries included in this repo to make things easy:

### [Alamofire](https://github.com/Alamofire/Alamofire)
### [SwiftyJSON](https://github.com/SwiftyJSON/SwiftyJSON)
### [PKHUD](https://github.com/pkluz/PKHUD)

Step 1: Product -> Build will start compiling the application. 

Step 2: Product -> Destination -> Generic iOS Device

Step 3: Product -> Archive

Step 4: Select Distribute, then select the Development radio button.

Step 5: Here you can strip symbols, which would be good if this was an App Store release where we wanted to make debugging more difficult.  You can also enable installation over-the-air if you so desire.  Click Next.

Step 6: If you have added your Apple Dev ID to Xcode, you can have Xcode automatically handle signing, or you can do it manually.  Click Next, and once the compiling is done, click Export.  This will produce a signed .ipa file which can be installed via Xcode. 

## Installing the Application
There are multiple ways of getting the app sideloaded onto your device(does not need to be jailbroken), but I will only cover one.

The most trustworthy way to sign and sideload apps onto your device is to install [Apple's XCode](https://developer.apple.com/xcode/).  

Make sure the device is connected, then go to Windows -> Devices.  Select the device you want to sideload the app on, and drag the .ipa file into the window.  

## Installing the Empire server certificate
Installing certs in iOS can be a pain as well.  You can email it to yourself, but you must use the Apple Mail app or you may get errors.  Another option is hosting it locally via python simpleHTTP server or Apache2.  If you choose to host and download the cert, you must use Safari to get the Apple prompt to install the cert.  Self-signed certs are not automatically trusted in iOS, so you will need to go to Settings -> General -> About -> Certificate Trust Settings, and approve this new certificate.

### More info on the app usage in [this blog](https://pickles.xyz).
