package com.rarestardev.logtracker

/**
 * Utility to format log messages with app metadata.
 */
object LogFormatter {

    /**
     * Formats a message with app name, version, API version, and platform.
     */
    fun format(
        appName: String,
        appVersion: String,
        apiVersion: String,
        platform: String = "Android app",
        message: String
    ): String {
        return StringBuilder().apply {
            appendLine("App name : $appName")
            appendLine("App version : $appVersion")
            appendLine("Api version : $apiVersion")
            appendLine("platform : $platform")
            append("Log message : -> $message")
        }.toString()
    }
}
