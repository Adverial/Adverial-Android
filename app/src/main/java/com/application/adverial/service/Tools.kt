package com.application.adverial.service

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.application.adverial.R
import com.application.adverial.remote.Repository
import com.application.adverial.utils.changeBackgroundRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_post.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.util.*


class Tools {


    private fun toolbarStatusBar(context: Context, window: Window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(context, R.color.toolbarColor)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    fun getCurrentLanguage(context: Context): String {
        var lang = "en"
        val language = context.getSharedPreferences("user", 0).getString("languageId", "")
        if (language!!.isBlank()) lang = "en"
        else if (language == "1") lang = "tr"
        else if (language == "2") lang = "ar"
        else if (language == "3") lang = "kmr"
        else if (language == "4") lang = "ckb"
        return lang
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun rotateLayout(context: Context, view: View) {
        val language = context.getSharedPreferences("user", 0).getString("languageId", "")
        if (language == "2" || language == "3" || language == "4") view.rotation = 180f
        else view.rotation = 0f
    }

    fun getLocale(activity: Activity) {
        val spLocal = activity.getSharedPreferences("user", 0).getString("local", "")!!.lowercase()
        if (!activity.resources.configuration.locale.toString().lowercase().contains(spLocal)) {
            val language = activity.getSharedPreferences("user", 0).getString("languageId", "")
            if (language == "" || language == "0" || language == "1") activity.window.decorView.layoutDirection =
                View.LAYOUT_DIRECTION_LTR
            else activity.window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
            Tools().setLocale(activity, language!!)
        }
    }

    private fun setLocale(activity: Activity, languageId: String) {
        var language = ""
        if (languageId == "1") language = "tr"
        if (languageId == "2") language = "ar"
        if (languageId == "3") language = "ku"
        if (languageId == "4") language = "kmr"
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = activity.resources.configuration
        config.setLocale(locale)
        activity.createConfigurationContext(config)
        activity.resources.updateConfiguration(config, activity.resources.displayMetrics)
        refreshActivity(activity)
    }

    private fun refreshActivity(activity: Activity) {
        val intent = activity.intent
        activity.overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        activity.finish()
        activity.overridePendingTransition(0, 0)
        activity.startActivity(intent)
    }

    fun goto(context: Context, target: Activity, backEnabled: Boolean) {
        val intent = Intent(context, target::class.java)
        if (!backEnabled) intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun viewEnable(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (idx in 0 until view.childCount) {
                viewEnable(view.getChildAt(idx), enabled)
            }
        }
    }

    fun navigate(view: View, currentId: Int, destinationId: Int) {
        val navController = Navigation.findNavController(view)
        val navOptions = NavOptions.Builder().setPopUpTo(currentId, true).build()
        navController.navigate(destinationId, null, navOptions)
    }

    fun openBrowser(activity: Activity, url: String) {
        val uri = Uri.parse(url)
        val intentBuilder = CustomTabsIntent.Builder()
        intentBuilder.setToolbarColor(
            ContextCompat.getColor(
                activity,
                com.application.adverial.R.color.yellow
            )
        )
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(activity, R.color.white))
        // add start and exit animations if you want(optional)
        /*intentBuilder.setStartAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left,android.R.anim.slide_out_right);*/
        // add start and exit animations if you want(optional)
        /*intentBuilder.setStartAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left,android.R.anim.slide_out_right);*/
        val customTabsIntent = intentBuilder.build()
        customTabsIntent.launchUrl(activity, uri)
    }

    fun authCheck(context: Context): Boolean {
        return context.getSharedPreferences("user", 0).getString("token", "") != ""
    }

    fun themeChangeControl(context: AppCompatActivity, theme: String) {
        if ((context.getSharedPreferences("system", 0).getBoolean(
                "darkMode",
                false
            ) && theme == "light") || (!context.getSharedPreferences("system", 0)
                .getBoolean("darkMode", false) && theme == "dark")
        ) {
            val intent = context.intent
            context.overridePendingTransition(0, 0)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            context.finish()
            context.overridePendingTransition(0, 0)
            context.startActivity(intent)
        }
    }

    fun autoDarkMode(context: AppCompatActivity, theme: String) {
        if (context.getSharedPreferences("system", 0).getBoolean("autoDark", false)) {
            var result = theme
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    context.getSharedPreferences("system", 0).edit().putBoolean("darkMode", true)
                        .apply()
                    Log.d("theme", "dark mode is on")
                    result = "dark"
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    context.getSharedPreferences("system", 0).edit().putBoolean("darkMode", false)
                        .apply()
                    Log.d("theme", "dark mode is off")
                    result = "light"
                }
                Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    context.getSharedPreferences("system", 0).edit().putBoolean("darkMode", false)
                        .apply()
                    Log.d("theme", "dark mode is off")
                    result = "light"
                }
            }
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            if (theme != result) {
                val intent = context.intent
                context.overridePendingTransition(0, 0)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                context.finish()
                context.overridePendingTransition(0, 0)
                context.startActivity(intent)
            }
        }
    }

    fun changeViewFromTheme(appCompatActivity: AppCompatActivity, view: View) {
        val sharedPrefManager : SharedPrefManager by lazy {
            SharedPrefManager(appCompatActivity)
        }

       Log.e("KEY",sharedPrefManager.getBackground().toString())

        loadUrlDrawable(appCompatActivity,sharedPrefManager.getBackground()?:""){
            view.background = it
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when (appCompatActivity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    Tools().toolbarStatusBar(
                        appCompatActivity.applicationContext,
                        appCompatActivity.window
                    )
                }
                Configuration.UI_MODE_NIGHT_YES -> {
                    Tools().toolbarStatusBar(
                        appCompatActivity.applicationContext,
                        appCompatActivity.window
                    )
                }
            }
        }
    }

    private fun loadUrlDrawable(context: Context, url: String, callback: (Drawable?) -> Unit) {
        Glide.with(context)
            .asDrawable()
            .load(url)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    callback(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }


    /*fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }*/

    fun locationRequest(activity: Activity) {
        val mLocationRequest =
            LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval((10 * 1000).toLong()).setFastestInterval((1 * 1000).toLong())
        val settingsBuilder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        settingsBuilder.setAlwaysShow(true)
        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(activity)
            .checkLocationSettings(settingsBuilder.build())
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
            } catch (ex: ApiException) {
                when (ex.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException =
                            ex as ResolvableApiException; resolvableApiException.startResolutionForResult(
                            activity,
                            3
                        )
                    } catch (e: IntentSender.SendIntentException) {
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        Toast.makeText(
                            activity,
                            "Lütfen konum hizmetinizi açın",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun gotoMyCountry(map: GoogleMap) {
        val one = LatLng(42.216071, 26.389816)
        val two = LatLng(36.690183, 44.747969)
        val builder = LatLngBounds.Builder()
        builder.include(one)
        builder.include(two)
        val bounds = builder.build()
        val padding = 10
        map.setLatLngBoundsForCameraTarget(bounds)
        map.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                displayWidth(),
                displayHeight(),
                padding
            )
        )
        map.setMinZoomPreference(map.cameraPosition.zoom)
    }

    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val cellular = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        return wifi != null && wifi.isConnected || cellular != null && cellular.isConnected
    }

    fun displayWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    fun displayHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }


    fun takeScreenShot(activity: Activity): Bitmap? {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val b1 = view.drawingCache
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        val statusBarHeight: Int = frame.top
        val width = activity.windowManager.defaultDisplay.width
        val height = activity.windowManager.defaultDisplay.height
        val b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height)
        view.destroyDrawingCache()
        return b
    }

    fun fastBlur(sentBitmap: Bitmap, radius: Int): Bitmap? {
        val bitmap = sentBitmap.copy(sentBitmap.config, true)
        if (radius < 1) {
            return null
        }
        val w = bitmap.width
        val h = bitmap.height
        val pix = IntArray(w * h)
        bitmap.getPixels(pix, 0, w, 0, 0, w, h)
        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1
        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(Math.max(w, h))
        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }
        yi = 0
        yw = yi
        val stack = Array(div) {
            IntArray(
                3
            )
        }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int
        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -radius
            while (i <= radius) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius
            x = 0
            while (x < w) {
                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm)
                }
                p = pix[yw + vmin[x]]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = Math.max(0, yp) + x
                sir = stack[i + radius]
                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]
                rbs = r1 - Math.abs(i)
                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {

                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] =
                    -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vmin[y]
                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi += w
                y++
            }
            x++
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h)
        return bitmap
    }

    fun getPath(): String {
        return "https://adverial.com/"
    }

    @Throws(IOException::class)
    fun rotateImageIfRequired(context: Context, img: Bitmap, selectedImage: Uri): Bitmap? {
        val input: InputStream = context.contentResolver.openInputStream(selectedImage)!!
        val ei: ExifInterface
        if (Build.VERSION.SDK_INT > 23) ei = ExifInterface(input) else ei =
            ExifInterface(selectedImage.path!!)
        val orientation: Int =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(img, 0)
            ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(img, 270)
            else -> img
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            System.currentTimeMillis().toString(),
            null
        )
        return Uri.parse(path)
    }
}