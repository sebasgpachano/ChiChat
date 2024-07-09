package com.team2.chitchat.data.usecase.local

import com.team2.chitchat.data.repository.DataProvider
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetChatsDatabaseUseCase @Inject constructor(private val dataProvider: DataProvider) {
    operator fun invoke(chats: ArrayList<ChatDB>): Flow<BaseResponse<Boolean>> {
        return dataProvider.insertChats(chats)
    }
}