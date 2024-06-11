package com.team2.chitchat.data.repository.remote.backend

import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.repository.remote.response.users.PostRegisterResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataProvider @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    DataSource {
    override fun postRegisterUser(registerUserRequest: RegisterUserRequest): Flow<BaseResponse<PostRegisterResponse>> {
        return remoteDataSource.postRegisterUser(registerUserRequest)
    }
}