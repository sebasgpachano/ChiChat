package com.team2.chitchat.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.team2.chitchat.R
import com.team2.chitchat.databinding.FragmentRegistrationBinding
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.utils.toastLong
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegistrationFragment : BaseFragment<FragmentRegistrationBinding>(), View.OnClickListener {

    private val registrationViewModel: RegistrationViewModel by viewModels()

    override fun inflateBinding() {
        binding = FragmentRegistrationBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        setupListeners()
    }

    private fun setupListeners() {
        binding?.btRegister?.setOnClickListener(this)
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

        viewLifecycleOwner.lifecycleScope.launch {
            registrationViewModel.successFlow.collect {
                requireContext().toastLong("Registro exitoso")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            registrationViewModel.errorFlow.collect { error ->
                requireContext().toastLong(error.message)
            }
        }
    }

    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) =
        Unit

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btRegister -> {
                val user = binding?.etUser?.text.toString()
                val pass = binding?.etPassword?.text.toString()
                val repeatPass = binding?.etRepeatPass?.text.toString()
                val nick = binding?.etNick?.text.toString()
                if (user.isNotBlank() && pass.isNotBlank() && repeatPass.isNotBlank() && nick.isNotBlank()) {
                    if (pass == repeatPass) {
                        registrationViewModel.postUser(user, pass, nick)
                        //TODO Navigate a login
                    } else {
                        //Error en EditText
                        requireContext().toastLong("Contraseñas no coinciden")
                    }
                } else {
                    requireContext().toastLong("Rellena todos los campos")
                }
            }
        }
    }
}