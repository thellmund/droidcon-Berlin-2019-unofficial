package com.hellmund.droidcon2019.ui.schedule.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.transaction
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.Level
import com.hellmund.droidcon2019.data.model.Session
import com.hellmund.droidcon2019.data.model.Speaker
import com.hellmund.droidcon2019.data.model.Stage
import com.hellmund.droidcon2019.data.model.Type
import com.hellmund.droidcon2019.data.repository.FavoritesStore
import com.hellmund.droidcon2019.ui.shared.BaseFragment
import com.hellmund.droidcon2019.ui.speakers.SpeakersAdapter
import com.hellmund.droidcon2019.ui.speakers.SpeakersRepository
import com.hellmund.droidcon2019.ui.speakers.details.SpeakerDetailsFragment
import com.hellmund.droidcon2019.util.NotificationScheduler
import kotlinx.android.synthetic.main.fragment_event_details.descriptionTextView
import kotlinx.android.synthetic.main.fragment_event_details.scrollView
import kotlinx.android.synthetic.main.fragment_event_details.speakersHeader
import kotlinx.android.synthetic.main.fragment_event_details.speakersRecyclerView
import kotlinx.android.synthetic.main.fragment_event_details.toolbar
import kotlinx.android.synthetic.main.view_event_info_container.addToFavoritesButton
import kotlinx.android.synthetic.main.view_event_info_container.levelTextView
import kotlinx.android.synthetic.main.view_event_info_container.removeFromFavoritesButton
import kotlinx.android.synthetic.main.view_event_info_container.stageTextView
import kotlinx.android.synthetic.main.view_event_info_container.timeTextView
import kotlinx.android.synthetic.main.view_event_info_container.titleTextView
import kotlinx.android.synthetic.main.view_event_info_container.typeTextView
import org.jetbrains.anko.defaultSharedPreferences
import org.threeten.bp.format.DateTimeFormatter

class EventDetailsFragment : BaseFragment() {

    private val favoritesRepository: FavoritesStore by lazy {
        FavoritesStore(requireContext().defaultSharedPreferences)
    }

    private val speakersRepository: SpeakersRepository by lazy {
        SpeakersRepository(requireContext())
    }

    private val event: Session by lazy {
        checkNotNull(arguments?.getParcelable<Session>(KEY_EVENT))
    }

    private val onScrollListener = ViewTreeObserver.OnScrollChangedListener {
        val isAtTop = scrollView.canScrollVertically(-1)
        toolbar.isSelected = isAtTop
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

        val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMMM d")
        val startFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val endFormatter = DateTimeFormatter.ofPattern("HH:mm")

        val formattedDate = dateFormatter.format(event.day.toDate())
        val formattedStart = startFormatter.format(event.startTime)
        val formattedEnd = endFormatter.format(event.endTime)

        timeTextView.text = "$formattedDate, $formattedStart – $formattedEnd"

        stageTextView.isVisible = event.stage != Stage.None
        stageTextView.text = event.stage.name

        typeTextView.isVisible = event.type != Type.None
        typeTextView.text = event.type.value
        typeTextView.setCompoundDrawablesWithIntrinsicBounds(event.type.resId, 0, 0, 0)

        levelTextView.isVisible = event.level != Level.None
        levelTextView.text = event.level.name
        levelTextView.setCompoundDrawablesWithIntrinsicBounds(event.level.resId, 0, 0, 0)

        descriptionTextView.text = event.description

        if (event.speakers.isNotEmpty()) {
            val speakers = event.speakers.mapNotNull { speakersRepository.getSpeaker(it) }
            val speakersAdapter = SpeakersAdapter(this::onSpeakerClick)
            speakersAdapter.update(speakers)
            speakersRecyclerView.adapter = speakersAdapter
        } else {
            speakersHeader.isVisible = false
            speakersRecyclerView.isVisible = false
        }

        // TODO
        /*
        if (event.speakers.isNotEmpty()) {
            val speakerName = event.formattedSpeakers
            val speaker = speakersRepository.getSpeaker(speakerName)

            Picasso.get()
                .load(speaker.imageUrl)
                .into(speakerImageView)

            speakerNameTextView.text = speaker.name
            speakerRoleTextView.text = speaker.role
            speakerCompanyTextView.text = speaker.company

            speakerCard.setOnClickListener {
                requireFragmentManager().transaction {
                    setCustomAnimations(R.anim.slide_in_right, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_right)
                    replace(R.id.contentFrame, SpeakerDetailsFragment.newInstance(speaker))
                    addToBackStack(null)
                }
            }
        } else {
            speakersHeader.isVisible = false
            speakerCard.isVisible = false
        }
        */

        scrollView.viewTreeObserver.addOnScrollChangedListener(onScrollListener)

        updateFavoriteIcon()

        addToFavoritesButton.setOnClickListener { toggleFavorite() }
        removeFromFavoritesButton.setOnClickListener { toggleFavorite() }
    }

    private fun onSpeakerClick(speaker: Speaker) {
        requireFragmentManager().transaction {
            setCustomAnimations(R.anim.slide_in_right, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_right)
            replace(R.id.contentFrame, SpeakerDetailsFragment.newInstance(speaker))
            addToBackStack(null)
        }
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

    override fun onDestroyView() {
        scrollView.viewTreeObserver.removeOnScrollChangedListener(onScrollListener)
        super.onDestroyView()
    }

    companion object {

        private const val KEY_EVENT = "KEY_EVENT"

        fun newInstance(
            event: Session
        ) = EventDetailsFragment().apply {
            arguments = bundleOf(KEY_EVENT to event)
        }

    }

}
