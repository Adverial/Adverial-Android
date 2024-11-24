package com.application.adverial.ui.dialog

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import com.application.adverial.R
import com.application.adverial.databinding.DialogNewAdCompletedBinding
import com.application.adverial.ui.navigation.Home
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.BlurTransformation

class NewAdCompletedDialog : DialogFragment() {

    private var _binding: DialogNewAdCompletedBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val ARG_CODE = "code"

        fun newInstance(code: String): NewAdCompletedDialog {
            val dialog = NewAdCompletedDialog()
            val args = Bundle().apply {
                putString(ARG_CODE, code)
            }
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_Luuk_Dark)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogNewAdCompletedBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        val code = arguments?.getString(ARG_CODE)

        binding.newAdCompletedNumber.text = "getString(R.string.new_ad_completed_new_ad, code)"

        binding.newAdCompletedHome.setOnClickListener {
            dismiss()
            navigateToHome()
        }
//
//        binding.newAdCompletedRepeat.setOnClickListener {
//            dismiss()
//            Toast.makeText(requireContext(), "Repeat Action", Toast.LENGTH_SHORT).show()
//        }

        return binding.root
    }

    private fun navigateToHome() {
        val intent = Intent(requireContext(), Home::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        _binding = null // Prevent memory leaks
        super.onDestroyView()
    }
}
