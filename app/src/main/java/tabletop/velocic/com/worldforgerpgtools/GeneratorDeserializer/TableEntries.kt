package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer

class TableEntries(
    val Name: String,
    val Metadata: Map<String, String>,
    val DiceRange: String,
    val RerollSubTable: Map<String, String>
) {
    fun getSubTableRollRange() : IntRange {
        //TODO: do we want Int.MAX_VALUE or Int.MAX_VALUE - 1 as the upper range bound?

        if (RerollSubTable.containsKey("ValidSubTableEntryRange")) {
            val rangeString = RerollSubTable["ValidSubTableEntryRange"];
            val separatorIndex = rangeString?.indexOf("-") ?: 0

            val (left, right) = if (separatorIndex == 0) {
                Pair(1, Int.MAX_VALUE - 1)
            } else {
                Pair(
                    rangeString?.substring(0, separatorIndex)?.toInt() ?: 1,
                    rangeString?.substring(separatorIndex + 1)?.toInt() ?: Int.MAX_VALUE - 1
                )
            }

            val (min, max) = if (left < right) {
                Pair(left, right)
            } else {
                Pair(right, left)
            }

            return min..max
        }

        return 1 until Int.MAX_VALUE
    }

    fun getNumSubTableRolls() : Int
    {
        return RerollSubTable["NumSubTableRolls"]?.toInt() ?: 1
    }
}