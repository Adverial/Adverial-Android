package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.ui.dialog.ChooseImageResource
import com.application.adverial.ui.dialog.SinglePhotoViewer
import java.io.File
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.ClientConfiguration
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.application.adverial.BuildConfig

class NewAdImagesAdapter(var itemList: ArrayList<com.application.adverial.ui.model.Image>, var adId: String) : RecyclerView.Adapter<NewAdImagesAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val result = MutableLiveData<String>()
    private val filePaths = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewAdImagesAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_new_ad_image, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: NewAdImagesAdapter.ViewHolder, position: Int) {
        if (position == itemList.size) {
            val params: ViewGroup.MarginLayoutParams = holder.item.layoutParams as ViewGroup.MarginLayoutParams
            params.rightMargin = context.resources.getDimension(com.intuit.sdp.R.dimen._5sdp).toInt()
            holder.item.setOnClickListener {
                val dialog= ChooseImageResource()
                dialog.show((context as AppCompatActivity).supportFragmentManager, "ChooseImageResource")
                dialog.getData().observe(context as LifecycleOwner) {
                    result.value = "loading"
                    val imgFile = File(it[0])
                    if (imgFile.exists()) {
                        uploadToDigitalOcean(it[0])
                    }
                }
            }
        }else{
            holder.layout.visibility= View.GONE
            holder.delete.visibility= View.VISIBLE
            val imgFile = File(itemList[position].path)
            if (imgFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                holder.image.setImageBitmap(bitmap)
            }
            holder.image.setOnClickListener{
                val dialog= SinglePhotoViewer(itemList[holder.adapterPosition].path)
                dialog.show((context as AppCompatActivity).supportFragmentManager, "SinglePhotoViewer")
            }
            holder.delete.setOnClickListener {
                result.value= "loading"
                val repo= Repository(context)
                repo.deleteImage(itemList[holder.adapterPosition].id)
                repo.getDeleteImageData().observe(context as LifecycleOwner, {
                    val imagesData= context.getSharedPreferences("newAdImages", 0)
                    imagesData.edit().remove(itemList[holder.adapterPosition].id).apply()
                    result.value= "refresh"
                })
            }
        }
    }

        private fun uploadToDigitalOcean(filePath: String) {
        val credentials = BasicAWSCredentials(BuildConfig.DO_SPACES_KEY, BuildConfig.DO_SPACES_SECRET)
        val endpoint = BuildConfig.DO_SPACES_ENDPOINT
        val clientConfiguration = ClientConfiguration().apply {
            maxErrorRetry = 3
            connectionTimeout = 50000
            socketTimeout = 50000
        }
        val s3Client = AmazonS3Client(credentials, clientConfiguration).apply {
            setEndpoint(endpoint)
        }
        TransferNetworkLossHandler.getInstance(context)
        val transferUtility = TransferUtility.builder()
            .context(context)
            .s3Client(s3Client)
            .defaultBucket(BuildConfig.DO_SPACES_BUCKET)
            .build()
    
        val file = File(filePath)
        val fileName = "uploads/ad/${file.name}"
    
        val uploadObserver = transferUtility.upload(
            BuildConfig.DO_SPACES_BUCKET, // bucket name
            fileName, // key
            file, // file
            CannedAccessControlList.PublicRead // set ACL to public-read
        )
    
        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (state == TransferState.COMPLETED) {
                    filePaths.add(fileName)
                    result.value = "refresh"
                } else if (state == TransferState.FAILED) {
                    result.value = "error"
                }
            }
    
            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                // You can add progress update logic here if needed
            }
    
            override fun onError(id: Int, ex: Exception) {
                result.value = "error"
            }
        })
    }

    fun getFilePaths(): ArrayList<String> {
        return filePaths
    }

    override fun getItemCount(): Int { return itemList.size + 1 }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val image: ShapeableImageView= itemView.findViewById(R.id.itemNewAdImage_image)
        val layout: LinearLayout= itemView.findViewById(R.id.itemNewAdImage_layout)
        val delete: ImageView= itemView.findViewById(R.id.itemNewAd_delete)
        val item: ConstraintLayout= itemView.findViewById(R.id.itemNewAdImage_item)

        init {
            val radius= context.resources.getDimension(com.intuit.sdp.R.dimen._10sdp)
            image.shapeAppearanceModel= ShapeAppearanceModel().toBuilder().setAllCorners(CornerFamily.ROUNDED, radius).build()
        }
    }

    fun getResult(): MutableLiveData<String>{
        return result
    }
}