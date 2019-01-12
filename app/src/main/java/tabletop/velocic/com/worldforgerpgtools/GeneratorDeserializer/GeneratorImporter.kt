package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import java.io.File
import java.security.MessageDigest

object GeneratorImporter {
    const val TAG_GENERATOR_IMPORT = "GENERATOR IMPORT"
    const val GENERATOR_DATA_FOLDER = "GeneratorData"
    const val IMPORTER_PREFERENCES_FILE = "GeneratorImporterPrefs"

    private val rootGeneratorCategory: GeneratorCategory? = null

    fun import(context: Context) {
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
                File(subDirPathString).mkdir()
            }

            //TODO: Don't traverse further if the directory doesn't exist and we didn't add it
            contents.forEach {
                oneTimeLocalStorageCopy(context, assetManager, sharedPrefs, prefsEditor, "$currentPath/$it")
            }
        } else {
            //TODO
        }

    }


    private fun importGenerators(context: Context) {

    }
}
