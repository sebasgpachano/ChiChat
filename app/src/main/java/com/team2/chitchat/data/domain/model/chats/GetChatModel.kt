package com.team2.chitchat.data.domain.model.chats

import com.team2.chitchat.data.domain.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetChatModel(
    val id: String,
    val userId: String,
    val name: String,
    val online: Boolean,
    val view: Boolean
) : BaseModel()