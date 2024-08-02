package com.team2.chitchat.data.repository.remote.response.messages

import com.google.gson.annotations.SerializedName

data class GetMessagesResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("chat")
    val chat: String?,
    @SerializedName("source")
    val source: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("date")
    val date: String?
)