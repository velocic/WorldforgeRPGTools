package tabletop.velocic.com.worldforgerpgtools

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorCategory
import kotlinx.android.synthetic.main.fragment_generators.*
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.Generator
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorImporter

class GeneratorSelectionFragment : android.support.v4.app.Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        val view = inflater.inflate(R.layout.fragment_generator_categories, container, false)

        val fragmentArgs = arguments
        val currentCategoryName = fragmentArgs?.getString(ARG_CATEGORY_PATH) ?: ""

        val generatorImporter = GeneratorImporter.getInstance(view.context)
        val rootCategory = generatorImporter.rootGeneratorCategory

        //TODO: probably refactor getCategoryFromFull path to pass itself as base node
        val currentCategory = rootCategory.getCategoryFromFullPath(currentCategoryName, rootCategory)

        //TODO: set up recyclerview w/ gridlayoutmanager
    }

    companion object {
        const val ARG_CATEGORY_PATH = "category_path"
        const val DIALOG_NUM_GENERATOR_RESULTS = "DialogNumGeneratorResults"
        const val REQUEST_NUM_GENERATOR_RESULTS = 0

        @JvmStatic
        fun newInstance(categoryPath: String) : GeneratorSelectionFragment {
            val fragment = GeneratorSelectionFragment()

            val args = Bundle()
            args.putString(ARG_CATEGORY_PATH, categoryPath)
            fragment.arguments = args

            return fragment
        }
    }
}

private class GeneratorSelectionAdapter(
    private val context: Context,
    private val currentCategoryNode: GeneratorCategory?
) : RecyclerView.Adapter<GeneratorOrCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneratorOrCategoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_generators, parent, false)

        val clickHandler: (GeneratorOrCategoryViewHolder) -> Unit = { viewHolder ->
            if (viewHolder.category == null) {
                val generatedResultsFragment = GeneratorResultsFragment.newInstance(
                    currentCategoryNode?.getGeneratorFullPath(viewHolder.generator)
                )
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, generatedResultsFragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                val subCategoryFragment = GeneratorSelectionFragment.newInstance(viewHolder.category?.assetPath ?: "")
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, subCategoryFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        val onLongClickHandler: (GeneratorOrCategoryViewHolder) -> Unit = { viewHolder ->
            if (viewHolder.category == null) {
                val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                dialog = NumGeneratorResultsFragment.newInstance()
            }
        }

        return GeneratorOrCategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        //TODO: this differs from original java impl, but I think it's correct; verify in testing
        val numCategories = currentCategoryNode?.numChildCategories ?: 0
        val numGenerators = currentCategoryNode?.numGenerators ?: 0

        return numCategories + numGenerators
    }

    override fun onBindViewHolder(holder: GeneratorOrCategoryViewHolder, position: Int) {
        val currentCategoryNode = this.currentCategoryNode ?: return

        if (isCategoryIndex(position)) {
            holder.bind(currentCategoryNode.childCategories[position])

            return
        }

        holder.bind(currentCategoryNode.generators[position])
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.grid_item_generators_and_categories
    }

    private fun isCategoryIndex(index: Int) : Boolean {
        if (index < currentCategoryNode?.numChildCategories ?: Int.MIN_VALUE) {
            return true
        }

        return false
    }
}

private class GeneratorOrCategoryViewHolder(
    view: View,
    private val onClick: (GeneratorOrCategoryViewHolder) -> Unit,
    private val onLongClick: (GeneratorOrCategoryViewHolder) -> Unit
) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {
    private var generatorOrCategoryIcon: ImageView = view.findViewById(R.id.generators_and_categories_grid_item_icon)
    private var generatorOrCategoryText: TextView = view.findViewById(R.id.generators_and_categories_grid_item_text)
    var generator: Generator? = null
        private set

    var category: GeneratorCategory? = null
        private set

    override fun onClick(v: View?) {
        val viewHolder = v?.tag
        if (viewHolder != null) {
            onClick(viewHolder as GeneratorOrCategoryViewHolder)
        }
    }

    override fun onLongClick(v: View?): Boolean {
        val viewHolder = v?.tag
        if (viewHolder != null) {
            onLongClick(viewHolder as GeneratorOrCategoryViewHolder)
        }

        return true
    }

    fun bind(generator: Generator) {
        this.generator = generator
        this.category = null

        setViewFromGeneratorOrCategory()
    }

    fun bind(category: GeneratorCategory) {
        this.category = category
        generator = null

        setViewFromGeneratorOrCategory()
    }

    private fun setViewFromGeneratorOrCategory() {
        if (generator != null) {
            generatorOrCategoryIcon.setImageResource(R.drawable.ic_generate_results)
            generatorOrCategoryText.text = generator?.name
            return
        }

        generatorOrCategoryIcon.setImageResource(R.drawable.ic_select_generator_category)
        generatorOrCategoryText.text = category?.name
    }
}