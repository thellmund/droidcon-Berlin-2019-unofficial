package com.hellmund.droidcon2019.ui.speakers

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hellmund.droidcon2019.data.model.Speaker
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class SpeakersRepository(
    private val context: Context // Yes, I know this is bad
) {

    private val type = object : TypeToken<List<Speaker>>() {}.type
    private val gson = Gson()

    private fun loadSpeakers() {
        val asset = context.assets.open("speakers.json")
        val text = asset.bufferedReader().readText()
        speakers += gson.fromJson<List<Speaker>>(text, type)
    }

    fun getSpeaker(name: String): Speaker? {
        if (speakers.isEmpty()) {
            loadSpeakers()
        }

        // TODO works?
        return speakers.firstOrNull { it.name.contains(name) }
    }

    fun getSpeakers(): Observable<List<Speaker>> {
        if (speakers.isEmpty()) {
            loadSpeakers()
        }
        return Observable.just(speakers)
            .subscribeOn(Schedulers.io())
            .map { speakers -> speakers.sortedBy { it.name } }
    }

    companion object {
        private val speakers = mutableListOf<Speaker>()
    }

}
