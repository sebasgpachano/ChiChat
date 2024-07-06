package com.team2.chitchat.data.repository.local.chat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<ChatDB>)

    @Query("DELETE FROM chat")
    suspend fun deleteChatTable()
}