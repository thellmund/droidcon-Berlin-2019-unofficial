package com.hellmund.droidcon2019.ui.schedule.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.Talk
import kotlinx.android.synthetic.main.list_item_event.view.favoriteButton
import kotlinx.android.synthetic.main.list_item_event.view.presenterTextView
import kotlinx.android.synthetic.main.list_item_event.view.stageTextView
import kotlinx.android.synthetic.main.list_item_event.view.titleTextView

class SearchResultsAdapter(
    private val onItemClick: (Talk) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    private val items = mutableListOf<Talk>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onItemClick)
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<Talk>) {
        items.clear()
        items += newItems
        notifyDataSetChanged()
    }

    fun clear() {
        update(emptyList())
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            event: Talk,
            onItemClick: (Talk) -> Unit
        ) = with(itemView) {
            titleTextView.text = event.title
            presenterTextView.text = event.speaker
            stageTextView.text = event.stage.name
            favoriteButton.isVisible = false
            setOnClickListener { onItemClick(event) }
        }

    }

}