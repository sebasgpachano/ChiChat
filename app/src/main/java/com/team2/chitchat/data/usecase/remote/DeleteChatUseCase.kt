package com.team2.chitchat.data.usecase.remote

import com.team2.chitchat.data.repository.DataProvider
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteChatUseCase @Inject constructor(private val dataProvider: DataProvider) {
    operator fun invoke(id: String): Flow<BaseResponse<Boolean>> {
        return dataProvider.deleteChat(id)
    }
}