package tabletop.velocic.com.worldforgerpgtools.GeneratorCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_new_generator_contents.*
import kotlinx.android.synthetic.main.list_item_generator_contents.view.*
import tabletop.velocic.com.worldforgerpgtools.GeneratorCreation.ViewModels.NewGeneratorContents.MainUserInput
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.Generator
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorImporter
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.TableEntries
import tabletop.velocic.com.worldforgerpgtools.R

class NewGeneratorContentsFragment : androidx.fragment.app.Fragment()
{
    private val newGenerator = Generator("Placeholder Name", 1, arrayOf(), GeneratorImporter.GENERATOR_DATA_FOLDER)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_new_generator_contents, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tableTemplate = arguments?.getSerializable(ARG_GENERATOR_TABLE_TEMPLATE) as GeneratorTableTemplate?
        val customTableSize = arguments?.getInt(ARG_CUSTOM_TABLE_SIZE)

        tableTemplate?.let {
            initializeGeneratorFromTemplate(it)
        } ?: customTableSize?.let {
            initializeGeneratorFromCustomTable(customTableSize)
        } ?: throw IllegalStateException("Missing necessary arguments to initialize a new generator.")

        val layoutInflater = LayoutInflater.from(activity) ?: throw IllegalStateException("Attempted to create" +
            " a LayoutInflater from a null Activity instance")

        new_generator_contents.adapter = NewGeneratorContentsAdapter(newGenerator, layoutInflater)
    }

    private fun initializeGeneratorFromTemplate(tableTemplate: GeneratorTableTemplate) =
        when (tableTemplate) {
            GeneratorTableTemplate.OneDFour -> newGenerator.table = generateBlankTableEntries(4)
            GeneratorTableTemplate.OneDSix -> newGenerator.table = generateBlankTableEntries(6)
            GeneratorTableTemplate.OneDEight -> newGenerator.table = generateBlankTableEntries(8)
            GeneratorTableTemplate.OneDTen -> newGenerator.table = generateBlankTableEntries(10)
            GeneratorTableTemplate.OneDTwelve -> newGenerator.table = generateBlankTableEntries(12)
            GeneratorTableTemplate.OneDTwenty -> newGenerator.table = generateBlankTableEntries(20)
            GeneratorTableTemplate.TwoDSix -> newGenerator.table = generateBlankTableEntries(11, 2)
            GeneratorTableTemplate.ThreeDSix -> newGenerator.table = generateBlankTableEntries(16, 3)
            GeneratorTableTemplate.OneDOneHundred -> newGenerator.table = generateBlankTableEntries(100)
        }

    private fun initializeGeneratorFromCustomTable(tableSize: Int) {
        newGenerator.table = generateBlankTableEntries(tableSize)
    }

    private fun generateBlankTableEntries(numEntries: Int, startIndex: Int = 1) : Array<TableEntries> =
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

class NewGeneratorContentsAdapter(
    private val generator: Generator,
    private val layoutInflater: LayoutInflater
) : RecyclerView.Adapter<NewGeneratorContentsViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewGeneratorContentsViewHolder {
        val view = layoutInflater.inflate(R.layout.list_item_generator_contents, parent, false)

        return NewGeneratorContentsViewHolder(view)
    }

    override fun getItemCount(): Int = generator.table.size

    override fun onBindViewHolder(holder: NewGeneratorContentsViewHolder, position: Int) =
        holder.bind(generator.table[position], dieSize = generator.table.size)

    override fun getItemViewType(position: Int): Int = R.layout.list_item_generator_contents
}

class NewGeneratorContentsViewHolder(
    view: View
) : RecyclerView.ViewHolder(view)
{
    private val mainUserInput = MainUserInput(view.generator_contents_main_body as ViewGroup)

    fun bind(tableEntry: TableEntries, numDie: Int = 1, dieSize: Int) {
        mainUserInput.bind(tableEntry)
        mainUserInput.updateResultChance(numDie, dieSize)
    }
}
