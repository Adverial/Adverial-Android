package com.application.adverial.ui.dialog

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.PixelCopy
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

    private var code: String? = null
    private var _binding: DialogNewAdCompletedBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val ARG_CODE = "code"

        // Factory method to create a new instance of the dialog with arguments
        fun newInstance(code: String): NewAdCompletedDialog {
            val dialog = NewAdCompletedDialog()
            val args = Bundle()
            args.putString(ARG_CODE, code)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        code = arguments?.getString(ARG_CODE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogNewAdCompletedBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setupDialogWindow()
        applyBlurEffect()

        setupUI()
        return binding.root
    }

    private fun setupDialogWindow() {
        dialog?.window?.let { window ->
            val params = window.attributes
            params.gravity = Gravity.TOP
            window.attributes = params
        }
    }

    private fun applyBlurEffect() {
        val activityWindow = requireActivity().window
        val bitmap = captureWindowBitmap(activityWindow)

        if (bitmap != null) {
            val blurredBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                applyRenderEffectBlur(bitmap, 10f)
            } else {
                applyGlideBlur(bitmap)
            }
            dialog?.window?.setBackgroundDrawable(BitmapDrawable(resources, blurredBitmap))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun captureWindowBitmap(window: Window): Bitmap? {
        val rootView = window.decorView
        val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)

        val location = IntArray(2)
        rootView.getLocationOnScreen(location)

        val rect = android.graphics.Rect(location[0], location[1], location[0] + rootView.width, location[1] + rootView.height)
        val handler = android.os.Handler()

        return try {
            PixelCopy.request(window, rect, bitmap, { copyResult ->
                if (copyResult != PixelCopy.SUCCESS) {
                    Toast.makeText(requireContext(), "Failed to capture background", Toast.LENGTH_SHORT).show()
                }
            }, handler)
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun applyRenderEffectBlur(bitmap: Bitmap, radius: Float): Bitmap {
        val renderEffect = RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
        val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = android.graphics.Canvas(outputBitmap)
        val paint = android.graphics.Paint().apply {
            setRenderEffect(renderEffect)
        }
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return outputBitmap
    }

    private fun setRenderEffect(renderEffect: RenderEffect) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.root.setRenderEffect(renderEffect)
        }
    }

    private fun applyGlideBlur(bitmap: Bitmap): Bitmap {
        return Glide.with(requireContext())
            .asBitmap()
            .load(bitmap)
            .transform(BlurTransformation(10))
            .submit(bitmap.width, bitmap.height)
            .get()
    }

    private fun setupUI() {
        binding.newAdCompletedNumber.text = "getString(R.string.new_ad_completed_new_ad, code)"

        binding.newAdCompletedHome.setOnClickListener {
            val intent = Intent(requireContext(), Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            requireContext().startActivity(intent)
            dismiss()
        }

        binding.newAdCompletedRepeat.setOnClickListener {
            Toast.makeText(requireContext(), "Repeat Action", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        _binding = null // Prevent memory leaks
        super.onDestroyView()
    }
}