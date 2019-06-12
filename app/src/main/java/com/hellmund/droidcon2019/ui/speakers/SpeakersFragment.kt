package com.hellmund.droidcon2019.ui.speakers

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.Speaker
import com.hellmund.droidcon2019.util.observe
import com.hellmund.droidcon2019.ui.shared.BackPressable
import com.hellmund.droidcon2019.ui.shared.MaterialDividerItemDecoration
import com.hellmund.droidcon2019.ui.shared.Reselectable
import com.hellmund.droidcon2019.ui.speakers.details.SpeakerDetailsFragment
import com.hellmund.droidcon2019.ui.speakers.search.SearchHistoryStore
import kotlinx.android.synthetic.main.fragment_speakers.recyclerView
import kotlinx.android.synthetic.main.fragment_speakers.toolbar
import kotlin.math.roundToInt

class SpeakersFragment : Fragment(), Reselectable, BackPressable {

    private val viewModel: SpeakersViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(SpeakersViewModel::class.java)
    }

    private val searchHistoryStore: SearchHistoryStore by lazy {
        SearchHistoryStore(requireContext())
    }

    private lateinit var searchView: SearchView

    private val adapter: SpeakersAdapter = SpeakersAdapter(this::onItemClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_speakers, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.speakers.observe(viewLifecycleOwner, this::render)
        recyclerView.adapter = adapter

        val padding = requireContext().resources.getDimension(R.dimen.material_list_inset).roundToInt()
        recyclerView.addItemDecoration(MaterialDividerItemDecoration(requireContext(), padding))
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
    }

    private fun render(speakers: List<Speaker>) {
        adapter.update(speakers)
    }

    private fun onItemClick(speaker: Speaker) {
        val fragment = SpeakerDetailsFragment.newInstance(speaker)
        fragment.show(childFragmentManager, fragment.tag)
    }

    override fun onReselected() {
        recyclerView.scrollToPosition(0)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_speakers, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        val searchManager = requireContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val info = searchManager.getSearchableInfo(requireActivity().componentName)
        searchView.setSearchableInfo(info)
        searchView.maxWidth = Int.MAX_VALUE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed(): Boolean {
        return if (searchView.isIconified.not()) {
            searchView.isIconified = true
            true
        } else {
            false
        }
    }

    companion object {
        fun newInstance() = SpeakersFragment()
    }

}
