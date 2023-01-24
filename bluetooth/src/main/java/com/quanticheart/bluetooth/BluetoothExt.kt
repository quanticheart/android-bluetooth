package com.quanticheart.bluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow


//
// Created by Jonn Alves on 20/01/23.
//

@SuppressLint("MissingPermission")
fun t() {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
    val list = ArrayList<String>()

    pairedDevices?.forEach { device ->
        val deviceName = device.name
        val deviceHardwareAddress = device.address // MAC address
        val alias = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            device.alias
        } else {
            ""
        } // MAC address
        val type = device.type // MAC address
        val bondState = device.bondState // MAC address

        val deviceClass = device.bluetoothClass.majorDeviceClass
        val deviceDeviceClass = device.bluetoothClass.deviceClass
        val deviceClassName = getBTMajorDeviceClass(device.bluetoothClass.majorDeviceClass)

        list.add("Name: $deviceName, MAC Address: $deviceHardwareAddress, Class: $deviceClass, Class Name: $deviceClassName, $alias, $type, $bondState, $deviceDeviceClass")
    }

    Log.e("LIST", list.toString())
}

fun getBluetoothAdapter() = BluetoothAdapter.getDefaultAdapter()

fun hasBluetooth(): Boolean =
    BluetoothAdapter.getDefaultAdapter() != null

@SuppressLint("MissingPermission")
fun Activity.enableBluetooth() {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    if (!bluetoothAdapter.isEnabled) {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(intent, 100)
    }
}

@SuppressLint("MissingPermission")
fun Activity.disableBluetooth() {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    bluetoothAdapter.disable()
    Toast.makeText(applicationContext, "Bluetooth Turned OFF", Toast.LENGTH_SHORT).show()
}

@SuppressLint("MissingPermission")
fun Activity.searchInBluetooth() {
    val dIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
    dIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
    startActivity(dIntent)
}

private fun getBTMajorDeviceClass(major: Int): String {
    return when (major) {
        BluetoothClass.Device.Major.AUDIO_VIDEO -> "AUDIO_VIDEO"
        BluetoothClass.Device.Major.COMPUTER -> "COMPUTER"
        BluetoothClass.Device.Major.HEALTH -> "HEALTH"
        BluetoothClass.Device.Major.IMAGING -> "IMAGING"
        BluetoothClass.Device.Major.MISC -> "MISC"
        BluetoothClass.Device.Major.NETWORKING -> "NETWORKING"
        BluetoothClass.Device.Major.PERIPHERAL -> "PERIPHERAL"
        BluetoothClass.Device.Major.PHONE -> "PHONE"
        BluetoothClass.Device.Major.TOY -> "TOY"
        BluetoothClass.Device.Major.UNCATEGORIZED -> "UNCATEGORIZED"
        BluetoothClass.Device.Major.WEARABLE -> "AUDIO_VIDEO"
        else -> "unknown!"
    }
}

fun Long.toReadableFileSize(): String {
    if (this <= 0) return "?"
    val units = arrayOf("B", "kB", "MB", "GB", "TB", "PB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
    if (digitGroups > 6) return "?"
    return DecimalFormat("#,##0.#").format(
        this / 1024.0.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}

inline fun <T : Any, V : Any> safeLet(p1: T?, p2: V?, block: (T, V) -> Unit) {
    if (p1 != null && p2 != null) {
        block(p1, p2)
    }
}

fun <T : View> Activity.bind(@IdRes idRes: Int) =
    lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(idRes) }

inline fun <reified T : Any?> AppCompatActivity.argument(key: String) = lazy {
    intent.extras?.let {
        return@lazy it[key] as T
    }
    return@lazy null
}

inline fun <reified T : Any> AppCompatActivity.argument(key: String, defaultValue: T) = lazy {
    intent.extras?.let {
        return@lazy it[key] as? T ?: defaultValue
    }
    return@lazy defaultValue
}