package com.team2.chitchat.ui.registration

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import com.team2.chitchat.R
import com.team2.chitchat.data.domain.model.error.ErrorModel
import com.team2.chitchat.data.repository.preferences.EncryptedSharedPreferencesKeys.Companion.ENCRYPTED_SHARED_PREFERENCES_KEY_PROFILE_IMAGE
import com.team2.chitchat.data.repository.remote.backend.ChatService
import com.team2.chitchat.databinding.FragmentRegistrationBinding
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.ui.extensions.setErrorBorder
import com.team2.chitchat.ui.extensions.toastLong
import com.team2.chitchat.ui.main.DbViewModel
import com.team2.chitchat.ui.registration.adapter.AvatarPagerAdapter
import com.team2.chitchat.ui.registration.adapter.SpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegistrationFragment : BaseFragment<FragmentRegistrationBinding>(), View.OnClickListener {

    private val registrationViewModel: RegistrationViewModel by viewModels()
    private val dbViewModel: DbViewModel by viewModels()
    private var selectedAvatarResId: Int = -1

    @Inject
    lateinit var encryptedSharedPreferences: EncryptedSharedPreferences

    override fun inflateBinding() {
        binding = FragmentRegistrationBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        setupListeners()
        setUpViewPager()
    }

    private fun setupListeners() {
        binding?.btRegister?.setOnClickListener(this)
    }

    private fun setUpViewPager() {
        val avatarList = listOf(
            R.drawable.avatar_lobster,
            R.drawable.avatar_cat,
            R.drawable.avatar_monkey,
            R.drawable.avatar_crab,
            R.drawable.avatar_girl,
            R.drawable.avatar_boy
        )
        val adapter = AvatarPagerAdapter(avatarList) { avatarResId ->
            selectedAvatarResId = avatarResId
            binding?.ivSelectedAvatar?.setImageResource(selectedAvatarResId)
        }
        binding?.vpAvatar?.adapter = adapter

        val spaceInPixels = dpToPx()
        val itemDecoration = SpaceItemDecoration(spaceInPixels)
        binding?.vpAvatar?.addItemDecoration(itemDecoration)
    }

    private fun dpToPx(): Int {
        val metrics = resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.toFloat(), metrics).toInt()
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
                    setImage()
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

    private fun setImage() {
        val imageBase64 = encryptedSharedPreferences.getString(
            ENCRYPTED_SHARED_PREFERENCES_KEY_PROFILE_IMAGE,
            null
        )
        val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) ?: null
        updateProfileImage(bitmap)
    }

    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) =
        Unit

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btRegister -> {
                clearErrors()
                if (canDoLogin()) {
                    if (binding?.etPassword?.text.toString() == binding?.etRepeatPass?.text.toString()) {
                        registrationViewModel.postUser(
                            binding?.etUser?.text.toString(),
                            binding?.etPassword?.text.toString(),
                            binding?.etNick?.text.toString()
                        )
                        registrationViewModel.saveProfilePicture(binding?.ivSelectedAvatar)
                    } else {
                        binding?.tvRepeatPasswordError?.text = getString(R.string.password_error)
                        binding?.etRepeatPass?.setErrorBorder(
                            true,
                            requireContext(),
                            binding?.tvRepeatPasswordError
                        )
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
}