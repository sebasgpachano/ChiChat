package com.team2.chitchat.data.repository.remote.backend

import com.team2.chitchat.data.domain.model.chats.GetChatsModel
import com.team2.chitchat.data.domain.model.chats.PostNewChatModel
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.domain.model.users.PostRegisterModel
import com.team2.chitchat.data.repository.remote.request.chats.NewChatRequest
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataProvider @Inject constructor(
    private val remoteDataSource: RemoteDataSource
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
    override fun getContactsList(): Flow<BaseResponse<ArrayList<GetUserModel>>> {
        return remoteDataSource.getContactsList()
    }

    //Chats
    override fun getChats(): Flow<BaseResponse<ArrayList<GetChatsModel>>> {
        return remoteDataSource.getChats()
    }

    override fun postNewChat(newChatRequest: NewChatRequest): Flow<BaseResponse<PostNewChatModel>> {
        return remoteDataSource.postNewChat(newChatRequest)
    }

    //Message
    override fun getMessage(): Flow<BaseResponse<ArrayList<GetMessagesModel>>> {
        return remoteDataSource.getMessage()
    }

    override fun getProfile(): Flow<BaseResponse<GetUserModel>> {
        return remoteDataSource.getProfile()
    }

    override fun putLogOut(): Flow<BaseResponse<Boolean>> {
        return remoteDataSource.putLogOut()
    }
}