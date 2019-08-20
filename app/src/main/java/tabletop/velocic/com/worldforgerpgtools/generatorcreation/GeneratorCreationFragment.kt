package tabletop.velocic.com.worldforgerpgtools.generatorcreation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_create_generator.*
import kotlinx.android.synthetic.main.fragment_create_generator.view.*
import kotlinx.android.synthetic.main.list_item_preview_generator_contents.view.*
import tabletop.velocic.com.worldforgerpgtools.R
import tabletop.velocic.com.worldforgerpgtools.appcommon.ProbabilityTableKey
import tabletop.velocic.com.worldforgerpgtools.appcommon.ProbabilityTables
import tabletop.velocic.com.worldforgerpgtools.appcommon.nullAndroidDependencyMessage
import tabletop.velocic.com.worldforgerpgtools.generatorcreation.viewmodels.GeneratorCreationInputEvents
import tabletop.velocic.com.worldforgerpgtools.generatorcreation.viewmodels.GeneratorCreationPreviewManager
import tabletop.velocic.com.worldforgerpgtools.generatorcreation.viewmodels.GeneratorCreationViewModel
import tabletop.velocic.com.worldforgerpgtools.persistence.Generator
import tabletop.velocic.com.worldforgerpgtools.persistence.GeneratorPersister
import tabletop.velocic.com.worldforgerpgtools.persistence.TableEntry

class GeneratorCreationFragment : androidx.fragment.app.Fragment()
{
    private lateinit var generatorCreationViewModel: GeneratorCreationViewModel
    private lateinit var inputEvents: GeneratorCreationInputEvents
    private lateinit var previewManager: GeneratorCreationPreviewManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View =
        inflater.inflate(R.layout.fragment_create_generator, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.create_generator_button_submit_new_generator.setOnClickListener {
            val action = "finalize new generator table"
            val nullCheckedContext = context ?:
                throw IllegalStateException(nullAndroidDependencyMessage.format("Context", action))
            val nullCheckedFragmentManager = fragmentManager ?:
                throw IllegalStateException(nullAndroidDependencyMessage.format("FragmentManager", action))
            val generator = generatorCreationViewModel.pendingGeneratorData.value?.newGenerator ?:
                throw IllegalStateException("Pending generator is null; nothing to finalize.")
            val generatorPath = generatorCreationViewModel.pendingGeneratorData.value?.newGenerator?.assetPath ?:
                throw IllegalArgumentException("Pending generator path is null; nowhere to put the new generator.")

            finalizeNewGenerator(nullCheckedContext, nullCheckedFragmentManager, generator, generatorPath)
        }

        val nullDependencyMessageAction = "initialize GeneratorCreationFragment"
        val nullCheckedFragmentManager = fragmentManager ?:
            throw IllegalStateException(nullAndroidDependencyMessage.format("FragmentManager", nullDependencyMessageAction))
        val inflater = layoutInflater ?:
            throw IllegalStateException(nullAndroidDependencyMessage.format("LayoutInflater", nullDependencyMessageAction))

        generatorCreationViewModel = ViewModelProviders.of(this)[GeneratorCreationViewModel::class.java]

        generatorCreationViewModel.generatorName.observe(this, Observer<String> { generatorName ->
            generatorCreationViewModel.pendingGeneratorData.value?.newGenerator?.name = generatorName
        })

        generatorCreationViewModel.categoryName.observe(this, Observer<String> { categoryName ->
            generatorCreationViewModel.pendingGeneratorData.value?.newGenerator?.assetPath = "${GeneratorPersister.GENERATOR_DATA_FOLDER}/$categoryName"
        })

        previewManager = GeneratorCreationPreviewManager(
            create_generator_templates,
            create_generator_preview,
            create_generator_button_submit_new_generator,
            nullCheckedFragmentManager,
            this,
            inflater,
            LinearLayoutManager(activity),
            REQUEST_NEW_GENERATOR_CONTENTS
        )

        generatorCreationViewModel.pendingGeneratorData.observe(this, Observer { pendingGeneratorData ->
            pendingGeneratorData?.let { previewManager.displayPendingGeneratorPreview(it) } ?:
                { previewManager.displayGeneratorTemplates() }()
        })

        inputEvents = GeneratorCreationInputEvents(
            edit_text_create_generator_name,
            edit_text_create_generator_category,
            generatorCreationViewModel,
            nullCheckedFragmentManager,
            this,
            REQUEST_NEW_CATEGORY_PATH
        )
    }

    override fun onResume() {
        super.onResume()

        edit_text_create_generator_name.setText(generatorCreationViewModel.generatorName.value, TextView.BufferType.EDITABLE)

        arguments?.let {
            generatorCreationViewModel.categoryName.value = it.getString(GeneratorCategorySelectionFragment.EXTRA_SELECTED_CATEGORY) ?: ""
            edit_text_create_generator_category.text = generatorCreationViewModel.categoryName.value
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_NEW_CATEGORY_PATH) {
            //Can't set the TextView directly, as this function is called before
            //onResume, and the TextView state gets lost
            arguments = data?.extras
            return
        }

        if (requestCode == REQUEST_NEW_GENERATOR_CONTENTS) {
            data?.run {
                generatorCreationViewModel.pendingGeneratorData.value = PendingNewGeneratorData(
                    getParcelableExtra(NewGeneratorContentsFragment.EXTRA_GENERATOR),
                    getParcelableExtra(NewGeneratorContentsFragment.EXTRA_TABLE_DATA)
                )
            }
            return
        }
    }

    private fun finalizeNewGenerator(context: Context, fragmentManager: FragmentManager, generator: Generator, generatorPath: String) {
        GeneratorPersister.export(context, generator, generatorPath)

        val displayMessage = if (generatorPath == "") {
            resources.getString(R.string.new_generator_successfully_created_message).format(generator.name, generatorPath)
        } else  {
            resources.getString(R.string.new_generator_successfully_created_at_subfolder_message).format(generator.name, generatorPath)
        }

        Toast.makeText(context, displayMessage, Toast.LENGTH_SHORT).show()
        fragmentManager.popBackStack()
    }

    companion object {
        const val BACK_STACK_GENERATOR_CREATION_FRAGMENT = "tabletop.velocic.com.worldforgerpgtools.GeneratorCreation.GeneratorCreationFragment"
        private const val REQUEST_NEW_CATEGORY_PATH = 0
        private const val REQUEST_NEW_GENERATOR_CONTENTS = 1

        fun newInstance() : GeneratorCreationFragment
        {
            return GeneratorCreationFragment()
        }
    }
}

class NewGeneratorPreviewAdapter(
    private val pendingGeneratorData: PendingNewGeneratorData,
    private val layoutInflater: LayoutInflater,
    private val editPendingGenerator: () -> Unit
) : RecyclerView.Adapter<NewGeneratorPreviewViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewGeneratorPreviewViewHolder {
        val view = layoutInflater.inflate(R.layout.list_item_preview_generator_contents, parent, false)

        return NewGeneratorPreviewViewHolder(view, pendingGeneratorData.tableData, editPendingGenerator)
    }

    override fun getItemCount(): Int = pendingGeneratorData.newGenerator.table.size

    override fun onBindViewHolder(holder: NewGeneratorPreviewViewHolder, position: Int) =
        holder.bind(pendingGeneratorData.newGenerator.table[position])

    override fun getItemViewType(position: Int): Int = R.layout.list_item_preview_generator_contents
}

class NewGeneratorPreviewViewHolder(
    rowView: View,
    private val tableData: ProbabilityTableKey,
    private val editPendingGenerator: () -> Unit
) : RecyclerView.ViewHolder(rowView)
{
    private val chanceOfResult = rowView.preview_generator_contents_percent_chance
    private val rollRange = rowView.preview_generator_contents_roll_range
    private val result = rowView.preview_generator_contents_result

    init {
        itemView.setOnClickListener { editPendingGenerator() }
    }

    fun bind(tableEntry: TableEntry) {
        val scaledProbability = ProbabilityTables.getProbability(
            tableEntry.diceRange, tableData
        ) * 100

        chanceOfResult.text = "${"%.2f".format(scaledProbability)}%"
        rollRange.text = tableEntry.diceRangeString
        result.text = tableEntry.name
    }
}

data class PendingNewGeneratorData(
    var newGenerator: Generator,
    var tableData: ProbabilityTableKey
)
