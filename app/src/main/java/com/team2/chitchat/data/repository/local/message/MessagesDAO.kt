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

    @Query("SELECT * FROM message")
    fun getListMessagesDb(): List<MessageDB>

    @Query("SELECT * FROM message WHERE chatId = :chatId ORDER BY date ASC")
    fun getMessagesForChat(chatId: String): Flow<List<MessageDB>>

    @Query("UPDATE message SET `view` = :view WHERE id = :id")
    fun updateMessageView(id: String, view: Boolean): Int

    @Query("UPDATE message SET `notified` = :notified WHERE id = :id")
    fun updateMessageNotification(id: String, notified: Boolean)

    @Query("DELETE FROM message WHERE id NOT IN (:messagesIds)")
    suspend fun deleteMessagesNotIn(messagesIds: List<String>)
}