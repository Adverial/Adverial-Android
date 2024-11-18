package com.application.adverial.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.databinding.ActivityChatBinding
import com.application.adverial.ui.ConversationViewModel
import com.application.adverial.ui.adapter.ConversationAdapter
import com.application.adverial.ui.navigation.Favorites
import com.application.adverial.ui.navigation.Home
import com.application.adverial.ui.navigation.NewAd
import com.application.adverial.ui.navigation.Notifications
import com.application.adverial.ui.navigation.Profile

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var conversationAdapter: ConversationAdapter
    private lateinit var conversationViewModel: ConversationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView
        conversationAdapter = ConversationAdapter()
        binding.recyclerViewConversations.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewConversations.adapter = conversationAdapter

        // Set up ViewModel
        conversationViewModel = ViewModelProvider(this)[ConversationViewModel::class.java]
        conversationViewModel.userConversationsLiveData.observe(this) { conversations ->
            conversationAdapter.setConversations(conversations)
        }

        // Load conversations
        conversationViewModel.loadUserConversations()

        // Set up click listeners for bottom navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.apply {
            homeHome.setOnClickListener { navigateToHome() }
            homeFavorite.setOnClickListener { navigateToFavorites() }
            homeAdd.setOnClickListener { navigateToNewAd() }
            homeNotification.setOnClickListener { openNotifications() }
            homeProfile.setOnClickListener { navigateToProfile() }
        }
    }

    // Navigation methods
    private fun navigateToHome() {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToFavorites() {
        val intent = Intent(this, Favorites::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToNewAd() {
        val intent = Intent(this, NewAd::class.java)
        startActivity(intent)
        finish()
    }

    private fun openNotifications() {
        val intent = Intent(this, Notifications::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToProfile() {
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        // Handle layout direction based on language preference
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection =
            if (language == "" || language == "0" || language == "1") View.LAYOUT_DIRECTION_LTR
            else View.LAYOUT_DIRECTION_RTL
    }
}
