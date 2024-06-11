package com.team2.chitchat.data.repository.remote.backend

import com.team2.chitchat.data.domain.model.users.PostRegisterModel
import com.team2.chitchat.data.mapper.users.PostRegisterMapper
import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val callApiService: CallApiService) :
    BaseService(), DataSource {
    override fun postRegisterUser(registerUserRequest: RegisterUserRequest): Flow<BaseResponse<PostRegisterModel>> =
        flow {
            val apiResult = callApiService.callPostRegisterUser(registerUserRequest)
            if (apiResult is BaseResponse.Success) {
                emit(BaseResponse.Success(PostRegisterMapper().fromResponse(apiResult.data)))
            } else if (apiResult is BaseResponse.Error) {
                emit(BaseResponse.Error(apiResult.error))
            }
        }
}