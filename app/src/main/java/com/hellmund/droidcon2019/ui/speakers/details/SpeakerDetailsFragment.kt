package com.hellmund.droidcon2019.ui.speakers.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.Speaker
import com.hellmund.droidcon2019.ui.shared.BaseFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_speaker_details.companyTextView
import kotlinx.android.synthetic.main.fragment_speaker_details.descriptionTextView
import kotlinx.android.synthetic.main.fragment_speaker_details.imageView
import kotlinx.android.synthetic.main.fragment_speaker_details.linksRecyclerView
import kotlinx.android.synthetic.main.fragment_speaker_details.nameTextView
import kotlinx.android.synthetic.main.fragment_speaker_details.roleTextView
import kotlinx.android.synthetic.main.fragment_speaker_details.scrollView
import kotlinx.android.synthetic.main.fragment_speaker_details.toolbar

class SpeakerDetailsFragment : BaseFragment() {

    private val speaker: Speaker by lazy {
        checkNotNull(arguments?.getParcelable<Speaker>(KEY_SPEAKER))
    }

    private val onScrollListener = ViewTreeObserver.OnScrollChangedListener {
        val isAtTop = scrollView.canScrollVertically(-1)
        toolbar.isSelected = isAtTop
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_speaker_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = null
        }

        Picasso.get()
            .load(speaker.imageUrl)
            .into(imageView)

        nameTextView.text = speaker.name
        roleTextView.text = speaker.role
        companyTextView.text = speaker.company
        descriptionTextView.text = speaker.description

        val links = speaker.links
        if (links.isNullOrEmpty()) {
            linksRecyclerView.isVisible = false
        } else {
            linksRecyclerView.adapter = LinksAdapter(links)
        }

        scrollView.viewTreeObserver.addOnScrollChangedListener(onScrollListener)
    }

    override fun onDestroyView() {
        scrollView.viewTreeObserver.removeOnScrollChangedListener(onScrollListener)
        super.onDestroyView()
    }

    companion object {

        private const val KEY_SPEAKER = "KEY_SPEAKER"

        fun newInstance(speaker: Speaker) = SpeakerDetailsFragment().apply {
            arguments = bundleOf(KEY_SPEAKER to speaker)
        }

    }

}
