package com.quanticheart.bluetooth.test.listeners

import com.quanticheart.bluetooth.test.mapper.model.Device

interface BluetoothListener {

    fun onStartDiscovery()

    fun onFinishDiscovery()

    fun onEnabledBluetooth()

    fun onDisabledBluetooth()

    fun permissionsStatus(status: Boolean)

    fun getBluetoothDeviceDiscoveryList(devices: List<Device>)

    fun getBluetoothDeviceList(devices: List<Device>)
}