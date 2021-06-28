package kr.knetz.qn.app.v.h;

import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.knetz.usbserial.driver.UsbSerialDriver;
import com.knetz.usbserial.driver.UsbSerialProber;
import com.knetz.usbserial.util.HexDump;
import com.knetz.usbserial.util.SerialInputOutputManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.knetz.qn.app.KNetzApp;
import kr.knetz.qn.app.l.Crc16;
import kr.knetz.qn.app.o.BluetoothConnModel;
import kr.knetz.qn.app.l.ByteUtil;
import kr.knetz.qn.app.v.MainActivity;
import kr.knetz.qn.app.v.x.Variables;
import kr.knetz.qn.app.l.Debug;


public class DeviceService extends Service {


    private boolean mUsbOrBluetooth = Variables.USB_OR_BLUETOOTH_FLAG;
    private boolean mAllinOne = Variables.ALLINONE_FLAG;


    // USB
    private boolean mIsInit = false;
    private UsbManager mManager;
    private UsbDevice mUsbDevice;
    private static UsbSerialDriver sDriver = null;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    private PendingIntent mPermissionIntent;
    KNetzApp application;
    Messenger mMainActivity = null;
    Messenger mConnectActivity = null;
    Messenger mLoginActivity = null;
    Messenger mFileListActivity = null;

    final Messenger mMessenger = new Messenger(new IncomingHandler(DeviceService.this));

    private static final int MESSAGE_REFRESH = 101;
    private static long REFRESH_TIMEOUT_MILLIS = 500;

    private byte mFrameInfo;
    private byte mReceiverFrameInfo = 0x00;
    private byte mModemFrameInfo = 0x00;
    private int mTsyncFrameInfo = 0x00;
    private byte mDspFrameInfo = 0x00;

    private String sdpath;
    private FileInputStream fis = null;
    private int FwDownloadPercentage;
    private static int dlSendCntBuf = 0;
    private static byte[] dlFWDataBuf;
    private static byte[] dlRetrySendBuf = new byte[Variables.DATA_BUFFER_SIZE];

    // Bluetooth
    private static final String TAG = "BluetoothConnController";
    private static final boolean D = true;

    public static final String SEND_MSG_FROM_BT_ACTION = "SEND_MSG_FROM_BT_ACTION";
    public static final String CONNECT_REQUEST_ACTION = "CONNECT_REQUEST_ACTION";
    public static final String DISCONNECT_REQUEST_ACTION = "DISCONNECT_REQUEST_ACTION";
    public static final String REQUEST_ECHO_ACTION = "REQUEST_ECHO_ACTION";
    public static final String TOAST = "toast";
    public static final String START_MONITOR_ACTION = "START_MONITOR_ACTION";
    public static final String GET_SERIVICE_STATUS_ACTION = "GET_SERIVICE_STATUS_ACTION";
    public static final String GET_SERIVICE_STATUS_EVENT = "GET_SERIVICE_STATUS_EVENT";
    public static final String MONITOR_STATUS = "MONITOR_STATUS";
    public static final String TX_BYTES = "TX_BYTES";
    public static final String RX_BYTES = "RX_BYTES";

    private MessageReceiver mBtMsgReceiver;
    //private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothConnModel mConnService = null;
    private MessageHandler msgHandler;
    private BluetoothDevice mDevice = null;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_ALERT_DIALOG = 6;
    public static final int MESSAGE_LOGIN_ALERT_DIALOG = 7;
    public static final int MESSAGE_START = 8;
    public static final int	MSG_MODE_SEND_STRING = 1;
    public static final int	MSG_MODE_SEND_FILE = 2;


    private class MessageHandler extends Handler {
        public String deviceName = null;

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case MESSAGE_STATE_CHANGE:
                    //	int state = (Integer)msg.obj;
                    // 	if (state == BluetoothSocketConfig.SOCKET_NONE){
                    // 		if(D) Log.e(TAG, "DISCONECT!!!!!!!!!!!!!!");
                    //Intent disconnectIntent = new Intent(ServiceController.CHANGE_LOCAL_SETTING_ACTION);
                    //disconnectIntent.putExtra(ServiceModel.KEY_LOCAL_SETTING, ServiceModel.TYPE_DISCONNECT_ALERT);
                    //BluetoothConnController.this.sendBroadcast(disconnectIntent);
                    // 	}
                    // 	break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mDevice = (BluetoothDevice)msg.obj;

                    deviceName = mDevice.getName();
                    //deviceName = (String)msg.obj;
                    Log.w(TAG, "[handleMessage] Device name: " + deviceName);
                    //        mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    //       Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //      Log.w(TAG, "[handleMessage] Write message: "+writeMessage);
                    //      Log.w(TAG, "[handleMessage] currentTimeMillis: "+ System.currentTimeMillis() );
                    sendBroadcast(writeMessage, MainActivity.OUTGOING_MSG, msg.arg2);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer

//                    String readMessage = new String(readBuf, 0, msg.arg1);

                    //aa  Log.v(TAG, "[handleMessage] Read message: "+readMessage);
                    //aa  Log.v(TAG, "[handleMessage] currentTimeMillis: "+ System.currentTimeMillis() );
                    //    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    //	boolean isEcho = settings.getBoolean(BluetoothConn.KEY_ECHO_PREF, false);
                    //    if (isEcho){
                    //    	BluetoothConnController.this.sendMessage(readMessage);//echo
                    //    }
                    //       BluetoothConnController.this.sendMessage(readMessage);//echo

//                    sendBroadcast(readMessage, BluetoothConn.INCOMING_MSG, msg.arg2);

//                    sendBroadcast(readBuf, BluetoothConn.INCOMING_MSG, msg.arg2); // Knetz delete

                    updateReceivedData(readBuf);

                    //  sendBroadcast(readMessage, ServiceController.RECEIVE_MSG_FROM_BT_ACTION);
                    //TODO: support only one remote device now
                    deviceName = null;
                    break;

                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;

                case MESSAGE_ALERT_DIALOG:
//                    Debug.logi(new Exception(),"=dhjung=======> Normal DeviceService.MESSAGE_ALERT_DIALOG");

                    String str = (String) msg.obj;
	         /*   	AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
	            	builder.setMessage(str).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {

	    				@Override
	    				public void onClick(DialogInterface arg0, int arg1) {
	    					// TODO Auto-generated method stub
	    					arg0.cancel();
	    				}
	    			});
	            	AlertDialog alert = builder.create();
	            	alert.show();*/
                    sendBroadcast(str, MainActivity.ALERT_MSG, msg.arg1);
                    break;

                case MESSAGE_LOGIN_ALERT_DIALOG:
                    Debug.logi(new Exception(),"=dhjung=======> Login DeviceService.MESSAGE_ALERT_DIALOG");

                    String strMsg = (String) msg.obj;

                    sendBroadcast(strMsg, MainActivity.LOGIN_ALERT_MSG, msg.arg1);
                    break;

                case MESSAGE_START :
                    Debug.loge(new Exception(),"dhjung (handleMessage) MESSAGE_START");
                    // Param Initialize
                    Variables.CmdState = Variables.Cmd_GUI_SETTING;
                    Variables.TotalSendRetryCnt = 0;
                    Variables.SendRetryCnt = 0;
                    Variables.FlagBitCheck = 0;
                    dataTransmit();

                    mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);

                    break;
            }
        }
        private void sendBroadcast(String str, String action, int num){
            String displayString = null;
            if (action.equals(MainActivity.OUTGOING_MSG)){
                displayString = "Me : "+str;
            } else if (action.equals(MainActivity.INCOMING_MSG)){
                displayString = /*deviceName+" : "+*/str;
            } else {
                displayString = str;
            }
            Intent i = new Intent(action);
            i.putExtra("STR", displayString);
            i.putExtra("COUNTER", num);
            DeviceService.this.sendBroadcast(i);
        }
        private void sendBroadcast(byte[] bytes, String action, int num){
            String displayString = null;

//            if (action.equals(BluetoothConn.OUTGOING_MSG)){
//                displayString = "Me : "+str;
//            } else if (action.equals(BluetoothConn.INCOMING_MSG)){
//                displayString = /*deviceName+" : "+*/str;
//            } else {
//                displayString = str;
//            }

            Intent i = new Intent(action);
            i.putExtra("BYTES", bytes);
            i.putExtra("COUNTER", num);
            DeviceService.this.sendBroadcast(i);
        }
//		private void sendBroadcast(String str, String action){
//			sendBroadcast(str, action, 0);
//		}

    } //MessageHandler
    private void sendFile(String file){
        if (file.length() > 0) {
            mConnService.SendFileToAllSockets(file);
        }
    }
    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            // byte[] send = message.getBytes();
            mConnService.writeToAllSockets(message);
        }
    }

    private void sendMessage(byte[] message) {

        // Check that there's actually something to send
        if (message.length > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            // byte[] send = message.getBytes();
            if (mConnService != null)
                mConnService.writeToAllSockets(message);
        }
    }
    private void connectTo(String deviceAddress){
        //if (!deviceAddress.matches("[A-Za-z0-9]{2}:[A-Za-z0-9]{2}:[A-Za-z0-9]{2}:[A-Za-z0-9]{2}:[A-Za-z0-9]{2}:[A-Za-z0-9]{2}"))
        Log.e(TAG, "connectTo : deviceaddress : "+deviceAddress);
        if (!deviceAddress.matches("([0-9a-fA-F][0-9a-fA-F]:){5}([0-9a-fA-F][0-9a-fA-F])"))
        {
            Log.e(TAG, "address " + deviceAddress + " is wrong, length = " + deviceAddress.length());
            return;
        }
        if (BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress) == null)
        {
            Log.e(TAG, "adapter is not exist");
            return;
        }

        bluetoothflagchange();

        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
        mConnService.connectTo(device);
//        this.startForeground(1234, new Notification());
        Log.e(TAG, "connectTo!!!");
    }

    private void disconnectTo(String address){
        if(mConnService != null)
            mConnService.disconnectSocketFromAddress(address);
//        this.stopForeground(true);
        Log.e(TAG, "disconnectTo!!!" + address);
    }

    private void terminatedAllSockets(){
        //mConnService.terminated();
        if(mConnService != null)
            mConnService.terminated();
        mConnService = null;
        Log.e(TAG, "terminatedAllSockets!!!");
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action  = intent.getAction();
//            if(D) Log.e(TAG, "** ON RECEIVE **" + action);
            if (action.equals(SEND_MSG_FROM_BT_ACTION)){
//			    String msg = intent.getExtras().getString("MESSAGEB");
                byte[] msg = intent.getExtras().getByteArray("MESSAGEB");
                int mode = intent.getExtras().getInt("MODE");
                switch(mode)
                {
                    case MSG_MODE_SEND_STRING:
                        sendMessage(msg);
                        break;
                    case MSG_MODE_SEND_FILE:
//			    	sendFile(msg);
                        break;
                }

            } else if (action.equals(CONNECT_REQUEST_ACTION)){
                String deviceAddress = intent.getExtras().getString(MainActivity.DEVICE_ADDRESS);
                Log.e(TAG, "[onReceive] deviceAddress = " + deviceAddress);
//                "00:00:00:00:00:0A"
                DeviceService.this.connectTo(deviceAddress);
            }else if (action.equals(DISCONNECT_REQUEST_ACTION)){
                Log.e(TAG, "[onReceive] DISCONNECT_REQUEST_ACTION");
                String deviceAddress = intent.getExtras().getString(MainActivity.DISCONNECT_DEVICE_ADDRESS);
                Log.e(TAG, "[onReceive] disconnect device address = " + deviceAddress);
                DeviceService.this.disconnectTo(deviceAddress);
            }else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                Log.i(TAG, "[onReceive] ACTION_STATE_CHANGED");
                int currentState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (currentState){
                    case BluetoothAdapter.STATE_ON:
                        Log.i(TAG, "[onReceive] current state = ON");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.i(TAG, "[onReceive] current state = OFF");
                        DeviceService.this.terminatedAllSockets();

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.i(TAG, "[onReceive] current state = TURNING_ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.i(TAG, "[onReceive] current state = TURNING_OFF");
                        break;
                }
                //	Log.i(TAG, "[onReceive] current state = "+currentState);
                //BluetoothConnController.this.disconnectTo(deviceAddress);
            }else if(action.equals(START_MONITOR_ACTION)){
//                Log.d(TAG, "START_MONITOR_ACTION");
                mConnService.startFileMonitor(intent.getBooleanExtra(MONITOR_STATUS, false));
            }else if(action.equals(GET_SERIVICE_STATUS_ACTION)){
                Intent i = new Intent(GET_SERIVICE_STATUS_EVENT);
                i.putExtra(MONITOR_STATUS, true);//mConnService.getFileMonitor());
                i.putExtra(TX_BYTES, mConnService.getTxBytes());
                i.putExtra(RX_BYTES, mConnService.getRxBytes());
                DeviceService.this.sendBroadcast(i);
            }else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
                BluetoothDevice device = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);
                //Toast.makeText(getApplicationContext(), device.getName() + " was disconnected: " + device.getAddress(),
                //        Toast.LENGTH_SHORT).show();
                DeviceService.this.disconnectTo(device.getAddress());
//                Log.d(TAG, "BT connection was disconnected!" + device.getAddress());
                //BluetoothConnController.this.msgHandler.sendBroadcast(device.getName() + " was disconnected: " + device.getAddress(), BluetoothConn.ALERT_MSG, -1);
            }else if(action.equals(MainActivity.OUTGOING_MSG)){
//                Log.e(TAG, "Knetz OUTGOING_MSG : " + action);
            }else if(action.equals(MainActivity.INCOMING_MSG)){
//                Log.e(TAG, "Knetz INCOMING_MSG : " + action);
            }
            else if (action.equals(MainActivity.ALERT_MSG)) {
//                Log.e(TAG, "Knetz ALERT_MSG : " + action);
//                String displayMsg = intent.getExtras().getString("STR");
//                mConversationArrayAdapter.add(displayMsg);
//                Log.d(TAG, displayMsg + "counter = " + mConversationArrayAdapter.getCount());
//                if (intent.getExtras().getInt("COUNTER") > 0){
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
//                    builder.setIcon(android.R.drawable.stat_notify_error)
//                            .setTitle("Error")
//                            .setMessage(displayMsg)
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener(){
//
//                                public void onClick(DialogInterface dialog, int button) {
//                                }
//                            })
//                            .create();
//                    AlertDialog alert = builder.create();
//                    alert.show();
//                }
            }
            else if (action.equals(DeviceService.GET_SERIVICE_STATUS_EVENT)) {
//                Log.e(TAG, "action GET_SERIVICE_STATUS_EVENT : " + action);
            }
            else{
//                Log.e(TAG, "another action: " + action);
            }
        }

    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str = intent.getAction();
            Debug.loge(new Exception(),"==============================================");
            Debug.loge(new Exception(),"BroadCastReceiver getAction : "+str);
            Debug.loge(new Exception(),"==============================================");

            if (Variables.ACTION_USB_PERMISSION.equals(str)){
                Debug.loge(new Exception(),"BroadCastReceiver getAction1111 : "+str);
                synchronized (this){
                    UsbDevice localUsbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,false)){
                        if (localUsbDevice != null){
//                            for (final UsbDevice device : mManager.getDeviceList().values()) {
                                if (localUsbDevice != null) {
                                    final List<UsbSerialDriver> drivers = UsbSerialProber.probeSingleDevice(mManager, localUsbDevice);

                                    Debug.logd(new Exception(), "Found usb device:" + localUsbDevice);
                                    if (drivers.isEmpty()) {
                                        Debug.logd(new Exception(), " - No UsbSerialDriver available."); // Unsupport Device
                                    } else {
                                        Debug.logd(new Exception(), " - ??????????????.");
                                        for (UsbSerialDriver driver : drivers) {
                                            Debug.logd(new Exception(), " + " + driver); // support Device
                                            sDriver = driver;
                                        }
                                    }
                                }else {
                                    Debug.loge(new Exception(), "device null!!!");
                                }

//                            }

                            Debug.logd(new Exception(), "Resumed, sDriver=" + sDriver);

                            if (sDriver == null) {
                                Debug.loge(new Exception(),"No Serial Device.");

                            } else {
                                try {
                                    sDriver.open();
                                    sDriver.setParameters(115200, 8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
                                } catch (IOException e) {
                                    Debug.loge(new Exception(), "Error setting up device: " + e.getMessage());
                                    try {
                                        sDriver.close();
                                    } catch (IOException e2) {
                                        //
                                    }
                                    sDriver = null;
//                                    return -1;
                                }
                                Debug.logd(new Exception(),"Serial device: " + sDriver.getClass().getCanonicalName());
                                onDeviceStateChange();

                                //requestRepeaterStatus();
                                dataTransmit();
                                mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);
                            }
                        }
                    }else {
                        Debug.loge(new Exception(),"permission denied for device "+ localUsbDevice);
                    }
                }
            }else if (Variables.ACTION_USB_DETACHED.equals(str)){
                Debug.loge(new Exception(),"BroadCastReceiver getActio22222 : "+str);
                stopServiceToMainActivity();
            }else if (Variables.ACTION_USB_ATTACHED.equals(str)){
                Debug.loge(new Exception(),"BroadCastReceiver getActio333 : "+str);
            }
        }
    };



    static class IncomingHandler extends Handler {
        private final WeakReference<DeviceService> mService;
        IncomingHandler(DeviceService service){
            mService = new WeakReference<DeviceService>(service);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DeviceService service = mService.get();
            if (service != null){
                service.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg){
        switch (msg.what) {
            case Variables.MSG_REGISTER_CLIENT :
                Debug.logi(new Exception(),"Service MSG_REGISTER_CLIENT");
                mMainActivity = msg.replyTo;
                break;
            case Variables.MSG_REGISTER_CLIENT_CONNECTACTIVITY :
                Debug.loge(new Exception(), "Service MSG_REGISTER_CLIENT_CONNECTACTIVITY");
                mConnectActivity = msg.replyTo;
                break;
            case Variables.MSG_REGISTER_CLIENT_LOGINACTIVITY:
                Debug.loge(new Exception(), "Service MSG_REGISTER_CLIENT_LOGINACTIVITY");
                mLoginActivity = msg.replyTo;
                break;
            case Variables.MSG_REGISTER_CLIENT_FILELISTACTIVITY:
                Debug.loge(new Exception(), "Service MSG_REGISTER_CLIENT_FILELISTACTIVITY");
                mFileListActivity = msg.replyTo;
                break;

            case Variables.MSG_MAINACTIVITY_TO_SERVICE_SETTING :
//                Debug.loge(new Exception(),"dhjung --> Service MSG_MAINACTIVITY_TO_SERVICE_SETTING");
                mFrameInfo = msg.getData().getByte("data");
//                Debug.loge(new Exception(),"dhjung --> frameinfo : "+String.format("0x%02x",mFrameInfo));
                break;
            case Variables.MSG_MAINACTIVITY_TO_SERVICE_SETTING_STOP :
//                Debug.loge(new Exception(),"dhjung --> Service MSG_MAINACTIVITY_TO_SERVICE_SETTING_STOP");
                mFrameInfo = 0x00;
                break;
            case Variables.MSG_MAINACTIVITY_TO_SERVICE_RECEIVER_SETTING :
//                Debug.loge(new Exception(),"dhjung --> Service MSG_MAINACTIVITY_TO_SERVICE_RECEIVER_SETTING");
                mReceiverFrameInfo = msg.getData().getByte("receiver_data");
//                Debug.loge(new Exception(),"dhjung --> mReceiverFrameInfo : "+String.format("0x%02x",mReceiverFrameInfo));
                break;
            case Variables.MSG_MAINACTIVITY_TO_SERVICE_RECEIVER_SETTING_STOP :
//                Debug.loge(new Exception(),"dhjung --> Service MSG_MAINACTIVITY_TO_SERVICE_RECEIVER_SETTING_STOP");
                mReceiverFrameInfo = 0x00;
                break;
            case Variables.MSG_MAINACTIVITY_TO_SERVICE_MODEM_SETTING :
//                Debug.loge(new Exception(),"dhjung --> Service MSG_MAINACTIVITY_TO_SERVICE_MODEM_SETTING");
                mModemFrameInfo = msg.getData().getByte("modem_data");
//                Debug.loge(new Exception(),"dhjung --> mModemFrameInfo : "+String.format("0x%02x",mModemFrameInfo));
                break;
            case Variables.MSG_MAINACTIVITY_TO_SERVICE_MODEM_SETTING_STOP :
//                Debug.loge(new Exception(),"dhjung --> Service MSG_MAINACTIVITY_TO_SERVICE_MODEM_SETTING_STOP");
                mModemFrameInfo = 0x00;
                break;
            case Variables.MSG_MAINACTIVITY_TO_SERVICE_TSYNC_SETTING :
//                Debug.loge(new Exception(),"dhjung --> Service MSG_MAINACTIVITY_TO_SERVICE_TSYNC_SETTING");
                mTsyncFrameInfo = msg.getData().getByte("tsync_data");
//                Debug.loge(new Exception(),"dhjung --> mTsyncFrameInfo : "+String.format("0x%02x",mTsyncFrameInfo));
                break;
            case Variables.MSG_MAINACTIVITY_TO_SERVICE_TSYNC_SETTING_STOP :
//                Debug.loge(new Exception(),"dhjung --> Service MSG_MAINACTIVITY_TO_SERVICE_TSYNC_SETTING_STOP");
                mTsyncFrameInfo = 0x00;
                break;
            case Variables.MSG_MAINACTIVITY_TO_SERVICE_DSP_CTRL :
//                Debug.loge(new Exception(),"dhjung --> Service MSG_MAINACTIVITY_TO_SERVICE_DSP_CTRL");
                mDspFrameInfo = msg.getData().getByte("dsp_data");
//                Debug.loge(new Exception(),"dhjung --> mDspFrameInfo : "+String.format("0x%02x",mDspFrameInfo));
                break;
            case Variables.MSG_MAINACTIVITY_TO_SERVICE_DSP_CTRL_STOP :
//                Debug.loge(new Exception(),"dhjung --> Service MSG_MAINACTIVITY_TO_SERVICE_DSP_CTRL_STOP");
                mDspFrameInfo = 0x00;
                break;

            case Variables.MSG_FILELISTACTIVITY_TO_SERVICE_PATH :
                Debug.loge(new Exception(),"Service MSG_FILELISTACTIVITY_TO_SERVICE_PATH");
                sdpath = msg.getData().getString("path");
                Debug.loge(new Exception(),"sdpath : "+ sdpath);
                Variables.Flag_Action_Download = 1;
                break;
            case Variables.MSG_LOGINACTIVITY_TO_SERVICE_START :
                Debug.loge(new Exception(),"Service MSG_LOGINACTIVITY_TO_SERVICE_START");
                if (!mAllinOne) {
                    if (mUsbOrBluetooth) {
                        startUsbConnection();
                    } else {
                        startBluetoothConnection();
                    }
                }else{
                    startAllInOneConnection();
                }
                break;
            case Variables.MSG_LOGINACTIVITY_TO_SERVICE_FINISH:
            case Variables.MSG_FILELISTACTIVITY_TO_SERVICE_FINISH:
                Debug.loge(new Exception(),"Service MSG_LOGINACTIVITY_TO_SERVICE_FINISH");
                stopServiceToMainActivity();
                break;
            case Variables.MSG_LOGINACTIVITY_TO_SERVICE_BLUETOOTH_INIT :
                Debug.loge(new Exception(),"Service MSG_LOGINACTIVITY_TO_SERVICE_BLUETOOTH_INIT");
                startBluetooth();
                break;

            case Variables.MSG_LOGINACTIVITY_TO_SERVICE_DATA :
                break;

            default :
                Debug.loge(new Exception(),"Service Default Msg : "+msg.what);
                break;
        }
    }

    private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener(){
        @Override
        public  void onRunError(Exception e){
            Debug.loge(new Exception(), "Runner stopped.");
        }
        @Override
        public void onNewData(final byte[] data) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    updateReceivedData(data);
                }
            };
            runnable.run();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
//        Debug.loge(new Exception(), "=dhjung=======> DeviceService onCreate");

        application = (KNetzApp)getApplication();
        if (!mAllinOne) {
            if (mUsbOrBluetooth) { // USB
                mManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(Variables.ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(Variables.ACTION_USB_PERMISSION);
                registerReceiver(mUsbReceiver, filter);
                IntentFilter filter2 = new IntentFilter(Variables.ACTION_USB_DETACHED);
                registerReceiver(mUsbReceiver, filter2);
            } else {

                msgHandler = new MessageHandler();
                IntentFilter mFilter01, mFilter02, mFilter03, stateChangedFilter;
                mFilter01 = new IntentFilter(SEND_MSG_FROM_BT_ACTION);
                mFilter02 = new IntentFilter(CONNECT_REQUEST_ACTION);
                mFilter03 = new IntentFilter(DISCONNECT_REQUEST_ACTION);
                stateChangedFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                mBtMsgReceiver = new MessageReceiver();
                registerReceiver(mBtMsgReceiver, mFilter01);
                registerReceiver(mBtMsgReceiver, mFilter02);
                registerReceiver(mBtMsgReceiver, mFilter03);
                registerReceiver(mBtMsgReceiver, new IntentFilter(START_MONITOR_ACTION));
                registerReceiver(mBtMsgReceiver, stateChangedFilter);
                registerReceiver(mBtMsgReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
                registerReceiver(mBtMsgReceiver, new IntentFilter(GET_SERIVICE_STATUS_ACTION));

                IntentFilter mFilter001, mFilter002;
                mFilter001 = new IntentFilter(MainActivity.INCOMING_MSG);
                mFilter002 = new IntentFilter(MainActivity.OUTGOING_MSG);
                registerReceiver(mBtMsgReceiver, mFilter001);
                registerReceiver(mBtMsgReceiver, mFilter002);
                registerReceiver(mBtMsgReceiver, new IntentFilter(MainActivity.ALERT_MSG));
                registerReceiver(mBtMsgReceiver, new IntentFilter(DeviceService.GET_SERIVICE_STATUS_EVENT));
            }
        }else{
            //USB
            mManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(Variables.ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(Variables.ACTION_USB_PERMISSION);
            registerReceiver(mUsbReceiver, filter);
            IntentFilter filter2 = new IntentFilter(Variables.ACTION_USB_DETACHED);
            registerReceiver(mUsbReceiver, filter2);
            //Bluetooth
            msgHandler = new MessageHandler();
            IntentFilter mFilter01, mFilter02, mFilter03, stateChangedFilter;
            mFilter01 = new IntentFilter(SEND_MSG_FROM_BT_ACTION);
            mFilter02 = new IntentFilter(CONNECT_REQUEST_ACTION);
            mFilter03 = new IntentFilter(DISCONNECT_REQUEST_ACTION);
            stateChangedFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            mBtMsgReceiver = new MessageReceiver();
            registerReceiver(mBtMsgReceiver, mFilter01);
            registerReceiver(mBtMsgReceiver, mFilter02);
            registerReceiver(mBtMsgReceiver, mFilter03);
            registerReceiver(mBtMsgReceiver, new IntentFilter(START_MONITOR_ACTION));
            registerReceiver(mBtMsgReceiver, stateChangedFilter);
            registerReceiver(mBtMsgReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
            registerReceiver(mBtMsgReceiver, new IntentFilter(GET_SERIVICE_STATUS_ACTION));

            IntentFilter mFilter001, mFilter002;
            mFilter001 = new IntentFilter(MainActivity.INCOMING_MSG);
            mFilter002 = new IntentFilter(MainActivity.OUTGOING_MSG);
            registerReceiver(mBtMsgReceiver, mFilter001);
            registerReceiver(mBtMsgReceiver, mFilter002);
            registerReceiver(mBtMsgReceiver, new IntentFilter(MainActivity.ALERT_MSG));
            registerReceiver(mBtMsgReceiver, new IntentFilter(DeviceService.GET_SERIVICE_STATUS_EVENT));
        }


    }
    private UsbDevice enumerateDevice(){
        Iterator localIterator = this.mManager.getDeviceList().values().iterator();
        UsbDevice localUsbDevice;
        do
        {
            if (!localIterator.hasNext())
                return null;
            localUsbDevice = (UsbDevice)localIterator.next();
            int i = localUsbDevice.getVendorId();
            Debug.loge(new Exception(),"venderId :"+i );
            if ((i != 1027) && (i != 9025) && (i != 5824) && (i != 4292) && (i != 1155))
                continue;
//            this.mManager.requestPermission(localUsbDevice, this.mPermissionIntent);
            return localUsbDevice;
        }while(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Debug.loge(new Exception(), "=dhjung=======> DeviceService onStartCommand");

        if (mUsbOrBluetooth) {

//            mUsbDevice = enumerateDevice();
//            Debug.loge(new Exception(), "onStartCommand 222222");
//            if (mUsbDevice != null) {
//                final List<UsbSerialDriver> drivers = UsbSerialProber.probeSingleDevice(mManager, mUsbDevice);
//                Debug.logd(new Exception(), "Found usb device:" + mUsbDevice);
//                if (drivers.isEmpty()) {
//                    Debug.logd(new Exception(), " - No UsbSerialDriver available."); // Unsupport Device
//                } else {
//                    Debug.logd(new Exception(), " - ??????????????.");
//                    for (UsbSerialDriver driver : drivers) {
//                        Debug.logd(new Exception(), " + " + driver); // support Device
//                        sDriver = driver;
//                    }
//                }
//            } else {
//                Debug.loge(new Exception(), "device null!!!");
//            }
//
//            Debug.logd(new Exception(), "Resumed, sDriver=" + sDriver);
//            if (sDriver == null) {
//                Debug.loge(new Exception(), "No Serial Device.");
//                if (mUsbDevice == null) {
//                    Debug.loge(new Exception(), "not connected Cable");
//                } else {
//                    this.mManager.requestPermission(mUsbDevice, this.mPermissionIntent);
//                }
//            } else {
//                try {
//                    sDriver.open();
//                    sDriver.setParameters(115200, 8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
//                } catch (IOException e) {
//                    Debug.loge(new Exception(), "Error setting up device: " + e.getMessage());
//                    try {
//                        sDriver.close();
//                    } catch (IOException e2) {
//                        //
//                    }
//                    sDriver = null;
//                    return -1;
//                }
//                Debug.loge(new Exception(), "Serial device: " + sDriver.getClass().getCanonicalName());
//                onDeviceStateChange();
//
//                //requestRepeaterStatus();
//                // Param Initialize
//                Variables.CmdState = Variables.Cmd_GUI_SETTING;
//                Variables.TotalSendRetryCnt = 0;
//                Variables.SendRetryCnt = 0;
//                Variables.FlagBitCheck = 0;
//                dataTransmit();
//
//                mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);
//            }
        }else { // Bluetooth
//            if (mConnService == null){
//                mConnService = new BluetoothConnModel(this, msgHandler);
//                mConnService.startSession();
//
//
//                Debug.loge(new Exception(),"Knetz before ");
//                DeviceConnectingService.this.connectTo("00:00:00:00:00:0A");
//                Debug.loge(new Exception(),"Knetz after ");
//            }

        }

        return START_STICKY;

    }

    private void bluetoothflagchange(){
        Variables.USB_OR_BLUETOOTH_FLAG = false;
        mUsbOrBluetooth = Variables.USB_OR_BLUETOOTH_FLAG;
    }

    private void startBluetoothConnection(){
        Debug.loge(new Exception(), "=dhjung=======> DeviceService startBluetoothConnection");


        Variables.USB_OR_BLUETOOTH_FLAG = false;
        mUsbOrBluetooth = Variables.USB_OR_BLUETOOTH_FLAG;
        if (mConnService == null){
            mConnService = new BluetoothConnModel(this, msgHandler);
            mConnService.startSession();

            Debug.logi(new Exception(), "before ");
            DeviceService.this.connectTo("00:00:00:00:00:0A");
            Debug.logi(new Exception(), "after ");
        }


    }

    private void startBluetooth(){
        Debug.loge(new Exception(), "=dhjung=======> DeviceService startBluetooth");

        Variables.USB_OR_BLUETOOTH_FLAG = false;
        mUsbOrBluetooth = Variables.USB_OR_BLUETOOTH_FLAG;
        if (mConnService == null){
            mConnService = new BluetoothConnModel(this, msgHandler);
            mConnService.startSession();
        }
    }


    private void startUsbConnection(){
        Debug.logi(new Exception(),"startUsbConnection() START");
        mUsbDevice = enumerateDevice();
        Debug.loge(new Exception(), "onStartCommand 222222");
        if (mUsbDevice != null) {
            final List<UsbSerialDriver> drivers = UsbSerialProber.probeSingleDevice(mManager, mUsbDevice);
            Debug.logd(new Exception(), "Found usb device:" + mUsbDevice);
            if (drivers.isEmpty()) {
                Debug.logd(new Exception(), " - No UsbSerialDriver available."); // Unsupport Device
            } else {
                Debug.logd(new Exception(), " - ??????????????.");
                for (UsbSerialDriver driver : drivers) {
                    Debug.logd(new Exception(), " + " + driver); // support Device
                    sDriver = driver;
                }
            }
        } else {
            Debug.loge(new Exception(), "device null!!!");
        }

        Debug.logd(new Exception(), "Resumed, sDriver=" + sDriver);
        if (sDriver == null) {
            Debug.loge(new Exception(), "No Serial Device.");
            if (mUsbDevice == null) {
                Debug.loge(new Exception(), "not connected Cable");
                sendCableNotConnectionActivityFinish();
            } else {
                this.mManager.requestPermission(mUsbDevice, this.mPermissionIntent);
            }
        } else {
            try {
                sDriver.open();
                sDriver.setParameters(115200, 8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
            } catch (IOException e) {
                Debug.loge(new Exception(), "Error setting up device: " + e.getMessage());
                try {
                    sDriver.close();
                } catch (IOException e2) {
                    //
                }
                sDriver = null;
                return;
            }

//            Debug.loge(new Exception(), "dhjung (startUsbConnection) Serial device: " + sDriver.getClass().getCanonicalName());
            onDeviceStateChange();

            //requestRepeaterStatus();
            // Param Initialize
            Variables.CmdState = Variables.Cmd_GUI_SETTING;
            Variables.TotalSendRetryCnt = 0;
            Variables.SendRetryCnt = 0;
            Variables.FlagBitCheck = 0;
            dataTransmit();

            mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);
        }
        Debug.logi(new Exception(),"startUsbConnection() END");

    }

    private void startAllInOneConnection(){
        Debug.loge(new Exception(), "=dhjung=======> DeviceService startAllInOneConnection");

        mUsbDevice = enumerateDevice();
        Debug.loge(new Exception(), "onStartCommand 222222");
        if (mUsbDevice != null) {
            final List<UsbSerialDriver> drivers = UsbSerialProber.probeSingleDevice(mManager, mUsbDevice);
            Debug.logd(new Exception(), "Found usb device:" + mUsbDevice);
            if (drivers.isEmpty()) {
                Debug.logd(new Exception(), " - No UsbSerialDriver available."); // Unsupport Device
            } else {
                Debug.logd(new Exception(), " - ??????????????.");
                for (UsbSerialDriver driver : drivers) {
                    Debug.logd(new Exception(), " + " + driver); // support Device
                    sDriver = driver;
                }
            }
        } else {
            Debug.loge(new Exception(), "device null!!!");
        }

        Debug.logd(new Exception(), "Resumed, sDriver=" + sDriver);
        if (sDriver == null) {
            Debug.loge(new Exception(), "No Serial Device.");
            if (mUsbDevice == null) {
                Debug.loge(new Exception(), "not connected Cable");
//                sendCableNotConnectionActivityFinish();
                sendBluetoothCheckDialogShow();
                return;
            } else {
                this.mManager.requestPermission(mUsbDevice, this.mPermissionIntent);
            }
        } else {
            try {
                sDriver.open();
                sDriver.setParameters(115200, 8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
            } catch (IOException e) {
                Debug.loge(new Exception(), "Error setting up device: " + e.getMessage());
                try {
                    sDriver.close();
                } catch (IOException e2) {
                    //
                }
                sDriver = null;
                return;
            }

            Debug.loge(new Exception(), "dhjung (startAllInOneConnection) Serial device: " + sDriver.getClass().getCanonicalName());
            onDeviceStateChange();

            //requestRepeaterStatus();
            // Param Initialize
            Variables.CmdState = Variables.Cmd_GUI_SETTING;
            Variables.TotalSendRetryCnt = 0;
            Variables.SendRetryCnt = 0;
            Variables.FlagBitCheck = 0;
            dataTransmit();

            mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);
        }
        Debug.logd(new Exception(),"startAllInOneConnection() END");

    }



    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            switch(msg.what) {
                case MESSAGE_REFRESH:
                    //requestRepeaterStatus();
                    dataTransmit();
                    mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);
                    break;

                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };


    private void requestRepeaterStatus(){
        byte[] txData = {0x7E, 0x01, 0x11, 0x04, 0x16, 0x7F};
        // if usb connect check
        //checkInfo();
        mSerialIoManager.writeAsync(txData);
    }

    private void stopIoManager(){
        if(mSerialIoManager != null){
//            Debug.loge(new Exception(), "=dhjung=======> DeviceService stopIoManager");

            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager(){
        if(sDriver != null){
            Debug.loge(new Exception(), "=dhjung=======> DeviceService startIoManager");

            mSerialIoManager = new SerialInputOutputManager(sDriver, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange(){
        stopIoManager();
        startIoManager();
    }

    public void updateReceivedData(byte[] data) {
//        Debug.loge(new Exception(), "mIsInit : " + mIsInit + "\n" + HexDump.toHexString(data));
        Debug.logi(new Exception(), "=dhjung=======> mIsInit : " + mIsInit + "\n" + HexDump.toHexString(data));
        if (mIsInit){
            if (mMainActivity != null){
                try{
                    Message msg = Message.obtain(null, Variables.MSG_SERVICE_TO_MAINACTIVITY_DATA,data);
                    mMainActivity.send(msg);
                } catch(RemoteException e){
                    e.printStackTrace();
                }
            }
        }else {
            mIsInit = true;
            Debug.loge(new Exception(),"msIsInit false");
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.putExtra("InitData",data);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            boolean flag = false;

            if(mLoginActivity != null) {
                Debug.loge(new Exception(),"mAllinOne : "+mAllinOne+" mUsbOrBluetooth : "+mUsbOrBluetooth);
                if(!mAllinOne) {
                    if (mUsbOrBluetooth) {
                        if (sDriver == null || mUsbDevice == null) {
                            try {
                                flag = true;
                                Message msg = Message.obtain(null, Variables.MSG_SERVICE_TO_LOGINACTIVITY_CABLE_NOT_CONNECT);
                                mLoginActivity.send(msg);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        Debug.loge(new Exception(), "flag : " + flag);
                        if (!flag) {
                            if (data.length > 7) {
                                String model = Parse_Modelname(MainActivity.getDec(data));
                                Debug.loge(new Exception(), "model : " + model);
                                Debug.loge(new Exception(), "model : " + model);
                                application.getDBRTypeUpdate(model);
                                try {
                                    Thread.sleep(500L);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
//                        startActivity(intent);
//                        ConnectActivity activity = (ConnectActivity) ConnectActivity.mConnectActivity;
//                        activity.finish();
                                sendLoginActivityFinish();
                            } else {
                                mIsInit = false;
                            }
                        }
                    } else {
//                    Debug.logi(new Exception(), "modelname hex  : " + HexDump.toHexString(data));
                        if (data.length > 7) {
                            String model = Parse_Modelname(MainActivity.getDec(data));
                    Debug.loge(new Exception(), "bluetooth Knetz model : " + model);
                    Debug.logi(new Exception(), "bluetooth Knetz model : " + model);
                            application.getDBRTypeUpdate(model);
                            try {
                                Thread.sleep(500L);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
//                    startActivity(intent);
//                    ConnectActivity activity = (ConnectActivity) ConnectActivity.mConnectActivity;
//                    activity.finish();
                            sendLoginActivityFinish();
                        } else {
                            mIsInit = false;
                        }
                    }
                }else{
                    if (data.length > 7) {
                        String model = Parse_Modelname(MainActivity.getDec(data));
                    Debug.loge(new Exception(), "bluetooth Knetz model : " + model);
                    Debug.logi(new Exception(), "bluetooth Knetz model : " + model);
                        application.getDBRTypeUpdate(model);
                        try {
                            Thread.sleep(500L);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                    startActivity(intent);
//                    ConnectActivity activity = (ConnectActivity) ConnectActivity.mConnectActivity;
//                    activity.finish();
                        sendLoginActivityFinish();
                    } else {
                        mIsInit = false;
                    }
                }
            }
        }

    }
    private void sendCableNotConnectionActivityFinish(){
        Debug.logd(new Exception(), "sendCableNotConnectionActivityFinish() START ");
        try {
            Message msg = Message.obtain(null, Variables.MSG_SERVICE_TO_LOGINACTIVITY_CABLE_NOT_CONNECT);
            mLoginActivity.send(msg);
        }catch (RemoteException e){
            e.printStackTrace();
        }
        Debug.logd(new Exception(), "sendCableNotConnectionActivityFinish() END ");
    }

    private void sendBluetoothCheckDialogShow(){
        Debug.loge(new Exception(), "sendBluetoothCheckDialogShow() START ");
        try {
            Message msg = Message.obtain(null, Variables.MSG_SERVICE_TO_LOGINACTIVITY_BLUETOOTH_CHECK);
            mLoginActivity.send(msg);
        }catch (RemoteException e){
            e.printStackTrace();
        }
        Debug.loge(new Exception(), "sendBluetoothCheckDialogShow() END ");
    }

    private void sendLoginActivityFinish(){
        Debug.logd(new Exception(), "sendLoginActivityFinish() START ");
        try {
            Message msg = Message.obtain(null, Variables.MSG_SERVICE_TO_LOGINACTIVITY_FINISH);
            mLoginActivity.send(msg);
        }catch (RemoteException e){
            e.printStackTrace();
        }
        Debug.logd(new Exception(),"sendLoginActivityFinish() END ");
    }

    public void stopServiceToMainActivity(){
        if (mMainActivity != null){
            try{
                Message msg = Message.obtain(null, Variables.MSG_SERVICE_TO_MAINACTIVITY_STOP);
//                Debug.loge(new Exception(), "=dhjung=======> DeviceService stopServiceToMainActivity");

                mMainActivity.send(msg);
            }catch(RemoteException e){
                e.printStackTrace();
            }
        }
    }

    public void firmwareDownloadPercentage(int data){
        if(mMainActivity != null){
            try{
                Message msg = Message.obtain(null, Variables.MSG_SERVICE_TO_MAINACTIVITY_FIRMWARE_DOWNLOAD);
                Bundle bundle = new Bundle();
                bundle.putInt("data",data);
                msg.setData(bundle);
                mMainActivity.send(msg);
            } catch(RemoteException e){
                e.printStackTrace();
            }
        }
    }

    public void firmwareDownloadFail() {
        if(mMainActivity != null){
            try{
                Message msg = Message.obtain(null, Variables.MSG_SERVICE_TO_MAINACTIVITY_FIRMWARE_DOWNLOAD_FAIL);
                mMainActivity.send(msg);
            }catch(RemoteException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Debug.loge(new Exception(),"onBind");
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Debug.loge(new Exception(),"onUnbind");
        if (!mAllinOne) {
            if (mUsbOrBluetooth) {
                mHandler.removeMessages(MESSAGE_REFRESH);

                unregisterReceiver(mUsbReceiver);
                try {
                    if (sDriver != null)
                        sDriver.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stopSelf();
                stopIoManager();
            } else {
                mHandler.removeMessages(MESSAGE_REFRESH);
                if (mConnService != null) {
                    mConnService.disconnectSocketFromAddress("00:00:00:00:00:0A");
                    mConnService.disconnectServerSocket();
//                mConnService.terminated();


                }
                mConnService = null;
                stopSelf();
            }
        }else{

            mHandler.removeMessages(MESSAGE_REFRESH);
            // USB
            if (mUsbReceiver != null)
                unregisterReceiver(mUsbReceiver);

            try {
                if (sDriver != null)
                    sDriver.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stopIoManager();
            // Bluetooth
            if (mConnService != null) {
//                mConnService.disconnectSocketFromAddress("00:00:00:00:00:0A");
//                mConnService.disconnectServerSocket();
                mConnService.terminated();
            }
            mConnService = null;
            stopSelf();
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        Debug.loge(new Exception(), "=dhjung=======> DeviceService onDestroy");

        if (!mAllinOne) {
            if (mUsbOrBluetooth) {
                if (mManager != null)
                    mManager = null;
//        mManager.close();

                try {
                    if (sDriver != null)
                        sDriver.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stopIoManager();
//        unregisterReceiver(mUsbReceiver);
            } else {

                stopSelf();
                if (mBtMsgReceiver != null)
                unregisterReceiver(mBtMsgReceiver);
            }
        }else{
            //USB
            if (mManager != null)
                mManager = null;
            try {
                if (sDriver != null)
                    sDriver.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stopIoManager();
            //Bluetooth
            stopSelf();
            if (mBtMsgReceiver != null)
                unregisterReceiver(mBtMsgReceiver);
        }
    }

    private void checkInfo() {
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        String i = "";
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            i += "\n" +
                    "DeviceID: " + device.getDeviceId() + "\n" +
                    "DeviceName: " + device.getDeviceName() + "\n" +
                    "DeviceClass: " + device.getDeviceClass() + " - "
                    + translateDeviceClass(device.getDeviceClass()) + "\n" +
                    "DeviceSubClass: " + device.getDeviceSubclass() + "\n" +
                    "VendorID: " + device.getVendorId() + "\n" +
                    "ProductID: " + device.getProductId() + "\n";
        }

        Debug.loge(new Exception(), i);
    }

    private String translateDeviceClass(int deviceClass){
        switch(deviceClass){
            case UsbConstants.USB_CLASS_APP_SPEC:
                return "Application specific USB class";
            case UsbConstants.USB_CLASS_AUDIO:
                return "USB class for audio devices";
            case UsbConstants.USB_CLASS_CDC_DATA:
                return "USB class for CDC devices (communications device class)";
            case UsbConstants.USB_CLASS_COMM:
                return "USB class for communication devices";
            case UsbConstants.USB_CLASS_CONTENT_SEC:
                return "USB class for content security devices";
            case UsbConstants.USB_CLASS_CSCID:
                return "USB class for content smart card devices";
            case UsbConstants.USB_CLASS_HID:
                return "USB class for human interface devices (for example, mice and keyboards)";
            case UsbConstants.USB_CLASS_HUB:
                return "USB class for USB hubs";
            case UsbConstants.USB_CLASS_MASS_STORAGE:
                return "USB class for mass storage devices";
            case UsbConstants.USB_CLASS_MISC:
                return "USB class for wireless miscellaneous devices";
            case UsbConstants.USB_CLASS_PER_INTERFACE:
                return "USB class indicating that the class is determined on a per-interface basis";
            case UsbConstants.USB_CLASS_PHYSICA:
                return "USB class for physical devices";
            case UsbConstants.USB_CLASS_PRINTER:
                return "USB class for printers";
            case UsbConstants.USB_CLASS_STILL_IMAGE:
                return "USB class for still image devices (digital cameras)";
            case UsbConstants.USB_CLASS_VENDOR_SPEC:
                return "Vendor specific USB class";
            case UsbConstants.USB_CLASS_VIDEO:
                return "USB class for video devices";
            case UsbConstants.USB_CLASS_WIRELESS_CONTROLLER:
                return "USB class for wireless controller devices";
            default: return "Unknown USB class!";

        }
    }

    // GUI Protocol
    private int makePacket(byte cmd, byte type, byte systype, byte[] aTarget, byte[] aSource, int nLen){
        int i, nCnt = 0;
        int length = 0;
        short sCrc16 =0;
//        byte[] crc16 = new byte[2];

        aTarget[nCnt++] = (byte)Variables.Proto_START_PACKET;
        length = nLen + 7;

        aTarget[nCnt++] = (byte)(length >> 8);
        aTarget[nCnt++] = (byte)(length);

        aTarget[nCnt++] = 0x00; // sourceId;
        aTarget[nCnt++] = (byte) Variables.PROTO_DEST_ID; //0x10; // destinationId;
//        Debug.loge(new Exception(), "=dhjung=======> PROTO_DEST_ID : " + String.format("%02X",Variables.PROTO_DEST_ID));

        aTarget[nCnt++] = systype;
        aTarget[nCnt++] = cmd;
        aTarget[nCnt++] = type;

        if(nLen != 0) System.arraycopy(aSource, 0, aTarget, nCnt, nLen);
        nCnt += nLen;

        sCrc16 = Crc16.fn_makeCRC16(aTarget, nLen+7);
        aTarget[nCnt++] = (byte)(sCrc16 >> 8);
        aTarget[nCnt++] = (byte)sCrc16;
//        Debug.loge(new Exception(), "sCrc16 : " + String.format("%04x ",sCrc16&0xFFFF));

        nCnt = 0;
        aSource[nCnt++] = (byte)Variables.Proto_START_PACKET;

        for(i=1; i<=nLen+9; i++){

            switch(aTarget[i]) {
                case (byte)Variables.Proto_START_PACKET:
                case (byte)Variables.Proto_ESCAPE_MASK:
                case (byte)Variables.Proto_END_PACKET:
                    aSource[nCnt++] = (byte)Variables.Proto_ESCAPE_MASK;
                    aSource[nCnt++] = (byte)(aTarget[i]^Variables.Proto_EXCEPTION_MASK);
                    break;
                default:
                    aSource[nCnt++] = aTarget[i];
                    break;
            }
        }
//        Debug.logw(new Exception(), "aSource : " + HexDump.toHexString(aSource) + ", nCnt: " + nCnt + "\n");
        System.arraycopy(aSource, 0, aTarget, 0, nCnt);
//        Debug.logw(new Exception(), "aTarget : " + HexDump.toHexString(aTarget));

        aTarget[nCnt++] = (byte)Variables.Proto_END_PACKET;

        return nCnt;
    }

/*    // Check sum 방식
    private int makePacket(byte cmd, byte type, byte systype, byte[] aTarget, byte[] aSource, int nLen){
        byte checkSum = 0;
        int i, nCnt = 0;

        aTarget[nCnt++] = (byte)Variables.Proto_START_PACKET;

        int length = 6;

        if (nLen > 0)
            length += nLen;

        aTarget[nCnt++] = (byte)(length << 8);
        aTarget[nCnt++] = (byte)(length);

        aTarget[nCnt++] = 0x00; // sourceId;
        checkSum += 0x00;

        aTarget[nCnt++] = (byte) Variables.PROTO_DEST_ID; //0x10; // destinationId;
        checkSum += (byte) Variables.PROTO_DEST_ID; //0x10;

        aTarget[nCnt++] = systype;
        checkSum += systype;

        aTarget[nCnt++] = cmd;
        checkSum += cmd;

        aTarget[nCnt++] = type;
        checkSum += type;


        for(i=0; i<nLen; i++){
            checkSum += aSource[i];
            //Debug.logd(new Exception(),"i: " + i + " C: " + HexDump.toHexString(checkSum) + " V: " + HexDump.toHexString(aSource[i]));

            switch(aSource[i]) {
                case (byte)Variables.Proto_ESCAPE_MASK:
                case (byte)Variables.Proto_START_PACKET:
                case (byte)Variables.Proto_END_PACKET:
                    aTarget[nCnt++] = (byte)Variables.Proto_ESCAPE_MASK;
                    aTarget[nCnt++] = (byte)(aSource[i]^Variables.Proto_EXCEPTION_MASK);
                    break;
                default:
                    aTarget[nCnt++] = aSource[i];
                    break;
            }
        }

        if(aSource != null){
            switch(checkSum){
                case (byte)Variables.Proto_ESCAPE_MASK:
                case (byte)Variables.Proto_START_PACKET:
                case (byte)Variables.Proto_END_PACKET:
                    aTarget[nCnt++] = (byte)Variables.Proto_ESCAPE_MASK;
                    aTarget[nCnt++] = (byte)(checkSum^Variables.Proto_EXCEPTION_MASK);
                    break;
                default:
                    aTarget[nCnt++] = checkSum;
                    break;
            }
        }else{
            aTarget[nCnt++] = checkSum;
        }

        aTarget[nCnt++] = (byte)Variables.Proto_END_PACKET;

        return nCnt;
    }
*/
    private void dataTransmit(){
        int nCnt = 0;
        byte msgType;
        //byte sysType = (byte)0x84;
        byte sysType = (byte)Variables.PROTO_SYSTEM_TYPE;
        byte[] txDataBuf = new byte[Variables.DATA_BUFFER_SIZE];
        byte[] txDataMakeBuf = new byte[Variables.DATA_BUFFER_SIZE];
        byte[] txData = null;

        REFRESH_TIMEOUT_MILLIS = 250;

        // Download Start
        if(Variables.Flag_Action_Download == 1){
            Variables.Flag_Action_Download = 0;
            Variables.CmdState = Variables.Cmd_DL_START;
            Variables.Dl_ExpPacketNum = 0;
            Variables.FlagBitCheck = 0;
            Variables.SendRetryCnt = 0;
        }
        // dataTransmit() 함수와 MainActivity의 parseRcvData() 함수가 비동기로 동작하기 때문에
        // 설정값이 내려가기 전에 데이터를 업데이트하면서 설정이 안되는 현상. 원인은 상태, 설정 변수를 같이 쓰기 때문.
        // 임시 방편으로 직전 CmdState 상태를 확인하고 설정값을 내리도록 함
        // Setting
        if((mFrameInfo != 0) && (Variables.CmdState != Variables.Cmd_MODEM_STATUS_KDDI_QN)) {
            Variables.CmdState = Variables.Cmd_SETTING_KDDI_QN;
            Debug.logi(new Exception(),"=dhjung=======> mFrameInfo: " + mFrameInfo + ", CmdState: Cmd_SETTING_KDDI_QN ");
            REFRESH_TIMEOUT_MILLIS = 3000;
            Variables.FlagBitCheck = 0;
            Variables.SendRetryCnt = 0;
            if(Variables.band == 4){    // 혹시 모를 버그
                Variables.band = 0;
                Variables.PROTO_DEST_ID = 0xC1;
            }
        }
        // Modem Setting
        if((mModemFrameInfo != 0) && (Variables.CmdState != Variables.Cmd_STATUS_KDDI_QN)){
            Variables.CmdState = Variables.Cmd_MODEM_SETTING_KDDI_QN;
            Debug.logi(new Exception(),"=dhjung=======> mModemFrameInfo: " + mModemFrameInfo + ", CmdState: Cmd_MODEM_SETTING_KDDI_QN ");
            REFRESH_TIMEOUT_MILLIS = 3000;
            Variables.FlagBitCheck = 0;
            Variables.SendRetryCnt = 0;
        }
        // Tsync Setting
//        if((mTsyncFrameInfo != 0) && (Variables.CmdState != Variables.Cmd_STATUS)){
//            Variables.CmdState = Variables.Cmd_TSYNC_CONTROL_CROSSWORKS;
////            Debug.logi(new Exception(),"=dhjung=======> CmdState: Cmd_TSYNC_CONTROL_CROSSWORKS ");
//            Variables.FlagBitCheck = 0;
//            Variables.SendRetryCnt = 0;
//        }
        // DSP Control
        if((mDspFrameInfo != 0) && (Variables.CmdState != Variables.Cmd_STATUS_KDDI_QN)){
            Variables.CmdState = Variables.Cmd_DSP_CONTROL;
            Variables.FlagBitCheck = 0;
            Variables.SendRetryCnt = 0;
        }

        // Retry Check
        if(Variables.SendRetryCnt == Variables.SEND_RETRY_CNT) {
            if(Variables.CmdState == Variables.Cmd_DL_PROGRESS){
                firmwareDownloadFail();
            }

            if(Variables.CmdState == Variables.Cmd_STATUS_KDDI_QN) Variables.CmdState = Variables.Cmd_SETTING_KDDI_QN;
            else                                                      Variables.CmdState = Variables.Cmd_STATUS_KDDI_QN;
            Variables.SendRetryCnt = 0;

            Variables.TotalSendRetryCnt++;
            if(Variables.TotalSendRetryCnt == Variables.Total_SEND_RETRY_CNT) {
                Debug.loge(new Exception(),"=dhjung=======> TotalSendRetryCnt: " + Variables.TotalSendRetryCnt + " Message Refresh");
                Variables.TotalSendRetryCnt = 0;
                stopServiceToMainActivity();
            }
        }

        switch(Variables.CmdState){
            case Variables.Cmd_STATUS_KDDI_QN:
//                Debug.logi(new Exception(),"=dhjung=======> Cmd_STATUS_KDDI_QN, FlagBitCheck : " + Variables.FlagBitCheck);
                if(Variables.FlagBitCheck == Variables.Flag_SEND_CHK) {
                    Variables.FlagBitCheck = 0;
                    Variables.SendRetryCnt = 0;
                    Variables.TotalSendRetryCnt = 0;
//                    Debug.logi(new Exception(),"=dhjung=======> Cmd_STATUS_KDDI_QN Send Check!");

                    if(Variables.bandStruct[0].sets.tFreqSelect10M15M == 0) {   // 800MHz LTE+CDMA
                        if(Variables.band == 0) {
                            Variables.band = 4;
                            Variables.PROTO_DEST_ID = 0xC5;
                            Variables.CmdState = Variables.Cmd_STATUS_KDDI_QN;
                        }else if(Variables.band == 4){
                            Variables.band = 0;
                            Variables.PROTO_DEST_ID = 0xC1;
                            Variables.CmdState = Variables.Cmd_SETTING_KDDI_QN;
                        }else{
                            Variables.CmdState = Variables.Cmd_SETTING_KDDI_QN;
                        }
                    }
                    else {
                        Variables.CmdState = Variables.Cmd_SETTING_KDDI_QN;
                    }
                    return;
                }else{
//                    Debug.logi(new Exception(),"=dhjung=======> Cmd_STATUS_KDDI_QN Send ==> ");
                    msgType = (Variables.Type_REPORT | Variables.Type_REQUEST);
                    nCnt = makePacket((byte)Variables.CmdState, msgType, sysType, txDataBuf, txDataMakeBuf, 0);
                }
                break;

            case Variables.Cmd_SETTING_KDDI_QN:
//                Debug.logi(new Exception(),"=dhjung=======> Cmd_SETTING_KDDI_QN, FlagBitCheck : " + Variables.FlagBitCheck);
                if(Variables.band == 4){    // 혹시 모를 버그
                    Variables.band = 0;
                    Variables.PROTO_DEST_ID = 0xC1;
                }

                if(Variables.FlagBitCheck == Variables.Flag_SEND_CHK) {
//                    Debug.logi(new Exception(),"=dhjung=======> Cmd_SETTING_KDDI_QN Send Check!");
                    Variables.FlagBitCheck = 0;
                    Variables.SendRetryCnt = 0;
                    Variables.TotalSendRetryCnt = 0;
                    Variables.CmdState = Variables.Cmd_MODEM_STATUS_KDDI_QN;
                    return;
                }else{
                    if(mFrameInfo != 0) {
                        msgType = (Variables.Type_SET | Variables.Type_REQUEST);
                        REFRESH_TIMEOUT_MILLIS = 3000;
                    }else{
                        msgType = (Variables.Type_REPORT | Variables.Type_REQUEST);
                    }
//                    Debug.logi(new Exception(),"=dhjung=======> Cmd_SETTING_KDDI_QN Send ==> msgType: "  + String.format("%02X",msgType));
                    nCnt = Make_SettingDataToRepeater(txDataMakeBuf);
                    nCnt = makePacket((byte)Variables.CmdState, msgType, sysType, txDataBuf, txDataMakeBuf, nCnt);
                }
                break;
/*
            case Variables.Cmd_DSP_CONTROL:
                if(Variables.FlagBitCheck == Variables.Flag_SEND_CHK) {
                    Variables.FlagBitCheck = 0;
                    Variables.SendRetryCnt = 0;
                    Variables.CmdState = Variables.Cmd_STATUS;
                    Variables.TotalSendRetryCnt = 0;
                    return;
                }else {
                    msgType = (Variables.Type_SET | Variables.Type_REQUEST);
                    nCnt = Make_DspCtrlToRepeater(txDataMakeBuf);
                    nCnt = makePacket((byte) Variables.CmdState, msgType, sysType, txDataBuf, txDataMakeBuf, nCnt);
                }
                break;
*/
            case Variables.Cmd_DL_START:
                if(Variables.FlagBitCheck == Variables.Flag_SEND_CHK) {
                    Variables.FlagBitCheck = 0;
                    Variables.SendRetryCnt = 0;
                    Variables.CmdState = Variables.Cmd_DL_PROGRESS;
                    Variables.TotalSendRetryCnt = 0;
                    return;
                }else{
                    //String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "stm32rtos_DCM_ICS_small.bin";

/*
                    String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    File dir = new File(sdpath + "/dir");
                    dir.mkdir();

                    //save
                    File file = new File(sdpath + "/dir/test.txt");
                    try{
                        FileOutputStream fos = new FileOutputStream(file);
                        String str = "This file exists in SD card";
                        fos.write(str.getBytes());
                        fos.close();
                    }catch(Exception e){;}
                    // load
                    try{
                        FileInputStream fis = new FileInputStream(sdpath + "/dir/test.txt");
                        byte[] data = new byte[fis.available()];
                        while(fis.read(data) != -1){;}
                        fis.close();
                    }catch(Exception e){;}
*/
                    Variables.band = 0;
                    Variables.PROTO_DEST_ID = 0xC1;

                    REFRESH_TIMEOUT_MILLIS = 3000;

                    try{
                        byte[] FileSizeBuf       = new byte[4];
                        byte[] OnePacketSizeBuf  = new byte[2];
                        byte[] TotalPacketCntBuf = new byte[2];
                        byte[] SendDataBuf       = new byte[20];

                        //FileInputStream fis = new FileInputStream(sdpath);
                        fis  = new FileInputStream(sdpath);
                        Variables.Dl_FileSize = fis.available();
                        dlFWDataBuf = new byte[Variables.Dl_FileSize];
                        while (fis.read(dlFWDataBuf) != -1);
                        fis.close();

                        Variables.Dl_OnePacketSize = Variables.DATA_PACKET_SIZE;
                        Variables.Dl_TotalPacketCnt = (Variables.Dl_FileSize / Variables.DATA_PACKET_SIZE);
                        Variables.Dl_lastPacketLen = (Variables.Dl_FileSize % Variables.DATA_PACKET_SIZE);
                        if(Variables.Dl_lastPacketLen != 0) Variables.Dl_TotalPacketCnt += 1;

                        msgType = (Variables.Type_SET | Variables.Type_REQUEST);

                        for(int i=0; i<4; i++){ // Data Swap
                            FileSizeBuf[i] = (byte)(Variables.Dl_FileSize >> (24 - (i*8)));
                            if(i < 2){
                                OnePacketSizeBuf[i]  = (byte)(Variables.Dl_OnePacketSize  >> (8 - (i*8)));
                                TotalPacketCntBuf[i] = (byte)(Variables.Dl_TotalPacketCnt >> (8 - (i*8)));
                            }
                        }
                        System.arraycopy(FileSizeBuf,       0, SendDataBuf, 0, 4);
                        System.arraycopy(OnePacketSizeBuf,  0, SendDataBuf, 4, 2);
                        System.arraycopy(TotalPacketCntBuf, 0, SendDataBuf, 6, 2);

                        Debug.logd(new Exception(),"file size: " + Variables.Dl_FileSize + "(" + HexDump.toHexString(FileSizeBuf) + ")");
                        Debug.logd(new Exception(),"total packet cnt: " + Variables.Dl_TotalPacketCnt + "(" + HexDump.toHexString(TotalPacketCntBuf) + ")");
                        Debug.logd(new Exception(),"last packet len: " + Variables.Dl_lastPacketLen);
                        Debug.logd(new Exception(),"SendDataBuf: " + HexDump.toHexString(SendDataBuf));

                        nCnt = makePacket((byte)Variables.CmdState, msgType, sysType, txDataBuf, SendDataBuf, 8);
                    }catch(Exception e){
                        Debug.logd(new Exception(),"File Exception: " + e);
                    }
                }
                break;

            case Variables.Cmd_DL_PROGRESS:
                //Debug.logd(new Exception(),"Dl_ExpPacketNum: " + Variables.Dl_ExpPacketNum + ", FlagBitCheck: " + Variables.FlagBitCheck);
                if(((Variables.Dl_ExpPacketNum == 1) && (Variables.SendRetryCnt == 0)) ||
                   ((Variables.Dl_ExpPacketNum > 1)  && (Variables.FlagBitCheck == Variables.Flag_SEND_CHK))) {

                    byte[] ExpPacketNumBuf = new byte[2];

                    if(Variables.Dl_ExpPacketNum == Variables.Dl_TotalPacketCnt) REFRESH_TIMEOUT_MILLIS = 3000;
                    else                                                         REFRESH_TIMEOUT_MILLIS = 200;

                    Variables.FlagBitCheck = 0;
                    Variables.SendRetryCnt = 0;
                    Variables.TotalSendRetryCnt = 0;

                    if(Variables.Dl_ExpPacketNum > Variables.Dl_TotalPacketCnt){
                        Variables.CmdState = Variables.Cmd_DL_END;
                        return;
                    }

                    FwDownloadPercentage = (Variables.Dl_ExpPacketNum * 100) / Variables.Dl_TotalPacketCnt;
                    //Debug.loge(new Exception(), "C: " + FwDownloadPercentage + " %");
                    firmwareDownloadPercentage(FwDownloadPercentage);

                    for(int i=0; i<2; i++){ // Data Swap
                        ExpPacketNumBuf[i] = (byte)(Variables.Dl_ExpPacketNum >> (8 - (i*8)));
                    }
                    System.arraycopy(ExpPacketNumBuf, 0, dlRetrySendBuf, 0, 2);
                    if(Variables.Dl_ExpPacketNum == Variables.Dl_TotalPacketCnt) dlSendCntBuf = Variables.Dl_lastPacketLen;
                    else                                                         dlSendCntBuf = Variables.DATA_PACKET_SIZE;
                    System.arraycopy(dlFWDataBuf, ((Variables.Dl_ExpPacketNum-1) * Variables.DATA_PACKET_SIZE), dlRetrySendBuf, 2, dlSendCntBuf);
                    dlSendCntBuf += 2;
                }else if(Variables.SendRetryCnt > 0){
                    REFRESH_TIMEOUT_MILLIS = 1000;
                }
                msgType = (Variables.Type_SET | Variables.Type_REQUEST);
                nCnt = makePacket((byte)Variables.CmdState, msgType, sysType, txDataBuf, dlRetrySendBuf, dlSendCntBuf);
                break;

            case Variables.Cmd_DL_END:
                if(Variables.FlagBitCheck == Variables.Flag_SEND_CHK) {
                    Variables.FlagBitCheck = 0;
                    Variables.SendRetryCnt = 0;
                    Variables.CmdState = Variables.Cmd_STATUS_KDDI_QN;
                    Variables.TotalSendRetryCnt = 0;
                    return;
                }else{
                    byte[] LastPacketNumBuf = new byte[2];
                    byte[] LastPacketLenBuf = new byte[2];
                    byte[] SendDataBuf      = new byte[20];

                    Variables.Dl_LastPacketNum = Variables.Dl_ExpPacketNum - 1;

                    msgType = (Variables.Type_SET | Variables.Type_REQUEST);

                    for(int i=0; i<2; i++){ // Data Swap
                        LastPacketNumBuf[i] = (byte)(Variables.Dl_LastPacketNum >> (8 - (i*8)));
                        LastPacketLenBuf[i] = (byte)(Variables.Dl_lastPacketLen >> (8 - (i*8)));
                    }
                    System.arraycopy(LastPacketNumBuf, 0, SendDataBuf, 0, 2);
                    System.arraycopy(LastPacketLenBuf, 0, SendDataBuf, 2, 2);

                    nCnt = makePacket((byte)Variables.CmdState, msgType, sysType, txDataBuf, SendDataBuf, 4);
                }
                break;

            case Variables.Cmd_MODEM_STATUS_KDDI_QN:
//                Debug.logi(new Exception(),"=dhjung=======> Cmd_MODEM_STATUS_KDDI_QN, FlagBitCheck : " + Variables.FlagBitCheck);
                if(Variables.FlagBitCheck == Variables.Flag_SEND_CHK) {
//                    Debug.logi(new Exception(),"=dhjung=======> Cmd_MODEM_STATUS_KDDI_QN Send Check!");
                    Variables.FlagBitCheck = 0;
                    Variables.SendRetryCnt = 0;
                    Variables.CmdState = Variables.Cmd_STATUS_KDDI_QN;
                    Variables.TotalSendRetryCnt = 0;
                    return;
                }else {
//                    Debug.logi(new Exception(),"=dhjung=======> Cmd_MODEM_STATUS_KDDI_QN Send ==> ");
                    msgType = (Variables.Type_REPORT | Variables.Type_REQUEST);
                    nCnt = makePacket((byte) Variables.CmdState, msgType, sysType, txDataBuf, txDataMakeBuf, 0);
                }
                break;

            case Variables.Cmd_MODEM_SETTING_KDDI_QN:
                if(Variables.FlagBitCheck == Variables.Flag_SEND_CHK) {
                    Variables.FlagBitCheck = 0;
                    Variables.SendRetryCnt = 0;
                    Variables.CmdState = Variables.Cmd_STATUS_KDDI_QN;
                    Variables.TotalSendRetryCnt = 0;
                    return;
                }else {
                    if (mModemFrameInfo != 0) {
//                        Debug.loge(new Exception(), "Knetz mModemFrameInfo: " + mModemFrameInfo);
                        msgType = (Variables.Type_SET | Variables.Type_REQUEST);
                        REFRESH_TIMEOUT_MILLIS = 3000;

                        nCnt = Make_ModemDataToRepeaterKddiQn(txDataMakeBuf);
                        nCnt = makePacket((byte) Variables.CmdState, msgType, sysType, txDataBuf, txDataMakeBuf, nCnt);
                    }
                }
                break;
/*
            case Variables.Cmd_RECEIVER_STATUS:
                if(Variables.FlagBitCheck == Variables.Flag_SEND_CHK) {
                    Variables.FlagBitCheck = 0;
                    Variables.SendRetryCnt = 0;
                    Variables.CmdState = Variables.Cmd_RECEIVER_SETTING;
                    Variables.TotalSendRetryCnt = 0;
                    return;
                }else {
                    msgType = (Variables.Type_REPORT | Variables.Type_REQUEST);
                    nCnt = makePacket((byte) Variables.CmdState, msgType, sysType, txDataBuf, txDataMakeBuf, 0);
                }
                break;

            case Variables.Cmd_RECEIVER_SETTING:
                if(Variables.FlagBitCheck == Variables.Flag_SEND_CHK) {
                    Variables.FlagBitCheck = 0;
                    Variables.SendRetryCnt = 0;
                    Variables.CmdState = Variables.Cmd_STATUS;
                    Variables.TotalSendRetryCnt = 0;
                    return;
                }else{
                    if(mReceiverFrameInfo != 0) {
                        Debug.loge(new Exception(),"Knetz mReceiver != 0");
                        msgType = (Variables.Type_SET | Variables.Type_REQUEST);
                        REFRESH_TIMEOUT_MILLIS = 3000;
                        Variables.receiverSendFlag = true;
                    }else{
                        msgType = (Variables.Type_REPORT | Variables.Type_REQUEST);
                    }
                    nCnt = Make_ReceiverDataToRepeater(txDataMakeBuf);
                    nCnt = makePacket((byte)Variables.CmdState, msgType, sysType, txDataBuf, txDataMakeBuf, nCnt);
                }
                break;
*/
//
//            case Variables.Cmd_TSYNC_STATUS_CROSSWORKS:
//                if(Variables.FlagBitCheck == Variables.Flag_SEND_CHK) {
//                    Variables.FlagBitCheck = 0;
//                    Variables.SendRetryCnt = 0;
//                    Variables.CmdState = Variables.Cmd_STATUS_KDDI_QN;
//                    Variables.TotalSendRetryCnt = 0;
//                    return;
//                }else {
//                    msgType = (Variables.Type_REPORT | Variables.Type_REQUEST);
//                    nCnt = makePacket((byte) Variables.CmdState, msgType, sysType, txDataBuf, txDataMakeBuf, 0);
//                }
//                break;
//
//            case Variables.Cmd_TSYNC_CONTROL_CROSSWORKS:
////                Debug.logi(new Exception(),"=dhjung=======> Cmd_TSYNC_CONTROL_CROSSWORKS, FlagBitCheck(0x03) : " + Variables.FlagBitCheck + " " + mTsyncFrameInfo);
//                if(Variables.FlagBitCheck == Variables.Flag_SEND_CHK) {
//                    Variables.FlagBitCheck = 0;
//                    Variables.SendRetryCnt = 0;
//                    Variables.CmdState = Variables.Cmd_STATUS_KDDI_QN;
//                    Variables.TotalSendRetryCnt = 0;
//                    return;
//                }else{
//                    if(mTsyncFrameInfo != 0) {
//                        msgType = (Variables.Type_SET | Variables.Type_REQUEST);
//                        REFRESH_TIMEOUT_MILLIS = 3000;
//                    }else{
//                        msgType = (Variables.Type_REPORT | Variables.Type_REQUEST);
//                    }
//                    nCnt = Make_TsyncDataToRepeater(txDataMakeBuf);
//                    nCnt = makePacket((byte)Variables.CmdState, msgType, sysType, txDataBuf, txDataMakeBuf, nCnt);
//                }
//                break;

            case Variables.Cmd_GUI_SETTING:
                Debug.loge(new Exception(), "===> Gui Setting Start: " + Variables.FlagBitCheck);
                if(Variables.FlagBitCheck == Variables.Flag_SEND_CHK) {
                    Variables.FlagBitCheck = 0;
                    Variables.SendRetryCnt = 0;
                    Variables.CmdState = Variables.Cmd_STATUS_KDDI_QN;
                    Variables.TotalSendRetryCnt = 0;
                    return;
                }else{
                    msgType = (Variables.Type_REPORT | Variables.Type_REQUEST);
                    nCnt = makePacket((byte)Variables.CmdState, msgType, sysType, txDataBuf, txDataMakeBuf, 0);
                }
                break;

            default:
                break;
        }

        Variables.FlagBitCheck |= Variables.Flag_SEND_REQ;
        if(Variables.SendRetryCnt >= 1) {
            Debug.loge(new Exception(), "=dhjung=======> [" + Variables.CmdState + "] retry: " + Variables.SendRetryCnt + "(" + Variables.TotalSendRetryCnt + ")");
            REFRESH_TIMEOUT_MILLIS = 1000;
        }
        Variables.SendRetryCnt++;
        Variables.dataSendCnt++;

        txData = new byte[nCnt];
        System.arraycopy(txDataBuf, 0, txData, 0, nCnt);
        Debug.logw(new Exception(), "=dhjung=======> Variables.CmdState : "+Variables.CmdState +" tx data: " + nCnt + "\n" + HexDump.toHexString(txData));
        if (mUsbOrBluetooth) {
            mSerialIoManager.writeAsync(txData);
        }else{
            Debug.loge(new Exception(),"=dhjung=======> Knetz txData : "+ByteUtil.toHexString(txData));
            sendMessage(txData);
        }
    }

    private int Make_SettingDataToRepeater(byte[] data){
        int nCnt = 0;
        byte[] frameInfo = {0,0};
        byte dataBuf = 0;

        data[nCnt++] = frameInfo[0];
        data[nCnt++] = frameInfo[1] = mFrameInfo;
        //Debug.loge(new Exception(), "Make FrameInfo: " + frameInfo[1]);

        switch(frameInfo[1]){
            case 0x01: // System
                data[nCnt++] = Variables.bandStruct[Variables.band].sets.tTempUpper;
                dataBuf |= (Variables.bandStruct[Variables.band].sets.tAutoShutdown & 0x01);
                dataBuf |= (Variables.bandStruct[Variables.band].sets.tAutoRecovery & 0x01) << 1;
                dataBuf |= (Variables.bandStruct[Variables.band].sets.tSleepMode & 0x01) << 2;
                dataBuf |= (Variables.bandStruct[Variables.band].sets.tILC & 0x01) << 3;
                data[nCnt++] = dataBuf;
                data[nCnt++] = Variables.bandStruct[Variables.band].sets.tCellSearch;
                dataBuf |= (Variables.bandStruct[Variables.band].sets.tFreqSelectAutoManual & 0x01);
                dataBuf |= (Variables.bandStruct[Variables.band].sets.tFreqSelect10M15M & 0x01) << 1;
                data[nCnt++] = dataBuf;
                Arrays.fill(data, nCnt, nCnt + 12, (byte) 0);
                nCnt += 12;
//                Debug.logi(new Exception(),"=dhjung=======> Make_SettingDataToRepeater 0x01 ");
                break;
            case 0x02: // DL DSP&RF
                data[nCnt++] = Variables.bandStruct[Variables.band].pathSets[0].tAtten;
                data[nCnt++] = Variables.bandStruct[Variables.band].pathSets[0].tIcsMode;
                data[nCnt++] = Variables.bandStruct[Variables.band].pathSets[0].tSystemGain;
                data[nCnt++] = Variables.bandStruct[Variables.band].pathSets[0].tAgcOffset;
                data[nCnt++] = (byte)((Variables.bandStruct[Variables.band].pathSets[0].tOutputUpper >> 8)&0xFF);
                data[nCnt++] = (byte)(Variables.bandStruct[Variables.band].pathSets[0].tOutputUpper & 0xFF);
                data[nCnt++] = (byte)((Variables.bandStruct[Variables.band].pathSets[0].tOutputLower >> 8)&0xFF);
                data[nCnt++] = (byte)(Variables.bandStruct[Variables.band].pathSets[0].tOutputLower & 0xFF);
                data[nCnt++] = (byte)((Variables.bandStruct[Variables.band].pathSets[0].tInputUpper >> 8)&0xFF);
                data[nCnt++] = (byte)(Variables.bandStruct[Variables.band].pathSets[0].tInputUpper & 0xFF);
                data[nCnt++] = (byte)((Variables.bandStruct[Variables.band].pathSets[0].tInputLower >> 8)&0xFF);
                data[nCnt++] = (byte)(Variables.bandStruct[Variables.band].pathSets[0].tInputLower & 0xFF);
                data[nCnt++] = Variables.bandStruct[Variables.band].pathSets[0].tIcsOff;
                Arrays.fill(data, nCnt, nCnt + 11, (byte)0);
                nCnt += 11;
//                Debug.logi(new Exception(),"=dhjung=======> Make_SettingDataToRepeater 0x02 ");
                break;
            case 0x04: // UL DSP&RF
                data[nCnt++] = Variables.bandStruct[Variables.band].pathSets[1].tAtten;
                data[nCnt++] = Variables.bandStruct[Variables.band].pathSets[1].tIcsMode;
                data[nCnt++] = Variables.bandStruct[Variables.band].pathSets[1].tSystemGain;
                data[nCnt++] = Variables.bandStruct[Variables.band].pathSets[1].tAgcOffset;
                data[nCnt++] = (byte)((Variables.bandStruct[Variables.band].pathSets[1].tOutputUpper >> 8)&0xFF);
                data[nCnt++] = (byte)(Variables.bandStruct[Variables.band].pathSets[1].tOutputUpper & 0xFF);
                data[nCnt++] = (byte)((Variables.bandStruct[Variables.band].pathSets[1].tInputUpper >> 8)&0xFF);
                data[nCnt++] = (byte)(Variables.bandStruct[Variables.band].pathSets[1].tInputUpper & 0xFF);
                data[nCnt++] = Variables.bandStruct[Variables.band].pathSets[1].tPathSleepLevel;
                data[nCnt++] = Variables.bandStruct[Variables.band].pathSets[1].tIcsOff;
                Arrays.fill(data, nCnt, nCnt + 14, (byte)0);
                nCnt += 14;
//                Debug.logi(new Exception(),"=dhjung=======> Make_SettingDataToRepeater 0x04 ");
                break;
            case 0x08: // Service FA
                data[nCnt++] = 0;
                data[nCnt++] = 0;
                data[nCnt++] = Variables.bandStruct[Variables.band].sets.tRfPathOn;
                Arrays.fill(data, nCnt, nCnt + 13, (byte)0);
                nCnt += 13;
                break;
            case 0x10:  // DL AMP
                data[nCnt++] = Variables.bandStruct[Variables.band].pathSets[0].tAmpOnOff;
                data[nCnt++]= Variables.bandStruct[Variables.band].pathSets[0].tAlcOnOff;
                Arrays.fill(data, nCnt, nCnt + 10, (byte)0);
                nCnt += 10;
//                Debug.logi(new Exception(),"=dhjung=======> Make_SettingDataToRepeater 0x10 ");
                break;
            case 0x20:  // UL AMP
                data[nCnt++] = Variables.bandStruct[Variables.band].pathSets[1].tAmpOnOff;
                data[nCnt++]= Variables.bandStruct[Variables.band].pathSets[1].tAlcOnOff;
                Arrays.fill(data, nCnt, nCnt + 10, (byte)0);
                nCnt += 10;
//                Debug.logi(new Exception(),"=dhjung=======> Make_SettingDataToRepeater 0x20 ");
                break;
            case 0x40:  // Common
                System.arraycopy(Variables.bandStruct[Variables.band].sets.tSerialNum, 0, data, nCnt, 20);
                nCnt += 20;
                System.arraycopy(Variables.bandStruct[Variables.band].sets.tModelName, 0, data, nCnt, 20);
                nCnt += 20;
                System.arraycopy(Variables.bandStruct[Variables.band].sets.tOperatorName, 0, data, nCnt, 20);
                nCnt += 20;
                System.arraycopy(Variables.bandStruct[Variables.band].sets.tSupplierName, 0, data, nCnt, 20);
                nCnt += 20;
                System.arraycopy(Variables.bandStruct[Variables.band].sets.tInstallAddr, 0, data, nCnt, 100);
                nCnt += 100;
                data[nCnt++] = Variables.bandStruct[Variables.band].sets.tPowerMode;
                data[nCnt++] = Variables.bandStruct[Variables.band].sets.tServiceBand;
                nCnt += 14;
                Debug.logi(new Exception(),"=dhjung=======> Make_SettingDataToRepeater 0x40 ");
                break;
            default:
                break;
        }

        return nCnt;
    }


    /*
    private int Make_ReceiverDataToRepeater(byte[] data){
        int nCnt = 0;
        byte frameInfo = mReceiverFrameInfo;

        data[nCnt++] = frameInfo;

        switch(frameInfo){
            case 0x01:
                data[nCnt++] = 0;
                data[nCnt++] = 1;
                nCnt++;     // Download On/Off
                nCnt++;     // Reset Cycle
                nCnt += 9;
                break;
            case 0x02:
                data[nCnt++] = 0;
                data[nCnt++] = 1;
                nCnt++;     // TxUpper
                nCnt++;     // TxLower
                nCnt++;     // RxUpper
                nCnt++;     // RxLower
                data[nCnt++] = Variables.bandStruct.rtPathLossRef;
                //Debug.loge(new Exception(), "Knetz Variables.mtPathLossRef : "+Variables.rtPathLossRef);
                nCnt += 5;
                break;
            case 0x04:
                data[nCnt++] = 0;
                data[nCnt++] = 1;
                nCnt++;     // Mode
                nCnt += 6;  // Select PLMN
                nCnt += 2;  // Select Channel
                nCnt += 6;
                break;
            case 0x08:
                data[nCnt++] = 0;
                data[nCnt++] = 1;
                nCnt++;     // Mode
                nCnt += 2;  // Select PSC
                nCnt++;     // RSCP Upper1
                nCnt++;     // RSCP Lower1
                nCnt++;     // PSC Ec/Io1 Lower
                nCnt += 6;
                break;
            default:
                break;
        }

        return nCnt;
    }

    private int Make_DspCtrlToRepeater(byte[] data){
        int nCnt = 0;
        byte frameInfo = mDspFrameInfo;

        switch(frameInfo){
            case 0x01:
                data[nCnt++] = 0x01;
                data[nCnt++] = 0;
                break;
            case 0x02:
                data[nCnt++] = 0x11;
                data[nCnt++] = 0;
                break;
            default:
                break;
        }

        return nCnt;
    }

*/

    private int Make_ModemDataToRepeaterKddiQn(byte[] data){
        int nCnt = 0, i;
        byte frameInfo = mModemFrameInfo;

        data[nCnt++] = frameInfo;

        switch(frameInfo){
            case 0x01:  // Remote
                System.arraycopy(Variables.modemStruct.modem.rLocalPhoneNum, 0, data, nCnt, 16);    // Local Phone Number
                nCnt += 16;
                System.arraycopy(Variables.modemStruct.modem.rRcsPhoneNum, 0, data, nCnt, 16);  // Host Phone Number
                nCnt += 16;
                System.arraycopy(Variables.modemStruct.modem.rPeriodicReport, 0, data, nCnt, 3);    // Heartbeat Time
                nCnt += 3;
                data[nCnt++] = Variables.modemStruct.modem.rAutoResetTime;
                nCnt += 16; // reserved
                break;
            case 0x02:  // SIM
                data[nCnt++] = Variables.modemStruct.modem.sLoopback;
                data[nCnt++] = Variables.modemStruct.modem.sPinLock;
                System.arraycopy(Variables.modemStruct.modem.sPinPassword, 0, data, nCnt, 4);   // Pin Lock Password
                nCnt += 4;
                nCnt += 10; // reserved
                break;
            case 0x04:  // Network
                System.arraycopy(Variables.modemStruct.modem.nApn, 0, data, nCnt, 30);    // APN
                nCnt += 30;
                System.arraycopy(Variables.modemStruct.modem.nUserId, 0, data, nCnt, 30);    // User ID
                nCnt += 30;
                System.arraycopy(Variables.modemStruct.modem.nPassword, 0, data, nCnt, 20);    // Password
                nCnt += 20;
                nCnt += 10; // reserved
                break;
            case 0x08:  // EMS
                for(i=0; i<10; i++) {
                    System.arraycopy(Variables.modemStruct.modem.eRcsIpAddr[i], 0, data, nCnt, 50);    // EMS IP
                    nCnt += 50;
                }
                data[nCnt++] = (byte)((Variables.modemStruct.modem.eRcsPort>>8)&0xFF);
                data[nCnt++] = (byte)(Variables.modemStruct.modem.eRcsPort&0xFF);
                nCnt += 10; // reserved
                break;
            default:
                break;
        }

        return nCnt;
    }

    private int Make_TsyncDataToRepeater(byte[] data){
        int nCnt = 0;
        byte[] frameInfo = {0,0};
        byte dataBuf = 0;

        data[nCnt++] = frameInfo[0]= (byte)((mTsyncFrameInfo>>8)&0xFF);
        data[nCnt++] = frameInfo[1] = (byte)(mTsyncFrameInfo&0xFF);

        if((mTsyncFrameInfo & 0x8000) == 0x8000) {
            // reset
        }

        if((mTsyncFrameInfo & 0x1) == 0x1) {
//            Debug.loge(new Exception(),"dhjung --> [" + nCnt + "]" + "tTddMode: " + Variables.bandStruct[Variables.band].tsync.tTddMode);
            data[nCnt++] = Variables.bandStruct[Variables.band].tsync.tTddMode;
            data[nCnt++] = (byte)((Variables.bandStruct[Variables.band].tsync.tDlOffTime >> 8)&0xFF);
            data[nCnt++] = (byte)(Variables.bandStruct[Variables.band].tsync.tDlOffTime & 0xFF);
            data[nCnt++] = (byte)((Variables.bandStruct[Variables.band].tsync.tUlOffTime >> 8)&0xFF);
            data[nCnt++] = (byte)(Variables.bandStruct[Variables.band].tsync.tUlOffTime & 0xFF);
            data[nCnt++] = (byte)((Variables.bandStruct[Variables.band].tsync.tDlOnTime >> 8)&0xFF);
            data[nCnt++] = (byte)(Variables.bandStruct[Variables.band].tsync.tDlOnTime & 0xFF);
            data[nCnt++] = (byte)((Variables.bandStruct[Variables.band].tsync.tUlOnTime >> 8)&0xFF);
            data[nCnt++] = (byte)(Variables.bandStruct[Variables.band].tsync.tUlOnTime & 0xFF);
//            Debug.loge(new Exception(),"dhjung --> [" + nCnt + "]" + "tDlUlConfigure: " + Variables.bandStruct[Variables.band].tsync.tDlUlConfigure);
            data[nCnt++] = Variables.bandStruct[Variables.band].tsync.tDlUlConfigure;
            dataBuf |= (Variables.bandStruct[Variables.band].tsync.tTsync1OutSel&0x01);
            dataBuf |= (Variables.bandStruct[Variables.band].tsync.tTsync2OutSel&0x01) << 1;
            dataBuf |= (Variables.bandStruct[Variables.band].tsync.tTsync3OutSel&0x01) << 2;
            dataBuf |= (Variables.bandStruct[Variables.band].tsync.tTsync4OutSel&0x01) << 3;
            data[nCnt++] = dataBuf;

            Arrays.fill(data, nCnt, nCnt + 4, (byte) 0);
            nCnt += 4;

            data[nCnt++] = Variables.bandStruct[Variables.band].tsync.tBandSel;
            data[nCnt++] = Variables.bandStruct[Variables.band].tsync.tBandWidth;
            data[nCnt++] = Variables.bandStruct[Variables.band].tsync.tSSBsearchMode;

//            Debug.logi(new Exception(),"=dhjung=======> Make_TsyncDataToRepeater 0x01 ");
        }

        return nCnt;
    }

    private String Parse_Modelname(byte[] data) {
        //int nCnt = 4;
        int nCnt = Variables.Protocol_MESSAGE_START;
        if (data != null) {
            if (data.length > 0) {
                Debug.loge(new Exception(), "KnetzH START " + HexDump.toHexString(data));
                byte[] lo_name = new byte[20];

                //Debug.loge(new Exception(),"[" + nCnt + "]" + "gSystem: " + data[nCnt]);
                nCnt += 4;
                //Debug.loge(new Exception(),"[" + nCnt + "]" + "gPsu: " + data[nCnt]);
                nCnt += 2;
                //Debug.loge(new Exception(),"[" + nCnt + "]" + "gDlFA: " + data[nCnt]);
                nCnt += 3;
                //Debug.loge(new Exception(),"[" + nCnt + "]" + "gUlFA: " + data[nCnt]);
                nCnt += 3;
                //Debug.loge(new Exception(),"[" + nCnt + "]" + "gDlDspRf: " + data[nCnt]);
                nCnt += 3;
                //Debug.loge(new Exception(),"[" + nCnt + "]" + "gUlDspRf: " + data[nCnt]);
                nCnt += 3;
                //Debug.loge(new Exception(),"[" + nCnt + "]" + "gDlAmp: " + data[nCnt]);
                nCnt += 2;
                //Debug.loge(new Exception(),"[" + nCnt + "]" + "gUlAmp: " + data[nCnt]);
                nCnt += 2;
                //Debug.loge(new Exception(),"[" + nCnt + "]" + "gSignal: " + data[nCnt]);
                nCnt += 1;
                //Debug.loge(new Exception(),"[" + nCnt + "]" + "gModem: " + data[nCnt]);
                nCnt += 1;
                nCnt += 7;  // Reserve

//        System.arraycopy(data, nCnt, Variables.gSystemName, 0, 20);
                nCnt += 20;
                System.arraycopy(data, nCnt, lo_name, 0, 20);
                nCnt += 20;
//        System.arraycopy(data, nCnt, Variables.gOperatorName, 0, 20);
                nCnt += 20;
//        System.arraycopy(data, nCnt, Variables.gSupplierName, 0, 20);
                nCnt += 20;
                return ByteUtil.byteArrayToString(lo_name);
            }
        }
        return "";
    }


}
