package tabletop.velocic.com.worldforgerpgtools.generatordeserializer

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.*
import java.lang.reflect.Type

data class ResultItemDetail(var name: String, var content: String) : Parcelable
{
    constructor(parcel: Parcel) : this(
            parcel.readString() ?: throw IllegalArgumentException(missingArgumentMessage.format("name")),
            parcel.readString() ?: throw IllegalArgumentException(missingArgumentMessage.format("content"))
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.run {
            writeString(name)
            writeString(content)
        }
    }

    override fun describeContents(): Int = 0

    companion object {
        private const val missingArgumentMessage = "Failed to retrieve %s from parceled ResultItemDetail."

        @JvmField
        val CREATOR: Parcelable.Creator<ResultItemDetail> = object : Parcelable.Creator<ResultItemDetail> {
            override fun createFromParcel(source: Parcel): ResultItemDetail {
                return ResultItemDetail(source)
            }

            override fun newArray(size: Int): Array<ResultItemDetail?> {
                return arrayOfNulls(size)
            }
        }
    }
}

class ResultItemDetailSerializer : JsonSerializer<ResultItemDetail>
{
    override fun serialize(src: ResultItemDetail?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val jsonResultItem = JsonObject()
        jsonResultItem.addProperty(src?.name, src?.content)

        return JsonPrimitive(jsonResultItem.toString())
    }
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
