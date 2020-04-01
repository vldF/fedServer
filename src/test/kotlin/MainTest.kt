package fed

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.util.*

internal class MainTest {
    private var serverPort = -1
    private var userOptionalId = -1
    private lateinit var userName: String
    private lateinit var userToken: String
    private lateinit var message: String
    private var req: Request

    init {
        val properties = Properties()
        properties.load(File("config.properties").reader())
        serverPort = properties.getProperty("port")?.toInt() ?: throw IllegalStateException()
        Main().main()

        req = Request(serverPort)
    }

    @Test
    fun testAll() {
        isServerAlive()
        register()
        val time = System.currentTimeMillis()
        getUserId()
        messageSendAndGetLast()
        getLast(time)
    }

    private fun isServerAlive() {
        val resp = req.doGet("ping", mapOf())
        Assert.assertNotNull(resp)
        val status = resp["status"]
        Assert.assertEquals("pong!", status.asString)
    }

    private fun register() {
        userName = generateToken()

        val resp = req.doGet("account.register", mapOf(
            "nick" to userName
        ))
        Assert.assertTrue(resp.has("status") && resp["status"].asString == "ok")
        userToken = resp["token"].asString
        Assert.assertTrue(userToken.isNotEmpty())
    }

    private fun getUserId() {
        val resp = req.doGet("users.getUserId", mapOf(
            "nick" to userName,
            "token" to userToken
        ))
        Assert.assertTrue(resp.has("id"))
        userOptionalId = resp["id"].asInt
    }

    private fun messageSendAndGetLast() {
        val rnd = generateToken()
        val respSend = req.doGet("messages.send", mapOf(
            "receiver" to userOptionalId,
            "message" to "test$rnd",
            "token" to userToken
        ))

        Assert.assertTrue(respSend.has("status") && respSend["status"].asString == "ok")

        val respGet = req.doGet("messages.get", mapOf(
            "by" to userOptionalId,
            "token" to userToken
        ))

        Assert.assertTrue(respGet.has("data"))
        Assert.assertEquals("test$rnd", respGet["data"].asJsonArray.last().asJsonObject["message"].asString)

        message = "test$rnd"
    }


    private fun getLast(lastTime: Long) {
        val resp = req.doGet("messages.getLast", mapOf(
                "receiver" to userOptionalId,
                "token" to userToken,
                "last_time" to lastTime
            ))
        Assert.assertTrue(resp.has("data"))
        Assert.assertTrue(resp["data"].asJsonArray.last().asJsonObject["message"].asString == message)
    }
}