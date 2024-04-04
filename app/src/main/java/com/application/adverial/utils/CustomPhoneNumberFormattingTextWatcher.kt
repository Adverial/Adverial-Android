package com.application.adverial.utils

import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import id.zelory.compressor.determineImageRotation

class CustomPhoneNumberFormattingTextWatcher(private var isHaveZero: Boolean = false) : TextWatcher {
    private var isFormatting = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isFormatting) {
            return
        }

        if (isHaveZero){
            PhoneNumberFormattingTextWatcher("TR")
            return
        }

        val text = s.toString()

        val digits = text.replace("\\D+".toRegex(), "")

        var formattedText = buildString {
            for (i in digits.indices) {
                if (i == 3 || i == 6) {
                    append(" ")
                }
                append(digits[i])
            }
        }

        if (isHaveZero && !formattedText.startsWith("0")) {
            formattedText = "0$formattedText"
        }

        isFormatting = true
        s?.replace(0, s.length, formattedText)
        isFormatting = false
    }
}








