package com.team2.chitchat.data.repository.remote.response.chats

import com.google.gson.annotations.SerializedName

data class DeleteResponse(
    @SerializedName("success")
    val success: Boolean
)
