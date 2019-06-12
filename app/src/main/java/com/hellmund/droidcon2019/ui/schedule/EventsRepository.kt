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

/*class FakeEventsRepository : EventsRepository {

    override fun getEventsByDay(day: EventDay): Observable<List<Talk>> {
        val speaker = Speaker(
            "Till Hellmund",
            "https://static.wixstatic.com/media/6e1ab2_6e0b91b4f2684b4c9daf7e946225b1d3~mv2.png",
            "Student + Android Developer",
            "Freeletics",
            "Lorem ipsum",
            listOf("https://twitter.com/tillhellmund")
        )
        return Observable.just(
            listOf(
                Talk(
                    "Title 1",
                    Stage.Marshmallow,
                    speaker = speaker.name,
                    day = EventDay.Monday,
                    startTime = LocalTime.of(10, 0),
                    endTime = LocalTime.of(11, 0),
                    description = "Lorem ipsum"
                ),
                Talk(
                    "Title 2",
                    Stage.Marshmallow,
                    speaker = speaker.name,
                    day = EventDay.Monday,
                    startTime = LocalTime.of(10, 0),
                    endTime = LocalTime.of(10, 45),
                    description = "Lorem ipsum"
                ),
                Talk(
                    "Title 3",
                    Stage.Pie,
                    speaker = speaker.name,
                    day = EventDay.Tuesday,
                    startTime = LocalTime.of(11, 0),
                    endTime = LocalTime.of(11, 30),
                    description = "Lorem ipsum"
                ),
                Talk(
                    "Title 4",
                    Stage.Pie,
                    speaker = speaker.name,
                    day = EventDay.Tuesday,
                    startTime = LocalTime.of(13, 0),
                    endTime = LocalTime.of(14, 30),
                    description = "Lorem ipsum"
                ),
                Talk(
                    "Title 5",
                    Stage.Pie,
                    speaker = speaker.name,
                    day = EventDay.Tuesday,
                    startTime = LocalTime.of(14, 0),
                    endTime = LocalTime.of(14, 30),
                    description = "Lorem ipsum"
                ),
                Talk(
                    "Title 3",
                    Stage.Pie,
                    speaker = speaker.name,
                    day = EventDay.Tuesday,
                    startTime = LocalTime.of(15, 0),
                    endTime = LocalTime.of(16, 0),
                    description = "Lorem ipsum"
                )
            )
        )
    }

}*/
