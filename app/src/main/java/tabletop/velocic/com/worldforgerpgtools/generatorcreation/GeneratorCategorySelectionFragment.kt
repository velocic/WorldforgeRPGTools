package tabletop.velocic.com.worldforgerpgtools.generatorcreation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tabletop.velocic.com.worldforgerpgtools.persistence.GeneratorCategory
import tabletop.velocic.com.worldforgerpgtools.persistence.GeneratorPersister

import kotlinx.android.synthetic.main.fragment_generator_categories.view.*
import tabletop.velocic.com.worldforgerpgtools.R

class GeneratorCategorySelectionFragment : androidx.fragment.app.Fragment() {
    private var currentCategoryName: String = ""
    private var currentCategory: GeneratorCategory? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        val view = inflater.inflate(R.layout.fragment_generator_categories, container, false)

        if (context == null || targetFragment == null) {
            return view
        }

        val fragmentArgs = arguments
        currentCategoryName = fragmentArgs?.getString(ARG_CATEGORY_PATH) ?: ""

        val rootCategory = GeneratorPersister.rootGeneratorCategory
        currentCategory = rootCategory?.getCategoryFromFullPath(currentCategoryName, rootCategory)

        view.button_select_category.setOnClickListener {
            onSelectButtonClicked()
        }

        view.textview_currently_selected_category.text = currentCategoryName

        view.generator_selection.setHasFixedSize(true)
        view.generator_selection.layoutManager = GridLayoutManager(activity, 2)
        view.generator_selection.adapter = GeneratorCategorySelectionAdapter(
                currentCategory,
                context as Context,
                targetFragment as GeneratorCreationFragment,
                targetRequestCode
        )

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
        sendResult(Activity.RESULT_OK, view?.textview_currently_selected_category?.text.toString())
    }

    private fun sendResult(resultCode: Int, selectedCategoryPath: String) {
        if (targetFragment == null) {
            return
        }

        val intent = Intent().apply {
            putExtra(EXTRA_SELECTED_CATEGORY, selectedCategoryPath)
        }

        (targetFragment as GeneratorCreationFragment).onActivityResult(targetRequestCode, resultCode, intent)

        activity?.supportFragmentManager?.popBackStack(
            GeneratorCreationFragment.BACK_STACK_GENERATOR_CREATION_FRAGMENT,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }


    companion object {
        const val ARG_CATEGORY_PATH = "category_path"
        const val EXTRA_SELECTED_CATEGORY = "tabletop.velocic.com.worldforgerpgtools.selected_category"

        @JvmStatic
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
        private val currentCategoryNode: GeneratorCategory?,
        private val context: Context,
        private val targetFragment: Fragment,
        private val targetRequestCode: Int
) : RecyclerView.Adapter<GeneratorCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneratorCategoryViewHolder {
        val newView = LayoutInflater.from(context).run {
            inflate(R.layout.grid_item_generators_and_categories, parent, false)
        }

        return GeneratorCategoryViewHolder(context, newView, currentCategoryNode, targetFragment, targetRequestCode)
    }

    override fun getItemCount(): Int {
        return currentCategoryNode?.numChildCategories ?: 0
    }

    override fun onBindViewHolder(holder: GeneratorCategoryViewHolder, position: Int) {
        holder.bind(currentCategoryNode?.childCategories?.get(position))
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.grid_item_generators_and_categories
    }
}

private class GeneratorCategoryViewHolder(
        private val context: Context,
        view: View,
        private var category: GeneratorCategory?,
        private val targetFragment: Fragment,
        private val targetRequestCode: Int

) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private val categoryIcon = view.findViewById<ImageView>(R.id.generators_and_categories_grid_item_icon)
    private val categoryText = view.findViewById<TextView>(R.id.generators_and_categories_grid_item_text)

    init {
        itemView.setOnClickListener(::onClick)
    }

    fun bind(category: GeneratorCategory?) {
        this.category = category
        categoryIcon.setImageResource(R.drawable.ic_select_generator_category)
        categoryText.text = category?.name
    }

    override fun onClick(v: View) {
        val subCategoryFragment = GeneratorCategorySelectionFragment.newInstance(category?.assetPath
                ?: "")
        subCategoryFragment.setTargetFragment(targetFragment, targetRequestCode)

        (context as FragmentActivity).supportFragmentManager?.beginTransaction()?.run {
            replace(R.id.fragment_container, subCategoryFragment)
            addToBackStack(null)
            commit()
        }
    }
}

