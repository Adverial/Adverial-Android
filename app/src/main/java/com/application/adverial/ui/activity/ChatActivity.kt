package com.application.adverial.ui.activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.ui.ConversationViewModel
import com.application.adverial.ui.adapter.ConversationAdapter
import com.application.adverial.ui.navigation.Favorites
import com.application.adverial.ui.navigation.Home
import com.application.adverial.ui.navigation.NewAd
import com.application.adverial.ui.navigation.Notifications
import com.application.adverial.ui.navigation.Profile
import kotlinx.android.synthetic.main.activity_chat.*
import android.view.Gravity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView


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
