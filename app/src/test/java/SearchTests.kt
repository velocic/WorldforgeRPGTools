import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import tabletop.velocic.com.worldforgerpgtools.appcommon.determineSimilarityScore

@RunWith(Parameterized::class)
class SimilarityScoreTests(
    val first: String,
    val second: String,
    val expectedSimilarityScore: Double
) {
    @Test
    fun similarityScoreReturnsAccurateResults() {
        val actualSimilartyScore = determineSimilarityScore(first, second)

        assertEquals(expectedSimilarityScore, actualSimilartyScore, .01)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun mockData() =
            listOf(
                arrayOf("cat", "dog", 0.0),
                arrayOf("sandwich", "sandwich", 1.0),
                arrayOf("space", "place", 0.6),
                arrayOf("spaceship", "rocketship", .5)
            )
    }
}