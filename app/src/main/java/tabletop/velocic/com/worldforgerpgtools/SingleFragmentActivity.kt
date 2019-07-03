package tabletop.velocic.com.worldforgerpgtools

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity


abstract class SingleFragmentActivity : AppCompatActivity() {
    abstract fun createFragment() : androidx.fragment.app.Fragment

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        supportFragmentManager?.findFragmentById(R.id.fragment_container) ?:
            createFragment().run {
                supportFragmentManager?.beginTransaction()
                    ?.add(R.id.fragment_container, this)
                    ?.commit()
            }
    }
}