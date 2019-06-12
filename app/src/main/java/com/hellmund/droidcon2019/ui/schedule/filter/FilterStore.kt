package com.hellmund.droidcon2019.ui.schedule.filter

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.hellmund.droidcon2019.data.model.Stage
import com.hellmund.droidcon2019.data.model.Type
import org.jetbrains.anko.defaultSharedPreferences

private const val KEY_FILTER = "KEY_FILTER"

data class Filter(
    var isFavorites: Boolean = false,
    var stages: MutableList<Stage> = mutableListOf(),
    var types: MutableList<Type> = mutableListOf()
) {

    companion object {
        val EMPTY = Filter()
    }

}

class FilterStore(
    context: Context
) {

    private val sharedPrefs = context.defaultSharedPreferences
    private val gson = Gson()

    fun get(): Filter {
        val value = sharedPrefs.getString(KEY_FILTER)
        if (value == null) {
            val filter = Filter()
            put(filter)
            return filter
        }
        return gson.fromJson(value, Filter::class.java)
    }

    private fun put(filter: Filter) {
        val value = gson.toJson(filter)
        sharedPrefs.edit().putString(KEY_FILTER, value).apply()
    }

    fun toggleFavorites() {
        transaction {
            isFavorites = isFavorites.not()
        }
    }

    fun toggleStage(stage: Stage) {
        transaction {
            if (stages.contains(stage)) {
                stages.remove(stage)
            } else {
                stages.add(stage)
            }
        }
    }

    fun toggleType(type: Type) {
        transaction {
            if (types.contains(type)) {
                types.remove(type)
            } else {
                types.add(type)
            }
        }
    }

    private fun transaction(block: Filter.() -> Unit) {
        val filter = get()
        filter.block()
        put(filter)
    }

    private fun SharedPreferences.getString(key: String): String? {
        return if (contains(key)) getString(key, "") else null
    }

}
