package kr.knetz.qn.app.v.h;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import kr.knetz.qn.app.KNetzApp;
import kr.knetz.qn.app.R;
import kr.knetz.qn.app.l.Debug;
import kr.knetz.qn.app.v.MainActivity;
import kr.knetz.qn.app.v.t.SettingsPreference;
import kr.knetz.qn.app.v.x.Variables;


public class LoginActivity extends Activity implements View.OnClickListener {
    Intent i;

    public LoginActivity() {
        super();
    }

    public static Activity mLoginActivity;
    EditText edPassword;
    CheckBox chkPassword;
    String old_password;
    String remember_password;
    SettingsPreference settingPreference;
    static boolean backKeyPressedFlag = false;
    private long backKeyPressedTime = 0;
    private Toast toast;

    private static final String TAG = "LoginActivity";

    private LoginMessageReceiver mBtLoginMsgReceiver;

    KNetzApp application;
    Messenger mService = null;
    boolean mIsBound = false; // MainActivity <-> Service Binding Check
    final Messenger mMessenger = new Messenger(new IncomingHandler(LoginActivity.this));

    private BluetoothAdapter mBluetoothAdapter = null;


    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_PREF_SETTING = 3;
    private static final int REQUEST_DISCONNECT_DEVICE =  4;
    private static final int REQUEST_ABOUT_INFO = 5;

    private static final int DIALOG_ABOUT_INFO = 6;
    private static final int FILE_DIALOG_REQUEST = 7;


    public boolean isServiceRunning() {
        String serviceName = Variables.SERVICE_NAME;
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {

            if (serviceName.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Debug.logi(new Exception(),"=dhjung=======> LoginActivity onCreate START");
        setContentView(R.layout.activity_login);

        settingPreference = new SettingsPreference(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (getActionBar() != null)
            getActionBar().hide();

        edPassword = findViewById(R.id.login_password_edit);
        chkPassword = findViewById(R.id.chk_remember_passwd);
        boolean bRememberFlag = SettingsPreference.getValue(SettingsPreference.REMEMBER_PASSWORD_FLAG, false);
        old_password = SettingsPreference.getValue(SettingsPreference.OLD_PASSWORD, Variables.DEFAULT_PASSWORD);

        chkPassword.setChecked(bRememberFlag);
        if(bRememberFlag){
            remember_password = SettingsPreference.getValue(SettingsPreference.REMEMBER_PASSWORD, "");
            edPassword.setText(remember_password);
        }

        Button bt = findViewById(R.id.login_button);
        mLoginActivity = LoginActivity.this;

        bt.setOnClickListener(this);
        Debug.logi(new Exception(),"isServiceRunning() : "+isServiceRunning());
        if (isServiceRunning())
            doBindService();

        mBtLoginMsgReceiver = new LoginMessageReceiver();
        registerReceiver(mBtLoginMsgReceiver, new IntentFilter(MainActivity.LOGIN_ALERT_MSG));

    }

    EditText.OnClickListener mEdit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showSoftKeyboard(v);
        }
    };

    protected void showSoftKeyboard(View view) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(view, 0);
    }

    protected void hideSoftKeyboard(View view) {
        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.login_button:
                Debug.logi(new Exception(),"=dhjung=======> EditText.getText().toString() : "+ edPassword.getText().toString());

                if (!edPassword.getText().toString().equals("demodemo")) {
                    Variables.DEMO_ENABLED = false;
                    if (edPassword.getText().toString().equals(old_password)) {
                        if (chkPassword.isChecked()) {
                            if (edPassword.getText().length() > 0) {
                                SettingsPreference.put(SettingsPreference.REMEMBER_PASSWORD, edPassword.getText().toString());
                                Debug.loge(new Exception(), "edit : " + edPassword.getText().toString() + "\n getShared : " + SettingsPreference.getValue(SettingsPreference.REMEMBER_PASSWORD, Variables.DEFAULT_PASSWORD));
                            }
                        }
                        // always check status Save!!!! true or false always save
                        SettingsPreference.put(SettingsPreference.REMEMBER_PASSWORD_FLAG, chkPassword.isChecked());

//                    i = new Intent(v.getContext(), ConnectActivity.class);
//                    startActivity(i);
//                    finish();
                        hideSoftKeyboard(v);
                        setContentView(R.layout.activity_connect);

                        Message msg = Message.obtain(null, Variables.MSG_LOGINACTIVITY_TO_SERVICE_START);
                        try {
//                            if(mService == null)
//                                doBindService();
//                            if (mService != null)
                                mService.send(msg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    } else {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        if (edPassword.getText().toString().length() > 0) {
                            alertDialog.setMessage("Wrong Password!! Again Input Password");
                        } else {
                            alertDialog.setMessage("Input Password");
                        }
                        alertDialog.show();
                    }

                }else{
                    Debug.logi(new Exception(),"=dhjung=======> demodemo ");

                    hideSoftKeyboard(v);
                    setContentView(R.layout.activity_connect);
                    Variables.DEMO_ENABLED = true;

//                    Message msg = Message.obtain(null, Variables.MSG_LOGINACTIVITY_TO_SERVICE_START);
//                    try {
//                        mService.send(msg);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
                    setResult(Variables.RESULT_CODE_LOGIN_OK);
                    finish();
                }
                break;
            default:
//                Debug.logi(new Exception(),"=dhjung=======> default : "+ v.getId());
                break;
        }
    }



    static class IncomingHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;
        IncomingHandler(LoginActivity service){
            mActivity = new WeakReference<LoginActivity>(service);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LoginActivity activity = mActivity.get();
            if (activity != null){
                activity.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg){
        AlertDialog.Builder alert;
        switch (msg.what) {
            case Variables.MSG_SERVICE_TO_LOGINACTIVITY_DATA :
                Debug.loge(new Exception(),"MSG_SERVICE_TO_LOGINACTIVITY_DATA.");
//                byte[] recvData = (byte[])msg.obj;
//                parseRcvData (recvData);
                break;
            case Variables.MSG_SERVICE_TO_LOGINACTIVITY_FINISH :
                setResult(Variables.RESULT_CODE_LOGIN_OK);
                finish();
                break;
            case Variables.MSG_SERVICE_TO_LOGINACTIVITY_CABLE_NOT_CONNECT :
                Debug.loge(new Exception(),"MSG_SERVICE_TO_LOGINACTIVITY_CABLE_NOT_CONNECT.");
//                Toast.makeText(getBaseContext(),"Cable Not Connected!!!",Toast.LENGTH_SHORT).show();
//                try {
//                    Thread.sleep(1000L);
//                    finish();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                alert = new AlertDialog.Builder(this);

                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(Variables.RESULT_CODE_LOGIN_FAIL);
                        dialog.dismiss();     //닫기
                        finish();

                    }
                });
                alert.setMessage("not connected Cable!");
                alert.show();

                break;
            case Variables.MSG_SERVICE_TO_LOGINACTIVITY_BLUETOOTH_CHECK :
                Debug.loge(new Exception(),"MSG_SERVICE_TO_LOGINACTIVITY_BLUETOOTH_CHECK.");

                if (mBluetoothAdapter == null) {
                    Toast.makeText(LoginActivity.this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
                    setResult(Variables.RESULT_CODE_LOGIN_FAIL);
                    finish();
                    return;
                }

                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    // Otherwise, setup the chat session
                } else {
                    sendToServiceBluetoothInit();
                    Intent serverIntent = new Intent(LoginActivity.this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                }

//                alert = new AlertDialog.Builder(this);
//                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // send msg
//
//                        dialog.dismiss();     //닫기
//
//                        if (mBluetoothAdapter == null) {
//                            Toast.makeText(LoginActivity.this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
//                            setResult(Variables.RESULT_CODE_LOGIN_FAIL);
//                            finish();
//                            return;
//                        }
//                        if (!mBluetoothAdapter.isEnabled()) {
//                            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//                            // Otherwise, setup the chat session
//                        } else {
//                            sendToServiceBluetoothInit();
//                            Intent serverIntent = new Intent(LoginActivity.this, DeviceListActivity.class);
//                            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
//                        }
//
//
//                    }
//                });
//                alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        setResult(Variables.RESULT_CODE_LOGIN_FAIL);
//                        dialog.dismiss();     //닫기
//                        finish();
//                    }
//                });
//                alert.setMessage("OTG Cable Not Connection!\nBluetooth Connection retry?");
//                alert.show();
                break;
//            case Variables.MSG_SERVICE_TO_LOGINACTIVITY_CABLE_NOT_CONNECT :
//                Debug.loge(new Exception(),"MSG_SERVICE_TO_CONNECTACTIVITY_CABLE_NOT_CONNECT.");
//                Toast.makeText(getBaseContext(), "Cable Not Connected!!!", Toast.LENGTH_SHORT).show();
//                try {
//                    Thread.sleep(1000L);
//                    finish();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                break;
        }
    }

    private void sendToServiceBluetoothInit(){
        Message msg = Message.obtain(null, Variables.MSG_LOGINACTIVITY_TO_SERVICE_BLUETOOTH_INIT);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sendToServiceFinish(){
        Message msg = Message.obtain(null, Variables.MSG_LOGINACTIVITY_TO_SERVICE_FINISH);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Debug.logi(new Exception(),"=dhjung=======> onActivityResult: "+requestCode);

        switch(requestCode){
            case REQUEST_CONNECT_DEVICE :
                Debug.logi(new Exception(),"=dhjung=======> REQUEST_CONNECT_DEVICE (Login)");
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    Debug.logi(new Exception(),"address :"+address);
                    // Get the BLuetoothDevice object
                    //   BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    //mConnService.connectTo(device);
                    Intent i = new Intent(DeviceService.CONNECT_REQUEST_ACTION);
                    i.putExtra(MainActivity.DEVICE_ADDRESS, address);
                    sendBroadcast(i);
                }
                break;
            case Variables.REQUEST_CODE_LOGIN :
                Debug.logi(new Exception(),"=dhjung=======> REQUEST_CODE_LOGIN (Login)");

                if (resultCode == Variables.RESULT_CODE_LOGIN_FAIL){
                    finish();
                }else {
                    Debug.logi(new Exception()," REQUEST_CODE_LOGIN refresh");
                    Variables.ACTIVITY_LOGIN_FLAG = true;
                    finish();
                    startActivity(getIntent());
                }
                break;
            case REQUEST_ENABLE_BT:
                Debug.logi(new Exception(),"=dhjung=======> REQUEST_ENABLE_BT (Login)");

                if (resultCode == Activity.RESULT_OK) {
                    sendToServiceBluetoothInit();
                    Intent serverIntent = new Intent(LoginActivity.this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                }else{
                    setResult(Variables.RESULT_CODE_LOGIN_FAIL);
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                //Debug.loge(new Exception(),">>> requestCode: " + requestCode);
                break;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            Debug.loge(new Exception(), "Attached.");

            try {
                Message msg = Message.obtain(null, Variables.MSG_REGISTER_CLIENT_LOGINACTIVITY);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            Debug.loge(new Exception(),"Disconnected.");
        }
    };

    void doBindService() {
        bindService(new Intent(LoginActivity.this, DeviceService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    public void onBackKeyPressed() {
        Variables.BACKKEY_PRESSED_FLAG = true;

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if(System.currentTimeMillis() <= backKeyPressedTime + 2000) {
//            activity.finish();
            Message msg = Message.obtain(null, Variables.MSG_SERVICE_TO_MAINACTIVITY_STOP);
            try {
                mMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            doUnbindService();
            sendToServiceFinish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(this,"\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        Debug.logi(new Exception(),"=dhjung=======> LoginActivity onBackPressed");

        onBackKeyPressed();

//        finish();
//        android.os.Process.killProcess(android.os.Process.myPid());
//        ActivityCompat.finishAffinity(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Debug.logi(new Exception(),"=dhjung=======> LoginActivity onDestroy");

        if (mBtLoginMsgReceiver != null)
            unregisterReceiver(mBtLoginMsgReceiver);

        doUnbindService();
    }


    private class LoginMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action  = intent.getAction();
//            Log.e(TAG, "** ON RECEIVE **" + action);
            if (action.equals(MainActivity.LOGIN_ALERT_MSG)) {
                Log.e(TAG, "Knetz ALERT_MSG : " + action);

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Pairing Failure!!");
                builder.setMessage("Pairing failure has occurred.\n Please reconnect the device.\n");
                builder.setCancelable(false);
                builder.setNeutralButton("close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.setView(View.INVISIBLE);


                        sendToServiceBluetoothInit();
                        Intent serverIntent = new Intent(LoginActivity.this, DeviceListActivity.class);
                        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);



//                        doUnbindService();
//                        sendToServiceFinish();

//                        moveTaskToBack(true);
//                        finishAndRemoveTask();
//                        android.os.Process.killProcess(android.os.Process.myPid());
//
//                        ActivityCompat.finishAffinity(LoginActivity.this);
                    }
                });
                builder.show();

            }
            else{
                Log.e(TAG, "another action: " + action);
            }
        }

    }
}
