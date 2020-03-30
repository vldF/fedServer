package fed.dataBase

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*
import javax.sql.DataSource

class DataBaseFactory() {
    private var url: String
    private var password: String
    private var user: String

    init {
        val properties = Properties()
        properties.load(File("config.properties").reader())
        url = properties.getProperty("database_url")
        password = properties.getProperty("database_password")
        user = properties.getProperty("database_user")

        Database.connect(hikari())
    }

    private fun hikari(): DataSource {
        val conf = HikariConfig()
        conf.jdbcUrl = url
        conf.username = user
        conf.password = password
        conf.maximumPoolSize = 3
        conf.isAutoCommit = true
        conf.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        return HikariDataSource(conf)
    }

    suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction {
            block()
        }
    }
}