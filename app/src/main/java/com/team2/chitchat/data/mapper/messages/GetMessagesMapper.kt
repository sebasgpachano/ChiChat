package com.team2.chitchat.data.mapper.messages

import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.data.mapper.ResponseMapper
import com.team2.chitchat.data.repository.remote.response.messages.GetMessagesResponse

class GetMessagesMapper :
    ResponseMapper<ArrayList<GetMessagesResponse>, ArrayList<GetMessagesModel>> {
    override fun fromResponse(response: ArrayList<GetMessagesResponse>): ArrayList<GetMessagesModel> {
        val messagesModel: ArrayList<GetMessagesModel> = if (response.isEmpty()) {
            ArrayList()
        } else {
            ArrayList(response.map { messagesResponse ->
                GetMessagesModel(
                    id = messagesResponse.id ?: "",
                    chatId = messagesResponse.chat ?: "",
                    sourceId = messagesResponse.source ?: "",
                    message = messagesResponse.message ?: "",
                    date = messagesResponse.date ?: ""
                )
            })
        }
        return messagesModel
    }
}