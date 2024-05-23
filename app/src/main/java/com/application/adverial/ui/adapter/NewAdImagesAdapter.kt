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
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.ui.dialog.ChooseImageResource
import com.application.adverial.ui.dialog.SinglePhotoViewer
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import java.io.File

class NewAdImagesAdapter(var itemList: ArrayList<com.application.adverial.ui.model.Image>, var adId: String) : RecyclerView.Adapter<NewAdImagesAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val result= MutableLiveData<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewAdImagesAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_new_ad_image, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: NewAdImagesAdapter.ViewHolder, position: Int) {
        if(position == itemList.size){
            val params: ViewGroup.MarginLayoutParams = holder.item.layoutParams as ViewGroup.MarginLayoutParams
            params.rightMargin = context.resources.getDimension(com.intuit.sdp.R.dimen._5sdp).toInt()
            holder.item.setOnClickListener {
                val dialog= ChooseImageResource()
                dialog.show((context as AppCompatActivity).supportFragmentManager, "ChooseImageResource")
                dialog.getData().observe(context as LifecycleOwner) {
                    result.value = "loading"
                    val imgFile = File(it[0])
                    if (imgFile.exists()) {
                        val repo = Repository(context)
                        repo.image(it[0], adId)
                        repo.getImageData().observe(context as LifecycleOwner) { itt ->
                            if (itt.status) {
                                context.getSharedPreferences("newAdImages", 0).edit().putString(itt.data.image_id.toString(), it[0]).apply()
                                result.value = "refresh"
                            }
                        }
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