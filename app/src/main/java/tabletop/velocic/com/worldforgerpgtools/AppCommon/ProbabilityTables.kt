package tabletop.velocic.com.worldforgerpgtools.AppCommon

import kotlin.math.pow

object ProbabilityTables {
    val oneDFour = ProbabilityTable(dieSize = 4)
    val oneDSix = ProbabilityTable(dieSize = 6)
    val oneDEight = ProbabilityTable(dieSize = 8)
    val oneDTen = ProbabilityTable(dieSize = 10)
    val oneDTwelve = ProbabilityTable(dieSize = 12)
    val oneDTwenty = ProbabilityTable(dieSize = 20)
    val twoDSix = ProbabilityTable(2, 6)
    val threeDSix = ProbabilityTable(3, 6)
    val oneDOneHundred = ProbabilityTable(dieSize = 100)
}

class ProbabilityTable(
    private val numDie: Int = 1,
    private val dieSize: Int
) {
    private val tableSize = (dieSize * numDie) - (numDie - 1)
    private val tableRange = numDie..(dieSize * numDie)
    private val lookupTable = Array(tableSize) { Pair(false, 0.0) }

    fun getProbability(targetValue: Int): Double {
        if (targetValue !in tableRange) {
            return 0.0
        }

        val (hasBeenCalculated, result) = lookupTable[targetValue]

        if (hasBeenCalculated) {
            return result
        }

        lookupTable[targetValue] = Pair(
            true,
            findProbabilityForTargetInDicePool(targetValue, dieSize, numDie)
        )

        return lookupTable[targetValue].second
    }

    fun getProbability(targetRange: IntRange): Double =
        targetRange.map { targetValue ->
            findProbabilityForTargetInDicePool(targetValue, dieSize, numDie)
        }.reduce { accumulator, probability -> accumulator + probability }
}

private fun findProbabilityForTargetInDicePool(targetValue: Int, dieSize: Int, numDie: Int = 1) : Double {
    if (numDie == 1) {
        return 1 / dieSize.toDouble()
    }

    val dicePool = (0 until numDie).map { 1..dieSize }

    val numAppearancesOfTargetValue = calculateAllCombinationsForDicePool(dicePool).filter { total ->
        total == targetValue
    }.size

    return numAppearancesOfTargetValue / dieSize.toDouble().pow(numDie)
}

private fun calculateAllCombinationsForDicePool(dicePool: List<IntRange>): List<Int> {
    if (dicePool.size < 2) {
        return dicePool.first().map { it }
    }

    return calculateCombinationTotalsAgainstDicePool(dicePool.first(), dicePool.drop(1))
}

private fun calculateCombinationTotalsAgainstDicePool(targetDie: IntRange, dicePool: List<IntRange>, totalProgress: List<Int>? = null) : List<Int> {
    if (dicePool.isEmpty()) {
        return totalProgress?.toList() ?: listOf()
    }

    var currentIterationProgress = totalProgress

    if (currentIterationProgress == null) {
        currentIterationProgress = targetDie.map { it }
    }

    val nextDie = dicePool.first()

    currentIterationProgress = currentIterationProgress.flatMap { partialCombinationResult ->
        nextDie.map { nextDieFace ->
            partialCombinationResult + nextDieFace
        }
    }

    val remainingDicePool = if (dicePool.size > 1) {
        dicePool.drop(1)
    } else {
        listOf()
    }

    return calculateCombinationTotalsAgainstDicePool(
        targetDie,
        remainingDicePool,
        currentIterationProgress
    )
}
