package com.team2.chitchat.data.repository.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsers(cities: List<UserDB>)
}