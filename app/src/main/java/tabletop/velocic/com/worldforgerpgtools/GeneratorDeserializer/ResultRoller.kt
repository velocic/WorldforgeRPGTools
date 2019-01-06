package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer

import tabletop.velocic.com.worldforgerpgtools.Extensions.random

class ResultRoller(
    private val rootGeneratorCategory: GeneratorCategory
) {
    fun generateResultSet(fullyQualifiedGeneratorPath: String, numRolls: Int = 1) : List<ResultItem> {
        return generateResultSet(fullyQualifiedGeneratorPath, numRolls, 1, Int.MAX_VALUE - 1)
    }

    fun generateResultSet(fullyQualifiedGeneratorPath: String, numRolls: Int = 1, minRollValue: Int, maxRollValue: Int) : List<ResultItem> {
        var resultSet: MutableList<ResultItem> = mutableListOf()

        val randomTable = rootGeneratorCategory.getGeneratorFromFullPath(fullyQualifiedGeneratorPath, rootGeneratorCategory).table
        val tableRollRange = determineTableDiceRange(randomTable)

        for (i in 0 until numRolls) {
            val actualMinRoll = if (minRollValue > tableRollRange.last) tableRollRange.last else minRollValue
            val actualMaxRoll = if (tableRollRange.last < maxRollValue) tableRollRange.last else maxRollValue

            val roll = (actualMinRoll..actualMaxRoll).random()

            for (entry in randomTable) {
                val entryIsMatch = roll in entry.diceRange

                if (entryIsMatch) {
                    val rerollSubTable = entry.rerollSubTable ?: mapOf()

                    //Some entries on random tables call for a roll on a completely different table
                    //So handle that here with a recursive call + merging the resulting sets of ResultItems
                    //together
                    if (!rerollSubTable.isEmpty()) {
                        val subTableName = rerollSubTable["SubTableName"] ?: ""
                        val subTableRollRange = entry.getSubTableRollRange()
                        val numSubTableRolls = entry.getNumSubTableRolls()
                        val subTableResults = mutableListOf(mutableListOf<ResultItem>())

                        for (subTableRoll in 0 until numSubTableRolls) {
                            subTableResults.add(generateResultSet(subTableName, numSubTableRolls, subTableRollRange.first, subTableRollRange.last).toMutableList())
                        }

                        subTableResults.add(resultSet)
                        resultSet = mergeResultSets(subTableResults)

                        continue
                    }

                    //Now the simple case. Add the new item directly, or increment an existing value
                    val newItem = ResultItem(fullyQualifiedGeneratorPath, entry.name, entry.metadata)
                    addResultItem(resultSet, newItem)
                }
            }
        }

        return resultSet
    }

    private fun mergeResultSets(mergeSets: MutableList<MutableList<ResultItem>>) : MutableList<ResultItem> {
        val finalMergedSet: MutableList<ResultItem> = mutableListOf()

        for (mergeSet in mergeSets) {
            for (item in mergeSet) {
                var hadMatchInFinalMergedSet = false

                for (mergedItem in finalMergedSet) {
                    if (item.name == mergedItem.name) {
                        hadMatchInFinalMergedSet = true
                        mergedItem.quantity = mergedItem.quantity + 1
                        break
                    }
                }

                if (!hadMatchInFinalMergedSet) {
                    finalMergedSet.add(item)
                }
            }
        }

        return finalMergedSet
    }

    private fun addResultItem(sourceList: MutableList<ResultItem>, newItem: ResultItem) : MutableList<ResultItem> {
        for (item in sourceList) {
            if (item.name == newItem.name) {
                item.quantity = item.quantity + 1
                return sourceList
            }
        }

        sourceList.add(newItem)
        return sourceList
    }

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