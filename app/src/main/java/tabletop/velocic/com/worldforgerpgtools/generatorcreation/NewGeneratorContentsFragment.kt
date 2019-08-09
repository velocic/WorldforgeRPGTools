package tabletop.velocic.com.worldforgerpgtools.generatorcreation

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_new_generator_contents.*
import kotlinx.android.synthetic.main.list_item_generator_contents.view.*
import tabletop.velocic.com.worldforgerpgtools.appcommon.ProbabilityTableKey
import tabletop.velocic.com.worldforgerpgtools.appcommon.getProbabilityTableSizeFromKey
import tabletop.velocic.com.worldforgerpgtools.extensions.combineAscending
import tabletop.velocic.com.worldforgerpgtools.generatorcreation.viewmodels.newgeneratorcontents.MainUserInput
import tabletop.velocic.com.worldforgerpgtools.generatorcreation.viewmodels.newgeneratorcontents.MergeRowsFlowInteractions
import tabletop.velocic.com.worldforgerpgtools.generatorcreation.viewmodels.newgeneratorcontents.PrimaryFlowInteractions
import tabletop.velocic.com.worldforgerpgtools.persistence.Generator
import tabletop.velocic.com.worldforgerpgtools.persistence.GeneratorPersister
import tabletop.velocic.com.worldforgerpgtools.persistence.TableEntry
import tabletop.velocic.com.worldforgerpgtools.R
import tabletop.velocic.com.worldforgerpgtools.appcommon.savedStateMissingArgumentMessage
import tabletop.velocic.com.worldforgerpgtools.persistence.ResultItemDetail

class NewGeneratorContentsFragment : androidx.fragment.app.Fragment()
{
    private lateinit var newGenerator: Generator
    private lateinit var tableData: ProbabilityTableKey

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_new_generator_contents, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            //Editing a pending generator previously submitted to GeneratorCreationFragment
            if (args.containsKey(EXTRA_GENERATOR)) {
                initializeFromExistingGenerator(args)
            } else {
                //Creating a new generator from GeneratorCreationFragment for the first time
                initializeFromExternalArgs(args)
                arguments = null
            }
        } ?: savedInstanceState?.let { savedState ->
            initializeFromExistingGenerator(savedState)
        }

        val layoutInflater = LayoutInflater.from(activity) ?: throw IllegalStateException("Attempted to create" +
            " a LayoutInflater from a null Activity instance")

        val fragmentManager = activity?.supportFragmentManager ?: throw IllegalStateException("Failed to retrieve" +
            " a required FragmentManager instance.")

        new_generator_contents.layoutManager = LinearLayoutManager(activity)
        new_generator_contents.adapter = NewGeneratorContentsAdapter(
            newGenerator,
            tableData,
            layoutInflater,
            resources,
            fragmentManager,
            this
        )

        new_generator_submit.setOnClickListener {
            targetFragment?.onActivityResult(
                targetRequestCode,
                Activity.RESULT_OK,
                Intent().apply {
                    putExtra(EXTRA_GENERATOR, newGenerator)
                    putExtra(EXTRA_TABLE_DATA, tableData)
                }
            )

            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val missingArgumentMessage = "Missing required argument %s for REQUEST_%s"
        val illegalStateMessage = "Received call to onActivityResult with a null intent (i.e. no result data)"

        when (requestCode) {
            REQUEST_RESULT_ITEM_DETAILS -> if (resultCode == Activity.RESULT_OK) {
                val targetRow = data?.getIntExtra(ResultItemDetailsFragment.EXTRA_ROW_INDEX, -1)
                    ?: throw IllegalStateException(missingArgumentMessage.format("targetRow", "RESULT_ITEM_DETAILS"))
                val resultItemDetails = data.getParcelableArrayListExtra<ResultItemDetail>(ResultItemDetailsFragment.EXTRA_RESULT_ITEM_DETAILS)
                    ?: throw IllegalArgumentException(illegalStateMessage)

                newGenerator.table[targetRow].metadata = resultItemDetails
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(EXTRA_GENERATOR, newGenerator)
        outState.putParcelable(EXTRA_TABLE_DATA, tableData)

        super.onSaveInstanceState(outState)
    }

    private fun initializeFromExternalArgs(args: Bundle) {
        val tableTemplate = args.getSerializable(ARG_GENERATOR_TABLE_TEMPLATE) as GeneratorTableTemplate?
        val customTableSize = args.getInt(ARG_CUSTOM_TABLE_SIZE)

        tableTemplate?.let {
            tableData = it.tableData
        } ?: customTableSize.let {
            tableData = ProbabilityTableKey(dieSize = it)
        }

        newGenerator = Generator("Placeholder Name", 1, listOf(), GeneratorPersister.GENERATOR_DATA_FOLDER)
        newGenerator.table = generateBlankTableEntries(tableData.numDie, getProbabilityTableSizeFromKey(tableData))
    }

    private fun initializeFromExistingGenerator(savedInstanceState: Bundle) {
        newGenerator = savedInstanceState.getParcelable(EXTRA_GENERATOR)
            ?: throw IllegalArgumentException(savedStateMissingArgumentMessage.format("newGenerator", "NewGeneratorContentsFragment"))
        tableData = savedInstanceState.getParcelable(EXTRA_TABLE_DATA)
            ?: throw IllegalArgumentException(savedStateMissingArgumentMessage.format("tableData", "NewGeneratorContentsFragment"))
    }

    private fun generateBlankTableEntries(startIndex: Int = 1, numEntries: Int) : List<TableEntry> =
        List(numEntries) { currentIndex ->
            TableEntry("", listOf(), "${currentIndex + startIndex}", null)
        }

    companion object {
        private const val ARG_GENERATOR_TABLE_TEMPLATE = "generator_table_template"
        private const val ARG_CUSTOM_TABLE_SIZE = "custom_table_size"
        const val EXTRA_TABLE_DATA = "tabletop.velocic.com.worldforgerpgtools.table_data"
        const val EXTRA_GENERATOR = "tabletop.velocic.com.worldforgerpgtools.generator"
        const val REQUEST_RESULT_ITEM_DETAILS = 0

        fun newInstance(generatorTableTemplate: GeneratorTableTemplate): NewGeneratorContentsFragment =
            NewGeneratorContentsFragment().apply {
                arguments = bundleOf(
                    ARG_GENERATOR_TABLE_TEMPLATE to generatorTableTemplate
                )
            }

        fun newInstance(customTableSize: Int): NewGeneratorContentsFragment =
            NewGeneratorContentsFragment().apply {
                arguments = bundleOf(
                    ARG_CUSTOM_TABLE_SIZE to customTableSize
                )
            }

        fun newInstance(pendingNewGeneratorData: PendingNewGeneratorData) =
            NewGeneratorContentsFragment().apply {
                arguments = bundleOf(
                    EXTRA_GENERATOR to pendingNewGeneratorData.newGenerator,
                    EXTRA_TABLE_DATA to pendingNewGeneratorData.tableData
                )
            }
    }
}

private class NewGeneratorContentsAdapter(
    private val generator: Generator,
    private val tableData: ProbabilityTableKey,
    private val layoutInflater: LayoutInflater,
    private val resources: Resources,
    private val fragmentManager: FragmentManager,
    private val targetFragment: NewGeneratorContentsFragment
) : RecyclerView.Adapter<NewGeneratorContentsViewHolder>()
{
    private val combineRowsEventState = CombineRowsEventStateTracker()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewGeneratorContentsViewHolder {
        val view = layoutInflater.inflate(R.layout.list_item_generator_contents, parent, false)

        return NewGeneratorContentsViewHolder(
            view,
            this::combineRowsEventHandler,
            this::expandCombinedRowsEventHandler,
            this::editDetailsEventHandler,
            resources
        )
    }

    override fun getItemCount(): Int = generator.table.size

    override fun onBindViewHolder(holder: NewGeneratorContentsViewHolder, position: Int) =
        holder.bind(position, generator.table[position], combineRowsEventState, tableData)

    override fun getItemViewType(position: Int): Int = R.layout.list_item_generator_contents

    private fun combineRowsEventHandler(rowIndex: Int, isCancelRequest: Boolean) {
        val clearEventState = {
            combineRowsEventState.initialRowIndex = 0
            combineRowsEventState.currentlyProcessingCombineEvent = false
        }

        if (isCancelRequest) {
            clearEventState()
            notifyDataSetChanged()
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

            val collapsedRow = TableEntry(
                initialRow.name,
                initialRow.metadata,
                "${collapsedRowRange.first}-${collapsedRowRange.last}",
                initialRow.rerollSubTable
            )

            generator.table = (rowsBeforeTargetRange + collapsedRow + rowsAfterTargetRange)

            clearEventState()
            notifyDataSetChanged()
            return
        }

        combineRowsEventState.initialRowIndex = rowIndex
        combineRowsEventState.currentlyProcessingCombineEvent = true

        notifyDataSetChanged()
    }

    private fun expandCombinedRowsEventHandler(rowIndex: Int) {
        val targetRow = generator.table[rowIndex]
        val rangeToSplit = targetRow.diceRange

        if (rangeToSplit.first == rangeToSplit.last) {
            return
        }

        val rowsBeforeTargetRange = generator.table.filter { it.diceRange.first < rangeToSplit.first }
        val rowsAfterTargetRange = generator.table.filter { it.diceRange.last > rangeToSplit.last }
        val newRows = rangeToSplit.map { TableEntry("", listOf(), "$it", null) }
        newRows[0].copy(targetRow)
        newRows[0].diceRangeString = "${rangeToSplit.first}"

        generator.table = (rowsBeforeTargetRange + newRows + rowsAfterTargetRange)
        notifyDataSetChanged()
    }

    private fun editDetailsEventHandler(rowIndex: Int, resultItemName: String, previouslySavedDetails: ArrayList<ResultItemDetail>?) {
        val resultItemDetailsFragment = ResultItemDetailsFragment.newInstance(rowIndex, resultItemName, previouslySavedDetails)
        resultItemDetailsFragment.setTargetFragment(targetFragment, NewGeneratorContentsFragment.REQUEST_RESULT_ITEM_DETAILS)

        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, resultItemDetailsFragment)
            .addToBackStack(null)
            .commit()
    }
}

private class NewGeneratorContentsViewHolder(
    rowView: View,
    combineRowsEventHandler: (Int, Boolean) -> Unit,
    expandCombinedRowsEventHandler: (Int) -> Unit,
    editDetailsEventHandler: (Int, String, ArrayList<ResultItemDetail>?) -> Unit,
    resources: Resources
) : RecyclerView.ViewHolder(rowView)
{
    private val selectedRowColor = ResourcesCompat.getColor(resources, R.color.colorSelectedRow, null)
    private val unselectedRowColor = ResourcesCompat.getColor(resources, R.color.colorUnselectedRow, null)
    private val mainUserInput = MainUserInput(rowView.generator_contents_main_body as ViewGroup)
    private val primaryFlow = PrimaryFlowInteractions(
        rowView.generator_contents_main_buttons as ViewGroup,
        editDetailsClickHandler = editDetailsEventHandler,
        mergeRowsClickHandler = combineRowsEventHandler,
        splitMergedRowsClickHandler = expandCombinedRowsEventHandler
    )
    private val mergeRowsFlow = MergeRowsFlowInteractions(
        rowView.generator_contents_merge_rows_buttons as ViewGroup,
        combineRowsEventHandler
    )

    fun bind(
        rowIndex: Int,
        rowData: TableEntry,
        combineRowsEventState: CombineRowsEventStateTracker,
        tableData: ProbabilityTableKey
    ) {
        mainUserInput.bind(rowData)
        mainUserInput.updateResultChance(tableData)
        primaryFlow.bind(rowIndex, rowData)
        mergeRowsFlow.bind(rowIndex)

        if (combineRowsEventState.currentlyProcessingCombineEvent) {
            transitionToMergeRowsFlow(rowIndex == combineRowsEventState.initialRowIndex)

            return
        }

        transitionToPrimaryFlow()
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
