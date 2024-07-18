package com.team2.chitchat.hilt

import android.app.Application
import com.team2.chitchat.data.repository.preferences.EncryptedSharedPreferencesKeys
import com.team2.chitchat.data.repository.preferences.EncryptedSharedPreferencesManager
import com.team2.chitchat.data.repository.preferences.SharedPreferencesManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SimpleApplication : Application() {
}