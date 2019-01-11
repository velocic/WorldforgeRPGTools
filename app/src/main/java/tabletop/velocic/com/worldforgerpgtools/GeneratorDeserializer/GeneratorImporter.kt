package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager

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

    private fun oneTimeLocalStorageCopy(context: Context, assetManager: AssetManager, sharedPrefs: SharedPreferences, prefsEditor: SharedPreferences.Editor, currentPath: String) {

    }

    private fun importGenerators(context: Context) {

    }
}
