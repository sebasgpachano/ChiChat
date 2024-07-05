package com.team2.chitchat.data.repository.local

import android.content.Context
import androidx.room.Room
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDatabaseManager @Inject constructor(context: Context) {
    val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "database"
    ).build()
}