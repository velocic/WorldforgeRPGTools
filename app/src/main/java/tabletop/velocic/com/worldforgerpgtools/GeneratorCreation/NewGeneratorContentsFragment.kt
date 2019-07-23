package tabletop.velocic.com.worldforgerpgtools.GeneratorCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_new_generator_contents.*
import kotlinx.android.synthetic.main.list_item_generator_contents.view.*
import tabletop.velocic.com.worldforgerpgtools.AppCommon.ProbabilityTableKey
import tabletop.velocic.com.worldforgerpgtools.AppCommon.getProbabilityTableSizeFromKey
import tabletop.velocic.com.worldforgerpgtools.GeneratorCreation.ViewModels.NewGeneratorContents.MainUserInput
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
        new_generator_contents.adapter = NewGeneratorContentsAdapter(newGenerator, tableData, layoutInflater)
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
    private val layoutInflater: LayoutInflater
) : RecyclerView.Adapter<NewGeneratorContentsViewHolder>()
{
    private val combineRowsEventState = CombineRowsEventStateTracker()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewGeneratorContentsViewHolder {
        val view = layoutInflater.inflate(R.layout.list_item_generator_contents, parent, false)

        val combineRowsEventHandler = combineRowsEventHandler@{row: Int, isCancelRequest: Boolean ->
            if (isCancelRequest) {
                combineRowsEventState.initialRow = 0
                combineRowsEventState.currentlyProcessingCombineEvent = false
                return@combineRowsEventHandler
            }

            if (combineRowsEventState.currentlyProcessingCombineEvent) {
                val rowsBeforeTargetRange = generator.table.filterIndexed { index, _ ->
                    index < minOf(combineRowsEventState.initialRow, row)
                }
                val rowsAfterTargetRange = generator.table.filterIndexed { index, _ ->
                    index > maxOf(combineRowsEventState.initialRow, row)
                }
                val collapsedRow = TableEntries(
                    generator.table[combineRowsEventState.initialRow].name,
                    generator.table[combineRowsEventState.initialRow].metadata,
                    "${minOf(combineRowsEventState.initialRow, row)}-${maxOf(combineRowsEventState.initialRow, row)}",
                    generator.table[combineRowsEventState.initialRow].rerollSubTable
                )

                generator.table = (rowsBeforeTargetRange + collapsedRow + rowsAfterTargetRange).toTypedArray()
                notifyDataSetChanged()

                return@combineRowsEventHandler
            }

            combineRowsEventState.initialRow = row
            combineRowsEventState.currentlyProcessingCombineEvent = true
        }

        return NewGeneratorContentsViewHolder(view, combineRowsEventHandler)
    }

    override fun getItemCount(): Int = generator.table.size

    override fun onBindViewHolder(holder: NewGeneratorContentsViewHolder, position: Int) =
        holder.bind(generator.table[position], tableData)

    override fun getItemViewType(position: Int): Int = R.layout.list_item_generator_contents
}

private class NewGeneratorContentsViewHolder(
    view: View,
    combineRowsEventHandler: (Int, Boolean) -> Unit
) : RecyclerView.ViewHolder(view)
{
    private val mainUserInput = MainUserInput(view.generator_contents_main_body as ViewGroup)

    fun bind(tableEntry: TableEntries, tableData: ProbabilityTableKey) {
        mainUserInput.bind(tableEntry)
        mainUserInput.updateResultChance(tableData)
    }
}

private data class CombineRowsEventStateTracker(var initialRow: Int = 0, var currentlyProcessingCombineEvent: Boolean = false)
