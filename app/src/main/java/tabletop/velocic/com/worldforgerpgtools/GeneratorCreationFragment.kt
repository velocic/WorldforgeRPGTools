package tabletop.velocic.com.worldforgerpgtools

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_fragment.view.*
import kotlinx.android.synthetic.main.fragment_create_generator.view.*

class GeneratorCreationFragment : android.support.v4.app.Fragment()
{
    private var newGeneratorCategoryName: TextView = TextView(context)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View
    {
        val view = inflater.inflate(R.layout.fragment_create_generator, container, false)

        newGeneratorCategoryName = view.edit_text_create_generator_category

        //TODO: Finish behavior for this screen after kotlin rewrite is complete
        val newGeneratorName = view.edit_text_create_generator_name
        val createNewResultEntryButton = view.button_add_generator_possible_result
        val submitGeneratorButton = view.button_submit_new_generator

        newGeneratorCategoryName.setOnClickListener(::onNewGeneratorCategoryNameClicked)

        return view
    }

    override fun onResume()
    {
        super.onResume()

        arguments?.let {
            newGeneratorCategoryName.text = it.getString(GeneratorCategorySelectionFragment.EXTRA_SELECTED_CATEGORY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_NEW_CATEGORY_PATH) {
            //Can't set the TextView directly, as this function is called before
            //onResume, and the TextView state gets lost
            arguments = data?.extras
        }
    }

    private fun onNewGeneratorCategoryNameClicked(v: View)
    {
        val textView = v as TextView
        val generatorCategorySelectionFragment = GeneratorCategorySelectionFragment.newInstance(textView.text.toString())

        activity?.supportFragmentManager?.beginTransaction()?.run {
            replace(R.id.fragment_container, generatorCategorySelectionFragment)
            addToBackStack(BACK_STACK_GENERATOR_CREATION_FRAGMENT)
            commit()
        }
    }

    companion object {
        const val BACK_STACK_GENERATOR_CREATION_FRAGMENT = "tabletop.velocic.com.worldforgerpgtools.GeneratorCreationFragment"
        private const val REQUEST_NEW_CATEGORY_PATH = 0

        fun newInstance() : GeneratorCreationFragment
        {
            return GeneratorCreationFragment()
        }
    }
}