-keepclasseswithmembers public class io.github.kdroidfilter.ytdlpgui.MainKt {  #
    public static void main(java.lang.String[]);
}

-dontwarn kotlinx.coroutines.debug.*

-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class org.jetbrains.skia.** { *; }
-keep class org.jetbrains.skiko.** { *; }
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }
-keepclassmembers class * extends com.sun.jna.* { public *; }
-keepclassmembers class * implements com.sun.jna.* { public *; }
-dontwarn com.sun.jna.**

# Keep specific JNA Platform classes used in the project
-keep class com.sun.jna.platform.** { *; }
-keep class com.sun.jna.win32.** { *; }
-dontwarn com.sun.jna.platform.**


-keep class com.kdroid.composetray.** { *; }

-assumenosideeffects public class androidx.compose.runtime.ComposerKt {
    void sourceInformation(androidx.compose.runtime.Composer,java.lang.String);
    void sourceInformationMarkerStart(androidx.compose.runtime.Composer,int,java.lang.String);
    void sourceInformationMarkerEnd(androidx.compose.runtime.Composer);
}

# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations
-dontnote kotlinx.serialization.SerializationKt


# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
# Keep BouncyCastle and NativeCerts classes intact to avoid bytecode changes
# that can invalidate signed JAR digests during release optimization.
-keep class org.bouncycastle.** { *; }
-keep class org.jetbrains.nativecerts.** { *; }

# Keep Ktor Kotlinx Serialization provider loaded via ServiceLoader
-keep class io.ktor.serialization.kotlinx.json.KotlinxSerializationJsonExtensionProvider { *; }
-keep class io.ktor.serialization.kotlinx.** { *; }

# Keep SQLite JDBC driver and any Driver implementations discoverable by DriverManager
-keep class org.sqlite.** { *; }
-keep class * implements java.sql.Driver { *; }
-dontwarn org.sqlite.**

# Keep GStreamer Java bindings (avoid enum unboxing/optimization)
-keep class org.freedesktop.gstreamer.** { *; }
-keep enum org.freedesktop.gstreamer.** { *; }
-dontwarn org.freedesktop.gstreamer.**

# Coil, OkHttp, and Okio are used for AsyncImage. Keep them to prevent
# release-only issues where fetchers/decoders or compose adapters are removed.
-keep class coil3.** { *; }
-keep class coil3.compose.** { *; }
-keep class coil3.network.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn coil3.**
-dontwarn okhttp3.**
-dontwarn okio.**

# FileKit dialogs (folder/file pickers). Keep providers and dialog implementations
# as they may be loaded reflectively (or via ServiceLoader) and get stripped.
-keep class io.github.vinceglb.filekit.** { *; }
-dontwarn io.github.vinceglb.filekit.**

# D-Bus bindings may be used on Linux via portals. Keep to be safe in release.
-keep class org.freedesktop.dbus.** { *; }
-dontwarn org.freedesktop.dbus.**
#################################### SLF4J #####################################
-dontwarn org.slf4j.**

# Prevent runtime crashes from use of class.java.getName()
-dontwarn javax.naming.**

# Ignore warnings and Don't obfuscate for now
-dontobfuscate
-ignorewarnings

-keep class sun.misc.Unsafe { *; }
-dontnote sun.misc.Unsafe

-keep class com.jetbrains.JBR* { *; }
-dontnote com.jetbrains.JBR*
-keep class com.jetbrains.** { *; }
-dontwarn com.jetbrains.**
-dontnote com.jetbrains.**

-keep class com.sun.jna** { *; }
-dontnote com.sun.jna**

-keep class androidx.compose.ui.input.key.KeyEvent_desktopKt { *; }
-dontnote androidx.compose.ui.input.key.KeyEvent_desktopKt

-keep class androidx.compose.ui.input.key.KeyEvent_skikoKt { *; }
-dontnote androidx.compose.ui.input.key.KeyEvent_skikoKt
-dontwarn androidx.compose.ui.input.key.KeyEvent_skikoKt

-dontnote org.jetbrains.jewel.intui.markdown.standalone.styling.extensions.**
-dontwarn org.jetbrains.jewel.intui.markdown.standalone.styling.extensions.**

-dontnote org.jetbrains.jewel.foundation.lazy.**
-dontwarn org.jetbrains.jewel.foundation.lazy.**

-dontnote org.jetbrains.jewel.foundation.util.**
-dontwarn org.jetbrains.jewel.foundation.util.**

-dontnote org.jetbrains.jewel.window.utils.**
-dontwarn org.jetbrains.jewel.window.utils.**

# Preserve sealed interface metadata so R8/ProGuard doesn't break sealed hierarchies (Java 17)
-keepattributes PermittedSubclasses

# Keep Jewel painter classes to prevent ICCE with sealed interface PainterHint
-keep class org.jetbrains.jewel.ui.painter.** { *; }
-dontwarn org.jetbrains.jewel.ui.painter.**
