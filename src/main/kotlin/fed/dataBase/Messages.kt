package fed.dataBase

import org.jetbrains.exposed.sql.Table

object Messages: Table() {
    val sender = integer("sender")
    val senderNick = text("sender_nick")
    val receiver = integer("receiver")
    val message = text("message")
    val time = long("time")
}