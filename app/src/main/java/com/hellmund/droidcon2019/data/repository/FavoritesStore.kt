package com.hellmund.droidcon2019.data.repository

import android.content.SharedPreferences
import com.hellmund.droidcon2019.data.model.Session

private const val KEY_FAVORITES = "KEY_FAVORITES"

class FavoritesStore(
    private val sharedPrefs: SharedPreferences
) {

    private val favorites: Set<String>
        get() = sharedPrefs.getStringSet(KEY_FAVORITES, emptySet())

    fun isFavorite(event: Session): Boolean {
        return favorites.contains(event.title)
    }

    fun toggleFavorite(event: Session) {
        val items = favorites.toMutableSet()

        if (items.contains(event.title)) {
            items.remove(event.title)
        } else {
            items.add(event.title)
        }

        sharedPrefs.edit().putStringSet(KEY_FAVORITES, items).apply()
    }

}
