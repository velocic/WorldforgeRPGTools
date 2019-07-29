package tabletop.velocic.com.worldforgerpgtools.generatordeserializer

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

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
        parcel.readString() ?: throw IllegalArgumentException(missingArgumentMessage.format("targetTableName")),
        parcel.readString() ?: throw IllegalArgumentException(missingArgumentMessage.format("targetTableRange")),
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

    val targetTableRollCount = maxOf(1, _targetTableRollCount)

    companion object {
        private const val missingArgumentMessage = "Failed to retrieve %s from parceled RerollSubTable."

        @JvmField
        val CREATOR: Parcelable.Creator<RerollSubTable> = object : Parcelable.Creator<RerollSubTable> {
            override fun createFromParcel(source: Parcel): RerollSubTable {
                return RerollSubTable(source)
            }

            override fun newArray(size: Int): Array<RerollSubTable?> {
                return arrayOfNulls(size)
            }
        }
    }
}