/**
 * Author: rarestardev
 * Website: rarestar.kavix-team.com
 */
package com.rarestardev.logtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rarestardev.logrelay.database.LogEntity
import com.rarestardev.logtracker.ui.theme.LogTrackerTheme
import com.rarestardev.logrelay.core.LogTracker
import com.rarestardev.logrelay.core.LogTags
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LogTrackerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(title = { Text("Log Tracker Viewer") })
                    }
                ) { innerPadding ->
                    LogScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LogScreen(modifier: Modifier = Modifier) {
    val logs by LogTracker.getAllLogs().collectAsState(initial = emptyList())

    Column(modifier = modifier.fillMaxSize()) {
        // First row of buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    val formattedLog = LogFormatter.format(
                        appName = "LogTracker Demo",
                        appVersion = "1.0.0",
                        apiVersion = "v1",
                        message = "User clicked on Info button"
                    )
                    LogTracker.i(LogTags.UI, formattedLog)
                }
            ) {
                Text("Info", fontSize = 12.sp)
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    LogTracker.d(LogTags.DATABASE, "Querying user records...")
                }
            ) {
                Text("Debug", fontSize = 12.sp)
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    LogTracker.v(LogTags.INTERNAL, "Initializing WebSocket keep-alive")
                }
            ) {
                Text("Verbose", fontSize = 12.sp)
            }
        }

        // Second row of buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    LogTracker.w(LogTags.WORKER, "Background sync delayed due to power saving")
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("Warn", fontSize = 12.sp)
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    LogTracker.e(LogTags.NETWORK, "Mock network error occurred", Throwable("Network Timeout"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Error", fontSize = 12.sp)
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    LogTracker.wtf(LogTags.AUTH, "Security token corrupted!", IllegalStateException("Invalid Token"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("WTF", fontSize = 12.sp)
            }
        }

        // Control buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    LogTracker.i(LogTags.UI, "Multi-log sequence started")
                    LogTracker.d(LogTags.DATABASE, "Querying user records...")
                    LogTracker.w(LogTags.WORKER, "Background sync delayed due to power saving")
                    LogTracker.v(LogTags.INTERNAL, "Initializing WebSocket keep-alive")
                    LogTracker.wtf(LogTags.AUTH, "Security token corrupted!", IllegalStateException("Invalid Token"))
                }
            ) {
                Text("Log All", fontSize = 12.sp)
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    LogTracker.clearAllLogs()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
            ) {
                Text("Clear All", fontSize = 12.sp)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(logs.reversed()) { log ->
                LogItem(log)
            }
        }
    }
}

@Composable
fun LogItem(log: LogEntity) {
    val color = when (log.level) {
        "ERROR", "ASSERT" -> MaterialTheme.colorScheme.errorContainer
        "WARNING" -> MaterialTheme.colorScheme.tertiaryContainer
        "INFO" -> MaterialTheme.colorScheme.primaryContainer
        "DEBUG" -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when (log.level) {
        "ERROR", "ASSERT" -> MaterialTheme.colorScheme.onErrorContainer
        "WARNING" -> MaterialTheme.colorScheme.onTertiaryContainer
        "INFO" -> MaterialTheme.colorScheme.onPrimaryContainer
        "DEBUG" -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val time = sdf.format(Date(log.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "[${log.tag}]",
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 12.sp
                )
                Text(
                    text = time,
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }
            Text(
                text = log.message,
                color = textColor,
                fontSize = 14.sp
            )
            log.throwable?.let {
                Text(
                    text = it.take(100) + "...",
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}