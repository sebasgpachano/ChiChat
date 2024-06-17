package com.team2.chitchat.data.domain.model.users

import com.team2.chitchat.data.domain.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetUserModel(
    val login: String = "",
    val nick: String = "",
    val avatar: String = "",
    val token: String = "",
    val online: Boolean = false,
    val created: String = "",
    val updated: String = ""
) : BaseModel()
