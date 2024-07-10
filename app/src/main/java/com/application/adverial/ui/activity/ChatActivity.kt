package com.application.adverial.ui.activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.ui.ConversationViewModel
import com.application.adverial.ui.adapter.ConversationAdapter
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private lateinit var conversationAdapter: ConversationAdapter
    private lateinit var conversationViewModel: ConversationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Set up RecyclerView
        conversationAdapter = ConversationAdapter()
        recyclerViewConversations.layoutManager = LinearLayoutManager(this)
        recyclerViewConversations.adapter = conversationAdapter

        // Set up ViewModel
        conversationViewModel = ViewModelProvider(this)[ConversationViewModel::class.java]

        // Observe LiveData from ViewModel
        conversationViewModel.userConversationsLiveData.observe(this, Observer { conversations ->
            conversationAdapter.setConversations(conversations)
        })

        // Load conversations
        conversationViewModel.loadUserConversations()
    }
}
