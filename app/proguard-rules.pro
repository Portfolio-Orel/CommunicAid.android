# For debugging purposes.
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-dontobfuscate # For debugging minify build
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable
-keepattributes *Annotation*

-keep class com.orelzman.mymessages.data.remote.dto.*
-keep class com.orelzman.mymessages.data.remote.*
-keep class com.orelzman.mymessages.domain.model.entities.*
-keep class com.orelzman.mymessages.presentation.*

-keepclasseswithmembernames class * { native <methods>; }