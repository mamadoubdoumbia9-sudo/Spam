# Android ProGuard rules for WhAlert application
# https://developer.android.com/studio/build/shrink-code

# Basic ProGuard rules for Android
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class * extends android.widget.BaseAdapter
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.arch.lifecycle.ViewModel

# Keep all activities, services, and receivers
-keep public class * extends androidx.activity.ComponentActivity
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.lifecycle.ViewModel

# Keep all Compose functions
-keep class androidx.compose.runtime.Composable { *; }
-keep class androidx.compose.runtime.ComposableTarget { *; }
-keep class androidx.compose.runtime.ComposableSingleton { *; }
-keep class androidx.compose.runtime.ComposableInstance { *; }

# Keep all Room database classes
-keep class androidx.room.Database { *; }
-keep class * extends androidx.room.Database { *; }
-keep class androidx.room.Entity { *; }
-keep class androidx.room.Dao { *; }
-keep class * extends androidx.room.Dao { *; }
-keep class androidx.room.TypeConverter { *; }
-keep class * implements androidx.room.TypeConverter { *; }

# Keep all Retrofit service interfaces
-keep class com.whalert.app.service.* { *; }
-keep class com.whalert.app.repository.* { *; }
-keep class com.whalert.app.model.* { *; }

# Keep all data classes for serialization
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep all Gson related classes
-keep class com.google.gson.** { *; }
-keep class com.google.gson.annotations.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep all Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep all OkHttp classes
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keep interface okhttp3.** { *; }

# Keep all Hilt/Dagger classes
-keep class dagger.** { *; }
-keep class * implements dagger.Module
-keep class * implements dagger.Component
-keep class * implements dagger.android.AndroidEntryPoint
-keep class * implements dagger.hilt.android.HiltModule
-keep class * implements dagger.hilt.android.HiltWorker
-keep class * implements dagger.hilt.android.HiltViewModel

# Keep all Coroutines classes
-keep class kotlinx.coroutines.** { *; }
-keep class kotlinx.coroutines.internal.** { *; }

# Keep all Jetpack Compose classes
-keep class androidx.compose.** { *; }
-keep class androidx.ui.** { *; }
-keep class androidx.activity.** { *; }
-keep class androidx.navigation.** { *; }

# Keep all Material 3 classes
-keep class com.google.android.material.** { *; }
-keep class androidx.compose.material3.** { *; }

# Keep all lifecycle classes
-keep class androidx.lifecycle.** { *; }

# Keep all Room classes
-keep class androidx.room.** { *; }

# Keep all security classes
-keep class androidx.security.** { *; }

# Keep all Coil image loading classes
-keep class io.coil.** { *; }

# Keep all WhAlert application classes
-keep class com.whalert.app.** { *; }
-keep interface com.whalert.app.** { *; }

# Keep all enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep all Parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep R classes
-keep class com.whalert.app.R { *; }
-keep class com.whalert.app.BuildConfig { *; }

# Keep all classes that have custom Parcel or Serializable constructors
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private <init>(...);
    private Object writeReplace();
    private Object readResolve();
}

# Keep all classes that might be used in When clauses
-keep class * extends java.lang.Enum
-keep class * implements java.lang.annotation.Annotation

# Keep all classes that might be used with @Keep annotation
-keep @com.android.annotations.Keep class *
-keep @androidx.annotation.Keep class *
-keep @dagger.hilt.android.internal.keep.Keep class *

# Keep all classes that might be used with @Inject annotation
-keep @javax.inject.Inject class *
-keep @dagger.Provides class *
-keep @dagger.Module class *
-keep @dagger.Component class *

# Keep all classes that might be used with @Singleton annotation
-keep @javax.inject.Singleton class *

# Keep all classes that might be used with @Binds annotation
-keep @dagger.Binds class *

# Keep all classes that might be used with @IntoSet annotation
-keep @dagger.multibindings.IntoSet class *

# Keep all classes that might be used with @IntoMap annotation
-keep @dagger.multibindings.IntoMap class *

# Keep all classes that might be used with @ClassKey annotation
-keep @dagger.multibindings.ClassKey class *

# Keep all classes that might be used with @StringKey annotation
-keep @dagger.multibindings.StringKey class *

# Keep all classes that might be used with @HiltAndroidApp annotation
-keep @dagger.hilt.android.HiltAndroidApp class *

# Keep all classes that might be used with @HiltViewModel annotation
-keep @dagger.hilt.android.lifecycle.HiltViewModel class *

# Keep all classes that might be used with @HiltWorker annotation
-keep @dagger.hilt.android.components.HiltWorker class *

# Keep all classes that might be used with @HiltModule annotation
-keep @dagger.hilt.android.components.HiltModule class *

# Keep all classes that might be used with @AndroidEntryPoint annotation
-keep @dagger.hilt.android.AndroidEntryPoint class *

# Keep all classes that might be used with @HiltAndroidTest annotation
-keep @dagger.hilt.android.testing.HiltAndroidTest class *

# Keep all classes that might be used with @HiltViewModel annotation
-keep @dagger.hilt.android.lifecycle.HiltViewModel class *

# Keep all classes that might be used in When clauses for sealed classes
-keep class * extends kotlin.Enum
-keep class * implements kotlin.Enum

# Keep all classes that might be used with @Composable annotation
-keep @androidx.compose.runtime.Composable class *
-keep @androidx.compose.runtime.Composable fun *

# Keep all classes that might be used with @Preview annotation
-keep @androidx.compose.ui.tooling.preview.Preview class *
-keep @androidx.compose.ui.tooling.preview.Preview fun *

# Keep all classes that might be used with @Stable annotation
-keep @androidx.compose.runtime.Stable class *
-keep @androidx.compose.runtime.Stable fun *

# Keep all classes that might be used with @Immutable annotation
-keep @androidx.compose.runtime.Immutable class *
-keep @androidx.compose.runtime.Immutable fun *

# Keep all classes that might be used with @ReadOnlyComposable annotation
-keep @androidx.compose.runtime.ReadOnlyComposable class *
-keep @androidx.compose.runtime.ReadOnlyComposable fun *

# Keep all classes that might be used with @ComposableTarget annotation
-keep @androidx.compose.runtime.ComposableTarget class *
-keep @androidx.compose.runtime.ComposableTarget fun *
