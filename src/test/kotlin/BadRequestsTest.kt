package fed

import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

class BadRequestsTest {
    private val req = Request()

    companion object {
        @BeforeClass
        @JvmStatic fun init() = Main().main()
    }

    @Test
    fun registration() {
        var resp = req.doGet("account.register", mapOf("nick" to "vldf"))  // vldf - already exist nick
        Assert.assertTrue(resp["error"].asBoolean)

        resp = req.doGet("account.register", mapOf())  // vldf - already exist nick
        Assert.assertTrue(resp["error"].asBoolean)
        Assert.assertEquals("Parameter nick is incorrect", resp["description"].asString)
    }

    @Test
    fun getLastMessages() {
        var resp = req.doGet("messages.getLast", mapOf())
        Assert.assertTrue(resp["error"].asBoolean)

        resp = req.doGet("messages.getLast", mapOf(
            "receiver" to 1,
            "token" to "random token",
            "last_time" to 0
        ))
        Assert.assertTrue(resp["error"].asBoolean)
    }

    @Test
    fun getMessages() {
        var resp = req.doGet("messages.get", mapOf())
        Assert.assertTrue(resp["error"].asBoolean)

        resp = req.doGet("messages.get", mapOf(
            "receiver" to 1,
            "token" to "random token"
        ))
        Assert.assertTrue(resp["error"].asBoolean)
    }

    @Test
    fun sendMessages() {
        var resp = req.doGet("messages.send", mapOf())
        Assert.assertTrue(resp["error"].asBoolean)

        resp = req.doGet("messages.send", mapOf(
            "receiver" to 1,
            "token" to "random token"
        ))
        Assert.assertTrue(resp["error"].asBoolean)
    }

    @Test
    fun getUserId() {
        var resp = req.doGet("users.getUserId", mapOf())
        Assert.assertTrue(resp["error"].asBoolean)

        resp = req.doGet("users.getUserId", mapOf(
            "nick" to "user",
            "token" to "random token"
        ))
        Assert.assertTrue(resp["error"].asBoolean)

        resp = req.doGet("users.getUserId", mapOf(
            "nick" to "user",  // nick `user` doesn't exist
            "token" to "WSAMHWNCFDHSRNOATGJBKYKQJGPJGHAN"
        ))
        Assert.assertTrue(resp["error"].asBoolean)
    }
}