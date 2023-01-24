package com.quanticheart.bluetooth.test.receivers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.quanticheart.bluetooth.test.extentions.log


//
// Created by Jonn Alves on 22/01/23.
//
class PairingRequest : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent) {

        if (BluetoothDevice.ACTION_PAIRING_REQUEST == intent.action) {
            val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            val type =
                intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR)
            val pin = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY, BluetoothDevice.ERROR)
            //the pin in case you need to accept for an specific pin
            Log.d("type", type.toString())
            Log.d("PIN", pin.toString())
            Log.d("Bonded", device?.name ?: "")
            if (type == BluetoothDevice.PAIRING_VARIANT_PIN) {
                device?.setPin("0000".toByteArray())
                abortBroadcast()
            }
        }

        if ("android.bluetooth.device.action.PAIRING_CANCEL" == intent.action) {
            "CANCEL".log()
        }
    }
}