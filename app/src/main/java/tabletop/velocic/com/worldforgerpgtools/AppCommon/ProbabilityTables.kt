package tabletop.velocic.com.worldforgerpgtools.AppCommon

import kotlin.math.pow

object ProbabilityTables {
    init {
        val twoDSixTable = (2..12).map {
            Pair(it, findProbabilityForTargetInDicePool(it, 6, 2))
        }
        val threeDSixTable = (3..18).map {
            Pair(it, findProbabilityForTargetInDicePool(it, 6, 3))
        }
        val fourDSixTable = (4..24).map {
            Pair(it, findProbabilityForTargetInDicePool(it, 6, 4))
        }

        val debug = 5
    }
}

private fun findProbabilityForTargetInDicePool(targetValue: Int, dieSize: Int, numDie: Int = 1) : Double {
    if (numDie == 1) {
        return 1 / dieSize.toDouble()
    }

    val dicePool = (0 until numDie).map { 1..dieSize }

    val allPossibleDieCombinationTotals = calculateCombinationTotalsAgainstDicePool(
        dicePool.first(),
        dicePool.drop(1)
    )

    val numCombinationsYieldingTargetValue = allPossibleDieCombinationTotals.filter { total ->
        total == targetValue
    }.size

    return numCombinationsYieldingTargetValue / dieSize.toDouble().pow(numDie)
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
