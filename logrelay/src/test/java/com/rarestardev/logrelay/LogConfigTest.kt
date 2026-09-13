package com.rarestardev.logrelay

import com.rarestardev.logrelay.core.LogConfig
import org.junit.Assert.assertEquals
import org.junit.Test

class LogConfigTest {

    @Test
    fun `test default values of LogConfig`() {
        val config = LogConfig(serverUrl = "ws://example.com")
        
        assertEquals("ws://example.com", config.serverUrl)
        assertEquals(true, config.realtimeEnabled)
        assertEquals(true, config.periodicSyncEnabled)
    }

    @Test
    fun `test custom values of LogConfig`() {
        val config = LogConfig(
            serverUrl = "ws://test.com",
            realtimeEnabled = false,
            periodicSyncEnabled = false
        )
        
        assertEquals("ws://test.com", config.serverUrl)
        assertEquals(false, config.realtimeEnabled)
        assertEquals(false, config.periodicSyncEnabled)
    }
}
