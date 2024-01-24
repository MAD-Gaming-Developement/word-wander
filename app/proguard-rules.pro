-keep class com.appsflyer.** { *; }

-keep class dev.karl.wordwander.** { *; }

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature
-keepattributes SetJavaScriptEnabled

-keep public class * extends android.app.Application

-keep class com.appsflyer.**$InstallReceiver { *; }
-keep class com.appsflyer.**$referrer { *; }
-keep class com.appsflyer.**$FirebaseMessagingService { *; }
-keep class com.appsflyer.**AppsFlyerLib

-keepclassmembers class * {
    @com.appsflyer.*
    <methods>;
}




-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature
-keepattributes SetJavaScriptEnabled

-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }
-keep class com.pusher.** { *; }
-keep class com.google.firebase.iid.** { *; }

-dontwarn com.google.firebase.iid.FirebaseInstanceId*
-dontwarn com.google.firebase.iid.InstanceIdResult*

# Keep the FirebaseMessagingService
-keep class com.google.firebase.messaging.FirebaseMessagingService.** { *; }



-keep class com.google.android.gms.common.ConnectionResult {
   int SUCCESS;
}


-keep class com.android.installreferrer.** { *; }

-keep class com.google.android.gms.measurement.** { *; }
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
   com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
}


-dontwarn java.awt.Color*
-dontwarn java.awt.Font*
-dontwarn java.awt.Point*
