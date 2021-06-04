![20200808_160757](https://1.bp.blogspot.com/-FgzZ6s4snE0/YLonted4bRI/AAAAAAAAA7M/btcFeIotLw42dSwU9AioHomX_GcG-xmogCLcBGAsYHQ/s1429/68747470733a2f2f312e62702e626c6f6773706f742e636f6d2f2d71374d454847336e4851342f583979763436467a3543492f41414141414141414152342f574a46387a3046766b6251325968725f6c6356686a6375473371306b5030587077434c63424741735948512f7.png)

<noscript><a href="https://liberapay.com/muneeb/donate"><img alt="Donate using Liberapay" src="https://liberapay.com/assets/widgets/donate.svg"></a></noscript>

# Dash
This is an Android Spyware App, Which uploads user data such as Contacts, Messages, Call log &amp; recordings, Send messages, Photos, Videos, etc.

The application is installed on the child's device as well as on the parent's device, in the login view the type of user is chosen.

# Feacture
- Multiple Child clients
- Hidden app icon (stealth mode)
- Real-time location.
- Recording calls: incoming/outgoing.
- SMS: received/sent.
- Environment recording.
- Take pictures.
- Keylogger.
- Phishing social network.
- Notifications received: Whatsapp, Instagram, Messenger.

# Build this project
the application work with the api of firebase with which you will have to create a project in firebase and synchronize the application with such project.
[Firebase API](https://firebase.google.com/)

Enable the following development platforms on firebase:
`Authentication`, `realtime database` and `storage`.

- in authentication/sign-in method enable the `email` access provider

- in firebase real-time database assign the following rules:
```java
{
  "rules": {
    ".read": "auth != null",
      ".write": "auth != null"
  }
}
```

- in firebase storage assign the following rules:
```java
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

- In the `build.gradle` assign the social network package of your preference.
also you will have to recreate the view in xml of the social network
```java
ext {
       PACKAGE_CHECK_SOCIAL = "\"PHISHING-SOCIAL_NETWORK\""
}
```

In the `res/values/string.xml` assign your `APY_KEY_MAPS`

- Get the GOOGLE MAPS API KEY [here](https://developers.google.com/maps/documentation/android-api/signup)
```java
<string name="APY_KEY_MAPS">YOU_API_KEY_MAPS</string>
```

note: it is very important that accept all the necessary permissions for the application to work properly

# Disclaimer
The Dash application is intended for legal and educational purposes ONLY. It is a violation of the law to install surveillance software on a mobile phone that you have no right to monitor.

Dash is not responsible if the user does not follow the laws of the country and goes against it. If it is found that the user violates any law or spy in secret, he will be subject to sanctions that govern the legislation of the country.


# License

```java 
Copyright [2020] [muneebwanee]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
