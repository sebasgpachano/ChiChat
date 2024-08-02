package com.team2.chitchat.data.mapper.messages

import com.team2.chitchat.data.mapper.ResponseMapper
import com.team2.chitchat.data.repository.local.message.MessageDB
import com.team2.chitchat.data.repository.remote.response.messages.GetMessagesResponse

class GetMessagesMapper :
    ResponseMapper<ArrayList<GetMessagesResponse>, ArrayList<MessageDB>> {
    override fun fromResponse(response: ArrayList<GetMessagesResponse>): ArrayList<MessageDB> {
        val messages: ArrayList<MessageDB> = if (response.isEmpty()) {
            ArrayList()
        } else {
            ArrayList(response.map { messagesResponse ->
                MessageDB(
                    id = messagesResponse.id ?: "",
                    chatId = messagesResponse.chat ?: "",
                    sourceId = messagesResponse.source ?: "",
                    message = messagesResponse.message ?: "",
                    date = messagesResponse.date ?: "",
                    view = false,
                    notified = false,
                )
            })
        }
        return messages
    }
}