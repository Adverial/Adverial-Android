package com.application.adverial.service

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.adverial.remote.Repository
import com.application.adverial.ui.adapter.AutoCompleteAdapter

class AutoComplete(private val context: Context, private val recyclerView: RecyclerView, private val editText: EditText, private val type: String) {

    private val repo= Repository(context)
    private lateinit var textWatcher: TextWatcher

    init {
        recyclerView.layoutManager= LinearLayoutManager(context)
        textWatcher= object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(editText.text.toString().isEmpty()){
                    recyclerView.visibility= View.GONE
                }else{
                    recyclerView.visibility= View.VISIBLE
                    if(type == "ad") repo.adAutoComplete(editText.text.toString())
                    else repo.categoryAutoComplete(editText.text.toString())
                    repo.autoComplete.observe(context as LifecycleOwner){
                        recyclerView.adapter= AutoCompleteAdapter(it.data?: listOf()).apply {
                            result.observe(context as LifecycleOwner){
                                editText.removeTextChangedListener(textWatcher)
                                editText.setText(it)
                                editText.addTextChangedListener(textWatcher)
                                editText.setSelection(editText.text.length)
                                recyclerView.visibility= View.GONE
                            }
                        }
                    }
                }
            }
        }
        editText.addTextChangedListener(textWatcher)
    }

}