package com.team2.chitchat.data.mapper.chats

import com.team2.chitchat.data.domain.model.chats.GetChatModel
import com.team2.chitchat.data.repository.local.chat.ChatDB
import javax.inject.Inject

class GetChatMapper @Inject constructor() {
    fun getChat(chat: ChatDB): GetChatModel {
        return GetChatModel(
            id = chat.id,
            userId = chat.idOtherUser,
            name = chat.otherUserName,
            online = chat.otherUserOnline
        )
    }
}