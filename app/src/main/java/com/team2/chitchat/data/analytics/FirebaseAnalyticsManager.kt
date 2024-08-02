package com.team2.chitchat.data.analytics

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsManager @Inject constructor() {

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun logLoginEvent(method: String) {
        try {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.METHOD, method)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
            Log.d("FirebaseAnalytics", "s> Login event logged: $method")
        } catch (e: Exception) {
            Log.e("FirebaseAnalytics", "s> Error logging login event: ${e.message}")
        }
    }
}