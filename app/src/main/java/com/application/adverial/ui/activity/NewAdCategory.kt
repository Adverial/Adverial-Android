package com.application.adverial.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.SubCategory
import com.application.adverial.service.Tools
import com.application.adverial.ui.adapter.NewAdCategoryAdapter
import kotlinx.android.synthetic.main.activity_new_ad_category.*

class NewAdCategory : AppCompatActivity() {

    private var id= ""
    private var name= ""
    private var position= 0
    private var type= ""
    private var data= ArrayList<SubCategory>()
    private var subData= ArrayList<SubCategory>()
    private var nameArray= ArrayList<String>()
    private var positionArray= ArrayList<Int>()
    private var idArray= ArrayList<String>()
    private lateinit var adapter: NewAdCategoryAdapter

    override fun onBackPressed() {
        if(idArray.size > 1){
            idArray.removeAt(idArray.size-1)
            nameArray.removeAt(nameArray.size-1)
            positionArray.removeAt(positionArray.size-1)
            updateCheck()
        }else super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_ad_category)
        Tools().rotateLayout(this,newAdCategory_back)
        Tools().changeViewFromTheme(this,newAddCategoryRoot)
        pageInit()
    }

    private fun pageInit(){
        lottie4.visibility= View.VISIBLE
        Tools().viewEnable(this.window.decorView.rootView, false)
        id= intent.getStringExtra("id")!!
        name= intent.getStringExtra("name")!!
        position= intent.getIntExtra("position", 0)
        subCategory_name.text= name
        newAdCategory_recyclerView.layoutManager= LinearLayoutManager(this)
        adapter= NewAdCategoryAdapter(subData)
        newAdCategory_recyclerView.adapter= adapter
        val repo= Repository(this)
        repo.mainCategory()
        repo.getMainCategoryData().observe(this, {
            data= it.data as ArrayList<SubCategory>
            idArray.add(id)
            nameArray.add(name)
            positionArray.add(position)
            updateCheck()
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateCheck(){
        lottie4.visibility= View.GONE
        Tools().viewEnable(this.window.decorView.rootView, true)
        when(positionArray.size){
            1 -> {
                if(data[positionArray[0]].parent_categories.isNotEmpty()){
                    subData= data[positionArray[0]].parent_categories as ArrayList<SubCategory>
                    update()
                }else nextPage()
            }
            2 -> {
                if(data[positionArray[0]].parent_categories[positionArray[1]].parent_categories.isNotEmpty()){
                    subData= data[positionArray[0]].parent_categories[positionArray[1]].parent_categories as ArrayList<SubCategory>
                    update()
                }else nextPage()
            }
            3 -> {
                if(data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories.isNotEmpty()){
                    subData= data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories as ArrayList<SubCategory>
                    update()
                }else nextPage()
            }
            4 -> {
                if(data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories[positionArray[3]].parent_categories.isNotEmpty()){
                    subData= data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories[positionArray[3]].parent_categories as ArrayList<SubCategory>
                    update()
                }else nextPage()
            }
            5 -> {
                if(data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories[positionArray[3]].parent_categories[positionArray[4]].parent_categories.isNotEmpty()){
                    subData= data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories[positionArray[3]].parent_categories[positionArray[4]].parent_categories as ArrayList<SubCategory>
                    update()
                }else nextPage()
            }
            6 -> {
                if(data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories[positionArray[3]].parent_categories[positionArray[4]].parent_categories[positionArray[5]].parent_categories.isNotEmpty()){
                    subData= data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories[positionArray[3]].parent_categories[positionArray[4]].parent_categories[positionArray[5]].parent_categories as ArrayList<SubCategory>
                    update()
                }else nextPage()
            }
            7 -> {
                if(data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories[positionArray[3]].parent_categories[positionArray[4]].parent_categories[positionArray[5]].parent_categories[positionArray[6]].parent_categories.isNotEmpty()){
                    subData= data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories[positionArray[3]].parent_categories[positionArray[4]].parent_categories[positionArray[5]].parent_categories[positionArray[6]].parent_categories as ArrayList<SubCategory>
                    update()
                }else nextPage()
            }
            8 -> {
                if(data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories[positionArray[3]].parent_categories[positionArray[4]].parent_categories[positionArray[5]].parent_categories[positionArray[6]].parent_categories[positionArray[7]].parent_categories.isNotEmpty()){
                    subData= data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories[positionArray[3]].parent_categories[positionArray[4]].parent_categories[positionArray[5]].parent_categories[positionArray[6]].parent_categories[positionArray[7]].parent_categories as ArrayList<SubCategory>
                    update()
                }else nextPage()
            }
            9 -> {
                if(data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories[positionArray[3]].parent_categories[positionArray[4]].parent_categories[positionArray[5]].parent_categories[positionArray[6]].parent_categories[positionArray[7]].parent_categories[positionArray[8]].parent_categories.isNotEmpty()){
                    subData= data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories[positionArray[3]].parent_categories[positionArray[4]].parent_categories[positionArray[5]].parent_categories[positionArray[6]].parent_categories[positionArray[7]].parent_categories[positionArray[8]].parent_categories as ArrayList<SubCategory>
                    update()
                }else nextPage()
            }
            10 -> {
                if(data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories[positionArray[3]].parent_categories[positionArray[4]].parent_categories[positionArray[5]].parent_categories[positionArray[6]].parent_categories[positionArray[7]].parent_categories[positionArray[8]].parent_categories[positionArray[9]].parent_categories.isNotEmpty()){
                    subData= data[positionArray[0]].parent_categories[positionArray[1]].parent_categories[positionArray[2]].parent_categories[positionArray[3]].parent_categories[positionArray[4]].parent_categories[positionArray[5]].parent_categories[positionArray[6]].parent_categories[positionArray[7]].parent_categories[positionArray[8]].parent_categories[positionArray[9]].parent_categories as ArrayList<SubCategory>
                    update()
                }else nextPage()
            }
        }
    }

    private fun renameTitle(){
        subCategory_name.text= ""
        for(i in nameArray.indices){
            subCategory_name.append(nameArray[i])
            if(i < nameArray.size-1){
                subCategory_name.append(" > ")
            }
        }
    }

    private fun nextPage(){
        if(Tools().authCheck(this)){
            var id= ""
            for(i in idArray.indices){
                id += idArray[i]
                if(i < idArray.size-1){
                    id += ","
                }
            }
            val intent= Intent(this, NewAdInfo()::class.java)
            intent.putExtra("type", type)
            intent.putExtra("idArray", id)
            startActivity(intent)
        }else{
            val intent= Intent(this, Login()::class.java)
            startActivity(intent)
        }
        idArray.removeAt(idArray.size-1)
        nameArray.removeAt(nameArray.size-1)
        positionArray.removeAt(positionArray.size-1)
        updateCheck()
    }

    private fun update(){
        adapter= NewAdCategoryAdapter(subData)
        newAdCategory_recyclerView.adapter= adapter
        renameTitle()
        adapter.getResult().observe(this, {
            nameArray.add(it.name)
            idArray.add(it.id)
            positionArray.add(it.position)
            type= it.type
            updateCheck()
        })
    }

    fun back(view: View){
        if(idArray.size > 1){
            idArray.removeAt(idArray.size-1)
            nameArray.removeAt(nameArray.size-1)
            positionArray.removeAt(positionArray.size-1)
            updateCheck()
        }else super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language =  getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "" ||language == "0" || language == "1") window.decorView.layoutDirection= View.LAYOUT_DIRECTION_LTR
        else window.decorView.layoutDirection= View.LAYOUT_DIRECTION_RTL
    }
}