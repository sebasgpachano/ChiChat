package com.team2.chitchat.data.repository.remote.response.messages

import com.google.gson.annotations.SerializedName

data class PostNewMessageResponse(
    @SerializedName("success")
    val success: Boolean?
)
