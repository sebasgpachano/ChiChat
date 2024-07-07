package com.team2.chitchat.data.repository.local

import android.content.Context
import com.team2.chitchat.R
import com.team2.chitchat.data.domain.model.error.ErrorModel
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.local.message.MessageDB
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val appDatabaseManager: AppDatabaseManager,
    private val context: Context
) {
    //User
    fun insertUsers(users: ArrayList<UserDB>): Flow<BaseResponse<Boolean>> = flow {
        try {
            appDatabaseManager.db.userDAO().insertUsers(users)
            emit(BaseResponse.Success(true))
        } catch (e: Exception) {
            val errorModel =
                ErrorModel("", "", e.message ?: context.getString(R.string.error_unknown_error))
            emit(BaseResponse.Error(errorModel))
        }
    }

    fun deleteUserTable(): Flow<BaseResponse<Boolean>> = flow {
        try {
            appDatabaseManager.db.userDAO().deleteUserTable()
            emit(BaseResponse.Success(true))
        } catch (e: Exception) {
            val errorModel =
                ErrorModel("", "", e.message ?: context.getString(R.string.error_unknown_error))
            emit(BaseResponse.Error(errorModel))
        }
    }

    //Chat
    fun insertChats(chats: ArrayList<ChatDB>): Flow<BaseResponse<Boolean>> = flow {
        try {
            appDatabaseManager.db.chatDAO().insertChats(chats)
            emit(BaseResponse.Success(true))
        } catch (e: Exception) {
            val errorModel =
                ErrorModel("", "", e.message ?: context.getString(R.string.error_unknown_error))
            emit(BaseResponse.Error(errorModel))
        }
    }

    fun deleteChatTable(): Flow<BaseResponse<Boolean>> = flow {
        try {
            appDatabaseManager.db.chatDAO().deleteChatTable()
            emit(BaseResponse.Success(true))
        } catch (e: Exception) {
            val errorModel =
                ErrorModel("", "", e.message ?: context.getString(R.string.error_unknown_error))
            emit(BaseResponse.Error(errorModel))
        }
    }

    //Messages
    fun insertMessages(messages: ArrayList<MessageDB>): Flow<BaseResponse<Boolean>> = flow {
        try {
            appDatabaseManager.db.messagesDAO().insertMessages(messages)
            emit(BaseResponse.Success(true))
        } catch (e: Exception) {
            val errorModel =
                ErrorModel("", "", e.message ?: context.getString(R.string.error_unknown_error))
            emit(BaseResponse.Error(errorModel))
        }
    }

    fun deleteMessageTable(): Flow<BaseResponse<Boolean>> = flow {
        try {
            appDatabaseManager.db.messagesDAO().deleteMessageTable()
            emit(BaseResponse.Success(true))
        } catch (e: Exception) {
            val errorModel =
                ErrorModel("", "", e.message ?: context.getString(R.string.error_unknown_error))
            emit(BaseResponse.Error(errorModel))
        }
    }

}