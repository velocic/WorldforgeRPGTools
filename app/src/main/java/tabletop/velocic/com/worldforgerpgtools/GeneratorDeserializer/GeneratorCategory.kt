package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer

class GeneratorCategory(
    val name: String,
    assetPath: String,
    var parent: GeneratorCategory?,
    val childCategories: MutableList<GeneratorCategory>,
    val generatorJsonDataPaths: MutableList<String>,
    val generators: MutableList<Generator>
) {
    val assetPath = assetPath
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

    val numChildCategories
        get() = childCategories.size

    val numGenerators
        get() = generators.size

    constructor(name: String, assetPath: String) : this(
            name,
            assetPath,
            null,
            mutableListOf<GeneratorCategory>(),
            mutableListOf<String>(),
            mutableListOf<Generator>()
    )

    fun getGenerator(name: String) : Generator? =
        generators.find {
            it.name == name
        }

    fun getCategory(name: String) : GeneratorCategory? =
        childCategories.find {
            it.name == name
        }

    fun getGeneratorFullPath(generator: Generator) : String {
        val resultGenerator = generators.find {
            it == generator
        }

        return if (resultGenerator != null) {
            assetPath + generator.name
        } else {
            ""
        }
    }

    fun getCategoryFromFullPath(fullQualifiedPath: String, node: GeneratorCategory?) : GeneratorCategory? {
        if ((fullQualifiedPath == node?.name) || fullQualifiedPath == "") {
            return node
        }

        val slashCharIndex = fullQualifiedPath.indexOf("/")

        //Nothing left to reduce on the path, and the current node does not match somehow
        if (slashCharIndex == -1) {
            return null
        }

        val nextCategoryName = fullQualifiedPath.substring(0, slashCharIndex)
        val reducedPath = fullQualifiedPath.substring(slashCharIndex + 1)

        val nextCategory = node?.getCategory(nextCategoryName)
        return getCategoryFromFullPath(reducedPath, nextCategory)
    }

    fun getGeneratorFromFullPath(fullQualifiedPath: String, node: GeneratorCategory?) : Generator? {
        if (node == null) {
            return null
        }

        val slashCharIndex = fullQualifiedPath.indexOf("/")

        //No more path to reduce. Return the generator with the name matching fullQualifiedPath
        if (slashCharIndex == -1) {
            return node.getGenerator(fullQualifiedPath)
        }

        //Get the next generator along the path down the tree and recurse
        val nextCategoryName = fullQualifiedPath.substring(0, slashCharIndex)
        val reducedPath = fullQualifiedPath.substring(slashCharIndex + 1)

        return getGeneratorFromFullPath(reducedPath, node.getCategory(nextCategoryName))
    }
}