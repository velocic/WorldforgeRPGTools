package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GeneratorImporter
{
    public static final String TAG_GENERATOR_IMPORT = "GENERATOR IMPORT";
    public static final String GENERATOR_DATA_FOLDER = "GeneratorData";
    public static final String IMPORTER_PREFERENCES_FILE = "GeneratorImporterPrefs";

    private static GeneratorImporter importerInstance;
    private GeneratorCategory rootGeneratorCategory;

    private GeneratorImporter() {}

    public static GeneratorImporter getInstance(Context context)
    {
        if (importerInstance == null) {
            importerInstance = new GeneratorImporter();

            AssetManager assetManager = context.getAssets();
            SharedPreferences sharedPrefs = context.getSharedPreferences(IMPORTER_PREFERENCES_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
            importerInstance.oneTimeLocalStorageCopy(context, assetManager, sharedPrefs, sharedPrefsEditor, GENERATOR_DATA_FOLDER);
            sharedPrefsEditor.apply();

            importerInstance.importGenerators(context);
        }

        return importerInstance;
    }

    private void oneTimeLocalStorageCopy(Context context, AssetManager assets, SharedPreferences sharedPrefs, SharedPreferences.Editor sharedPrefsEditor, String currentPath)
    {
        try {
            String[] contents = assets.list(currentPath);
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            //Path is a non-empty directory
            if (contents.length > 0) {
                byte[] hash = md5.digest(currentPath.getBytes());
                String encodedHash = Base64.encodeToString(hash, Base64.DEFAULT);
                boolean hasBeenCopied = sharedPrefs.getBoolean(encodedHash, false);

                int extensionIndex = currentPath.lastIndexOf(".");

                String extension = "";

                if (extensionIndex != -1) {
                    extension = currentPath.substring(extensionIndex + 1);
                }

                if (hasBeenCopied == false && extension == "") {
                    sharedPrefsEditor.putBoolean(encodedHash, true);

                    String subDirPathString = context.getFilesDir() + "/" + currentPath;

                    //Create subdirectory (getDir can only create at the top-level)
                    File subDirectory = new File(subDirPathString);
                    subDirectory.mkdir();
                }

                //TODO: Don't traverse further if the directory doesn't exist and we didn't add it

                for (String item : contents) {
                    oneTimeLocalStorageCopy(context, assets, sharedPrefs, sharedPrefsEditor, currentPath + "/" + item);
                }
            } else {
                //Path is a file

                //Read the source file from assets & hash the file contents
                InputStream sourceStream = assets.open(currentPath);
                String encodedHash = createChecksum(sourceStream, "MD5");
                sourceStream.close();

                boolean hasBeenCopied = sharedPrefs.getBoolean(encodedHash, false);

                if (hasBeenCopied == false) {
                    sharedPrefsEditor.putBoolean(encodedHash, true);

                    //Reopen the stream to copy the file to the new destination
                    sourceStream = assets.open(currentPath);

                    //Create a new file in internal storage
                    String newFilePath = context.getFilesDir() + "/" + currentPath;
                    File file = new File(newFilePath);
                    FileOutputStream outputStream;

                    byte[] buffer = new byte[32768];

                    if (file.exists() == false) {
                        file.createNewFile();
                    }

                    outputStream = new FileOutputStream(file);

                    int bytesRead = 0;
                    while ((bytesRead = sourceStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.d(TAG_GENERATOR_IMPORT, "Failed to one-time copy pre-made tables from assets directory to internal storage: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG_GENERATOR_IMPORT, e.getMessage());
        }
    }

    private String createChecksum(InputStream input, String digestType)
    {
        MessageDigest digest = null;
        String checksum = "";
        int numRead = 0;

        try {
            digest = MessageDigest.getInstance(digestType);
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG_GENERATOR_IMPORT, e.getMessage());
        }

        if (digest == null) {
            return checksum;
        }

        byte[] buffer = new byte[32768];

        try {
            do {
                numRead = input.read(buffer);
                if (numRead > 0) {
                    digest.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

        } catch (IOException e) {
            Log.d(TAG_GENERATOR_IMPORT, "Failed to calculate checksum: " + e.getMessage());
        }

        checksum = Base64.encodeToString(digest.digest(), Base64.DEFAULT);

        return checksum;
    }

    public void importGenerators(Context context)
    {
        AssetManager assetManager = context.getAssets();

        rootGeneratorCategory = loadGeneratorCategories(
            new GeneratorCategory("root", GENERATOR_DATA_FOLDER),
            GENERATOR_DATA_FOLDER,
            assetManager
        );

        rootGeneratorCategory = populateGenerators(rootGeneratorCategory, assetManager);
    }

    private GeneratorCategory loadGeneratorCategories(GeneratorCategory parent, File internalStorageDirectory)
    {
        File[] items = internalStorageDirectory.listFiles();

        if (items.length > 0) {
            for (File item : items) {
                String fullPath = item.getAbsolutePath();
                String fileExtension = fullPath.substring(fullPath.lastIndexOf(".") + 1);

                if (item.isDirectory()) {
                    if (fileExtension.equals("json")) {
                        parent.addGeneratorJsonDataPath(fullPath);
                        continue;
                    }

                    GeneratorCategory childCategory = new GeneratorCategory(item.getName(), fullPath, parent);
                    parent.addChildCategory(loadGeneratorCategories(childCategory, item));
                }
            }

            return parent;
        }

        String fullPath = internalStorageDirectory.getAbsolutePath();
        String fileExtension = fullPath.substring(fullPath.lastIndexOf(".") + 1);

        if (fileExtension.isEmpty()) {
            String categoryName = fullPath.substring(fullPath.lastIndexOf(".") + 1);
            parent.addChildCategory(new GeneratorCategory(categoryName, fullPath, parent));
        }

        return parent;
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

    private GeneratorCategory populateGenerators(GeneratorCategory rootNode, AssetManager assets)
    {
        //TODO: Later optimization, lazy load JSON files as needed if loading times or
        //memory usage is too much.
        Gson gson = new Gson();
        for (GeneratorCategory child : rootNode.getChildCategories()) {
            for (String jsonDataPath : child.getGeneratorJsonDataPaths()) {
                try {
                    InputStream jsonInputStream = assets.open(jsonDataPath);
                    Reader jsonInputReader = new InputStreamReader(jsonInputStream);

                    Generator generator = gson.fromJson(jsonInputReader, Generator.class);
                    generator.setAssetPath(jsonDataPath);
                    child.addGenerator(generator);
                } catch (IOException e) {
                    Log.d(TAG_GENERATOR_IMPORT, "Failed to deserialize " + jsonDataPath + ": " + e.getMessage());
                    continue;
                } catch (JsonSyntaxException e) {
                    Log.d(TAG_GENERATOR_IMPORT, "Invalid JSON syntax in " + jsonDataPath + ": " + e.getMessage());
                    continue;
                }
            }

            populateGenerators(child, assets);
        }

        return rootNode;
    }

    public GeneratorCategory getRootGeneratorCategory()
    {
        return rootGeneratorCategory;
    }
}
