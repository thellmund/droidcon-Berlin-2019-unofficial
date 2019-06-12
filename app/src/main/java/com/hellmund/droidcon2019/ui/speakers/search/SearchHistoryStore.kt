package com.hellmund.droidcon2019.ui.speakers.search

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.defaultSharedPreferences

private const val KEY_SEARCH_HISTORY = "KEY_SEARCH_HISTORY"

class SearchHistoryStore(
    context: Context
) {

    private val type = object : TypeToken<List<String>>() {}.type
    private val gson = Gson()
    private val sharedPrefs = context.defaultSharedPreferences

    fun get(): List<String> {
        val value = sharedPrefs.getString(KEY_SEARCH_HISTORY, "")
        return gson.fromJson<List<String>>(value, type)
    }

    fun put(query: String) {
        val items = get().toMutableList()
        items.add(query)

        val json = gson.toJson(items)
        sharedPrefs.edit().putString(KEY_SEARCH_HISTORY, json).apply()
    }

}
