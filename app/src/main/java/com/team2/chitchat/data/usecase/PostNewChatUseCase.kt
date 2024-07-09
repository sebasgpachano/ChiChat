package com.team2.chitchat.data.usecase

import com.team2.chitchat.data.domain.model.chats.PostNewChatModel
import com.team2.chitchat.data.repository.remote.backend.DataProvider
import com.team2.chitchat.data.repository.remote.request.chats.NewChatRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostNewChatUseCase @Inject constructor(private val dataProvider: DataProvider) {
    operator fun invoke(newChatRequest: NewChatRequest): Flow<BaseResponse<PostNewChatModel>> {
        return dataProvider.postNewChat(newChatRequest)
    }
}