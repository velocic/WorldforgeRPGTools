package tabletop.velocic.com.worldforgerpgtools.appcommon

import tabletop.velocic.com.worldforgerpgtools.extensions.magnitude
import tabletop.velocic.com.worldforgerpgtools.extensions.normalize
import tabletop.velocic.com.worldforgerpgtools.extensions.minus
import tabletop.velocic.com.worldforgerpgtools.persistence.GeneratorCategory
import tabletop.velocic.com.worldforgerpgtools.persistence.TableEntry

private const val UMBRELLA_SEARCH_MATCH_THRESHOLD = 0.25 //0.0 = exact match, 2.0 = 100% different

fun umbrellaSearchRelatedTableEntries(partialEntryName: String, parentGeneratorName: String, startCategory: GeneratorCategory) : List<TableEntry> =
    umbrellaSearchRelatedTableEntries(partialEntryName, parentGeneratorName, startCategory, startCategory, ArrayList<GeneratorCategory>()).sortedWith(
        Comparator { first, second ->
            when {
                first.distance > second.distance -> 1
                first.distance < second.distance -> -1
                else -> 0
            }
        }
    ).map { it.tableEntry }

private fun umbrellaSearchRelatedTableEntries(
    partialEntryName: String,
    parentGeneratorName: String,
    initialCategory: GeneratorCategory,
    currentCategory: GeneratorCategory,
    memo: MutableList<GeneratorCategory>
) : List<SearchResult> {
    //If memo.contains(currentCategory.categoryPath), return
    //Iterate all generators in current category
        //Iterate all tableEntry in generator.tableEntries
            //If normalized baselineVector - normalized similarityVector magnitude < threshold, add to results
    //Iterate all child categories and recurse
    //Recurse on currentCategory.parentCategory

    val currentAlreadyProcessed = memo.find { currentCategory.assetPath == it.assetPath } != null
    if (currentAlreadyProcessed) {
        return ArrayList()
    }

    memo.add(currentCategory)
    val matchingResults = arrayListOf<SearchResult>()

    val categorySimilarityScore = determineSimilarityScore(initialCategory.assetPath, currentCategory.assetPath)
    for (generator in currentCategory.generators) {
        val generatorSimilarityScore = determineSimilarityScore(generator.name, parentGeneratorName)

        for (tableEntry in generator.table) {
            val entrySimilarityScore = determineSimilarityScore(tableEntry.name, partialEntryName)

            val similarityVector = listOf(categorySimilarityScore, generatorSimilarityScore, entrySimilarityScore).normalize()
            val baselineVector = listOf(1, 1, 1).normalize()
            val vectorDistance = (baselineVector - similarityVector).magnitude()

            if (vectorDistance < UMBRELLA_SEARCH_MATCH_THRESHOLD) {
                matchingResults.add(SearchResult(tableEntry, vectorDistance))
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

fun determineSimilarityScore(first: String, second: String) : Double =
    determineNormalizedLevenshteinDistance(first.toLowerCase(), second.toLowerCase())

fun determineNormalizedLevenshteinDistance(first: String, second: String) : Double {
    val maxEditDistance = maxOf(first.length, second.length).toDouble()

    if (first.isEmpty() || second.isEmpty()) {
        return 0.0
    }

    if (first == second) {
        return 1.0
    }

    val costMatrix = Array(first.length) { Array(second.length) { 0 } }
    for (i in first.indices) {
        for (j in second.indices) {
            if (first[i] != second[j]) {
                costMatrix[i][j] = 1
            }
        }
    }

    val similarityMatrix = Array(first.length + 1) { Array(second.length + 1) { 0 } }
    for (index in 1 until first.length) {
        similarityMatrix[index][0] = index
    }

    for (index in 1 until second.length) {
        similarityMatrix[0][index] = index
    }

    for (i in 1..first.length) {
        for (j in 1..second.length) {
            val hasValidCellAbove = i - 1 >= 0
            val hasValidCellLeft = j - 1 >= 0
            val hasValidCellTopLeft = hasValidCellAbove && hasValidCellLeft
            val tentativeEditDistances = mutableListOf<Int>()

            if (hasValidCellAbove) {
                tentativeEditDistances.add(similarityMatrix[i - 1][j] + 1)
            }

            if (hasValidCellLeft) {
                tentativeEditDistances.add(similarityMatrix[i][j - 1] + 1)
            }

            if (hasValidCellTopLeft) {
                tentativeEditDistances.add(similarityMatrix[i - 1][j - 1] + costMatrix[i - 1][j - 1])
            }

            similarityMatrix[i][j] = tentativeEditDistances.reduce { minimum, currentElement ->
                if (currentElement < minimum) { currentElement } else { minimum }
            }
        }
    }

    val totalEditDistance = similarityMatrix[similarityMatrix.lastIndex][similarityMatrix[0].lastIndex].toDouble()

    return 1 - (totalEditDistance / maxEditDistance)
}

private class SearchResult(val tableEntry: TableEntry, val distance: Double)
