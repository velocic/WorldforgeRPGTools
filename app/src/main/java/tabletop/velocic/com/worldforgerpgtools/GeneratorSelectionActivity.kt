package tabletop.velocic.com.worldforgerpgtools

import androidx.fragment.app.Fragment

class GeneratorSelectionActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return GeneratorSelectionFragment()
    }
}