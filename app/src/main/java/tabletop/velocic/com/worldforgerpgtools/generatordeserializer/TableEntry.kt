package tabletop.velocic.com.worldforgerpgtools.generatordeserializer

import com.google.gson.annotations.SerializedName

class TableEntry(
    @SerializedName("Name")
    var name: String,

    @SerializedName("Metadata")
    var metadata: List<ResultItemDetail>,

    @SerializedName("DiceRange")
    var diceRangeString: String,

    @SerializedName("RerollSubTable")
    var rerollSubTable: RerollSubTable?
) {
    val diceRange
        get() = parseDiceRangeString(diceRangeString)

    fun copy(other: TableEntry) {
        name = other.name
        metadata = other.metadata
        diceRangeString = other.diceRangeString
        rerollSubTable = other.rerollSubTable
    }

    fun getSubTableRollRange() : IntRange {
        val nullCheckedSubTable = rerollSubTable?.let {
            it
        } ?: return 1 until Int.MAX_VALUE

        return parseDiceRangeString(nullCheckedSubTable.targetTableRange)
    }

    fun getNumSubTableRolls() : Int
    {
        val nullCheckedSubTable = rerollSubTable?.let {
            it
        } ?: return 0

        return nullCheckedSubTable.targetTableRollCount
    }

    private fun parseDiceRangeString(diceRangeString: String?) : IntRange {
        val separatorIndex = diceRangeString?.indexOf("-") ?: -1

        val (left, right) = if (separatorIndex == -1) {
            val singleItemRange = diceRangeString?.toInt() ?: 1
            singleItemRange to singleItemRange
        } else {
            Pair(
                diceRangeString?.substring(0, separatorIndex)?.toInt() ?: 1,
                diceRangeString?.substring(separatorIndex + 1)?.toInt() ?: Int.MAX_VALUE - 1
            )
        }

        val (min, max) = if (left < right) {
            left to right
        } else {
            right to left
        }

        return min..max
    }
}