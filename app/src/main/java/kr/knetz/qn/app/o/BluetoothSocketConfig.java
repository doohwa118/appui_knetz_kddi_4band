/*
 * Copyright (C) 2011 Wireless Network and Multimedia Laboratory, NCU, Taiwan
 * 
 * You can reference http://wmlab.csie.ncu.edu.tw
 * 
 * This class defines that the related socket state and I/O thread (ConnectedThread).
 * 
 * 
 * @author Fiona
 * @version 0.0.1
 *
 */


package kr.knetz.qn.app.o;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class BluetoothSocketConfig {
    // Debugging
    private static final String TAG = "BluetoothSocketConfig";
    private static final boolean D = true;
    
    private static BluetoothSocketConfig mBtSocketConfig = null;  
    
	public static final int SOCKET_NONE = 0;
	public static final int SOCKET_CONNECTED = 1;
	
	public static final int FIELD_CONNECTED_THREAD = 0;
	public static final int FIELD_SOCKET_STATE = 1;
	
//	public static final int SOCKET_CLOSED = 2;
	
    private Map<BluetoothSocket, BluetoothSocketInfo> mBluetoothSocekts;
    
    private BluetoothSocketConfig(){
    	mBluetoothSocekts = new HashMap<BluetoothSocket, BluetoothSocketInfo>();
    }
    public static BluetoothSocketConfig getInstance(){
    	if (mBtSocketConfig == null){
    		synchronized(BluetoothSocketConfig.class){
    			if (mBtSocketConfig == null){
    				mBtSocketConfig = new BluetoothSocketConfig();
    			}
    		}
    	}
    	return mBtSocketConfig;
    }
    
    public boolean registerSocket(BluetoothSocket socket, BluetoothConnModel.ConnectedThread t, int socketState){
    	Log.d(TAG, "[registerSocket] start");
    	boolean status = true;
    	if (socketState == SOCKET_CONNECTED) {
        	Set<BluetoothSocket> socketSets = this.containSockets(socket.getRemoteDevice().getAddress());
        	for(BluetoothSocket tmp : socketSets){
        		unregisterSocket(tmp);
        		status = false;
        	}    		
    	}

    	BluetoothSocketInfo socketInfo = new BluetoothSocketInfo();
    	socketInfo.setBluetoothSocket(socket);
    	socketInfo.setConnectedThread(t);  
    	socketInfo.setSocketState(socketState);    	
    	mBluetoothSocekts.put(socket, socketInfo); 
    	return status;
    }
    public void updateSocketInfo(BluetoothSocket socket, int field, Object arg){
    	if (mBluetoothSocekts.containsKey(socket)){
    		BluetoothSocketInfo socketInfo = mBluetoothSocekts.get(socket);    		
    		if (field == FIELD_CONNECTED_THREAD ){
    			BluetoothConnModel.ConnectedThread t = (BluetoothConnModel.ConnectedThread)arg;
    			socketInfo.setConnectedThread(t);
    		} else if (field == FIELD_SOCKET_STATE){
    			int socketState = (Integer)arg;
    			socketInfo.setSocketState(socketState);
    		}
    		mBluetoothSocekts.put(socket, socketInfo);
    	} else {
    		Log.e(TAG, "[updateSocketInfo] Socket doesn't exist.");
    	}   
    	
    }
    
    public void unregisterSocket(BluetoothSocket socket){
    	Log.d(TAG, "[unregisterSocket] start");
    	if (mBluetoothSocekts.containsKey(socket)){
    		BluetoothSocketInfo socketInfo = mBluetoothSocekts.get(socket);
    		try {
            	InputStream inputStream = socket.getInputStream();
            	OutputStream outputStream = socket.getOutputStream();
            	
            	if (inputStream != null){
            		inputStream.close();
            		Log.w(TAG, "[disconnectSocket] Close the input stream");
            	}
            	if (outputStream != null){
            		outputStream.close();
            		Log.w(TAG, "[disconnectSocket] Close the output stream");
            	}
            	if (socket != null) {
            		socket.close();
            	    Log.w(TAG, "[disconnectSocket] Close bluetooth socket " + socket.toString() + " ; device name is " + socket.getRemoteDevice().getName());
            	}
            } catch (IOException e) {
                Log.e(TAG, "[disconnectSocket] close() of connect socket failed", e);
            }
    		socketInfo.setConnectedThread(null);
    		socketInfo.setSocketState(SOCKET_NONE);
    		socketInfo.setBluetoothSocket(null);
    		mBluetoothSocekts.remove(socket);
    		Log.e(TAG, "[updateSocketInfo] Remove socket " + socket.getRemoteDevice().getAddress());
    	}    		
    }

    public Set<BluetoothSocket> containSockets(String address){
    	Set<BluetoothSocket> socketSets = new HashSet<BluetoothSocket>();
    	Iterator<BluetoothSocket> it = mBluetoothSocekts.keySet().iterator();
    	while(it.hasNext()){
    		BluetoothSocket socket = it.next();
    		if (socket.getRemoteDevice().getAddress().contains(address)){
    		//mBluetoothSocekts.get(socket).getDevice();
    			socketSets.add(socket);
    		}
    	}   	
    	return socketSets;
    }

    public Set<BluetoothSocket> getConnectedSocketList(){
    	return mBluetoothSocekts.keySet();
    }
    public BluetoothConnModel.ConnectedThread getConnectedThread(BluetoothSocket socket){
    	BluetoothSocketInfo socketInfo = mBluetoothSocekts.get(socket);
    	return socketInfo.getConnectedThread(socket);
    }
    public boolean isSocketConnected(BluetoothSocket socket){
        return mBluetoothSocekts.containsKey(socket);
    }
    
	
	private class BluetoothSocketInfo{
	    private int mState = 0;
	    private BluetoothSocket mBluetoothSocket;
	    private BluetoothConnModel.ConnectedThread mConnectedThread;
	//    private BluetoothDevice mDevice;
	    //getter
	    public BluetoothSocket getBluetoothSocket() {
	    	return mBluetoothSocket;    	
	    }
	    public BluetoothConnModel.ConnectedThread getConnectedThread(BluetoothSocket socket){
	    	return mConnectedThread;
	    }
	    
	 //   protected BluetoothDevice getDevice(){
	 //   	return mDevice;
	//    }
	    //setter
	    protected void setBluetoothSocket(BluetoothSocket socket) {
	    	mBluetoothSocket = socket;   
	   // 	mDevice = socket.getRemoteDevice();
	    }
	    protected void setSocketState(int socketState){
	    	mState = socketState;
	    }
	    protected void setConnectedThread(BluetoothConnModel.ConnectedThread t){
	    	mConnectedThread = t;
	    }
	    //state
	 //   public boolean isSocketConnected() {
	  //  	if (mState == BluetoothSocketConfig.SOCKET_CONNECTED)
	//    	    return true;
	 //   	else
	 //   		return false;
	 //   }
		
	}

}
