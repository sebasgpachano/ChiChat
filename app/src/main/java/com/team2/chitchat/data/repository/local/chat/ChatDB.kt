package com.team2.chitchat.data.repository.local.chat

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.team2.chitchat.data.repository.local.user.UserDB

@Entity(
    tableName = "chat",
    foreignKeys = [ForeignKey(
        entity = UserDB::class,
        parentColumns = ["id"],
        childColumns = ["sourceId", "targetId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["sourceId", "targetId"])]
)
data class ChatDB(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val sourceId: Int,
    val targetId: Int,
    var view: Boolean
)