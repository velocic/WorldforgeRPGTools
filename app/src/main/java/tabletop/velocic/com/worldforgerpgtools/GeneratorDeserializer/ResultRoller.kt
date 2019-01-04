package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer

import tabletop.velocic.com.worldforgerpgtools.Extensions.random

class ResultRoller(
    val rootGeneratorCategory: GeneratorCategory
) {
    fun generateResultSet(fullyQualifiedGeneratorPath: String, numRolls: Int) : List<ResultItem> {
        return generateResultSet(fullyQualifiedGeneratorPath, numRolls, 1, Integer.MAX_VALUE - 1)
    }

    fun generateResultSet(fullyQualifiedGeneratorPath: String, numRolls: Int, minRollValue: Int, maxRollValue: Int) : List<ResultItem> {

    }
}