package com.application.adverial.utils

import android.app.AlertDialog
import android.content.Context

object DialogUtils {
    // Function to show an alert dialog for no internet connection
    fun showNoInternetDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun showBaseUrlAlertDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Network Error")
            .setMessage("Network is not available. Please check your network connection and try again.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    fun showServerErrorDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Server Error")
            .setMessage("Server is not available. Please try again later.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    // show alert dialog for unexpected error in the app functionality
    fun showUnexpectedErrorDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Unexpected Error")
            .setMessage("An unexpected error occurred. Please try again later.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}