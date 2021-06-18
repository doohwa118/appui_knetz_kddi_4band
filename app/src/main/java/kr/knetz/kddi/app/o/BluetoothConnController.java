/*
 * Copyright (C) 2011 Wireless Network and Multimedia Laboratory, NCU, Taiwan
 * 
 * You can reference http://wmlab.csie.ncu.edu.tw
 * 
 * This class is used to handle process or transfer the control messages about connection * 
 * 
 * @author Fiona
 * @version 0.0.1
 *
 */

package kr.knetz.kddi.app.o;


import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import kr.knetz.kddi.app.l.Debug;
import kr.knetz.kddi.app.v.MainActivity;

public class BluetoothConnController extends Service {
	
    // Debugging
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
	         //       Toast.makeText(getApplicationContext(), "Connected to "+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
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
	            	Debug.loge(new Exception(),"MESSAGE READ ~!!!!!!");
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
                    sendBroadcast(readBuf, MainActivity.INCOMING_MSG, msg.arg2);

	              //  sendBroadcast(readMessage, ServiceController.RECEIVE_MSG_FROM_BT_ACTION);
                  //TODO: support only one remote device now
	                deviceName = null;
	                break;	            

	            case MESSAGE_TOAST:
	                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
	                break;

	            case MESSAGE_ALERT_DIALOG:
//					Debug.logi(new Exception(),"=dhjung=======> BluetoothConnController.MESSAGE_ALERT_DIALOG");

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
				BluetoothConnController.this.sendBroadcast(i);
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
            BluetoothConnController.this.sendBroadcast(i);
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
            mConnService.writeToAllSockets(message);
        }
    }
    private void connectTo(String deviceAddress){
    	//if (!deviceAddress.matches("[A-Za-z0-9]{2}:[A-Za-z0-9]{2}:[A-Za-z0-9]{2}:[A-Za-z0-9]{2}:[A-Za-z0-9]{2}:[A-Za-z0-9]{2}"))
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
    	BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
    	mConnService.connectTo(device);
    	this.startForeground(1234, new Notification());
    	Log.e(TAG, "connectTo!!!");
    }
    
    private void disconnectTo(String address){
    	if(mConnService != null)
    		mConnService.disconnectSocketFromAddress(address);
    	this.stopForeground(true);
    	Log.e(TAG, "disconnectTo!!!" + address);
    }
    
    private void terminatedAllSockets(){
    	//mConnService.terminated();
    	if(mConnService != null)
    		mConnService.terminated();
    	mConnService = null;
    	Log.e(TAG, "terminatedAllSockets!!!");
    }

//	@Override
//	public void onCreate() {
//		if(D) Log.e(TAG, "[onCreate]");
//
//
//		msgHandler = new MessageHandler();
//	//	mConnService = new BluetoothConnModel(this, msgHandler);
//	//	mConnService.startSession();
//
//		IntentFilter mFilter01, mFilter02, mFilter03, stateChangedFilter;
//        mFilter01 = new IntentFilter(SEND_MSG_FROM_BT_ACTION);
//        mFilter02 = new IntentFilter(CONNECT_REQUEST_ACTION);
//        mFilter03 = new IntentFilter(DISCONNECT_REQUEST_ACTION);
//        stateChangedFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//        mBtMsgReceiver = new MessageReceiver();
//        registerReceiver(mBtMsgReceiver, mFilter01);
//        registerReceiver(mBtMsgReceiver, mFilter02);
//        registerReceiver(mBtMsgReceiver, mFilter03);
//        registerReceiver(mBtMsgReceiver, new IntentFilter(START_MONITOR_ACTION));
//        registerReceiver(mBtMsgReceiver, stateChangedFilter);
//        registerReceiver(mBtMsgReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
//        registerReceiver(mBtMsgReceiver, new IntentFilter(GET_SERIVICE_STATUS_ACTION));
//
//        super.onCreate();
//
//	}
//

	@Override
	public void onDestroy() {
//		if(D) Log.e(TAG, "[onDestroy]");
		super.onDestroy();
		if (mConnService != null) mConnService.terminated();
		mConnService = null;
		stopSelf();
		
		unregisterReceiver(mBtMsgReceiver);
	}

	@Override
	public void onStart(Intent intent, int startId) {
//		if(D) Log.e(TAG, "[onStart]");
		super.onStart(intent, startId);
		if (mConnService == null){
			mConnService = new BluetoothConnModel(this, msgHandler);
			mConnService.startSession();
		}
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if(D) Log.e(TAG, "[onStart]");
        super.onStartCommand(intent, flags, startId);

        if (mConnService == null){
            mConnService = new BluetoothConnModel(this, msgHandler);
            mConnService.startSession();
        }
        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    @Override
	public IBinder onBind(Intent arg0) {
//		if(D) Log.e(TAG, "[onBind]");
		return null;
	}
	
	public class LocalBinder extends Binder {
		BluetoothConnController getService() {
            return BluetoothConnController.this;
        }
    }

    public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action  = intent.getAction();
//			if(D) Log.e(TAG, "** ON RECEIVE **" + action);
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
				Log.i(TAG, "[onReceive] deviceAddress = " + deviceAddress);
				BluetoothConnController.this.connectTo(deviceAddress);
			}else if (action.equals(DISCONNECT_REQUEST_ACTION)){
				Log.i(TAG, "[onReceive] DISCONNECT_REQUEST_ACTION");
				String deviceAddress = intent.getExtras().getString(MainActivity.DISCONNECT_DEVICE_ADDRESS);
				Log.i(TAG, "[onReceive] disconnect device address = " + deviceAddress);
				BluetoothConnController.this.disconnectTo(deviceAddress);				
			}else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
				Log.i(TAG, "[onReceive] ACTION_STATE_CHANGED");
				int currentState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
				switch (currentState){
				case BluetoothAdapter.STATE_ON:
					Log.i(TAG, "[onReceive] current state = ON");
					break;
				case BluetoothAdapter.STATE_OFF:
					Log.i(TAG, "[onReceive] current state = OFF");
					BluetoothConnController.this.terminatedAllSockets();
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
				Log.d(TAG, "START_MONITOR_ACTION");
				mConnService.startFileMonitor(intent.getBooleanExtra(MONITOR_STATUS, false));
			}else if(action.equals(GET_SERIVICE_STATUS_ACTION)){
				Intent i = new Intent(GET_SERIVICE_STATUS_EVENT);
				i.putExtra(MONITOR_STATUS, true);//mConnService.getFileMonitor());
				i.putExtra(TX_BYTES, mConnService.getTxBytes());
				i.putExtra(RX_BYTES, mConnService.getRxBytes());
				BluetoothConnController.this.sendBroadcast(i);
			}else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
				BluetoothDevice device = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);
				//Toast.makeText(getApplicationContext(), device.getName() + " was disconnected: " + device.getAddress(), Toast.LENGTH_SHORT).show();
				BluetoothConnController.this.disconnectTo(device.getAddress());
				Log.d(TAG, "BT connection was disconnected!" + device.getAddress());
				//BluetoothConnController.this.msgHandler.sendBroadcast(device.getName() + " was disconnected: " + device.getAddress(), BluetoothConn.ALERT_MSG, -1);
			}
			else{
				Log.e(TAG, "another action: " + action);
			}
		}
    }

}
