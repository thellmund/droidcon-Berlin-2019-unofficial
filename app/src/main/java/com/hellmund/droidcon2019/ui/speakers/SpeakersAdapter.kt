package com.hellmund.droidcon2019.ui.speakers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.Speaker
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_speaker.view.companyTextView
import kotlinx.android.synthetic.main.list_item_speaker.view.imageView
import kotlinx.android.synthetic.main.list_item_speaker.view.nameTextView
import kotlinx.android.synthetic.main.list_item_speaker.view.roleTextView

class SpeakersAdapter(
    private val onItemClick: (Speaker) -> Unit
) : RecyclerView.Adapter<SpeakersAdapter.ViewHolder>(), Filterable {

    private val speakers = mutableListOf<Speaker>()
    private var filtered: MutableList<Speaker>? = null

    private val items: List<Speaker>
        get() = filtered ?: speakers

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_speaker, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onItemClick)
    }

    override fun getItemCount() = items.size

    fun update(newSpeakers: List<Speaker>) {
        speakers.clear()
        speakers += newSpeakers
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                val results = FilterResults()

                if (query.isNullOrEmpty()) {
                    filtered = null
                } else {
                    results.values = speakers.filter { it.contains(query.toString()) }
                }

                return results
            }

            override fun publishResults(query: CharSequence?, results: FilterResults?) {
                filtered = results?.values as MutableList<Speaker>?
                notifyDataSetChanged()
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            speaker: Speaker,
            onItemClick: (Speaker) -> Unit
        ) = with(itemView) {
            Picasso.get()
                .load(speaker.imageUrl)
                .into(imageView)

            nameTextView.text = speaker.name
            roleTextView.text = speaker.role
            companyTextView.text = speaker.company

            setOnClickListener { onItemClick(speaker) }
        }

    }

}
