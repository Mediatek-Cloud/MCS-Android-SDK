# For anyone who uses Mcs Mobile SDK should add proguard rules as below
# to correctly serialize and deserialize objects
#
# Required:
#
# -keep public class com.mediatek.mcs.entity.** { *; }
#
#
# Opional for easier debugging:
#
# -keepattributes SourceFile,LineNumberTable
# -keepnames class com.mediatek.mcs.** { *; }
#

# Keep Mcs-Android
-keep public class com.mediatek.mcs.entity.** { *; }

# Keep Gson
-keepattributes Signature, *Annotation*, EnclosingMethod
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

# [optional] Keep EventBus if you used it
-keepclassmembers class ** { public void onEvent*(**); }
