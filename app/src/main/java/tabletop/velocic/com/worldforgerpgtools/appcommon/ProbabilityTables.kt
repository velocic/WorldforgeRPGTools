package tabletop.velocic.com.worldforgerpgtools.appcommon

import android.os.Parcel
import android.os.Parcelable
import kotlin.math.pow

data class ProbabilityTableKey(val numDie: Int = 1, val dieSize:Int) : Parcelable
{
    constructor(parcel: Parcel) : this(parcel.readInt(), parcel.readInt())

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeInt(numDie)
            writeInt(dieSize)
        }
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<ProbabilityTableKey> {
            override fun createFromParcel(source: Parcel): ProbabilityTableKey =
                ProbabilityTableKey(source)

            override fun newArray(size: Int): Array<ProbabilityTableKey?> =
                arrayOfNulls(size)
        }
    }
}

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

    val customTables = HashMap<ProbabilityTableKey, ProbabilityTable>()

    fun getProbability(targetValue: Int, targetTable: ProbabilityTableKey): Double =
        when(targetTable.numDie) {
            1 -> when (targetTable.dieSize) {
                4 -> oneDFour.getProbability(targetValue)
                6 -> oneDSix.getProbability(targetValue)
                8 -> oneDEight.getProbability(targetValue)
                10 -> oneDTen.getProbability(targetValue)
                12 -> oneDTwelve.getProbability(targetValue)
                20 -> oneDTwenty.getProbability(targetValue)
                100 -> oneDOneHundred.getProbability(targetValue)
                else -> getCustomTable(targetTable).getProbability(targetValue)
            }
            2 -> when (targetTable.dieSize) {
                6 -> twoDSix.getProbability(targetValue)
                else -> getCustomTable(targetTable).getProbability(targetValue)
            }
            3 -> when (targetTable.dieSize) {
                6 -> threeDSix.getProbability(targetValue)
                else -> getCustomTable(targetTable).getProbability(targetValue)
            }
            else -> getCustomTable(targetTable).getProbability(targetValue)
        }

    fun getProbability(targetRange: IntRange, targetTable: ProbabilityTableKey): Double =
        when(targetTable.numDie) {
            1 -> when (targetTable.dieSize) {
                4 -> oneDFour.getProbability(targetRange)
                6 -> oneDSix.getProbability(targetRange)
                8 -> oneDEight.getProbability(targetRange)
                10 -> oneDTen.getProbability(targetRange)
                12 -> oneDTwelve.getProbability(targetRange)
                20 -> oneDTwenty.getProbability(targetRange)
                100 -> oneDOneHundred.getProbability(targetRange)
                else -> getCustomTable(targetTable).getProbability(targetRange)
            }
            2 -> when (targetTable.dieSize) {
                6 -> twoDSix.getProbability(targetRange)
                else -> getCustomTable(targetTable).getProbability(targetRange)
            }
            3 -> when (targetTable.dieSize) {
                6 -> threeDSix.getProbability(targetRange)
                else -> getCustomTable(targetTable).getProbability(targetRange)
            }
            else -> getCustomTable(targetTable).getProbability(targetRange)
        }

    private fun getCustomTable(targetTable: ProbabilityTableKey): ProbabilityTable =
        customTables.getOrElse(targetTable) {
            val newCustomTable = ProbabilityTable(targetTable.numDie, targetTable.dieSize)
            customTables[targetTable] = newCustomTable
            newCustomTable
        }
}

class ProbabilityTable(
    private val numDie: Int = 1,
    private val dieSize: Int
) {
    private val tableSize = getProbabilityTableSize(numDie, dieSize)
    private val tableRange = numDie..(dieSize * numDie)
    private val lookupTable = Array(tableSize) { false to 0.0 }

    fun getProbability(targetValue: Int): Double {
        if (targetValue !in tableRange) {
            return 0.0
        }

        val (hasBeenCalculated, result) = lookupTable[targetValue]

        if (hasBeenCalculated) {
            return result
        }

        lookupTable[targetValue] = true to findProbabilityForTargetInDicePool(targetValue, numDie)

        return lookupTable[targetValue].second
    }

    fun getProbability(targetRange: IntRange): Double =
        targetRange.map { targetValue ->
            findProbabilityForTargetInDicePool(targetValue, dieSize, numDie)
        }.reduce { accumulator, probability -> accumulator + probability }
}

fun getProbabilityTableSizeFromKey(tableData: ProbabilityTableKey): Int =
    (tableData.dieSize * tableData.numDie) - (tableData.numDie - 1)

fun getProbabilityTableSize(numDie: Int = 1, dieSize: Int): Int =
    getProbabilityTableSizeFromKey(ProbabilityTableKey(numDie = numDie, dieSize = dieSize))

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
