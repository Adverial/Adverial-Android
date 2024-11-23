// NewAdImagesAdapter.kt
package com.application.adverial.ui.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
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
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.*
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.ClientConfiguration
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.application.adverial.BuildConfig
import com.application.adverial.remote.model.ImageItem
import com.application.adverial.ui.activity.NewAdImages
import java.io.File
import java.util.UUID

class NewAdImagesAdapter(
    private val itemList: ArrayList<ImageItem>,
    private val adId: String
) : RecyclerView.Adapter<NewAdImagesAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val result = MutableLiveData<String>()
    private val filePaths = ArrayList<String>()
    private val fileUris = ArrayList<Uri>()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_new_ad_image, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int
    ) {
        if (position == itemList.size) {
            holder.layout.visibility = View.VISIBLE
            holder.delete.visibility = View.GONE
            holder.image.setImageResource(R.drawable.ic_add)
            holder.item.setOnClickListener {
                if (context is AppCompatActivity) {
                    (context as AppCompatActivity).let { activity ->
                        (activity as NewAdImages).openGalleryForMedia()
                    }
                }
            }
        } else {
            holder.layout.visibility = View.GONE
            holder.delete.visibility = View.VISIBLE
            val imageItem = itemList[position]
            val uri = imageItem.uri
            val mimeType = context.contentResolver.getType(uri)

            if (mimeType != null) {
                when {
                    mimeType.startsWith("image") -> {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        holder.image.setImageBitmap(bitmap)
                    }
                    mimeType.startsWith("video") -> {
                        val bitmap = ThumbnailUtils.createVideoThumbnail(
                            getRealPathFromURI(uri),
                            MediaStore.Images.Thumbnails.MINI_KIND
                        )
                        holder.image.setImageBitmap(bitmap)
                    }
                    else -> holder.image.setImageResource(R.drawable.ic_add)
                }
            }

            holder.image.setOnClickListener {
                // Implement preview functionality if needed
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

    fun uploadToDigitalOcean() {
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

        var filesUploaded = 0
        for (uri in fileUris) {
            val filePath = getRealPathFromURI(uri)
            val file = File(filePath)
            val fileName = "uploads/ad/${file.name}"

            val uploadObserver = transferUtility.upload(
                BuildConfig.DO_SPACES_BUCKET,
                fileName,
                file,
                CannedAccessControlList.PublicRead
            )

            uploadObserver.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState) {
                    if (state == TransferState.COMPLETED) {
                        val fileUrl = "https://${BuildConfig.DO_SPACES_BUCKET}." +
                                "${BuildConfig.DO_SPACES_ENDPOINT}/$fileName"
                        filePaths.add(fileUrl)
                        filesUploaded++
                        if (filesUploaded == fileUris.size) {
                            result.value = "refresh"
                        }
                    } else if (state == TransferState.FAILED) {
                        result.value = "error"
                    }
                }

                override fun onProgressChanged(
                    id: Int, bytesCurrent: Long, bytesTotal: Long
                ) {
                    // Update progress if needed
                }

                override fun onError(id: Int, ex: Exception) {
                    result.value = "error"
                }
            })
        }
    }

    fun getFilePaths(): ArrayList<String> {
        return filePaths
    }

    fun getResult(): MutableLiveData<String> {
        return result
    }

    fun getFileUris(): ArrayList<Uri> {
        return fileUris
    }

    fun addImages(uris: List<Uri>) {
        for (uri in uris) {
            val id = UUID.randomUUID().toString()
            itemList.add(ImageItem(uri, id))
            fileUris.add(uri)
        }
        notifyDataSetChanged()
    }
}