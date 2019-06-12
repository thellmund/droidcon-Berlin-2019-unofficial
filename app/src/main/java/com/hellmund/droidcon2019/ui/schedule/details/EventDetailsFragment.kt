package com.hellmund.droidcon2019.ui.schedule.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.Talk
import com.hellmund.droidcon2019.data.repository.FavoritesStore
import com.hellmund.droidcon2019.ui.shared.RoundedBottomSheetDialogFragment
import com.hellmund.droidcon2019.ui.speakers.SpeakersRepository
import com.hellmund.droidcon2019.ui.speakers.details.SpeakerDetailsFragment
import com.hellmund.droidcon2019.util.NotificationScheduler
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_event_details.descriptionTextView
import kotlinx.android.synthetic.main.fragment_event_details.favoriteButton
import kotlinx.android.synthetic.main.fragment_event_details.speakerCard
import kotlinx.android.synthetic.main.fragment_event_details.speakerCompanyTextView
import kotlinx.android.synthetic.main.fragment_event_details.speakerImageView
import kotlinx.android.synthetic.main.fragment_event_details.speakerNameTextView
import kotlinx.android.synthetic.main.fragment_event_details.speakerRoleTextView
import kotlinx.android.synthetic.main.fragment_event_details.stageTextView
import kotlinx.android.synthetic.main.fragment_event_details.timeTextView
import kotlinx.android.synthetic.main.fragment_event_details.titleTextView
import org.jetbrains.anko.defaultSharedPreferences
import org.threeten.bp.format.DateTimeFormatter

class EventDetailsFragment : RoundedBottomSheetDialogFragment() {

    private var onFavoriteClick: () -> Unit = {}

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
                val fragment = SpeakerDetailsFragment.newInstance(speaker)
                fragment.show(childFragmentManager, fragment.tag)
            }
        } else {
            speakerCard.isVisible = true
        }

        updateFavoriteIcon()

        favoriteButton.setOnClickListener {
            favoritesRepository.toggleFavorite(event)
            updateFavoriteIcon()

            if (favoritesRepository.isFavorite(event)) {
                NotificationScheduler(requireContext()).schedule(event)
            } else {
                NotificationScheduler(requireContext()).remove(event)
            }

            onFavoriteClick()
        }
    }

    private fun updateFavoriteIcon() {
        val isFavorite = favoritesRepository.isFavorite(event)
        val resId = if (isFavorite) R.drawable.ic_baseline_star else R.drawable.outline_star_border
        favoriteButton.setImageResource(resId)
    }

    companion object {

        private const val KEY_EVENT = "KEY_EVENT"

        fun newInstance(
            event: Talk,
            onFavoriteClick: () -> Unit
        ) = EventDetailsFragment().apply {
            arguments = bundleOf(KEY_EVENT to event)
            this.onFavoriteClick = onFavoriteClick
        }

    }

}
