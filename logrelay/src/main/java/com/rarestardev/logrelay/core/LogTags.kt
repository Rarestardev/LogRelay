package com.rarestardev.logrelay.core

/**
 * Standard log tags to be used throughout the application.
 * This helps in categorizing and filtering logs efficiently.
 */
object LogTags {
    const val UI = "UI_EVENT"
    const val NETWORK = "NETWORK_OP"
    const val DATABASE = "DATABASE_OP"
    const val AUTH = "AUTH_FLOW"
    const val APP_LIFECYCLE = "APP_LIFECYCLE"
    const val WORKER = "BACKGROUND_WORKER"
    const val ANALYTICS = "ANALYTICS"
    const val INTERNAL = "LOG_TRACKER_INTERNAL"
}
