# LogTracker Project

A robust, lifecycle-aware logging system for Android that supports real-time synchronization, periodic background syncing, and offline persistence.

## Project Structure

- **`:app`**: A sample Android application demonstrating how to integrate and use the library.
- **`:logrelay`**: The core Android library that handles log storage, network transmission (WebSocket & REST), and background syncing.

---

# LogRelay Library Documentation

## Features

- **Real-time Sync**: Uses WebSockets to send logs to your server instantly.
- **Background Sync**: Uses WorkManager to upload all cached logs every 24 hours (respects network constraints).
- **Offline Persistence**: Powered by Room Database to ensure no log is lost, even if the app crashes or the internet is disconnected.
- **Dynamic Configuration**: Configure server URLs, custom API paths, and sync options at runtime.
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
            uploadPath = "api/logs/batch",         // Custom API path for sync (Optional)
            realtimeEnabled = true,                // Enable instant sync
            periodicSyncEnabled = true             // Enable 24h background sync
        )

        LogTracker.initialize(this, config)
    }
}
```

> [!TIP]
> The library automatically converts your `wss://` or `ws://` URL to `https://` or `http://` when communicating with the REST API for background syncing.

## Usage

### Logging Messages

You can use the helper methods for different log levels:

| Method | Level | Description |
| :--- | :--- | :--- |
| `LogTracker.v()` | VERBOSE | Highest volume, most detailed info. |
| `LogTracker.d()` | DEBUG | Useful during development. |
| `LogTracker.i()` | INFO | General informational messages. |
| `LogTracker.w()` | WARNING | Potentially harmful situations. |
| `LogTracker.e()` | ERROR | Error events that might still allow the app to run. |
| `LogTracker.wtf()` | ASSERT | Terrible failures that should never happen. |

```kotlin
// Using standard tags from LogTags
LogTracker.i(LogTags.UI, "User opened the Home screen")
LogTracker.e(LogTags.NETWORK, "Connection failed", Throwable("Timeout"))

// Custom log
LogTracker.log(tag = "CUSTOM", message = "My message", level = "INFO")

// Clear all local logs
LogTracker.clearAllLogs()
```

### Log Viewer UI

The library includes a real-time log viewer. Here is how it looks in the sample app:

![Log Tracker UI](https://github.com/Rarestardev/LogTracker/blob/master/Screenshot_20260913_123411.png)

LogTracker exposes a `Flow` of logs:

```kotlin
val logs by LogTracker.getAllLogs().collectAsState(initial = emptyList())
```

---

## Server Implementation

For background syncing, your server needs to implement a batch upload endpoint.

### Batch Upload Endpoint

- **URL**: `{serverUrl}/{uploadPath}` (Default path: `api/logs/batch`)
- **Method**: `POST`
- **Content-Type**: `application/json`

### Payload Format (JSON Array)

```json
[
  {
    "logId": 1,
    "tag": "UI",
    "message": "User clicked on Info button",
    "level": "INFO",
    "throwable": null,
    "timestamp": 1726215216819
  }
]
```

> [!NOTE]
> The server should return a success status code (e.g., `200 OK`) to signal the library to clear these logs from the device.

## Requirements

- Min SDK: 24
- AndroidX
- Internet Permission

## License

MIT License.
