package fed

import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

class ConversationTest {
    private fun createUser(): String = System.currentTimeMillis().toString()
    private lateinit var apiFirst: Api
    private lateinit var apiSecond: Api

    companion object {
        @BeforeClass
        @JvmStatic fun init() = Main().main()
    }

    @Test
    fun conversationTest() {
        val userFirst = createUser()
        val userSecond = createUser()

        apiFirst = Api(userFirst, "localhost")
        apiSecond = Api(userSecond, "localhost")

        val secondId = apiFirst.getUserId(userSecond)["id"].asInt
        val firstId = apiSecond.getUserId(userFirst)["id"].asInt
        val randomMessage = System.currentTimeMillis().toString()
        apiFirst.messageSend(secondId, randomMessage)


        val messages = apiSecond.getLastMessages(firstId, 0)

        Assert.assertTrue(messages.has("data") && messages["data"].asJsonArray.size() != 0)
        Assert.assertTrue(messages["data"].asJsonArray.any{ it.asJsonObject["message"].asString == randomMessage })
    }
}