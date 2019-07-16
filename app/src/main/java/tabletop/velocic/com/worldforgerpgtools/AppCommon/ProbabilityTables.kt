package tabletop.velocic.com.worldforgerpgtools.AppCommon

import kotlin.math.pow

object ProbabilityTables {

    init {
    }
}

private class OneDFourTable {
    val one by lazy { findProbabilityForTargetInDicePool(1, 4, 1) }
    val two by lazy { findProbabilityForTargetInDicePool(2, 4, 1) }
    val three by lazy { findProbabilityForTargetInDicePool(3, 4, 1) }
    val four by lazy { findProbabilityForTargetInDicePool(4, 4, 1) }
}

private class OneDSixTable {
    val one by lazy { findProbabilityForTargetInDicePool(1, 6, 1) }
    val two by lazy { findProbabilityForTargetInDicePool(2, 6, 1) }
    val three by lazy { findProbabilityForTargetInDicePool(3, 6, 1) }
    val four by lazy { findProbabilityForTargetInDicePool(4, 6, 1) }
    val five by lazy { findProbabilityForTargetInDicePool(5, 6, 1) }
    val six by lazy { findProbabilityForTargetInDicePool(6, 6, 1) }
}

private class OneDEightTable {
    val one by lazy { findProbabilityForTargetInDicePool(1, 8, 1) }
    val two by lazy { findProbabilityForTargetInDicePool(2, 8, 1) }
    val three by lazy { findProbabilityForTargetInDicePool(3, 8, 1) }
    val four by lazy { findProbabilityForTargetInDicePool(4, 8, 1) }
    val five by lazy { findProbabilityForTargetInDicePool(5, 8, 1) }
    val six by lazy { findProbabilityForTargetInDicePool(6, 8, 1) }
    val seven by lazy { findProbabilityForTargetInDicePool(7, 8, 1) }
    val eight by lazy { findProbabilityForTargetInDicePool(8, 8, 1) }
}

private class OneDTenTable {
    val one by lazy { findProbabilityForTargetInDicePool(1, 10, 1) }
    val two by lazy { findProbabilityForTargetInDicePool(2, 10, 1) }
    val three by lazy { findProbabilityForTargetInDicePool(3, 10, 1) }
    val four by lazy { findProbabilityForTargetInDicePool(4, 10, 1) }
    val five by lazy { findProbabilityForTargetInDicePool(5, 10, 1) }
    val six by lazy { findProbabilityForTargetInDicePool(6, 10, 1) }
    val seven by lazy { findProbabilityForTargetInDicePool(7, 10, 1) }
    val eight by lazy { findProbabilityForTargetInDicePool(8, 10, 1) }
    val nine by lazy { findProbabilityForTargetInDicePool(9, 10, 1) }
    val ten by lazy { findProbabilityForTargetInDicePool(10, 10, 1) }
}

private class OneDTwelveTable {
    val one by lazy { findProbabilityForTargetInDicePool(1, 12, 1) }
    val two by lazy { findProbabilityForTargetInDicePool(2, 12, 1) }
    val three by lazy { findProbabilityForTargetInDicePool(3, 12, 1) }
    val four by lazy { findProbabilityForTargetInDicePool(4, 12, 1) }
    val five by lazy { findProbabilityForTargetInDicePool(5, 12, 1) }
    val six by lazy { findProbabilityForTargetInDicePool(6, 12, 1) }
    val seven by lazy { findProbabilityForTargetInDicePool(7, 12, 1) }
    val eight by lazy { findProbabilityForTargetInDicePool(8, 12, 1) }
    val nine by lazy { findProbabilityForTargetInDicePool(9, 12, 1) }
    val ten by lazy { findProbabilityForTargetInDicePool(10, 12, 1) }
    val eleven by lazy { findProbabilityForTargetInDicePool(11, 12, 1) }
    val twelve by lazy { findProbabilityForTargetInDicePool(12, 12, 1) }
}

private class OneDTwentyTable {
    val one by lazy { findProbabilityForTargetInDicePool(1, 12, 1) }
    val two by lazy { findProbabilityForTargetInDicePool(2, 12, 1) }
    val three by lazy { findProbabilityForTargetInDicePool(3, 12, 1) }
    val four by lazy { findProbabilityForTargetInDicePool(4, 12, 1) }
    val five by lazy { findProbabilityForTargetInDicePool(5, 12, 1) }
    val six by lazy { findProbabilityForTargetInDicePool(6, 12, 1) }
    val seven by lazy { findProbabilityForTargetInDicePool(7, 12, 1) }
    val eight by lazy { findProbabilityForTargetInDicePool(8, 12, 1) }
    val nine by lazy { findProbabilityForTargetInDicePool(9, 12, 1) }
    val ten by lazy { findProbabilityForTargetInDicePool(10, 12, 1) }
    val eleven by lazy { findProbabilityForTargetInDicePool(11, 12, 1) }
    val twelve by lazy { findProbabilityForTargetInDicePool(12, 12, 1) }
    val thirteen by lazy { findProbabilityForTargetInDicePool(13, 20, 1) }
    val fourteen by lazy { findProbabilityForTargetInDicePool(14, 20, 1) }
    val fifteen by lazy { findProbabilityForTargetInDicePool(15, 20, 1) }
    val sixteen by lazy { findProbabilityForTargetInDicePool(16, 20, 1) }
    val seventeen by lazy { findProbabilityForTargetInDicePool(17, 20, 1) }
    val eighteen by lazy { findProbabilityForTargetInDicePool(18, 20, 1) }
    val nineteen by lazy { findProbabilityForTargetInDicePool(19, 20, 1) }
    val twenty by lazy { findProbabilityForTargetInDicePool(20, 20, 1) }
}

private class TwoDSixTable {
    val one by lazy { findProbabilityForTargetInDicePool(1, 6, 2) }
    val two by lazy { findProbabilityForTargetInDicePool(2, 6, 2) }
    val three by lazy { findProbabilityForTargetInDicePool(3, 6, 2) }
    val four by lazy { findProbabilityForTargetInDicePool(4, 6, 2) }
    val five by lazy { findProbabilityForTargetInDicePool(5, 6, 2) }
    val six by lazy { findProbabilityForTargetInDicePool(6, 6, 2) }
}

private class ThreeDSixTable {
    val one by lazy { findProbabilityForTargetInDicePool(1, 6, 3) }
    val two by lazy { findProbabilityForTargetInDicePool(2, 6, 3) }
    val three by lazy { findProbabilityForTargetInDicePool(3, 6, 3) }
    val four by lazy { findProbabilityForTargetInDicePool(4, 6, 3) }
    val five by lazy { findProbabilityForTargetInDicePool(5, 6, 3) }
    val six by lazy { findProbabilityForTargetInDicePool(6, 6, 3) }
}

private class OneDOneHundredTable {
    val one by lazy { findProbabilityForTargetInDicePool(1, 100, 1) }
    val two by lazy { findProbabilityForTargetInDicePool(2, 100, 1) }
    val three by lazy { findProbabilityForTargetInDicePool(3, 100, 1) }
    val four by lazy { findProbabilityForTargetInDicePool(4, 100, 1) }
    val five by lazy { findProbabilityForTargetInDicePool(5, 100, 1) }
    val six by lazy { findProbabilityForTargetInDicePool(6, 100, 1) }
    val seven by lazy { findProbabilityForTargetInDicePool(7, 100, 1) }
    val eight by lazy { findProbabilityForTargetInDicePool(8, 100, 1) }
    val nine by lazy { findProbabilityForTargetInDicePool(9, 100, 1) }
    val ten by lazy { findProbabilityForTargetInDicePool(10, 100, 1) }
    val eleven by lazy { findProbabilityForTargetInDicePool(11, 100, 1) }
    val twelve by lazy { findProbabilityForTargetInDicePool(12, 100, 1) }
    val thirteen by lazy { findProbabilityForTargetInDicePool(13, 100, 1) }
    val fourteen by lazy { findProbabilityForTargetInDicePool(14, 100, 1) }
    val fifteen by lazy { findProbabilityForTargetInDicePool(15, 100, 1) }
    val sixteen by lazy { findProbabilityForTargetInDicePool(16, 100, 1) }
    val seventeen by lazy { findProbabilityForTargetInDicePool(17, 100, 1) }
    val eighteen by lazy { findProbabilityForTargetInDicePool(18, 100, 1) }
    val nineteen by lazy { findProbabilityForTargetInDicePool(19, 100, 1) }
    val twenty by lazy { findProbabilityForTargetInDicePool(20, 100, 1) }
    val twentyone by lazy { findProbabilityForTargetInDicePool(21, 100, 1) }
    val twentytwo by lazy { findProbabilityForTargetInDicePool(22, 100, 1) }
    val twentythree by lazy { findProbabilityForTargetInDicePool(23, 100, 1) }
    val twentyfour by lazy { findProbabilityForTargetInDicePool(24, 100, 1) }
    val twentyfive by lazy { findProbabilityForTargetInDicePool(25, 100, 1) }
    val twentysix by lazy { findProbabilityForTargetInDicePool(26, 100, 1) }
    val twentyseven by lazy { findProbabilityForTargetInDicePool(27, 100, 1) }
    val twentyeight by lazy { findProbabilityForTargetInDicePool(28, 100, 1) }
    val twentynine by lazy { findProbabilityForTargetInDicePool(29, 100, 1) }
    val thirty by lazy { findProbabilityForTargetInDicePool(30, 100, 1) }
    val thirtyone by lazy { findProbabilityForTargetInDicePool(31, 100, 1) }
    val thirtytwo by lazy { findProbabilityForTargetInDicePool(32, 100, 1) }
    val thirtythree by lazy { findProbabilityForTargetInDicePool(33, 100, 1) }
    val thirtyfour by lazy { findProbabilityForTargetInDicePool(34, 100, 1) }
    val thirtyfive by lazy { findProbabilityForTargetInDicePool(35, 100, 1) }
    val thirtysix by lazy { findProbabilityForTargetInDicePool(36, 100, 1) }
    val thirtyseven by lazy { findProbabilityForTargetInDicePool(37, 100, 1) }
    val thirtyeight by lazy { findProbabilityForTargetInDicePool(38, 100, 1) }
    val thirtynine by lazy { findProbabilityForTargetInDicePool(39, 100, 1) }
    val fourty by lazy { findProbabilityForTargetInDicePool(40, 100, 1) }
    val fourtyone by lazy { findProbabilityForTargetInDicePool(41, 100, 1) }
    val fourtytwo by lazy { findProbabilityForTargetInDicePool(42, 100, 1) }
    val fourtythree by lazy { findProbabilityForTargetInDicePool(43, 100, 1) }
    val fourtyfour by lazy { findProbabilityForTargetInDicePool(44, 100, 1) }
    val fourtyfive by lazy { findProbabilityForTargetInDicePool(45, 100, 1) }
    val fourtysix by lazy { findProbabilityForTargetInDicePool(46, 100, 1) }
    val fourtyseven by lazy { findProbabilityForTargetInDicePool(47, 100, 1) }
    val fourtyeight by lazy { findProbabilityForTargetInDicePool(48, 100, 1) }
    val fourtynine by lazy { findProbabilityForTargetInDicePool(49, 100, 1) }
    val fifty by lazy { findProbabilityForTargetInDicePool(50, 100, 1) }
    val fiftyone by lazy { findProbabilityForTargetInDicePool(51, 100, 1) }
    val fiftytwo by lazy { findProbabilityForTargetInDicePool(52, 100, 1) }
    val fiftythree by lazy { findProbabilityForTargetInDicePool(53, 100, 1) }
    val fiftyfour by lazy { findProbabilityForTargetInDicePool(54, 100, 1) }
    val fiftyfive by lazy { findProbabilityForTargetInDicePool(55, 100, 1) }
    val fiftysix by lazy { findProbabilityForTargetInDicePool(56, 100, 1) }
    val fiftyseven by lazy { findProbabilityForTargetInDicePool(57, 100, 1) }
    val fiftyeight by lazy { findProbabilityForTargetInDicePool(58, 100, 1) }
    val fiftynine by lazy { findProbabilityForTargetInDicePool(59, 100, 1) }
    val sixty by lazy { findProbabilityForTargetInDicePool(60, 100, 1) }
    val sixtyone by lazy { findProbabilityForTargetInDicePool(61, 100, 1) }
    val sixtytwo by lazy { findProbabilityForTargetInDicePool(62, 100, 1) }
    val sixtythree by lazy { findProbabilityForTargetInDicePool(63, 100, 1) }
    val sixtyfour by lazy { findProbabilityForTargetInDicePool(64, 100, 1) }
    val sixtyfive by lazy { findProbabilityForTargetInDicePool(65, 100, 1) }
    val sixtysix by lazy { findProbabilityForTargetInDicePool(66, 100, 1) }
    val sixtyseven by lazy { findProbabilityForTargetInDicePool(67, 100, 1) }
    val sixtyeight by lazy { findProbabilityForTargetInDicePool(68, 100, 1) }
    val sixtynine by lazy { findProbabilityForTargetInDicePool(69, 100, 1) }
    val seventy by lazy { findProbabilityForTargetInDicePool(70, 100, 1) }
    val seventyone by lazy { findProbabilityForTargetInDicePool(71, 100, 1) }
    val seventytwo by lazy { findProbabilityForTargetInDicePool(72, 100, 1) }
    val seventythree by lazy { findProbabilityForTargetInDicePool(73, 100, 1) }
    val seventyfour by lazy { findProbabilityForTargetInDicePool(74, 100, 1) }
    val seventyfive by lazy { findProbabilityForTargetInDicePool(75, 100, 1) }
    val seventysix by lazy { findProbabilityForTargetInDicePool(76, 100, 1) }
    val seventyseven by lazy { findProbabilityForTargetInDicePool(77, 100, 1) }
    val seventyeight by lazy { findProbabilityForTargetInDicePool(78, 100, 1) }
    val seventynine by lazy { findProbabilityForTargetInDicePool(79, 100, 1) }
    val eighty by lazy { findProbabilityForTargetInDicePool(80, 100, 1) }
    val eightyone by lazy { findProbabilityForTargetInDicePool(81, 100, 1) }
    val eightytwo by lazy { findProbabilityForTargetInDicePool(82, 100, 1) }
    val eightythree by lazy { findProbabilityForTargetInDicePool(83, 100, 1) }
    val eightyfour by lazy { findProbabilityForTargetInDicePool(84, 100, 1) }
    val eightyfive by lazy { findProbabilityForTargetInDicePool(85, 100, 1) }
    val eightysix by lazy { findProbabilityForTargetInDicePool(86, 100, 1) }
    val eightyseven by lazy { findProbabilityForTargetInDicePool(87, 100, 1) }
    val eightyeight by lazy { findProbabilityForTargetInDicePool(88, 100, 1) }
    val eightynine by lazy { findProbabilityForTargetInDicePool(89, 100, 1) }
    val ninety by lazy { findProbabilityForTargetInDicePool(90, 100, 1) }
    val ninetyone by lazy { findProbabilityForTargetInDicePool(91, 100, 1) }
    val ninetytwo by lazy { findProbabilityForTargetInDicePool(92, 100, 1) }
    val ninetythree by lazy { findProbabilityForTargetInDicePool(93, 100, 1) }
    val ninetyfour by lazy { findProbabilityForTargetInDicePool(94, 100, 1) }
    val ninetyfive by lazy { findProbabilityForTargetInDicePool(95, 100, 1) }
    val ninetysix by lazy { findProbabilityForTargetInDicePool(96, 100, 1) }
    val ninetyseven by lazy { findProbabilityForTargetInDicePool(97, 100, 1) }
    val ninetyeight by lazy { findProbabilityForTargetInDicePool(98, 100, 1) }
    val ninetynine by lazy { findProbabilityForTargetInDicePool(99, 100, 1) }
    val onehundred by lazy { findProbabilityForTargetInDicePool(100, 100, 1) }
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
