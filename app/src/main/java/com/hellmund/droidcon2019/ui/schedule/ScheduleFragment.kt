package com.hellmund.droidcon2019.ui.schedule

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.transaction
import com.google.android.material.tabs.TabLayout
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.EventDay
import com.hellmund.droidcon2019.data.model.Session
import com.hellmund.droidcon2019.ui.schedule.details.EventDetailsFragment
import com.hellmund.droidcon2019.ui.schedule.filter.Filter
import com.hellmund.droidcon2019.ui.schedule.filter.FilterChip
import com.hellmund.droidcon2019.ui.schedule.filter.FilterFragment
import com.hellmund.droidcon2019.ui.schedule.filter.FilterStore
import com.hellmund.droidcon2019.ui.schedule.search.SearchResultsAdapter
import com.hellmund.droidcon2019.ui.shared.BaseFragment
import kotlinx.android.synthetic.main.fragment_schedule.contentContainer
import kotlinx.android.synthetic.main.fragment_schedule.fab
import kotlinx.android.synthetic.main.fragment_schedule.searchRecyclerView
import kotlinx.android.synthetic.main.fragment_schedule.tabLayout
import kotlinx.android.synthetic.main.fragment_schedule.toolbar
import kotlinx.android.synthetic.main.fragment_schedule.viewPager
import kotlinx.android.synthetic.main.view_active_filters.activeFiltersChipGroup
import kotlinx.android.synthetic.main.view_active_filters.activeFiltersContainer
import kotlinx.android.synthetic.main.view_active_filters.clearFilterButton
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class ScheduleFragment : BaseFragment() {

    private lateinit var searchView: SearchView

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            viewPager.setCurrentItem(tab.position, true)
        }
        override fun onTabReselected(tab: TabLayout.Tab) {
            val adapter = viewPager.adapter as DaysAdapter
            val fragment = adapter.getFragmentAt(viewPager.currentItem)
            fragment?.scrollToTop()
        }
        override fun onTabUnselected(tab: TabLayout.Tab) = Unit
    }

    private val daysAdapter: DaysAdapter by lazy {
        DaysAdapter(childFragmentManager, this::onEventClick)
    }

    private val searchResultsAdapter: SearchResultsAdapter by lazy {
        SearchResultsAdapter(this::onEventClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_schedule, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager.adapter = daysAdapter
        searchRecyclerView.adapter = searchResultsAdapter

        tabLayout.addOnTabSelectedListener(onTabSelectedListener)
        tabLayout.setupWithViewPager(viewPager)

        setCurrentDay()

        fab.setOnClickListener { openFilters() }
        activeFiltersContainer.setOnClickListener { openFilters() }

        clearFilterButton.setOnClickListener {
            val store = FilterStore.getInstance()
            store.clear()
            onFilterChanged(Filter.empty())
        }
    }

    private fun openFilters() {
        val fragment = FilterFragment.newInstance(this::onFilterChanged)
        fragment.show(childFragmentManager, fragment.tag)
    }

    private fun onFilterChanged(filter: Filter) {
        val isFilterActive = filter != Filter.empty()
        fab.isVisible = isFilterActive.not()
        activeFiltersContainer.isVisible = isFilterActive

        if (isFilterActive) {
            activeFiltersChipGroup.removeAllViews()
            val items = filter.getActiveFilters(requireContext())
            val chips = items
                .map { FilterChip(requireContext()).apply { text = it } }

            chips.forEach {
                it.disable()
                activeFiltersChipGroup.addView(it)
            }
        }

        val adapter = viewPager.adapter as DaysAdapter
        for (index in 0 until adapter.count) {
            val fragment = adapter.getFragmentAt(index)
            fragment?.applyFilter(filter)
        }
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
    }

    private fun setCurrentDay() {
        val today = LocalDate.now()
        val dates = EventDay.values().map { it.toDate() }

        val index = dates.indexOfFirst { it == today }.takeIf { it != -1 } ?: 0
        viewPager.currentItem = index
    }

    private fun onEventClick(event: Session) {
        requireFragmentManager().transaction {
            setCustomAnimations(R.anim.slide_in_right, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_right)
            replace(R.id.contentFrame, EventDetailsFragment.newInstance(event))
            addToBackStack(null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_speakers, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        val searchManager = requireContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val info = searchManager.getSearchableInfo(requireActivity().componentName)
        searchView.setSearchableInfo(info)
        searchView.maxWidth = Int.MAX_VALUE

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                searchResultsAdapter.clear()
                tabLayout.isVisible = false
                contentContainer.isVisible = false
                searchRecyclerView.isVisible = true
                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                searchResultsAdapter.clear()
                tabLayout.isVisible = true
                contentContainer.isVisible = true
                searchRecyclerView.isVisible = false
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            private val events = ScheduleRepository.getInstance(requireContext()).getAll()

            override fun onQueryTextSubmit(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    return false
                }

                val results = events.filter { it.contains(newText) }
                searchResultsAdapter.update(results)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    return false
                }

                val results = events.filter { it.contains(newText) }
                searchResultsAdapter.update(results)
                return false
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        tabLayout.removeOnTabSelectedListener(onTabSelectedListener)
        viewPager.adapter = null
        searchRecyclerView.adapter = null
        super.onDestroyView()
    }

    class DaysAdapter(
        fm: FragmentManager,
        private val onEventClick: (Session) -> Unit
    ) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val cache = ArrayMap<Int, DayFragment>()

        override fun getItem(position: Int): Fragment {
            val eventDay = EventDay.values()[position]
            return DayFragment.newInstance(eventDay, onEventClick).also {
                cache[position] = it
            }
        }

        fun getFragmentAt(index: Int): DayFragment? {
            return cache[index]
        }

        override fun getCount(): Int {
            return EventDay.values().size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            val formatter = DateTimeFormatter.ofPattern("EEE, MMMM d")
            return EventDay.values()[position].toDate().format(formatter)
        }

    }

    companion object {
        fun newInstance() = ScheduleFragment()
    }

}
