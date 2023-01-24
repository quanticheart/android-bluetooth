package com.quanticheart.bluetooth.test.extentions

import android.Manifest.permission.*
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*

//
// Created by Jonn Alves on 21/01/23.
//
private var uuid: UUID? = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

internal var permsRequestCode: Int = 200
internal var permissionsCallback: ((Boolean) -> Unit)? = null

internal fun Context.checkBluetoothPermissions(callback: (Boolean) -> Unit) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val requestMultiplePermissions =
            (this as AppCompatActivity).registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val verify = permissions.entries.map {
                    Log.d("test006", "${it.key} = ${it.value}")
                    it.value
                }
                callback(!verify.contains(false))
                if (verify.contains(false)) {
                    checkBluetoothPermissions(callback)
                }
            }

        requestMultiplePermissions.launch(
            arrayOf(
                BLUETOOTH_SCAN,
                BLUETOOTH_CONNECT
            )
        )
    } else {
        if (!checkPermission(this)) {
            permissionsCallback = callback
            (this as Activity).requestPermissions(
                arrayOf(ACCESS_COARSE_LOCATION), permsRequestCode
            )
            callback(false)
        } else
            callback(true)
    }
}

private fun checkPermission(context: Context): Boolean {
//    val result = ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION)
    val result = ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION)
    return result == PackageManager.PERMISSION_GRANTED
}

internal fun handleRequestPermissionsResult(
    activity: AppCompatActivity,
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    when (requestCode) {
        permsRequestCode -> if (grantResults.isNotEmpty()) {
            val coarseAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
//            val fineAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
            if (coarseAccepted) {
                permissionsCallback?.let { it(true) }
            } else {
                activity.run {
                    if (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)) {
                        showMessageOKCancel(
                            this,
                            "You need to allow access to both the permissions"
                        ) { _, _ ->
                            requestPermissions(
                                arrayOf(ACCESS_COARSE_LOCATION),
                                permsRequestCode
                            )
                        }
                        return
                    } else {
                        permissionsCallback?.let { it(true) }
                    }
                }
            }
        }
    }
}

private fun showMessageOKCancel(
    activity: Activity,
    message: String,
    okListener: DialogInterface.OnClickListener
) {
    AlertDialog.Builder(activity)
        .setMessage(message)
        .setPositiveButton("OK", okListener)
        .setNegativeButton("Cancel", null)
        .create()
        .show()
}

internal fun Context.getBluetoothAdapter(): BluetoothAdapter {
    val bluetoothManager =
        getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    return bluetoothManager.adapter?.let {
        return it
    } ?: run {
        throw RuntimeException(
            "Bluetooth is not supported on this hardware platform. " +
                    "Make sure you try it from the real device\n " +
                    "You could more information from here:\n" +
                    "https://developer.android.com/reference/android/bluetooth/BluetoothAdapter"
        )
    }
}

private const val TAG = "Bluetooth Manager"
internal fun Any?.log() = Log.d(TAG, this?.toString() ?: "empty var")
