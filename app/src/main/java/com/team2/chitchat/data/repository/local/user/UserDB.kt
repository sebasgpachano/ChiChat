package com.team2.chitchat.data.repository.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "user"
)
data class UserDB(
    @PrimaryKey val id: String,
    val avatar: String,
    val nick: String,
    var online: Boolean,
    var block: Boolean
)