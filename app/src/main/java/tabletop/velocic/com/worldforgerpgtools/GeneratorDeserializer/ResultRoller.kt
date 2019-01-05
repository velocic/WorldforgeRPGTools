package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer

import tabletop.velocic.com.worldforgerpgtools.Extensions.random

class ResultRoller(
    private val rootGeneratorCategory: GeneratorCategory
) {
    fun generateResultSet(fullyQualifiedGeneratorPath: String, numRolls: Int = 1) : List<ResultItem> {
        val resultSet: List<ResultItem> = mutableListOf()

        val randomTable = rootGeneratorCategory.getGeneratorFromFullPath(fullyQualifiedGeneratorPath, rootGeneratorCategory).table
        val tableRollRange = determineTableDiceRange(randomTable)

        for (i in 0..numRolls) {
            val roll = tableRollRange.random()

            for (entry in randomTable) {
                val entryIsMatch = roll in tableRollRange

                if (entryIsMatch) {
                    val rerollSubTable = entry.rerollSubTable

                    //Some entries on random tables call for a roll on a completely different table
                    //So handle that here with a recursive call + merging the resulting sets of ResultItems
                    //together
                }
            }
        }
    }

//    fun generateResultSet(fullyQualifiedGeneratorPath: String, numRolls: Int = 1, minRollValue: Int, maxRollValue: Int) : List<ResultItem> {
//        val resultSet: List<ResultItem> = mutableListOf()
//
//        val randomTable = rootGeneratorCategory.getGeneratorFromFullPath(fullyQualifiedGeneratorPath, rootGeneratorCategory)
//        val defaultTableRollRange = determineTableDiceRange(randomTable.table)
//
//        for (i in 0..numRolls) {
//
//        }
//    }

    private fun determineTableDiceRange(tableEntries: Array<TableEntries>): IntRange {
        var tableMinRoll = 1
        var tableMaxRoll = 1

        tableEntries.forEach {
            val entryRange = it.diceRange
            if (entryRange.first < tableMinRoll) {
                tableMinRoll = entryRange.first
            }
            if (entryRange.last > tableMaxRoll) {
                tableMaxRoll = entryRange.last
            }
        }

        return tableMinRoll..tableMaxRoll
    }
}