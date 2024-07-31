package com.team2.chitchat.data.domain.usecase.remote

import com.team2.chitchat.data.repository.DataProvider
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostLoginUseCase @Inject constructor(private val dataProvider: DataProvider) {
    operator fun invoke(loginUserRequest: LoginUserRequest): Flow<BaseResponse<Boolean>> {
        return dataProvider.postLoginUser(loginUserRequest)
    }
}