package tabletop.velocic.com.worldforgerpgtools.appcommon

import tabletop.velocic.com.worldforgerpgtools.persistence.GeneratorCategory
import tabletop.velocic.com.worldforgerpgtools.persistence.TableEntry

private const val UMBRELLA_SEARCH_MATCH_THRESHOLD = 0

fun umbrellaSearchRelatedTableEntries(partialEntryName: String, parentGeneratorName: String, startCategory: GeneratorCategory) : List<TableEntry> =
    umbrellaSearchRelatedTableEntries(partialEntryName, parentGeneratorName, startCategory, startCategory, ArrayList<GeneratorCategory>())

fun umbrellaSearchRelatedTableEntries(
    partialEntryName: String,
    parentGeneratorName: String,
    initialCategory: GeneratorCategory,
    currentCategory: GeneratorCategory,
    memo: MutableList<GeneratorCategory>
) : List<TableEntry> {
    //If memo.contains(currentCategory.categoryPath), return
    //Iterate all generators in current category
        //Iterate all tableEntry in generator.tableEntries
            //If name score > threshold, add to results
    //Iterate all child categories and recurse
    //Recurse on currentCategory.parentCategory

    val currentAlreadyProcessed = memo.find { currentCategory.assetPath == it.assetPath } != null
    if (currentAlreadyProcessed) {
        return ArrayList()
    }

    memo.add(currentCategory)
    val matchingResults = arrayListOf<TableEntry>()

    //Weight the results: matching entry > matching generator > matching category
    val categorySimilarityScore = determineSimilarityScore(initialCategory.assetPath, currentCategory.assetPath) / 4
    for (generator in currentCategory.generators) {
        val generatorSimilarityScore = determineSimilarityScore(generator.name, parentGeneratorName) / 2

        for (tableEntry in generator.table) {
            val entrySimilarityScore = determineSimilarityScore(tableEntry.name, partialEntryName)
            if (categorySimilarityScore + generatorSimilarityScore + entrySimilarityScore > UMBRELLA_SEARCH_MATCH_THRESHOLD) {
                matchingResults.add(tableEntry)
            }
        }
    }

    for (category in currentCategory.childCategories) {
        matchingResults.addAll(
            umbrellaSearchRelatedTableEntries(
                partialEntryName,
                parentGeneratorName,
                initialCategory,
                category,
                memo
            )
        )
    }

    val parentCategory = currentCategory.parent?.let { it } ?: return matchingResults

    matchingResults.addAll(
        umbrellaSearchRelatedTableEntries(
            partialEntryName,
            parentGeneratorName,
            initialCategory,
            parentCategory,
            memo
        )
    )

    return matchingResults
}

private fun determineSimilarityScore(first: String, second: String) : Int {
    return 0
}