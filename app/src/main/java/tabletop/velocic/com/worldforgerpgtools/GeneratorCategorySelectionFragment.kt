package tabletop.velocic.com.worldforgerpgtools

import android.app.Activity
import android.app.FragmentManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorCategory
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorImporter

import kotlinx.android.synthetic.main.activity_fragment.*
import kotlinx.android.synthetic.main.fragment_generator_categories.*

class GeneratorCategorySelectionFragment : android.support.v4.app.Fragment() {
    private var currentCategoryName: String = ""
    private var currentCategory: GeneratorCategory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        val view = inflater.inflate(R.layout.fragment_generator_categories, container, false)

        val fragmentArgs = arguments
        currentCategoryName = fragmentArgs?.getString(ARG_CATEGORY_PATH) ?: ""

        val generatorImporter = GeneratorImporter.getInstance(view.context)
        val rootCategory = generatorImporter.rootGeneratorCategory
        currentCategory = rootCategory.getCategoryFromFullPath(currentCategoryName, rootCategory)

        button_select_category.setOnClickListener {
            onSelectButtonClicked()
        }

        textview_currently_selected_category.text = currentCategoryName

        //TODO: gridViewAdapter port, continue from there
    }

    private fun onSelectButtonClicked() {
        /*
        TODO:
            - open modal dialog asking for your "generator name"
            - pass it the string from this fragment containing the full generator path
            - return from that dialog to the "create new generator" fragment, closing
                the possibly many instances of this fragment between the modal and that screen
         */
        sendResult(Activity.RESULT_OK, textview_currently_selected_category.toString())
    }

    private fun sendResult(resultCode: Int, selectedCategoryPath: String) {
        if (targetFragment == null) {
            return
        }

        val intent = Intent().apply {
            putExtra(EXTRA_SELECTED_CATEGORY, selectedCategoryPath)
        }

        //TODO: find target fragment here, manually cast it to compile correctly
        //targetFragment.onActivityResult(targetRequestCode, resultCode, intent)

        activity?.supportFragmentManager?.popBackStack(
            GeneratorCreationFragment.BACK_STACK_GENERATOR_CREATION_FRAGMENT,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

    }

    companion object {
        val ARG_CATEGORY_PATH = "category_path"
        val EXTRA_SELECTED_CATEGORY = "tabletop.velocic.com.worldforgerpgtools.selected_category"

        fun newInstance(categoryPath: String) : GeneratorCategorySelectionFragment {
            val fragment = GeneratorCategorySelectionFragment()
            val args = Bundle().apply {
                putString(ARG_CATEGORY_PATH, categoryPath)
            }

            fragment.arguments = args

            return fragment
        }
    }
}