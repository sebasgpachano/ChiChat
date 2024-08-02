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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class com.team2.chitchat.data.repository.remote.response.** { *; }
-keep class com.team2.chitchat.data.repository.remote.request.** { *; }

-keep class com.team2.chitchat.data.repository.local.** { *; }
-keepattributes *Annotation*
-keepattributes Signature

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response

 # With R8 full mode generic signatures are stripped for classes that are not
 # kept. Suspend functions are wrapped in continuations where the type argument
 # is used.
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

 -keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
 -keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

 -keep class com.google.firebase.** { *; }
 -dontwarn com.google.firebase.**

-assumenosideeffects class android.util.Log {
     public static *** d(...);
     public static *** e(...);
     public static *** w(...);
     public static *** i(...);
     public static *** v(...);
 }