package tabletop.velocic.com.worldforgerpgtools.generatordeserializer

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import tabletop.velocic.com.worldforgerpgtools.appcommon.parcelableMissingArgumentMessage

class TableEntry(
    @SerializedName("Name")
    var name: String,

    @SerializedName("Metadata")
    var metadata: List<ResultItemDetail>,

    @SerializedName("DiceRange")
    var diceRangeString: String,

    @SerializedName("RerollSubTable")
    var rerollSubTable: RerollSubTable?
) : Parcelable
{
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: throw IllegalArgumentException(parcelableMissingArgumentMessage.format("name", "TableEntry")),
        listOf(),
        "",
        null
    ) {
        val resultItemDetailListLoader = object : TypeToken<List<ResultItemDetail>>(){}::class.java.classLoader
        parcel.readList(metadata, resultItemDetailListLoader)
        diceRangeString = parcel.readString() ?: throw IllegalArgumentException(parcelableMissingArgumentMessage.format("diceRangeString", "TableEntry"))
        rerollSubTable = parcel.readParcelable(RerollSubTable::class.java.classLoader)
    }

    val diceRange
        get() = parseDiceRangeString(diceRangeString)

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.run {
            writeString(name)
            writeList(metadata)
            writeString(diceRangeString)
            writeParcelable(rerollSubTable, flags)
        }
    }

    override fun describeContents(): Int = 0

    fun copy(other: TableEntry) {
        name = other.name
        metadata = other.metadata
        diceRangeString = other.diceRangeString
        rerollSubTable = other.rerollSubTable
    }

    fun getSubTableRollRange() : IntRange {
        val nullCheckedSubTable = rerollSubTable?.let {
            it
        } ?: return 1 until Int.MAX_VALUE

        return parseDiceRangeString(nullCheckedSubTable.targetTableRange)
    }

    fun getNumSubTableRolls() : Int
    {
        val nullCheckedSubTable = rerollSubTable?.let {
            it
        } ?: return 0

        return nullCheckedSubTable.targetTableRollCount
    }

    private fun parseDiceRangeString(diceRangeString: String?) : IntRange {
        val separatorIndex = diceRangeString?.indexOf("-") ?: -1

        val (left, right) = if (separatorIndex == -1) {
            val singleItemRange = diceRangeString?.toInt() ?: 1
            singleItemRange to singleItemRange
        } else {
            Pair(
                diceRangeString?.substring(0, separatorIndex)?.toInt() ?: 1,
                diceRangeString?.substring(separatorIndex + 1)?.toInt() ?: Int.MAX_VALUE - 1
            )
        }

        val (min, max) = if (left < right) {
            left to right
        } else {
            right to left
        }

        return min..max
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<TableEntry>
        {
            override fun createFromParcel(source: Parcel): TableEntry =
                TableEntry(source)

            override fun newArray(size: Int): Array<TableEntry?> =
                arrayOfNulls(size)
        }
    }
}