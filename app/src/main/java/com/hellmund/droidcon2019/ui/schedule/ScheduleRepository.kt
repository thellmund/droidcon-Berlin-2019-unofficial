package com.hellmund.droidcon2019.ui.schedule

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hellmund.droidcon2019.data.model.EventDay
import com.hellmund.droidcon2019.data.model.Session
import com.hellmund.droidcon2019.util.EventSerializer
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class ScheduleRepository(
    context: Context
) {

    private val type = object : TypeToken<List<Session>>() {}.type
    private val gson: Gson = EventSerializer.gson

    init {
        loadEventsFromAssets(context)
    }

    private fun loadEventsFromAssets(context: Context) {
        val asset = context.assets.open("schedule_v2.json")
        val text = asset.bufferedReader().readText()
        events += gson.fromJson<List<Session>>(text, type)
    }

    fun getAll(): List<Session> = events

    fun getEventId(event: Session): Int {
        return events.indexOf(event)
    }

    fun getEventsByDay(day: EventDay): Observable<List<Session>> {
        return Observable
            .fromCallable { events.filter { it.day == day } }
            .subscribeOn(Schedulers.io())
            .map { events -> events.sortedBy { it.startTime } }
    }

    companion object {
        private val events = mutableListOf<Session>()
        private var instance: ScheduleRepository? = null

        fun getInstance(context: Context): ScheduleRepository {
            if (instance == null) {
                instance = ScheduleRepository(context)
            }
            return instance!!
        }
    }

}
