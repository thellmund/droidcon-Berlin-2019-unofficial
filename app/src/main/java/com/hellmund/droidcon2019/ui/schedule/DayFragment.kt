package com.hellmund.droidcon2019.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.EventDay
import com.hellmund.droidcon2019.data.model.Session
import com.hellmund.droidcon2019.data.repository.FavoritesStore
import com.hellmund.droidcon2019.ui.schedule.filter.Filter
import com.hellmund.droidcon2019.ui.schedule.filter.FilterStore
import com.hellmund.droidcon2019.ui.shared.BaseFragment
import kotlinx.android.synthetic.main.fragment_day.scheduleRecyclerView
import org.jetbrains.anko.defaultSharedPreferences

class DayFragment : BaseFragment() {

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
        viewModel.events.observe(viewLifecycleOwner, Observer<List<Session>> { render(it) })
        scheduleRecyclerView.adapter = adapter

        val filterStore = FilterStore.instance
        if (filterStore.filter != Filter.empty()) {
            applyFilter(filterStore.filter)
        }

        val intent = requireActivity().intent
        val eventFromNotification = intent.getParcelableExtra(KEY_EVENT) as? Session
        eventFromNotification?.let {
            showEventDetails(it)
            intent.removeExtra(KEY_EVENT)
        }
    }

    private fun render(events: List<Session>) {
        adapter.update(events)
    }

    private fun onEventClick(event: Session) {
        showEventDetails(event)
    }

    private fun showEventDetails(event: Session) {
        onEventClick.invoke(event)
    }

    fun applyFilter(filter: Filter) {
        adapter.applyFilter(filter)
    }

    fun scrollToTop() {
        scheduleRecyclerView.smoothScrollToPosition(0)
    }

    override fun onDestroyView() {
        scheduleRecyclerView.adapter = null
        super.onDestroyView()
    }

    private var onEventClick: (Session) -> Unit = {}

    companion object {

        private const val KEY_EVENT_DAY = "KEY_EVENT_DAY"
        private const val KEY_EVENT = "KEY_EVENT"

        fun newInstance(
            day: EventDay,
            onEventClick: (Session) -> Unit
        ) = DayFragment().apply {
            arguments = bundleOf(KEY_EVENT_DAY to day)
            this.onEventClick = onEventClick
        }

    }

}
