package com.hellmund.droidcon2019.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Locale

@Parcelize
data class Speaker(
    val name: String,
    val imageUrl: String,
    val role: String,
    val company: String,
    val description: String,
    val links: List<String>? = null
) : Parcelable {

    fun contains(text: String): Boolean {
        return name.toLowerCase(Locale.getDefault()).contains(text)
            || role.toLowerCase(Locale.getDefault()).contains(text)
            || company.toLowerCase(Locale.getDefault()).contains(text)
    }

}
