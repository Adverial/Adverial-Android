package com.application.adverial.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.ui.adapter.LanguageAdapter
import com.application.adverial.ui.model.Language
import com.application.adverial.ui.model.Language1
import java.util.*
import kotlin.collections.ArrayList


class Language() : DialogFragment(){

    private val status= MutableLiveData<Language>()
    private var items= ArrayList<Language1>()
    private var data= Language("", "", 0)



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.dialog_background1)
        val view= inflater.inflate(R.layout.dialog_drop_list, container, false)
        val window: Window? = dialog!!.window
        val wlp: WindowManager.LayoutParams = window!!.attributes
        //dialog!!.window?.attributes?.windowAnimations = R.style.DialogAnimation
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp

        uiSetup(view)

        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun uiSetup(view: View){
        val recyclerView= view.findViewById<RecyclerView>(R.id.dropList_recyclerView)
        val ok= view.findViewById<Button>(R.id.dropList_ok)
        val itemList= ArrayList<Language>()
        itemList.add(Language("English", "0", R.drawable.im_flag_england))
        itemList.add(Language("Turkish", "1", R.drawable.img_turkey_flag))
        itemList.add(Language("Arabic", "2", R.drawable.img_flag_arabic))
        itemList.add(Language("Kurdish Badini", "3", R.drawable.img_irak_flag))
        itemList.add(Language("Kurdish Sorani", "4", R.drawable.img_irak_flag))
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        val selected= requireContext().getSharedPreferences("user", 0).getString("languageId", "0")!!.toInt()
        for(i in itemList.indices){
            items.add(Language1(itemList[i].name, itemList[i].id, itemList[i].flag, selected))
        }
        val adapter= LanguageAdapter(items)
        recyclerView.adapter= adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(itemList.size)
        adapter.getResult().observe(viewLifecycleOwner, {
            data= Language(it.name, it.id, it.flag)
            items.clear()
            for(i in itemList.indices){
                items.add(Language1(itemList[i].name, itemList[i].id, itemList[i].flag, it.position))
            }
            adapter.notifyDataSetChanged()
        })
        ok.setOnClickListener{
            if(data.name.isNotBlank()){
                if (data.id == "0"){
                    changeLanguage(requireContext(),"en")
                    requireContext().getSharedPreferences("user", 0).edit().putString("local", "en").apply()
                }
                if (data.id == "1"){
                    changeLanguage(requireContext(),"tr")
                    requireContext().getSharedPreferences("user", 0).edit().putString("local", "tr").apply()
                }
                if (data.id == "2"){
                    changeLanguage(requireContext(),"ar")
                    requireContext().getSharedPreferences("user", 0).edit().putString("local", "ar").apply()
                }
                if (data.id == "3"){
                    changeLanguage(requireContext(),"ku")
                    requireContext().getSharedPreferences("user", 0).edit().putString("local", "ku").apply()
                }
                if (data.id == "4"){
                    changeLanguage(requireContext(),"kmr")
                    requireContext().getSharedPreferences("user", 0).edit().putString("local", "kmr").apply()
                }
                status.value= data
            }
            dialog!!.dismiss()
        }
    }

    fun getStatus(): MutableLiveData<Language>{ return status }

    private fun changeLanguage(context: Context, language : String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        val activity = requireActivity()
        if (language == "en" || language == "tr"){
            requireActivity().window.decorView.layoutDirection= View.LAYOUT_DIRECTION_LTR
        }else{
            requireActivity().window.decorView.layoutDirection= View.LAYOUT_DIRECTION_RTL
        }
        val intent = activity.intent
        activity.overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        activity.finish()
        activity.overridePendingTransition(0, 0)
        activity.startActivity(intent)
    }
}