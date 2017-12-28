package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        return generateResultSet(fullyQualifiedGeneratorPath, numRolls, 1, Integer.MAX_VALUE - 1);
    }

    public List<ResultItem> generateResultSet(String fullyQualifiedGeneratorPath, int numRolls, int minRollValue, int maxRollValue)
    {
        List<ResultItem> resultSet = new ArrayList<ResultItem>();

        Generator randomTable = rootGeneratorCategory.getGeneratorFromFullPath(fullyQualifiedGeneratorPath, rootGeneratorCategory);
        int tableMaxRollValue = determineTableDiceRange(randomTable.getTable());

        for (int i = 0; i < numRolls; ++i) {
            int actualMinRoll = minRollValue > tableMaxRollValue ? tableMaxRollValue : minRollValue;
            int actualMaxRoll = tableMaxRollValue < maxRollValue ? tableMaxRollValue : maxRollValue;
            int roll = rng.nextInt((actualMaxRoll - actualMinRoll) + 1) + minRollValue;

            for (TableEntries tableEntry : randomTable.getTable()) {
                boolean entryIsMatch = checkRollResult(roll, tableEntry);

                if (entryIsMatch) {
                    Map<String, String> rerollSubTable = tableEntry.getRerollSubTable();

                    //Some entries on random tables call for a roll on a completely different table
                    //So handle that here with a recursive call + merging the resulting sets of
                    //ResultItems together
                    if (rerollSubTable != null && rerollSubTable.isEmpty() == false) {
                        String subTableName = rerollSubTable.get("SubTableName");
                        SubTableRollRange rollRange = tableEntry.getSubTableRollRange();
                        int numSubTableRolls = tableEntry.getNumSubTableRolls();

                        List<List<ResultItem>> subTableResults = new ArrayList<List<ResultItem>>();
                        for (int rollIteration = 0; rollIteration < numSubTableRolls; ++rollIteration) {
                            subTableResults.add(
                                generateResultSet(subTableName, numSubTableRolls, rollRange.getMinRoll(), rollRange.getMaxRoll())
                            );
                        }

                        subTableResults.add(resultSet);
                        resultSet = mergeResultSets(subTableResults);

                        continue;
                    }

                    //Now the simple case. Add the new item directly, or increment an existing value.
                    ResultItem newItem = new ResultItem(fullyQualifiedGeneratorPath, tableEntry.getName(), tableEntry.getMetadata());
                    resultSet = addResultItem(resultSet, newItem);
                }
            }
        }

        return resultSet;
    }

    private List<ResultItem> addResultItem(List<ResultItem> sourceList, ResultItem newItem)
    {
        List<ResultItem> resultList = sourceList;

        for (ResultItem item : resultList) {
            if (item.getName().equals(newItem.getName())) {
                item.setQuantity(item.getQuantity() + 1);
                return resultList;
            }
        }

        resultList.add(newItem);
        return resultList;
    }

    private List<ResultItem> mergeResultSets(List<List<ResultItem>> mergeSets)
    {
        List<ResultItem> finalMergedSet = new ArrayList<ResultItem>();

        for (List<ResultItem> mergeSet : mergeSets) {
            for (ResultItem item : mergeSet) {

                boolean hadMatchInFinalMergedSet = false;
                for (ResultItem mergedItem : finalMergedSet) {
                    if (item.getName().equals(mergedItem.getName())) {
                        hadMatchInFinalMergedSet = true;
                        mergedItem.setQuantity(mergedItem.getQuantity() + 1);
                        break;
                    }
                }

                if (hadMatchInFinalMergedSet == false) {
                    finalMergedSet.add(item);
                }
            }
        }

        return finalMergedSet;
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

            return false;
        }

        //Entry contains a range of values. check if roll is between the range
        //inclusive on both ends
        int leftVal = Integer.parseInt(diceRange.substring(0, rangeSeparatorIndex));
        int rightVal = Integer.parseInt(diceRange.substring(rangeSeparatorIndex + 1));

        if (roll >= leftVal && roll <= rightVal) {
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
