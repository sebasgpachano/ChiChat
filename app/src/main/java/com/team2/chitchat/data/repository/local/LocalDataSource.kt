package com.team2.chitchat.data.repository.local

import android.content.Context
import com.team2.chitchat.R
import com.team2.chitchat.data.domain.model.error.ErrorModel
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.local.message.MessageDB
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val appDatabaseManager: AppDatabaseManager,
    @ApplicationContext private val context: Context
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

    suspend fun deleteChatsNotIn(listIds: List<String>) {
        return appDatabaseManager.db.chatDAO().deleteChatsNotIn(listIds)
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

    fun getChatsDb(): Flow<BaseResponse<ArrayList<ChatDB>>> = flow {
        try {
            appDatabaseManager.db.chatDAO().getChatsDb().collect { chats ->
                if (chats.isNotEmpty()) {
                    emit(BaseResponse.Success(ArrayList(chats)))
                } else {
                    emit(
                        BaseResponse.Error(
                            ErrorModel(
                                "",
                                "",
                                context.getString(R.string.empty_list)
                            )
                        )
                    )
                }
            }
        } catch (e: Exception) {
            val errorModel =
                ErrorModel("", "", e.message ?: context.getString(R.string.error_unknown_error))
            emit(BaseResponse.Error(errorModel))
        }
    }

    fun updateChatView(id: String, view: Boolean): Flow<BaseResponse<Boolean>> = flow {
        try {
            val rowsUpdated = appDatabaseManager.db.chatDAO().updateChatView(id, view)
            if (rowsUpdated > 0) {
                emit(BaseResponse.Success(true))
            }
        } catch (e: Exception) {
            val errorModel =
                ErrorModel("", "", e.message ?: context.getString(R.string.error_unknown_error))
            emit(BaseResponse.Error(errorModel))
        }
    }


    fun getChat(chatId: String): Flow<BaseResponse<ChatDB>> = flow {
        try {
            appDatabaseManager.db.chatDAO().getChat(chatId).collect { chat ->
                emit(BaseResponse.Success(chat))
            }
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

    suspend fun deleteMessagesNotIn(listIds: List<String>) {
        return appDatabaseManager.db.messagesDAO().deleteMessagesNotIn(listIds)
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

    fun getMessagesDb(): Flow<BaseResponse<ArrayList<MessageDB>>> = flow {
        try {
            appDatabaseManager.db.messagesDAO().getMessagesDb().collect { messages ->
                if (messages.isNotEmpty()) {
                    emit(BaseResponse.Success(ArrayList(messages)))
                } else {
                    emit(
                        BaseResponse.Error(
                            ErrorModel(
                                "",
                                "",
                                context.getString(R.string.empty_list)
                            )
                        )
                    )
                }
            }
        } catch (e: Exception) {
            val errorModel =
                ErrorModel("", "", e.message ?: context.getString(R.string.error_unknown_error))
            emit(BaseResponse.Error(errorModel))
        }
    }

    fun getMessagesForChat(chatId: String): Flow<BaseResponse<List<MessageDB>>> = flow {
        try {
            appDatabaseManager.db.messagesDAO().getMessagesForChat(chatId).collect { messages ->
                if (messages.isNotEmpty()) {
                    emit(BaseResponse.Success(ArrayList(messages)))
                } else {
                    emit(
                        BaseResponse.Error(
                            ErrorModel(
                                "", "",
                                context.getString(R.string.empty_list)
                            )
                        )
                    )
                }
            }
        } catch (e: Exception) {
            val errorModel =
                ErrorModel("", "", e.message ?: context.getString(R.string.error_unknown_error))
            emit(BaseResponse.Error(errorModel))
        }
    }

}