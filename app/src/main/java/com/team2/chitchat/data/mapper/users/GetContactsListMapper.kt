package com.team2.chitchat.data.mapper.users

import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.mapper.ResponseMapper
import com.team2.chitchat.data.repository.remote.response.users.GetUserResponse

class GetContactsListMapper : ResponseMapper<ArrayList<GetUserResponse>, ArrayList<GetUserModel>> {
    override fun fromResponse(response: ArrayList<GetUserResponse>): ArrayList<GetUserModel> {
        val userModel: ArrayList<GetUserModel> = if (response.isEmpty()) {
            ArrayList()
        } else {
            ArrayList(response.map { userResponse ->
                GetUserModel(
                    login = userResponse.login ?: "",
                    nick = userResponse.nick ?: "",
                    avatar = userResponse.avatar ?: "",
                    token = userResponse.token ?: "",
                    online = userResponse.online ?: false,
                    created = userResponse.created ?: "",
                    updated = userResponse.updated ?: ""
                )
            })
        }
        return userModel
    }
}