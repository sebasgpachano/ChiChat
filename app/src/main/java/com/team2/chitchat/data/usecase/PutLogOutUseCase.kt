package com.team2.chitchat.data.usecase

import com.team2.chitchat.data.repository.remote.backend.DataProvider
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PutLogOutUseCase @Inject constructor(
    private val dataProvider: DataProvider
) {
    operator fun invoke(): Flow<BaseResponse<Boolean>> {
        return dataProvider.putLogOut()
    }
}