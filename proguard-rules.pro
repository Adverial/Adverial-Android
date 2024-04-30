-keep class com.application.** { *; }
-keep interface com.application.** { *; }
-assumenosideeffects class java.lang.System {
    public static long currentTimeMillis();
}