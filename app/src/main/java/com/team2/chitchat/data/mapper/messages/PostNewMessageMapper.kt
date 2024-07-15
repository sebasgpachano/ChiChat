package com.team2.chitchat.data.mapper.messages

import com.team2.chitchat.data.domain.model.messages.PostNewMessageModel
import com.team2.chitchat.data.mapper.ResponseMapper
import com.team2.chitchat.data.repository.remote.response.messages.PostNewMessageResponse

class PostNewMessageMapper : ResponseMapper<PostNewMessageResponse, PostNewMessageModel> {
    override fun fromResponse(response: PostNewMessageResponse): PostNewMessageModel {
        return PostNewMessageModel(
            success = response.success ?: false
        )
    }
}