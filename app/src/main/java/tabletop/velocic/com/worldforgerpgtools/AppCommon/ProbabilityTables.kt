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
        val debug = 5
    }
}

private fun findProbabilityForTargetInDicePool(targetValue: Int, dieSize: Int, numDie: Int = 1) : Double {
    if (numDie == 1) {
        return 1 / dieSize.toDouble()
    }

    val dicePool = (0 until numDie).map { 1..dieSize }

    val allPossibleDieCombinationTotals = mutableListOf<Int>()

    for (currentDieIndex in 0 until dicePool.size) {
        val currentDie = dicePool[currentDieIndex]
        val reducedDicePool = dicePool.subList(currentDieIndex, dicePool.lastIndex)

        allPossibleDieCombinationTotals.addAll(
            calculateCombinationTotalsAgainstDicePool(currentDie, reducedDicePool)
        )
    }

    val numCombinationsYieldingTargetValue = allPossibleDieCombinationTotals.filter { total ->
        total == targetValue
    }.size

    return numCombinationsYieldingTargetValue / dieSize.toDouble().pow(numDie)
}

private fun calculateCombinationTotalsAgainstDicePool(targetDie: IntRange, dicePool: List<IntRange>) : List<Int> {
    if (dicePool.isEmpty()) {
        return listOf()
    }

    val nextDie = dicePool.first()

    val combinationResultProgress = targetDie.flatMap { targetDieFace ->
        nextDie.map { nextDieFace ->
            targetDieFace + nextDieFace
        }
    }

    val remainingDicePool = if (dicePool.size > 1) {
        dicePool.subList(1, dicePool.lastIndex)
    } else {
        listOf()
    }

    return combinationResultProgress + calculateCombinationTotalsAgainstDicePool(
        targetDie,
        remainingDicePool
    )
}
