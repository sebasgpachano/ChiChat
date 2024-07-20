package com.team2.chitchat.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
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
import com.team2.chitchat.data.repository.preferences.SharedPreferencesManager
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
        loadProfilePicture()
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
            viewModel.errorFlow.collect{errorModel->
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
            viewModel.getUserStateFlow.collect {user->
                binding?.apply {
                    textVUserNameProfileFragment.text = user.login
                    textVUserNickNameProfileFragment.text = user.nick
                }
            }
        }
        lifecycleScope.launch {
            viewModel.putLogOutStateFlow.collect {logOutIsOk->
                if (logOutIsOk) {
                    Log.d(TAG, "l> observeViewModel: logOutIsOk")
                    findNavController().navigate(R.id.action_profileFragment_to_chatListFragment)
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
                    title = "¿${context?.getString(R.string.close_session)}?",
                    message = context?.getString(R.string.question_close_session)?: "",
                    textPositiveButton = context?.getString(R.string.accept)?: "Aceptar",
                    textNegativeButton = context?.getString(R.string.cancel)?: "Cancelar",
                    listener = object : MessageDialogFragment.MessageDialogListener{
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

    private fun loadProfilePicture() {
        val profilePicture = SharedPreferencesManager.loadProfilePicture(requireContext())
        if (profilePicture != null) {
            val circularBitmap = getCircularBitmap(profilePicture)
            binding?.imageVProfileFragment?.setImageBitmap(circularBitmap)
        }
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = Math.min(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2

        val squaredBitmap = Bitmap.createBitmap(bitmap, x, y, size, size)
        if (squaredBitmap != bitmap) {
            bitmap.recycle()
        }

        val circularBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(circularBitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        return circularBitmap
    }
}