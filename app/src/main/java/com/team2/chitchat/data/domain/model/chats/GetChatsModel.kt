package com.team2.chitchat.data.domain.model.chats

import com.team2.chitchat.data.domain.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetChatsModel(
    val id: String,
    val source: String,
    val target: String
) : BaseModel()
