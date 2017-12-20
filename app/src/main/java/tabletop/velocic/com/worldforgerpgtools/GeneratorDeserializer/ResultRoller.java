package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ResultRoller
{
    private GeneratorCategory rootGeneratorCategory;
    private Random rng;

    public ResultRoller(GeneratorCategory rootGeneratorCategory)
    {
        this.rootGeneratorCategory = rootGeneratorCategory;
        this.rng = new Random();
    }

    public List<ResultItem> generateResultSet(String fullyQualifiedGeneratorPath, int numRolls)
    {
        //TODO: finish implementation
        List<ResultItem> resultSet = new ArrayList<ResultItem>();

        Generator randomTable = rootGeneratorCategory.getGeneratorFromFullPath(fullyQualifiedGeneratorPath, rootGeneratorCategory);
        int tableMaxRollValue = determineTableDiceRange(randomTable.getTable());

        for (int i = 0; i < numRolls; ++i) {
            int roll = 1 + rng.nextInt(tableMaxRollValue);

            for (TableEntries tableEntry : randomTable.getTable()) {
                boolean entryIsMatch = checkRollResult(roll, );

                if (entryIsMatch) {
                    //if entry has a SubtableReroll
                        //recursively call this function, with override for
                        //min and max roll, and pass the fully qualified path to
                        //the generator specified in SubtableReroll

                    //If a similar entry exists in resultSet, add 1 to its' count
                    //of matches

                    //otherwise, insert entry into the result set
                }
            }
        }

        return null;
    }

    private boolean checkRollResult(int roll, TableEntries tableEntry)
    {
        String diceRange = tableEntry.getDiceRange();
        int rangeSeparatorIndex = diceRange.indexOf("-");

        //Entry contains a single value
        if (rangeSeparatorIndex == -1) {
            int targetValue = Integer.parseInt(diceRange);

            if (roll == targetValue) {
                return true;
            }
        }

        //Entry contains a range of values. check if roll is between the range
        //inclusive on both ends
        int leftVal = Integer.parseInt(diceRange.substring(0, rangeSeparatorIndex));
        int rightVal = Integer.parseInt(diceRange.substring(rangeSeparatorIndex + 1));

        if (roll >= leftVal || roll <= rightVal) {
            return true;
        }

        return false;
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
