package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

public class GeneratorImporter
{
    public static final String TAG_GENERATOR_IMPORT = "GENERATOR IMPORT";

    private final String GENERATOR_DATA_FOLDER = "GeneratorData";
    private GeneratorCategory rootGeneratorCategory;

    public void importGenerators(Context context)
    {
        rootGeneratorCategory = loadGeneratorCategories(
            new GeneratorCategory("root", GENERATOR_DATA_FOLDER),
            GENERATOR_DATA_FOLDER,
            context.getAssets()
        );

        int debug = 5;
        //rootGeneratorCategory = populateGenerators(rootGeneratorCategory);
    }

    private GeneratorCategory loadGeneratorCategories(GeneratorCategory parent, String path, AssetManager assets)
    {
        try {
            String[] contents = assets.list(path);

            //Path is a non-empty directory
            if (contents.length > 0) {
                for (String item : contents) {
                    String fileExtension = item.substring(item.lastIndexOf(".") + 1);
                    String fullPath = path + "/" + item;

                    if (fileExtension.equals("json")) {
                        parent.addGeneratorJsonDataPath(fullPath);
                        continue;
                    }

                    GeneratorCategory childCategory = new GeneratorCategory(item, fullPath, parent);
                    parent.addChildCategory(loadGeneratorCategories(childCategory, fullPath, assets));
                }

                return parent;
            }

            //Path is a file (non-json, which shouldn't be here), or an empty directory. Check extension
            //to try and prevent adding invalid files as categories.
            String extension = path.substring(path.lastIndexOf(".") + 1);
            if (extension.isEmpty()) {
                String categoryName = path.substring(path.lastIndexOf("/" + 1));
                parent.addChildCategory(new GeneratorCategory(categoryName, path, parent));
            }
            return parent;

        } catch (IOException e) {
            Log.d(TAG_GENERATOR_IMPORT, "Failed to import random generator tables: " + e.getMessage());
            return parent;
        }
    }

    private GeneratorCategory populateGenerators(GeneratorCategory rootNode)
    {
        //Later optimization, lazy load JSON files as needed if loading times or
        //memory usage is too much.
        //TODO: populate generators at each level with gson
        for (GeneratorCategory child : rootNode.getChildCategories()) {
            for (String jsonDataPath : child.getGeneratorJsonDataPaths()) {
                //parse gson from path
                //populate into category object
                //put category object into child
            }

            populateGenerators(child);
        }

        return rootNode;
    }
}


