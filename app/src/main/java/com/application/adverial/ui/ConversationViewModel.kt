package com.application.adverial.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.application.adverial.remote.ConversationRepository
import com.application.adverial.remote.model.Conversation

class ConversationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ConversationRepository = ConversationRepository(application)

    val userConversationsLiveData: LiveData<List<Conversation>> = repository.getUserConversationsResponse()

    fun loadUserConversations() {
        repository.getUserConversations()
    }
}
