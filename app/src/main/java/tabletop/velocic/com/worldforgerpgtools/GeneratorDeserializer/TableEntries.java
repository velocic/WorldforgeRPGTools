package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

import java.util.Map;

public class TableEntries
{
    public String Name;
    public Map<String, String> Metadata;
    public String DiceRange;
    public Map<String, String> RerollSubTable;

    public String getName()
    {
        return Name;
    }

    public Map<String, String> getMetadata()
    {
        return Metadata;
    }

    public String getDiceRange()
    {
        return DiceRange;
    }
    public Map<String, String> getRerollSubTable()
    {
        return RerollSubTable;
    }
}