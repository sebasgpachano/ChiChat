package com.team2.chitchat.data.usecase

import com.team2.chitchat.data.domain.model.users.PostRegisterModel
import com.team2.chitchat.data.repository.remote.backend.DataProvider
import com.team2.chitchat.data.repository.remote.request.users.RegisterUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostRegisterUseCase @Inject constructor(private val dataProvider: DataProvider) {
    operator fun invoke(registerUserRequest: RegisterUserRequest): Flow<BaseResponse<PostRegisterModel>> {
        return dataProvider.postRegisterUser(registerUserRequest)
    }
}