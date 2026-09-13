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

You can use the helper methods for different log levels:

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

(Include your license information here)
