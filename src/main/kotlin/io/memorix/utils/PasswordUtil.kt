package io.memorix.utils

import at.favre.lib.crypto.bcrypt.BCrypt

class PasswordUtil {

    object PasswordUtil {
        fun hashPassword(password: String): String {
            return BCrypt.withDefaults().hashToString(12, password.toCharArray())
        }
    }
}