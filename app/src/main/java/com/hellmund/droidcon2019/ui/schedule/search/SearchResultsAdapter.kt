package com.hellmund.droidcon2019.ui.schedule.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.Session
import kotlinx.android.synthetic.main.list_item_event.view.favoriteButton
import kotlinx.android.synthetic.main.list_item_event.view.presenterTextView
import kotlinx.android.synthetic.main.list_item_event.view.stageTextView
import kotlinx.android.synthetic.main.list_item_event.view.titleTextView

class SearchResultsAdapter(
    private val onItemClick: (Session) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    private val items = mutableListOf<Session>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onItemClick)
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<Session>) {
        items.clear()
        items += newItems
        notifyDataSetChanged()
    }

    fun clear() {
        update(emptyList())
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            event: Session,
            onItemClick: (Session) -> Unit
        ) = with(itemView) {
            titleTextView.text = event.title
            presenterTextView.isVisible = event.speakers.isNotEmpty()
            presenterTextView.text = event.formattedSpeakers
            stageTextView.text = event.stage.name
            favoriteButton.isVisible = false
            setOnClickListener { onItemClick(event) }
        }

    }

}