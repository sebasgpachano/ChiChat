package com.team2.chitchat.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.team2.chitchat.R
import com.team2.chitchat.data.repository.remote.backend.ChatService
import com.team2.chitchat.databinding.FragmentProfileBinding
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.ui.dialogfragment.MessageDialogFragment
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private val viewModel: ProfileViewModel by viewModels()

    override fun inflateBinding() {
        binding = FragmentProfileBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        initializeListeners()
    }

    override fun configureToolbarAndConfigScreenSections() {
        fragmentLayoutWithToolbar()
        showToolbar(
            showBack = true,
            showProfile = false,
            showNotification = true,
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
                    binding?.imageVProfileFragment?.setImageBitmap(bitmap)
                }
            }
        }
    }

    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) =
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
        }
    }
}