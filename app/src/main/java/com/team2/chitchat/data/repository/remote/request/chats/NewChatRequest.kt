package com.team2.chitchat.data.repository.remote.request.chats

import com.google.gson.annotations.SerializedName

data class NewChatRequest(
    @SerializedName("source")
    val source: String,
    @SerializedName("target")
    val target: String
)