package com.hellmund.droidcon2019.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.EventDay
import com.hellmund.droidcon2019.data.model.Talk
import com.hellmund.droidcon2019.data.repository.FavoritesStore
import com.hellmund.droidcon2019.ui.schedule.filter.Filter
import com.hellmund.droidcon2019.ui.shared.EqualSpacingItemDecoration
import com.hellmund.droidcon2019.util.observe
import kotlinx.android.synthetic.main.fragment_day.eventsRecyclerView
import org.jetbrains.anko.defaultSharedPreferences
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import kotlin.math.roundToInt

class DayFragment : Fragment() {

    private val viewModel: DayViewModel by lazy {
        val factory = DayViewModel.Factory(day, requireActivity().application)
        ViewModelProviders.of(this, factory).get(DayViewModel::class.java)
    }

    private val favoritesStore: FavoritesStore by lazy {
        FavoritesStore(requireContext().defaultSharedPreferences)
    }

    private val adapter: EventsAdapter by lazy {
        EventsAdapter(favoritesStore, this::onEventClick)
    }

    val day: EventDay by lazy {
        checkNotNull(arguments?.getSerializable(KEY_EVENT_DAY) as EventDay)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_day, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.events.observe(viewLifecycleOwner, this::render)
        eventsRecyclerView.adapter = adapter

        val spacing = requireContext().resources.getDimension(R.dimen.default_space).roundToInt()
        eventsRecyclerView.addItemDecoration(EqualSpacingItemDecoration(spacing))

        val intent = requireActivity().intent
        val eventFromNotification = intent.getParcelableExtra(KEY_EVENT) as? Talk
        eventFromNotification?.let {
            showEventDetails(it)
            intent.removeExtra(KEY_EVENT)
        }
    }

    private fun render(events: List<Talk>) {
        adapter.update(events)

        if (LocalDate.now() == day.toDate()) {
            val now = LocalTime.now()

            val currentTime = adapter.items
                .mapIndexed { index, item -> index to item }
                .filter { (_, item) -> item is AdapterItem.Header }
                .map { (index, item) -> index to (item as AdapterItem.Header) }
                .lastOrNull { (_, item) -> item.time.isBefore(now) }

            currentTime?.let { (index, _) ->
                eventsRecyclerView.scrollToPosition(index)
            }
        }
    }

    private fun onEventClick(event: Talk) {
        showEventDetails(event)
    }

    private fun showEventDetails(event: Talk) {
        onEventClick.invoke(event)
        /*val fragment = EventDetailsFragment.newInstance(event, this::onFavoriteClick)
        fragment.show(childFragmentManager, fragment.tag)*/
    }

    private fun onFavoriteClick() {
        adapter.notifyDataSetChanged()
    }

    fun applyFilter(filter: Filter) {
        adapter.applyFilter(filter)
    }

    private var onEventClick: (Talk) -> Unit = {}

    companion object {

        private const val KEY_EVENT_DAY = "KEY_EVENT_DAY"
        private const val KEY_EVENT = "KEY_EVENT"

        fun newInstance(
            day: EventDay,
            onEventClick: (Talk) -> Unit
        ) = DayFragment().apply {
            arguments = bundleOf(KEY_EVENT_DAY to day)
            this.onEventClick = onEventClick
        }

    }

}
