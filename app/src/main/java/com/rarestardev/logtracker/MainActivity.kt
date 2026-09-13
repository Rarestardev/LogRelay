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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                LogTracker.i(LogTags.UI, "User clicked on Info button")
            }) {
                Text("Log Info")
            }
            Button(
                onClick = {
                    LogTracker.e(LogTags.NETWORK, "Mock network error occurred", Throwable("Network Timeout"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Log Error")
            }
        }

        HorizontalDivider()

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
        "ERROR" -> MaterialTheme.colorScheme.errorContainer
        "INFO" -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    val textColor = when (log.level) {
        "ERROR" -> MaterialTheme.colorScheme.onErrorContainer
        "INFO" -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSecondaryContainer
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