package tabletop.velocic.com.worldforgerpgtools

import android.support.v4.app.Fragment

class GeneratorSelectionActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return GeneratorSelectionFragment()
    }
}