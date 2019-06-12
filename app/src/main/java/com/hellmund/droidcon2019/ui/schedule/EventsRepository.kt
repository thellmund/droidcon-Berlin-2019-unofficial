package com.hellmund.droidcon2019.ui.schedule

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hellmund.droidcon2019.data.model.EventDay
import com.hellmund.droidcon2019.data.model.Talk
import com.hellmund.droidcon2019.util.EventSerializer
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

private const val KEY_EVENTS = "KEY_EVENTS"

class EventsRepository(
    context: Context
) {

    private val type = object : TypeToken<List<Talk>>() {}.type
    private val gson: Gson = EventSerializer.gson

    init {
        loadEventsFromAssets(context)
    }

    private fun loadEventsFromAssets(context: Context) {
        val asset = context.assets.open("schedule_v1.json")
        val text = asset.bufferedReader().readText()
        events += gson.fromJson<List<Talk>>(text, type)
    }

    fun getAll(): List<Talk> = events

    fun getEventId(event: Talk): Int {
        return events.indexOf(event)
    }

    fun getEventsByDay(day: EventDay): Observable<List<Talk>> {
        return Observable
            .fromCallable { events.filter { it.day == day } }
            .subscribeOn(Schedulers.io())
            .map { events -> events.sortedBy { it.startTime } }
    }

    companion object {
        private val events = mutableListOf<Talk>()
        private var instance: EventsRepository? = null

        fun getInstance(context: Context): EventsRepository {
            if (instance == null) {
                instance = EventsRepository(context)
            }
            return instance!!
        }
    }

}
