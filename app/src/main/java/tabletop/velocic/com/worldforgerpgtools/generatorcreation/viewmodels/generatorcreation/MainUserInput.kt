package tabletop.velocic.com.worldforgerpgtools.generatorcreation.viewmodels.generatorcreation

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import tabletop.velocic.com.worldforgerpgtools.R
import tabletop.velocic.com.worldforgerpgtools.generatorcreation.GeneratorCategorySelectionFragment
import tabletop.velocic.com.worldforgerpgtools.generatorcreation.GeneratorCreationFragment
import tabletop.velocic.com.worldforgerpgtools.generatorcreation.PendingNewGeneratorData

class MainUserInput(
    generatorNameField: EditText,
    categoryNameField: TextView,
    fragmentManager: FragmentManager,
    parentFragment: GeneratorCreationFragment,
    newCategoryPathRequestCode: Int
)
{
    var generatorName = ""
    var categoryName = ""

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
                generatorName = s?.toString() ?: ""
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