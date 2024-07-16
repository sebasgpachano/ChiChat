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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChat(chat: ChatDB): Long

    @Query("DELETE FROM chat")
    suspend fun deleteChatTable()

    @Query("SELECT * FROM chat")
    fun getChatsDb(): Flow<List<ChatDB>>

    @Query("SELECT * FROM chat WHERE id = :chatId")
    fun getChat(chatId: String): Flow<ChatDB>

    @Query("DELETE FROM chat WHERE id NOT IN (:chatIds)")
    suspend fun deleteChatsNotIn(chatIds: List<String>)

    @Query("UPDATE chat SET `view` = :view WHERE id = :id")
    fun updateChatView(id: String, view: Boolean): Int
}