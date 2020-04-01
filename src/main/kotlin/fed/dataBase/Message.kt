package fed.dataBase

data class Message(val sender: Int, val receiver: Int, val senderNick: String, val message: String, val time: Long)