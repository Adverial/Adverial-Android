package com.application.adverial.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.adverial.R
import com.application.adverial.databinding.ActivityMyAccountBinding
import com.application.adverial.remote.Repository
import com.application.adverial.service.Tools
import com.application.adverial.ui.dialog.AlertDialog1
import com.application.adverial.ui.navigation.Home
import java.util.regex.Pattern

class MyAccount : AppCompatActivity() {

    private lateinit var binding: ActivityMyAccountBinding
    private var lastPhone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools().changeViewFromTheme(this, binding.myAccountRoot)
        Tools().setBasedLogo(this, R.id.imageView42)

        // Disable the phone number field
        binding.myaccountPhone.isEnabled = false
        pageInit()
    }

    private fun pageInit() {
        binding.lottie14.visibility = View.VISIBLE
        Tools().viewEnable(window.decorView.rootView, false)

        val repo = Repository(this)
        repo.user()
        repo.getUserData().observe(this) {
            binding.lottie14.visibility = View.GONE
            Tools().viewEnable(window.decorView.rootView, true)

            if (it.status) {
                lastPhone = it.data.phone ?: ""
                binding.myaccountFirstname.setText(it.data.name ?: "")
                binding.myaccountLastname.setText(it.data.last_name ?: "")
                binding.myaccountEmail.setText(it.data.email ?: "")

                // Update countryCodePicker to be first four digits of it.data.whatsappNumber
                binding.countryCodePicker.setCountryForPhoneCode(it.data.whatsappNumber?.substring(0, 4)?.toInt() ?: 0)
                binding.myaccountPhone.setText(it.data.whatsappNumber?.substring(4) ?: "")
            }
        }

        binding.deleteAccount.setOnClickListener {
            val dialog = AlertDialog1(getString(R.string.attention), getString(R.string.deleteAccount))
            dialog.show(supportFragmentManager, "AlertDialog1")
            dialog.getStatus().observe(this) { ittt ->
                if (ittt == "ok") {
                    val dialog1 = AlertDialog1(getString(R.string.attention), getString(R.string.deleteAccount1))
                    dialog1.show(supportFragmentManager, "AlertDialog1")
                    dialog1.getStatus().observe(this) { itt ->
                        if (itt == "ok") {
                            binding.lottie14.visibility = View.VISIBLE
                            Tools().viewEnable(window.decorView.rootView, false)
                            with(Repository(this)) {
                                deleteAccount()
                                deleteAccount.observe(this@MyAccount) {
                                    binding.lottie14.visibility = View.GONE
                                    Tools().viewEnable(this@MyAccount.window.decorView.rootView, true)
                                    if (it.status) {
                                        getSharedPreferences("user", 0).edit().clear().apply()
                                        val intent = Intent(this@MyAccount, Home::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun save(view: View) {
        val emailPattern = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b")
        val emailMatcher = emailPattern.matcher(binding.myaccountEmail.text.toString())
        if (binding.myaccountFirstname.text.isNotBlank() && binding.myaccountLastname.text.isNotBlank() &&
            emailMatcher.find() && binding.myaccountPhone.length() == 10
        ) {
            binding.lottie14.visibility = View.VISIBLE
            Tools().viewEnable(window.decorView.rootView, false)

            val repo = Repository(this)
            repo.userUpdate(
                binding.myaccountFirstname.text.toString(),
                binding.myaccountLastname.text.toString(),
                binding.myaccountEmail.text.toString(),
                binding.myaccountPhone.text.toString()
            )

            repo.getUserUpdateData().observe(this) {
                if (it.status) {
                    binding.lottie14.visibility = View.GONE
                    Tools().viewEnable(window.decorView.rootView, true)
                    if (binding.myaccountPhone.text.toString() == lastPhone) {
                        Toast.makeText(this, resources.getString(R.string.accountUpdateDone), Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // Need to verify phone
                        val intent = Intent(this, PhoneAuth::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        } else {
            Toast.makeText(this, resources.getString(R.string.fieldsAreEmpty), Toast.LENGTH_SHORT).show()
        }
    }

    fun clear(view: View) {
        binding.myaccountPhone.setText("")
    }

    fun back(view: View) {
        finish()
    }

    override fun onResume() {
        super.onResume()
        Tools().getLocale(this)
        val language = getSharedPreferences("user", 0).getString("languageId", "")
        window.decorView.layoutDirection =
            if (language.isNullOrEmpty() || language == "0" || language == "1") {
                View.LAYOUT_DIRECTION_LTR
            } else {
                View.LAYOUT_DIRECTION_RTL
            }
    }
}
