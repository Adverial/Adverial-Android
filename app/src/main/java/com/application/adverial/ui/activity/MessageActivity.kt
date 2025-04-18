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
                                        Log.d(
                                                "MediaDebug",
                                                "Preparing media file: ${mediaFile.absolutePath}, size: ${mediaFile.length()} bytes"
                                        )
                                        // Ensure we're creating a proper image file RequestBody
                                        val mediaType =
                                                when {
                                                    path.toLowerCase().endsWith(".jpg") ||
                                                            path.toLowerCase().endsWith(".jpeg") ->
                                                            "image/jpeg"
                                                    path.toLowerCase().endsWith(".png") ->
                                                            "image/png"
                                                    else -> "image/jpeg" // default to jpeg
                                                }
                                        Log.d(
                                                "MediaDebug",
                                                "Using media type: $mediaType for file: $path"
                                        )
                                        RequestBody.create(MediaType.get(mediaType), mediaFile)
                                    } else {
                                        Log.e(
                                                "MediaDebug",
                                                "Media file invalid or empty: $path, exists: ${mediaFile.exists()}, size: ${mediaFile.length()}"
                                        )
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
                                Log.e("MediaDebug", "Error processing media: ${e.message}")
                                e.printStackTrace()
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
        updateVoiceButtonState()
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
        Log.d("MediaDebug", "Getting path from URI: $uri")

        // Direct file path
        if ("file".equals(uri.scheme, ignoreCase = true)) {
            val path = uri.path
            Log.d("MediaDebug", "File URI path: $path")
            return path
        }

        // MediaStore content
        else if ("content".equals(uri.scheme, ignoreCase = true)) {
            try {
                // Try to get path from content resolver
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        val path = cursor.getString(columnIndex)
                        Log.d("MediaDebug", "Content resolver path: $path")

                        // Verify the file exists and has content
                        val file = File(path)
                        if (file.exists() && file.length() > 0) {
                            return path
                        } else {
                            Log.e(
                                    "MediaDebug",
                                    "File exists: ${file.exists()}, size: ${file.length()}"
                            )
                        }
                    }
                }

                // If cannot get path or file is empty, create a temporary file
                Log.d("MediaDebug", "Creating temporary file from content URI")
                val mimeType = context.contentResolver.getType(uri)
                val extension =
                        when {
                            mimeType?.contains("jpeg") == true ||
                                    mimeType?.contains("jpg") == true -> ".jpg"
                            mimeType?.contains("png") == true -> ".png"
                            else -> ".jpg"
                        }

                val tempFile =
                        File(context.cacheDir, "temp_media_${System.currentTimeMillis()}$extension")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output ->
                        val bytesCopied = input.copyTo(output)
                        Log.d("MediaDebug", "Copied $bytesCopied bytes to ${tempFile.absolutePath}")
                    }
                }

                if (tempFile.exists() && tempFile.length() > 0) {
                    Log.d(
                            "MediaDebug",
                            "Temp file created: ${tempFile.absolutePath}, size: ${tempFile.length()}"
                    )
                    return tempFile.absolutePath
                } else {
                    Log.e(
                            "MediaDebug",
                            "Failed to create valid temp file, exists: ${tempFile.exists()}, size: ${tempFile.length()}"
                    )
                    return null
                }
            } catch (e: Exception) {
                Log.e("MediaDebug", "Error handling content URI: ${e.message}")
                e.printStackTrace()
            }
        }

        // Document provider
        else if (DocumentsContract.isDocumentUri(context, uri)) {
            try {
                Log.d("MediaDebug", "Handling document URI")
                val tempFile = File(context.cacheDir, "temp_doc_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output ->
                        val bytesCopied = input.copyTo(output)
                        Log.d("MediaDebug", "Copied $bytesCopied bytes to ${tempFile.absolutePath}")
                    }
                }

                if (tempFile.exists() && tempFile.length() > 0) {
                    Log.d(
                            "MediaDebug",
                            "Document temp file created: ${tempFile.absolutePath}, size: ${tempFile.length()}"
                    )
                    return tempFile.absolutePath
                } else {
                    Log.e(
                            "MediaDebug",
                            "Failed to create valid doc temp file, exists: ${tempFile.exists()}, size: ${tempFile.length()}"
                    )
                    return null
                }
            } catch (e: Exception) {
                Log.e("MediaDebug", "Error handling document URI: ${e.message}")
                e.printStackTrace()
            }
        }

        // Fallback: create a temporary file
        try {
            Log.d("MediaDebug", "Using fallback method for URI")
            val tempFile = File(context.cacheDir, "temp_file_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    val bytesCopied = input.copyTo(output)
                    Log.d("MediaDebug", "Copied $bytesCopied bytes to ${tempFile.absolutePath}")
                }
            }

            if (tempFile.exists() && tempFile.length() > 0) {
                Log.d(
                        "MediaDebug",
                        "Fallback temp file created: ${tempFile.absolutePath}, size: ${tempFile.length()}"
                )
                return tempFile.absolutePath
            } else {
                Log.e(
                        "MediaDebug",
                        "Failed to create valid fallback temp file, exists: ${tempFile.exists()}, size: ${tempFile.length()}"
                )
                return null
            }
        } catch (e: Exception) {
            Log.e("MediaDebug", "Error creating fallback temp file: ${e.message}")
            e.printStackTrace()
        }

        Log.e("MediaDebug", "Could not get path for URI: $uri")
        return null
    }

    private fun setupVoiceRecording() {
        // Check if permission is already granted and update UI accordingly
        updateVoiceButtonState()

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

    private fun updateVoiceButtonState() {
        // Visual indication of permission status
        if (checkRecordAudioPermission()) {
            binding.buttonVoiceRecord.alpha = 1.0f
        } else {
            binding.buttonVoiceRecord.alpha = 0.7f
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
        // Show explanation dialog first
        showPermissionExplanationDialog()
    }

    private fun showPermissionExplanationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Voice Messages")
                .setMessage(
                        "To send voice messages, we need permission to access your microphone. This permission is only used when you press and hold the record button."
                )
                .setPositiveButton("Allow") { _, _ ->
                    ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.RECORD_AUDIO),
                            REQUEST_RECORD_AUDIO_PERMISSION
                    )
                }
                .setNegativeButton("Not Now") { dialog, _ ->
                    dialog.dismiss()
                    Toast.makeText(
                                    this,
                                    "Voice messaging is disabled without microphone permission",
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                }
                .setCancelable(false)
                .show()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, update UI and start recording
                updateVoiceButtonState()
                startRecording()
            } else {
                Toast.makeText(
                                this,
                                "Voice messages require microphone permission. You can still send text and images.",
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
