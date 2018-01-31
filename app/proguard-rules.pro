# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/wangyazhen/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class * implements android.os.Parcelable {
  *;
}

-dontshrink
-dontoptimize
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes *Annotation*
-keepattributes Signature
-ignorewarnings


# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
#-libraryjars libs/android-support-v4.jar
-dontwarn android.support.**
-keep class android.support.** { *;}
#rxbus start
-keep class com.chemanman.rxbus1.CustomModel.** {
    *;
}
-keep class **_BindInject { *; }
-keep class com.chemanman.rxbus.** { *; }
-keepclasseswithmembernames class * {
    @com.chemanman.rxbus.annotation.* <methods>;
}
#-keepnames class * { @butterknife.InjectView *;}
#-keepnames class * {  @com.chemanman.rxbus.annotation.EventMethodBind *;}

#rxbus end
#rxjava start
-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontnote rx.internal.util.PlatformDependent
#rxjava end