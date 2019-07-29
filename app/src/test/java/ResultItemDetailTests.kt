import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
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

    @Test
    fun resultItemDetailCollectionSerializesCorrectlyWithGson() {
        val resultItemDetails = (1..5).map { ResultItemDetail("Name$it", "Content$it") }
        val expected = "[\"{\\\"Name1\\\":\\\"Content1\\\"}\",\"{\\\"Name2\\\":\\\"Content2\\\"}\",\"{\\\"Name3\\\":\\\"Content3\\\"}\",\"{\\\"Name4\\\":\\\"Content4\\\"}\",\"{\\\"Name5\\\":\\\"Content5\\\"}\"]"
        val actual = gson.toJson(resultItemDetails)

        assertEquals(expected, actual)
    }

    @Test
    fun resultItemDetailCollectionDeserializesCorrectlyWithGson() {
        val deserializedListType = object : TypeToken<List<ResultItemDetail>>(){}.type

        val testJsonResultItemList = "[{\"Name1\":\"Content1\"},{\"Name2\":\"Content2\"},{\"Name3\":\"Content3\"},{\"Name4\":\"Content4\"},{\"Name5\":\"Content5\"}]"
        val expected = (1..5).map { ResultItemDetail("Name$it", "Content$it") }
        val actual = gson.fromJson<List<ResultItemDetail>>(testJsonResultItemList, deserializedListType)

        assertEquals(expected, actual)
    }

    companion object {
        val gson = GsonBuilder().apply {
            registerTypeAdapter(ResultItemDetail::class.java, ResultItemDetailSerializer())
            registerTypeAdapter(ResultItemDetail::class.java, ResultItemDetailDeserializer())
        }.create()
    }
}