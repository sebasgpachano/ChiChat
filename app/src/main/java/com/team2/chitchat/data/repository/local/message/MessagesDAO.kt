package com.team2.chitchat.data.repository.local.message

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessagesDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessages(messages: List<MessageDB>)

    @Query("DELETE FROM message")
    suspend fun deleteMessageTable()

    @Query("SELECT * FROM message")
    fun getMessagesDb(): Flow<List<MessageDB>>

    @Query("SELECT * FROM message WHERE chatId = :chatId ORDER BY date ASC")
    fun getMessagesForChat(chatId: String): Flow<List<MessageDB>>

    @Query("DELETE FROM message WHERE id NOT IN (:messagesIds)")
    suspend fun deleteMessagesNotIn(messagesIds: List<String>)
}