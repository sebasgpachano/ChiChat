package com.team2.chitchat.ui.chatlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.chitchat.R
import com.team2.chitchat.data.domain.model.chats.ListChatsModel
import com.team2.chitchat.databinding.FragmentChatListBinding
import com.team2.chitchat.hilt.SimpleApplication
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.ui.chatlist.adapter.ChatsListAdapter
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatListFragment : BaseFragment<FragmentChatListBinding>(),
    ChatsListAdapter.ListChatsAdapterListener {
    private val chatListViewModel: ChatListViewModel by viewModels()
    private val chatsListAdapter = ChatsListAdapter(this)

    override fun inflateBinding() {
        binding = FragmentChatListBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) {
        configRecyclerView()
        binding?.btnAddChat?.setOnClickListener {
            findNavController().navigate(ChatListFragmentDirections.actionChatListFragmentToContactsListFragment())
        }
        if ((context?.applicationContext as SimpleApplication).getAuthToken().isBlank()) {
            findNavController().navigate(R.id.action_chatListFragment_to_loginNavigation)
        }
    }

    private fun configRecyclerView() {
        binding?.rvChatList?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = chatsListAdapter
        }
    }

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
                updateList(chatsList)
            }
        }
    }

    private fun updateList(chatList: ArrayList<ListChatsModel>) {
        chatsListAdapter.submitList(chatList) {
            binding?.rvChatList?.scrollToPosition(0)
        }
    }

    override fun viewCreatedAfterSetupObserverViewModel(view: View, savedInstanceState: Bundle?) {
        chatListViewModel.getChats()
    }

    override fun onItemClick(idChat: String) {
        Log.d(TAG, "%> Has pulsado en el chat con id: $idChat")
    }
}