package tabletop.velocic.com.worldforgerpgtools.generatordeserializer

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.reflect.TypeToken

class ResultItem(
    val tableGeneratedFrom: String,
    val name: String,
    val detailData: MutableList<ResultItemDetail>
) : Parcelable {
    var quantity = 1
    val numDetailDataFields
        get() = detailData.size

    constructor(retrieved: Parcel,
                quantity: Int = retrieved.readInt(),
                tableGeneratedFrom: String = retrieved.readString() ?: throw IllegalArgumentException(missingArgumentMessage.format("tableGeneratedFrom")),
                name: String = retrieved.readString() ?: throw IllegalArgumentException(missingArgumentMessage.format("name"))
    ) : this(tableGeneratedFrom, name, mutableListOf()) {
        this.quantity = quantity

        val resultItemDetailListLoader = object : TypeToken<List<ResultItemDetail>>(){}::class.java.classLoader
        retrieved.readList(detailData, resultItemDetailListLoader)
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.run {
            writeInt(quantity)
            writeString(tableGeneratedFrom)
            writeString(name)
            writeList(detailData)
        }
    }

    companion object {
        private const val missingArgumentMessage = "Failed to retrieve %s from parceled ResultItem."

        @JvmField
        val CREATOR: Parcelable.Creator<ResultItem> = object : Parcelable.Creator<ResultItem> {
            override fun createFromParcel(source: Parcel): ResultItem {
                return ResultItem(source)
            }

            override fun newArray(size: Int): Array<ResultItem?> {
                return arrayOfNulls(size)
            }
        }
    }
}