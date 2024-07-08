package com.team2.chitchat.data.repository.local.chat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChats(chats: List<ChatDB>)

    @Query("DELETE FROM chat")
    suspend fun deleteChatTable()

    @Query("SELECT * FROM chat")
    fun getChatsDb(): Flow<List<ChatDB>>
}