package com.application.adverial.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.NewAdImagesAdapter
import com.application.adverial.ui.dialog.NewAdCompletedDialog
import com.application.adverial.ui.model.Image
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.wookweb.lezzetapp.service.ItemDecorations
import kotlinx.android.synthetic.main.activity_new_ad_images.home_menu3
import kotlinx.android.synthetic.main.activity_new_ad_images.lottie10
import kotlinx.android.synthetic.main.activity_new_ad_images.newAdImageRoot
import kotlinx.android.synthetic.main.activity_new_ad_images.newAdImages_recyclerView
import kotlinx.android.synthetic.main.activity_new_ad_images.publishName
import kotlinx.android.synthetic.main.activity_new_ad_images.publishPhone
import kotlinx.android.synthetic.main.activity_new_ad_images.publishRadio1

class NewAdImages : AppCompatActivity() {

    val images= ArrayList<Image>()
    private var adId= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_ad_images)
        Tools().rotateLayout(this,home_menu3)
        Tools().changeViewFromTheme(this,newAdImageRoot)

        permissions()
        setDefaultUserDetails()
        publishPhone.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(publishPhone.text.toString().isNotEmpty()){
                    publishPhone.removeTextChangedListener(this)
                    if(publishPhone.text.toString().length > 1) publishPhone.setText("0"+ publishPhone.text.toString().substring(1))
                    else publishPhone.setText("0")
                    var phoneText= ""
                    for((c, i) in (0 until publishPhone.text.toString().replace(" ", "").length).withIndex()){
                        phoneText+= publishPhone.text.toString().replace(" ", "").substring(i, i+1)
                        if((c == 3 || c == 6) && i != publishPhone.text.toString().replace(" ", "").length - 1) phoneText += " "
                    }
                    publishPhone.setText(phoneText)
                    publishPhone.addTextChangedListener(this)
                    publishPhone.setSelection(publishPhone.text.toString().length)
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        pageInit()
    }

  private  fun setDefaultUserDetails(){
      if (Tools().authCheck(this)) {
          val repo = Repository(this)
          repo.user()

          repo.getUserData().observe(this) { userData ->
              userData?.let {
                  publishPhone.setText(userData.data.phone)
                  publishName.setText(userData.data.name)

              }
          }

      }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun pageInit(){
        adId= intent.getStringExtra("adId")!!
        newAdImages_recyclerView.layoutManager= GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
        newAdImages_recyclerView.addItemDecoration(ItemDecorations(this).images(images.size))
        updateData()
    }

    private fun updateData(){
        images.clear()
        val imagesData= getSharedPreferences("newAdImages", 0)
        val allEntries: Map<String, *> = imagesData.all
        for ((key, value) in allEntries) {
            images.add(Image(value.toString(), key))
        }
        images.sortWith { lhs, rhs -> lhs.id.compareTo(rhs.id) }
        val adapter= NewAdImagesAdapter(images, adId)
        newAdImages_recyclerView.adapter= adapter
        newAdImages_recyclerView.setHasFixedSize(true)
        newAdImages_recyclerView.setItemViewCacheSize(images.size + 1)
        adapter.getResult().observe(this, {
            if(it == "loading"){
                lottie10.visibility= View.VISIBLE
                Tools().viewEnable(this.window.decorView.rootView, false)
            }
            if(it == "refresh"){
                lottie10.visibility= View.GONE
                Tools().viewEnable(this.window.decorView.rootView, true)
                updateData()
            }
        })
    }

    fun next(view: View){
        if(publishName.text.isNotBlank() && publishPhone.text.toString().replace(" ", "").length == 11){
            if(images.isNotEmpty()){
                lottie10.visibility= View.VISIBLE
                Tools().viewEnable(this.window.decorView.rootView, false)
                var type= ""
                type = when {
                    publishRadio1.isChecked -> "1"
//                    publishRadio2.isChecked -> "2"
                    else -> "3"
                }
                val repo= Repository(this)
                repo.publishAd(adId, publishPhone.text.toString().replace(" ", ""), publishName.text.toString(), type)
                repo.getPublishAdData().observe(this, {
                    lottie10.visibility= View.GONE
                    Tools().viewEnable(this.window.decorView.rootView, true)
                    if(it.status){
                        val dialog= NewAdCompletedDialog(it.data.ad_no.toString())
                        dialog.show(supportFragmentManager, "NewAdCompletedDialog")
                    }
                })
            }else Toast.makeText(this, resources.getString(R.string.uploadImage), Toast.LENGTH_SHORT).show()
        }else Toast.makeText(this, resources.getString(R.string.fieldsAreEmpty), Toast.LENGTH_SHORT).show()
    }

    private fun permissions(){
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {}
            override fun onPermissionDenied(deniedPermissions: List<String?>) {}
        }
        TedPermission.with(this).setPermissionListener(permissionListener).setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).check()
    }

    fun back(view: View){
        finish()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language =  getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" ||language == "0" || language == "1") window.decorView.layoutDirection= View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection= View.LAYOUT_DIRECTION_RTL
    }
}