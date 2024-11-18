package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.CategoryOptionsData
import com.application.adverial.ui.dialog.DropList

class FilterAdapter(val type: String) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val status= MutableLiveData<String>()
    private var country= ""
    private var city= ""
    private var district= ""
    private val itemList= ArrayList<com.application.adverial.ui.model.DropList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterAdapter.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        context= parent.context
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onBindViewHolder(holder: FilterAdapter.ViewHolder, position: Int) {
        holder.countryDrop.setOnClickListener {
            status.value= "show"
            val repo= Repository(context)
            repo.country()
            repo.getCountryData().observe(context as LifecycleOwner, { itt->
                status.value= "hide"
                itemList.clear()
                for(i in itt.data.indices){ itemList.add(com.application.adverial.ui.model.DropList(itt.data[i].name, itt.data[i].id.toString())) }
                val dialog= DropList(itemList, context.resources.getString(R.string.address_country))
                dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
                dialog.getStatus().observe(context as LifecycleOwner, { result ->
                    holder.countryValue.text= result.name
                    holder.countryDrop.visibility= View.GONE
                    holder.countryValue.visibility= View.VISIBLE
                    country= result.id
                    city= ""
                    district= ""
                    holder.cityDrop.visibility= View.VISIBLE
                    holder.districtDrop.visibility= View.VISIBLE
                    holder.cityValue.visibility= View.GONE
                    holder.districtValue.visibility= View.GONE
                    context.getSharedPreferences("filter", 0).edit().putString("country", country).putString("city", "").putString("district", "").apply()
                })
            })
        }
        holder.countryValue.setOnClickListener {
            status.value= "show"
            val repo= Repository(context)
            repo.country()
            repo.getCountryData().observe(context as LifecycleOwner, { itt->
                status.value= "hide"
                itemList.clear()
                for(i in itt.data.indices){ itemList.add(com.application.adverial.ui.model.DropList(itt.data[i].name, itt.data[i].id.toString())) }
                val dialog= DropList(itemList, context.resources.getString(R.string.address_country))
                dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
                dialog.getStatus().observe(context as LifecycleOwner, { result ->
                    holder.countryValue.text= result.name
                    holder.countryDrop.visibility= View.GONE
                    holder.countryValue.visibility= View.VISIBLE
                    country= result.id
                    city= ""
                    district= ""
                    holder.cityDrop.visibility= View.VISIBLE
                    holder.districtDrop.visibility= View.VISIBLE
                    holder.cityValue.visibility= View.GONE
                    holder.districtValue.visibility= View.GONE
                    context.getSharedPreferences("filter", 0).edit().putString("country", country).putString("city", "").putString("district", "").apply()
                })
            })
        }
        holder.cityDrop.setOnClickListener {
            status.value= "show"
            val repo= Repository(context)
            repo.city(country)
            repo.getCityData().observe(context as LifecycleOwner, { itt->
                status.value= "hide"
                itemList.clear()
                for(i in itt.data.indices){ itemList.add(com.application.adverial.ui.model.DropList(itt.data[i].name, itt.data[i].id.toString())) }
                val dialog= DropList(itemList, context.resources.getString(R.string.address_province))
                dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
                dialog.getStatus().observe(context as LifecycleOwner, { result ->
                    holder.cityValue.text= result.name
                    holder.cityDrop.visibility= View.GONE
                    holder.cityValue.visibility= View.VISIBLE
                    city= result.id
                    district= ""
                    holder.districtDrop.visibility= View.VISIBLE
                    holder.districtValue.visibility= View.GONE
                    context.getSharedPreferences("filter", 0).edit().putString("city", city).putString("district", "").apply()
                })
            })
        }
        holder.cityValue.setOnClickListener {
            status.value= "show"
            val repo= Repository(context)
            repo.city(country)
            repo.getCityData().observe(context as LifecycleOwner, { itt->
                status.value= "hide"
                itemList.clear()
                for(i in itt.data.indices){ itemList.add(com.application.adverial.ui.model.DropList(itt.data[i].name, itt.data[i].id.toString())) }
                val dialog= DropList(itemList, context.resources.getString(R.string.address_province))
                dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
                dialog.getStatus().observe(context as LifecycleOwner, { result ->
                    holder.cityValue.text= result.name
                    holder.cityDrop.visibility= View.GONE
                    holder.cityValue.visibility= View.VISIBLE
                    city= result.id
                    district= ""
                    holder.districtDrop.visibility= View.VISIBLE
                    holder.districtValue.visibility= View.GONE
                    context.getSharedPreferences("filter", 0).edit().putString("city", city).putString("district", "").apply()
                })
            })
        }
        holder.districtDrop.setOnClickListener {
            status.value= "show"
            val repo= Repository(context)
            repo.district(city)
            repo.getDistrictData().observe(context as LifecycleOwner, { itt->
                status.value= "hide"
                itemList.clear()
                for(i in itt.data.indices){ itemList.add(com.application.adverial.ui.model.DropList(itt.data[i].name, itt.data[i].id.toString())) }
                val dialog= DropList(itemList, context.resources.getString(R.string.address_district))
                dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
                dialog.getStatus().observe(context as LifecycleOwner, { result ->
                    holder.districtValue.text= result.name
                    holder.districtDrop.visibility= View.GONE
                    holder.districtValue.visibility= View.VISIBLE
                    district= result.id
                    context.getSharedPreferences("filter", 0).edit().putString("district", district).apply()
                })
            })
        }
        holder.districtValue.setOnClickListener {
            status.value= "show"
            val repo= Repository(context)
            repo.district(city)
            repo.getDistrictData().observe(context as LifecycleOwner, { itt->
                status.value= "hide"
                itemList.clear()
                for(i in itt.data.indices){ itemList.add(com.application.adverial.ui.model.DropList(itt.data[i].name, itt.data[i].id.toString())) }
                val dialog= DropList(itemList, context.resources.getString(R.string.address_district))
                dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
                dialog.getStatus().observe(context as LifecycleOwner, { result ->
                    holder.districtValue.text= result.name
                    holder.districtDrop.visibility= View.GONE
                    holder.districtValue.visibility= View.VISIBLE
                    district= result.id
                    context.getSharedPreferences("filter", 0).edit().putString("district", district).apply()
                })
            })
        }
        holder.currencyDrop.setOnClickListener {
            status.value= "show"
            val repo= Repository(context)
            repo.currency()
            repo.getCurrencyData().observe(context as LifecycleOwner, { itt->
                status.value= "hide"
                itemList.clear()
                for(i in itt.data.indices) itemList.add(com.application.adverial.ui.model.DropList(itt.data[i].country + "   " + itt.data[i].code, itt.data[i].id.toString()))
                val dialog= DropList(itemList, context.resources.getString(R.string.new_ad_info_currency))
                dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
                dialog.getStatus().observe(context as LifecycleOwner, {
                    holder.currencyDrop.visibility= View.GONE
                    holder.currencyValue.visibility= View.VISIBLE
                    holder.currencyValue.text= it.name.split("   ")[1]
                    context.getSharedPreferences("filter", 0).edit().putString("currency", it.name.split("   ")[1]).apply()
                })
            })
        }
        holder.currencyValue.setOnClickListener {
            status.value= "show"
            val repo= Repository(context)
            repo.currency()
            repo.getCurrencyData().observe(context as LifecycleOwner, { itt->
                status.value= "hide"
                itemList.clear()
                for(i in itt.data.indices) itemList.add(com.application.adverial.ui.model.DropList(itt.data[i].country + "   " + itt.data[i].code, itt.data[i].id.toString()))
                val dialog= DropList(itemList, context.resources.getString(R.string.new_ad_info_currency))
                dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
                dialog.getStatus().observe(context as LifecycleOwner, {
                    holder.currencyDrop.visibility= View.GONE
                    holder.currencyValue.visibility= View.VISIBLE
                    holder.currencyValue.text= it.name.split("   ")[1]
                    context.getSharedPreferences("filter", 0).edit().putString("currency", it.name.split("   ")[1]).apply()
                })
            })
        }
        holder.apply.setOnClickListener{
            val data= context.getSharedPreferences("filter", 0).edit()
            if(holder.priceMin.text.isNotBlank()) data.putString("priceMin", holder.priceMin.text.toString()).apply()
            else data.putString("priceMin", "").apply()
            if(holder.priceMax.text.isNotBlank()) data.putString("priceMax", holder.priceMax.text.toString()).apply()
            else data.putString("priceMax", "").apply()
            if(holder.photoCheck.isChecked) data.putString("photo", "1").apply()
            else data.putString("photo", "0").apply()
            if(holder.videoCheck.isChecked) data.putString("video", "1").apply()
            else data.putString("video", "0").apply()
            status.value= "apply"
        }
    }

    override fun getItemCount(): Int { return 1 }

    inner class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView){
        val countryDrop: ImageView= itemView.findViewById(R.id.filter_countryDrop)
        val countryValue: TextView= itemView.findViewById(R.id.filter_countryValue)
        val cityDrop: ImageView= itemView.findViewById(R.id.filter_cityDrop)
        val cityValue: TextView= itemView.findViewById(R.id.filter_cityValue)
        val districtDrop: ImageView= itemView.findViewById(R.id.filter_districtDrop)
        val districtValue: TextView= itemView.findViewById(R.id.filter_districtValue)
        val currencyDrop: ImageView= itemView.findViewById(R.id.filter_currencyDrop)
        val currencyValue: TextView= itemView.findViewById(R.id.filter_currencyValue)
        val priceMin: TextView= itemView.findViewById(R.id.filter_priceMin)
        val priceMax: TextView= itemView.findViewById(R.id.filter_priceMax)
        val recyclerView: RecyclerView= itemView.findViewById(R.id.filter_recyclerView)
        val apply: Button= itemView.findViewById(R.id.filter_apply)
        val photoCheck: CheckBox= itemView.findViewById(R.id.filter_photoCheck)
        val videoCheck: CheckBox= itemView.findViewById(R.id.filter_videoCheck)

        init {
            recyclerView.layoutManager= LinearLayoutManager(context)
            val repo= Repository(context)
            repo.categoryOptions(type)
            repo.getCategoryOptionsData().observe(context as LifecycleOwner, {
                recyclerView.adapter= FilterItemsAdapter(it.data as ArrayList<CategoryOptionsData>)
            })
        }
    }

    fun getResult(): MutableLiveData<String>{ return status }
}