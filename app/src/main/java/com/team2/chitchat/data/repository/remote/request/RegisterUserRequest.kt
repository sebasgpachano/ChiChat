package com.team2.chitchat.data.repository.remote.request

data class RegisterUserRequest(
    val login: String,
    val password: String,
    val nick: String,
    val avatar: String,
    val platform: String,
    val uuid: String,
    val online: Boolean
)
