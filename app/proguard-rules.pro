# ---- 基本设置 ----
-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# ---- Retrofit + OkHttp ----
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# ---- Gson (API 响应模型) ----
-keep class com.app.fimtale.model.** { *; }
-keepclassmembers class com.app.fimtale.model.** { *; }

# ---- Glide ----
-keep class com.bumptech.glide.** { *; }
-keep class * extends com.bumptech.glide.module.AppGlideModule { *; }
-keep class * extends com.bumptech.glide.module.LibraryGlideModule { *; }

# ---- Room ----
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ---- ZXing ----
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

# ---- Markwon ----
-keep class io.noties.markwon.** { *; }
-dontwarn io.noties.markwon.**

# ---- WebView JavaScript 接口 ----
-keepclassmembers class com.app.fimtale.LoginActivity$CaptchaInterface {
    public *;
}

# ---- 应用入口 ----
-keep class com.app.fimtale.FimTaleApplication { *; }
-keep class com.app.fimtale.MainActivity { *; }
