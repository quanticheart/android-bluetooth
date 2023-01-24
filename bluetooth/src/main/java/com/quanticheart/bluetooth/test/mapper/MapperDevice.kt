package com.quanticheart.bluetooth.test.mapper

import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.os.Build

import com.quanticheart.bluetooth.test.mapper.model.Device

//
// Created by Jonn Alves on 21/01/23.
//
class MapperDevice {
    fun map(devices: List<BluetoothDevice>) = devices.map { map(it) }.distinctBy { it.macAddress }
    fun map(devices: Set<BluetoothDevice>?): List<Device> =
        devices?.map { map(it) }?.distinctBy { it.macAddress } ?: emptyList()

    @SuppressLint("MissingPermission")
    fun map(device: BluetoothDevice): Device {
        return Device(
            name = device.name ?: "--",
            macAddress = device.address,
            alias = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                device.alias ?: "--"
            } else {
                "--"
            },
            type = device.type,
            bondState = device.bondState,
            majorDeviceClass = device.bluetoothClass.majorDeviceClass,
            deviceClass = device.bluetoothClass.deviceClass,
            deviceClassName = getBTMajorDeviceClass(device.bluetoothClass.majorDeviceClass),
            bluetoothDevice = device
        )
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

    private fun BluetoothClass.withPotentiallyInstalledApplication() =
        this.majorDeviceClass == BluetoothClass.Device.Major.PHONE ||
                this.majorDeviceClass == BluetoothClass.Device.Major.COMPUTER ||
                this.majorDeviceClass == BluetoothClass.Device.Major.UNCATEGORIZED ||
                this.majorDeviceClass == BluetoothClass.Device.Major.MISC
}

fun List<Device>.onlyAudio() =
    this.filter { it.majorDeviceClass == BluetoothClass.Device.Major.AUDIO_VIDEO }

fun List<Device>.onlyPhone() =
    this.filter { it.majorDeviceClass == BluetoothClass.Device.Major.PHONE }