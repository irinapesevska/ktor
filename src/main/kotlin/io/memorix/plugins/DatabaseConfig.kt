package io.memorix.plugins

import io.ktor.server.application.*
import io.memorix.models.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

fun Application.configureDatabase() {
    // Initialize the database connection (PostgreSQL example)
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/ktor",
        driver = "org.postgresql.Driver",
        user = "root",
        password = "postgres"
    )

    // Set the transaction isolation level (optional)
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    transaction {
        SchemaUtils.create(UserTable)
    }
}