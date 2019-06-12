package com.hellmund.droidcon2019.data.model

import android.os.Parcelable
import com.hellmund.droidcon2019.data.repository.FavoritesStore
import com.hellmund.droidcon2019.ui.schedule.filter.Filter
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalTime

interface Filterable {
    fun isInFilter(filter: Filter, favoritesStore: FavoritesStore): Boolean
}

@Parcelize
data class Talk(
    val title: String,
    val stage: Stage,
    val speaker: String? = null,
    val day: EventDay,
    val description: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val type: Type? = null,
    val level: Level? = null
) : Parcelable, Filterable {

    override fun isInFilter(
        filter: Filter,
        favoritesStore: FavoritesStore
    ): Boolean {
        return if (filter == Filter.EMPTY) {
            true
        } else {
            var result = true
            if (filter.isFavorites) {
                result = result && favoritesStore.isFavorite(this)
            }
            if (filter.stages.isNotEmpty()) {
                result = result && filter.stages.contains(stage)
            }
            if (filter.types.isNotEmpty()) {
                result = result && filter.types.contains(type)
            }
            return result
        }
    }

}

enum class Type {
    Talk, LightningTalk, Workshop
}

enum class Level {
    Introductory, Intermediate, Advanced
}
