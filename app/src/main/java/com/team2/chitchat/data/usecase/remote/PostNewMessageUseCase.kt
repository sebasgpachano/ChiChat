package com.team2.chitchat.data.usecase.remote

import com.team2.chitchat.data.domain.model.messages.PostNewMessageModel
import com.team2.chitchat.data.repository.DataProvider
import com.team2.chitchat.data.repository.remote.request.messages.NewMessageRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostNewMessageUseCase @Inject constructor(private val dataProvider: DataProvider) {
    operator fun invoke(newMessageRequest: NewMessageRequest): Flow<BaseResponse<PostNewMessageModel>> {
        return dataProvider.postNewMessage(newMessageRequest)
    }
}