package com.team2.chitchat.data.repository.remote.response.users

import com.google.gson.annotations.SerializedName

data class PostRegisterResponse(
    @SerializedName("success")
    val success: Boolean?,
    @SerializedName("user")
    val user: GetUserResponse?
)
