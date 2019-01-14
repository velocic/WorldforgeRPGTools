package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.util.Base64
import java.io.File
import java.io.InputStream
import java.security.MessageDigest

object GeneratorImporter {
    const val TAG_GENERATOR_IMPORT = "GENERATOR IMPORT"
    const val GENERATOR_DATA_FOLDER = "GeneratorData"
    const val IMPORTER_PREFERENCES_FILE = "GeneratorImporterPrefs"
    private const val BUFFER_SIZE = 32768

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
            }
        }

    }

    private fun importGenerators(context: Context) {
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
