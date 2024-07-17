package com.team2.chitchat.ui.login

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
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
import com.team2.chitchat.databinding.FragmentLoginBinding
import com.team2.chitchat.hilt.SimpleApplication
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.ui.extensions.setErrorBorder
import com.team2.chitchat.ui.main.DbViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.nio.charset.Charset
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    @Inject
    lateinit var simpleApplication: SimpleApplication
    private val viewModel: LoginViewModel by viewModels()
    private val dbViewModel: DbViewModel by viewModels()
    //Biometric
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
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

    override fun onResume() {
        super.onResume()
        if (simpleApplication.getAccessBiometric() && simpleApplication.getUserPassword().isNotEmpty()) {
            val cipherText = simpleApplication.getUserPassword()
            declareTypeAuthentication(viewModel.decryptCipher(Base64.decode(cipherText.split("-")[1], Base64.DEFAULT)), object : AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    val cipher = result.cryptoObject?.cipher
                    if (cipher != null) {
                        val encryptedData: ByteArray = Base64.decode(cipherText.split("-")[0], Base64.DEFAULT)
                        val decryptedData = cipher.doFinal(encryptedData)
                        val decryptedString = String(decryptedData, Charset.defaultCharset())
                        Log.d("prueba", "Información desencriptada: $decryptedString")
                    }
                }
            })
        }
    }
    override fun configureToolbarAndConfigScreenSections() {
        fragmentLayoutWithToolbar()
        hideToolbar()
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginStateFlow.collect { isOk ->
                if (isOk) {
                    dbViewModel.startDataBase()
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
            viewModel.loadingFlow.collect { loading ->
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
                    if (simpleApplication.getAccessBiometric()) {
                        declareTypeAuthentication(viewModel.encryptedCipher(),object : AuthenticationCallback() {
                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                super.onAuthenticationSucceeded(result)
                                result.cryptoObject?.cipher?.let {
                                    simpleApplication.setUserPassword(viewModel.encrypt(it,"$userInput,$passwordInput"))
                                    Log.d("prueba", "Encrypted information: ${ simpleApplication.getUserPassword() }")
//                                viewModel.getAuthenticationUser(
//                                    LoginUserRequest(
//                                        login = userInput, password = passwordInput
//                                    )
//                                )
                                }

                            }
                        })
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
            switchBiometric.apply {
                isChecked = simpleApplication.getAccessBiometric()
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        simpleApplication.saveAccessBiometric(true)
                    } else {
                        simpleApplication.saveAccessBiometric(false)
                    }
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
    private fun declareTypeAuthentication(cipher: Cipher, biometricCallback: AuthenticationCallback) {

        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")

                showBiometricDialog(cipher, biometricCallback)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                }
                startActivityForResult(enrollIntent, 11)
            }
        }
    }

    private fun showBiometricDialog(cipher: Cipher, biometricCallback: AuthenticationCallback) {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,biometricCallback)
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()
        biometricPrompt.authenticate(promptInfo,
            BiometricPrompt.CryptoObject(cipher))
    }

}