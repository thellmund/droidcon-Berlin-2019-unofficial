package com.hellmund.droidcon2019.ui.speakers.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hellmund.droidcon2019.R
import kotlinx.android.synthetic.main.list_item_link.view.linkTextView
import org.jetbrains.anko.browse

class LinksAdapter(
    private val links: List<String>
) : RecyclerView.Adapter<LinksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_link, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(links[position])
    }

    override fun getItemCount() = links.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(link: String) = with(itemView) {
            linkTextView.text = "@" + link.replace("https://twitter.com/", "")
            setOnClickListener {
                context.browse(link)
            }
        }

    }

}
