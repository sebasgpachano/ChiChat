package com.team2.chitchat.data.repository.remote.backend

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.team2.chitchat.data.repository.DataProvider
import com.team2.chitchat.data.repository.local.chat.ChatDB
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ChatService : Service() {
    @Inject
    lateinit var dataProvider: DataProvider

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
                addChats()
                delay(10000)
            }
        }
    }

    private suspend fun getChats(): ArrayList<ChatDB> = withContext(Dispatchers.IO) {
        val listChat = ArrayList<ChatDB>()
        try {
            dataProvider.getChats().collect { response ->
                when (response) {
                    is BaseResponse.Error -> {
                        Log.e(TAG, "%> Error fetching chats: ${response.error}")
                    }

                    is BaseResponse.Success -> {
                        listChat.addAll(response.data)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "%> Exception in getChats: ${e.message}", e)
        }
        return@withContext listChat
    }

    private suspend fun addChats(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "%> Insert chats...")
            val chats = getChats()
            var response = false
            dataProvider.insertChats(chats).collect { result ->
                response = when (result) {
                    is BaseResponse.Error -> {
                        Log.e(TAG, "%> Error inserting chats: ${result.error}")
                        false
                    }

                    is BaseResponse.Success -> result.data
                }
            }
            return@withContext response
        } catch (e: Exception) {
            Log.e(TAG, "%> Exception in addChats: ${e.message}", e)
            return@withContext false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        Log.d(TAG, "%> Service stop...")
    }
}