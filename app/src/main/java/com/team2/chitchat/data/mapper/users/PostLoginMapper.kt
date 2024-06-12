package com.team2.chitchat.data.mapper.users

import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.domain.model.users.PostLoginModel
import com.team2.chitchat.data.mapper.ResponseMapper
import com.team2.chitchat.data.repository.remote.response.users.PostLoginResponse

class PostLoginMapper : ResponseMapper<PostLoginResponse, PostLoginModel> {
    override fun fromResponse(response: PostLoginResponse): PostLoginModel {
        val userModel = if (response.user != null) {
            GetUserMapper().fromResponse(response.user)
        } else {
            GetUserModel()
        }

        return PostLoginModel(
            response.token ?: "",
            userModel
        )
    }
}