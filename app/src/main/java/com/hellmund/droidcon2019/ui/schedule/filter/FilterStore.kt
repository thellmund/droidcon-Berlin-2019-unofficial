package com.hellmund.droidcon2019.ui.schedule.filter

import android.content.Context
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.Level
import com.hellmund.droidcon2019.data.model.Stage
import com.hellmund.droidcon2019.data.model.Type

data class Filter(
    var isFavorites: Boolean = false,
    var stages: MutableList<Stage> = mutableListOf(),
    var types: MutableList<Type> = mutableListOf(),
    var levels: MutableList<Level> = mutableListOf()
) {

    fun getActiveFilters(context: Context): List<String> {
        val results = mutableListOf<String>()

        if (isFavorites) {
            results += context.getString(R.string.my_favorites)
        }

        results += stages.map { it.name }
        results += types.map { it.value }
        results += levels.map { it.name }

        return results
    }

    companion object {
        fun empty() = Filter()
    }

}

class FilterStore {

    var filter = Filter.empty()

    fun toggleFavorites() {
        filter.isFavorites = filter.isFavorites.not()
    }

    fun toggleStage(stage: Stage) {
        if (filter.stages.contains(stage)) {
            filter.stages.remove(stage)
        } else {
            filter.stages.add(stage)
        }
    }

    fun toggleType(type: Type) {
        if (filter.types.contains(type)) {
            filter.types.remove(type)
        } else {
            filter.types.add(type)
        }
    }

    fun toggleLevel(level: Level) {
        if (filter.levels.contains(level)) {
            filter.levels.remove(level)
        } else {
            filter.levels.add(level)
        }
    }

    fun clear() {
        filter = Filter.empty()
    }

    companion object {

        private var instance: FilterStore? = null

        fun getInstance(): FilterStore {
            if (instance == null) {
                instance = FilterStore()
            }
            return instance!!
        }

    }

}
