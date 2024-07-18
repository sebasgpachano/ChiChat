package com.team2.chitchat.data.mapper.chats

import com.team2.chitchat.data.mapper.ResponseMapper
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.remote.response.chats.GetChatsResponse
import com.team2.chitchat.data.session.DataUserSession
import com.team2.chitchat.hilt.SimpleApplication
import javax.inject.Inject

class GetChatsMapper @Inject constructor(
    private val dataUserSession: DataUserSession
) : ResponseMapper<ArrayList<GetChatsResponse>, ArrayList<ChatDB>> {
    override fun fromResponse(response: ArrayList<GetChatsResponse>): ArrayList<ChatDB> {
        val currentUserID = dataUserSession.userId
        val chatList = ArrayList<ChatDB>()
        for (chatsResponse in response) {
            val isCurrentUserSource = chatsResponse.source == currentUserID
            val otherUserResponseId = if (isCurrentUserSource) chatsResponse.target
            else chatsResponse.source
            val otherUserResponseName = if (isCurrentUserSource) chatsResponse.targetNick
            else chatsResponse.sourceNick
            val otherUserResponseImg = if (isCurrentUserSource) chatsResponse.targetAvatar
            else chatsResponse.sourceAvatar
            val otherUserResponseOnline = if (isCurrentUserSource) chatsResponse.targetOnline
            else chatsResponse.sourceOnline

            val chatDB = ChatDB(
                id = chatsResponse.chat ?: "",
                view = true,
                otherUserOnline = otherUserResponseOnline ?: false,
                otherUserImg = otherUserResponseImg ?: "",
                idOtherUser = otherUserResponseId ?: "",
                otherUserName = otherUserResponseName ?: "",
                dateLastMessageSend = ""
            )
            chatList.add(chatDB)
        }
        return chatList
    }
}