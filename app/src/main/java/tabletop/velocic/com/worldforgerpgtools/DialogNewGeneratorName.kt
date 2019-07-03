package tabletop.velocic.com.worldforgerpgtools

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.dialog_new_generator_name.view.*

class DialogNewGeneratorName : androidx.fragment.app.DialogFragment()
{
    override fun onCreateDialog(savedInstanceState: Bundle?) : Dialog
    {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_new_generator_name, null)
        val newGeneratorName = view.new_generator_name

        return AlertDialog.Builder(activity).run {
            setView(view)
            setTitle(R.string.title_new_generator_name)
            setPositiveButton(android.R.string.ok) { _, _ ->
                sendResult(Activity.RESULT_OK, newGeneratorName.text.toString())
            }
            setNegativeButton(android.R.string.cancel) { _, _ ->
                sendResult(Activity.RESULT_CANCELED, "")
            }
            setMessage(R.string.message_new_generator_name)
            create()
        }
    }

    private fun sendResult(resultCode: Int, newGeneratorName: String) {
        val target = targetFragment ?: return

        val intent = Intent()
        intent.putExtra(EXTRA_NEW_GENERATOR_NAME, newGeneratorName)

        target.onActivityResult(targetRequestCode, resultCode, intent)
    }

    companion object {
        const val EXTRA_NEW_GENERATOR_NAME = "tabletop.velocic.com.worldforgerpgtools.DialogNewGeneratorName.newGeneratorName"
    }
}