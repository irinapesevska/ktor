package io.memorix.models

import org.jetbrains.exposed.sql.Table


object UserTable : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    override val primaryKey = PrimaryKey(id)
}
