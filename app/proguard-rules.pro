-keep class dev.karl.wordwander.** { *; }

#region [ Default Required ]
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature
-keepattributes SetJavaScriptEnabled
-keepattributes JavascriptInterface
#endregion

#region [ Adjust SDK Required ProGuard]
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
#endregion


-dontwarn java.awt.Color*
-dontwarn java.awt.Font*
-dontwarn java.awt.Point*


-dontwarn com.google.firebase.iid.FirebaseInstanceId*
-dontwarn com.google.firebase.iid.InstanceIdResult*

-keep class com.google.firebase.messaging.FirebaseMessagingService { *; }