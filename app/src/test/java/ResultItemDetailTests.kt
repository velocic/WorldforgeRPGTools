import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Test
import tabletop.velocic.com.worldforgerpgtools.generatordeserializer.ResultItemDetail
import tabletop.velocic.com.worldforgerpgtools.generatordeserializer.ResultItemDetailDeserializer
import tabletop.velocic.com.worldforgerpgtools.generatordeserializer.ResultItemDetailSerializer

class ResultItemDetailTests {
    @Test
    fun resultItemDetailSerializesCorrectlyWithGson() {
        val testResultItem = ResultItemDetail("Test Name", "Test Content")
        val actual = gson.toJson(testResultItem)

        //Without the extra escapes necessary here, output looks like:
        //"{\"Test Name\":\"Test Content\"}"
        val expected = "\"{\\\"Test Name\\\":\\\"Test Content\\\"}\""

        assertEquals(expected, actual)
    }

    @Test
    fun resultItemDetailDeserializesCorrectlyWithGson() {
        val testJsonResultItem = "{\"Test Name\":\"Test Content\"}"
        val expected = ResultItemDetail("Test Name", "Test Content")
        val actual = gson.fromJson(testJsonResultItem, ResultItemDetail::class.java)

        assertEquals(expected, actual)
    }

    companion object {
        val gson = GsonBuilder().apply {
            registerTypeAdapter(ResultItemDetail::class.java, ResultItemDetailSerializer())
            registerTypeAdapter(ResultItemDetail::class.java, ResultItemDetailDeserializer())
        }.create()
    }
}