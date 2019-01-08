package tabletop.velocic.com.worldforgerpgtools

import android.app.Activity
import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorCategory
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorImporter

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
        //generator_selection.adapter = GeneratorCategorySelectionAdapter(currentCategory)

        return view
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
        const val ARG_CATEGORY_PATH = "category_path"
        const val EXTRA_SELECTED_CATEGORY = "tabletop.velocic.com.worldforgerpgtools.selected_category"

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

private class GeneratorCategorySelectionAdapter(
        private val currentCategoryNode: GeneratorCategory,
        private val context: Context
) : RecyclerView.Adapter<GeneratorCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneratorCategoryViewHolder {
        val newView = LayoutInflater.from(context).run {
            inflate(R.layout.grid_item_generators_and_categories, parent, false)
        }

        return GeneratorCategoryViewHolder(newView)
    }

    override fun getItemCount(): Int {
        return currentCategoryNode.numChildCategories
    }

    override fun onBindViewHolder(holder: GeneratorCategoryViewHolder, position: Int) {
        holder.bind(currentCategoryNode.childCategories[position])
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.grid_item_generators_and_categories
    }
}

private class GeneratorCategoryViewHolder(
        private val context: Context,
        view: View,
        private var category: GeneratorCategory,
        private val targetFragment: Fragment

) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private val categoryIcon = view.findViewById<ImageView>(R.id.generators_and_categories_grid_item_icon)
    private val categoryText = view.findViewById<TextView>(R.id.generators_and_categories_grid_item_text)

    fun bind(category: GeneratorCategory) {
        this.category = category
        categoryIcon.setImageResource(R.drawable.ic_select_generator_category)
        categoryText.text = category.name
    }

    override fun onClick(v: View) {
        val subCategoryFragment = GeneratorCategorySelectionFragment.newInstance(category.assetPath)
        subCategoryFragment.setTargetFragment(targetFragment, targetRequestCode)

//        context?.supportFragmentManager?.beginTransaction()
//                ?.replace(R.id.fragment_container, subCategoryFragment)
//                ?.addToBackStack(null)
//                ?.commit()
    }
}

