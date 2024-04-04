package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.model.CategoryOptionsData
import com.application.adverial.ui.dialog.DropList
import java.util.*
import kotlin.collections.ArrayList

import android.view.Gravity
import android.widget.LinearLayout

import com.application.adverial.remote.Repository


class NewAdInfoAdapter(var itemList: ArrayList<CategoryOptionsData>) : RecyclerView.Adapter<NewAdInfoAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val result= MutableLiveData<kotlin.String>()
    private val currency= ArrayList<com.application.adverial.ui.model.DropList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewAdInfoAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_new_ad_info, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits", "RtlHardcoded")
    override fun onBindViewHolder(holder: NewAdInfoAdapter.ViewHolder, position: Int) {
        when(position){
            0 -> {
                holder.title.text= itemList[position].title
                holder.enter.visibility= View.VISIBLE
                holder.enter.layoutParams.height= context.resources.getDimension(R.dimen._30sdp).toInt()
                holder.enter.setPadding(context.resources.getDimension(R.dimen._5sdp).toInt(), context.resources.getDimension(R.dimen._5sdp).toInt(), context.resources.getDimension(R.dimen._5sdp).toInt(), context.resources.getDimension(R.dimen._5sdp).toInt())
                holder.enter.background= ContextCompat.getDrawable(context, R.drawable.rounded_full3)
                holder.enter.backgroundTintList= ContextCompat.getColorStateList(context, R.color.white_cover)
                holder.enter.addTextChangedListener(object: TextWatcher{
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        context.getSharedPreferences("newAd", 0).edit().putString("title", holder.enter.text.toString()).apply()
                    }
                    override fun afterTextChanged(p0: Editable?) {}
                })
            }
            1 -> {
                holder.title.text= itemList[position].title
                holder.priceA.visibility= View.VISIBLE
                /*val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
                holder.priceD.text= Currency.getInstance(Locale("", telephonyManager!!.networkCountryIso)).symbol?:""*/
                holder.priceA.addTextChangedListener(object: TextWatcher{
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        context.getSharedPreferences("newAd", 0).edit().putString("price", holder.priceA.text.toString()).apply()
                    }
                    override fun afterTextChanged(p0: Editable?) {}
                })
            }
            2 -> {
                holder.title.text= itemList[position].title
                holder.dropList.visibility= View.VISIBLE
                holder.value.setPadding(context.resources.getDimension(R.dimen._5sdp).toInt(), context.resources.getDimension(R.dimen._5sdp).toInt(), context.resources.getDimension(R.dimen._5sdp).toInt(), context.resources.getDimension(R.dimen._5sdp).toInt())
                holder.dropList.setOnClickListener {
                    result.value= "show"
                    currency.clear()
                    val repo= Repository(context)
                    repo.currency()
                    repo.getCurrencyData().observe(context as LifecycleOwner, {
                        for(i in it.data.indices) currency.add(com.application.adverial.ui.model.DropList(it.data[i].country + "   " + it.data[i].code, it.data[i].id.toString()))
                        val dialog= DropList(currency, context.resources.getString(R.string.new_ad_info_currency))
                        dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
                        dialog.getStatus().observe(context as LifecycleOwner, {
                            holder.dropList.visibility= View.GONE
                            holder.value.visibility= View.VISIBLE
                            holder.value.text= it.name.split("   ")[1]
                            holder.id.text= it.name.split("   ")[1]
                            context.getSharedPreferences("newAd", 0).edit().putString("currency", it.name.split("   ")[1]).apply()
                        })
                        result.value= "hide"
                    })
                }
                holder.value.setOnClickListener {
                    result.value= "show"
                    currency.clear()
                    val repo= Repository(context)
                    repo.currency()
                    repo.getCurrencyData().observe(context as LifecycleOwner, {
                        for(i in it.data.indices) currency.add(com.application.adverial.ui.model.DropList(it.data[i].country + "   " + it.data[i].code, it.data[i].id.toString()))
                        val dialog= DropList(currency, "currency")
                        dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
                        dialog.getStatus().observe(context as LifecycleOwner, {
                            holder.dropList.visibility= View.GONE
                            holder.value.visibility= View.VISIBLE
                            holder.value.text= it.name.split("   ")[1]
                            holder.id.text= it.name.split("   ")[1]
                            context.getSharedPreferences("newAd", 0).edit().putString("currency", it.name.split("   ")[1]).apply()
                        })
                        result.value= "hide"
                    })
                }
            }
            itemList.size - 1 -> {
                holder.title.text= itemList[position].title
                holder.enter.visibility= View.VISIBLE
                holder.enter.layoutParams.height= context.resources.getDimension(R.dimen._100sdp).toInt()
                holder.enter.setPadding(context.resources.getDimension(R.dimen._5sdp).toInt(), context.resources.getDimension(R.dimen._5sdp).toInt(),
                    context.resources.getDimension(R.dimen._5sdp).toInt(), context.resources.getDimension(R.dimen._5sdp).toInt())
                holder.enter.background= ContextCompat.getDrawable(context, R.drawable.rounded_full3)
                holder.enter.backgroundTintList= ContextCompat.getColorStateList(context, R.color.white_cover)
                holder.enter.gravity= Gravity.RIGHT or Gravity.TOP
                holder.enter.isSingleLine = false
                holder.enter.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
                holder.enter.addTextChangedListener(object: TextWatcher{
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        context.getSharedPreferences("newAd", 0).edit().putString("description", holder.enter.text.toString()).apply()
                    }
                    override fun afterTextChanged(p0: Editable?) {}
                })
            }
            else -> {
                holder.title.text= itemList[position].title
                holder.enter.layoutParams.height= context.resources.getDimension(R.dimen._30sdp).toInt()
                holder.enter.setPadding(context.resources.getDimension(R.dimen._5sdp).toInt(), context.resources.getDimension(R.dimen._5sdp).toInt(),
                    context.resources.getDimension(R.dimen._5sdp).toInt(), context.resources.getDimension(R.dimen._5sdp).toInt())
                holder.value.setPadding(context.resources.getDimension(R.dimen._5sdp).toInt(), context.resources.getDimension(R.dimen._5sdp).toInt(),
                    context.resources.getDimension(R.dimen._5sdp).toInt(), context.resources.getDimension(R.dimen._5sdp).toInt())
                if(itemList[position].values == null || itemList[position].values!!.isEmpty()){
                    holder.id.text= itemList[position].id.toString()
                    holder.enter.visibility= View.VISIBLE
                    holder.enter.background= ContextCompat.getDrawable(context, R.drawable.rounded_full3)
                    holder.enter.backgroundTintList= ContextCompat.getColorStateList(context, R.color.white_cover)
                    holder.enter.addTextChangedListener(object: TextWatcher{
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            if(holder.enter.text.isNotBlank()){
                                context.getSharedPreferences("newAdOptions", 0).edit().putString(itemList[holder.adapterPosition].id.toString(), holder.enter.text.toString()).apply()
                            }else{
                                context.getSharedPreferences("newAdOptions", 0).edit().remove(itemList[holder.adapterPosition].id.toString()).apply()
                            }
                        }
                        override fun afterTextChanged(p0: Editable?) {}
                    })
                }
                else holder.dropList.visibility= View.VISIBLE

                holder.dropList.setOnClickListener {
                    val data= ArrayList<com.application.adverial.ui.model.DropList>()
                    for(i in itemList[holder.adapterPosition].values!!.indices){
                        data.add(com.application.adverial.ui.model.DropList(itemList[holder.adapterPosition].values!![i].title, itemList[holder.adapterPosition].values!![i].id.toString()))
                    }
                    val dialog= DropList(data, itemList[position].title)
                    dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
                    dialog.getStatus().observe(context as LifecycleOwner, {
                        holder.dropList.visibility= View.GONE
                        holder.value.visibility= View.VISIBLE
                        holder.value.text= it.name
                        holder.id.text= it.id
                        context.getSharedPreferences("newAdOptions", 0).edit().putString(itemList[holder.adapterPosition].id.toString(), it.id).apply()
                    })
                }
                holder.value.setOnClickListener {
                    val data= ArrayList<com.application.adverial.ui.model.DropList>()
                    for(i in itemList[holder.adapterPosition].values!!.indices){
                        data.add(com.application.adverial.ui.model.DropList(itemList[holder.adapterPosition].values!![i].title, itemList[holder.adapterPosition].values!![i].id.toString()))
                    }
                    val dialog= DropList(data, itemList[position].title)
                    dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
                    dialog.getStatus().observe(context as LifecycleOwner, {
                        holder.dropList.visibility= View.GONE
                        holder.value.visibility= View.VISIBLE
                        holder.value.text= it.name
                        holder.id.text= it.id
                        context.getSharedPreferences("newAdOptions", 0).edit().putString(itemList[holder.adapterPosition].id.toString(), it.id).apply()
                    })
                }
            }
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView= itemView.findViewById(R.id.itemNewAdInfo_title)
        val value: TextView= itemView.findViewById(R.id.itemNewAdInfo_value)
        val enter: TextView= itemView.findViewById(R.id.itemNewAdInfo_enter)
        val dropList: ImageView= itemView.findViewById(R.id.itemNewAdInfo_dropList)
        val id: TextView= itemView.findViewById(R.id.itemNewAdInfo_id)
        val priceA: TextView= itemView.findViewById(R.id.itemNewAdInfo_priceA)
        val linearLayout12: LinearLayout= itemView.findViewById(R.id.linearLayout12)
    }

    fun getResult(): MutableLiveData<kotlin.String>{ return result }
}