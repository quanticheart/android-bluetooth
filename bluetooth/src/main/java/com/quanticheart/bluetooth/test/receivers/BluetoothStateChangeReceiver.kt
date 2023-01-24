package com.quanticheart.bluetooth.test.receivers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.quanticheart.bluetooth.test.extentions.log


private val list = ArrayList<BluetoothDevice>()

abstract class BluetoothStateChangeReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {

        val action = intent?.action
        action.log()
        val state = intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
        when (action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                when (state) {
                    BluetoothAdapter.STATE_OFF -> onDisabledBluetooth()
                    BluetoothAdapter.STATE_ON -> onEnabledBluetooth()
                    BluetoothAdapter.STATE_TURNING_OFF -> {}
                    BluetoothAdapter.STATE_TURNING_ON -> {}
                }
            }

            BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                list.clear()
                onStartDiscovering()
            }

            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                onFinishDiscovering()
                getFoundDevices(list)
            }

            BluetoothDevice.ACTION_FOUND -> {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                device?.let {
                    list.add(it)
                }
            }
            BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                when (intent.getIntExtra(
                    BluetoothAdapter.EXTRA_SCAN_MODE,
                    BluetoothAdapter.ERROR
                )) {
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> {}
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE -> {}
                    BluetoothAdapter.SCAN_MODE_NONE -> {}
                }
            }

            BluetoothDevice.ACTION_PAIRING_REQUEST -> {
//                val device =
//                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//                val type =
//                    intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR)
//                if (type == BluetoothDevice.PAIRING_VARIANT_PIN) {
//                    device?.setPin("1235".toByteArray())
//                    device?.setPairingConfirmation(true)
//                    abortBroadcast()
//                }
            }
            BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                "Bonding State: ${device?.bondState}".log()

                when (device?.bondState) {
                    BluetoothDevice.BOND_BONDING -> {
                        "Bonding is processing.".log()
                    }
                    BluetoothDevice.BOND_BONDED -> {
                        "Bonded".log()
                    }
                    BluetoothDevice.BOND_NONE -> {
                        "Bonding is fail!".log()
                    }
                }
            }
        }

        if (BluetoothDevicePicker.ACTION_DEVICE_SELECTED == intent?.action) {
            val device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice?
            device?.address.log()
        }
    }

    abstract fun onEnabledBluetooth()

    abstract fun onDisabledBluetooth()

    abstract fun onStartDiscovering()

    abstract fun onFinishDiscovering()

    abstract fun getFoundDevices(devices: List<BluetoothDevice>)
}

interface BluetoothDevicePicker {
    companion object {
        const val EXTRA_NEED_AUTH = "android.bluetooth.devicepicker.extra.NEED_AUTH"
        const val EXTRA_FILTER_TYPE = "android.bluetooth.devicepicker.extra.FILTER_TYPE"
        const val EXTRA_LAUNCH_PACKAGE = "android.bluetooth.devicepicker.extra.LAUNCH_PACKAGE"
        const val EXTRA_LAUNCH_CLASS =
            "android.bluetooth.devicepicker.extra.DEVICE_PICKER_LAUNCH_CLASS"

        /**
         * Broadcast when one BT device is selected from BT device picker screen.
         * Selected [BluetoothDevice] is returned in extra data named
         * [BluetoothDevice.EXTRA_DEVICE].
         */
        const val ACTION_DEVICE_SELECTED = "android.bluetooth.devicepicker.action.DEVICE_SELECTED"

        /**
         * Broadcast when someone want to select one BT device from devices list.
         * This intent contains below extra data:
         * - [.EXTRA_NEED_AUTH] (boolean): if need authentication
         * - [.EXTRA_FILTER_TYPE] (int): what kinds of device should be
         * listed
         * - [.EXTRA_LAUNCH_PACKAGE] (string): where(which package) this
         * intent come from
         * - [.EXTRA_LAUNCH_CLASS] (string): where(which class) this intent
         * come from
         */
        const val ACTION_LAUNCH = "android.bluetooth.devicepicker.action.LAUNCH"

        /**
         * Ask device picker to show all kinds of BT devices
         */
        const val FILTER_TYPE_ALL = 0

        /**
         * Ask device picker to show BT devices that support AUDIO profiles
         */
        const val FILTER_TYPE_AUDIO = 1

        /**
         * Ask device picker to show BT devices that support Object Transfer
         */
        const val FILTER_TYPE_TRANSFER = 2

        /**
         * Ask device picker to show BT devices that support
         * Personal Area Networking User (PANU) profile
         */
        const val FILTER_TYPE_PANU = 3

        /**
         * Ask device picker to show BT devices that support Network Access Point (NAP) profile
         */
        const val FILTER_TYPE_NAP = 4
    }
}