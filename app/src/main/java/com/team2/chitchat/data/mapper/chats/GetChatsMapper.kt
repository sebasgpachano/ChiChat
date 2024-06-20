package com.team2.chitchat.data.mapper.chats

import com.team2.chitchat.data.domain.model.chats.GetChatsModel
import com.team2.chitchat.data.mapper.ResponseMapper
import com.team2.chitchat.data.repository.remote.response.chats.GetChatsResponse

class GetChatsMapper : ResponseMapper<ArrayList<GetChatsResponse>, ArrayList<GetChatsModel>> {
    override fun fromResponse(response: ArrayList<GetChatsResponse>): ArrayList<GetChatsModel> {
        val chatModel: ArrayList<GetChatsModel> = if (response.isEmpty()) {
            ArrayList()
        } else {
            ArrayList(response.map { chatsResponse ->
                GetChatsModel(
                    id = chatsResponse.id ?: "",
                    source = chatsResponse.source ?: "",
                    target = chatsResponse.target ?: ""
                )
            })
        }
        return chatModel
    }
}