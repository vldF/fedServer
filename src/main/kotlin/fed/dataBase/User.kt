package fed.dataBase

import com.google.gson.*
import java.lang.reflect.Type

data class User(val id: Int, val nick: String, val token: String) : JsonSerializer<User> {
    override fun serialize(src: User?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val res = JsonObject()
        res.add("id", JsonPrimitive(id))
        res.add("nick", JsonPrimitive(nick))
        res.add("token", JsonPrimitive(token))

        return res
    }

}