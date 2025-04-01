package com.application.adverial.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.databinding.ActivityMessageBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.Conversation
import com.application.adverial.remote.model.MessageResponse
import com.application.adverial.service.Tools
import com.application.adverial.ui.MessageAdapter
import com.application.adverial.ui.MessageViewModel
import com.bumptech.glide.Glide
import java.io.File
import okhttp3.MediaType
import okhttp3.RequestBody

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageViewModel: MessageViewModel
    private var selectedMediaData: String? = null
    private var conversation: Conversation? = null
    private var selectedVoiceFile: File? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private val audioFilePath by lazy {
        "${getExternalFilesDir(null)?.absolutePath}/voice_message.m4a"
    }

    // Declare conversationId as a class-level property

    companion object {
        var conversationId: Int = -1
        private const val REQUEST_MEDIA_PICK = 1
        private const val REQUEST_STORAGE_PERMISSION = 2
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 3
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
        setupVoiceRecording()

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
        val voiceFile = selectedVoiceFile

        if (messageText.isNotEmpty() || mediaUri != null || voiceFile != null) {
            // Show loading indicator
            binding.loadingAnimation.visibility = View.VISIBLE
            binding.buttonSend.isEnabled = false
            binding.buttonAddMedia.isEnabled = false
            binding.buttonVoiceRecord.isEnabled = false

            try {
                // Process media file if present
                val mediaRequestBody: RequestBody? =
                        mediaUri?.let { uri ->
                            try {
                                val mediaPath = getPathFromUri(this, uri)
                                mediaPath?.let { path ->
                                    val mediaFile = File(path)
                                    if (mediaFile.exists() && mediaFile.length() > 0) {
                                        RequestBody.create(MediaType.get("image/jpeg"), mediaFile)
                                    } else {
                                        Log.e("MessageActivity", "Media file invalid: $path")
                                        Toast.makeText(
                                                        this,
                                                        "Media file is invalid or empty",
                                                        Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        null
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("MessageActivity", "Error processing media: ${e.message}")
                                Toast.makeText(
                                                this,
                                                "Error processing media file",
                                                Toast.LENGTH_SHORT
                                        )
                                        .show()
                                null
                            }
                        }

                // Process voice file if present
                val voiceRequestBody: RequestBody? =
                        voiceFile?.let {
                            try {
                                if (it.exists() && it.length() > 0) {
                                    Log.d(
                                            "VoiceDebug",
                                            "Preparing voice file: ${it.absolutePath}, size: ${it.length()} bytes"
                                    )
                                    // Ensure the file has a valid extension for the server (.m4a,
                                    // .mp3, or .wav)
                                    // The server requires specific file extensions
                                    val correctPath =
                                            if (!it.name.toLowerCase().endsWith(".m4a") &&
                                                            !it.name
                                                                    .toLowerCase()
                                                                    .endsWith(".mp3") &&
                                                            !it.name.toLowerCase().endsWith(".wav")
                                            ) {
                                                // Default to .m4a since that's how we're recording
                                                val newFile = File(it.parent, "voice_message.m4a")
                                                if (it.renameTo(newFile)) {
                                                    Log.d(
                                                            "VoiceDebug",
                                                            "Renamed voice file to ensure proper extension"
                                                    )
                                                    newFile.absolutePath
                                                } else {
                                                    it.absolutePath
                                                }
                                            } else {
                                                it.absolutePath
                                            }

                                    val fileToSend = File(correctPath)
                                    // Log the file details for debugging
                                    Log.d(
                                            "VoiceDebug",
                                            "Sending voice file: ${fileToSend.absolutePath}, " +
                                                    "exists: ${fileToSend.exists()}, size: ${fileToSend.length()}, " +
                                                    "extension: ${fileToSend.extension}"
                                    )

                                    // Create request body with correct MIME type based on file
                                    // extension
                                    when {
                                        fileToSend.name.toLowerCase().endsWith(".m4a") ->
                                                RequestBody.create(
                                                        MediaType.parse("audio/mp4"),
                                                        fileToSend
                                                )
                                        fileToSend.name.toLowerCase().endsWith(".mp3") ->
                                                RequestBody.create(
                                                        MediaType.parse("audio/mpeg"),
                                                        fileToSend
                                                )
                                        fileToSend.name.toLowerCase().endsWith(".wav") ->
                                                RequestBody.create(
                                                        MediaType.parse("audio/wav"),
                                                        fileToSend
                                                )
                                        else ->
                                                RequestBody.create(
                                                        MediaType.parse("audio/mp4"),
                                                        fileToSend
                                                )
                                    }
                                } else {
                                    Log.e("VoiceDebug", "Voice file invalid: ${it.absolutePath}")
                                    Toast.makeText(
                                                    this,
                                                    "Voice file is invalid or empty",
                                                    Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    null
                                }
                            } catch (e: Exception) {
                                Log.e("VoiceDebug", "Error processing voice: ${e.message}")
                                Toast.makeText(
                                                this,
                                                "Error processing voice file",
                                                Toast.LENGTH_SHORT
                                        )
                                        .show()
                                null
                            }
                        }

                // Send the message
                if (messageText.isNotEmpty() || mediaRequestBody != null || voiceRequestBody != null
                ) {
                    messageViewModel.sendMessage(
                            conversationId,
                            messageText.ifEmpty {
                                " "
                            }, // Send a space if no text but have media/voice
                            mediaRequestBody,
                            voiceRequestBody
                    )

                    // Clear UI
                    binding.editTextMessage.text.clear()
                    binding.imageViewMediaPreview.visibility = View.GONE
                    binding.voiceRecordIndicator.visibility = View.GONE
                    selectedMediaData = null
                    selectedVoiceFile = null
                } else {
                    // All content was invalid
                    binding.loadingAnimation.visibility = View.GONE
                    binding.buttonSend.isEnabled = true
                    binding.buttonAddMedia.isEnabled = true
                    binding.buttonVoiceRecord.isEnabled = true
                    Toast.makeText(this, "No valid content to send", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle any unexpected errors
                Log.e("MessageActivity", "Error sending message: ${e.message}")
                binding.loadingAnimation.visibility = View.GONE
                binding.buttonSend.isEnabled = true
                binding.buttonAddMedia.isEnabled = true
                binding.buttonVoiceRecord.isEnabled = true
                Toast.makeText(this, "Error sending message: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
            }
        } else {
            Toast.makeText(this, "Message, media, or voice is required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSendMessageResponse(response: MessageResponse?) {
        // Hide loading indicator
        binding.loadingAnimation.visibility = View.GONE
        binding.buttonSend.isEnabled = true
        binding.buttonAddMedia.isEnabled = true
        binding.buttonVoiceRecord.isEnabled = true

        if (response == null) {
            Toast.makeText(this, "Failed to send message. Please try again.", Toast.LENGTH_SHORT)
                    .show()
        } else {
            // Message sent successfully, scroll to bottom
            binding.recyclerViewMessages.scrollToPosition(messageAdapter.itemCount - 1)
        }
    }

    private fun addItem() {
        if (intent.getBooleanExtra("show_item", true)) {
            setItemDataToViews()
        }
    }

    private fun setItemDataToViews() {
        Glide.with(this).load(Tools().getPath() + conversation?.adImage).into(binding.itemPhoto)
        binding.itemTitle.text = conversation?.adTitle
        binding.itemPrice.text = conversation?.adPriceCurrency
        binding.itemContainer.visibility = View.VISIBLE

        binding.itemContainer.setOnClickListener {
            val postIntent =
                    Intent(this, Post::class.java).apply {
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
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMISSION
        )
    }

    private fun getPathFromUri(context: Context, uri: Uri): String? {
        // Direct file path
        if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }

        // MediaStore content
        else if ("content".equals(uri.scheme, ignoreCase = true)) {
            try {
                // Try to get path from content resolver
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        return cursor.getString(columnIndex)
                    }
                }

                // If cannot get path, create a temporary file
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val tempFile =
                            File(context.cacheDir, "temp_media_${System.currentTimeMillis()}.jpg")
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        tempFile.outputStream().use { output -> input.copyTo(output) }
                    }
                    return tempFile.absolutePath
                }
            } catch (e: Exception) {
                Log.e("MessageActivity", "Error handling content URI: ${e.message}")
                e.printStackTrace()
            }
        }

        // Document provider
        else if (DocumentsContract.isDocumentUri(context, uri)) {
            try {
                val tempFile = File(context.cacheDir, "temp_doc_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output -> input.copyTo(output) }
                }
                return tempFile.absolutePath
            } catch (e: Exception) {
                Log.e("MessageActivity", "Error handling document URI: ${e.message}")
                e.printStackTrace()
            }
        }

        // Fallback: create a temporary file
        try {
            val tempFile = File(context.cacheDir, "temp_file_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output -> input.copyTo(output) }
            }
            return tempFile.absolutePath
        } catch (e: Exception) {
            Log.e("MessageActivity", "Error creating temp file: ${e.message}")
            e.printStackTrace()
        }

        return null
    }

    private fun setupVoiceRecording() {
        binding.buttonVoiceRecord.setOnClickListener {
            if (checkRecordAudioPermission()) {
                if (isRecording) {
                    stopRecording()
                } else {
                    startRecording()
                }
            } else {
                requestRecordAudioPermission()
            }
        }
    }

    private fun startRecording() {
        try {
            // Ensure the directory exists
            val audioDir = getExternalFilesDir(null)
            audioDir?.mkdirs()

            // Initialize MediaRecorder with safer approach
            try {
                mediaRecorder =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            MediaRecorder(this)
                        } else {
                            @Suppress("DEPRECATION") MediaRecorder()
                        }

                mediaRecorder?.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    // Use proper AAC encoding with MPEG_4 container to ensure m4a compatibility
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setAudioSamplingRate(44100)
                    setAudioEncodingBitRate(128000)

                    // Ensure the file has .m4a extension - this is one of the required formats by
                    // the server
                    // Server accepts: mp3, wav, or m4a (as specified in the error message)
                    val audioFile = File(audioFilePath)
                    if (!audioFile.parentFile?.exists()!!) {
                        audioFile.parentFile?.mkdirs()
                    }
                    setOutputFile(audioFilePath)

                    prepare()
                    start()

                    // Update UI
                    isRecording = true
                    binding.buttonVoiceRecord.setImageResource(android.R.drawable.ic_media_pause)
                    binding.voiceRecordIndicator.visibility = View.VISIBLE
                    binding.voiceRecordIndicator.text = "Recording..."
                    selectedVoiceFile = null

                    // Animate recording indicator
                    val pulseAnimation =
                            AnimationUtils.loadAnimation(
                                    this@MessageActivity,
                                    android.R.anim.fade_in
                            )
                    pulseAnimation.repeatMode = Animation.REVERSE
                    pulseAnimation.repeatCount = Animation.INFINITE
                    binding.voiceRecordIndicator.startAnimation(pulseAnimation)

                    Log.d("VoiceDebug", "Started recording to $audioFilePath")
                }
            } catch (e: Exception) {
                Log.e("VoiceDebug", "Error with MediaRecorder: ${e.message}")
                e.printStackTrace()
                Toast.makeText(this, "Could not start recording: ${e.message}", Toast.LENGTH_SHORT)
                        .show()

                // Clean up on error
                mediaRecorder?.release()
                mediaRecorder = null
            }
        } catch (e: Exception) {
            Log.e("VoiceDebug", "General error starting recording: ${e.message}")
            e.printStackTrace()
            Toast.makeText(this, "Recording failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        try {
            if (isRecording) {
                mediaRecorder?.apply {
                    try {
                        stop()
                    } catch (e: Exception) {
                        Log.e("VoiceDebug", "Error stopping MediaRecorder: ${e.message}")
                    }
                    release()
                }

                // Update UI
                isRecording = false
                binding.buttonVoiceRecord.setImageResource(android.R.drawable.ic_btn_speak_now)
                binding.voiceRecordIndicator.clearAnimation()
                binding.voiceRecordIndicator.text = "Voice recorded"

                // Set the recorded file for sending
                val recordedFile = File(audioFilePath)

                // Verify the file exists and has content
                if (recordedFile.exists() && recordedFile.length() > 0) {
                    selectedVoiceFile = recordedFile
                    Log.d(
                            "VoiceDebug",
                            "Voice recording saved: ${recordedFile.length()} bytes at ${recordedFile.absolutePath}, file exists: ${recordedFile.exists()}, can read: ${recordedFile.canRead()}, extension: ${recordedFile.extension}"
                    )
                } else {
                    Log.e("VoiceDebug", "Recorded file is invalid or empty: $audioFilePath")
                    Toast.makeText(this, "Failed to save voice recording", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        } catch (e: Exception) {
            Log.e("VoiceDebug", "Error in stopRecording: ${e.message}")
            e.printStackTrace()
            Toast.makeText(this, "Failed to save recording", Toast.LENGTH_SHORT).show()
        } finally {
            // Clean up resources regardless of success/failure
            mediaRecorder = null
        }
    }

    private fun checkRecordAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start recording
                startRecording()
            } else {
                Toast.makeText(
                                this,
                                "Audio recording permission is required to send voice messages",
                                Toast.LENGTH_SHORT
                        )
                        .show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (isRecording) {
            stopRecording()
        }
    }
}
