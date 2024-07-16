package com.team2.chitchat.data.domain.model.chats

import com.team2.chitchat.data.domain.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostNewChatModel(
    val success: Boolean,
    val created: Boolean,
    val idChat: String,
    val idUser: String
) : BaseModel()
