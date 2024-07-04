package com.team2.chitchat.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.team2.chitchat.R
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.databinding.FragmentLoginBinding
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.ui.extensions.setErrorBorder
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
                    findNavController().popBackStack()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.errorFlow.collect{errorModel->

                when(errorModel.errorCode) {
                    "400" -> {
                        binding?.apply {
                            editTUserLoginFragment.setErrorBorder(
                                true,
                                requireContext(),
                                binding?.textVUserErrorLoginFragment
                            )
                            textVUserErrorLoginFragment.setText(R.string.user_error)
                        }
                    }
                    "401" -> {
                        binding?.apply {
                            editTPasswordLoginFragment.setErrorBorder(
                                true,
                                requireContext(),
                                binding?.textVPasswordErrorLoginFragment
                            )
                            textVPasswordErrorLoginFragment.setText(R.string.password_invalid)
                        }
                    }
                    "" -> {
                        binding?.apply {
                            editTUserLoginFragment.setErrorBorder(
                                false,
                                requireContext(),
                                binding?.textVUserErrorLoginFragment
                            )
                            textVUserErrorLoginFragment.setText(R.string.user_error)
                        }
                        binding?.apply {
                            editTPasswordLoginFragment.setErrorBorder(
                                false,
                                requireContext(),
                                binding?.textVPasswordErrorLoginFragment
                            )
                            textVPasswordErrorLoginFragment.setText(R.string.password_invalid)
                        }
                    }
                    else -> {
                        showDialogError(errorModel.errorCode) {

                        }
                    }
                }

            }
        }
        lifecycleScope.launch {
            viewModel.loadingFlow.collect{loading->
                showLoading(loading)
            }
        }

    }

    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) =
        Unit

    private fun initListener() {

        binding?.apply {

            textButtonRegisterLoginF.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
            }
            textVRegisterLoginF.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
            }
            buttonLogin.setOnClickListener {
                viewModel.resetError()
                val userInput = editTUserLoginFragment.text.toString()
                val passwordInput = editTPasswordLoginFragment.text.toString()
                if (userInput.isNotBlank() && passwordInput.isNotBlank()) {
                    viewModel.getAuthenticationUser(LoginUserRequest(
                        login = userInput, password = passwordInput
                    ))
                } else {
                    emptyEditText(
                        listOf(
                            editTUserLoginFragment to textVUserErrorLoginFragment,
                            editTPasswordLoginFragment to textVPasswordErrorLoginFragment
                        )
                    )
                }
            }
        }

    }
    private fun emptyEditText(pairOfEditTextToTextView: List<Pair<EditText,TextView>>) {

        pairOfEditTextToTextView.forEach { (editText, textView) ->
            if (editText.text.toString().isBlank()) {
                textView.text = getString(R.string.required_field)
                editText.setErrorBorder(true, requireContext(), textView)
            }
        }
    }


}