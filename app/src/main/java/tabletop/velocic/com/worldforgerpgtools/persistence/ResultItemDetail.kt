package tabletop.velocic.com.worldforgerpgtools.persistence

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.*
import tabletop.velocic.com.worldforgerpgtools.appcommon.parcelableMissingArgumentMessage
import java.lang.reflect.Type

data class ResultItemDetail(var name: String, var content: String) : Parcelable
{
    constructor(parcel: Parcel) : this(
            parcel.readString() ?: throw IllegalArgumentException(parcelableMissingArgumentMessage.format("name", "ResultItemDetail")),
            parcel.readString() ?: throw IllegalArgumentException(parcelableMissingArgumentMessage.format("content", "ResultItemDetail"))
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.run {
            writeString(name)
            writeString(content)
        }
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<ResultItemDetail> {
            override fun createFromParcel(source: Parcel): ResultItemDetail =
                ResultItemDetail(source)

            override fun newArray(size: Int): Array<ResultItemDetail?> =
                arrayOfNulls(size)
        }
    }
}

class ResultItemDetailSerializer : JsonSerializer<ResultItemDetail>
{
    override fun serialize(src: ResultItemDetail?, typeOfSrc: Type?, context: JsonSerializationContext?) =
        JsonObject().apply { addProperty(src?.name, src?.content) }
}

class ResultItemDetailDeserializer : JsonDeserializer<ResultItemDetail>
{
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ResultItemDetail {
        var name = ""
        var content = ""

        val jsonData = json?.asJsonObject

        //Only one key/val per ResultItemDetail, but have to access them as
        //a collection from the jsonObject API
        jsonData?.keySet()?.forEach { jsonKey ->
            val jsonVal = jsonData.get(jsonKey)
            name = jsonKey
            content = jsonVal.asString ?: ""
        }

        return ResultItemDetail(name, content)
    }
}
