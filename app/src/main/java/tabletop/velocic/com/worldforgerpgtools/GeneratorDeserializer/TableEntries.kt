package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer

import com.google.gson.annotations.SerializedName

class TableEntries(
    @SerializedName("Name")
    val name: String,

    @SerializedName("Metadata")
    val metadata: Map<String, String>,

    @SerializedName("DiceRange")
    private val diceRangeString: String,

    @SerializedName("RerollSubTable")
    val rerollSubTable: Map<String, String>?
) {
    val diceRange
        get() = parseDiceRangeString(diceRangeString)

    fun getSubTableRollRange() : IntRange {
        if (rerollSubTable == null) {
            return 1 until Int.MAX_VALUE
        }

        if (rerollSubTable.containsKey("ValidSubTableEntryRange")) {
            return parseDiceRangeString(rerollSubTable["ValidSubTableEntryRange"])
        }

        return 1 until Int.MAX_VALUE
    }

    fun getNumSubTableRolls() : Int
    {
        if (rerollSubTable == null) {
            return 0
        }

        return rerollSubTable["NumSubTableRolls"]?.toInt() ?: 1
    }

    private fun parseDiceRangeString(diceRangeString: String?) : IntRange {
        val separatorIndex = diceRangeString?.indexOf("-") ?: -1

        val (left, right) = if (separatorIndex == -1) {
            val singleItemRange = diceRangeString?.toInt() ?: 1
            Pair(singleItemRange, singleItemRange)
        } else {
            Pair(
                diceRangeString?.substring(0, separatorIndex)?.toInt() ?: 1,
                diceRangeString?.substring(separatorIndex + 1)?.toInt() ?: Int.MAX_VALUE - 1
            )
        }

        val (min, max) = if (left < right) {
            Pair(left, right)
        } else {
            Pair(right, left)
        }

        return min..max
    }
}