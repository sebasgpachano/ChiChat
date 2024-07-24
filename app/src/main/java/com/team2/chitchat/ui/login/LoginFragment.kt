package com.team2.chitchat.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.team2.chitchat.R
import com.team2.chitchat.data.repository.remote.backend.ChatService
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.session.DataUserSession
import com.team2.chitchat.databinding.FragmentLoginBinding
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.ui.dialogfragment.MessageDialogFragment
import com.team2.chitchat.ui.extensions.TAG
import com.team2.chitchat.ui.extensions.gone
import com.team2.chitchat.ui.extensions.setErrorBorder
import com.team2.chitchat.ui.main.DbViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val viewModel: LoginViewModel by viewModels()
    private val dbViewModel: DbViewModel by viewModels()

    @Inject
    lateinit var dataUserSession: DataUserSession

    //Biometric
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    //Activity Result
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultLauncher ->
            if (resultLauncher.resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "resultLauncher: ${resultLauncher.data}")
            } else {
                showErrorMessage(
                    message = getString(R.string.not_registered_biometric_access),
                    object : MessageDialogFragment.MessageDialogListener {
                        override fun positiveButtonOnclick(view: View) {
                            viewModel.saveAccessBiometric(false)
                        }

                    }
                )
            }
        }

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
            viewModel.loginStateFlow.collect { isOk ->
                startDataBase(isOk)
            }
        }

        lifecycleScope.launch {
            viewModel.getRefreshTokenStateFlow.collect { isOk ->
                startDataBase(isOk)
            }
        }

        lifecycleScope.launch {
            viewModel.accessBiometricStateFlow.collect { isOk ->

                if (isOk) {
                    binding?.apply {
                        imageVFingerprintLoginF.visibility = View.VISIBLE
                    }
                }

                if (isOk && !dataUserSession.haveSession()) {
                    declareTypeAuthentication(object : AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            Log.d(TAG, "onAuthenticationSucceeded: ${result.cryptoObject?.cipher}")
                            viewModel.loaRefreshToken()
                        }
                    })

                }
            }
        }

        lifecycleScope.launch {
            dbViewModel.initDbSharedFlow.collect { isOk ->
                if (isOk) {
                    val intent = Intent(requireContext(), ChatService::class.java)
                    requireContext().startService(intent)
                    findNavController().popBackStack()
                }
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

    private fun startDataBase(isOk: Boolean) {
        if (isOk) {
            dbViewModel.startDataBase()
        }
    }

    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) {}

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
                    if (!viewModel.accessBiometricStateFlow.value
                        && !dataUserSession.haveSession()) {
                        showMessageDialog(
                            iconID = R.drawable.delete_chat_icon,
                            title = getString(R.string.title_biometric_activated),
                            message = getString(R.string.mesage_biometric_activated),
                            listener = object : MessageDialogFragment.MessageDialogListener {
                                override fun positiveButtonOnclick(view: View) {
                                    viewModel.saveAccessBiometric(true)
                                    viewModel.doLogin(login)
                                }

                                override fun negativeButtonOnclick(view: View) {
                                    viewModel.doLogin(login)
                                }
                            })
                    } else {
                        viewModel.doLogin(login)
                    }
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
                    declareTypeAuthentication(object : AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            Log.d(TAG, "onAuthenticationSucceeded: ${result.cryptoObject?.cipher}")
                            viewModel.loaRefreshToken()
                        }
                    })
                }
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

    private fun declareTypeAuthentication(biometricCallback: AuthenticationCallback) {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d(TAG, "App can authenticate using biometrics.")
                showBiometricDialog(biometricCallback)
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e(TAG, "No biometric features available on this device.")
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e(TAG, "Biometric features are currently unavailable.")
                showErrorMessage(
                    message = getString(R.string.biometric_unavailable),
                    object : MessageDialogFragment.MessageDialogListener {
                        override fun positiveButtonOnclick(view: View) {
                        }

                    }
                )
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val enrollIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                        )
                    }
                } else {
                    Intent(Settings.ACTION_SECURITY_SETTINGS)
                }
                resultLauncher.launch(enrollIntent)
            }

            else -> {
                Log.e(TAG, "Unhandled biometric status.")
            }
        }
    }

    private fun showBiometricDialog(biometricCallback: AuthenticationCallback) {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor, biometricCallback)
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.title_biometric_dialog))
            .setSubtitle(resources.getString(R.string.subtitle_biometric_dialog))
            .setNegativeButtonText(resources.getString(R.string.cancel_biometric_dialog))
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

}