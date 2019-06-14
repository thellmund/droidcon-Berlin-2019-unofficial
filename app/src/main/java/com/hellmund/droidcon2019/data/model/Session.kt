package com.hellmund.droidcon2019.data.model

import android.os.Parcelable
import com.hellmund.droidcon2019.R
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
    val speakers: List<String>,
    val day: EventDay,
    val description: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val type: Type,
    val level: Level
) : Parcelable, Filterable {

    val formattedSpeakers: String
        get() {
            return if (speakers.size > 2) {
                val first = speakers.first()
                "$first & ${speakers.size - 1} more"
            } else {
                speakers.joinToString(", ")
            }
        }

    override fun isInFilter(
        filter: Filter,
        favoritesStore: FavoritesStore
    ): Boolean {
        return if (filter == Filter.empty()) {
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

        if (speakers.isNotEmpty()) {
            val candidates = speakers.map { it.toLowerCase(Locale.getDefault()) }
            result = result || candidates.any { it.contains(text) }
        }

        result = result || description.toLowerCase(Locale.getDefault()).contains(text)

        return result
    }

}

enum class Stage {
    Pie, Oreo, Marshmallow, Nougat, Lollipop, None
}

enum class Type(val value: String, val resId: Int) {
    Session("Session", R.drawable.ic_outline_video_label),
    LightningTalk("Lightning talk", R.drawable.ic_outline_flash_on),
    Workshop("Workshop", R.drawable.ic_outline_build),
    PanelDiscussion ("Panel discussion", R.drawable.ic_outline_question_answer),
    None("None", 0)
}

enum class Level(val resId: Int) {
    Introductory(R.drawable.ic_outline_signal_cellular_one),
    Intermediate(R.drawable.ic_outline_signal_cellular_two),
    Advanced(R.drawable.ic_outline_signal_cellular_three),
    None(0)
}
