package com.hellmund.droidcon2019.ui.schedule.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.Talk
import com.hellmund.droidcon2019.data.repository.FavoritesStore
import com.hellmund.droidcon2019.ui.speakers.SpeakersRepository
import com.hellmund.droidcon2019.util.NotificationScheduler
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_event_details.descriptionTextView
import kotlinx.android.synthetic.main.fragment_event_details.toolbar
import kotlinx.android.synthetic.main.view_event_info_container.addToFavoritesButton
import kotlinx.android.synthetic.main.view_event_info_container.removeFromFavoritesButton
import kotlinx.android.synthetic.main.view_event_info_container.stageTextView
import kotlinx.android.synthetic.main.view_event_info_container.timeTextView
import kotlinx.android.synthetic.main.view_event_info_container.titleTextView
import kotlinx.android.synthetic.main.view_speaker_card.speakerCard
import kotlinx.android.synthetic.main.view_speaker_card.speakerCompanyTextView
import kotlinx.android.synthetic.main.view_speaker_card.speakerImageView
import kotlinx.android.synthetic.main.view_speaker_card.speakerNameTextView
import kotlinx.android.synthetic.main.view_speaker_card.speakerRoleTextView
import org.jetbrains.anko.defaultSharedPreferences
import org.threeten.bp.format.DateTimeFormatter

class EventDetailsFragment : Fragment() {

    private val favoritesRepository: FavoritesStore by lazy {
        FavoritesStore(requireContext().defaultSharedPreferences)
    }

    private val speakersRepository: SpeakersRepository by lazy {
        SpeakersRepository(requireContext())
    }

    private val event: Talk by lazy {
        checkNotNull(arguments?.getParcelable<Talk>(KEY_EVENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_event_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = null
        }

        titleTextView.text = event.title

        val formattedStart = event.startTime.format(DateTimeFormatter.ISO_LOCAL_TIME)
        val formattedEnd = event.endTime.format(DateTimeFormatter.ISO_LOCAL_TIME)
        timeTextView.text = "$formattedStart – $formattedEnd"

        stageTextView.text = event.stage.name
        descriptionTextView.text = event.description

        val speakerName = event.speaker

        if (speakerName != null) {
            val speaker = speakersRepository.getSpeaker(speakerName)
            Picasso.get()
                .load(speaker.imageUrl)
                .into(speakerImageView)

            speakerNameTextView.text = speaker.name
            speakerRoleTextView.text = speaker.role
            speakerCompanyTextView.text = speaker.company

            speakerCard.setOnClickListener {
                TODO()
                // val fragment = SpeakerDetailsFragment.newInstance(speaker)
                // fragment.show(childFragmentManager, fragment.tag)
            }
        } else {
            speakerCard.isVisible = true
        }

        updateFavoriteIcon()

        addToFavoritesButton.setOnClickListener { toggleFavorite() }
        removeFromFavoritesButton.setOnClickListener { toggleFavorite() }
    }

    private fun toggleFavorite() {
        favoritesRepository.toggleFavorite(event)
        updateFavoriteIcon()

        if (favoritesRepository.isFavorite(event)) {
            NotificationScheduler(requireContext()).schedule(event)
        } else {
            NotificationScheduler(requireContext()).remove(event)
        }
    }

    private fun updateFavoriteIcon() {
        val isFavorite = favoritesRepository.isFavorite(event)
        addToFavoritesButton.isVisible = isFavorite.not()
        removeFromFavoritesButton.isVisible = isFavorite
    }

    companion object {

        private const val KEY_EVENT = "KEY_EVENT"

        fun newInstance(
            event: Talk
        ) = EventDetailsFragment().apply {
            arguments = bundleOf(KEY_EVENT to event)
        }

    }

}
