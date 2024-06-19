package com.team2.chitchat.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.team2.chitchat.R
import com.team2.chitchat.databinding.FragmentLoginBinding
import com.team2.chitchat.ui.base.BaseFragment
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val viewModel: LoginViewModel by viewModels()
    override fun inflateBinding() {
        binding = FragmentLoginBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {

    }

    override fun configureToolbarAndConfigScreenSections() {
        fragmentLayoutWithToolbar()
        hideToolbar()
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginStateFlow.collect {loginModel->
                
            }
        }

    }
    fun initListener() {

    }
    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) {

    }

}