package tabletop.velocic.com.worldforgerpgtools.AppCommon

object ProbabilityTables {

}

private fun findProbabilityForTargetInDicePool(targetValue: Int, dieSize: Int, numDie: Int = 1) : Double {
    if (numDie == 1) {
        return 1 / dieSize.toDouble()
    }

    val dicePool = mutableListOf<IntRange>()

    for(i in 0 until numDie) {
        dicePool.add(1..dieSize)
    }

    val allPossibleDieCombinationResults = mutableListOf<Int>()

    //Note: lambdas can't be recursive, so break this into another function
    val calcCombinationResultsAgainstTargetDie = calcCombinations@ { targetDie: IntRange, remainingDicePool: List<IntRange> ->
        if (remainingDicePool.isEmpty()) {
            return@calcCombinations listOf<Int>()
        }

        val nextDie = remainingDicePool.first()

        val combinationResultProgress = targetDie.flatMap { targetDieFace ->
            nextDie.map { nextDieFace ->
                targetDieFace + nextDieFace
            }
        }

        return@calcCombinations combinationResultProgress//+ result of next recursive invocation
    }

    for(currentDieIndex in 0 until dicePool.size) {
        val currentDie = dicePool[currentDieIndex]
        val allOtherDice = dicePool.filterIndexed { otherDieIndex, _ ->
            otherDieIndex != currentDieIndex
        }

        allPossibleDieCombinationResults.addAll(calcCombinationResultsAgainstTargetDie(currentDie, allOtherDice))
    }
}
