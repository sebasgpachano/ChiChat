package com.team2.chitchat.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.team2.chitchat.R
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.databinding.FragmentLoginBinding
import com.team2.chitchat.hilt.SimpleApplication
import com.team2.chitchat.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
@AndroidEntryPoint
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
        initListener()
    }

    override fun configureToolbarAndConfigScreenSections() {
        fragmentLayoutWithToolbar()
        hideToolbar()
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginStateFlow.collect {isOk->
                if (isOk) {
                    findNavController().navigate(R.id.action_loginFragment_to_chatListFragment)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.errorFlow.collect{errorModel->

            }
        }
        lifecycleScope.launch {
            viewModel.loadingFlow.collect{loading->
                showLoading(loading)
            }
        }

    }
    private fun initListener() {
        binding?.let {fragmentBinding->
            fragmentBinding.textButtonRegisterLoginF.apply {
                setOnClickListener {
                    findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
                }
            }
            fragmentBinding.textVRegisterLoginF.apply {
                setOnClickListener {
                    findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
                }
            }
            fragmentBinding.buttonLogin.apply {
                setOnClickListener {
                    val userInput = fragmentBinding.editTUserLoginFragment.text.toString()
                    val passwordInput = fragmentBinding.editTPasswordLoginFragment.text.toString()
                    if (userInput.isNotBlank() && passwordInput.isNotBlank()) {
                        viewModel.getAuthenticationUser(LoginUserRequest(
                            login = userInput, password = passwordInput
                        ))
                    } else {
                        Toast.makeText(context,"Por favor ingresa usuario y contraseña válidos",Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }

    }
    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) {

    }

}