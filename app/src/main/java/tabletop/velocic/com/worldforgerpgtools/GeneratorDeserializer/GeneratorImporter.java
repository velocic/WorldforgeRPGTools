package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

public class GeneratorImporter
{
    static final String TAG_GENERATOR_IMPORT = "GENERATOR IMPORT";
    final String GENERATOR_DATA_FOLDER = "GeneratorData";

    public String[] importGenerators(Context context)
    {
        String[] generators = {};
        listGeneratorAssetFiles(context);

        return generators;
    }

    private String[] listGeneratorAssetFiles(Context context)
    {
        String[] assetFiles = {};
        try {
            assetFiles = context.getAssets().list("");
        } catch (IOException e) {
            Log.d(e.getMessage(), TAG_GENERATOR_IMPORT);
        }

        return assetFiles;
    }
}
