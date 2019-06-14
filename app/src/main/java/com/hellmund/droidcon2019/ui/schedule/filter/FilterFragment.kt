package com.hellmund.droidcon2019.ui.schedule.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.Chip
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.Level
import com.hellmund.droidcon2019.data.model.Stage
import com.hellmund.droidcon2019.data.model.Type
import com.hellmund.droidcon2019.ui.shared.RoundedBottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_filter.closeButton
import kotlinx.android.synthetic.main.fragment_filter.favoritesChip
import kotlinx.android.synthetic.main.fragment_filter.levelChipGroup
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
            chip.isCloseIconVisible = false
        }

        val stages = Stage.values().toList().minus(Stage.None).map { it.name }
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
                c.isChecked = false
                c.isCloseIconVisible = false
            }
            stagesChipGroup.addView(chip)
        }

        val types = Type.values().toList().minus(Type.None).map { it.value }
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
                c.isCloseIconVisible = false
            }
            typesChipGroup.addView(chip)
        }

        val levels = Level.values().toList().minus(Level.None).map { it.name }
        val levelChips = getChips(levels)
        levelChips.forEachIndexed { index, chip ->
            chip.isChecked = filterStore.get().levels.contains(Level.values()[index])

            chip.setOnCheckedChangeListener { compoundButton, isChecked ->
                val c = compoundButton as Chip
                c.isCloseIconVisible = isChecked
                filterStore.toggleLevel(Level.values()[index])
                onFilterChanged(filterStore.get())
            }
            chip.setOnCloseIconClickListener {
                val c = it as Chip
                c.isCloseIconVisible = false
                c.isCloseIconVisible = false
            }
            levelChipGroup.addView(chip)
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
