# WhAlert ProGuard/R8 Rules
# Android ProGuard configuration for release builds

# Basic ProGuard rules for Android
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Keep all Activities, Services, BroadcastReceivers
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

# Keep all ViewModel classes
-keep class androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Keep Room Database components
-keep class androidx.room.Database { *; }
-keep class * extends androidx.room.Database { *; }
-keep class androidx.room.Entity { *; }
-keep class * extends androidx.room.Entity { *; }
-keep class androidx.room.Dao { *; }
-keep class * extends androidx.room.Dao { *; }
-keep class androidx.room.TypeConverters { *; }
-keep class * extends androidx.room.TypeConverters { *; }

# Keep Retrofit and OkHttp
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class com.squareup.okhttp3.** { *; }
-keep class com.squareup.retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keep interface okhttp3.** { *; }

# Keep Gson for JSON serialization
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep libphonenumber
-keep class com.google.i18n.phonenumbers.** { *; }
-keep class com.google.phonenumber.** { *; }

# Keep Hilt/Dagger
-keep class com.google.dagger.** { *; }
-keep class dagger.** { *; }
-keep class * extends dagger.Module { *; }
-keep class * extends dagger.Component { *; }
-keep class * extends javax.inject.Provider { *; }
-keep class * extends javax.inject.Inject { *; }

# Keep AndroidX components
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# Keep Compose components
-keep class androidx.compose.** { *; }
-keep class androidx.ui.** { *; }
-keep class com.whalert.app.ui.** { *; }

# Keep Material components
-keep class com.google.android.material.** { *; }

# Keep Firebase components
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep Coil image loading
-keep class coil.** { *; }
-keep class okio.** { *; }

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }
-keep class kotlinx.coroutines.internal.** { *; }

# Keep Security Crypto
-keep class androidx.security.** { *; }

# Keep our application classes
-keep class com.whalert.app.** { *; }
-keep interface com.whalert.app.** { *; }

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep R classes
-keep class com.whalert.app.R { *; }
-keep class com.whalert.app.R$* { *; }

# Keep BuildConfig
-keep class com.whalert.app.BuildConfig { *; }

# Keep data models
-keep class com.whalert.app.model.** { *; }
-keep class com.whalert.app.data.** { *; }
-keep class com.whalert.app.repository.** { *; }
-keep class com.whalert.app.viewmodel.** { *; }
-keep class com.whalert.app.util.** { *; }
-keep class com.whalert.app.service.** { *; }
-keep class com.whalert.app.di.** { *; }

# Keep annotations
-keepattributes *Annotation*
-keepattributes SourceFile, LineNumberTable
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes *Annotation*

# Keep generic type information for reflection
-keepattributes Signature

# Keep method parameter names
-keepparameters

# Keep all WebView related classes
-keep class * extends android.webkit.WebViewClient { *; }

# Keep all Parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep all Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private <fields>;
    private <methods>;
}

# Optimizations
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Remove unused code
-allowaccessmodification
-allowoptimization
-mergeinterfacesaggressively
-shrinkresources

# Obfuscation dictionary for better readability
-obfuscationdictionary /usr/share/dict/words
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn com.squareup.okhttp3.**
-dontwarn com.squareup.retrofit2.**
-dontwarn com.google.gson.**
-dontwarn com.google.i18n.phonenumbers.**
-dontwarn androidx.room.**
-dontwarn dagger.**
-dontwarn com.google.dagger.**
-dontwarn androidx.compose.**
-dontwarn coil.**

# Keep names of native methods
-keepclassmembers class * {
    native <methods>;
}

# Keep native libraries
-keep class * extends java.lang.Object {
    native <methods>;
}

# Print usage information
-printusage unused
-whyareyoukeeping class com.whalert.app.**
-whyareyoukeeping class androidx.**
-whyareyoukeeping class com.google.**
