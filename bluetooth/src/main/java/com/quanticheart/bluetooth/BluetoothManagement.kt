package com.quanticheart.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.quanticheart.bluetooth.extentions.checkBluetoothPermissions
import com.quanticheart.bluetooth.extentions.getBluetoothAdapter
import com.quanticheart.bluetooth.extentions.handleRequestPermissionsResult
import com.quanticheart.bluetooth.listeners.BluetoothListener
import com.quanticheart.bluetooth.mapper.MapperDevice
import com.quanticheart.bluetooth.receivers.BluetoothDevicePicker
import com.quanticheart.bluetooth.receivers.BluetoothStateChangeReceiver
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*


//
// Created by Jonn Alves on 21/01/23.
//
@SuppressLint("MissingPermission")
class BluetoothManagement(
    private val context: Context,
    private val listener: BluetoothListener
) {

    private val mBluetoothAdapter by lazy {
        context.getBluetoothAdapter()
    }

    private var isEnabled = mBluetoothAdapter.isEnabled
    private var isDiscovering = mBluetoothAdapter.isDiscovering
    private var permissions = false

    init {
        context.checkBluetoothPermissions {
            permissions = it
            listener.permissionsStatus(it)
            if (isEnabled)
                listener.onEnabledBluetooth()
            else
                listener.onDisabledBluetooth()
        }
    }

    fun isPermissionsEnabled() = permissions
    fun isBluetoothEnabled() = isEnabled
    fun isBluetoothScanning() = isDiscovering

    fun enable() {
        if (!isEnabled) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                mBluetoothAdapter.enable()
            } else {
                val requestBluetooth =
                    (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                        callback(result.resultCode == Activity.RESULT_OK)
                    }
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                requestBluetooth.launch(enableBtIntent)
            }
        }
    }

    fun disable() {
        if (isEnabled) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                mBluetoothAdapter.disable()
            } else {
                val requestBluetooth =
                    (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                        callback(result.resultCode == Activity.RESULT_OK)
                    }
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                requestBluetooth.launch(enableBtIntent)
            }
        }
    }

    private val mBluetoothStateChangeReceiver by lazy {
        object : BluetoothStateChangeReceiver() {
            override fun onStartDiscovering() {
                isDiscovering = true
                listener.onStartDiscovery()
            }

            override fun onFinishDiscovering() {
                isDiscovering = false
                listener.onFinishDiscovery()
            }

            override fun getFoundDevices(devices: List<BluetoothDevice>) {
                val filter = MapperDevice().map(devices)
                listener.getBluetoothDeviceDiscoveryList(filter)
            }

            override fun onEnabledBluetooth() {
                isEnabled = true
                listener.onEnabledBluetooth()
            }

            override fun onDisabledBluetooth() {
                isEnabled = false
                listener.onDisabledBluetooth()
            }
        }
    }

    fun registerBluetoothStateChanged() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevicePicker.ACTION_DEVICE_SELECTED)

        context.registerReceiver(mBluetoothStateChangeReceiver, intentFilter)
    }

    fun unregisterBluetoothStateChanged() {
        context.unregisterReceiver(mBluetoothStateChangeReceiver)
    }

    fun startDiscovery() {
        if (isEnabled && !isDiscovering) {
            mBluetoothAdapter.startDiscovery()
        }
    }

    fun visibleForDiscovery() {
        val dIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        dIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        (context as AppCompatActivity).startActivityForResult(dIntent, 10110)
    }

    fun stopDiscovery() {
        if (isEnabled && isDiscovering) {
            mBluetoothAdapter.cancelDiscovery()
        }
    }

    fun getHardware() {
        val pairedDevices: Set<BluetoothDevice>? = mBluetoothAdapter.bondedDevices
        val filter = MapperDevice().map(pairedDevices)
        listener.getBluetoothDeviceList(filter)
    }


    fun openSettings() {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        (context as AppCompatActivity).startActivity(intent)
    }

    fun openPicker() {
        val bluetoothPicker = Intent(BluetoothDevicePicker.ACTION_LAUNCH)
            .putExtra(BluetoothDevicePicker.EXTRA_NEED_AUTH, false)
            .putExtra(
                BluetoothDevicePicker.EXTRA_FILTER_TYPE,
                BluetoothDevicePicker.FILTER_TYPE_ALL
            )
            .setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        (context as AppCompatActivity).startActivity(bluetoothPicker)
    }

    fun pairDevice(device: BluetoothDevice?) = device?.createBond() ?: false

    fun pairDeviceIntent(device: BluetoothDevice?) {
        val intent = Intent(BluetoothDevice.ACTION_PAIRING_REQUEST)
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device)
        intent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.PAIRING_VARIANT_PIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun pairDeviceHard(device: BluetoothDevice?): Boolean {
        return try {
            val class1 = device?.javaClass

//            val pin = class1?.getMethod("convertPinToBytes", String::class.java)
//                ?.invoke(class1, "000000") as ByteArray

            val m: Method? = class1?.getMethod("setPin", ByteArray::class.java)
            m?.invoke(device, ("0001").toByteArray(charset("UTF-8")))

//            class1?.getMethod("setPairingConfirmation", Boolean::class.javaPrimitiveType)
//                ?.invoke(device, true)

            val createBondMethod = class1?.getMethod("createBond")
            createBondMethod?.invoke(device) as Boolean

        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            false
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            false
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
            false
        }
    }

    fun unpairDevice(device: BluetoothDevice?) {
        val method: Method?
        try {
            method = device?.javaClass?.getMethod("removeBond")
            method?.invoke(device)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

    fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        if (requestCode == 10110) {

        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        handleRequestPermissionsResult(
            (context as AppCompatActivity),
            requestCode,
            permissions,
            grantResults
        )
    }
}
