package com.team2.chitchat.data.domain.model.messages

import com.team2.chitchat.data.domain.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostNewMessageModel(val success: Boolean) : BaseModel()
