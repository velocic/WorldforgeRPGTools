package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

import java.util.ArrayList;
import java.util.List;

public class ResultRoller
{
    private GeneratorCategory rootGeneratorCategory;

    public ResultRoller(GeneratorCategory rootGeneratorCategory)
    {
        this.rootGeneratorCategory = rootGeneratorCategory;
    }

    public List<ResultItem> generateResultSet(String fullyQualifiedGeneratorPath, int numRolls)
    {
        //TODO: finish implementation
        List<ResultItem> resultSet = new ArrayList<ResultItem>();

        Generator randomTable = rootGeneratorCategory.getGeneratorFromFullPath(fullyQualifiedGeneratorPath, rootGeneratorCategory);
        int tableMaxRollValue = determineTableDiceRange(randomTable.getTable());

        return null;
    }

    private int determineTableDiceRange(TableEntries[] tableItems)
    {
        int tableMaxRoll = 1;

        for (TableEntries item : tableItems) {
            String diceRange = item.getDiceRange();
            int rangeSeparatorIndex = diceRange.indexOf("-");

            //Entry contains a single value
            if (rangeSeparatorIndex == -1) {
                int rollValue = Integer.parseInt(diceRange);

                if (rollValue > tableMaxRoll) {
                    tableMaxRoll = rollValue;
                }

                continue;
            }

            //Entry contains a range of values. Two integers hyphen-separated
            int leftVal = Integer.parseInt(diceRange.substring(0, rangeSeparatorIndex));
            int rightVal = Integer.parseInt(diceRange.substring(rangeSeparatorIndex + 1));

            if (leftVal > tableMaxRoll) {
                tableMaxRoll = leftVal;
            }

            if (rightVal > tableMaxRoll) {
                tableMaxRoll = rightVal;
            }
        }

        return tableMaxRoll;
    }
}
