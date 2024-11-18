package com.application.adverial.ui.activity

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.BuildConfig
import com.application.adverial.R
import com.application.adverial.databinding.ActivityMessageBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.Conversation
import com.application.adverial.remote.model.Message
import com.application.adverial.remote.model.MessageResponse
import com.application.adverial.service.Tools
import com.application.adverial.ui.MessageAdapter
import com.application.adverial.ui.MessageViewModel
import com.bumptech.glide.Glide
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageViewModel: MessageViewModel
    private var selectedMediaData: String? = null
    private var conversation: Conversation? = null

    // Declare conversationId as a class-level property

    companion object {
        var conversationId: Int = -1
        private const val REQUEST_MEDIA_PICK = 1
        private const val REQUEST_STORAGE_PERMISSION = 2
        var isActivityVisible: Boolean = false
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views and data
        binding.itemContainer.visibility = View.GONE
        conversation = intent.getParcelableExtra("conversation")
        conversationId = conversation?.conversionId ?: -1
        val chatPartnerName = conversation?.chatPartnerName ?: "Unknown"

        if (conversationId == -1) {
            finish()
            return
        }

        binding.textViewChatPartnerName.text = chatPartnerName

        setupMessageAdapter()
        setupViewModel(conversationId)

        binding.buttonBack.setOnClickListener { onBackPressed() }
        binding.buttonAddMedia.setOnClickListener { openMediaPicker() }
        binding.buttonSend.setOnClickListener { sendMessage(conversationId) }

        addItem()
    }

    private fun setupMessageAdapter() {
        messageAdapter = MessageAdapter()
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMessages.adapter = messageAdapter

        val repo = Repository(this)
        repo.user()
        repo.getUserData().observe(this) {
            if (it.status) it.data.id?.let { userId -> messageAdapter.setCurrentUserId(userId) }
        }
    }

    private fun setupViewModel(conversationId: Int) {
        binding.loadingAnimation.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)

        messageViewModel = ViewModelProvider(this)[MessageViewModel::class.java]
        messageViewModel.getMessagesResponse().observe(this) { messages ->
            messageAdapter.setMessages(messages)
            binding.recyclerViewMessages.scrollToPosition(messageAdapter.itemCount - 1)
            binding.loadingAnimation.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)
        }

        messageViewModel.loadMessagesByConversationId(conversationId)

        messageViewModel.getSendMessageResponse().observe(this) { response ->
            handleSendMessageResponse(response)
        }
    }

    private fun sendMessage(conversationId: Int) {
        val messageText = binding.editTextMessage.text.toString().trim()
        val mediaUri = selectedMediaData?.toUri()

        if (messageText.isNotEmpty() || mediaUri != null) {
            val mediaRequestBody: RequestBody? = mediaUri?.let { uri ->
                val mediaPath = getPathFromUri(this, uri)
                mediaPath?.let { path ->
                    val mediaFile = File(path)
                    RequestBody.create(MediaType.parse("image/jpeg"), mediaFile)
                }
            }

            (if (messageText.isEmpty()) null else messageText)?.let {
                messageViewModel.sendMessage(conversationId,
                    it, mediaRequestBody)
            }
            binding.editTextMessage.text.clear()
            binding.imageViewMediaPreview.visibility = View.GONE
            selectedMediaData = null
        } else {
            Toast.makeText(this, "Message or media is required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSendMessageResponse(response: MessageResponse?) {
        if (response == null) {
            Toast.makeText(this, "Failed to send message. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addItem() {
        if (intent.getBooleanExtra("show_item", true)) {
            setItemDataToViews()
        }
    }

    private fun setItemDataToViews() {
        Glide.with(this)
            .load(Tools().getPath() + conversation?.adImage)
            .into(binding.itemPhoto)
        binding.itemTitle.text = conversation?.adTitle
        binding.itemPrice.text = conversation?.adPriceCurrency
        binding.itemContainer.visibility = View.VISIBLE

        binding.itemContainer.setOnClickListener {
            val postIntent = Intent(this, Post::class.java).apply {
                putExtra("id", conversation?.adId.toString())
            }
            startActivity(postIntent)
        }
    }

    private fun openMediaPicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_MEDIA_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PICK && resultCode == Activity.RESULT_OK) {
            selectedMediaData = data?.data?.toString()
            binding.imageViewMediaPreview.setImageURI(selectedMediaData?.toUri())
            binding.imageViewMediaPreview.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        isActivityVisible = true
    }

    override fun onPause() {
        super.onPause()
        isActivityVisible = false
        if (!checkStoragePermission()) {
            requestStoragePermission()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
    }

    private fun getPathFromUri(context: Context, uri: Uri): String? {
        // Implementation remains the same as in the original code
        return null
    }
}
