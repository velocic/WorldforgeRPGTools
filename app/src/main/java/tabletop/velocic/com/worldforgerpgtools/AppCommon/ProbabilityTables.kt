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

    for (currentDieIndex in 0 until dicePool.size) {
        val currentDie = dicePool[currentDieIndex]
        val allOtherDice = dicePool.filterIndexed { otherDieIndex, _ ->
            otherDieIndex != currentDieIndex
        }
    }

}
