package tabletop.velocic.com.worldforgerpgtools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorCategory
import kotlinx.android.synthetic.main.fragment_generators.*

class GeneratorSelectionFragment : android.support.v4.app.Fragment() {
    //TODO: relocate these as locals?
    private val currentCategory: GeneratorCategory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        val view = inflater.inflate(R.layout.fragment_generator_categories, container, false)

        val fragmentArgs = arguments
        val currentCategoryName = fragmentArgs?.getString(ARG_CATEGORY_PATH) ?: ""
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