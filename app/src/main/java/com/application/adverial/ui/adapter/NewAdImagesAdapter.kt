package com.application.adverial.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.*
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.ClientConfiguration
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.application.adverial.BuildConfig
import com.application.adverial.remote.model.ImageItem
import com.application.adverial.ui.activity.NewAdImages
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class NewAdImagesAdapter(
    private val itemList: ArrayList<ImageItem>,
    private val adId: String
) : RecyclerView.Adapter<NewAdImagesAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val result = MutableLiveData<String>()
    private val filePaths = ArrayList<String>()
    private val fileUris = ArrayList<Uri>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_new_ad_image, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == itemList.size) {
            holder.layout.visibility = View.VISIBLE
            holder.delete.visibility = View.GONE
            holder.image.setImageResource(0)
            holder.item.setOnClickListener {
                if (context is AppCompatActivity) {
                    (context as NewAdImages).openGalleryForMedia()
                }
            }
        } else {
            holder.layout.visibility = View.GONE
            holder.delete.visibility = View.VISIBLE
            val imageItem = itemList[position]
            val uri = imageItem.uri
            holder.image.setImageURI(uri)
            val mimeType = context.contentResolver.getType(uri)

            if (mimeType != null) {
                when {
                    mimeType.startsWith("image") -> {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        holder.image.setImageBitmap(bitmap)
                    }
                    mimeType.startsWith("video") -> {
                        val thumbnail = ThumbnailUtils.createVideoThumbnail(
                            getRealPathFromURI(uri),
                            MediaStore.Images.Thumbnails.MINI_KIND
                        )
                        holder.image.setImageBitmap(thumbnail ?: BitmapFactory.decodeResource(context.resources, R.drawable.im_image))
                    }
                    else -> holder.image.setImageResource(R.drawable.ic_add)
                }
            }

            holder.delete.setOnClickListener {
                itemList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemList.size)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size + 1
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ShapeableImageView = itemView.findViewById(R.id.itemNewAdImage_image)
        val layout: LinearLayout = itemView.findViewById(R.id.itemNewAdImage_layout)
        val delete: ImageView = itemView.findViewById(R.id.itemNewAd_delete)
        val item: ConstraintLayout = itemView.findViewById(R.id.itemNewAdImage_item)

        init {
            val radius = context.resources.getDimension(com.intuit.sdp.R.dimen._10sdp)
            image.shapeAppearanceModel = ShapeAppearanceModel().toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, radius)
                .build()
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        var path = ""
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            it.moveToFirst()
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            path = it.getString(columnIndex)
            it.close()
        }
        return path
    }

    private fun compressImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val compressedFile = File(context.cacheDir, "compressed_${file.name}")
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(compressedFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        } catch (e: IOException) {
            throw e // Re-throw the exception to handle it upstream
        } finally {
            outputStream?.close() // Safely close the stream
            bitmap.recycle() // Free memory used by the bitmap
        }
        return compressedFile
    }

    private fun compressVideo(uri: Uri): File {
        val inputPath = getRealPathFromURI(uri)
        val outputFile = File(context.cacheDir, "compressed_${File(inputPath).name}")
        val outputPath = outputFile.absolutePath

        val command = arrayOf(
            "-i", inputPath,
            "-vf", "scale=640:-1",
            "-c:v", "mpeg4", // Fallback to a codec like mpeg4 if libx264 is unavailable
            "-preset", "ultrafast",
            "-crf", "28",
            outputPath
        )

        val session = FFmpegKit.execute(command.joinToString(" "))

        if (ReturnCode.isSuccess(session.returnCode)) {
            Log.i("FFmpegKit", "Video compression successful")
            return outputFile
        } else {
            Log.e("FFmpegKit", "Video compression failed: ${session.returnCode}")
            return File(inputPath)
        }
    }

    fun uploadToServer() {
        val credentials = BasicAWSCredentials(
            BuildConfig.DO_SPACES_KEY, BuildConfig.DO_SPACES_SECRET
        )
        val endpoint = BuildConfig.DO_SPACES_URL
        val clientConfig = ClientConfiguration().apply {
            maxErrorRetry = 3
            connectionTimeout = 50000
            socketTimeout = 50000
        }
        val s3Client = AmazonS3Client(credentials, clientConfig).apply {
            setEndpoint(endpoint)
        }
        TransferNetworkLossHandler.getInstance(context)
        val transferUtility = TransferUtility.builder()
            .context(context)
            .s3Client(s3Client)
            .defaultBucket(BuildConfig.DO_SPACES_BUCKET)
            .build()

        fileUris.forEach { uri ->
            val originalFile = File(getRealPathFromURI(uri))
            if (!originalFile.exists()) {
                Log.e("Upload", "File does not exist: ${originalFile.absolutePath}")
                return@forEach
            }

            val compressedFile = when {
                context.contentResolver.getType(uri)?.startsWith("image") == true -> compressImage(originalFile)
                context.contentResolver.getType(uri)?.startsWith("video") == true -> compressVideo(uri)
                else -> originalFile
            }

            val fileName = "uploads/ad/${compressedFile.name}"
            val uploadObserver = transferUtility.upload(
                BuildConfig.DO_SPACES_BUCKET,
                fileName,
                compressedFile,
                CannedAccessControlList.PublicRead
            )

            uploadObserver.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState) {
                    if (state == TransferState.COMPLETED) {
                        val fileUrl = "${BuildConfig.DO_SPACES_ENDPOINT}/$fileName"
                        filePaths.add(fileUrl)
                        if (filePaths.size == fileUris.size) {
                            result.value = "refresh"
                        }
                    } else if (state == TransferState.FAILED) {
                        result.value = "error"
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    // Log progress
                }

                override fun onError(id: Int, ex: Exception) {
                    Log.e("Upload", "Error uploading file: ${ex.message}")
                    result.value = "error"
                }
            })
        }
    }

    fun addImages(uris: List<Uri>) {
        for (uri in uris) {
            val id = UUID.randomUUID().toString()
            itemList.add(ImageItem(uri, id))
            fileUris.add(uri)
        }
        notifyDataSetChanged()
    }

    fun getFilePaths(): ArrayList<String> = filePaths
    fun getResult(): MutableLiveData<String> = result
    fun getFileUris(): ArrayList<Uri> = fileUris
}