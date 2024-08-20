package com.adverial.huawei

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import com.huawei.hms.location.GeofenceData

class GeoFenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_PROCESS_LOCATION) {
            val geofenceData = GeofenceData.getDataFromIntent(intent)
            geofenceData?.let {
                val errorCode = it.errorCode
                val conversion = it.conversion
                val geofenceList = it.convertingGeofenceList
                val mLocation: Location = it.convertingLocation
                val status = it.isSuccess
            }
        }
    }

    companion object {
        const val ACTION_PROCESS_LOCATION = "com.huawei.hmssample.geofence.GeoFenceBroadcastReceiver.ACTION_PROCESS_LOCATION"
    }
}