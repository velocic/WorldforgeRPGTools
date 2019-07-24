package tabletop.velocic.com.worldforgerpgtools.GeneratorCreation

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_new_generator_contents.*
import kotlinx.android.synthetic.main.list_item_generator_contents.view.*
import tabletop.velocic.com.worldforgerpgtools.AppCommon.ProbabilityTableKey
import tabletop.velocic.com.worldforgerpgtools.AppCommon.getProbabilityTableSizeFromKey
import tabletop.velocic.com.worldforgerpgtools.Extensions.combineAscending
import tabletop.velocic.com.worldforgerpgtools.GeneratorCreation.ViewModels.NewGeneratorContents.MainUserInput
import tabletop.velocic.com.worldforgerpgtools.GeneratorCreation.ViewModels.NewGeneratorContents.MergeRowsFlowInteractions
import tabletop.velocic.com.worldforgerpgtools.GeneratorCreation.ViewModels.NewGeneratorContents.PrimaryFlowInteractions
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.Generator
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorImporter
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.TableEntries
import tabletop.velocic.com.worldforgerpgtools.R

class NewGeneratorContentsFragment : androidx.fragment.app.Fragment()
{
    private val newGenerator = Generator("Placeholder Name", 1, arrayOf(), GeneratorImporter.GENERATOR_DATA_FOLDER)
    lateinit var tableData: ProbabilityTableKey

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_new_generator_contents, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tableTemplate = arguments?.getSerializable(ARG_GENERATOR_TABLE_TEMPLATE) as GeneratorTableTemplate?
        val customTableSize = arguments?.getInt(ARG_CUSTOM_TABLE_SIZE)

        tableTemplate?.let {
            tableData = it.tableData
        } ?: customTableSize?.let {
            tableData = ProbabilityTableKey(dieSize = it)
        } ?: throw IllegalStateException("Missing necessary arguments to initialize a new generator.")

        initializeGenerator(tableData)

        val layoutInflater = LayoutInflater.from(activity) ?: throw IllegalStateException("Attempted to create" +
            " a LayoutInflater from a null Activity instance")

        new_generator_contents.layoutManager = LinearLayoutManager(activity)
        new_generator_contents.adapter = NewGeneratorContentsAdapter(newGenerator, tableData, layoutInflater, resources)
    }

    private fun initializeGenerator(tableData: ProbabilityTableKey) {
        newGenerator.table = generateBlankTableEntries(tableData.numDie, getProbabilityTableSizeFromKey(tableData))
    }

    private fun generateBlankTableEntries(startIndex: Int = 1, numEntries: Int) : Array<TableEntries> =
        Array(numEntries) { currentIndex ->
            TableEntries("", mapOf(), "${currentIndex + startIndex}", null)
        }

    companion object {
        private const val ARG_GENERATOR_TABLE_TEMPLATE = "generator_table_template"
        private const val ARG_CUSTOM_TABLE_SIZE = "custom_table_size"

        fun newInstance(generatorTableTemplate: GeneratorTableTemplate): NewGeneratorContentsFragment =
            NewGeneratorContentsFragment().apply {
                arguments = bundleOf(
                    Pair(ARG_GENERATOR_TABLE_TEMPLATE, generatorTableTemplate)
                )
            }

        fun newInstance(customTableSize: Int): NewGeneratorContentsFragment =
            NewGeneratorContentsFragment().apply {
                arguments = bundleOf(
                    Pair(ARG_CUSTOM_TABLE_SIZE, customTableSize)
                )
            }
    }
}

private class NewGeneratorContentsAdapter(
    private val generator: Generator,
    private val tableData: ProbabilityTableKey,
    private val layoutInflater: LayoutInflater,
    private val resources: Resources
) : RecyclerView.Adapter<NewGeneratorContentsViewHolder>()
{
    private val combineRowsEventState = CombineRowsEventStateTracker()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewGeneratorContentsViewHolder {
        val view = layoutInflater.inflate(R.layout.list_item_generator_contents, parent, false)

        return NewGeneratorContentsViewHolder(
            view,
            this::combineRowsEventHandler,
            {},
            resources
        )
    }

    override fun getItemCount(): Int = generator.table.size

    override fun onBindViewHolder(holder: NewGeneratorContentsViewHolder, position: Int) =
        holder.bind(position, combineRowsEventState, generator.table[position], tableData)

    override fun getItemViewType(position: Int): Int = R.layout.list_item_generator_contents

    private fun combineRowsEventHandler(rowIndex: Int, isCancelRequest: Boolean) {
        if (isCancelRequest) {
            combineRowsEventState.initialRowIndex = 0
            combineRowsEventState.currentlyProcessingCombineEvent = false
            return
        }

        if (combineRowsEventState.currentlyProcessingCombineEvent) {
            val initialRow = generator.table[combineRowsEventState.initialRowIndex]
            val endRow = generator.table[rowIndex]

            val collapsedRowRange = IntRange.combineAscending(initialRow.diceRange, endRow.diceRange)

            val rowsBeforeTargetRange = generator.table.filter {
                it.diceRange.first < collapsedRowRange.first
            }
            val rowsAfterTargetRange = generator.table.filter {
                it.diceRange.first > collapsedRowRange.last
            }

            val collapsedRow = TableEntries(
                initialRow.name,
                initialRow.metadata,
                "${collapsedRowRange.first}-${collapsedRowRange.last}",
                initialRow.rerollSubTable
            )

            generator.table = (rowsBeforeTargetRange + collapsedRow + rowsAfterTargetRange).toTypedArray()
            notifyDataSetChanged()

            return
        }

        combineRowsEventState.initialRowIndex = rowIndex
        combineRowsEventState.currentlyProcessingCombineEvent = true

        notifyDataSetChanged()
    }
}

private class NewGeneratorContentsViewHolder(
    rowView: View,
    combineRowsEventHandler: (Int, Boolean) -> Unit,
    cancelCombineRowsEventHandler: () -> Unit,
    resources: Resources
) : RecyclerView.ViewHolder(rowView)
{
    private val selectedRowColor = ResourcesCompat.getColor(resources, R.color.colorSelectedRow, null)
    private val unselectedRowColor = ResourcesCompat.getColor(resources, R.color.colorUnselectedRow, null)
    private val mainUserInput = MainUserInput(rowView.generator_contents_main_body as ViewGroup)
    private val primaryFlow = PrimaryFlowInteractions(
        rowView.generator_contents_main_buttons as ViewGroup,
        mergeRowsClickHandler = combineRowsEventHandler
    )
    private val mergeRowsFlow = MergeRowsFlowInteractions(
        rowView.generator_contents_merge_rows_buttons as ViewGroup,
        cancelCombineRowsEventHandler
    )

    fun bind(
        rowIndex: Int,
        combineRowsEventState: CombineRowsEventStateTracker,
        tableEntry: TableEntries,
        tableData: ProbabilityTableKey
    ) {
        mainUserInput.bind(tableEntry)
        mainUserInput.updateResultChance(tableData)
        updateRowIndex(rowIndex)

        if (combineRowsEventState.currentlyProcessingCombineEvent) {
            transitionToMergeRowsFlow(rowIndex == combineRowsEventState.initialRowIndex)

            return
        }

        transitionToPrimaryFlow()
    }

    private fun updateRowIndex(rowIndex: Int) {
        primaryFlow.rowIndex = rowIndex
        mergeRowsFlow.rowIndex = rowIndex
    }

    private fun transitionToMergeRowsFlow(isInitialSelectedRowForMerge: Boolean) {
        if (isInitialSelectedRowForMerge) {
            itemView.setBackgroundColor(selectedRowColor)
            mergeRowsFlow.show(isInitialSelectedRowForMerge)
            primaryFlow.hide()

            return
        }

        itemView.setBackgroundColor(unselectedRowColor)
        mergeRowsFlow.show(isInitialSelectedRowForMerge)
        primaryFlow.hide()
    }

    private fun transitionToPrimaryFlow() {
        itemView.setBackgroundColor(unselectedRowColor)
        mergeRowsFlow.hide()
        primaryFlow.show()
    }
}

private data class CombineRowsEventStateTracker(var initialRowIndex: Int = 0, var currentlyProcessingCombineEvent: Boolean = false)
