package com.team2.chitchat.data.repository

import com.team2.chitchat.data.domain.model.chats.PostNewChatModel
import com.team2.chitchat.data.domain.model.messages.PostNewMessageModel
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.domain.model.users.PostRegisterModel
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.local.message.MessageDB
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.data.repository.remote.request.chats.NewChatRequest
import com.team2.chitchat.data.repository.remote.request.messages.NewMessageRequest
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow

interface DataSource {
    //RegisterUser
    fun postRegisterUser(registerUserRequest: RegisterUserRequest): Flow<BaseResponse<PostRegisterModel>>

    //LoginUser
    fun postLoginUser(loginUserRequest: LoginUserRequest): Flow<BaseResponse<Boolean>>

    //ContactsList
    fun getContactsList(): Flow<BaseResponse<ArrayList<UserDB>>>

    //Chats
    fun getChats(): Flow<BaseResponse<ArrayList<ChatDB>>>
    fun postNewChat(newChatRequest: NewChatRequest): Flow<BaseResponse<PostNewChatModel>>
    fun deleteChat(id: String): Flow<BaseResponse<Boolean>>

    //Message
    fun getMessage(): Flow<BaseResponse<ArrayList<MessageDB>>>
    fun postNewMessage(newMessageRequest: NewMessageRequest): Flow<BaseResponse<PostNewMessageModel>>

    //Profile
    fun getProfile(): Flow<BaseResponse<GetUserModel>>

    //LogOut
    fun putLogOut(): Flow<BaseResponse<Boolean>>

    //User Database
    fun insertUsers(users: ArrayList<UserDB>): Flow<BaseResponse<Boolean>>
    fun deleteUserTable(): Flow<BaseResponse<Boolean>>

    //Chat Database
    fun insertChats(chats: ArrayList<ChatDB>): Flow<BaseResponse<Boolean>>
    suspend fun deleteChatsNotIn(chats: List<String>)
    fun deleteChatTable(): Flow<BaseResponse<Boolean>>
    fun getChatsDb(): Flow<BaseResponse<ArrayList<ChatDB>>>
    fun getChat(chatId: String): Flow<BaseResponse<ChatDB>>
    fun updateChatView(id: String, view: Boolean): Flow<BaseResponse<Boolean>>

    //Message Database
    fun insertMessages(messages: ArrayList<MessageDB>): Flow<BaseResponse<Boolean>>
    suspend fun deleteMessagesNotIn(messages: List<String>)
    fun deleteMessageTable(): Flow<BaseResponse<Boolean>>
    fun getMessageDb(): Flow<BaseResponse<ArrayList<MessageDB>>>
    fun getMessagesForChat(chatId: String): Flow<BaseResponse<List<MessageDB>>>
}