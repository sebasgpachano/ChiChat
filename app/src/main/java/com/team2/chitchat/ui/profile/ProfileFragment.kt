package com.team2.chitchat.ui.profile

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.team2.chitchat.R
import com.team2.chitchat.data.repository.crypto.BiometricCryptoManager
import com.team2.chitchat.data.repository.remote.backend.ChatService
import com.team2.chitchat.data.session.DataUserSession
import com.team2.chitchat.databinding.FragmentProfileBinding
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.ui.dialogfragment.MessageDialogFragment
import com.team2.chitchat.ui.extensions.TAG
import com.team2.chitchat.ui.extensions.gone
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private val viewModel: ProfileViewModel by viewModels()
    @Inject
    lateinit var dataUserSession: DataUserSession
    @Inject
    lateinit var biometricCryptoManager: BiometricCryptoManager
    private var isBiometricEnabled: Boolean = false

    override fun inflateBinding() {
        binding = FragmentProfileBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding() {
        initializeListeners()
        isBiometricEnabled = biometricCryptoManager.declareTypeAuthentication(requireContext())
        showSwitchBiometric()
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

    override fun viewCreatedAfterSetupObserverViewModel() = Unit

    private fun showSwitchBiometric() {
        if (!isBiometricEnabled) binding?.switchBiometric?.gone()
    }

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
                        showDialogRestartingApp(isChecked)
                    }
                }
            }
        }
    }
    private fun showDialogRestartingApp(isChecked: Boolean) {
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
}