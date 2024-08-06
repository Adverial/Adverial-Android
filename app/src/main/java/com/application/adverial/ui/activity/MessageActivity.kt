// File: MessageActivity.kt

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.BuildConfig
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.Conversation
import com.application.adverial.remote.model.Message
import com.application.adverial.remote.model.MessageResponse
import com.application.adverial.service.Tools
import com.application.adverial.ui.MessageAdapter
import com.application.adverial.ui.MessageViewModel
import com.bumptech.glide.Glide
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import kotlinx.android.synthetic.main.activity_masseges_list.messages_back_btn
import kotlinx.android.synthetic.main.activity_message.buttonAddMedia
import kotlinx.android.synthetic.main.activity_message.buttonBack
import kotlinx.android.synthetic.main.activity_message.buttonSend
import kotlinx.android.synthetic.main.activity_message.editTextMessage
import kotlinx.android.synthetic.main.activity_message.imageViewMediaPreview
import kotlinx.android.synthetic.main.activity_message.itemContainer
import kotlinx.android.synthetic.main.activity_message.itemPhoto
import kotlinx.android.synthetic.main.activity_message.itemPrice
import kotlinx.android.synthetic.main.activity_message.itemTitle
import kotlinx.android.synthetic.main.activity_message.loadingAnimation
import kotlinx.android.synthetic.main.activity_message.recyclerViewMessages
import kotlinx.android.synthetic.main.activity_message.textViewChatPartnerName
import okhttp3.*
import org.json.JSONObject
import java.io.File

class MessageActivity : AppCompatActivity() {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageViewModel: MessageViewModel
    private var selectedMediaData: String? = null
    private  var conversation: Conversation? = null

    companion object {
        private val itemCache = HashMap<Int, Bundle>()
        private const val REQUEST_MEDIA_PICK = 1
        public  var conversationId=-1;
        private const val REQUEST_STORAGE_PERMISSION = 2
        @JvmStatic var isActivityVisible: Boolean = false
            private set
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        itemContainer.visibility = View.GONE
        buttonAddMedia.setOnClickListener {
            openMediaPicker()
        }
        //Tools().rotateLayout(this,buttonBack)

         conversation  = intent.getParcelableExtra("conversation")
         conversationId = conversation?.conversionId ?: -1
        val chatPartnerName = conversation?.chatPartnerName ?: "Unknown"
        if (conversationId == -1) {
            finish()
            return
        }

        textViewChatPartnerName.text = chatPartnerName

        buttonBack.setOnClickListener {
           //don't finish the activity just go back

            onBackPressed()
        }

        messageAdapter = MessageAdapter()

        val repo = Repository(this)
        repo.user()
        repo.getUserData().observe(this) {
            if (it.status) {
                it.data.id?.let { userId -> messageAdapter.setCurrentUserId(userId) }
            }
        }

        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        recyclerViewMessages.adapter = messageAdapter

        loadingAnimation.visibility = View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
        messageViewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
        messageViewModel.getMessagesResponse().observe(this, Observer { messages ->
            messageAdapter.setMessages(messages)
            recyclerViewMessages.scrollToPosition(messageAdapter.itemCount - 1)
            loadingAnimation.visibility = View.GONE
            Tools().viewEnable(this.window.decorView.rootView, true)
        })

        messageViewModel.loadMessagesByConversationId(conversationId)

        buttonSend.setOnClickListener {
            var message = editTextMessage.text.toString().trim()
            val mediaUri = selectedMediaData?.toUri()

            //check if media no null and message is empty then set message to media name

            if (message.isNotEmpty() || mediaUri != null) {
                val mediaRequestBody: RequestBody? = mediaUri?.let {
                    val mediaPath = getPathFromUri(this, it)
                    mediaPath?.let { path ->
                        val mediaFile = File(path)
                        RequestBody.create(MediaType.parse("image/jpeg"), mediaFile)
                    }
                }
                if (mediaUri != null && message.isEmpty()) {
                    message = "Media"
                }
                messageViewModel.sendMessage(conversationId, message, mediaRequestBody)
                editTextMessage.text.clear()
                imageViewMediaPreview.visibility = View.GONE
                selectedMediaData = null
            } else {
                Toast.makeText(this, "Message or media is required", Toast.LENGTH_SHORT).show()
            }
        }

        messageViewModel.getSendMessageResponse().observe(this, Observer { response ->
            handleSendMessageResponse(response)
        })

        setupPusher(conversationId)



        addItem()

        //scroll to last message

    }


    // add item
  private fun addItem() {
    val showItem = intent.getBooleanExtra("show_item", true)
    if (showItem) {
        setItemDataToViews()
    }
}

private fun setItemDataToViews() {
    Glide.with(this)
        .load(Tools().getPath() + conversation?.adImage)
        .into(itemPhoto)
    itemTitle.text = conversation?.adTitle
    itemPrice.text = conversation?.adPriceCurrency
    itemContainer.visibility = View.VISIBLE
    itemContainer.setOnClickListener {
        val PostIntent = Intent(this, Post::class.java).apply {
            putExtra("id", conversation?.adId.toString())
        }
        startActivity(PostIntent)
    }
}
    private fun handleSendMessageResponse(response: MessageResponse?) {
        if (response == null) {
            Toast.makeText(this, "Failed to send message. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupPusher(conversationId: Int) {
        val options = PusherOptions().setCluster(BuildConfig.PUSHER_APP_CLUSTER)
        val pusher = Pusher(BuildConfig.PUSHER_APP_KEY, options)

        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                // Handle connection state changes if needed
            }

            override fun onError(message: String, code: String, e: Exception) {
                // Handle connection errors if needed
            }
        }, ConnectionState.ALL)

        val channel = pusher.subscribe("chat.$conversationId")
        channel.bind("message.sent") { event ->
            val jsonObject = JSONObject(event.data)
            val message = jsonObject.getJSONObject("message").getString("message")
            val senderId = jsonObject.getJSONObject("user").getInt("id")
            val mediaUrl = jsonObject.getJSONObject("message").optString("media_url", null)
            val createdAt = jsonObject.getJSONObject("message").getString("created_at")
            val messageId = jsonObject.getJSONObject("message").getInt("id")
            val newMessage = Message(
                messageId = messageId,
                conversionId = conversationId,
                message = message,
                mediaUrl = mediaUrl,
                createdAt = createdAt,
                senderId = senderId
            )
            try {
                runOnUiThread {
                    messageAdapter.addMessage(newMessage)
                    recyclerViewMessages.scrollToPosition(messageAdapter.itemCount - 1)
                }
            } catch (e: Exception) {
                Log.i("Pusher", "Error: $e")
            }
        }
    }

    private fun getPathFromUri(context: Context, uri: Uri): String? {
        val isKitKat = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return context.getExternalFilesDir(null).toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)

        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }

        return null
    }

    private fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                              selectionArgs: Array<String>?): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = uri?.let { context.contentResolver.query(it, projection, selection, selectionArgs, null) }
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
    }

    private fun openMediaPicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_MEDIA_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PICK && resultCode == Activity.RESULT_OK) {
            selectedMediaData = data?.data?.toString()
            imageViewMediaPreview.setImageURI(selectedMediaData?.toUri())
            imageViewMediaPreview.visibility = View.VISIBLE
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

}
