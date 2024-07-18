package com.team2.chitchat.data.repository.remote.request.users

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginUserRequest(
    @SerializedName("login")
    var login: String,
    @SerializedName("password")
    var password: String,
): Serializable
