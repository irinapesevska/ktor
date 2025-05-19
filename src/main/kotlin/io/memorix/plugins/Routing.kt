package io.memorix.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.memorix.models.User
import io.memorix.models.UserRequest
import io.memorix.models.UserResponse
import io.memorix.models.UserTable
import io.memorix.user.user
import io.memorix.utils.PasswordUtil
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        route("/users") {
            post {
                val userRequest = call.receive<UserRequest>()
                val existingUser = transaction {
                    UserTable.select { UserTable.email eq userRequest.email }.singleOrNull()
                }
                if (existingUser != null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Duplicate e-mail: ${userRequest.email}")
                    )
                    return@post
                }
                val hashedPassword = PasswordUtil.PasswordUtil.hashPassword(userRequest.password)
                transaction {
                    UserTable.insert {
                        it[name] = userRequest.name
                        it[email] = userRequest.email
                        it[password] = hashedPassword
                    }
                }
                call.respond(HttpStatusCode.Accepted)
            }
            get {
                val query = call.request.queryParameters["query"] ?: ""
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10

                // Fetch users from database
                val users = transaction {
                    UserTable.select { UserTable.name.lowerCase().like("$query%") }
                        .limit(limit)
                        .map {
                            User(
                                name = it[UserTable.name],
                                email = it[UserTable.email]
                            )
                        }
                }

                // Respond with UserResponse data class
                call.respond(UserResponse(users = users, total = users.size))

            }
        }
        user()
    }
}
