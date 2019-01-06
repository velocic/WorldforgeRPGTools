package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer

import com.google.gson.annotations.SerializedName

class Generator(
    @SerializedName("Name")
    val name: String,

    @SerializedName("DefaultNumResultRolls")
    val defaultNumResultRolls: Int = 1,

    @SerializedName("TableEntries")
    val tableEntries: Array<TableEntries>,

    assetPath: String
) {
    @SerializedName("AssetPath")
    var assetPath = assetPath
        get() {
            val firstSlashIndex = field.indexOf("/")
            var firstPathComponent = ""

            if (firstSlashIndex != -1) {
                firstPathComponent = field.substring(0, firstSlashIndex)
            }

            if (firstPathComponent == GeneratorImporter.GENERATOR_DATA_FOLDER) {
                return "${field.substring(firstSlashIndex + 1, field.length)}/"
            }

            return "$field/"
        }

    val table
        get() = tableEntries
}