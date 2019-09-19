import org.junit.Assert.assertEquals
import org.junit.Test
import tabletop.velocic.com.worldforgerpgtools.extensions.magnitude
import tabletop.velocic.com.worldforgerpgtools.extensions.minus
import tabletop.velocic.com.worldforgerpgtools.extensions.normalize
import kotlin.math.sqrt

class ListVectorMathTests {
    @Test
    fun vectorIntSubtraction() {
        val a = listOf(3, 5, 2)
        val b = listOf(2, 8, 1)

        val actual = a - b

        assertEquals(listOf(1, -3, 1), actual)
    }

    @Test
    fun vectorDoubleSubtraction() {
        val a = listOf(3.1, 5.2, 2.3)
        val b = listOf(2.0, 8.0, 1.0)

        val expected = listOf(1.1, -2.8, 1.3)
        val actual = a - b

        for (i in a.indices) {
            assertEquals(expected[i], actual[i], .01)
        }
    }

    @Test
    fun vectorIntMagnitude() {
        val input = listOf(1, 2, 3)

        val expected = sqrt(14.0)
        val actual = input.magnitude()

        assertEquals(expected, actual, .01)
    }

    @Test
    fun vectorDoubleMagnitude() {
        val input = listOf(2.0, 3.0, 4.0)

        val expected = sqrt(29.0)
        val actual = input.magnitude()

        assertEquals(expected, actual, .01)
    }

    @Test
    fun vectorIntNormalization() {
        val input = listOf(3, 2, 1)

        val expected = listOf(
            3 / sqrt(14.0),
            sqrt(2.0 / 7.0),
            1 / sqrt(14.0)
        )
        val actual = input.normalize()

        for (i in expected.indices) {
            assertEquals(expected[i], actual[i], .01)
        }
    }

    @Test
    fun vectorDoubleNormalization() {
        val input = listOf(5.0, -4.0, -2.0)

        val expected = listOf(0.7454, -0.5963, -0.29814)
        val actual = input.normalize()

        for (i in expected.indices) {
            assertEquals(expected[i], actual[i], .01)
        }
    }
}