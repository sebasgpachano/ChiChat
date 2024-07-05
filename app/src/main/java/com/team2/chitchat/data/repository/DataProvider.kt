package com.team2.chitchat.data.repository

import com.team2.chitchat.data.domain.model.chats.GetChatsModel
import com.team2.chitchat.data.domain.model.messages.GetMessagesModel
import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.domain.model.users.PostRegisterModel
import com.team2.chitchat.data.repository.local.LocalDataSource
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.data.repository.remote.backend.RemoteDataSource
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataProvider @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,

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

    //Message
    override fun getMessage(): Flow<BaseResponse<ArrayList<GetMessagesModel>>> {
        return remoteDataSource.getMessage()
    }

    //Profile
    override fun getProfile(): Flow<BaseResponse<GetUserModel>> {
        return remoteDataSource.getProfile()
    }

    //LogOut
    override fun putLogOut(): Flow<BaseResponse<Boolean>> {
        return remoteDataSource.putLogOut()
    }

    //User Database
    override fun insertUsers(users: ArrayList<UserDB>): Flow<BaseResponse<Boolean>> {
        return localDataSource.insertUsers(users)
    }

}