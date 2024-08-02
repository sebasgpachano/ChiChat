package com.team2.chitchat.data.domain.usecase.local

import com.team2.chitchat.data.repository.DataProvider
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateMessageViewUseCase @Inject constructor(private val dataProvider: DataProvider) {
    operator fun invoke(id: String, view: Boolean): Flow<BaseResponse<Boolean>> {
        return dataProvider.updateMessageView(id, view)
    }
}