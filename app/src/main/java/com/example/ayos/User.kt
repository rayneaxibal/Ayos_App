package com.example.ayos

data class User(
    val name: String = "",
    val phone: String = "",
    val role: String = "",
    val password: String = "",
    val inviteCode: String? = null
)

