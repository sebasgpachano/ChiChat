package com.team2.chitchat.ui.chatlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.team2.chitchat.R
import com.team2.chitchat.databinding.FragmentChatListBinding
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.utils.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatListFragment : BaseFragment<FragmentChatListBinding>() {
    private val chatListViewModel: ChatListViewModel by viewModels()
    override fun inflateBinding() {
        binding = FragmentChatListBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = Unit

    override fun configureToolbarAndConfigScreenSections() {
        fragmentLayoutWithToolbar()
        showToolbar(title = getString(R.string.chat_list_title_toolbar), showProfile = true)
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            chatListViewModel.loadingFlow.collect { loading ->
                showLoading(loading)
            }
        }
        lifecycleScope.launch {
            chatListViewModel.errorFlow.collect { errorModel ->
                Log.d(TAG, "%>Error: ${errorModel.message}")
            }
        }
        lifecycleScope.launch {
            chatListViewModel.chatsSharedFlow.collect { chatsList ->
                Log.d(TAG, "%>Respuesta: $chatsList")
            }
        }
    }

    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) {
        chatListViewModel.getChats()
    }
}