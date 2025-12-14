package com.chatterboxtalk.core.observability

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date

/**
 * Android mirror of iOS `Analytics.swift`.
 * Provides minimal, lightweight analytics recording.
 */

data class AnalyticsEvent(
    val name: String,
    val properties: Map<String, String> = emptyMap(),
    val context: Map<String, String> = emptyMap(),
    val timestamp: Date = Date()
)

interface AnalyticsSink {
    suspend fun record(event: AnalyticsEvent)
}

interface AnalyticsRecording {
    fun record(event: AnalyticsEvent)
}

class AnalyticsRecorder(
    private val sinks: List<AnalyticsSink> = emptyList()
) : AnalyticsRecording {
    
    override fun record(event: AnalyticsEvent) {
        if (sinks.isEmpty()) return
        // Fire-and-forget: record to all sinks asynchronously
        sinks.forEach { sink ->
            GlobalScope.launch {
                sink.record(event)
            }
        }
    }
}

/**
 * Logs analytics events to logcat for debugging.
 * Only logs event name and property keys to avoid leaking values.
 */
class LogcatAnalyticsSink : AnalyticsSink {
    override suspend fun record(event: AnalyticsEvent) {
        val keys = event.properties.keys.sorted().joinToString(",")
        Log.i("Analytics", "Event: ${event.name} props=$keys")
    }
}

