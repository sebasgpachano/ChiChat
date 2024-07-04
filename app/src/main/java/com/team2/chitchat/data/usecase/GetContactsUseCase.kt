package com.team2.chitchat.data.usecase

import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.repository.DataProvider
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContactsUseCase @Inject constructor(private val dataProvider: DataProvider) {
    operator fun invoke(): Flow<BaseResponse<ArrayList<GetUserModel>>> {
        return dataProvider.getContactsList()
    }
}