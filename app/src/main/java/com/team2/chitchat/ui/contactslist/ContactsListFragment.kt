package com.team2.chitchat.ui.contactslist

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.team2.chitchat.R
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.databinding.FragmentContactsListBinding
import com.team2.chitchat.ui.base.BaseFragment
import com.team2.chitchat.ui.contactslist.adapter.ContactsListAdapter
import com.team2.chitchat.ui.extensions.TAG
import com.team2.chitchat.ui.extensions.toastLong
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactsListFragment : BaseFragment<FragmentContactsListBinding>(),
    ContactsListAdapter.ContactsListAdapterListener {
    private val contactsListViewModel: ContactsListViewModel by viewModels()
    private val contactsListAdapter = ContactsListAdapter(this)
    private var allContacts = ArrayList<UserDB>()

    override fun inflateBinding() {
        binding = FragmentContactsListBinding.inflate(layoutInflater)
    }

    override fun createViewAfterInflateBinding() {
        configRecyclerView()
        configSwipeRefreshLayout()
        setupSearch()
    }

    private fun configRecyclerView() {
        binding?.rvContacts?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = contactsListAdapter
        }
    }

    private fun configSwipeRefreshLayout() {
        binding?.srContacts?.setOnRefreshListener {
            contactsListViewModel.getContactsList()
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
        val filteredList = allContacts.filter {
            it.nick.contains(query, ignoreCase = true)
        }
        updateList(ArrayList(filteredList))
    }

    override fun configureToolbarAndConfigScreenSections() {
        fragmentLayoutWithToolbar()
        showToolbar(title = getString(R.string.list_contacts_title_toolbar), showBack = true)
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            contactsListViewModel.loadingFlow.collect { loading ->
                showLoading(loading)
            }
        }
        lifecycleScope.launch {
            contactsListViewModel.errorFlow.collect { errorModel ->
                if (errorModel.message == "No token provided") {
                    requireContext().toastLong(getString(R.string.error_session_expired))
                    findNavController().navigateUp()
                }
            }
        }
        lifecycleScope.launch {
            contactsListViewModel.contactsSharedFlow.collect { contactsList ->
                allContacts = contactsList
                updateList(contactsList)
            }
        }
        lifecycleScope.launch {
            contactsListViewModel.newChatSharedFlow.collect { newChat ->
                findNavController().navigate(
                    ContactsListFragmentDirections.actionContactsListFragmentToChatFragment(
                        newChat.idChat
                    )
                )
            }
        }
    }

    private fun updateList(contactsList: ArrayList<UserDB>) {
        Log.d(TAG, "%> Contacts list size: ${contactsList.size}")
        contactsListAdapter.submitList(contactsList) {
            binding?.rvContacts?.scrollToPosition(0)
        }
        binding?.srContacts?.isRefreshing = false
    }

    override fun viewCreatedAfterSetupObserverViewModel() {
        contactsListViewModel.getContactsList()
    }

    override fun onItemClick(idChat: String) {
        contactsListViewModel.postNewChat(idChat)
    }

}