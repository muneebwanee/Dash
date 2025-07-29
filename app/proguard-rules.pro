# Add project specific ProGuard rules here.  
# You can control the set of applied configuration files using the  
# proguardFiles setting in build.gradle.  
#  
# For more details, see  
#   http://developer.android.com/guide/developing/tools/proguard.html  
  
# If your project uses WebView with JS, uncomment the following  
# and specify the fully qualified class name to the JavaScript interface  
# class:  
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {  
#   public *;  
#}  
  
# Uncomment this to preserve the line number information for  
# debugging stack traces.  
-keepattributes SourceFile,LineNumberTable  
  
# If you keep the line number information, uncomment this to  
# hide the original source file name.  
-renamesourcefileattribute SourceFile  
  
# Keep all model classes for Firebase and serialization  
-keep class com.github.muneebwanee.dash.data.model.** { *; }  
  
# Firebase specific rules  
-keep class com.google.firebase.** { *; }  
-keep class com.google.android.gms.** { *; }  
-dontwarn com.google.firebase.**  
-dontwarn com.google.android.gms.**  
  
# Gson specific classes  
-keepattributes Signature  
-keepattributes *Annotation*  
-dontwarn sun.misc.**  
-keep class com.google.gson.** { *; }  
-keep class * implements com.google.gson.TypeAdapter  
-keep class * implements com.google.gson.TypeAdapterFactory  
-keep class * implements com.google.gson.JsonSerializer  
-keep class * implements com.google.gson.JsonDeserializer  
  
# Retrofit and OkHttp  
-keepattributes Signature, InnerClasses, EnclosingMethod  
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations  
-keepclassmembers,allowshrinking,allowobfuscation interface * {  
    @retrofit2.http.* <methods>;  
}  
-dontwarn okhttp3.**  
-dontwarn okio.**  
-dontwarn javax.annotation.**  
-dontwarn org.conscrypt.**  
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase  
  
# Glide  
-keep public class * implements com.bumptech.glide.module.GlideModule  
-keep class * extends com.bumptech.glide.module.AppGlideModule {  
 <init>(...);  
}  
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {  
  **[] $VALUES;  
  public *;  
}  
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {  
  *** rewind();  
}  
  
# Hilt/Dagger  
-dontwarn com.google.errorprone.annotations.**  
-keep class dagger.hilt.** { *; }  
-keep class javax.inject.** { *; }  
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel {  
    <init>(...);  
}  
  
# Kotlin Coroutines  
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}  
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}  
-keepclassmembers class kotlinx.coroutines.** {  
    volatile <fields>;  
}  
-dontwarn kotlinx.coroutines.**  
  
# Jetpack Compose  
-keep class androidx.compose.** { *; }  
-keep class kotlin.Metadata { *; }  
-dontwarn androidx.compose.**  
  
# Room Database  
-keep class * extends androidx.room.RoomDatabase  
-keep @androidx.room.Entity class *  
-dontwarn androidx.room.paging.**  
  
# WorkManager  
-keep class * extends androidx.work.Worker  
-keep class * extends androidx.work.InputMerger  
-keep class androidx.work.** { *; }  
  
# CameraX  
-keep class androidx.camera.** { *; }  
-dontwarn androidx.camera.**  
  
# Security and Biometric  
-keep class androidx.biometric.** { *; }  
-keep class androidx.security.crypto.** { *; }  
  
# Accessibility Services  
-keep class * extends android.accessibilityservice.AccessibilityService {  
    public <init>(...);  
    public void onAccessibilityEvent(android.view.accessibility.AccessibilityEvent);  
    public void onInterrupt();  
}  
  
# Notification Listener Services  
-keep class * extends android.service.notification.NotificationListenerService {  
    public <init>(...);  
    public void onNotificationPosted(android.service.notification.StatusBarNotification);  
    public void onNotificationRemoved(android.service.notification.StatusBarNotification);  
}  
  
# Keep all service classes  
-keep class com.github.muneebwanee.dash.services.** { *; }  
  
# Keep all receiver classes  
-keep class com.github.muneebwanee.dash.receiver.** { *; }  
  
# Keep all interactor classes  
-keep class com.github.muneebwanee.dash.interactor.** { *; }  
  
# Keep ViewBinding classes  
-keep class * extends androidx.viewbinding.ViewBinding {  
    public static *** inflate(android.view.LayoutInflater);  
    public static *** inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);  
    public static *** bind(android.view.View);  
}  
  
# Keep Parcelable implementations  
-keep class * implements android.os.Parcelable {  
    public static final ** CREATOR;  
}  
  
# Keep enum classes  
-keepclassmembers enum * {  
    public static **[] values();  
    public static ** valueOf(java.lang.String);  
}  
  
# Remove logging in release builds  
-assumenosideeffects class android.util.Log {  
    public static boolean isLoggable(java.lang.String, int);  
    public static int v(...);  
    public static int i(...);  
    public static int w(...);  
    public static int d(...);  
    public static int e(...);  
}  
  
# Remove Timber logging in release builds  
-assumenosideeffects class timber.log.Timber* {  
    public static *** v(...);  
    public static *** d(...);  
    public static *** i(...);  
    public static *** w(...);  
    public static *** e(...);  
}  
  
# Optimization settings  
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*  
-optimizationpasses 5  
-allowaccessmodification  
-dontpreverify  
  
# Keep line numbers for crash reports  
-keepattributes SourceFile,LineNumberTable  
-renamesourcefileattribute SourceFile
