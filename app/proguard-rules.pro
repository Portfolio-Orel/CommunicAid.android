# For debugging purposes.
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-keep class com.orels.app.*
-keep class com.orels.auth.*
-keep class com.orels.BuildConfig
-keep class com.orels.data.*
-keep class com.orels.domain.*
-keep class com.orels.presentation.*

-keepclasseswithmembernames class * { native <methods>; }
#-keep class com.orels.app.data.remote.dto.*
#-keep class com.orels.app.data.remote.*
#-keep class com.orels.app.di.*