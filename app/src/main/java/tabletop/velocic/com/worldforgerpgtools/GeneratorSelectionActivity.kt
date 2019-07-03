package tabletop.velocic.com.worldforgerpgtools

import androidx.fragment.app.Fragment

class GeneratorSelectionActivity : SingleFragmentActivity() {
    override fun createFragment(): androidx.fragment.app.Fragment {
        return GeneratorSelectionFragment()
    }
}