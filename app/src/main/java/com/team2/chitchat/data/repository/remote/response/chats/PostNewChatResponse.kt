package com.team2.chitchat.data.repository.remote.response.chats

import com.google.gson.annotations.SerializedName

data class PostNewChatResponse(
    @SerializedName("success")
    val success: Boolean?,
    @SerializedName("created")
    val created: Boolean?,
    @SerializedName("chat")
    val chat: GetChatsResponse?
)
