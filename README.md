# MCS SDK for Android with Tutorial

[ ![jCenter](https://api.bintray.com/packages/mtk-mcs/maven/MCS-Android/images/download.svg) ](https://bintray.com/mtk-mcs/maven/MCS-Android/_latestVersion)
[ ![License](http://img.shields.io/:license-apache-blue.svg) ](https://github.com/Mediatek-Cloud/mcs-sdk-android/blob/master/LICENSE)

A quick start that guide you through MCS SDK for Android (or equivalently MCS Android SDK). 

The MCS SDK for Android enables you to build Android apps to interact with the powerful [MediaTek Cloud Sandbox (MCS)](https://mcs.mediatek.com/) platform for IoT devices. Features including:

* Auth: Manage access tokens for MCS.
* Enable push notifications on mobile devices.
* Get a list of a user's MCS devices.
* Get data channels of devices.
* Retrieve and upload data points of data channels.

#### Beta notice

The MCS SDK for Android is still under development. Though it's tested and mostly API-stable, bugs and other issues might be present. The APIs are subject to change, too.


## Getting Started

Before install MCS SDK for Android, please prepare as the following:

1. Signup for an [MCS](https://mcs.mediatek.com/) Account.
2. Obtain your **CLIENT_ID** 
from the [Service Provider](https://mcs.mediatek.com/v2console/console/profile) section of your MCS Profile. 
3. Create your first prototype and device.
4. Review the [MCS API Documentation](https://mcs.mediatek.com/resources/latest/api_references/).


## Download

Download [the latest JAR](https://bintray.com/mtk-mcs/maven/MCS-Android/_latestVersion) or define in Gradle:

```
compile 'com.mediatek.mcs:mcs-android:0.0.2'
```

For the SNAPSHOT version:

```
compile 'com.mediatek.mcs:mcs-android:0.0.3-SNAPSHOT'
```
Snapshots of the development version are available in [JFrog's snapshots repository](http://oss.jfrog.org/oss-snapshot-local/). So it's necessary to add the repository: 

```
maven { url "http://oss.jfrog.org/oss-snapshot-local/" }	
```


## Setup

There are 2 different ways to setup MCS SDK for Android,

A. Setup without Push Installation
B. Setup with Push Installation

It depends on whether you need the push notification feature.


### A. Setup without Push Installation

> Follow this section if you DO NOT NEED the push notification feature

1) Initialize in `Application` class

Call `Mcs.initialize` from the `onCreate` method of your `Application` class to set your application id and client key:


```
public class TutorialApplication extends Application {
  @Override public void onCreate() {
    // Add this lines in your extended Application class
    Mcs.initialize(this, "YOUR_CLIENT_ID");
  }
}
```

2) `AndroidManifest.xml`

First, specify the application you just set

```
  <!--
    IMPORTANT: Change ".TutorialApplication" in the lines below
    to match your application class.
  -->
<application
      android:name=".TutorialApplication"
      android:allowBackup="true"
      android:icon="@mipmap/ic_launcher"
      android:label="@string/app_name"
      android:theme="@style/AppTheme"
      >
```

NOTE: Change the `android:name` attribute of `<application>` tag above to match your application class.

3) DONE! Compile and run your app.

The above codes are minimum requirement to setup MCS SDK, please reference to other code in this tutorial project or [MCS Android Tutorial Doc](https://www.gitbook.com/book/mtk-mcs/mcs-android-tutorial-doc/details) for more customization.



### B. Setup with Push Installation

> Follow this section if you NEED the push notification feature

0) Create your own GCM project and get your **GCM_SENDER_ID** and **GCM_API_KEY** from Google Developers Console according to the guide of [Google Cloud Messaging](https://developers.google.com/cloud-messaging/).

1) Initialize in `Application` class

Call `Mcs.initialize` from the `onCreate` method of your `Application` class to set your application id and client key:

```
public class TutorialApplication extends Application {
  @Override public void onCreate() {
    // Add these lines in your extended Application class
    Mcs.initialize(this, "YOUR_CLIENT_ID");
    McsPushInstallation.getInstance().registerInBackground(
    	"YOUR_GCM_SENDER_ID", "YOUR_GCM_API_KEY"
    );
  }
}
```

2) `AndroidManifest.xml`

First, specify the application you just set

```
  <!--
    IMPORTANT: Change ".TutorialApplication" in the lines below
    to match your application class.
  -->
<application
      android:name=".TutorialApplication"
      android:allowBackup="true"
      android:icon="@mipmap/ic_launcher"
      android:label="@string/app_name"
      android:theme="@style/AppTheme"
      >
```

NOTE: Change the `android:name` attribute of `<application>` tag above to match your application class.


Secondly, add the permissions below, typically immediately before the opening `<application>` tag:

```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.WAKE_LOCK"/>
<uses-permission android:name="android.permission.VIBRATE"/>
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
<uses-permission android:name="android.permission.GET_ACCOUNTS"/>
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE"/>

  <!--
    IMPORTANT: Change "com.mediatek.mcstutorial.permission.C2D_MESSAGE" in the lines below
    to match your app's package name + ".permission.C2D_MESSAGE".
  -->
<permission
    android:name="com.mediatek.mcstutorial.permission.C2D_MESSAGE"
    android:protectionLevel="signature"
    />
<uses-permission android:name="com.mediatek.mcstutorial.permission.C2D_MESSAGE"/>
```

NOTE: Change the `android:name` attribute of `<category>` element above to match your application's package name.

Then, add the following service and broadcast receiver definitions to AndroidManifest.xml immediately before the closing `</application>` tag:

```
<service android:name="com.mediatek.mcs.push.PushService"/>

<receiver
    android:name="com.mediatek.mcs.push.McsPushBroadcastReceiver"
    android:exported="false"
    >
  <intent-filter>
    <action android:name="com.mediatek.mcs.push.intent.RECEIVE"/>
    <action android:name="com.mediatek.mcs.push.intent.DISMISS"/>
    <action android:name="com.mediatek.mcs.push.intent.OPEN"/>
  </intent-filter>
</receiver>
<receiver
    android:name="com.mediatek.mcs.push.GcmBroadcastReceiver"
    android:permission="com.google.android.c2dm.permission.SEND"
    >
  <intent-filter>
    <action android:name="com.google.android.c2dm.intent.RECEIVE"/>
    <action android:name="com.google.android.c2dm.intent.REGISTRATION"/>
    <!--
      IMPORTANT: Change "com.mediatek.mcstutorial" to match your app's package name.
    -->
    <category android:name="com.mediatek.mcstutorial"/>
  </intent-filter>
</receiver>
```

NOTE: Change the `android:name` attribute of `<category>` element above to match your application's package name.

3) DONE! Compile and run your app.

The above codes are minimum requirement to setup MCS SDK, please reference to other code in this tutorial project or [MCS Android Tutorial Doc](https://www.gitbook.com/book/mtk-mcs/mcs-android-tutorial-doc/details) for more customization.


### ProGuard

> Follow the instructions in this section only if your app obfuscates with [Android ProGuard](http://developer.android.com/intl/zh-tw/tools/help/proguard.html). Otherwise, skip this section.

If you are using ProGuard to obfuscate your app, please be noted that MCS Android SDK uses Gson to dynamically serializes/deserializes entities fetch from MCS API server, so it's necessary to keep them for reflection.

Add the following lines to your project’s `proguard.cfg` file:

```
-keepattributes Signature, *Annotation*, EnclosingMethod
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
-keep public class com.mediatek.mcs.entity.** { *; }
```

## Usage

- [MCS SDK Android Tutorial Doc](https://www.gitbook.com/book/mtk-mcs/mcs-sdk-android-tutorial-doc/details) for detailed tutorial documentation.
- [MCS SDK Android Guide](https://www.gitbook.com/book/mtk-mcs/mcs-sdk-android-guide-doc/details) for detailed SDK components documentation.
- [MCS API References](https://mcs.mediatek.com/resources/latest/api_references/) for RESTful API documentation.

## Versioning

This SDK aims to adhere to [Semantic Versioning 2.0.0](http://semver.org/). As a summary, given a version number `MAJOR.MINOR.PATCH`:

1. `MAJOR` will increment when backwards-incompatible changes are introduced to the client.
2. `MINOR` will increment when backwards-compatible functionality is added.
3. `PATCH` will increment with backwards-compatible bug fixes.
Additional labels for pre-release and build metadata are available as extensions to the `MAJOR.MINOR.PATCH` format.

Note: the client version does not necessarily reflect the version used in the MCS API.

## Contact

If you have any problems, please contact <mtkcloudsandbox@mediatek.com> or open an issue. Also, check [our forum](http://labs.mediatek.com/forums/forums/show/68.page) for some discussion.

## License

```
Copyright (C) 2015, MediaTek Inc. All rights reserved.

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

The sample code of this tutorial is provided under the Apache License 2.0. See LICENSE file for applicable terms. This material also follows [Legal Notice](http://www.mediatek.com/en/legal-notice/) and [Privacy Policy](http://www.mediatek.com/en/privacy-policy/) on MediaTek website.
