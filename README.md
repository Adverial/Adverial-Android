# Adverial Project

## Description
The Adverial project is a comprehensive ad management system that allows both administrators and end users to effectively manage advertisements. Administrators have full CRUD (Create, Read, Update, Delete) capabilities for ads, filters, and status changes, while end users can register, log in, and interact with ads through the mobile application via API. The application also supports localization for multiple languages.

## Features

### User Registration/Login
End users can register for an account or log in to their existing account.

### Viewing Ads
End users can browse through ads using filters and view detailed information about each ad.

### Posting Ads
End users can create and post their own ads, specifying relevant details such as title, description, price, contact information, and location.

### Interacting with Ads
End users can contact the ad poster via phone or message directly from the mobile application.

### Notifications
End users receive notifications about new ads, responses to their ads, and other relevant updates.

### Favorite Ads
End users can mark ads as their favorites for easy access and tracking.

## Platforms
The Adverial project supports both iOS and Android platforms.

## Important Information for Developers
- The `kotlin-android-extensions` Gradle plugin is deprecated. Please migrate to using View Binding and the `kotlin-parcelize` plugin. Refer to the migration guide [here](https://goo.gle/kotlin-android-extensions-deprecation).
- View Binding has been enabled for this project. Make sure to use binding classes generated for XML layouts to access views efficiently.
- The minimum SDK version required for this project is 22, and the target SDK version is 31.
- Various third-party libraries are used in this project, including Glide, Retrofit, Picasso, and others. Make sure to check their documentation for usage instructions.
