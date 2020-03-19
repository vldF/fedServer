package fed.dataBase

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").autoIncrement()
    val nick = text("nick").uniqueIndex()
    val token = text("token").uniqueIndex()

    override val primaryKey = PrimaryKey(id)
}