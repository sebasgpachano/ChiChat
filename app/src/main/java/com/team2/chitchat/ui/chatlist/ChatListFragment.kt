package com.team2.chitchat.ui.chatlist

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import com.team2.chitchat.ui.extensions.gone
import com.team2.chitchat.ui.extensions.invisible
import com.team2.chitchat.ui.extensions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatListFragment : BaseFragment<FragmentChatListBinding>(),
    ChatsListAdapter.ListChatsAdapterListener, View.OnClickListener {
    private val chatListViewModel: ChatListViewModel by viewModels()
    private val chatsListAdapter = ChatsListAdapter(this)
    private var allChats = ArrayList<ListChatsModel>()

    override fun inflateBinding() {
        binding = FragmentChatListBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) {
        configRecyclerView()
        setupListeners()
        setupSearch()
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

    private fun setupListeners() {
        binding?.btnAddChat?.setOnClickListener(this)
        binding?.ibSearch?.setOnClickListener(this)
        binding?.ibQuitSearch?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnAddChat -> findNavController().navigate(ChatListFragmentDirections.actionChatListFragmentToContactsListFragment())

            R.id.ibSearch -> {
                binding?.ibSearch?.invisible()
                binding?.tvSubtitle?.invisible()
                binding?.etSearchUser?.visible()
                binding?.ibQuitSearch?.visible()
                binding?.etSearchUser?.requestFocus()

                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.showSoftInput(binding?.etSearchUser, InputMethodManager.SHOW_IMPLICIT)
            }

            R.id.ibQuitSearch -> {
                binding?.ibSearch?.visible()
                binding?.tvSubtitle?.visible()
                binding?.etSearchUser?.gone()
                binding?.ibQuitSearch?.gone()
                binding?.etSearchUser?.text?.clear()
                updateList(allChats)

                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(binding?.etSearchUser?.windowToken, 0)
            }
        }
    }

    private fun setupSearch() {
        binding?.etSearchUser?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                filterUsers(s.toString())
            }
        })
    }

    private fun filterUsers(query: String) {
        val filteredList = allChats.filter {
            it.name.contains(query, ignoreCase = true)
        }
        updateList(ArrayList(filteredList))
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
                allChats = chatsList
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
        findNavController().navigate(
            ChatListFragmentDirections.actionChatListFragmentToChatFragment(
                idChat
            )
        )
    }
}