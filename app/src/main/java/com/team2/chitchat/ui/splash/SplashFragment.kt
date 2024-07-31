package com.team2.chitchat.ui.splash

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.team2.chitchat.R
import com.team2.chitchat.databinding.FragmentSplashBinding
import com.team2.chitchat.ui.base.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    override fun inflateBinding() {
        binding = FragmentSplashBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(3000)
            findNavController().navigate(R.id.action_splashFragment_to_mainNavigation)
        }
    }

    override fun configureToolbarAndConfigScreenSections() {
        fragmentFullScreenLayoutWithoutToolbar()
    }

    override fun observeViewModel() = Unit

    override fun viewCreatedAfterSetupObserverViewModel() =
        Unit

}