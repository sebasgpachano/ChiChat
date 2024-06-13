package com.team2.chitchat.data.mapper.users

import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.mapper.ResponseMapper
import com.team2.chitchat.data.repository.remote.response.users.GetUserResponse

class GetUserMapper : ResponseMapper<GetUserResponse, GetUserModel> {
    override fun fromResponse(response: GetUserResponse): GetUserModel {
        return GetUserModel(
            response.login ?: "",
            response.nick ?: "",
            response.avatar ?: "",
            response.token ?: "",
            response.online ?: false,
            response.created ?: "",
            response.updated ?: ""
        )
    }
}