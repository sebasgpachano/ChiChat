package com.team2.chitchat.data.repository.remote.backend

import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.repository.remote.response.users.PostRegisterResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallApiService @Inject constructor(private val apiService: ApiService) :
    BaseService() {
    suspend fun callPostRegisterUser(registerUserRequest: RegisterUserRequest): BaseResponse<PostRegisterResponse> {
        return apiCall { apiService.postRegisterUser(registerUserRequest) }
    }

}