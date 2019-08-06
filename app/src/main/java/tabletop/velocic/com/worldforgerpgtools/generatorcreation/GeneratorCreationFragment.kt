package tabletop.velocic.com.worldforgerpgtools.generatorcreation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_create_generator.*
import kotlinx.android.synthetic.main.fragment_create_generator.view.*
import tabletop.velocic.com.worldforgerpgtools.R
import tabletop.velocic.com.worldforgerpgtools.generatordeserializer.Generator

class GeneratorCreationFragment : androidx.fragment.app.Fragment()
{
    private lateinit var newGeneratorCategoryName: TextView
    private var newGenerator: Generator? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View =
        inflater.inflate(R.layout.fragment_create_generator, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newGeneratorCategoryName = view.edit_text_create_generator_category

        val newGeneratorName = view.edit_text_create_generator_name
        val submitGeneratorButton = view.create_generator_button_submit_new_generator

        newGeneratorCategoryName.setOnClickListener(::onNewGeneratorCategoryNameClicked)

        initializeGeneratorTemplateClickEvents()
    }

    override fun onResume() {
        super.onResume()

        arguments?.let {
            newGeneratorCategoryName.text = it.getString(GeneratorCategorySelectionFragment.EXTRA_SELECTED_CATEGORY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_NEW_CATEGORY_PATH) {
            //Can't set the TextView directly, as this function is called before
            //onResume, and the TextView state gets lost
            arguments = data?.extras
            return
        }

        if (requestCode == REQUEST_NEW_GENERATOR_CONTENTS) {
            newGenerator = data?.getParcelableExtra(NewGeneratorContentsFragment.EXTRA_GENERATOR)
            return
        }
    }

    private fun initializeGeneratorTemplateClickEvents() {
        val transitionToContentsFragment = { chosenTemplate : GeneratorTableTemplate ->
            val contentsFragment = NewGeneratorContentsFragment.newInstance(chosenTemplate)
            contentsFragment.setTargetFragment(this, REQUEST_NEW_GENERATOR_CONTENTS)

            activity?.supportFragmentManager?.beginTransaction()?.run {
                replace(R.id.fragment_container, contentsFragment)
                addToBackStack(null)
                commit()
            }
        }

        create_generator_template_1d4.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDFour) }
        create_generator_template_1d6.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDSix) }
        create_generator_template_1d8.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDEight) }
        create_generator_template_1d10.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDTen) }
        create_generator_template_1d12.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDTwelve) }
        create_generator_template_1d20.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDTwenty) }
        create_generator_template_2d6.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.TwoDSix) }
        create_generator_template_3d6.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.ThreeDSix) }
        create_generator_template_d100.setOnClickListener { transitionToContentsFragment(GeneratorTableTemplate.OneDOneHundred) }
    }

    private fun onNewGeneratorCategoryNameClicked(view: View) {
        val textView = view as TextView
        val generatorCategorySelectionFragment = GeneratorCategorySelectionFragment.newInstance(textView.text.toString())
        generatorCategorySelectionFragment.setTargetFragment(this, REQUEST_NEW_CATEGORY_PATH)

        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.fragment_container, generatorCategorySelectionFragment)
            addToBackStack(BACK_STACK_GENERATOR_CREATION_FRAGMENT)
            commit()
        }
    }

    companion object {
        const val BACK_STACK_GENERATOR_CREATION_FRAGMENT = "tabletop.velocic.com.worldforgerpgtools.GeneratorCreation.GeneratorCreationFragment"
        private const val REQUEST_NEW_CATEGORY_PATH = 0
        private const val REQUEST_NEW_GENERATOR_CONTENTS = 1

        fun newInstance() : GeneratorCreationFragment
        {
            return GeneratorCreationFragment()
        }
    }
}
