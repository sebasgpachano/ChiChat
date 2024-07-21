package com.team2.chitchat.hilt

import android.app.Application
import com.team2.chitchat.data.repository.preferences.PreferencesDataSource
import com.team2.chitchat.data.session.DataUserSession
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SimpleApplication : Application() {
    @Inject
    lateinit var preferencesDataSource: PreferencesDataSource

    @Inject
    lateinit var dataUserSession: DataUserSession

    override fun onCreate() {
        super.onCreate()
        initSession()
    }

    private fun initSession() {
        if (preferencesDataSource.getAuthToken().isNotBlank()
            && preferencesDataSource.getAuthToken().isNotEmpty()
        ) {
            dataUserSession.userId = preferencesDataSource.getUserID()
            dataUserSession.tokenIb = preferencesDataSource.getAuthToken()
        }
    }
}