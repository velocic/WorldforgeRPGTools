package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.*
import java.security.MessageDigest

object GeneratorImporter {
    const val GENERATOR_DATA_FOLDER = "GeneratorData"
    private const val IMPORTER_PREFERENCES_FILE = "GeneratorImporterPrefs"
    private const val TAG_GENERATOR_IMPORT = "GENERATOR IMPORT"
    private const val BUFFER_SIZE = 32768

    @JvmStatic
    var rootGeneratorCategory: GeneratorCategory? = null
        private set

    fun import(context: Context?) {

        if (context == null) {
            Log.d(TAG_GENERATOR_IMPORT, "Received a null context while attempting to import data from the filesystem; aborting.")
            return
        }

        val assetManager = context.assets
        val sharedPrefs = context.getSharedPreferences(IMPORTER_PREFERENCES_FILE, Context.MODE_PRIVATE)
        val prefsEditor = sharedPrefs.edit()
        oneTimeLocalStorageCopy(context, assetManager, sharedPrefs, prefsEditor, GENERATOR_DATA_FOLDER)
        prefsEditor.apply()

        importGenerators(context)
    }

    private fun oneTimeLocalStorageCopy(
        context: Context,
        assetManager: AssetManager,
        sharedPrefs: SharedPreferences,
        prefsEditor: SharedPreferences.Editor,
        currentPath: String
    ) {
        val contents = assetManager.list(currentPath)
        val messageDigest = MessageDigest.getInstance("MD5")

        if (contents.isNotEmpty()) {
            val encodedHash = messageDigest.digest(currentPath.toByteArray()).toString()
            val hasBeenCopied = sharedPrefs.getBoolean(encodedHash, false)

            val extension = currentPath.let {
                val extensionIndex = it.lastIndexOf('.')
                if (extensionIndex != -1) it.substring(extensionIndex + 1) else ""
            }

            //Target to copy is a directory (all data files can be assumed to have an extension in this app)
            if (!hasBeenCopied && extension == "") {
                prefsEditor.putBoolean(encodedHash, true)
                val subDirPathString = "${context.filesDir}/$currentPath"

                try {
                    File(subDirPathString).mkdir()
                } catch (e: IOException) {
                    Log.d(TAG_GENERATOR_IMPORT, "Failed to one-time copy premade tables from assets directory to internal storage: ${e.message}")
                    return
                }
            }

            //TODO: Don't traverse further if the directory doesn't exist and we didn't add it
            contents.forEach {
                oneTimeLocalStorageCopy(context, assetManager, sharedPrefs, prefsEditor, "$currentPath/$it")
            }
        } else {
            //Path is a file

            //Read the source file from assets & hash the file contents
            var sourceStream = assetManager.open(currentPath)
            val encodedHash = createChecksum(sourceStream, "MD5")
            val hasBeenCopied = sharedPrefs.getBoolean(encodedHash, true)

            if (!hasBeenCopied) {
                prefsEditor.putBoolean(encodedHash, true)

                //Reopen the stream to copy the file to the new destination
                sourceStream = assetManager.open(currentPath)

                //Create a new file in internal storage
                val file = File("${context.filesDir}/$currentPath")
                val buffer = ByteArray(BUFFER_SIZE)

                if (!file.exists()) {
                    try {
                        file.createNewFile()
                    } catch (e: IOException) {
                        Log.d(TAG_GENERATOR_IMPORT, "Failed to copy $currentPath from assets directory into internal storage: ${e.message}")
                    }
                }

                val outputStream = FileOutputStream(file)
                var bytesRead: Int

                do {
                    bytesRead = sourceStream.read(buffer)
                    outputStream.write(buffer, 0, bytesRead)
                } while (bytesRead > 0)

                outputStream.close()
            }
        }
    }

    private fun importGenerators(context: Context) {
        val assetManager = context.assets


        val rootGeneratorCategory = loadGeneratorCategories(
            GeneratorCategory("root", GENERATOR_DATA_FOLDER),
            File("${context.filesDir}/$GENERATOR_DATA_FOLDER")
        )

        this.rootGeneratorCategory = populateGenerators(rootGeneratorCategory, assetManager)
    }

    private fun loadGeneratorCategories(parent: GeneratorCategory, internalStorageTarget: File) : GeneratorCategory {
        val files = internalStorageTarget.listFiles()

        if (files.isNotEmpty()) {
            for (file in files) {
                //Truncate system directories out of the path. Full path begins from the contextual
                //root of our data files (GENERATOR_DATA_FOLDER)
                val rawPath = file.absolutePath
                val generatorDataIndex = rawPath.indexOf(GENERATOR_DATA_FOLDER)

                val fullPath = rawPath.substring(generatorDataIndex)
                val fileExtension = file.extension

                if (fileExtension == "json") {
                    parent.generatorJsonDataPaths.add(fullPath)
                    continue;
                }

                val childCategory = GeneratorCategory(file.name, fullPath, parent)
                parent.childCategories.add(loadGeneratorCategories(childCategory, file))
            }

            return parent
        }

        val rawPath = internalStorageTarget.absolutePath
        val generatorDataIndex = rawPath.indexOf(GENERATOR_DATA_FOLDER)

        val fullPath = rawPath.substring(generatorDataIndex)
        val fileExtension = internalStorageTarget.extension

        if (fileExtension.isEmpty()) {
            val categoryName = fullPath.substringAfterLast(".")
            parent.childCategories.add(GeneratorCategory(categoryName, fullPath, parent))
        }

        return parent
    }

    private fun populateGenerators(rootNode: GeneratorCategory, assets: AssetManager) : GeneratorCategory {
        val gson = Gson()

        for (child in rootNode.childCategories) {
            for (jsonDataPath in child.generatorJsonDataPaths) {
                try {
                    val jsonInputReader = InputStreamReader(assets.open(jsonDataPath))
                    val generator = gson.fromJson(jsonInputReader, Generator::class.java)

                    generator.assetPath = jsonDataPath
                    child.generators.add(generator)
                } catch (e: IOException) {
                    Log.d(TAG_GENERATOR_IMPORT, "Failed to deserialize $jsonDataPath: ${e.message}")
                    continue
                } catch (e: JsonSyntaxException) {
                    Log.d(TAG_GENERATOR_IMPORT, "Invalid JSON syntax in $jsonDataPath: ${e.message}")
                    continue
                }
            }

            populateGenerators(child, assets)
        }

        return rootNode
    }

    private fun createChecksum(input: InputStream, digestType: String) : String {
        val digest = MessageDigest.getInstance(digestType) ?: return ""
        var numBytesRead: Int
        val buffer = ByteArray(BUFFER_SIZE)

        do {
            numBytesRead = input.read(buffer)

            if (numBytesRead > 0) {
                digest.update(buffer, 0, numBytesRead)
            }
        } while (numBytesRead != -1)

        return Base64.encodeToString(digest.digest(), Base64.DEFAULT)
    }
}
