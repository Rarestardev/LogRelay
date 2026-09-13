# LogTracker Project

A robust, lifecycle-aware logging system for Android that supports real-time synchronization, periodic background syncing, and offline persistence.

# [![](https://jitpack.io/v/Rarestardev/LogRelay.svg)](https://jitpack.io/#Rarestardev/LogRelay)
---

## Project Structure

- **`:app`**: A sample Android application demonstrating how to integrate and use the library.
- **`:logrelay`**: The core Android library that handles log storage, network transmission (WebSocket & REST), and background syncing.

---

# LogRelay Library Documentation

## Features

- **Dual Connection Modes**: Choose between real-time **WebSocket** transmission or request-based **HTTP API** calls.
- **Background Sync**: Uses WorkManager to upload all cached logs periodically (respects network constraints).
- **Offline Persistence**: Powered by Room Database to ensure no log is lost, even if the app crashes or the internet is disconnected.
- **ISO 8601 Timestamps**: Automatically formats log timestamps to standard ISO 8601 strings (e.g., `2026-09-13T13:56:00+03:30`) for easier server-side processing.
- **Dynamic Configuration**: Configure server URLs, custom API paths, authentication tokens, and sync modes at runtime.
- **Standardized Tags**: Centralized `LogTags` to categorize logs (UI, Network, Database, etc.).
- **Reactive API**: Exposes logs as a `Flow` for easy UI integration.

## Installation

Add the library to your project's `build.gradle` dependencies:

```kotlin
dependencies {
    implementation("com.github.Rarestardev:LogRelay:1.0.1")
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
            serverUrl = "http://your-server.com",  // Server URL
            uploadPath = "api/logs/batch",         // Custom API path for upload
            authToken = "YOUR_BEARER_TOKEN",       // Optional Bearer token
            connectionMode = LogConnectionMode.NORMAL, // NORMAL (HTTP) or WEB_SOCKET
            periodicSyncEnabled = true             // Enable periodic background sync
        )

        LogTracker.initialize(this, config)
    }
}
```

> [!TIP]
> If you use `LogConnectionMode.NORMAL`, every log call will trigger an HTTP POST request. For high-frequency logging, consider using `WEB_SOCKET` or relying on periodic sync.

### 2. Network Security (for HTTP)

If you are using a non-HTTPS server, you must allow cleartext traffic in your `AndroidManifest.xml`:

```xml
<application
    ...
    android:networkSecurityConfig="@xml/network_security_config">
```

`res/xml/network_security_config.xml`:
```xml
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">your-server.com</domain>
    </domain-config>
</network-security-config>
```

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

<img src="https://github.com/Rarestardev/LogTracker/blob/master/Screenshot_20260913_123411.png?raw=true" width="200" alt="Log Tracker UI" />

LogTracker exposes a `Flow` of logs:

```kotlin
val logs by LogTracker.getAllLogs().collectAsState(initial = emptyList())
```

---

## Server Implementation

For both real-time (WebSocket) and batch syncing (HTTP), your server should handle the following JSON structure.

### API Endpoint

- **URL**: `{serverUrl}/{uploadPath}`
- **Method**: `POST`
- **Authentication**: `Authorization: Bearer <authToken>`
- **Content-Type**: `application/json`

### Payload Format

The data is sent as an object containing a list of logs. Timestamps are formatted as **ISO 8601** strings.

```json
{
  "logs": [
    {
      "logId": 123,
      "level": "ERROR",
      "tag": "NETWORK",
      "message": "API request failed",
      "throwable": "java.net.SocketTimeoutException: timeout",
      "timestamp": "2026-09-13T15:45:00+03:30"
    }
  ]
}
```

> [!NOTE]
> For HTTP requests, the server should return a success status code (e.g., `200 OK`) to signal the library to clear the uploaded logs from the local device storage.

## Requirements

- Min SDK: 24
- AndroidX
- Internet Permission

## License

MIT License.
