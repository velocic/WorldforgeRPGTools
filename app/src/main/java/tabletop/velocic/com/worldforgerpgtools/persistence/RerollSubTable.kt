package tabletop.velocic.com.worldforgerpgtools.persistence

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import tabletop.velocic.com.worldforgerpgtools.appcommon.parcelableMissingArgumentMessage

data class RerollSubTable(
    @SerializedName("SubTableName")
    val targetTableName: String,
    @SerializedName("ValidSubTableEntryRange")
    val targetTableRange: String,
    @SerializedName("NumberOfRolls")
    private val _targetTableRollCount: Int
) : Parcelable
{
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: throw IllegalArgumentException(parcelableMissingArgumentMessage.format("targetTableName", "RerollSubTable")),
        parcel.readString() ?: throw IllegalArgumentException(parcelableMissingArgumentMessage.format("targetTableRange", "RerollSubTable")),
        parcel.readInt()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.run {
            writeString(targetTableName)
            writeString(targetTableRange)
            writeInt(targetTableRollCount)
        }
    }

    override fun describeContents(): Int = 0

    val targetTableRollCount
        get() = maxOf(1, _targetTableRollCount)

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<RerollSubTable> {
            override fun createFromParcel(source: Parcel): RerollSubTable {
                return RerollSubTable(source)
            }

            override fun newArray(size: Int): Array<RerollSubTable?> {
                return arrayOfNulls(size)
            }
        }
    }
}