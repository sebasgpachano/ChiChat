package com.team2.chitchat.data.domain.usecase.local

import com.team2.chitchat.data.repository.DataProvider
import com.team2.chitchat.data.repository.local.message.MessageDB
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesDbUseCase @Inject constructor(private val dataProvider: DataProvider) {
    operator fun invoke(): Flow<BaseResponse<ArrayList<MessageDB>>> {
        return dataProvider.getMessageDb()
    }
}