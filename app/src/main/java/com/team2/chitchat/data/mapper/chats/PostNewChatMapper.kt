package com.team2.chitchat.data.mapper.chats

import com.team2.chitchat.data.domain.model.chats.PostNewChatModel
import com.team2.chitchat.data.mapper.ResponseMapper
import com.team2.chitchat.data.repository.remote.response.chats.PostNewChatResponse

class PostNewChatMapper : ResponseMapper<PostNewChatResponse, PostNewChatModel> {
    override fun fromResponse(response: PostNewChatResponse): PostNewChatModel {
        return PostNewChatModel(
            success = response.success ?: false,
            created = response.created ?: false,
            idChat = response.chat?.id ?: ""
        )
    }
}