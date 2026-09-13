package com.rarestardev.logrelay

import com.rarestardev.logrelay.core.LogConfig
import com.rarestardev.logrelay.core.LogConnectionMode
import org.junit.Assert.assertEquals
import org.junit.Test

class LogConfigTest {

    @Test
    fun `test default values of LogConfig`() {
        val config = LogConfig(serverUrl = "ws://example.com")
        
        assertEquals("ws://example.com", config.serverUrl)
        assertEquals(true, config.connectionMode == LogConnectionMode.WEB_SOCKET)
        assertEquals(true, config.periodicSyncEnabled)
    }

    @Test
    fun `test custom values of LogConfig`() {
        val config = LogConfig(
            serverUrl = "ws://test.com",
            connectionMode = LogConnectionMode.NORMAL,
            periodicSyncEnabled = false
        )
        
        assertEquals("ws://test.com", config.serverUrl)
        assertEquals(false, config.connectionMode == LogConnectionMode.NORMAL)
        assertEquals(false, config.periodicSyncEnabled)
    }
}
