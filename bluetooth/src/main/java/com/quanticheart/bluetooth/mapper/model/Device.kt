package com.quanticheart.bluetooth.mapper.model

import android.bluetooth.BluetoothDevice
import com.quanticheart.bluetooth.extentions.log

data class Device(
    val name: String = "",
    val macAddress: String = "",
    val alias: String = "",
    val type: Int = 0,
    val bondState: Int = 0,
    val deviceClass: Int = 0,
    val majorDeviceClass: Int = 0,
    val deviceClassName: String = "",
    val bluetoothDevice: BluetoothDevice? = null
) {
    fun log() = name.log()
}