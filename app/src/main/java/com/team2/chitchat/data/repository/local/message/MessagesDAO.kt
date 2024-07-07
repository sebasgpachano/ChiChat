package com.team2.chitchat.data.repository.local.message

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface MessagesDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessages(messages: List<MessageDB>)
}