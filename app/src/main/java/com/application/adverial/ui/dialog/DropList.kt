package com.application.adverial.ui.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.ui.adapter.DropListAdapter
import com.application.adverial.ui.model.DropList
import com.application.adverial.ui.model.DropList1


class DropList(var itemList: ArrayList<DropList>, var title: String) : DialogFragment(){

    private val status= MutableLiveData<DropList>()
    private val items= ArrayList<DropList1>()
    private var data= DropList("", "")

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
        val dialogTitle= view.findViewById<TextView>(R.id.dropList_title)
        val ok= view.findViewById<Button>(R.id.dropList_ok)
        dialogTitle.text= title
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        if(itemList.size > 10) recyclerView.layoutParams.height= resources.getDimension(R.dimen._300sdp).toInt()
        for(i in itemList.indices) items.add(DropList1(itemList[i].name, itemList[i].id, -1))
        val adapter= DropListAdapter(items)
        recyclerView.adapter= adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(itemList.size)
        adapter.getResult().observe(viewLifecycleOwner, {
            data= DropList(it.name, it.id)
            items.clear()
            for(i in itemList.indices){
                items.add(DropList1(itemList[i].name, itemList[i].id, it.position))
            }
            adapter.notifyDataSetChanged()
        })
        ok.setOnClickListener{
            if(data.name != "") {
                status.value = data
                dialog!!.dismiss()
            }
        }
    }

    fun getStatus(): MutableLiveData<DropList>{ return status }
}