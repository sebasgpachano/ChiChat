package com.team2.chitchat.data.repository.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsers(users: List<UserDB>)

    @Query("DELETE FROM user")
    suspend fun deleteUserTable()
}