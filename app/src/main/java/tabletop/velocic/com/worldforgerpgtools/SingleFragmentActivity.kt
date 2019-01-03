package tabletop.velocic.com.worldforgerpgtools

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity


abstract class SingleFragmentActivity : AppCompatActivity() {
    abstract fun createFragment() : Fragment

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