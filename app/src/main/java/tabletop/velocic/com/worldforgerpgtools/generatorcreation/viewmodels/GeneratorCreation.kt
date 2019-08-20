package tabletop.velocic.com.worldforgerpgtools.generatorcreation.viewmodels

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_create_generator.view.*
import tabletop.velocic.com.worldforgerpgtools.R
import tabletop.velocic.com.worldforgerpgtools.generatorcreation.*

class GeneratorCreationViewModel : ViewModel()
{
    val generatorName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val categoryName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val pendingGeneratorData: MutableLiveData<PendingNewGeneratorData?> by lazy { MutableLiveData<PendingNewGeneratorData?>() }
}

class GeneratorCreationInputEvents(
    generatorNameField: EditText,
    categoryNameField: TextView,
    private val viewModel: GeneratorCreationViewModel,
    fragmentManager: FragmentManager,
    parentFragment: GeneratorCreationFragment,
    newCategoryPathRequestCode: Int
)
{
    init {
        categoryNameField.setOnClickListener {
            onNewGeneratorCategoryNameClicked(
                it as TextView,
                fragmentManager,
                parentFragment,
                newCategoryPathRequestCode
            )
        }

        generatorNameField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.generatorName.value = s?.toString() ?: ""
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun onNewGeneratorCategoryNameClicked(
        categoryNameField: TextView,
        fragmentManager: FragmentManager,
        parentFragment: GeneratorCreationFragment,
        newCategoryPathRequestCode: Int
    ) {
        val generatorCategorySelectionFragment = GeneratorCategorySelectionFragment.newInstance(categoryNameField.text.toString())
        generatorCategorySelectionFragment.setTargetFragment(parentFragment, newCategoryPathRequestCode)

        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, generatorCategorySelectionFragment)
            addToBackStack(GeneratorCreationFragment.BACK_STACK_GENERATOR_CREATION_FRAGMENT)
            commit()
        }
    }
}

class GeneratorCreationPreviewManager(
        private val templates: ViewGroup,
        private val preview: RecyclerView,
        private val submit: Button,
        private val fragmentManager: FragmentManager,
        private val parentFragment: GeneratorCreationFragment,
        private val inflater: LayoutInflater,
        private val layoutManager: RecyclerView.LayoutManager,
        private val newGeneratorContentsRequestCode: Int
)
{
    init {
        initializeGeneratorTemplateClickEvents()
    }

    fun displayPendingGeneratorPreview(pendingGeneratorData: PendingNewGeneratorData) {
        templates.visibility = View.GONE

        val editPendingGeneratorClickListener = editPendingGenerator@{
            val destination = NewGeneratorContentsFragment.newInstance(pendingGeneratorData)
            destination.setTargetFragment(parentFragment, newGeneratorContentsRequestCode)

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, destination)
                    .addToBackStack(null)
                    .commit()

            return@editPendingGenerator
        }

        preview.let {
            it.adapter = NewGeneratorPreviewAdapter(
                    pendingGeneratorData,
                    inflater,
                    editPendingGeneratorClickListener
            )
            it.layoutManager = layoutManager
            it.visibility = View.VISIBLE
        }

        submit.visibility = View.VISIBLE
    }

    fun displayGeneratorTemplates() {
        templates.visibility = View.VISIBLE
        preview.visibility = View.GONE
        submit.visibility = View.GONE
    }

    private fun initializeGeneratorTemplateClickEvents() {
        val transitionToContentsFragment = { chosenTemplate : GeneratorTableTemplate ->
            val contentsFragment = NewGeneratorContentsFragment.newInstance(chosenTemplate)
            contentsFragment.setTargetFragment(parentFragment, newGeneratorContentsRequestCode)

            fragmentManager.beginTransaction().run {
                replace(R.id.fragment_container, contentsFragment)
                addToBackStack(null)
                commit()
            }
        }

        templates.create_generator_template_1d4.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDFour) }
        templates.create_generator_template_1d6.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDSix) }
        templates.create_generator_template_1d8.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDEight) }
        templates.create_generator_template_1d10.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDTen) }
        templates.create_generator_template_1d12.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDTwelve) }
        templates.create_generator_template_1d20.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDTwenty) }
        templates.create_generator_template_2d6.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.TwoDSix) }
        templates.create_generator_template_3d6.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.ThreeDSix) }
        templates.create_generator_template_d100.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDOneHundred) }
    }
}
