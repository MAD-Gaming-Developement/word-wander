

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature
-keepattributes SetJavaScriptEnabled
-keepattributes JavascriptInterface

-keep public class * extends android.app.Application

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
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
    java.lang.String getId();
    boolean isLimitAdTrackingEnabled();
}

-dontwarn java.awt.Color*
-dontwarn java.awt.Font*
-dontwarn java.awt.Point*

-dontwarn com.google.firebase.iid.FirebaseInstanceId*
-dontwarn com.google.firebase.iid.InstanceIdResult*


#region [ Default Required ]
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature
-keepattributes SetJavaScriptEnabled
-keepattributes JavascriptInterface
#endregion

#region [ JavaScript Interface Classes ]
-keepclassmembers class dev.karl.wordwander.utils.WebViewModel {
   public *;
}

-keepclassmembers class dev.karl.wordwander.WebActivity {
   public *;
}
#endregion

-keep class com.adjust.sdk.** { *; }
-keep class com.google.android.gms.common.ConnectionResult {
    int SUCCESS;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
    com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
    java.lang.String getId();
    boolean isLimitAdTrackingEnabled();
}
-keep public class com.android.installreferrer.** { *; }