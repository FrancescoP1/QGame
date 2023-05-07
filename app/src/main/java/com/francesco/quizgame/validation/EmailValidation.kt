package com.francesco.quizgame.validation

object EmailValidation {
    private val emailRegex =
        Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+\$")

    fun isEmailValid(email: String): Boolean {
        return email.matches(emailRegex)
    }
}