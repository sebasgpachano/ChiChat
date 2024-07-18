package com.team2.chitchat.data.usecase.local

import com.team2.chitchat.data.repository.DataProvider
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

class GetChatUseCase @Inject constructor(private val dataProvider: DataProvider) {
    operator fun invoke(chatId: String): Flow<BaseResponse<ChatDB?>> {
        return dataProvider.getChat(chatId)
    }
}