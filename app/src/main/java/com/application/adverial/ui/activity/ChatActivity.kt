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
        conversationViewModel = ViewModelProvider(this).get(ConversationViewModel::class.java)

        // Observe LiveData from ViewModel
        conversationViewModel.userConversationsLiveData.observe(this, Observer { conversations ->
            conversationAdapter.setConversations(conversations)
        })

        // Load conversations
        conversationViewModel.loadUserConversations()

        // Set up navigation drawer
        val navigationView = findViewById<NavigationView>(R.id.nav_menu)
        val headerView = navigationView.getHeaderView(0)

        // Add click listeners for navigation drawer items
        headerView.findViewById<View>(R.id.menu_newAd).setOnClickListener {
            val intent = Intent(this, NewAd::class.java)
            startActivity(intent)
            finish()
        }
        headerView.findViewById<View>(R.id.menu_profile).setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Methods to handle bottom navigation clicks
    fun menu(view: View) {
        chat_drawerLayout.openDrawer(GravityCompat.START)
    }

    fun home(view: View) {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    fun favorites(view: View) {
        val intent = Intent(this, Favorites::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    fun newAd(view: View) {
        val intent = Intent(this, NewAd::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    fun notifications(view: View) {
        val intent = Intent(this, Notifications::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    fun profile(view: View) {
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    override fun onResume() {
        super.onResume()
        // Handle layout direction based on language preference
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection =
            if (language == "" || language == "0" || language == "1") View.LAYOUT_DIRECTION_LTR
            else View.LAYOUT_DIRECTION_RTL

        // load conversations
        //conversationViewModel.loadUserConversations()

    }
}
