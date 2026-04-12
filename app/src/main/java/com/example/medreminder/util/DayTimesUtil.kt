package com.example.medreminder.util

import org.json.JSONArray
import org.json.JSONObject

data class ScheduleEntry(val days: List<Int>, val times: List<String>)

object DayTimesUtil {

    /** Parse JSON array [{"d":[1,3],"t":["09:00"]}] → List<ScheduleEntry>
     *  Also handles legacy map format {"1":"09:00"} for backward compatibility. */
    fun parseEntries(json: String): List<ScheduleEntry> {
        if (json.isBlank()) return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                val dArr = obj.getJSONArray("d")
                val tArr = obj.getJSONArray("t")
                ScheduleEntry(
                    days = (0 until dArr.length()).map { dArr.getInt(it) },
                    times = (0 until tArr.length()).map { tArr.getString(it) }
                )
            }
        } catch (e: Exception) {
            // Fallback: old map format {"1":"09:00,21:00"} — one entry per day
            try {
                val obj = JSONObject(json)
                obj.keys().asSequence().mapNotNull { key ->
                    val day = key.toIntOrNull() ?: return@mapNotNull null
                    val times = obj.getString(key).split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    ScheduleEntry(days = listOf(day), times = times)
                }.toList()
            } catch (e2: Exception) { emptyList() }
        }
    }

    fun serializeEntries(entries: List<ScheduleEntry>): String {
        if (entries.isEmpty()) return ""
        val arr = JSONArray()
        entries.forEach { entry ->
            val obj = JSONObject()
            val dArr = JSONArray().also { a -> entry.days.sorted().forEach { a.put(it) } }
            val tArr = JSONArray().also { a -> entry.times.forEach { a.put(it) } }
            obj.put("d", dArr); obj.put("t", tArr)
            arr.put(obj)
        }
        return arr.toString()
    }

    /** Times scheduled for a specific day (union across all matching entries). */
    fun getTimesForDay(entries: List<ScheduleEntry>, day: Int): List<String> =
        entries.filter { day in it.days }.flatMap { it.times }.distinct().sorted()

    /** All unique times across all entries — used for alarm scheduling. */
    fun allUniqueTimes(entries: List<ScheduleEntry>): List<String> =
        entries.flatMap { it.times }.distinct().sorted()

    /** All days covered by any entry. */
    fun allDays(entries: List<ScheduleEntry>): List<Int> =
        entries.flatMap { it.days }.distinct().sorted()
}