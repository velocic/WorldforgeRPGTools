package tabletop.velocic.com.worldforgerpgtools

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_num_generator_results.*

class NumGeneratorResultsFragment : android.support.v4.app.DialogFragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dialog_num_generator_results, container, false)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        picker_num_generator_results.minValue = 1
        picker_num_generator_results.maxValue = 999

        val generatorPath = arguments?.getString(ARG_GENERATOR_PATH)
            ?: throw IllegalStateException("NumGeneratorResultsFragment requires a target Generator" +
                " path to invoke after a result has been selected, but none was provided.")

        val sendResult = { resultCode: Int, numResultsToGenerate: Int ->
            val intent = Intent().apply {
                putExtra(EXTRA_NUM_GENERATOR_RESULTS, numResultsToGenerate)
                putExtra(EXTRA_GENERATOR_PATH, generatorPath)
            }

            targetFragment?.onActivityResult(targetRequestCode, resultCode, intent)
                ?: throw IllegalStateException("Attempted to send NumGeneratorResultsFragment dialog" +
                    " result to the calling Fragment, however that fragment was somehow null.")
        }

        return AlertDialog.Builder(activity)
            .setView(view)
            .setTitle(R.string.num_generator_results_dialog_title)
            .setPositiveButton(android.R.string.ok) {_, _ -> sendResult(Activity.RESULT_OK, picker_num_generator_results.value)}
            .setNegativeButton(android.R.string.cancel) {_, _ -> sendResult(Activity.RESULT_CANCELED, 0)}
            .create()
    }

    companion object {
        const val ARG_GENERATOR_PATH = "generator_path"
        const val EXTRA_NUM_GENERATOR_RESULTS = "tabletop.velocic.com.worldforgerpgtools.num_generator_results"
        const val EXTRA_GENERATOR_PATH = "tabletop.velocic.com.worldforgerpgtools.generator_path"

        fun newInstance(generatorPath: String) : NumGeneratorResultsFragment
        {
            val fragment = NumGeneratorResultsFragment()

            fragment.arguments = Bundle().apply {
                putString(ARG_GENERATOR_PATH, generatorPath)
            }

            return fragment
        }
    }
}