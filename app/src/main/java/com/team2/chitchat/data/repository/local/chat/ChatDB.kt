package com.team2.chitchat.data.repository.local.chat

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.team2.chitchat.data.repository.local.message.MessageDB
import com.team2.chitchat.data.repository.local.user.UserDB

@Entity(
    tableName = "chat",
    foreignKeys = [
        ForeignKey(
            entity = UserDB::class,
            parentColumns = ["id"],
            childColumns = ["idOtherUser"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MessageDB::class,
            parentColumns = ["id"],
            childColumns = ["idLastViewMsg"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idOtherUser", "idLastViewMsg"])]
)
data class ChatDB(
    @PrimaryKey val id: String,
    val idOtherUser: String,
    var view: Boolean,
    var otherUserOnline: Boolean,
    val otherUserName: String,
    var otherUserImg: String,
    var idLastViewMsg: String,
)