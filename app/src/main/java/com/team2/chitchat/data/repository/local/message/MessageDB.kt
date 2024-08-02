package com.team2.chitchat.data.repository.local.message

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.local.user.UserDB

@Entity(
    tableName = "message",
    foreignKeys = [
        ForeignKey(
            entity = UserDB::class,
            parentColumns = ["id"],
            childColumns = ["sourceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChatDB::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["chatId", "sourceId"])]
)
data class MessageDB(
    @PrimaryKey val id: String,
    val chatId: String,
    val sourceId: String,
    val message: String,
    val date: String,
    var view: Boolean,
    var notified: Boolean
)
