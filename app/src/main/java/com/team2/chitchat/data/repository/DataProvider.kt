package com.team2.chitchat.data.repository

import com.team2.chitchat.data.domain.model.chats.PostNewChatModel
import com.team2.chitchat.data.domain.model.messages.PostNewMessageModel
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.domain.model.users.PostRegisterModel
import com.team2.chitchat.data.repository.local.LocalDataSource
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.local.message.MessageDB
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.data.repository.preferences.PreferencesDataSource
import com.team2.chitchat.data.repository.remote.backend.RemoteDataSource
import com.team2.chitchat.data.repository.remote.request.chats.NewChatRequest
import com.team2.chitchat.data.repository.remote.request.messages.NewMessageRequest
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataProvider @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val preferencesDataSource: PreferencesDataSource
    ) : DataSource {
    //RegisterUSer
    override fun postRegisterUser(registerUserRequest: RegisterUserRequest): Flow<BaseResponse<PostRegisterModel>> {
        return remoteDataSource.postRegisterUser(registerUserRequest)
    }

    //LoginUser
    override fun postLoginUser(loginUserRequest: LoginUserRequest): Flow<BaseResponse<Boolean>> {
        return remoteDataSource.postLoginUser(loginUserRequest)
    }

    //ContactsList
    override fun getContactsList(): Flow<BaseResponse<ArrayList<UserDB>>> {
        return remoteDataSource.getContactsList()
    }

    //Chats
    override fun getChats(): Flow<BaseResponse<ArrayList<ChatDB>>> {
        return remoteDataSource.getChats()
    }

    override fun postNewChat(newChatRequest: NewChatRequest): Flow<BaseResponse<PostNewChatModel>> {
        return remoteDataSource.postNewChat(newChatRequest)
    }

    override fun deleteChat(id: String): Flow<BaseResponse<Boolean>> {
        return remoteDataSource.deleteChat(id)
    }

    //Message
    override fun getMessage(): Flow<BaseResponse<ArrayList<MessageDB>>> {
        return remoteDataSource.getMessage()
    }

    override fun postNewMessage(newMessageRequest: NewMessageRequest): Flow<BaseResponse<PostNewMessageModel>> {
        return remoteDataSource.postNewMessage(newMessageRequest)
    }

    //Profile
    override fun getProfile(): Flow<BaseResponse<GetUserModel>> {
        return remoteDataSource.getProfile()
    }

    //LogOut
    override fun putLogOut(): Flow<BaseResponse<Boolean>> {
        return remoteDataSource.putLogOut()
    }

    //State
    override fun putOnline(): Flow<BaseResponse<Boolean>> {
        return remoteDataSource.putOnline()
    }

    override fun putOffline(): Flow<BaseResponse<Boolean>> {
        return remoteDataSource.putOffline()
    }

    //User Database
    override fun insertUsers(users: ArrayList<UserDB>): Flow<BaseResponse<Boolean>> {
        return localDataSource.insertUsers(users)
    }

    override suspend fun getContactsListDB(): ArrayList<UserDB> {
        return localDataSource.getContactsListDB()
    }

    override suspend fun deleteUsersNotIn(users: List<String>) {
        return localDataSource.deleteUsersNotIn(users)
    }

    override suspend fun updateState(id: String, state: Boolean) {
        return localDataSource.updateState(id, state)
    }

    override fun deleteUserTable(): Flow<BaseResponse<Boolean>> {
        return localDataSource.deleteUserTable()
    }

    override fun getUsersDb(): Flow<BaseResponse<ArrayList<UserDB>>> {
        return localDataSource.getUsersDb()
    }

    //Chat Database
    override fun insertChats(chats: ArrayList<ChatDB>): Flow<BaseResponse<Boolean>> {
        return localDataSource.insertChats(chats)
    }

    override suspend fun deleteChatsNotIn(chats: List<String>) {
        return localDataSource.deleteChatsNotIn(chats)
    }

    override fun deleteChatTable(): Flow<BaseResponse<Boolean>> {
        return localDataSource.deleteChatTable()
    }

    override fun getChatsDb(): Flow<BaseResponse<ArrayList<ChatDB>>> {
        return localDataSource.getChatsDb()
    }

    override fun getChat(chatId: String): Flow<BaseResponse<ChatDB?>> {
        return localDataSource.getChat(chatId)
    }

    override fun updateChatView(id: String, view: Boolean): Flow<BaseResponse<Boolean>> {
        return localDataSource.updateChatView(id, view)
    }

    //Message Database
    override fun insertMessages(messages: ArrayList<MessageDB>): Flow<BaseResponse<Boolean>> {
        return localDataSource.insertMessages(messages)
    }

    override suspend fun deleteMessagesNotIn(messages: List<String>) {
        return localDataSource.deleteMessagesNotIn(messages)
    }

    override fun deleteMessageTable(): Flow<BaseResponse<Boolean>> {
        return localDataSource.deleteMessageTable()
    }

    override fun getMessageDb(): Flow<BaseResponse<ArrayList<MessageDB>>> {
        return localDataSource.getMessagesDb()
    }

    override fun getMessagesForChat(chatId: String): Flow<BaseResponse<List<MessageDB>>> {
        return localDataSource.getMessagesForChat(chatId)
    }

    override fun updateMessageView(id: String, view: Boolean): Flow<BaseResponse<Boolean>> {
        return localDataSource.updateMessageView(id, view)
    }

    //EncryptPreferences
    override fun putPasswordLogin(password: String) {
        preferencesDataSource.setUserPassword(password)
    }

    override fun getPasswordLogin(): String {
        return preferencesDataSource.getUserPassword()
    }

    override fun putAccessBiometric(access: Boolean) {
        preferencesDataSource.saveAccessBiometric(access)
    }

    override fun getAccessBiometric(): Flow<BaseResponse<Boolean>> {
        return preferencesDataSource.getAccessBiometric()
    }
}