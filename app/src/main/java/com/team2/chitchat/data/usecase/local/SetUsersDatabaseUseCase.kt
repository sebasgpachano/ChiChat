package com.team2.chitchat.data.usecase.local

import com.team2.chitchat.data.repository.DataProvider
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetUsersDatabaseUseCase @Inject constructor(private val dataProvider: DataProvider) {
    operator fun invoke(users: ArrayList<UserDB>): Flow<BaseResponse<Boolean>> {
        return dataProvider.insertUsers(users)
    }
}