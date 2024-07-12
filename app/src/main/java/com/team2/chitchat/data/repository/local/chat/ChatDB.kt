package com.team2.chitchat.data.repository.local.chat

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.team2.chitchat.data.repository.local.user.UserDB

@Entity(
    tableName = "chat",
    foreignKeys = [
        ForeignKey(
            entity = UserDB::class,
            parentColumns = ["id"],
            childColumns = ["idOtherUser"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idOtherUser"])]
)
data class ChatDB(
    @PrimaryKey val id: String,
    val idOtherUser: String,
    val otherUserName: String,
    var view: Boolean,
    var otherUserOnline: Boolean,
    var otherUserImg: String,
    var dateLastMessageSend: String
)