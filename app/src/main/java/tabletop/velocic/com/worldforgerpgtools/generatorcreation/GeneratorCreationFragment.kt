package tabletop.velocic.com.worldforgerpgtools.generatorcreation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_create_generator.*
import kotlinx.android.synthetic.main.fragment_create_generator.view.*
import kotlinx.android.synthetic.main.list_item_preview_generator_contents.view.*
import tabletop.velocic.com.worldforgerpgtools.R
import tabletop.velocic.com.worldforgerpgtools.appcommon.ProbabilityTableKey
import tabletop.velocic.com.worldforgerpgtools.appcommon.ProbabilityTables
import tabletop.velocic.com.worldforgerpgtools.persistence.Generator
import tabletop.velocic.com.worldforgerpgtools.persistence.GeneratorPersister
import tabletop.velocic.com.worldforgerpgtools.persistence.TableEntry

class GeneratorCreationFragment : androidx.fragment.app.Fragment()
{
    private var newGeneratorName = ""
    private var newGeneratorCategoryName = ""
    private var pendingNewGeneratorData: PendingNewGeneratorData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View =
        inflater.inflate(R.layout.fragment_create_generator, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.create_generator_button_submit_new_generator.setOnClickListener submitHandler@{
            val generator = pendingNewGeneratorData?.newGenerator ?: return@submitHandler
            val nullCheckedContext = context ?: return@submitHandler

            generator.name = newGeneratorName
            generator.assetPath = "${GeneratorPersister.GENERATOR_DATA_FOLDER}/$newGeneratorCategoryName"
            GeneratorPersister.export(nullCheckedContext, generator, newGeneratorCategoryName)
        }

        view.edit_text_create_generator_category.setOnClickListener(::onNewGeneratorCategoryNameClicked)

        edit_text_create_generator_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                newGeneratorName = s?.toString() ?: ""
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        initializeGeneratorTemplateClickEvents()
    }

    override fun onResume() {
        super.onResume()

        edit_text_create_generator_name.setText(newGeneratorName, TextView.BufferType.EDITABLE)

        arguments?.let {
            newGeneratorCategoryName = it.getString(GeneratorCategorySelectionFragment.EXTRA_SELECTED_CATEGORY) ?: ""
            edit_text_create_generator_category.text = newGeneratorCategoryName
        }

        pendingNewGeneratorData?.let { displayPendingGeneratorPreview(it) } ?: {
            create_generator_templates.visibility = View.VISIBLE
            create_generator_preview.visibility = View.GONE
            create_generator_button_submit_new_generator.visibility = View.GONE
        }()
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
                pendingNewGeneratorData = PendingNewGeneratorData(
                    getParcelableExtra(NewGeneratorContentsFragment.EXTRA_GENERATOR),
                    getParcelableExtra(NewGeneratorContentsFragment.EXTRA_TABLE_DATA)
                )
            }

            return
        }
    }

    private fun displayPendingGeneratorPreview(pendingGeneratorData: PendingNewGeneratorData) {
        val fragmentManager = activity?.supportFragmentManager ?: throw IllegalStateException("Failed to retrieve" +
                " a required FragmentManager instance.")
        val targetFragment = this
        create_generator_templates.visibility = View.GONE

        val editPendingGeneratorClickListener = editPendingGenerator@{
            val destination = NewGeneratorContentsFragment.newInstance(pendingGeneratorData)
            destination.setTargetFragment(targetFragment, REQUEST_NEW_GENERATOR_CONTENTS)

            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, destination)
                .addToBackStack(null)
                .commit()

            return@editPendingGenerator
        }

        create_generator_preview.apply {
            adapter = NewGeneratorPreviewAdapter(
                pendingGeneratorData,
                layoutInflater,
                editPendingGeneratorClickListener
            )
            layoutManager = LinearLayoutManager(activity)
            visibility = View.VISIBLE
        }

        create_generator_button_submit_new_generator.visibility = View.VISIBLE
    }

    private fun initializeGeneratorTemplateClickEvents() {
        val transitionToContentsFragment = { chosenTemplate : GeneratorTableTemplate ->
            val contentsFragment = NewGeneratorContentsFragment.newInstance(chosenTemplate)
            contentsFragment.setTargetFragment(this, REQUEST_NEW_GENERATOR_CONTENTS)

            activity?.supportFragmentManager?.beginTransaction()?.run {
                replace(R.id.fragment_container, contentsFragment)
                addToBackStack(null)
                commit()
            }
        }

        create_generator_template_1d4.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDFour) }
        create_generator_template_1d6.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDSix) }
        create_generator_template_1d8.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDEight) }
        create_generator_template_1d10.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDTen) }
        create_generator_template_1d12.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDTwelve) }
        create_generator_template_1d20.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDTwenty) }
        create_generator_template_2d6.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.TwoDSix) }
        create_generator_template_3d6.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.ThreeDSix) }
        create_generator_template_d100.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDOneHundred) }
    }

    private fun onNewGeneratorCategoryNameClicked(view: View) {
        val textView = view as TextView
        val generatorCategorySelectionFragment = GeneratorCategorySelectionFragment.newInstance(textView.text.toString())
        generatorCategorySelectionFragment.setTargetFragment(this, REQUEST_NEW_CATEGORY_PATH)

        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.fragment_container, generatorCategorySelectionFragment)
            addToBackStack(BACK_STACK_GENERATOR_CREATION_FRAGMENT)
            commit()
        }
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

private class NewGeneratorPreviewAdapter(
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

private class NewGeneratorPreviewViewHolder(
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
