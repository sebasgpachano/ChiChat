package com.team2.chitchat.ui.main

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.team2.chitchat.R
import com.team2.chitchat.data.constants.GeneralConstants.Companion.INTENT_KEY_PUSH_NOTIFICATION_BODY
import com.team2.chitchat.databinding.ActivityMainBinding
import com.team2.chitchat.ui.base.BaseActivity
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val mainViewModel: MainViewModel by viewModels()
    private val callRequestPermissionPostNotification =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "%> Permiso concedido")
            } else {
                if (!shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                    Log.d(TAG, "%> El usuario pulso en no volver a mostrar")
                    openAppSettings()
                    this.finish()
                } else {
                    Log.d(TAG, "%> Permiso no concedido")
                    this.finish()
                }
            }
        }

    override fun inflateBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
    }

    override fun observeViewModel() = Unit

    override fun createAfterInflateBindingSetupObserverViewModel(savedInstanceState: Bundle?) {
        askPermissionNotification()
        configNavigation()
    }

    private fun askPermissionNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                this,
                POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            callRequestPermissionPostNotification.launch(POST_NOTIFICATIONS)
        }
    }

    override fun configureToolbarAndConfigScreenSections() = Unit

    private fun configNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

    }

    override fun goToProfileFragment() {
        super.goToProfileFragment()
        navController.navigate(R.id.action_global_profileFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.logOut()
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.logOut()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.putOnline()
        Log.d(
            TAG,
            "Firebase push notification OnResume ${
                intent.extras?.getString(
                    INTENT_KEY_PUSH_NOTIFICATION_BODY
                )
            }"
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
        Log.d(
            TAG,
            "Firebase push notification OnNewIntent ${
                intent.extras?.getString(
                    INTENT_KEY_PUSH_NOTIFICATION_BODY
                )
            }"
        )
    }

    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", this.packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}