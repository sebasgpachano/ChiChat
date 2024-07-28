package com.team2.chitchat.ui.registration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.chitchat.R
import com.team2.chitchat.data.domain.model.error.ErrorModel
import com.team2.chitchat.data.repository.remote.backend.ChatService
import com.team2.chitchat.databinding.FragmentRegistrationBinding
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.ui.extensions.setErrorBorder
import com.team2.chitchat.ui.extensions.toastLong
import com.team2.chitchat.ui.main.DbViewModel
import com.team2.chitchat.ui.registration.adapter.Avatar
import com.team2.chitchat.ui.registration.adapter.AvatarAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegistrationFragment : BaseFragment<FragmentRegistrationBinding>(), View.OnClickListener,
    AvatarAdapter.OnItemClickListener {

    private val registrationViewModel: RegistrationViewModel by viewModels()
    private val dbViewModel: DbViewModel by viewModels()
    private lateinit var avatarAdapter: AvatarAdapter

    override fun inflateBinding() {
        binding = FragmentRegistrationBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        setupListeners()
        setupRecyclerView()
    }

    private fun setupListeners() {
        binding?.btRegister?.setOnClickListener(this)
    }

    private fun setupRecyclerView() {
        binding?.rvAvatar?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        avatarAdapter = AvatarAdapter(getAvatars(), this)
        binding?.rvAvatar?.adapter = avatarAdapter
    }

    private fun getAvatars(): List<Avatar> {
        return listOf(
            Avatar(R.mipmap.avatar_lobster_foreground),
            Avatar(R.mipmap.avatar_cat_foreground),
            Avatar(R.mipmap.avatar_monkey_foreground),
            Avatar(R.mipmap.avatar_crab_foreground),
            Avatar(R.mipmap.avatar_girl_foreground),
            Avatar(R.mipmap.avatar_boy_foreground),
        )
    }

    override fun configureToolbarAndConfigScreenSections() {
        fragmentLayoutWithToolbar()
        showToolbar(title = getString(R.string.registration_title), showBack = true)
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            registrationViewModel.loadingFlow.collect {
                showLoading(it)
            }
        }

        lifecycleScope.launch {
            dbViewModel.initDbSharedFlow.collect { isOk ->
                if (isOk) {
                    val intent = Intent(requireContext(), ChatService::class.java)
                    requireContext().startService(intent)
                    findNavController().navigate(RegistrationFragmentDirections.actionRegistrationFragmentToMainNavigation())
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            registrationViewModel.successFlow.collect { isOk ->
                if (isOk) {
                    dbViewModel.startDataBase()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            registrationViewModel.errorFlow.collect { error ->
                checkUser(error)
            }
        }
    }

    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) =
        Unit

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btRegister -> {
                clearErrors()
                if (canDoLogin()) {
                    val user = binding?.etUser?.text.toString()
                    val nick = binding?.etNick?.text.toString()
                    val password = binding?.etPassword?.text.toString()
                    val repeatPassword = binding?.etRepeatPass?.text.toString()

                    when {
                        user.length > 25 -> {
                            binding?.tvUserError?.text = getString(R.string.user_length_error)
                            binding?.etUser?.setErrorBorder(
                                true,
                                requireContext(),
                                binding?.tvUserError
                            )
                        }

                        nick.length > 25 -> {
                            binding?.tvNickError?.text = getString(R.string.nick_length_error)
                            binding?.etNick?.setErrorBorder(
                                true,
                                requireContext(),
                                binding?.tvNickError
                            )
                        }

                        password != repeatPassword -> {
                            binding?.tvRepeatPasswordError?.text =
                                getString(R.string.password_error)
                            binding?.etRepeatPass?.setErrorBorder(
                                true,
                                requireContext(),
                                binding?.tvRepeatPasswordError
                            )
                        }

                        else -> {
                            registrationViewModel.postUser(user, password, nick)
                            registrationViewModel.saveProfilePicture(binding?.ivSelectedAvatar)
                        }
                    }
                } else {
                    emptyEditText()
                }
            }
        }
    }

    private fun checkUser(error: ErrorModel) {
        if (error.message == "User exist") {
            binding?.tvUserError?.text = getString(R.string.user_error)
            binding?.etUser?.setErrorBorder(true, requireContext(), binding?.tvUserError)
        } else {
            requireContext().toastLong(error.message)
        }
    }

    private fun canDoLogin(): Boolean {
        return binding?.etUser?.text.toString().isNotBlank() && binding?.etPassword?.text.toString()
            .isNotBlank() && binding?.etRepeatPass?.text.toString()
            .isNotBlank() && binding?.etNick?.text.toString().isNotBlank()
    }

    private fun clearErrors() {
        binding?.etUser?.setErrorBorder(false, requireContext(), binding?.tvUserError)
        binding?.etPassword?.setErrorBorder(false, requireContext(), binding?.tvPasswordError)
        binding?.etRepeatPass?.setErrorBorder(
            false,
            requireContext(),
            binding?.tvRepeatPasswordError
        )
        binding?.etNick?.setErrorBorder(false, requireContext(), binding?.tvNickError)
    }

    private fun emptyEditText() {
        val editTexts = listOf(
            binding?.etUser to binding?.tvUserError,
            binding?.etPassword to binding?.tvPasswordError,
            binding?.etRepeatPass to binding?.tvRepeatPasswordError,
            binding?.etNick to binding?.tvNickError
        )
        editTexts.forEach { (editText, textView) ->
            if (editText?.text.toString().isBlank()) {
                textView?.text = getString(R.string.required_field)
                editText?.setErrorBorder(true, requireContext(), textView)
            }
        }
    }

    override fun onItemClick(avatar: Avatar) {
        binding?.ivSelectedAvatar?.setImageResource(avatar.imageResId)
    }
}