package tabletop.velocic.com.worldforgerpgtools

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_generator_results.*
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorImporter
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.ResultItem
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.ResultRoller

class GeneratorResultsFragment : android.support.v4.app.Fragment()
{
    val resultItems = mutableListOf<ResultItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_generator_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val generatorPath = arguments?.getString(ARG_GENERATOR_PATH) ?: ""
        val numberOfResultsOverride = arguments?.getInt(ARG_NUMBER_OF_RESULTS_OVERRIDE) ?: 0

        val rootCategory = GeneratorImporter.rootGeneratorCategory ?:
            throw java.lang.IllegalStateException("Failed to retrieve the root generator category. Has" +
                " GeneratorImporter been properly initialized?")

        val generator = rootCategory.getGeneratorFromFullPath(generatorPath, rootCategory) ?:
            throw IllegalStateException("A valid Generator is required to build a result set." +
                "Is $generatorPath the correct target?")

        val actualNumberOfResults = if (numberOfResultsOverride == 0) {
            generator.defaultNumResultRolls
        } else{
            numberOfResultsOverride
        }

        val previousResults = arguments?.getParcelableArray(INSTANCE_STATE_GENERATED_RESULT_SET)

        val resultSet = previousResults?.let {
            ResultRoller(rootCategory).generateResultSet(generatorPath, actualNumberOfResults)
        } ?: previousResults

        generated_results_table_name.text = rootCategory.getGeneratorFromFullPath(generatorPath, rootCategory)?.name
        generated_item_list.layoutManager = LinearLayoutManager(activity)
        generated_item_list.adapter = GeneratorResultsAdapter(resultSet)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
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