# LogRelay Library

A robust, lifecycle-aware logging library for Android that supports real-time synchronization, periodic background syncing, and offline persistence.

## Features

- **Real-time Sync**: Uses WebSockets to send logs to your server instantly.
- **Background Sync**: Uses WorkManager to upload all cached logs every 24 hours (respects network constraints).
- **Offline Persistence**: Powered by Room Database to ensure no log is lost, even if the app crashes or the internet is disconnected.
- **Standardized Tags**: Centralized `LogTags` to categorize logs (UI, Network, Database, etc.).
- **Reactive API**: Exposes logs as a `Flow` for easy UI integration.

## Installation

Add the library to your project's `build.gradle` dependencies:

```kotlin
dependencies {
    implementation(project(":logrelay"))
}
```

## Setup

### 1. Initialize LogTracker

Create or update your `Application` class to initialize the library:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = LogConfig(
            serverUrl = "wss://your-log-server.com", // WebSocket URL
            realtimeEnabled = true,                // Enable instant sync
            periodicSyncEnabled = true             // Enable 24h background sync
        )

        LogTracker.initialize(this, config)
    }
}
```

Don't forget to register your `Application` class in `AndroidManifest.xml`:

```xml
<application
    android:name=".MyApplication"
    ...>
</application>
```

## Usage

### Logging Messages

You can use the helper methods for different log levels. The library provides a set of standardized tags in `LogTags`:

- `LogTags.UI`: UI events and interactions.
- `LogTags.NETWORK`: Network requests and responses.
- `LogTags.DATABASE`: Database operations.
- `LogTags.AUTH`: Authentication flows.
- `LogTags.APP_LIFECYCLE`: Application lifecycle events.
- `LogTags.WORKER`: Background worker tasks.
- `LogTags.ANALYTICS`: Analytics events.
- `LogTags.INTERNAL`: Internal library logs.

```kotlin
// Using standard tags
LogTracker.i(LogTags.UI, "User opened the Home screen")
LogTracker.d(LogTags.NETWORK, "Fetching user profile...")

// Logging errors with throwables
try {
    // ... code
} catch (e: Exception) {
    LogTracker.e(LogTags.DATABASE, "Failed to save data", e)
}

// Custom log
LogTracker.log(tag = "CUSTOM_TAG", message = "My custom message", level = "VERBOSE")
```

### Log Viewer UI

The library includes an example of how to build a real-time log viewer. Here is how it looks in the sample app:

![Log Tracker UI](file:///C:/Users/Ronak/AppData/Local/Google/AndroidStudio2026.1.2/projects/logtracker.466ee597/.artifacts/cd426b39-3713-4260-ac4e-c523e83bd3f4/screenshot.png)

### Displaying Logs in UI

LogTracker exposes a `Flow` of logs, which you can collect in your Composables or ViewModels:

```kotlin
@Composable
fun LogList() {
    val logs by LogTracker.getAllLogs().collectAsState(initial = emptyList())
    
    LazyColumn {
        items(logs) { log ->
            Text("${log.timestamp} [${log.tag}]: ${log.message}")
        }
    }
}
```

## Requirements

- Min SDK: 24
- AndroidX
- Internet Permission (automatically added by the library)

## License

This project is licensed under the MIT License - see the [LICENSE](file:///C:/Users/Ronak/AndroidStudioProjects/LogTracker/LICENSE) file for details.
