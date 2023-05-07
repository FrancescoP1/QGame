package com.francesco.quizgame.validation

object PasswordValidation {

    fun isPasswordValid(pass: String): Boolean {
        return pass.length > 5
    }

    fun passwordsMatch(pass: String, passConfirmed: String): Boolean {
        return pass == passConfirmed
    }
}