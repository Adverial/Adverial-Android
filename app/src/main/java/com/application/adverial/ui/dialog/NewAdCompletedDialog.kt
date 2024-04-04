package com.application.adverial.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment

import com.application.adverial.R
import com.application.adverial.service.Tools
import fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.application.adverial.ui.navigation.Home

class NewAdCompletedDialog(var code: String): DialogFragment(){
    private lateinit var mBlurEngine: BlurDialogEngine

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBlurEngine = BlurDialogEngine(requireActivity())
        mBlurEngine.setBlurRadius(10)
        mBlurEngine.setDownScaleFactor(10f)
        mBlurEngine.setBlurActionBar(true)
        mBlurEngine.setUseRenderScript(true)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val view= inflater.inflate(R.layout.dialog_new_ad_completed, container, false)
        val window: Window? = dialog!!.window
        val wlp: WindowManager.LayoutParams = window!!.attributes
        //dialog!!.window?.attributes?.windowAnimations = R.style.DialogAnimation
        wlp.gravity = Gravity.TOP
        window.attributes = wlp

        uiSetup(view)
        onBackPress(view)

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window?.setLayout(Tools().displayWidth(), ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun uiSetup(view: View){
        val number= view.findViewById<TextView>(R.id.newAdCompleted_number)
        val home= view.findViewById<Button>(R.id.newAdCompleted_home)
        val repeat= view.findViewById<Button>(R.id.newAdCompleted_repeat)
        number.append(" $code")

        home.setOnClickListener{
            val intent= Intent(requireContext(), Home::class.java)
            intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            requireContext().startActivity(intent)
            dialog!!.dismiss()
        }
    }

    private fun onBackPress(view: View){
        dialog!!.setOnCancelListener {
            val intent= Intent(requireContext(), Home::class.java)
            intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            requireContext().startActivity(intent)
            dialog!!.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        mBlurEngine.onResume(retainInstance)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mBlurEngine.onDismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBlurEngine.onDetach()
    }

    override fun onDestroyView() {
        if (dialog != null) {
            dialog!!.setDismissMessage(null)
        }
        super.onDestroyView()
    }
}