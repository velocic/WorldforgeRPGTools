package tabletop.velocic.com.worldforgerpgtools

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.dialog_num_generator_results.view.*

class NumGeneratorResultsDialog : androidx.fragment.app.DialogFragment()
{
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_num_generator_results, null)

        dialogView.picker_num_generator_results.minValue = 1
        dialogView.picker_num_generator_results.maxValue = 999

        val generatorPath = arguments?.getString(ARG_GENERATOR_PATH)
            ?: throw IllegalStateException("NumGeneratorResultsDialog requires a target Generator" +
                " path to invoke after a result has been selected, but none was provided.")

        val sendResult = { resultCode: Int, numResultsToGenerate: Int ->
            val intent = Intent().apply {
                putExtra(EXTRA_NUM_GENERATOR_RESULTS, numResultsToGenerate)
                putExtra(EXTRA_GENERATOR_PATH, generatorPath)
            }

            targetFragment?.onActivityResult(targetRequestCode, resultCode, intent)
                ?: throw IllegalStateException("Attempted to send NumGeneratorResultsDialog dialog" +
                    " result to the calling Fragment, however that fragment was somehow null.")
        }

        return AlertDialog.Builder(activity)
            .setView(dialogView)
            .setTitle(R.string.num_generator_results_dialog_title)
            .setPositiveButton(android.R.string.ok) {_, _ -> sendResult(Activity.RESULT_OK, dialogView.picker_num_generator_results.value)}
            .setNegativeButton(android.R.string.cancel) {_, _ -> sendResult(Activity.RESULT_CANCELED, 0)}
            .create()
    }

    companion object {
        private const val ARG_GENERATOR_PATH = "generator_path"
        const val EXTRA_NUM_GENERATOR_RESULTS = "tabletop.velocic.com.worldforgerpgtools.num_generator_results"
        const val EXTRA_GENERATOR_PATH = "tabletop.velocic.com.worldforgerpgtools.generator_path"

        fun newInstance(generatorPath: String) : NumGeneratorResultsDialog
        {
            val fragment = NumGeneratorResultsDialog()

            fragment.arguments = Bundle().apply {
                putString(ARG_GENERATOR_PATH, generatorPath)
            }

            return fragment
        }
    }
}