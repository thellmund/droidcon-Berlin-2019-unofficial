package com.hellmund.droidcon2019.data.model

import android.os.Parcelable
import com.hellmund.droidcon2019.data.repository.FavoritesStore
import com.hellmund.droidcon2019.ui.schedule.filter.Filter
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalTime
import java.util.Locale

interface Filterable {
    fun isInFilter(filter: Filter, favoritesStore: FavoritesStore): Boolean
}

@Parcelize
data class Session(
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

    fun contains(query: String): Boolean {
        val text = query.toLowerCase(Locale.getDefault())
        var result = false

        result = result || title.toLowerCase(Locale.getDefault()).contains(text)

        if (speaker != null) {
            result = result || speaker.toLowerCase(Locale.getDefault()).contains(text)
        }

        result = result || description.toLowerCase(Locale.getDefault()).contains(text)

        return result
    }

}

enum class Type {
    Talk, LightningTalk, Workshop
}

enum class Level {
    Introductory, Intermediate, Advanced
}
