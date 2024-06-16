package com.team2.chitchat.data.domain.model.messages

import com.team2.chitchat.data.domain.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetMessagesModel(
    val id: String,
    val chatId: String,
    val sourceId: String,
    val message: String,
    val date: String
) : BaseModel()
