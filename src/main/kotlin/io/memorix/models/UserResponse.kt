package io.memorix.models

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val users: List<User>,
    val total: Int
)

@Serializable
data class UserRes(
    val name: String,
    val email: String
)