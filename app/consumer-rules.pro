# Consumer ProGuard rules for WhAlert application
# https://developer.android.com/studio/build/shrink-code

# Basic consumer rules
-keep class * implements android.content.ComponentCallbacks2 {
    void onTrimMemory(int);
}

# Keep all WebView and JS interface classes
-keep class * extends android.webkit.WebViewClient
-keep class * extends android.webkit.WebView
-keep class * implements android.webkit.JavascriptInterface

# Keep all classes that implement Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep all classes that implement Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private <init>(...);
}

# Keep all classes that might be used in When clauses
-keep class * extends java.lang.Enum
-keep class * implements java.lang.annotation.Annotation

# Keep all classes that might be used with @Keep annotation
-keep @com.android.annotations.Keep class *
-keep @androidx.annotation.Keep class *

# Keep all classes that might be used with @VisibleForTesting annotation
-keep @androidx.annotation.VisibleForTesting class *
-keep @androidx.annotation.VisibleForTesting interface *
-keep @androidx.annotation.VisibleForTesting enum *

# Keep all classes that might be used with @RestrictTo annotation
-keep @androidx.annotation.RestrictTo class *

# Keep all classes that might be used with @RequiresApi annotation
-keep @androidx.annotation.RequiresApi class *

# Keep all classes that might be used with @TargetApi annotation
-keep @androidx.annotation.TargetApi class *

# Keep all classes that might be used with @SuppressLint annotation
-keep @androidx.annotation.SuppressLint class *

# Keep all classes that might be used with @Deprecated annotation
-keep @java.lang.Deprecated class *

# Keep all classes that might be used with @VisibleForTesting annotation
-keep @androidx.test.ext.junit.runners.AndroidJUnit4 class *
-keep @org.junit.Test class *
-keep @org.junit.Before class *
-keep @org.junit.After class *
-keep @org.junit.Ignore class *
-keep @org.junit.Rule class *
-keep @org.junit.rules.TestRule class *
-keep @org.junit.runners.model.FrameworkMethod class *
-keep @org.junit.runners.model.TestClass class *

# Keep all classes that might be used with @RunWith annotation
-keep @org.junit.runner.RunWith class *

# Keep all classes that might be used with @SmallTest annotation
-keep @androidx.test.filters.SmallTest class *

# Keep all classes that might be used with @MediumTest annotation
-keep @androidx.test.filters.MediumTest class *

# Keep all classes that might be used with @LargeTest annotation
-keep @androidx.test.filters.LargeTest class *

# Keep all classes that might be used with @UiThreadTest annotation
-keep @androidx.test.filters.UiThreadTest class *

# Keep all classes that might be used with @ApplicationTest annotation
-keep @androidx.test.filters.ApplicationTest class *

# Keep all classes that might be used with @InstrumentationTest annotation
-keep @androidx.test.filters.InstrumentationTest class *

# Keep all classes that might be used with @SdkSuppress annotation
-keep @androidx.annotation.SdkSuppress class *

# Keep all classes that might be used with @VisibleForTesting annotation
-keep @androidx.test.ext.junit.runners.AndroidJUnit4 class *
