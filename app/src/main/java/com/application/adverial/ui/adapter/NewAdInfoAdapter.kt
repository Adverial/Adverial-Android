package com.application.adverial.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.R
import com.application.adverial.databinding.ItemNewAdInfoBinding
import com.application.adverial.remote.Repository
import com.application.adverial.remote.model.CategoryOptionsData
import com.application.adverial.ui.dialog.DropList
import com.application.adverial.ui.model.DropList as DropListItem

class NewAdInfoAdapter(private var itemList: ArrayList<CategoryOptionsData>) :
        RecyclerView.Adapter<NewAdInfoAdapter.ViewHolder>() {

    private val result = MutableLiveData<String>()
    private val currencyList = ArrayList<DropListItem>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ItemNewAdInfoBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(private val binding: ItemNewAdInfoBinding) :
            RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("CommitPrefEdits")
        fun bind(item: CategoryOptionsData, position: Int) {
            binding.itemNewAdInfoTitle.text = item.title

            when (position) {
                0 -> setupTitleField()
                1 -> setupPriceField()
                2 -> setupCurrencyDropDown()
                itemList.size - 1 -> setupDescriptionField()
                else -> setupOtherOptions(item)
            }
        }

        private fun setupTitleField() {
            with(binding) {
                itemNewAdInfoEnter.visibility = View.VISIBLE
                itemNewAdInfoEnter.addTextChangedListener(
                        object : SimpleTextWatcher() {
                            override fun onTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    before: Int,
                                    count: Int
                            ) {
                                context.getSharedPreferences("newAd", 0)
                                        .edit()
                                        .putString("title", s.toString())
                                        .apply()
                            }
                        }
                )
            }
        }

        private fun setupPriceField() {
            with(binding) {
                itemNewAdInfoPriceA.visibility = View.VISIBLE
                itemNewAdInfoPriceA.addTextChangedListener(
                        object : SimpleTextWatcher() {
                            override fun onTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    before: Int,
                                    count: Int
                            ) {
                                context.getSharedPreferences("newAd", 0)
                                        .edit()
                                        .putString("price", s.toString())
                                        .apply()
                            }
                        }
                )
            }
        }

        private fun setupCurrencyDropDown() {
            with(binding) {
                itemNewAdInfoDropList.visibility = View.VISIBLE
                itemNewAdInfoDropList.setOnClickListener { showCurrencyDialog() }
                itemNewAdInfoValue.setOnClickListener { showCurrencyDialog() }
            }
        }

        private fun showCurrencyDialog() {
            result.value = "show"
            currencyList.clear()
            val repo = Repository(context)
            repo.currency()
            repo.getCurrencyData().observe(context as LifecycleOwner) { response ->
                response.data?.forEach {
                    currencyList.add(DropListItem("${it.country}   ${it.code}", it.id.toString()))
                }
                val dialog =
                        DropList(currencyList, context.getString(R.string.new_ad_info_currency))
                dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
                dialog.getStatus().observe(context as LifecycleOwner) { selection ->
                    binding.itemNewAdInfoValue.text = selection.name.split("   ")[1]
                    context.getSharedPreferences("newAd", 0)
                            .edit()
                            .putString("currency", selection.name.split("   ")[1])
                            .apply()
                }
                result.value = "hide"
            }
        }

        private fun setupDescriptionField() {
            with(binding) {
                itemNewAdInfoEnter.visibility = View.VISIBLE
                itemNewAdInfoEnter.layoutParams.height =
                        context.resources.getDimension(com.intuit.sdp.R.dimen._100sdp).toInt()
                itemNewAdInfoEnter.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
                itemNewAdInfoEnter.addTextChangedListener(
                        object : SimpleTextWatcher() {
                            override fun onTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    before: Int,
                                    count: Int
                            ) {
                                context.getSharedPreferences("newAd", 0)
                                        .edit()
                                        .putString("description", s.toString())
                                        .apply()
                            }
                        }
                )
            }
        }

        private fun setupOtherOptions(item: CategoryOptionsData) {
            with(binding) {
                itemNewAdInfoEnter.visibility =
                        if (item.values.isNullOrEmpty()) View.VISIBLE else View.GONE
                itemNewAdInfoDropList.visibility =
                        if (item.values.isNullOrEmpty()) View.GONE else View.VISIBLE

                itemNewAdInfoDropList.setOnClickListener { showOptionsDialog(item) }
                itemNewAdInfoValue.setOnClickListener { showOptionsDialog(item) }
            }
        }

        private fun showOptionsDialog(item: CategoryOptionsData) {
            val data = ArrayList<DropListItem>()

            when (item.type) {
                "checkbox" -> {
                    data.add(DropListItem("Yes", "1"))
                    data.add(DropListItem("No", "0"))
                }
                "select" -> {
                    item.values?.split(",")?.forEach { value ->
                        data.add(DropListItem(value.trim(), value.trim()))
                    }
                }
            }

            val dialog = DropList(data, item.title)
            dialog.show((context as AppCompatActivity).supportFragmentManager, "DropList")
            dialog.getStatus().observe(context as LifecycleOwner) { selection ->
                binding.itemNewAdInfoValue.text = selection.name
                context.getSharedPreferences("newAdOptions", 0)
                        .edit()
                        .putString(item.id.toString(), selection.id)
                        .apply()
            }
        }
    }

    fun getResult(): MutableLiveData<String> = result
}

abstract class SimpleTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable?) {}
}
