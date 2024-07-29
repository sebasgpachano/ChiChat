package com.team2.chitchat.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.team2.chitchat.R
import com.team2.chitchat.data.repository.remote.backend.ChatService
import com.team2.chitchat.data.session.DataUserSession
import com.team2.chitchat.databinding.FragmentProfileBinding
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.ui.dialogfragment.MessageDialogFragment
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private val viewModel: ProfileViewModel by viewModels()
    @Inject
    lateinit var dataUserSession: DataUserSession
    //Activity Result
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultLauncher ->
            if (resultLauncher.resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "%> resultLauncher Biometric: ${resultLauncher.data}")
            } else {
                viewModel.saveAccessBiometric(false)
                showErrorMessage(
                    message = getString(R.string.not_registered_biometric_access),
                    object : MessageDialogFragment.MessageDialogListener {
                        override fun positiveButtonOnclick(view: View) {
                            Log.d(TAG, "l%> positiveButtonOnclick: ")

                        }

                    }
                )
            }
        }

    override fun inflateBinding() {
        binding = FragmentProfileBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding() {
        initializeListeners()
    }

    override fun configureToolbarAndConfigScreenSections() {
        fragmentLayoutWithToolbar()
        showToolbar(
            showBack = true,
            title = getString(R.string.profile)
        )
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.errorFlow.collect { errorModel ->
                showDialogError(errorModel.errorCode) {}
            }
        }

        lifecycleScope.launch {
            viewModel.deleteDbSharedFlow.collect { isOk ->
                if (isOk) {
                    viewModel.putLogOut()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getUserStateFlow.collect { user ->
                binding?.apply {
                    textVUserNameProfileFragment.text = user.login
                    textVUserNickNameProfileFragment.text = user.nick
                }
            }
        }
        lifecycleScope.launch {
            viewModel.putLogOutStateFlow.collect { logOutIsOk ->
                if (logOutIsOk) {
                    Log.d(TAG, "l> observeViewModel: logOutIsOk")
                    findNavController().navigate(R.id.action_profileFragment_to_chatListFragment)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.profilePictureStateFlow.collect { bitmap ->
                bitmap?.let {
                    binding?.imageVProfileFragment?.setImageBitmap(getCircularBitmap(bitmap))
                }
            }
        }

        lifecycleScope.launch {
            viewModel.accessBiometricStateFlow.collect { isOk ->
                Log.d(TAG, "observeViewModel: $isOk")
                binding?.switchBiometric?.isChecked = isOk
            }
        }
    }

    override fun viewCreatedAfterSetupObserverViewModel() =
        Unit

    private fun initializeListeners() {
        if (binding == null) Log.d(TAG, "l> BINDING IS NULL")
        else Log.d(TAG, "l> BINDING IS NOT NULL")
        binding?.apply {

            actionILogoutProfileFragment.binding.root.setOnClickListener {
                Log.d(TAG, "l> initializeListeners: actionILogoutProfileFragment")
                showMessageDialog(
                    iconID = R.drawable.logout_24,
                    title = "¿${requireContext().getString(R.string.close_session)}?",
                    message = requireContext().getString(R.string.question_close_session),
                    textPositiveButton = requireContext().getString(R.string.accept),
                    textNegativeButton = requireContext().getString(R.string.cancel),
                    listener = object : MessageDialogFragment.MessageDialogListener {
                        override fun positiveButtonOnclick(view: View) {
                            val intent = Intent(requireContext(), ChatService::class.java)
                            requireContext().stopService(intent)
                            viewModel.deleteDb()
                        }
                    }
                )
            }

            switchBiometric.apply {
                setOnCheckedChangeListener { view, isChecked ->
                    if (view.isPressed) {
                        declareTypeAuthentication(isChecked)
                    }
                }
            }
        }
    }

    private fun declareTypeAuthentication(isChecked: Boolean) {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d(TAG, "App can authenticate using biometrics.")
                showMessageDialog(
                    iconID = R.drawable.baseline_fingerprint_62,
                    title = getString(R.string.title_biometric_activated),
                    message = getString(R.string.message_biometric_restarting),
                    listener = object : MessageDialogFragment.MessageDialogListener {
                        override fun positiveButtonOnclick(view: View) {
                            if (isChecked) {
                                val intent = Intent(requireContext(), ChatService::class.java)
                                requireContext().stopService(intent)
                                dataUserSession.clearSession()
                                viewModel.saveAccessBiometric(true)
                                restartActivity()
                            } else {
                                viewModel.decryptToken()
                                val intent = Intent(requireContext(), ChatService::class.java)
                                requireContext().stopService(intent)
                                viewModel.saveAccessBiometric(false)
                                restartActivity()
                            }
                        }

                        override fun negativeButtonOnclick() {
                            binding?.switchBiometric?.isChecked = !isChecked
                        }
                    })
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e(TAG, "No biometric features available on this device.")
                binding?.switchBiometric?.isEnabled = false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e(TAG, "Biometric features are currently unavailable.")
                binding?.switchBiometric?.isEnabled = false
                showErrorMessage(
                    message = getString(R.string.biometric_unavailable),
                    object : MessageDialogFragment.MessageDialogListener {
                        override fun positiveButtonOnclick(view: View) = Unit
                    }
                )
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val enrollIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BiometricManager.Authenticators.BIOMETRIC_STRONG
                        )
                    }
                } else {
                    Intent(Settings.ACTION_SECURITY_SETTINGS)
                }
                resultLauncher.launch(enrollIntent)
            }

            else -> {
                binding?.switchBiometric?.isEnabled = false
                Log.e(TAG, "Unhandled biometric status.")
            }
        }
    }
}