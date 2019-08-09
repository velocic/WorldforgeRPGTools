package tabletop.velocic.com.worldforgerpgtools

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_generator_results.*
import kotlinx.android.synthetic.main.list_item_generator_results.view.*
import tabletop.velocic.com.worldforgerpgtools.persistence.GeneratorPersister
import tabletop.velocic.com.worldforgerpgtools.persistence.ResultItem
import tabletop.velocic.com.worldforgerpgtools.persistence.ResultRoller

class GeneratorResultsFragment : androidx.fragment.app.Fragment()
{
    private var resultItems = listOf<ResultItem>()
    private var generatorPath = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_generator_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generatorPath = arguments?.getString(ARG_GENERATOR_PATH) ?: ""
        val numberOfResultsOverride = arguments?.getInt(ARG_NUMBER_OF_RESULTS_OVERRIDE) ?: 0

        val rootCategory = GeneratorPersister.rootGeneratorCategory ?:
            throw IllegalStateException("Failed to retrieve the root generator category. Has" +
                " GeneratorPersister been properly initialized?")

        val generator = rootCategory.getGeneratorFromFullPath(generatorPath, rootCategory) ?:
            throw IllegalStateException("A valid Generator is required to build a result set." +
                "Is $generatorPath the correct target?")

        val activityInstance = activity ?: throw IllegalStateException("GeneratorResultsFragment cannot" +
            " exist without a valid FragmentActivity instance.")

        val actualNumberOfResults = if (numberOfResultsOverride == 0) {
            generator.defaultNumResultRolls
        } else{
            numberOfResultsOverride
        }

        val previousResults = arguments?.getParcelableArray(INSTANCE_STATE_GENERATED_RESULT_SET)?.map { untypedParcelable ->
            untypedParcelable as ResultItem
        }

        val resultSet = previousResults ?: ResultRoller(rootCategory).generateResultSet(generatorPath, actualNumberOfResults)

        generated_results_table_name.text = rootCategory.getGeneratorFromFullPath(generatorPath, rootCategory)?.name
        generated_item_list.layoutManager = LinearLayoutManager(activityInstance)
        generated_item_list.adapter = GeneratorResultsAdapter(activityInstance, resultSet) {
            saveState()
        }

        resultItems = resultSet
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        saveState()
    }

    private fun saveState()
    {
        val generatedResults = resultItems.toTypedArray()
        arguments?.apply {
            putString(ARG_GENERATOR_PATH, generatorPath)
            putParcelableArray(INSTANCE_STATE_GENERATED_RESULT_SET, generatedResults)
        }
    }

    companion object{
        const val ARG_GENERATOR_PATH = "generator_path"
        const val ARG_NUMBER_OF_RESULTS_OVERRIDE = "number_of_results_override"
        const val INSTANCE_STATE_GENERATED_RESULT_SET = "generated_result_set"

        fun newInstance(generatorPath: String, numberOfResultsOverride: Int) : GeneratorResultsFragment {
            val fragment = GeneratorResultsFragment()

            fragment.arguments = Bundle().apply {
                putString(ARG_GENERATOR_PATH, generatorPath)
                putInt(ARG_NUMBER_OF_RESULTS_OVERRIDE, numberOfResultsOverride)
            }

            return fragment
        }
    }
}

private class GeneratorResultsAdapter(
        private val activity: FragmentActivity,
        private val results: List<ResultItem>,
        private val onPreScreenTransition: () -> Unit
) : RecyclerView.Adapter<GeneratorResultsViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneratorResultsViewHolder {
        val view = LayoutInflater.from(activity).inflate(
            R.layout.list_item_generator_results,
            parent,
            false
        )

        return GeneratorResultsViewHolder(view, activity.supportFragmentManager, onPreScreenTransition)
    }

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: GeneratorResultsViewHolder, position: Int) {
        holder.result = results[position]
    }

    override fun getItemViewType(position: Int): Int = R.layout.list_item_generator_results
}

private class GeneratorResultsViewHolder(
        view: View,
        private val fragmentManager: FragmentManager,
        private val onPreScreenTransition: () -> Unit
): RecyclerView.ViewHolder(view), View.OnClickListener
{
    private val resultQuantity = view.edit_text_generator_result_quantity
    private val resultName = view.edit_text_generator_result_name
    var result: ResultItem? = null
        set(value) {
            field = value
            resultQuantity.text = result?.quantity.toString()
            resultName.text = result?.name
        }

    init {
        view.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        onPreScreenTransition()

        result?.let { result ->
            val detailsFragment = GeneratorResultDetailsFragment.newInstance(result)
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailsFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}
