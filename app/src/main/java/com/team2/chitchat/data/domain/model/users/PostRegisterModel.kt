package com.team2.chitchat.data.domain.model.users

import com.team2.chitchat.data.domain.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostRegisterModel(
    val success: Boolean = false,
    val userModel: GetUserModel = GetUserModel()
) : BaseModel()
