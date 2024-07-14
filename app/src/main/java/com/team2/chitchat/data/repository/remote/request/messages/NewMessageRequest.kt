package com.team2.chitchat.data.repository.remote.request.messages

import com.google.gson.annotations.SerializedName

data class NewMessageRequest(
    @SerializedName("chat")
    val chat: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("message")
    val message: String
)
