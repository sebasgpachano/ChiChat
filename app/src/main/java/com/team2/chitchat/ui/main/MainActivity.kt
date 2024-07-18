package com.team2.chitchat.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.team2.chitchat.R
import com.team2.chitchat.databinding.ActivityMainBinding
import com.team2.chitchat.hilt.SimpleApplication
import com.team2.chitchat.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    @Inject
    lateinit var simpleApplication: SimpleApplication
    private val mainViewModel: MainViewModel by viewModels()

    override fun inflateBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
    }

    override fun observeViewModel() = Unit

    override fun createAfterInflateBindingSetupObserverViewModel(savedInstanceState: Bundle?) {
        configNavigation()
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
    }
}