package com.team2.chitchat.data.repository.remote.response.error

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("success") var errorRegister: String?,
    @SerializedName("error") var error: String?,
    @SerializedName("errorCode") var errorCode: String?,
    @SerializedName("message") var message: String?
)