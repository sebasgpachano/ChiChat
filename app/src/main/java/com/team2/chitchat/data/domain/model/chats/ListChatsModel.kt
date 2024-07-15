package com.team2.chitchat.data.domain.model.chats

import com.team2.chitchat.data.domain.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListChatsModel(
    val id: String,
    val name: String,
    val image: String,
    val state: Boolean,
    val notification: Int,
    val lastMessage: String,
    val date: String,
    var view: Boolean
) : BaseModel()
