package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer

import android.os.Parcel
import android.os.Parcelable

class ResultItem(
    val tableGeneratedFrom: String,
    val name: String,
    val detailData: MutableMap<String, String>
) : Parcelable {
    var quantity = 1
    val numDetailDataFields
        get() = detailData.size

    constructor(retrieved: Parcel,
        quantity: Int = retrieved.readInt(),
        tableGeneratedFrom: String = retrieved.readString(),
        name: String = retrieved.readString()
    ) : this(tableGeneratedFrom, name, mutableMapOf()) {
        this.quantity = quantity

        val numMapEntries = retrieved.readInt()

        for (i in 0 until numMapEntries) {
            val key = retrieved.readString()
            val value = retrieved.readString()

            detailData[key] = value
        }
    }

    fun addDetailDataField(fieldName: String, fieldCount: String) {
        detailData[fieldName] = fieldCount
    }

    fun getDetailDataFieldByIndex(index: Int) : Map.Entry<String, String>? {
        if (index >= detailData.size) {
            return null
        }

        var count = 0
        var result: Map.Entry<String, String>? = null

        for (entry in detailData.entries) {
            if (count < index) {
                ++count
                continue
            }

            result = entry
            break
        }

        return result
    }

    companion object {
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

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.run {
            writeInt(quantity)
            writeString(tableGeneratedFrom)
            writeString(name)
            writeInt(detailData.size)

            for (entry in detailData.entries) {
                writeString(entry.key)
                writeString(entry.value)
            }
        }
    }
}