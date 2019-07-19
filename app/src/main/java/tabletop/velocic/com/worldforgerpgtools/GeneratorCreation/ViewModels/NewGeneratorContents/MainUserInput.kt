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
    parent: ViewGroup,
    private val boundTableEntry: TableEntries
)
{
    private val percentChance = parent.generator_contents_percent_chance as TextView
    private val result = parent.generator_contents_result as EditText
    private val rollRange = parent.generator_contents_roll_range as TextView

    init {
        result.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                boundTableEntry.name = s?.toString()
                    ?: throw IllegalArgumentException("Attempted to store a user-provided table" +
                        "result entry, but unexpectedly received a bad Editable. ")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        rollRange.text = boundTableEntry.diceRangeString
    }

    fun updateFromBackingData() {
        rollRange.text = boundTableEntry.diceRangeString
        result.setText(boundTableEntry.name, TextView.BufferType.EDITABLE)
    }

    fun updateResultChance(numDie: Int = 1, dieSize: Int) {
        val scaledProbability = ProbabilityTables.getProbability(
            boundTableEntry.diceRange, ProbabilityTableKey(numDie, dieSize)
        ) * 100

        val fieldContent = "${"%.2f".format(scaledProbability)}%"
        percentChance.text = fieldContent
    }
}