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
    public SubTableRollRange getSubTableRollRange()
    {
        if (RerollSubTable.containsKey("ValidSubTableEntryRange")) {
            String range = RerollSubTable.get("ValidSubTableEntryRange");
            int separatorIndex = range.indexOf("-");
            String minValue = range.substring(0, separatorIndex);
            String maxValue = range.substring(separatorIndex + 1);

            return new SubTableRollRange(
                Integer.parseInt(minValue),
                Integer.parseInt(maxValue)
            );
        }

        return new SubTableRollRange(1, Integer.MAX_VALUE - 1);
    }
    public int getNumSubTableRolls()
    {
        if (RerollSubTable.containsKey("NumSubTableRolls")) {
            int numRerolls = Integer.parseInt(RerollSubTable.get("NumSubTableRolls"));
        }

        return 1;
    }
}