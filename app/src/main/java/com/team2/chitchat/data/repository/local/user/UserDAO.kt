package com.team2.chitchat.data.repository.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsers(users: List<UserDB>)

    @Query("SELECT * FROM user")
    suspend fun getContactsListDB(): List<UserDB>

    @Query("DELETE FROM user")
    suspend fun deleteUserTable()

    @Query("DELETE FROM user WHERE id NOT IN (:usersIds)")
    suspend fun deleteUsersNotIn(usersIds: List<String>)

    @Query("UPDATE user SET `online` = :state WHERE id = :id")
    suspend fun updateState(id: String, state: Boolean)

    @Query("SELECT * FROM user")
    fun getUsersDb(): Flow<List<UserDB>>
}