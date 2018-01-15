package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

public class Generator
{
    public String Name;
    public int DefaultNumResultRolls = 1;
    public TableEntries[] TableEntries;
    public String AssetPath;

    public String getName()
    {
        return Name;
    }

    public int getDefaultNumResultRolls()
    {
        return DefaultNumResultRolls;
    }

    public void setAssetPath(String assetPath)
    {
        this.AssetPath = assetPath;
    }

    public String getAssetPath()
    {
        int firstSlashIndex = AssetPath.indexOf("/");
        String firstPathComponent = "";

        if (firstSlashIndex != -1) {
            firstPathComponent = AssetPath.substring(0, firstSlashIndex);
        }

        if (firstPathComponent.equals(GeneratorImporter.GENERATOR_DATA_FOLDER)) {
            return AssetPath.substring(firstSlashIndex + 1, AssetPath.length()) + "/";
        }

        return AssetPath + "/";
    }

    public TableEntries[] getTable()
    {
        return TableEntries;
    }
}
