package tabletop.velocic.com.worldforgerpgtools.persistence

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import tabletop.velocic.com.worldforgerpgtools.appcommon.parcelableMissingArgumentMessage

class Generator(
    @SerializedName("Name")
    var name: String,

    @SerializedName("DefaultNumResultRolls")
    var defaultNumResultRolls: Int = 1,

    @SerializedName("TableEntries")
    var table: List<TableEntry>,
    assetPath: String
) : Parcelable
{
    var assetPath = assetPath
        get() {
            val firstSlashIndex = field.indexOf("/")
            var firstPathComponent = ""

            if (firstSlashIndex != -1) {
                firstPathComponent = field.substring(0, firstSlashIndex)
            }

            if (firstPathComponent == GeneratorPersister.GENERATOR_DATA_FOLDER) {
                val resultPath = field.substring(firstSlashIndex + 1, field.length)
                return if (resultPath.last() == '/') { resultPath } else { "$resultPath/" }
            }

            return if (field.last() == '/') { field } else { "$field/" }
        }
    val parent: GeneratorCategory?
        get() = GeneratorPersister.rootGeneratorCategory?.let {
            it.getCategoryFromFullPath(assetPath, it)
        }

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: throw IllegalArgumentException(parcelableMissingArgumentMessage.format("name", "Generator")),
        parcel.readInt(),
        listOf<TableEntry>(),
        ""
    ) {
        val tableClassLoader = object : TypeToken<List<TableEntry>>(){}::class.java.classLoader
        parcel.readList(table, tableClassLoader)
        assetPath = parcel.readString() ?: throw IllegalArgumentException(parcelableMissingArgumentMessage.format("assetPath", "Generator"))
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.run {
            writeString(name)
            writeInt(defaultNumResultRolls)
            writeList(table)
            writeString(assetPath)
        }
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Generator>
        {
            override fun createFromParcel(source: Parcel): Generator =
                Generator(source)

            override fun newArray(size: Int): Array<Generator?> =
                arrayOfNulls(size)
        }
    }
}