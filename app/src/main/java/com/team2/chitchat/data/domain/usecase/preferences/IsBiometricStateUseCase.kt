package com.team2.chitchat.data.domain.usecase.preferences

import com.team2.chitchat.data.repository.DataProvider
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsBiometricStateUseCase @Inject constructor(
    private val dataProvider: DataProvider
) {
    operator fun invoke(): Flow<BaseResponse<Boolean>> = dataProvider.getAccessBiometric()
}