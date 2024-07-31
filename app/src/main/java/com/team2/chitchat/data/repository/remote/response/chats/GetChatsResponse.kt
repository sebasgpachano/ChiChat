package com.team2.chitchat.data.repository.remote.response.chats

import com.google.gson.annotations.SerializedName

data class GetChatsResponse(
    @SerializedName("chat")
    val chat: String?,
    @SerializedName("source")
    val source: String?,
    @SerializedName("sourcenick")
    val sourceNick: String?,
    @SerializedName("sourceavatar")
    val sourceAvatar: String?,
    @SerializedName("sourceonline")
    val sourceOnline: Boolean?,
    @SerializedName("sourcetoken")
    val sourceToken: String?,
    @SerializedName("target")
    val target: String?,
    @SerializedName("targetnick")
    val targetNick: String?,
    @SerializedName("targetavatar")
    val targetAvatar: String?,
    @SerializedName("targetonline")
    val targetOnline: Boolean?,
    @SerializedName("targettoken")
    val targetToken: String?,
    @SerializedName("chatcreated")
    val chatCreated: String?,
)
