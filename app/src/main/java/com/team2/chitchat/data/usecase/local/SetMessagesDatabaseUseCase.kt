package com.team2.chitchat.data.usecase.local

import com.team2.chitchat.data.repository.DataProvider
import com.team2.chitchat.data.repository.local.message.MessageDB
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetMessagesDatabaseUseCase @Inject constructor(private val dataProvider: DataProvider) {
    operator fun invoke(messages: ArrayList<MessageDB>): Flow<BaseResponse<Boolean>> {
        return dataProvider.insertMessages(messages)
    }
}