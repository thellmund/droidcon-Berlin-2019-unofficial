package com.hellmund.droidcon2019.ui.schedule.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.Chip
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.Stage
import com.hellmund.droidcon2019.data.model.Type
import com.hellmund.droidcon2019.ui.shared.RoundedBottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_filter.closeButton
import kotlinx.android.synthetic.main.fragment_filter.favoritesChip
import kotlinx.android.synthetic.main.fragment_filter.stagesChipGroup
import kotlinx.android.synthetic.main.fragment_filter.typesChipGroup

class FilterFragment : RoundedBottomSheetDialogFragment() {

    private val filterStore: FilterStore by lazy {
        FilterStore(requireContext())
    }

    private var onFilterChanged: (Filter) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_filter, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        closeButton.setOnClickListener { dismiss() }

        favoritesChip.isChecked = filterStore.get().isFavorites
        favoritesChip.setOnCheckedChangeListener { compoundButton, isChecked ->
            val chip = compoundButton as Chip
            chip.isCloseIconVisible = isChecked
            filterStore.toggleFavorites()
            onFilterChanged(filterStore.get())
        }
        favoritesChip.setOnCloseIconClickListener {
            val chip = it as Chip
            chip.isCloseIconVisible = false
            filterStore.toggleFavorites()
            onFilterChanged(filterStore.get())
        }

        val stages = Stage.values().map { it.name }
        val stageChips = getChips(stages)
        stageChips.forEachIndexed { index, chip ->
            chip.isChecked = filterStore.get().stages.contains(Stage.values()[index])

            chip.setOnCheckedChangeListener { compoundButton, isChecked ->
                val c = compoundButton as Chip
                c.isCloseIconVisible = isChecked
                filterStore.toggleStage(Stage.values()[index])
                onFilterChanged(filterStore.get())
            }
            chip.setOnCloseIconClickListener {
                val c = it as Chip
                c.isCloseIconVisible = false
                filterStore.toggleStage(Stage.values()[index])
                onFilterChanged(filterStore.get())
            }
            stagesChipGroup.addView(chip)
        }

        val types = Type.values().map { it.name }
        val typeChips = getChips(types)
        typeChips.forEachIndexed { index, chip ->
            chip.isChecked = filterStore.get().types.contains(Type.values()[index])

            chip.setOnCheckedChangeListener { compoundButton, isChecked ->
                val c = compoundButton as Chip
                c.isCloseIconVisible = isChecked
                filterStore.toggleType(Type.values()[index])
                onFilterChanged(filterStore.get())
            }
            chip.setOnCloseIconClickListener {
                val c = it as Chip
                c.isCloseIconVisible = false
                filterStore.toggleType(Type.values()[index])
                onFilterChanged(filterStore.get())
            }
            typesChipGroup.addView(chip)
        }
    }

    private fun getChips(values: List<String>): List<Chip> {
        return values.map { value ->
            FilterChip(requireContext()).apply { text = value }
        }
    }

    companion object {
        fun newInstance(
            onFilterChanged: (Filter) -> Unit
        ) = FilterFragment().apply {
            this.onFilterChanged = onFilterChanged
        }
    }

}
