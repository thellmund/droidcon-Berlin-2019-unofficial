package com.hellmund.droidcon2019.ui.schedule

import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.transaction
import com.google.android.material.tabs.TabLayout
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.EventDay
import com.hellmund.droidcon2019.data.model.Talk
import com.hellmund.droidcon2019.ui.schedule.details.EventDetailsFragment
import com.hellmund.droidcon2019.ui.schedule.filter.Filter
import com.hellmund.droidcon2019.ui.schedule.filter.FilterFragment
import kotlinx.android.synthetic.main.fragment_schedule.fab
import kotlinx.android.synthetic.main.fragment_schedule.tabLayout
import kotlinx.android.synthetic.main.fragment_schedule.toolbar
import kotlinx.android.synthetic.main.fragment_schedule.viewPager
import org.threeten.bp.LocalDate

class ScheduleFragment : Fragment() {

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            viewPager.setCurrentItem(tab.position, true)
        }
        override fun onTabReselected(tab: TabLayout.Tab) = Unit
        override fun onTabUnselected(tab: TabLayout.Tab) = Unit
    }

    private val adapter: DaysAdapter by lazy {
        DaysAdapter(childFragmentManager)
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
        viewPager.adapter = adapter

        tabLayout.addOnTabSelectedListener(onTabSelectedListener)
        tabLayout.setupWithViewPager(viewPager)

        setCurrentDay()

        fab.setOnClickListener {
            val fragment = FilterFragment.newInstance(this::onFilterChanged)
            fragment.show(childFragmentManager, fragment.tag)
        }
    }

    private fun onFilterChanged(filter: Filter) {
        val fragment = adapter.getFragmentAt(viewPager.currentItem)
        fragment?.applyFilter(filter)
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

    private fun onEventClick(event: Talk) {
        requireFragmentManager().transaction {
            replace(R.id.contentFrame, EventDetailsFragment.newInstance(event))
            addToBackStack(null)
        }
    }

    override fun onDestroyView() {
        tabLayout.removeOnTabSelectedListener(onTabSelectedListener)
        super.onDestroyView()
    }

    inner class DaysAdapter(
        fm: FragmentManager
    ) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val cache = ArrayMap<Int, DayFragment>()

        override fun getItem(position: Int): Fragment {
            val eventDay = EventDay.values()[position]
            return DayFragment.newInstance(eventDay, ::onEventClick).also {
                cache[position] = it
            }
        }

        fun getFragmentAt(index: Int) = cache[index]

        override fun getCount(): Int {
            return EventDay.values().size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return EventDay.values()[position].name
        }

    }

    companion object {
        fun newInstance() = ScheduleFragment()
    }

}
