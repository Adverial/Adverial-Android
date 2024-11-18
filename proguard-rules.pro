-keep class com.application.** { *; }
-keep interface com.application.** { *; }
-keep class com.huawei.hms.**{*;}
-assumenosideeffects class java.lang.System {
    public static long currentTimeMillis();
}