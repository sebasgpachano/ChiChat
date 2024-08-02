package com.team2.chitchat.data.repository.remote.response.users

import com.google.gson.annotations.SerializedName

data class PostLoginResponse(
    @SerializedName("token")
    val token: String?,
    @SerializedName("user")
    val user: GetUserResponse?
)
