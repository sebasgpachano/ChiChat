package com.team2.chitchat.data.mapper.users

import com.team2.chitchat.data.domain.model.users.GetUserModel
import com.team2.chitchat.data.domain.model.users.PostRegisterModel
import com.team2.chitchat.data.mapper.ResponseMapper
import com.team2.chitchat.data.repository.remote.response.users.PostRegisterResponse

class PostRegisterMapper : ResponseMapper<PostRegisterResponse, PostRegisterModel> {
    override fun fromResponse(response: PostRegisterResponse): PostRegisterModel {
        val userModel = if (response.user != null) {
            GetUserMapper().fromResponse(response.user)
        } else {
            GetUserModel()
        }

        return PostRegisterModel(
            response.success ?: false,
            userModel
        )
    }
}