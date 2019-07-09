package tabletop.velocic.com.worldforgerpgtools.GeneratorCreation.ViewModels.NewGeneratorContents

import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.partial_generator_contents_main_body.view.*
import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.TableEntries
import kotlin.math.absoluteValue

class MainUserInput(
    parent: ViewGroup,
    private val boundTableEntry: TableEntries
)
{
    private val percentChance = parent.generator_contents_percent_chance as TextView
    private val result = parent.generator_contents_result as EditText
    private val rollRange = parent.generator_contents_roll_range as TextView
    private val rollRangeSize: Int
        get() = (boundTableEntry.diceRange.last - boundTableEntry.diceRange.first).absoluteValue

    init {
        rollRange.text = boundTableEntry.diceRangeString
    }

    fun updateFromBackingData() {

    }

    //TODO: create probability matrix singleton object that precalculates (lazily) lookup tables
    //for each table template type. This function will simply look up values using that utility
//    fun updateResultChance(currentTableSize: Int, dieSize: Int, numDie: Int = 1) =
//        if (numDie == 1) {
//            rollRangeSize / dieSize.toDouble()
//        } else {
//
//        }

    fun updateResult(result: String) {
        boundTableEntry.name = result
    }
}