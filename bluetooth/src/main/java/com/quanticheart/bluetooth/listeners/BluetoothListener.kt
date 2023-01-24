package com.quanticheart.bluetooth.listeners

import com.quanticheart.bluetooth.mapper.model.Device

interface BluetoothListener {

    fun onStartDiscovery()

    fun onFinishDiscovery()

    fun onEnabledBluetooth()

    fun onDisabledBluetooth()

    fun permissionsStatus(status: Boolean)

    fun getBluetoothDeviceDiscoveryList(devices: List<Device>)

    fun getBluetoothDeviceList(devices: List<Device>)
}