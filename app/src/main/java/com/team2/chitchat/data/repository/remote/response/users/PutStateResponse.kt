package com.team2.chitchat.data.repository.remote.response.users

import com.google.gson.annotations.SerializedName

data class PutStateResponse(
    @SerializedName("message")
    val message: String?
)