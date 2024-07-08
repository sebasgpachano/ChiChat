package com.team2.chitchat.data.repository.remote.backend

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.team2.chitchat.data.repository.DataProvider
import com.team2.chitchat.ui.extensions.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatService @Inject constructor(private val dataProvider: DataProvider) : Service() {
    private val job = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    init {
        Log.d(TAG, "%> Service running...")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        initLoadData()
    }

    private fun initLoadData() {
        serviceScope.launch {
            while (true) {
                Log.d(TAG, "%> loadData")
                delay(10000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        Log.d(TAG, "%> Service stop...")
    }
}