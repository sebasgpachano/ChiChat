package com.team2.chitchat.data.repository.remote.response.chats

import com.google.gson.annotations.SerializedName

data class GetChatsResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("source")
    val source: String?,
    @SerializedName("target")
    val target: String?,
    @SerializedName("created")
    val created: String?
)
