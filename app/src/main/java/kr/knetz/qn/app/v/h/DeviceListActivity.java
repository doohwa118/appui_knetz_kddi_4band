/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.knetz.qn.app.v.h;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

import kr.knetz.qn.app.R;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class DeviceListActivity extends Activity {
    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;
    private int listCnt = 0;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Debug.logi(new Exception(),"=dhjung=======> DeviceListActivity onCreate");

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);

//        this.setFinishOnTouchOutside(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL , WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);


//        // Initialize the button to perform device discovery
//        Button scanButton = (Button) findViewById(R.id.button_scan);
//        scanButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
////                doDiscovery();
//                v.setVisibility(View.GONE); //complete hidden
//            }
//        });
//
//        // Initialize array adapters. One for already paired devices and
//        // one for newly discovered devices
//        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
//        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
//
//        // Find and set up the ListView for paired devices
//        ListView pairedListView = findViewById(R.id.paired_devices);
//        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
//        pairedListView.setOnItemClickListener(mDeviceClickListener);
//
//        // Find and set up the ListView for newly discovered devices
//        ListView newDevicesListView = findViewById(R.id.new_devices);
//        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
//        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
//
//        // Register for broadcasts when a device is discovered
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        this.registerReceiver(mReceiver, filter);
//
//        // Register for broadcasts when discovery has finished
//        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        this.registerReceiver(mReceiver, filter);
//
//        // Get the local Bluetooth adapter
//        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        // Get a set of currently paired devices
//        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
//
//        // If there are paired devices, add each one to the ArrayAdapter
//        if (pairedDevices.size() > 0) {
//            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
//            for (BluetoothDevice device : pairedDevices) {
//                if(device.getName().contains("202007") || device.getName().contains("Sub6")) {
//                    mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//                    listCnt++;
//                }
//            }
//        } else {
//            String noDevices = getResources().getText(R.string.none_paired).toString();
//            mPairedDevicesArrayAdapter.add(noDevices);
//        }
//
//        if(pairedDevices.size() <= 0 || listCnt == 0){
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Pair a Bluetooth (Repeater)");
//            builder.setMessage("1. Swipe down from the top of the screen\n" +
//                    "2. Touch and hold Bluetooth Icon\n" +
//                    "3. Tap Pair new device\n" +
//                    "- If you  don't find Pair new device, check under 'Available devices' or tap More > Refresh.\n" +
//                    "4. Tap the name of the Bluetooth device you want to pair with your device\n");
//            builder.setNeutralButton("close", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
//                    startActivityForResult(intent, 0);
//
////                    finish();
////                    finishAndRemoveTask();
////                    android.os.Process.killProcess(android.os.Process.myPid());
//                }
//            });
//            builder.show();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Debug.logi(new Exception(),"=dhjung=======> DeviceListActivity onActivityResult");

//        Intent intent = new Intent(DeviceListActivity.this, DeviceListActivity.class);
//        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
//        Debug.logi(new Exception(),"=dhjung=======> DeviceListActivity onResume");

        Button scanButton = findViewById(R.id.button_scan);

        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                doDiscovery();
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivityForResult(intent, 0);

//                v.setVisibility(View.GONE); //complete hidden
            }
        });

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices
        ListView pairedListView = findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                //if(device.getName().contains("202006") || device.getName().contains("202007") || device.getName().contains("Sub6")) {
                if(device.getName().contains("QN") ) {
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    listCnt++;
                }
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }

        if(pairedDevices.size() <= 0 || listCnt == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pair a Bluetooth (Repeater)");
            builder.setCancelable(false);
            builder.setMessage("1. Swipe down from the top of the screen\n" +
                    "2. Touch and hold Bluetooth Icon\n" +
                    "3. Tap Pair new device\n" +
                    "- If you  don't find Pair new device, check under 'Available devices' or tap More > Refresh.\n" +
                    "4. Tap the name of the Bluetooth device you want to pair with your device\n");
            builder.setNeutralButton("Go Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivityForResult(intent, 0);

//                    finish();
//                    finishAndRemoveTask();
//                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Debug.logi(new Exception(),"=dhjung=======> DeviceListActivity onDestroy");

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

//            Debug.logi(new Exception(),"=dhjung=======> DeviceListActivity onItemClick");

            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
//                    Debug.logi(new Exception(),"=dhjung=======> mNewDevicesArrayAdapter");
                    String noDevices = getResources().getText(R.string.none_found).toString();
//                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };

}
