package tabletop.velocic.com.worldforgerpgtools.GeneratorCreation.ViewModels.NewGeneratorContents

import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.partial_generator_contents_main_body.view.*
import tabletop.velocic.com.worldforgerpgtools.AppCommon.ProbabilityTableKey
import tabletop.velocic.com.worldforgerpgtools.AppCommon.ProbabilityTables
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.TableEntries
import java.lang.IllegalArgumentException
import kotlin.math.absoluteValue

class MainUserInput(
    parent: ViewGroup
)
{
    private var boundTableEntry: TableEntries? = null
    private val percentChance = parent.generator_contents_percent_chance as TextView
    private val result = parent.generator_contents_result as EditText
    private val rollRange = parent.generator_contents_roll_range as TextView
    private var isInitialized = false

    fun bind(tableEntry: TableEntries) {
        if (!isInitialized ) {
            lateInitializeAtFirstBinding()
        }

        boundTableEntry = tableEntry

        rollRange.text = boundTableEntry?.diceRangeString
        result.setText(boundTableEntry?.name, TextView.BufferType.EDITABLE)
    }

    fun updateResultChance(numDie: Int = 1, dieSize: Int) {
        val checkedBoundTableEntry = boundTableEntry ?: return

        val scaledProbability = ProbabilityTables.getProbability(
            checkedBoundTableEntry.diceRange, ProbabilityTableKey(numDie, dieSize)
        ) * 100

        val fieldContent = "${"%.2f".format(scaledProbability)}%"
        percentChance.text = fieldContent
    }

    private fun lateInitializeAtFirstBinding() {
        result.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                boundTableEntry?.name = s?.toString()
                    ?: throw IllegalArgumentException("Attempted to store a user-provided table" +
                            "result entry, but unexpectedly received a bad Editable. ")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        isInitialized = true
    }
}