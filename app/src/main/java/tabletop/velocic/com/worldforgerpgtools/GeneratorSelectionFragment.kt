package tabletop.velocic.com.worldforgerpgtools

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_generators.view.*
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorCategory
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.Generator
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorImporter

class GeneratorSelectionFragment : android.support.v4.app.Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        val view = inflater.inflate(R.layout.fragment_generators, container, false)

        val fragmentArgs = arguments
        val currentCategoryName = fragmentArgs?.getString(ARG_CATEGORY_PATH) ?: ""

        GeneratorImporter.import(context)

        val rootCategory = GeneratorImporter.rootGeneratorCategory

        //TODO: probably refactor getCategoryFromFull path to pass itself as base node
        val currentCategory = rootCategory?.getCategoryFromFullPath(currentCategoryName, rootCategory)

        //TODO: Setting a hard-coded column size for now. This should be dynamic based on available space
        //Setting the item width to some fixed value may also be a solution
        view.generator_selection.setHasFixedSize(true)
        view.generator_selection.layoutManager = GridLayoutManager(context, 2)
        view.generator_selection.adapter = GeneratorSelectionAdapter(context, currentCategory, this)

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_NUM_GENERATOR_RESULTS) {
            val numResultsToGenerate = data.getIntExtra(NumGeneratorResultsFragment.EXTRA_NUM_GENERATOR_RESULTS, 1)
            val generatorPath = data.getStringExtra(NumGeneratorResultsFragment.EXTRA_GENERATOR_PATH)
            val generatorFragment = GeneratorResultsFragment.newInstance(generatorPath, numResultsToGenerate)
            activity?.supportFragmentManager?.beginTransaction()?.run {
                replace(R.id.fragment_container, generatorFragment)
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.generator_selection_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.create_new_generator -> {
            val generatorCreationFragment = GeneratorCreationFragment.newInstance();
            activity?.supportFragmentManager?.beginTransaction()?.run {
                replace(R.id.fragment_container, generatorCreationFragment)
                addToBackStack(null)
                commit()
            }

            true
        }
        else -> super.onOptionsItemSelected(item)
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
    private val context: Context?,
    private val currentCategoryNode: GeneratorCategory?,
    private val targetFragment: GeneratorSelectionFragment
) : RecyclerView.Adapter<GeneratorOrCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneratorOrCategoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.grid_item_generators_and_categories, parent, false)

        if (context == null) {
            return GeneratorOrCategoryViewHolder(view, {}, {})
        }

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

        val longClickHandler: (GeneratorOrCategoryViewHolder) -> Unit = { viewHolder ->
            if (viewHolder.category == null) {
                val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                val dialog = NumGeneratorResultsFragment.newInstance(currentCategoryNode?.getGeneratorFullPath(viewHolder.generator))
                dialog.setTargetFragment(targetFragment, GeneratorSelectionFragment.REQUEST_NUM_GENERATOR_RESULTS)
                dialog.show(fragmentManager, GeneratorSelectionFragment.DIALOG_NUM_GENERATOR_RESULTS)
            }
        }

        return GeneratorOrCategoryViewHolder(view, clickHandler, longClickHandler)
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

    init {
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

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