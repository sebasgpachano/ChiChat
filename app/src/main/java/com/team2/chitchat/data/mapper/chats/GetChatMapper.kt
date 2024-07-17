package com.team2.chitchat.data.mapper.chats

import com.team2.chitchat.data.domain.model.chats.GetChatModel
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.local.user.UserDB
import javax.inject.Inject

class GetChatMapper @Inject constructor() {

    fun getChat(chat: ChatDB, users: ArrayList<UserDB>): GetChatModel {
        val state = users.find { it.id == chat.idOtherUser }?.online ?: false
        return GetChatModel(
            id = chat.id,
            userId = chat.idOtherUser,
            name = chat.otherUserName,
            online = state,
            view = chat.view
        )
    }
}