package com.team2.chitchat.ui.login

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.team2.chitchat.R
import com.team2.chitchat.data.analytics.FirebaseAnalyticsManager
import com.team2.chitchat.data.repository.crypto.BiometricCryptoManager
import com.team2.chitchat.data.repository.remote.backend.ChatService
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.session.DataUserSession
import com.team2.chitchat.databinding.FragmentLoginBinding
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.ui.dialogfragment.MessageDialogFragment
import com.team2.chitchat.ui.extensions.TAG
import com.team2.chitchat.ui.extensions.hideKeyboard
import com.team2.chitchat.ui.extensions.setErrorBorder
import com.team2.chitchat.ui.extensions.showKeyboard
import com.team2.chitchat.ui.main.DbViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val viewModel: LoginViewModel by viewModels()
    private val dbViewModel: DbViewModel by viewModels()

    @Inject
    lateinit var biometricCryptoManager: BiometricCryptoManager

    @Inject
    lateinit var dataUserSession: DataUserSession

    //Biometric
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var isBiometricEnabled: Boolean = false

    @Inject
    lateinit var firebaseAnalyticsManager: FirebaseAnalyticsManager

    override fun inflateBinding() {
        binding = FragmentLoginBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding() {
        initListener()
        isBiometricEnabled = biometricCryptoManager.declareTypeAuthentication(requireContext())
    }

    override fun onResume() {
        super.onResume()
        binding?.apply {
            if (!viewModel.accessBiometricStateFlow.value) {
                editTUserLoginFragment.requestFocus()
                context?.showKeyboard(editTUserLoginFragment)
            }
        }
    }

    override fun configureToolbarAndConfigScreenSections() {
        fragmentLayoutWithToolbar()
        hideToolbar()
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginStateFlow.collect {isLogged ->
                handledLogin(isLogged)
            }
        }

        lifecycleScope.launch {
            viewModel.getRefreshTokenStateFlow.collect {}
        }

        lifecycleScope.launch {
            viewModel.accessBiometricStateFlow.collect { isOk ->

                if (isOk && !dataUserSession.haveSession() && isBiometricEnabled) {
                    showBiometricDialog(BiometricPrompt.CryptoObject(biometricCryptoManager.encryptedCipher()),object : AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            Log.d(TAG, "onAuthenticationSucceeded: ${result.cryptoObject?.cipher}")
                            result.cryptoObject?.let {
                                viewModel.loaRefreshToken {
                                    startDataBase()
                                }

                            }
                        }
                    })
                    binding?.apply {
                        imageVFingerprintLoginF.visibility = View.VISIBLE
                    }
                }
            }
        }

        lifecycleScope.launch {
            dbViewModel.initDbSharedFlow.collect {isOk->
                gotoHome(isOk)
            }
        }

        lifecycleScope.launch {
            viewModel.errorFlow.collect { errorModel ->

                when (errorModel.errorCode) {
                    "400" -> {
                        binding?.apply {
                            editTUserLoginFragment.setErrorBorder(
                                true,
                                requireContext(),
                                binding?.textVUserErrorLoginFragment
                            )
                            textVUserErrorLoginFragment.setText(R.string.user_not_exist_error)
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
                            textVUserErrorLoginFragment.setText(R.string.user_not_exist_error)
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
                viewModel.saveAccessBiometric(false)
            }
        }

        lifecycleScope.launch {
            viewModel.loadingFlow.collect { loading ->
                showLoading(loading)
            }
        }

    }

    private fun gotoHome(isLogged: Boolean) {
        if (isLogged) {
            val intent = Intent(requireContext(), ChatService::class.java)
            requireContext().startService(intent)
            findNavController().popBackStack()
        }
    }

    private fun handledLogin(isOk: Boolean) {
        if (isOk) {
            if (isBiometricEnabled) {
                showMessageDialog(
                    iconID = R.drawable.baseline_fingerprint_62,
                    title = getString(R.string.title_biometric_activated),
                    message = getString(R.string.mesage_biometric_activated),
                    listener = object : MessageDialogFragment.MessageDialogListener {
                        override fun positiveButtonOnclick(view: View) {
                            viewModel.saveAccessBiometric(true)
                            startDataBase()
                        }

                        override fun negativeButtonOnclick() {
                            startDataBase()
                        }
                    })
            } else {
                startDataBase()
            }
        }
    }

    private fun startDataBase() {
            logLoginEvent()
            dbViewModel.startDataBase()
    }

    private fun logLoginEvent() {
        val loginMethod =
            if (viewModel.accessBiometricStateFlow.value) getString(R.string.biometric_event) else getString(
                R.string.password_event
            )
        firebaseAnalyticsManager.logLoginEvent(loginMethod)
    }

    override fun viewCreatedAfterSetupObserverViewModel() =
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
                val login = LoginUserRequest(userInput, passwordInput)

                if (userInput.isNotBlank() && passwordInput.isNotBlank()) {
                    activity?.hideKeyboard()
                    viewModel.doLogin(login)
                } else {
                    emptyEditText(
                        listOf(
                            editTUserLoginFragment to textVUserErrorLoginFragment,
                            editTPasswordLoginFragment to textVPasswordErrorLoginFragment
                        )
                    )
                }
            }
            imageVFingerprintLoginF.apply {
                setOnClickListener {
                    showBiometricDialog(BiometricPrompt.CryptoObject(biometricCryptoManager.encryptedCipher()),object : AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            Log.d(TAG, "onAuthenticationSucceeded: ${result.cryptoObject?.cipher}")
                            result.cryptoObject?.let {
                                viewModel.loaRefreshToken {
                                    startDataBase()
                                }
                            }
                        }
                    })
                }
            }
            editTPasswordLoginFragment.setOnEditorActionListener { _, actionId, _ ->

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    context?.hideKeyboard(editTPasswordLoginFragment)
                    activity?.hideKeyboard()
                    buttonLogin.performClick()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }

    }

    private fun emptyEditText(pairOfEditTextToTextView: List<Pair<EditText, TextView>>) {

        pairOfEditTextToTextView.forEach { (editText, textView) ->
            if (editText.text.toString().isBlank()) {
                textView.text = getString(R.string.required_field)
                editText.setErrorBorder(true, requireContext(), textView)
            }
        }
    }

    private fun showBiometricDialog(cryptoObject: BiometricPrompt.CryptoObject, biometricCallback: AuthenticationCallback) {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor, biometricCallback)
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.title_biometric_dialog))
            .setSubtitle(resources.getString(R.string.subtitle_biometric_dialog))
            .setNegativeButtonText(resources.getString(R.string.cancel_biometric_dialog))
            .build()
        biometricPrompt.authenticate(promptInfo,cryptoObject)
    }

}