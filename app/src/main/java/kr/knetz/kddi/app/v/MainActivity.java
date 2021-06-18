package kr.knetz.kddi.app.v;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

import kr.knetz.kddi.app.KNetzApp;
import kr.knetz.kddi.app.R;

import kr.knetz.kddi.app.l.ByteUtil;
import kr.knetz.kddi.app.l.Crc16;
import kr.knetz.kddi.app.o.AppSQLiteHelper;
import kr.knetz.kddi.app.v.h.DeviceService;
import kr.knetz.kddi.app.l.Debug;
import kr.knetz.kddi.app.v.x.BackPressCloseHandler;
import kr.knetz.kddi.app.v.c.DataType;
import kr.knetz.kddi.app.v.t.SubTitleNameClass;
import kr.knetz.kddi.app.v.x.Variables;
import kr.knetz.kddi.app.v.h.FileListActivity;
import kr.knetz.kddi.app.v.h.HistoryActivity;
import kr.knetz.kddi.app.v.h.LoginActivity;
import kr.knetz.kddi.app.v.h.SettingActivity;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener,
        Tab3_Setting.OnSettingSendListener,
        Tab4_Modem.OnModemSendListener, Tab5_Tsync.OnTsyncSendListener{

    /* UI Declaration */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private FragmentPagerAdapter pagerAdapter;
    private ViewPager mViewPager;
    private PagerAdapter wrappedAdapter;
    private int mActionBarState = Variables.DISPLAY_ACTIONBAR_CONFIGURATION | Variables.DISPLAY_ACTIONBAR_DOWNLOAD;

    private TextView[] mTextView = new TextView[4];

    Window window;

    //    private CustomPagerAdapter customPagerAdapter;
//    private ViewPager pager;
    private TabPageIndicator indicator;
    private ArrayList listIcon;
    private ArrayList fragmentContents;


    static byte settingFrameInfo;
    static byte dspCtrlFrameInfo;
    static byte receiverFrameInfo;
    static byte modemFrameInfo;
    static byte tsyncFrameInfo;
//    static boolean settingSendFlag;
//    static boolean receiverSendFlag;
//    static boolean modemSendFlag;
//    static boolean tsyncSendFlag;
//    static boolean dspSendFlag;
    static boolean settingUpdateFlag;
    static boolean receiverUpdateFlag;
    static boolean modemUpdateFlag;
    static boolean tsyncUpdateFlag;
    static boolean dspUpdateFlag;
    static boolean dlStartCheckFlag;

    static boolean actionbarSelFlag = false;
    static boolean mainDestroyFlag = false;

    private static final int mFileListActivity = 0;
    public int FwDownloadPercentage;

    private ProgressDialog mDialog;

    static int DeviceWidth;
    static int DeviceHeight;

    static int BandSelectCount = 0;

    static int getIdBackup = R.id.tv_tab1;


    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public static final String NOTIFY_UI = "NOTIFY_UI";
    public static final String INCOMING_MSG = "INCOMING_MSG";
    public static final String OUTGOING_MSG = "OUTGOING_MSG";
    public static final String ALERT_MSG = "ALERT_MSG";
    public static final String LOGIN_ALERT_MSG = "LOGIN_ALERT_MSG";
    public static final String KEY_ECHO_PREF = "KEY_ECHO_PREF";
    public static final String KEY_SHOW_RX_PREF = "KEY_SHOW_RX_PREF";
    public static final String DEVICE_ADDRESS = "device_address";
    public static final String DISCONNECT_DEVICE_ADDRESS = "disconnected_device_address";


    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_PREF_SETTING = 3;
    private static final int REQUEST_DISCONNECT_DEVICE = 4;
    private static final int REQUEST_ABOUT_INFO = 5;

    private static final int DIALOG_ABOUT_INFO = 6;
    private static final int FILE_DIALOG_REQUEST = 7;

    private static Intent serviceIntent = null;


    public static int getDeviceWidth() {
        return DeviceWidth;
    }

    public static int getDeviceHeight() {
        return DeviceHeight;
    }

    private BackPressCloseHandler backPressCloseHandler;

    /* Service Declaration */
    Messenger mService = null;
    boolean mIsBound = false; // MainActivity <-> Service Binding Check
    final Messenger mMessenger = new Messenger(new IncomingHandler(MainActivity.this));

    static SubTitleNameClass subTitleNameClass;
//    private Handler mHandler;
    private ProgressDialog mProgressDialog;
//    private Runnable mRunnable;

    private long backKeyPressedTime = 0;
    private Toast toast;

    KNetzApp application;

//    private Handler confirmHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
////            Debug.loge(new Exception(),"Knetz ====handleMessage : "+msg.what);
//            Tab3_Setting fragment = (Tab3_Setting) mSectionsPagerAdapter.getFragment(2);
//            fragment.doRefreshGridViewList();
//        }
//    };
//
//    private Handler progressHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
////            Debug.loge(new Exception(),"Knetz ====progressHandler : "+msg.what);
//            if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                mProgressDialog.dismiss();
//            }
//        }
//    };

    public void onBackKeyPressed() {
        Variables.BACKKEY_PRESSED_FLAG = true;

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if((System.currentTimeMillis() <= backKeyPressedTime + 2000) && (actionbarSelFlag == false)) {
//            activity.finish();
            Message msg = Message.obtain(null, Variables.MSG_SERVICE_TO_MAINACTIVITY_STOP);
            try {
                mMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(this,"\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onModemSettingSend(int type) {
//        Debug.loge(new Exception(),"=dhjung=======> onModemSettingSend: " + type);

        if (Variables.SYSTEM_MODEM_RECEIVER != Variables.SYSTEM_RECEIVER) {
            sendModemSettingMsg();
        } else {
            sendReceiverSettingMsg();
        }
    }

    @Override
    public void onSettingSend(int type) {
//        Debug.loge(new Exception(),"=dhjung=======> onSettingSend: " + type);

        if (type == Variables.DIALOG_TYPE_SEEKBAR_DSP) sendDspCtrlMsg();
        else sendSettingMsg();
    }

    @Override
    public void onTsyncSettingSend(int type) {
//        Debug.loge(new Exception(),"=dhjung=======> onTsyncSettingSend: " + type);
        sendTsyncSettingMsg();
    }

    public MainActivity() {
        super();
    }

    static class IncomingHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        IncomingHandler(MainActivity service) {
            mActivity = new WeakReference<MainActivity>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg) {
        byte[] recvData;
        switch (msg.what) {
            case Variables.MSG_SERVICE_TO_MAINACTIVITY_DATA:
                Variables.dataSendCnt = 0;
                recvData = (byte[]) msg.obj;
                parseRcvData(recvData);

                BandSelectCount++;
                if (mProgressDialog != null && mProgressDialog.isShowing()){
                    if (BandSelectCount > 6){
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                        BandSelectCount = 0;
//                        Variables.REFRESH_TIMEOUT_MILLIS = 3000;
                    }
                }

//                if(BandSelectCount % 2 == 0)  getActionBar().setBackgroundDrawable(new ColorDrawable(0xDDa00709));
//                else   getActionBar().setBackgroundDrawable(new ColorDrawable(0xF5A00709));
                if(BandSelectCount % 2 == 0) window.setStatusBarColor(Color.parseColor("#FF000000")); // FF0000
                else window.setStatusBarColor(Color.parseColor("#173660"));
                break;

            case Variables.MSG_SERVICE_TO_MAINACTIVITY_INIT_DATA:
                recvData = (byte[]) msg.obj;
                parseRcvData(recvData);
                break;

            case Variables.MSG_SERVICE_TO_MAINACTIVITY_STOP:
                Toast.makeText(this,"connection end!!!",Toast.LENGTH_SHORT).show();

                if (mIsBound) {
                    unbindService(mConnection);
                    mIsBound = false;
                }

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                if(Variables.dataSendCnt > 20) {
                    Variables.dataSendCnt = 0;
                    Context context;
                    if( Variables.ACTIVITY_LOGIN_FLAG ){
                        context = MainActivity.this;
                    }
                    else {
                        context = LoginActivity.mLoginActivity;
                    }
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Connection Failure!!");
                    builder.setMessage("Connection failure has occurred.\n Please run the app again.\n");
                    builder.setCancelable(false);
                    builder.setNeutralButton("close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            builder.setView(View.INVISIBLE);
//                            moveTaskToBack(true);
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });
                    builder.show();

//                    finish();
//                    android.os.Process.killProcess(android.os.Process.myPid());
                }

                if(Variables.BACKKEY_PRESSED_FLAG == false) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Communication Failure!!");
                    builder.setMessage("A communication failure has occurred.\n Please run the app again.\n");
                    builder.setCancelable(false);
                    builder.setNeutralButton("close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            builder.setView(View.INVISIBLE);
//                            moveTaskToBack(true);
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });
                    builder.show();
                }
                else {
                    Toast.makeText(this,"process kill!!",Toast.LENGTH_SHORT).show();

                    Variables.BACKKEY_PRESSED_FLAG = false;
//                    moveTaskToBack(true);
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }

                break;

            case Variables.MSG_SERVICE_TO_MAINACTIVITY_FIRMWARE_DOWNLOAD:
                FwDownloadPercentage = msg.getData().getInt("data");
                //Debug.loge(new Exception(), "P: " + FwDownloadPercentage + " %");
                break;

            case Variables.MSG_SERVICE_TO_MAINACTIVITY_FIRMWARE_DOWNLOAD_FAIL:
                FwDownloadPercentage = -10;
                break;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            Debug.loge(new Exception(), "Attached.");
            try {
                Message msg = Message.obtain(null, Variables.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            Debug.loge(new Exception(), "Disconnected.");
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
//                UsbDevice device  = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            Debug.loge(new Exception(), "onNewIntent ATTACHED");
        }
    }

    void doBindService() {
        bindService(new Intent(MainActivity.this, DeviceService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    public void sendReceiverSettingMsg() {
//        Debug.loge(new Exception(),"=dhjung=======> sendReceiverSettingMsg frameinfo :  " + String.format("0x%02x", MainActivity.receiverFrameInfo));
//        Debug.loge(new Exception(),"=dhjung=======> sendReceiverSettingMsg frameinfo :  " + String.format("0x%02x", getReceiverSettingFrameInfo()));
        try {
            Message msg = Message.obtain(null, Variables.MSG_MAINACTIVITY_TO_SERVICE_RECEIVER_SETTING);
            Bundle data = new Bundle();
            data.putByte("receiver_data", getReceiverSettingFrameInfo());
//            msg.replyTo = mMessenger;
            msg.setData(data);
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        receiverSendFlag = true;
//        Debug.loge(new Exception(),"=dhjung=======> sendReceiverSettingMsg END");
    }

    public void sendModemSettingMsg() {
//        Debug.loge(new Exception(),"=dhjung=======> sendModemSettingMsg frameinfo :  " + String.format("0x%02x", MainActivity.modemFrameInfo));
//        Debug.loge(new Exception(),"=dhjung=======> sendModemSettingMsg frameinfo :  " + String.format("0x%02x", getModemSettingFrameInfo()));
        try {
            Message msg = Message.obtain(null, Variables.MSG_MAINACTIVITY_TO_SERVICE_MODEM_SETTING);
            Bundle data = new Bundle();
            data.putByte("modem_data", getModemSettingFrameInfo());
//            msg.replyTo = mMessenger;
            msg.setData(data);
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        modemSendFlag = true;
//        Debug.loge(new Exception(),"=dhjung=======> sendModemSettingMsg END");
    }

    public void sendTsyncSettingMsg() {
//        Debug.loge(new Exception(),"=dhjung=======> sendTsyncSettingMsg frameinfo :  " + String.format("0x%02x", MainActivity.tsyncFrameInfo));
//        Debug.loge(new Exception(),"=dhjung=======> sendTsyncSettingMsg frameinfo :  " + String.format("0x%02x", getTsyncSettingFrameInfo()));
        try {
            Message msg = Message.obtain(null, Variables.MSG_MAINACTIVITY_TO_SERVICE_TSYNC_SETTING);
            Bundle data = new Bundle();
            data.putByte("tsync_data", getTsyncSettingFrameInfo());
//            msg.replyTo = mMessenger;
            msg.setData(data);
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        tsyncSendFlag = true;
//        Debug.loge(new Exception(),"=dhjung=======> sendTsyncSettingMsg END");
    }

    public void sendDspCtrlMsg() {
//        Debug.loge(new Exception(),"=dhjung=======> sendDspCtrlMsg frameinfo :  " + String.format("0x%02x", MainActivity.dspCtrlFrameInfo));
//        Debug.loge(new Exception(),"=dhjung=======> sendDspCtrlMsg frameinfo :  " + String.format("0x%02x", getDspCtrlFrameInfo()));
        try {
            Message msg = Message.obtain(null, Variables.MSG_MAINACTIVITY_TO_SERVICE_DSP_CTRL);
            Bundle data = new Bundle();
            data.putByte("dsp_data", getDspCtrlFrameInfo());
//            msg.replyTo = mMessenger;
            msg.setData(data);
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        dspSendFlag = true;
//        Debug.loge(new Exception(),"=dhjung=======> sendDspCtrlMsg END");
    }

    public void sendSettingMsg() {
//        Debug.loge(new Exception(),"=dhjung=======> sendSettingMsg frameinfo :  " + String.format("0x%02x", MainActivity.settingFrameInfo));
//        Debug.loge(new Exception(),"=dhjung=======> sendSettingMsg frameinfo :  " + String.format("0x%02x", getSettingFrameInfo()));
        try {
            Message msg = Message.obtain(null, Variables.MSG_MAINACTIVITY_TO_SERVICE_SETTING);
            Bundle data = new Bundle();
            data.putByte("data", getSettingFrameInfo());
//            msg.replyTo = mMessenger;
            msg.setData(data);
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        settingSendFlag = true;
//        Debug.loge(new Exception(),"=dhjung=======> sendSettingMsg END");
    }




    public void sendReceiverSettingStopMsg() {
        try {
            Message msg = Message.obtain(null, Variables.MSG_MAINACTIVITY_TO_SERVICE_RECEIVER_SETTING_STOP);
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Variables.receiverSendFlag = false;
    }

    public void sendModemSettingStopMsg() {
        try {
            Message msg = Message.obtain(null, Variables.MSG_MAINACTIVITY_TO_SERVICE_MODEM_SETTING_STOP);
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Variables.modemSendFlag = false;
    }

    public void sendTsyncSettingStopMsg() {
        try {
            Message msg = Message.obtain(null, Variables.MSG_MAINACTIVITY_TO_SERVICE_TSYNC_SETTING_STOP);
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Variables.tsyncSendFlag = false;
    }

    public void sendDspCtrlStopMsg() {
        try {
            Message msg = Message.obtain(null, Variables.MSG_MAINACTIVITY_TO_SERVICE_DSP_CTRL_STOP);
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Variables.dspSendFlag = false;
    }

    public void sendSettingStopMsg() {
        try {
            Message msg = Message.obtain(null, Variables.MSG_MAINACTIVITY_TO_SERVICE_SETTING_STOP);
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Variables.settingSendFlag = false;
    }

    public byte getSettingFrameInfo() {
        return settingFrameInfo;
    }
    public byte getReceiverSettingFrameInfo() {
        return receiverFrameInfo;
    }
    public byte getModemSettingFrameInfo() {
        return modemFrameInfo;
    }
    public byte getTsyncSettingFrameInfo() {
        return tsyncFrameInfo;
    }
    public byte getDspCtrlFrameInfo() {
        return dspCtrlFrameInfo;
    }

    public void setSettingFrameInfo(byte settingFrameInfo) {
        MainActivity.settingFrameInfo = settingFrameInfo;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Debug.logi(new Exception(),"=dhjung=======> Main onCreate");

        AppSQLiteHelper sqLiteHelper = new AppSQLiteHelper(getBaseContext());
        sqLiteHelper.initialize();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // display always on!!!
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        backPressCloseHandler = new BackPressCloseHandler(this);

        DeviceWidth = displayMetrics.widthPixels;
        DeviceHeight = displayMetrics.heightPixels;

        // 초기 시작
        Variables.PROTO_DEST_ID = Variables.Dest_ID_ICS_B3_700;    // 기본값 0xC3
        Variables.band = 2;

        Variables.settingFlag = (byte) 0x00;
        Variables.receiverSettingFlag = (byte) 0x00;
        application = (KNetzApp) getApplication();
//        settingSendFlag = false;
        settingUpdateFlag = false;
//        receiverSendFlag = false;
        receiverUpdateFlag = false;
//        modemSendFlag = false;
        modemUpdateFlag = false;
//        tsyncSendFlag = false;
        tsyncUpdateFlag = false;

        Debug.loge(new Exception(), "serivce Variables.SYSTEM_R_TYPE : " + Variables.SYSTEM_R_TYPE);

        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

//        TextView tv1 = findViewById(R.id.tv_tab1);
//        TextView tv2 = findViewById(R.id.tv_tab2);
//        TextView tv3 = findViewById(R.id.tv_tab3);
//
//        LinearLayout.LayoutParams tv1_param = (LinearLayout.LayoutParams) tv1.getLayoutParams();
//        LinearLayout.LayoutParams tv2_param = (LinearLayout.LayoutParams) tv2.getLayoutParams();
//        LinearLayout.LayoutParams tv3_param = (LinearLayout.LayoutParams) tv3.getLayoutParams();

        mTextView[0] = findViewById(R.id.tv_tab1);
        mTextView[1] = findViewById(R.id.tv_tab2);
        mTextView[2] = findViewById(R.id.tv_tab3);
        mTextView[3] = findViewById(R.id.tv_tab4);

        LinearLayout.LayoutParams tv1_param = (LinearLayout.LayoutParams) mTextView[0].getLayoutParams();
        LinearLayout.LayoutParams tv2_param = (LinearLayout.LayoutParams) mTextView[1].getLayoutParams();
        LinearLayout.LayoutParams tv3_param = (LinearLayout.LayoutParams) mTextView[2].getLayoutParams();
        LinearLayout.LayoutParams tv4_param = (LinearLayout.LayoutParams) mTextView[3].getLayoutParams();

        switch(Variables.SYSTEM_R_TYPE){
            case Variables.SYSTEM_R_TYPE_KNETZ:
                tv1_param.weight = 0;
                mTextView[0].setLayoutParams(tv1_param);
                tv2_param.weight = 0;
                mTextView[1].setLayoutParams(tv2_param);
                tv3_param.weight = 0;
                mTextView[2].setLayoutParams(tv3_param);
                tv4_param.weight = 0;
                mTextView[3].setLayoutParams(tv4_param);
                break;
            case Variables.SYSTEM_R_TYPE_SUB6_5G:
                mTextView[0].setText("UNIT1");
                mTextView[1].setText("UNIT2");
                mTextView[0].setOnClickListener(mClickListner);
                mTextView[1].setOnClickListener(mClickListner);
                tv3_param.weight = 0;
                mTextView[2].setLayoutParams(tv3_param);
                tv4_param.weight = 0;
                mTextView[3].setLayoutParams(tv4_param);
                break;
            case Variables.SYSTEM_R_TYPE_3BAND:
                mTextView[0].setOnClickListener(mClickListner);
                mTextView[1].setOnClickListener(mClickListner);
                mTextView[2].setOnClickListener(mClickListner);
                mTextView[3].setOnClickListener(mClickListner);
                break;
            case Variables.SYSTEM_R_TYPE_KDDI_QN:
                mTextView[0].setText("700MHz");
                mTextView[1].setText("800MHz");
                mTextView[2].setText("1.7GHz");
                mTextView[3].setText("2.1GHz");
                mTextView[0].setOnClickListener(mClickListner);
                mTextView[1].setOnClickListener(mClickListner);
                mTextView[2].setOnClickListener(mClickListner);
                mTextView[3].setOnClickListener(mClickListner);
                break;
        }
        mTextView[0].setBackgroundColor(Color.parseColor("#EB5405"));
        mTextView[1].setBackgroundColor(Color.parseColor("#173660"));
        mTextView[2].setBackgroundColor(Color.parseColor("#173660"));
        mTextView[3].setBackgroundColor(Color.parseColor("#173660"));

//        findViewById(R.id.tv_tab1).setOnClickListener(mClickListner);
//        findViewById(R.id.tv_tab2).setOnClickListener(mClickListner);
//        findViewById(R.id.tv_tab3).setOnClickListener(mClickListner);

        // binding service start
//        doBindService();

//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);


        subTitleNameClass = application.getSubTitleNameClass();

//        Debug.logi(new Exception(), "service Variables.ACTIVITY_LOGIN_FLAG : "+Variables.ACTIVITY_LOGIN_FLAG);
        // LoginActivity Start
        if (!Variables.ACTIVITY_LOGIN_FLAG) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, Variables.REQUEST_CODE_LOGIN);
        } else {
            new loadProgressDialogAsync().execute(1);
        }

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
//            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("REPQN-ZK-2101");
        }
        mSectionsPagerAdapter = new SectionsPagerAdapter(getApplicationContext(), getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
//                        if (actionBar != null)
//                            actionBar.setSelectedNavigationItem(position);
//                        if (actionBar != null)
//                            actionBar.setTitle(mSectionsPagerAdapter.getPageTitle(position)); // Alarm Menu
                    }
                });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getTabCount(); i++) {
//            if (actionBar != null)
//                actionBar.addTab(actionBar.newTab().setIcon(mSectionsPagerAdapter.getIcon(i)).setTabListener(this));

            if (actionBar != null) {
                actionBar.addTab(actionBar.newTab().setTabListener(this).setCustomView(R.layout.actionbar_tab_layout));
                ((ImageView) findViewById(R.id.iv_actionbar_icon)).setImageDrawable(mSectionsPagerAdapter.getIcon(i));
                ((TextView) findViewById(R.id.tv_actionbar_text)).setText(mSectionsPagerAdapter.getPageTitle(i));
            }
        }

//        if (actionBar != null)
//            actionBar.setTitle(mSectionsPagerAdapter.getPageTitle(0));
//        actionBar.hide();

        mViewPager.setCurrentItem(0, false);

        indicator = findViewById(R.id.tabs);
        indicator.setTabIconLocation(TabPageIndicator.LOCATION_UP);
        indicator.setViewPager(mViewPager);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Debug.logi(new Exception(), "onPageSelected : " + position);
//                if (actionBar != null)
//                    actionBar.setTitle(mSectionsPagerAdapter.getPageTitle(position));

                mActionBarState = mSectionsPagerAdapter.getActionBarButtonType(position);
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

//        indicator.getview
//        Intent intent = getIntent();
//        byte[] data = intent.getExtras().getByteArray("InitData");
//        parseRcvData (data);

        Debug.logi(new Exception(),"=dhjung=======> Main onCreate Done!!");
    }


    TextView.OnClickListener mClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        if(Variables.DEMO_ENABLED == false && getIdBackup != v.getId()) {
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Updating.... Please wait....");
            mProgressDialog.show();
            BandSelectCount = 1;
        }

        switch(v.getId()){
            case R.id.tv_tab1:  // 700MHz
//                Toast.makeText(getBaseContext(),"BAND1",Toast.LENGTH_SHORT).show();
                mTextView[0].setBackgroundColor(Color.parseColor("#EB5405"));
                mTextView[1].setBackgroundColor(Color.parseColor("#173660"));
                mTextView[2].setBackgroundColor(Color.parseColor("#173660"));
                mTextView[3].setBackgroundColor(Color.parseColor("#173660"));
                Variables.PROTO_DEST_ID = Variables.Dest_ID_ICS_B3_700;
//                Variables.REFRESH_TIMEOUT_MILLIS = 1000;
                Variables.band = 2;
                getIdBackup = R.id.tv_tab1;
                break;
            case R.id.tv_tab2:  // 800MHz
//                Toast.makeText(getBaseContext(),"BAND2",Toast.LENGTH_SHORT).show();
                mTextView[0].setBackgroundColor(Color.parseColor("#173660"));
                mTextView[1].setBackgroundColor(Color.parseColor("#EB5405"));
                mTextView[2].setBackgroundColor(Color.parseColor("#173660"));
                mTextView[3].setBackgroundColor(Color.parseColor("#173660"));
                Variables.PROTO_DEST_ID = Variables.Dest_ID_ICS_B1_800;
//                Variables.REFRESH_TIMEOUT_MILLIS = 1000;
                Variables.band = 0;
                getIdBackup = R.id.tv_tab2;
                break;
            case R.id.tv_tab3:  // 1.7GHz
//                Toast.makeText(getBaseContext(),"BAND3",Toast.LENGTH_SHORT).show();
                mTextView[0].setBackgroundColor(Color.parseColor("#173660"));
                mTextView[1].setBackgroundColor(Color.parseColor("#173660"));
                mTextView[2].setBackgroundColor(Color.parseColor("#EB5405"));
                mTextView[3].setBackgroundColor(Color.parseColor("#173660"));
                Variables.PROTO_DEST_ID = Variables.Dest_ID_ICS_B4_1700;
//                Variables.REFRESH_TIMEOUT_MILLIS = 1000;
                Variables.band = 3;
                getIdBackup = R.id.tv_tab3;
                break;
            case R.id.tv_tab4:  // 2.1GHz
//                Toast.makeText(getBaseContext(),"BAND4",Toast.LENGTH_SHORT).show();
                mTextView[0].setBackgroundColor(Color.parseColor("#173660"));
                mTextView[1].setBackgroundColor(Color.parseColor("#173660"));
                mTextView[2].setBackgroundColor(Color.parseColor("#173660"));
                mTextView[3].setBackgroundColor(Color.parseColor("#EB5405"));
                Variables.PROTO_DEST_ID = Variables.Dest_ID_ICS_B2_2100;
//                Variables.REFRESH_TIMEOUT_MILLIS = 1000;
                Variables.band = 1;
                getIdBackup = R.id.tv_tab4;
                break;
        }
        }
    };


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
    public boolean dispatchKeyEvent(KeyEvent event) {

//            Tab3_Setting tab3;
//            if (event.getAction() == KeyEvent.ACTION_DOWN)
//            {
//                switch(event.getKeyCode())
//                {
//                    case KeyEvent.KEYCODE_VOLUME_UP :
//                        Debug.logi(new Exception(), "VOLUME UP " + mViewPager.getCurrentItem());
////                        Tab1_Alarm tab1 = (Tab1_Alarm)mSectionsPagerAdapter.getFragment(0);
////                        Tab2_Status tab2 = (Tab2_Status)mSectionsPagerAdapter.getFragment(1);
//                        tab3 = (Tab3_Setting)mSectionsPagerAdapter.getFragment(2);
//                        tab3.onListTextViewTextSizeChanged(KeyEvent.KEYCODE_VOLUME_UP);
//                        return true;
//                    case KeyEvent.KEYCODE_VOLUME_DOWN :
//                        Debug.logi(new Exception(),"VOLUME DOWN");
//                        tab3 = (Tab3_Setting)mSectionsPagerAdapter.getFragment(2);
//                        tab3.onListTextViewTextSizeChanged(KeyEvent.KEYCODE_VOLUME_UP);
//                        return true;
//
//                }
//            }

        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Debug.logi(new Exception(),"=dhjung=======> Main onResume");

        if (isServiceRunning()) {
//            Debug.logi(new Exception(),"=dhjung=======> isServiceRunning()"+isServiceRunning());
            if (!mIsBound) {
                doBindService();
            } else {
//                Debug.logi(new Exception(), "=dhjung=======> mIsBound true binding ok");
            }
        } else {
            Debug.logi(new Exception(),"=dhjung=======> start Service");
            serviceIntent = new Intent(MainActivity.this, DeviceService.class);
            startService(serviceIntent);
            doBindService();
        }
        actionbarSelFlag = false;
    }

    public void getResolution() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Debug.loge(new Exception(), "displayMetrics.density : " + displayMetrics.density);
        Debug.loge(new Exception(), "deviceWidth : " + deviceWidth + ", deviceHeight : " + deviceHeight);
        Toast.makeText(this, "displayMetrics.density : " + displayMetrics.density + "\n deviceWidth : " + deviceWidth + ", deviceHeight : " + deviceHeight + "\n width : " + width + " height : " + height, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
//            super.onBackPressed();
//        Debug.loge(new Exception(), "==dhjung=======> onBackPressed !!");
//            backPressCloseHandler.onBackPressed();
        onBackKeyPressed();
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Debug.logd(new Exception(), "onTabSelected START : " + tab.getPosition());
        mViewPager.setCurrentItem(tab.getPosition());
        try {
            assert getActionBar() != null;
            getActionBar().setTitle(mViewPager.getAdapter().getPageTitle(tab.getPosition()));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        mSectionsPagerAdapter.getFragment(tab.getPosition()).

        mActionBarState = mSectionsPagerAdapter.getActionBarButtonType(tab.getPosition());

        invalidateOptionsMenu();

//        if (application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle().length > 0) {
//            Debug.loge(new Exception(), " length : " + application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle().length);
//            if (application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle()[0].getValue() != null) {
//                Debug.logi(new Exception(),"TEST getValue :"+application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle()[0].getValue());
//                switch (tab.getPosition()){
//                    case 0 :
//                        Tab1_Alarm fragment = (Tab1_Alarm)mSectionsPagerAdapter.getFragment(tab.getPosition());
//                        fragment.doRefreshGridViewList();
//                        break;
//                    case 1 :
//                        Tab2_Status fragment1 = (Tab2_Status)mSectionsPagerAdapter.getFragment(tab.getPosition());
//                        fragment1.doRefreshGridViewList();
//                        break;
//                    case 2 :
//                        Tab3_Setting fragment2 = (Tab3_Setting)mSectionsPagerAdapter.getFragment(tab.getPosition());
//                        fragment2.doRefreshGridViewList();
//                        break;
//                    case 3 :
//                        Tab4_Modem fragment3 = (Tab4_Modem)mSectionsPagerAdapter.getFragment(tab.getPosition());
//                        fragment3.doRefreshGridViewList();
//                        break;
//                }
//            }
//            Debug.loge(new Exception(), " length : " + application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle().length);
//        }

        Debug.logd(new Exception(), "onTabSelected END");
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Debug.logd(new Exception(), "onTabUnseleted");
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Debug.logd(new Exception(), "onTabReselected");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        Debug.logd(new Exception(), "onCreateOptionsMenu START");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);

        onSettingActionBarType(menu);

//        Debug.logd(new Exception(), "onCreateOptionsMenu END");
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        Debug.logd(new Exception(), "onPrepareOptionsMenu START");

//        menu.getItem(0).setVisible(true);
//        menu.getItem(0).setEnabled(true);

//        Debug.logd(new Exception(), "onPrepareOptionsMenu END");
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
//        Debug.logi(new Exception(),"=dhjung=======> Main onUserLeaveHint");

        if((mainDestroyFlag == true) && (actionbarSelFlag == false)){
            mainDestroyFlag = false;
            doUnbindService();
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            if (serviceIntent != null) {
//                Debug.logi(new Exception(), "stopService START");
                stopService(serviceIntent);
//                Debug.logi(new Exception(), "stopService END");
            }
            finish();
//            finishAndRemoveTask();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Debug.logi(new Exception(),"=dhjung=======> Main onDestroy");

        mainDestroyFlag = true;

        doUnbindService();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (serviceIntent != null) {
//            Debug.logi(new Exception(), "stopService START");
            stopService(serviceIntent);
//            Debug.logi(new Exception(), "stopService END");
        }
    }

    public void onSettingActionBarType(Menu menu) {
        int type = mActionBarState;
        int n = 0x01;
        Debug.loge(new Exception(), "type : " + type);

        for (int i = 0; i < menu.size(); i++){
            if ((type & n) == n){
                if (!menu.getItem(i).isVisible()) menu.getItem(i).setVisible(true);
            }else{
                menu.getItem(i).setVisible(false);
            }
            n = n << 0x01;
        }

//        for (int i = 0; i < menu.getItem(0).getSubMenu().size(); i++) {
//            if ((type & n) == n) {
//                if (!menu.getItem(0).getSubMenu().getItem(i).isVisible())
//                    menu.getItem(0).getSubMenu().getItem(i).setVisible(true);
//            } else {
//                menu.getItem(0).getSubMenu().getItem(i).setVisible(false);
//            }
//            n = n << 0x01;
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;

        actionbarSelFlag = true;

        switch (item.getItemId()) {
            case R.id.action_history:
//                Toast.makeText(getBaseContext(),"history",Toast.LENGTH_SHORT).show();
                File f = new File(Environment.getExternalStorageDirectory().getPath() + "/knetz");
                f.mkdir();

                File[] files = f.listFiles();
                if (files.length == 0) {
                    Toast.makeText(getBaseContext(), "Csv Files Directory Empty", Toast.LENGTH_SHORT).show();
                } else {
                    i = new Intent(this, HistoryActivity.class);
                    startActivity(i);
                }
                break;
            case R.id.action_save:
 //               Toast.makeText(getBaseContext(),"action_save",Toast.LENGTH_SHORT).show();
                try {
                    exportEmailInCSV();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_download:
                i = new Intent(getBaseContext(), FileListActivity.class);
                Debug.loge(new Exception(),"action_download");
                startActivityForResult(i, mFileListActivity);
                Toast.makeText(getBaseContext(),"action_download",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_configuration:
//                Toast.makeText(getBaseContext(),"action_configuration",Toast.LENGTH_SHORT).show();
                i = new Intent(getBaseContext(), SettingActivity.class);
                startActivity(i);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Debug.logi(new Exception(), "onActivityResult : " + requestCode);

        switch (requestCode) {
            case mFileListActivity:
                if (resultCode == RESULT_OK) {
                    startFirmwareDownload();
                }
                break;
            case Variables.REQUEST_CODE_LOGIN:
//                Debug.logi(new Exception(),"=dhjung=======> REQUEST_CODE_LOGIN (Main)");
                Debug.logi(new Exception(), "==> resultCode: " + resultCode);
                if (resultCode == Variables.RESULT_CODE_LOGIN_FAIL) {
                    finish();
                } else {
                    Debug.logi(new Exception(), " REQUEST_CODE_LOGIN refresh");
                    Variables.ACTIVITY_LOGIN_FLAG = true;
                    finish();
                    startActivity(getIntent()); // MainActivity 실행
                }
                break;
            default:
                //Debug.loge(new Exception(),">>> requestCode: " + requestCode);
                break;
        }
    }

    class loadProgressDialogAsync extends AsyncTask<Integer, Integer, String> {
        public loadProgressDialogAsync() {
            super();
        }

        @Override
        protected String doInBackground(Integer... params) {
            boolean flag = true;
            int cnt = 0;
            int nFlag = 0;
            while (flag) {
//                Debug.loge(new Exception(), "onCreate bbbb: "+application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle().length);
//                        if (application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle().length > 0) {
                if (application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle().length > 0) {
//                    Debug.loge(new Exception(), " length : " + application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle().length);
                    if (application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle()[0].getValue() != null) {
                        if (application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle()[0].getValue().length() > 4) {
                            Debug.logi(new Exception(), "TEST getValue :" + application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle()[0].getValue());
                            break;
                        }
                    }
//                    Debug.loge(new Exception(), " length : " + application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle().length);
                }
//                Debug.loge(new Exception(), "Knetz cnt : " + cnt);
                if (cnt >= 30) {
                    break;
                }
                try {
                    cnt++;
                    Thread.sleep(300L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            Debug.loge(new Exception(), "[Knetz] while break;");

            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading.... Please wait....");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Tab1_Alarm fragment = (Tab1_Alarm) mSectionsPagerAdapter.getFragment(0);
            fragment.doRefreshGridViewList();

            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }


    private void startFirmwareDownload() {
        new DownloadFileAsync().execute(100);
    }

    class DownloadFileAsync extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... integers) {
            while (FwDownloadPercentage < integers[0]) {

                if (FwDownloadPercentage < 0) {
                    break;
                } else {
                    publishProgress(FwDownloadPercentage);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setTitle("Firmware");
            mDialog.setMessage("DownLoading...");
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setCancelable(false);

            mDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Variables.CmdState = Variables.Cmd_STATUS_KDDI_QN;
                    dlStartCheckFlag = false;
                    FwDownloadPercentage = 0;
                }
            });

            mDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }

            if (FwDownloadPercentage < 0) {
                Toast.makeText(MainActivity.this, "Download Fail!!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Download Complete!!", Toast.LENGTH_SHORT).show();
            }

            dlStartCheckFlag = false;
            FwDownloadPercentage = 0;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mDialog.setProgress(values[0]);
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        //        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


        private int tabCount = 4;

        Fragment[] registeredFragments = new Fragment[tabCount];

        Context mContext;

        public int getTabCount() {
            return tabCount;
        }

        public void setTabCount(int tabCount) {
            this.tabCount = tabCount;
        }

        public SectionsPagerAdapter(Context mContext, FragmentManager fm) {
            super(fm);
            this.mContext = mContext;
        }

        @Override
        public int getIconResId(int index) {
//            switch (index) {
//                case 0:
//                    //return R.drawable.actionbar_alarm_96x96;
//                case 1:
//                    return R.drawable.actionbar_status_96x96;
//                case 2:
//                    return R.drawable.actionbar_setting_96x96;
//                case 3:
//                    return R.drawable.actionbar_modem_96x96;
//            }
            return 0;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            Debug.loge(new Exception(), "SectionsPagerAdapter status position : " + position);
            switch (position) {
//                case 0:
//                    return new Tab1_Alarm(mContext);
//                case 1:
//                    return new Tab2_Status(mContext);
//                case 2:
//                    return new Tab3_Setting(mContext);
//                case 3:
//                    return new Tab4_Modem(mContext);
                case 0:
                    registeredFragments[position] = new Tab1_Alarm(mContext);
//                    registeredFragments[position] = fragment;
                    return registeredFragments[position];
                case 1:
                    registeredFragments[position] = new Tab2_Status(mContext);
//                    return new Tab2_Status(mContext);
                    return registeredFragments[position];
                case 2:
                    registeredFragments[position] = new Tab3_Setting(mContext);
//                    return new Tab3_Setting(mContext);
                    return registeredFragments[position];
                case 3:
                    registeredFragments[position] = new Tab4_Modem(mContext);
//                    return new Tab4_Modem(mContext);
                    return registeredFragments[position];
//                case 4:
//                    registeredFragments[position] = new Tab5_Tsync(mContext);
////                    return new Tab5_Tsync(mContext);
//                    return registeredFragments[position];
            }
            return null;
        }

        @Override
        public int getCount() {
            return getTabCount();
        }

        public Fragment getFragment(int position) {
            return registeredFragments[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_section1);
                case 1:
                    return getString(R.string.title_section2);
                case 2:
                    return getString(R.string.title_section3);
                case 3:
//                    String section4;
//                    if (Variables.SYSTEM_MODEM_RECEIVER != Variables.SYSTEM_RECEIVER)
//                        section4 = getString(R.string.title_section4_1);
//                    else
//                        section4 = getString(R.string.title_section4_2);
//                    return section4;
//                case 4:
//                    return getString(R.string.title_section5);
                    return getString(R.string.title_section4_1);
            }
            return null;
        }

        public Drawable getIcon(int position) {
            switch (position) {
                case 0:
                    return getResources().getDrawable(R.drawable.actionbar_alarm_96x96);
                case 1:
                    return getResources().getDrawable(R.drawable.actionbar_status_96x96);
                case 2:
                    return getResources().getDrawable(R.drawable.actionbar_setting_96x96);
                case 3:
                    return getResources().getDrawable(R.drawable.actionbar_modem_96x96);
                case 4:
                    return getResources().getDrawable(R.drawable.actionbar_modem_96x96);
            }
            return null;
        }

        public int getActionBarButtonType(int position) {
            Debug.logi(new Exception(), "position : " + position);
            switch (position) {
                case 0:
                case 1:
//                    return Variables.DISPLAY_ACTIONBAR_CONFIGURATION;
//                case 1:
//                    return Variables.DISPLAY_ACTIONBAR_HISTORY | Variables.DISPLAY_ACTIONBAR_SAVE | Variables.DISPLAY_ACTIONBAR_CONFIGURATION;
                case 2:
                case 3:
                case 4:
                    return Variables.DISPLAY_ACTIONBAR_DOWNLOAD | Variables.DISPLAY_ACTIONBAR_CONFIGURATION;
 //               case 3:
 //                   return Variables.DISPLAY_ACTIONBAR_CONFIGURATION;
                default:
                    break;
            }
            return 0;
        }
    }

    public void exportEmailInCSV() throws IOException {

        String currentDateandTime = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        // Status
        final DataType[] systemInfo = subTitleNameClass.getStatusSubTitle().getSystemInformationSubTitle();
        final DataType[] psuInfo = subTitleNameClass.getStatusSubTitle().getPsuSubTitle();
        final DataType[] downDspRfInfo = subTitleNameClass.getStatusSubTitle().getDownlinkDspRfSubTitle();
        final DataType[] upDspRfInfo = subTitleNameClass.getStatusSubTitle().getUplinkDspRfSubTitle();
        final DataType[] downAmpInfo = subTitleNameClass.getStatusSubTitle().getDownlinkAmpSubTitle();
        final DataType[] upAmpInfo = subTitleNameClass.getStatusSubTitle().getUplinkAmpSubTitle();
        final DataType[] commonInfo = subTitleNameClass.getStatusSubTitle().getCommonSubTitle();

        // Modem
//        final DataType[] modemSystemInfo = subTitleNameClass.getModemSubTitle().getSystemInformationSubTitle();
//        final DataType[] modemCommonInfo = subTitleNameClass.getModemSubTitle().getCommonSubTitle();
//        final DataType[] modemDownInfo = subTitleNameClass.getModemSubTitle().getDownlinkCommonSubTitle();
//        final DataType[] modemUpInfo = subTitleNameClass.getModemSubTitle().getUplinkCommonSubTitle();

        File folder = new File(Environment.getExternalStorageDirectory() + "/knetz");

        boolean var = false;
        if (!folder.exists()) {
            var = folder.mkdir();
            if(!var){
                Debug.loge(new Exception(), "mkdir false!!!!");
            }
        }
//            System.out.println("" + var);
        String modelname = "";

        for (int i = 0; i < systemInfo.length; i++) {
            if (systemInfo[i].getId() == 0) {
                modelname = systemInfo[i].getValue();
                Debug.loge(new Exception(), "K-NETZ model name : " + modelname);
            }
        }

        if (modelname.equals("")) {
            modelname = "K-NETZ";
        }
        final String filename = modelname + "_" + currentDateandTime + ".csv";
        final String filenamepath = folder.toString() + "/" + filename;
        Debug.loge(new Exception(), filename);


        // show waiting screen
        CharSequence contentTitle = getString(R.string.app_name);
        final ProgressDialog progDailog = ProgressDialog.show(
                this, contentTitle, "Saving..............",
                true);//please wait
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(MainActivity.this, "CSV File Save Success!!", Toast.LENGTH_SHORT).show();
            }
        };

        new Thread() {
            public void run() {
            try {

                FileWriter fw = new FileWriter(filenamepath);

                fw.append(filename + "\n");

                for (int i = 0; i < systemInfo.length; i++) {
                    fw.append(systemInfo[i].getName());
                    if (i != (systemInfo.length - 1))
                        fw.append(',');
                    else
                        fw.append("\n");
                }
                for (int i = 0; i < systemInfo.length; i++) {
                    fw.append(systemInfo[i].getValue());
                    if (i != (systemInfo.length - 1))
                        fw.append(',');
                    else
                        fw.append("\n");
                }
//                for (int i = 0; i < psuInfo.length; i++) {
//                    fw.append(psuInfo[i].getName());
//                    if (i != (psuInfo.length - 1))
//                        fw.append(',');
//                    else
//                        fw.append("\n");
//                }
//                for (int i = 0; i < psuInfo.length; i++) {
//                    fw.append(psuInfo[i].getValue());
//                    if (i != (psuInfo.length - 1))
//                        fw.append(',');
//                    else
//                        fw.append("\n");
//                }


/*
                for (int i = 0; i < modemSystemInfo.length; i++) {
                    fw.append(modemSystemInfo[i].getName());
                    if (i != (modemSystemInfo.length - 1))
                        fw.append(',');
                    else
                        fw.append("\n");
                }
                for (int i = 0; i < modemSystemInfo.length; i++) {
                    fw.append(modemSystemInfo[i].getValue());
                    if (i != (modemSystemInfo.length - 1))
                        fw.append(',');
                    else
                        fw.append("\n");
                }
                for (int i = 0; i < modemCommonInfo.length; i++) {
                    fw.append(modemCommonInfo[i].getName());
                    if (i != (modemCommonInfo.length - 1))
                        fw.append(',');
                    else
                        fw.append("\n");
                }
                for (int i = 0; i < modemCommonInfo.length; i++) {
                    fw.append(modemCommonInfo[i].getValue());
                    if (i != (modemCommonInfo.length - 1))
                        fw.append(',');
                    else
                        fw.append("\n");
                }
                for (int i = 0; i < modemDownInfo.length; i++) {
                    fw.append(modemDownInfo[i].getName());
                    if (i != (modemDownInfo.length - 1))
                        fw.append(',');
                    else
                        fw.append("\n");
                }
                for (int i = 0; i < modemDownInfo.length; i++) {
                    fw.append(modemDownInfo[i].getValue());
                    if (i != (modemDownInfo.length - 1))
                        fw.append(',');
                    else
                        fw.append("\n");
                }
                for (int i = 0; i < modemUpInfo.length; i++) {
                    fw.append(modemUpInfo[i].getName());
                    if (i != (modemUpInfo.length - 1))
                        fw.append(',');
                    else
                        fw.append("\n");
                }
                for (int i = 0; i < modemUpInfo.length; i++) {
                    fw.append(modemUpInfo[i].getValue());
                    if (i != (modemUpInfo.length - 1))
                        fw.append(',');
                    else
                        fw.append("\n");
                }
*/
                fw.flush();
                fw.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(0);
            progDailog.dismiss();
            }
        }.start();
    }


    // GUI Protocol
    private static byte checkSum(byte[] src, int nLen) {
        byte ret = 0;

        for (int i = 3; i <= nLen; i++) {
            ret += src[i];
            //Debug.logd(new Exception(),"ret: " + HexDump.toHexString(ret) + " src[" + i + "]: " + HexDump.toHexString(src[i]));
        }
//        for(int i=1; i<=nLen; i++){
//            ret += src[i];
//            //Debug.logd(new Exception(),"ret: " + HexDump.toHexString(ret) + " src[" + i + "]: " + HexDump.toHexString(src[i]));
//        }

        return ret;
    }

    private static int decodePacket(byte[] aBuf, byte[] aTarget, int nPacketCnt) {

        int nCnt = 0, nLen = 0, rLen = 0, sLen = 0;
        short sCrc16 = 0, rCrc16 = 0;

//        Debug.logd(new Exception(), "========> aBuf : " + HexDump.toHexString(aBuf) + "nPacketCnt: " + nPacketCnt);

        rLen = nPacketCnt;
        aTarget[nLen++] = aBuf[nCnt++]; // Start
        for(;nCnt<rLen; nCnt++){
            if (aBuf[nCnt] == (byte) Variables.Proto_ESCAPE_MASK) {
                nCnt++;
                aBuf[nCnt] ^= (byte) Variables.Proto_EXCEPTION_MASK;
            }
            aTarget[nLen++] = aBuf[nCnt];
        }

        sLen = (aTarget[1] << 8) & 0xFF00;
        sLen |= (aTarget[2] & 0xFF);
        sCrc16 = (short)((aTarget[nLen-3] << 8) & 0xFF00);
        sCrc16 |= (short)(aTarget[nLen-2] & 0xFF);

//        Debug.logd(new Exception(), "aTarget : " + HexDump.toHexString(aTarget) + " nLen-4 :  " + (nLen - 4));

        if(sLen != (nLen - 4)) {
            Debug.logd(new Exception(), "=dhjung=======>  sLen: " + sLen + " != nLen: " + nLen);
            return 0;
        }
        rCrc16 = Crc16.fn_makeCRC16(aTarget, nLen-4);
        if(rCrc16 != sCrc16){
            Debug.logd(new Exception(), "=dhjung=======>  rCrc16: " + rCrc16 + " != sCrc16: " + sCrc16);
            return 0;
        }

        return nLen;
    }
/*
    private static int decodePacket(byte[] aBuf, byte[] aTarget, int nPacketCnt) {
        byte checkSumVal = 0;
        int nCnt = 0, nLen = 0;

        aTarget[nLen++] = aBuf[nCnt++];
        while (aBuf[nCnt] != (byte) Variables.Proto_END_PACKET) {
            if (aBuf[nCnt] == (byte) Variables.Proto_ESCAPE_MASK) {
                nCnt++;
                aBuf[nCnt] ^= (byte) Variables.Proto_EXCEPTION_MASK;
            }
            aTarget[nLen++] = aBuf[nCnt++];

            if (nCnt > nPacketCnt) break;
        }
        aTarget[nLen++] = aBuf[nCnt];

        checkSumVal = checkSum(aTarget, nLen - 3);
//        Debug.logd(new Exception(), "aTarget : " + HexDump.toHexString(aTarget) + " nLen-3 :  " + (nLen - 3));
        if (checkSumVal != aTarget[nLen - 2]) {
            Debug.logd(new Exception(), "nLen: " + nLen + " checkSumVal: " + HexDump.toHexString(checkSumVal) + " aTarget[nLen-2]: " + HexDump.toHexString(aTarget[nLen - 2]));
//            Debug.logd(new Exception(), "aBuf.length : " + aBuf.length + " aTarget : " + aTarget.length);
//            Debug.logi(new Exception(),"aBuf : " + HexDump.toHexString(aBuf)+"\naTarget : "+HexDump.toHexString(aTarget));

            return 0;
        }

        return nLen;
    }
*/
    // Start + Cmd + Type + sysType + Data + Checksum + End
    private void parseRcvData(byte[] data) {
        int nCnt = 0, nPacketCnt = 0, nRcvPacketNum = 0;
        byte bStartFlag = 0, bEndFlag = 0;
        byte[] rxDataBuf = new byte[Variables.DATA_BUFFER_SIZE];
        byte[] rxDataDecBuf = new byte[Variables.DATA_BUFFER_SIZE];
        byte[] rxData = null;

        for (nCnt = 0; nCnt < data.length; nCnt++) {
            if (data[nCnt] == (byte) Variables.Proto_START_PACKET) {
                bStartFlag = 1;
                bEndFlag = 0;
                nPacketCnt = 0;
            } else if ((data[nCnt] == (byte) Variables.Proto_END_PACKET) & (bStartFlag == 1)) {
                bEndFlag = 1;
            }
            rxDataBuf[nPacketCnt++] = data[nCnt];
        }
        //Debug.logi(new Exception(),HexDump.toHexString(data));

        if ((bStartFlag == 1) && (bEndFlag == 1) && (nPacketCnt > 0)) {
            nCnt = decodePacket(rxDataBuf, rxDataDecBuf, nPacketCnt);
            if (nCnt == 0) {
                bStartFlag = 0;
                bEndFlag = 0;
                nPacketCnt = 0;
                Variables.FlagBitCheck = 0;
            } else {
                rxData = new byte[nCnt];
                System.arraycopy(rxDataDecBuf, 0, rxData, 0, nCnt);
//                Debug.logi(new Exception(),"rcv state: " + rxData[6]);
//                switch(rxData[1] & 0xFF){
                switch (rxData[Variables.Protocol_COMMAND] & (byte) 0xFF) {
//                    case (byte) Variables.Cmd_STATUS:
//                        if (rxData[Variables.Protocol_MESSAGE_TYPE] == (byte) (Variables.Type_REPORT | Variables.Type_RESPONSE)) {
//                            Parse_StatusDataFromRepeater(rxData);
//                            Variables.FlagBitCheck |= (byte) Variables.Flag_SEND_RES;
//                        } else {
//                            Variables.FlagBitCheck &= ~(byte) Variables.Flag_SEND_RES;
//                        }
//                        break;
//
//                    case (byte) Variables.Cmd_SETTING:
//                        if ((rxData[Variables.Protocol_MESSAGE_TYPE] == (byte) (Variables.Type_SET | Variables.Type_RESPONSE)) || (rxData[7] == (Variables.Type_REPORT | Variables.Type_RESPONSE))) {
//                            Parse_SettingDataFromRepeater(rxData);
//                            Variables.FlagBitCheck |= (byte) Variables.Flag_SEND_RES;
//                        } else {
//                            Variables.FlagBitCheck &= ~(byte) Variables.Flag_SEND_RES;
//                        }
//                        break;

                    case (byte) Variables.Cmd_STATUS_KDDI_QN:
//                        Debug.logi(new Exception(),"=dhjung=======> Cmd_STATUS_KDDI_QN");
                        if (rxData[Variables.Protocol_MESSAGE_TYPE] == (byte) (Variables.Type_REPORT | Variables.Type_RESPONSE)) {
                            Parse_StatusDataFromRepeaterKddiQn(rxData);
                            Variables.FlagBitCheck |= (byte) Variables.Flag_SEND_RES;
                        } else {
                            Variables.FlagBitCheck &= ~(byte) Variables.Flag_SEND_RES;
                        }
                        break;

                    case (byte) Variables.Cmd_SETTING_KDDI_QN:
//                        Debug.logi(new Exception(),"=dhjung=======> Cmd_SETTING_KDDI_QN");
                        if ((rxData[Variables.Protocol_MESSAGE_TYPE] == (byte) (Variables.Type_SET | Variables.Type_RESPONSE)) || (rxData[7] == (Variables.Type_REPORT | Variables.Type_RESPONSE))) {
                            Parse_SettingDataFromRepeaterKddiQn(rxData);
                            Variables.FlagBitCheck |= (byte) Variables.Flag_SEND_RES;
                        } else {
                            Variables.FlagBitCheck &= ~(byte) Variables.Flag_SEND_RES;
                        }
                        break;

                    case (byte) Variables.Cmd_DSP_CONTROL:
                        if (rxData[Variables.Protocol_MESSAGE_TYPE] == (byte) (Variables.Type_SET | Variables.Type_RESPONSE)) {

                            //Debug.loge(new Exception(),"ICTLAB dspSendFlag 11 : "+dspSendFlag);
                            if (Variables.dspSendFlag) {
                                sendDspCtrlStopMsg();   // FrameInfo Clear
                                Variables.settingFlag = (byte) 0x99;
                            }
                            Variables.FlagBitCheck |= (byte) Variables.Flag_SEND_RES;
                        } else {
                            Variables.FlagBitCheck &= ~(byte) Variables.Flag_SEND_RES;
                        }
                        break;

                    case (byte) Variables.Cmd_DL_START:
                        if (rxData[Variables.Protocol_MESSAGE_TYPE] == (byte) (Variables.Type_SET | Variables.Type_RESPONSE)) {
                            Variables.FlagBitCheck |= (byte) Variables.Flag_SEND_RES;
                            if (dlStartCheckFlag == false) {
                                Variables.Dl_ExpPacketNum++;
                                dlStartCheckFlag = true;
                            }
                        } else {
                            Variables.FlagBitCheck &= ~(byte) Variables.Flag_SEND_RES;
                        }
                        break;

                    case (byte) Variables.Cmd_DL_PROGRESS:
                        nRcvPacketNum = ((rxData[Variables.Protocol_DL_PRGGRESS_PACKET_NUM_MSB] & 0xFF) << 8) | (rxData[Variables.Protocol_DL_PRGGRESS_PACKET_NUM_LSB] & 0xFF);
                        if (rxData[Variables.Protocol_MESSAGE_TYPE] == (byte) (Variables.Type_SET | Variables.Type_RESPONSE)) {
                            //Debug.logd(new Exception(),"nRcvPacketNum: " + nRcvPacketNum + ", Dl_ExpPacketNum: " + Variables.Dl_ExpPacketNum);
                            if (rxData[Variables.Protocol_DL_PRGGRESS_STATUS] == (byte) 0x01) {          // Correct Sequence
                                if (nRcvPacketNum == Variables.Dl_ExpPacketNum) {
                                    Variables.FlagBitCheck |= Variables.Flag_SEND_RES;
                                    Variables.Dl_ExpPacketNum++;
                                }
                            } else if (rxData[Variables.Protocol_DL_PRGGRESS_STATUS] == (byte) 0x03) {    // Sequence Error
                                Debug.loge(new Exception(), "Sequence Error == > nRcvPacketNum: " + nRcvPacketNum + ", Dl_ExpPacketNum: " + Variables.Dl_ExpPacketNum);
                                Variables.FlagBitCheck |= (byte) Variables.Flag_SEND_RES;
                                if (nRcvPacketNum == Variables.Dl_ExpPacketNum) {
                                    Variables.Dl_ExpPacketNum++;
                                }
                            }
                        } else {
                            Variables.FlagBitCheck &= ~(byte) Variables.Flag_SEND_RES;
                        }
                        break;

                    case (byte) Variables.Cmd_DL_END:
                        if (rxData[Variables.Protocol_MESSAGE_TYPE] == (byte) (Variables.Type_SET | Variables.Type_RESPONSE) && rxData[4] == 0x01) {
                            Variables.FlagBitCheck |= (byte) Variables.Flag_SEND_RES;
                        } else {
                            Variables.FlagBitCheck &= ~(byte) Variables.Flag_SEND_RES;
                        }
                        break;

                    case (byte) Variables.Cmd_MODEM_STATUS_KDDI_QN:
//                        Debug.logi(new Exception(),"=dhjung=======> Cmd_MODEM_STATUS_KDDI_QN");
                        if (rxData[Variables.Protocol_MESSAGE_TYPE] == (byte) (Variables.Type_REPORT | Variables.Type_RESPONSE)) {
                            Parse_ModemStatusDataFromRepeaterKddiQn(rxData);
                            Variables.FlagBitCheck |= (byte) Variables.Flag_SEND_RES;
                        } else {
                            Variables.FlagBitCheck &= ~(byte) Variables.Flag_SEND_RES;
                        }
                        break;


                    case (byte) Variables.Cmd_MODEM_SETTING_KDDI_QN:
//                        Debug.logi(new Exception(),"=dhjung=======> Cmd_MODEM_SETTING_KDDI_QN");
                        if ((rxData[Variables.Protocol_MESSAGE_TYPE] == (byte) (Variables.Type_SET | Variables.Type_RESPONSE)) || (rxData[2] == (Variables.Type_REPORT | Variables.Type_RESPONSE))) {
 //                           Parse_ModemStatusDataFromRepeater(rxData);
                            sendModemSettingStopMsg();   // FrameInfo Clear
                            Variables.modemSettingFlag = (byte) 0x99;
                            Variables.FlagBitCheck |= (byte) Variables.Flag_SEND_RES;
                        } else {
                            Variables.FlagBitCheck &= ~(byte) Variables.Flag_SEND_RES;
                        }
                        break;

//                    case (byte) Variables.Cmd_RECEIVER_STATUS:
//                        if (rxData[Variables.Protocol_MESSAGE_TYPE] == (byte) (Variables.Type_REPORT | Variables.Type_RESPONSE)) {
//                            Parse_ReceiverStatusDataFromRepeater(rxData);
//                            Variables.FlagBitCheck |= (byte) Variables.Flag_SEND_RES;
//                        } else {
//                            Variables.FlagBitCheck &= ~(byte) Variables.Flag_SEND_RES;
//                        }
//                        break;
//
//                    case (byte) Variables.Cmd_RECEIVER_SETTING:
//                        if ((rxData[Variables.Protocol_MESSAGE_TYPE] == (byte) (Variables.Type_SET | Variables.Type_RESPONSE)) || (rxData[2] == (Variables.Type_REPORT | Variables.Type_RESPONSE))) {
//                            Parse_ReceiverSettingDataFromRepeater(rxData);
//                            Variables.FlagBitCheck |= (byte) Variables.Flag_SEND_RES;
//                        } else {
//                            Variables.FlagBitCheck &= ~(byte) Variables.Flag_SEND_RES;
//                        }
//                        break;
//
//                    case (byte) Variables.Cmd_TSYNC_STATUS_CROSSWORKS:
//                        if (rxData[Variables.Protocol_MESSAGE_TYPE] == (byte) (Variables.Type_REPORT | Variables.Type_RESPONSE)) {
//                            Parse_TsyncStatusDataFromRepeater(rxData);
//                            Variables.FlagBitCheck |= (byte) Variables.Flag_SEND_RES;
//                        } else {
//                            Variables.FlagBitCheck &= ~(byte) Variables.Flag_SEND_RES;
//                        }
//                        break;
//
//                    case (byte) Variables.Cmd_TSYNC_CONTROL_CROSSWORKS:
//                        if ((rxData[Variables.Protocol_MESSAGE_TYPE] == (byte) (Variables.Type_SET | Variables.Type_RESPONSE)) || (rxData[7] == (Variables.Type_REPORT | Variables.Type_RESPONSE))) {
////                            Parse_TsyncStatusDataFromRepeater(rxData);
//                            sendTsyncSettingStopMsg();   // FrameInfo Clear
//                            Variables.tsyncSettingFlag = (byte) 0x99;
//                            Variables.FlagBitCheck |= (byte) Variables.Flag_SEND_RES;
//                        } else {
//                            Variables.FlagBitCheck &= ~(byte) Variables.Flag_SEND_RES;
//                        }
//                        break;

                    case (byte) Variables.Cmd_GUI_SETTING:
                        if (rxData[Variables.Protocol_MESSAGE_TYPE] == (byte) (Variables.Type_REPORT | Variables.Type_RESPONSE)) {
                            Parse_GuiSettingDataFromRepeater(rxData);
                            Variables.FlagBitCheck |= (byte) Variables.Flag_SEND_RES;
                        } else {
                            Variables.FlagBitCheck &= ~(byte) Variables.Flag_SEND_RES;
                        }
                        break;

                    default:
                        break;
                }
            }
        }
    }

    public static byte[] getDec(byte[] data) {
        int nCnt = 0, nPacketCnt = 0, nRcvPacketNum = 0;
        byte bStartFlag = 0, bEndFlag = 0;
        byte[] rxDataBuf = new byte[Variables.DATA_BUFFER_SIZE];
        byte[] rxDataDecBuf = new byte[Variables.DATA_BUFFER_SIZE];
        byte[] rxData = null;

        for (nCnt = 0; nCnt < data.length; nCnt++) {
//            Debug.logi(new Exception(),HexDump.toHexString(data[nCnt]));
            if (data[nCnt] == (byte) Variables.Proto_START_PACKET) {
                bStartFlag = 1;
                bEndFlag = 0;
                nPacketCnt = 0;
            } else if ((data[nCnt] == (byte) Variables.Proto_END_PACKET) & (bStartFlag == 1)) {
                bEndFlag = 1;
            }
            rxDataBuf[nPacketCnt++] = data[nCnt];
        }
//        Debug.logi(new Exception(),HexDump.toHexString(data));
//        Debug.logi(new Exception(),HexDump.toHexString(rxDataBuf));
//        Debug.logi(new Exception(),"asdasdasdasdasdasdasd : "+bStartFlag+" : "+bEndFlag+" nPacketCnt : "+nPacketCnt);
        if ((bStartFlag == 1) && (bEndFlag == 1) && (nPacketCnt > 0)) {
//            Debug.logi(new Exception(),"asdasdasdasdasdasdasd");
            nCnt = decodePacket(rxDataBuf, rxDataDecBuf, nPacketCnt);
//            Debug.logi(new Exception(),HexDump.toHexString(rxDataDecBuf));
//            Debug.logi(new Exception(),"asdasdasdasdasdasdasd111");
            if (nCnt == 0) {
                bStartFlag = 0;
                bEndFlag = 0;
                nPacketCnt = 0;
                Variables.FlagBitCheck = 0;
            } else {
                rxData = new byte[nCnt];
                System.arraycopy(rxDataDecBuf, 0, rxData, 0, nCnt);
                //Debug.logd(new Exception(),"rcv state: " + rxData[1]);
//                switch (rxData[1] & 0xFF) {
//                    case Variables.Cmd_STATUS:
//                        if (rxData[2] == (Variables.Type_REPORT | Variables.Type_RESPONSE)) {
//                            return rxData;
//                        }
//                }
//                Debug.logi(new Exception(),"count : "+nCnt);
//                for(int i=0; i < nCnt;i++)
//                    Debug.logi(new Exception(),"["+i+"] :"+(rxData[i]));

                //Debug.logi(new Exception(),HexDump.toHexString(rxData));
                return rxData;
            }
        }
        return null;
    }
//
//    private void Parse_StatusDataFromRepeater(byte[] data) {
//
//        int nCnt = Variables.Protocol_MESSAGE_START;
//        short dummy = 0;
//
//        // GUI System Category
//        if ((data[nCnt] & 0x01) == 0x01) Variables.bandStruct[Variables.band].stats.aTsyncLink1 = 1;
//        else Variables.bandStruct[Variables.band].stats.aTsyncLink1 = 0;
//        if ((data[nCnt] & 0x02) == 0x02) Variables.bandStruct[Variables.band].stats.aTsyncLink2 = 1;
//        else Variables.bandStruct[Variables.band].stats.aTsyncLink2 = 0;
//        nCnt++; // Upper byte for Alarm bit
//
//        if ((data[nCnt] & 0x01) == 0x01) Variables.bandStruct[Variables.band].stats.aTempHigh = 1;
//        else Variables.bandStruct[Variables.band].stats.aTempHigh = 0;
//        if ((data[nCnt] & 0x02) == 0x02) Variables.bandStruct[Variables.band].stats.aDoorOpen = 1;
//        else Variables.bandStruct[Variables.band].stats.aDoorOpen = 0;
//        if ((data[nCnt] & 0x08) == 0x08) Variables.bandStruct[Variables.band].stats.aTsyncLock1 = 1;
//        else Variables.bandStruct[Variables.band].stats.aTsyncLock1 = 0;
//        if ((data[nCnt] & 0x10) == 0x10) Variables.bandStruct[Variables.band].stats.aTsyncLock2 = 1;
//        else Variables.bandStruct[Variables.band].stats.aTsyncLock2 = 0;
//        if ((data[nCnt] & 0x80) == 0x80) Variables.bandStruct[Variables.band].stats.aModemLink = 1;
//        else Variables.bandStruct[Variables.band].stats.aModemLink = 0;
//        nCnt++; // Lower byte for Alarm bit
//
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sRepeaterMaker: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].stats.sRepeaterMaker = data[nCnt++];
//        nCnt += 3; // reserve
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sSupplier: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].stats.sSupplier = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sVersion: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].stats.sVersion = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sTemperature: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].stats.sTemperature = data[nCnt++];
//        nCnt += 25; // reserve
//
//        // GUI PSU Category
//        nCnt++; // Upper byte for Alarm bit
//        if ((data[nCnt] & 0x01) == 0x01) Variables.bandStruct[Variables.band].stats.aAcFail = 1;
//        else Variables.bandStruct[Variables.band].stats.aAcFail = 0;
//        if ((data[nCnt] & 0x02) == 0x02) Variables.bandStruct[Variables.band].stats.aDcFail = 1;
//        else Variables.bandStruct[Variables.band].stats.aDcFail = 0;
//        if ((data[nCnt] & 0x04) == 0x04) Variables.bandStruct[Variables.band].stats.aBatterySwOnOff = 1;
//        else Variables.bandStruct[Variables.band].stats.aBatterySwOnOff = 0;
//        if ((data[nCnt] & 0x08) == 0x08) Variables.bandStruct[Variables.band].stats.aDcInputFail = 1;
//        else Variables.bandStruct[Variables.band].stats.aDcInputFail = 0;
//        if ((data[nCnt] & 0x10) == 0x10) Variables.bandStruct[Variables.band].stats.aUpsStatus = 1;
//        else Variables.bandStruct[Variables.band].stats.aUpsStatus = 0;
//        if ((data[nCnt] & 0x20) == 0x20) Variables.bandStruct[Variables.band].stats.aLowBattery = 1;
//        else Variables.bandStruct[Variables.band].stats.aLowBattery = 0;
//        if ((data[nCnt] & 0x40) == 0x40) Variables.bandStruct[Variables.band].stats.aOnBattery = 1;
//        else Variables.bandStruct[Variables.band].stats.aOnBattery = 0;
//        if ((data[nCnt] & 0x80) == 0x80) Variables.bandStruct[Variables.band].stats.aOverCurrent = 1;
//        else Variables.bandStruct[Variables.band].stats.aOverCurrent = 0;
//        nCnt++; // Lower byte for Alarm bit
//
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sBatteryType: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].stats.sBatteryType = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sInputVoltage: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].stats.sInputVoltage = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sOutputVoltage: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].stats.sOutputVoltage = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sBatteryVoltage: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].stats.sBatteryVoltage = data[nCnt++];
//        dummy = (short)(data[nCnt++] << 8);
//        dummy |= data[nCnt++];
//        Variables.bandStruct[Variables.band].stats.sDcCurrent = dummy;
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sDcCurrent: " + dummy);
//        nCnt += 3; // reserve
//
//        // GUI DL DSP&RF Category
//        nCnt++; // Upper byte for Alarm bit
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "Dl Alarm: " + data[nCnt]);
//        if ((data[nCnt] & 0x01) == 0x01) Variables.bandStruct[Variables.band].pathStats[0].aDspLinkFail = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aDspLinkFail = 0;
//        if ((data[nCnt] & 0x02) == 0x02) Variables.bandStruct[Variables.band].pathStats[0].aIcsStatus = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aIcsStatus = 0;
//        if ((data[nCnt] & 0x04) == 0x04) Variables.bandStruct[Variables.band].pathStats[0].aIsolationLack = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aIsolationLack = 0;
//        if ((data[nCnt] & 0x08) == 0x08) Variables.bandStruct[Variables.band].pathStats[0].aRfDevicePll = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aRfDevicePll = 0;
//        if ((data[nCnt] & 0x10) == 0x10) Variables.bandStruct[Variables.band].pathStats[0].aInputLower = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aInputLower = 0;
//        if ((data[nCnt] & 0x20) == 0x20) Variables.bandStruct[Variables.band].pathStats[0].aInputUpper = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aInputUpper = 0;
//        if ((data[nCnt] & 0x40) == 0x40) Variables.bandStruct[Variables.band].pathStats[0].aOutputLower = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aOutputLower = 0;
//        if ((data[nCnt] & 0x80) == 0x80) Variables.bandStruct[Variables.band].pathStats[0].aOutputUpper = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aOutputUpper = 0;
//        nCnt++; // Lower byte for Alarm bit
//
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sIsolOscGain: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[0].sIsolOscGain = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sInputPower: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[0].sInputPower = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sCurrentAtten: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[0].sCurrentAtten = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sCurrentGain: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[0].sCurrentGain = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sAlcAtten: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[0].sAlcAtten = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sInputPowerReal: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[0].sInputPowerReal = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sAlcMinAtten: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[0].sAlcMinAtten = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sIsolMinGain: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[0].sIsolMinGain = data[nCnt++];
//        nCnt += 2; // reserve
//        dummy = (short)(data[nCnt++] << 8);
//        dummy |= data[nCnt++];
//        Variables.bandStruct[Variables.band].pathStats[0].sInputPowerX10 = dummy;
//        nCnt += 2; // reserve
//
//        // GUI UL DSP&RF Category
//        nCnt++; // Upper byte for Alarm bit
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "Ul Alarm: " + data[nCnt]);
//        if ((data[nCnt] & 0x01) == 0x01) Variables.bandStruct[Variables.band].pathStats[1].aDspLinkFail = 1;
//        else Variables.bandStruct[Variables.band].pathStats[1].aDspLinkFail = 0;
//        if ((data[nCnt] & 0x02) == 0x02) Variables.bandStruct[Variables.band].pathStats[1].aIcsStatus = 1;
//        else Variables.bandStruct[Variables.band].pathStats[1].aIcsStatus = 0;
//        if ((data[nCnt] & 0x04) == 0x04) Variables.bandStruct[Variables.band].pathStats[1].aIsolationLack = 1;
//        else Variables.bandStruct[Variables.band].pathStats[1].aIsolationLack = 0;
//        if ((data[nCnt] & 0x08) == 0x08) Variables.bandStruct[Variables.band].pathStats[1].aRfDevicePll = 1;
//        else Variables.bandStruct[Variables.band].pathStats[1].aRfDevicePll = 0;
//        if ((data[nCnt] & 0x20) == 0x20) Variables.bandStruct[Variables.band].pathStats[1].aInputUpper = 1;
//        else Variables.bandStruct[Variables.band].pathStats[1].aInputUpper = 0;
//        if ((data[nCnt] & 0x80) == 0x80) Variables.bandStruct[Variables.band].pathStats[1].aOutputUpper = 1;
//        else Variables.bandStruct[Variables.band].pathStats[1].aOutputUpper = 0;
//        nCnt++; // Lower byte for Alarm bit
//
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sIsolOscGain: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[1].sIsolOscGain = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sInputPower: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[1].sInputPower = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sCurrentAtten: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[1].sCurrentAtten = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sCurrentGain: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[1].sCurrentGain = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sAlcAtten: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[1].sAlcAtten = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sInputPowerReal: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[1].sInputPowerReal = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sAlcMinAtten: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[1].sAlcMinAtten = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sIsolMinGain: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[1].sIsolMinGain = data[nCnt++];
//        nCnt += 2; // reserve
//        dummy = (short)(data[nCnt++] << 8);
//        dummy |= data[nCnt++];
//        Variables.bandStruct[Variables.band].pathStats[1].sInputPowerX10 = dummy;
//        nCnt += 2; // reserve
//
//        // GUI DL Amp Category
//        if ((data[nCnt] & 0x01) == 0x01) Variables.bandStruct[Variables.band].pathStats[0].aLinkFail = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aLinkFail = 0;
//        nCnt++; // Upper byte for Alarm bit
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "Dl Alarm: " + data[nCnt]);
//        if ((data[nCnt] & 0x01) == 0x01) Variables.bandStruct[Variables.band].pathStats[0].aOverTemp = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aOverTemp = 0;
//        if ((data[nCnt] & 0x02) == 0x02) Variables.bandStruct[Variables.band].pathStats[0].aAmpDcFail = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aAmpDcFail = 0;
//        if ((data[nCnt] & 0x04) == 0x04) Variables.bandStruct[Variables.band].pathStats[0].aVSWR = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aVSWR = 0;
//        if ((data[nCnt] & 0x08) == 0x08) Variables.bandStruct[Variables.band].pathStats[0].aLoopFail = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aLoopFail = 0;
//        if ((data[nCnt] & 0x10) == 0x10) Variables.bandStruct[Variables.band].pathStats[0].aAmpOnoff = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aAmpOnoff = 0;
//        if ((data[nCnt] & 0x20) == 0x20) Variables.bandStruct[Variables.band].pathStats[0].aDeviceFail = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aDeviceFail = 0;
//        if ((data[nCnt] & 0x80) == 0x80) Variables.bandStruct[Variables.band].pathStats[0].aOverPower = 1;
//        else Variables.bandStruct[Variables.band].pathStats[0].aOverPower = 0;
//        nCnt++; // Lower byte for Alarm bit
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sOutputPower: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[0].sOutputPower = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sAmpMaker: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[0].sAmpMaker = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sAmpTemp: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[0].sAmpTemp = data[nCnt++];
//        dummy = (short)(data[nCnt++] << 8);
//        dummy |= data[nCnt++];
//        Variables.bandStruct[Variables.band].pathStats[0].sOutputPowerX10 = dummy;
//        nCnt += 3; // reserve
//
//        // GUI UL Amp Category
//        if ((data[nCnt] & 0x01) == 0x01) Variables.bandStruct[Variables.band].pathStats[1].aLinkFail = 1;
//        else Variables.bandStruct[Variables.band].pathStats[1].aLinkFail = 0;
//        nCnt++; // Upper byte for Alarm bit
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "Ul Alarm: " + data[nCnt]);
//        if ((data[nCnt] & 0x04) == 0x04) Variables.bandStruct[Variables.band].pathStats[1].aVSWR = 1;
//        else Variables.bandStruct[Variables.band].pathStats[1].aVSWR = 0;
//        if ((data[nCnt] & 0x10) == 0x10) Variables.bandStruct[Variables.band].pathStats[1].aAmpOnoff = 1;
//        else Variables.bandStruct[Variables.band].pathStats[1].aAmpOnoff = 0;
//        if ((data[nCnt] & 0x20) == 0x20) Variables.bandStruct[Variables.band].pathStats[1].aDeviceFail = 1;
//        else Variables.bandStruct[Variables.band].pathStats[1].aDeviceFail = 0;
//        if ((data[nCnt] & 0x80) == 0x80) Variables.bandStruct[Variables.band].pathStats[1].aOverPower = 1;
//        else Variables.bandStruct[Variables.band].pathStats[1].aOverPower = 0;
//        nCnt++; // Lower byte for Alarm bit
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sOutputPower: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[1].sOutputPower = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "sAmpMaker: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].pathStats[1].sAmpMaker = data[nCnt++];
//        dummy = (short)(data[nCnt++] << 8);
//        dummy |= data[nCnt++];
//        Variables.bandStruct[Variables.band].pathStats[1].sOutputPowerX10 = dummy;
//        nCnt += 3; // reserve
//
//        Update_AlarmTab();
//        Update_StatusTab();
//    }


    private void Parse_StatusDataFromRepeaterKddiQn(byte[] data) {

        int nCnt = Variables.Protocol_MESSAGE_START;
        short dummy = 0;

//        Debug.loge(new Exception(),"=dhjung=======> Variables.band : " + Variables.band);

        // GUI System Category
        nCnt++; // Upper byte for Alarm bit

        if ((data[nCnt] & 0x01) == 0x01) Variables.bandStruct[Variables.band].stats.aTempHigh = 1;
        else Variables.bandStruct[Variables.band].stats.aTempHigh = 0;
        if ((data[nCnt] & 0x80) == 0x80) Variables.bandStruct[Variables.band].stats.aModemLink = 1; // modem link는 1이 연결
        else Variables.bandStruct[Variables.band].stats.aModemLink = 0;
        nCnt++; // Lower byte for Alarm bit

//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sVersion: " + data[nCnt]);
        Variables.bandStruct[Variables.band].stats.sVersion = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sTemperature: " + data[nCnt]);
        Variables.bandStruct[Variables.band].stats.sTemperature = data[nCnt++];
        nCnt += 19; // reserve
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sCpuUsage: " + data[nCnt]);
        Variables.bandStruct[Variables.band].stats.sCpuUsage = data[nCnt++];

        // GUI PSU Category
        nCnt++; // Upper byte for Alarm bit
        if ((data[nCnt] & 0x01) == 0x01) Variables.bandStruct[Variables.band].stats.aAcFail = 1;
        else Variables.bandStruct[Variables.band].stats.aAcFail = 0;
        if ((data[nCnt] & 0x02) == 0x02) Variables.bandStruct[Variables.band].stats.aDcFail = 1;
        else Variables.bandStruct[Variables.band].stats.aDcFail = 0;
        if ((data[nCnt] & 0x04) == 0x04) Variables.bandStruct[Variables.band].stats.aBatterySwOnOff = 1;
        else Variables.bandStruct[Variables.band].stats.aBatterySwOnOff = 0;
        if ((data[nCnt] & 0x08) == 0x08) Variables.bandStruct[Variables.band].stats.aDcInputFail = 1;
        else Variables.bandStruct[Variables.band].stats.aDcInputFail = 0;
        if ((data[nCnt] & 0x10) == 0x10) Variables.bandStruct[Variables.band].stats.aUpsStatus = 1;
        else Variables.bandStruct[Variables.band].stats.aUpsStatus = 0;
        if ((data[nCnt] & 0x20) == 0x20) Variables.bandStruct[Variables.band].stats.aLowBattery = 1;
        else Variables.bandStruct[Variables.band].stats.aLowBattery = 0;
        if ((data[nCnt] & 0x40) == 0x40) Variables.bandStruct[Variables.band].stats.aOnBattery = 1;
        else Variables.bandStruct[Variables.band].stats.aOnBattery = 0;
        if ((data[nCnt] & 0x80) == 0x80) Variables.bandStruct[Variables.band].stats.aOverCurrent = 1;
        else Variables.bandStruct[Variables.band].stats.aOverCurrent = 0;
        nCnt++; // Lower byte for Alarm bit

//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sBatteryType: " + data[nCnt]);
        Variables.bandStruct[Variables.band].stats.sBatteryType = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sInputVoltage: " + data[nCnt]);
        Variables.bandStruct[Variables.band].stats.sInputVoltage = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sOutputVoltage: " + data[nCnt]);
        Variables.bandStruct[Variables.band].stats.sOutputVoltage = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sBatteryVoltage: " + data[nCnt]);
        Variables.bandStruct[Variables.band].stats.sBatteryVoltage = data[nCnt++];

        dummy = (short)((data[nCnt++] << 8) & 0xFF00);
        dummy |= (short)(data[nCnt++]&0xFF);
        Variables.bandStruct[Variables.band].stats.sDcCurrent = dummy;
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sDcCurrent: " + dummy);
        nCnt += 4; // reserve

        // GUI DL DSP&RF Category
        nCnt++; // Upper byte for Alarm bit
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "Dl Alarm: " + data[nCnt]);
        if ((data[nCnt] & 0x01) == 0x01) Variables.bandStruct[Variables.band].pathStats[0].aDspLinkFail = 1;
        else Variables.bandStruct[Variables.band].pathStats[0].aDspLinkFail = 0;
        if ((data[nCnt] & 0x02) == 0x02) Variables.bandStruct[Variables.band].pathStats[0].aIcsStatus = 1;
        else Variables.bandStruct[Variables.band].pathStats[0].aIcsStatus = 0;
        if ((data[nCnt] & 0x08) == 0x08) Variables.bandStruct[Variables.band].pathStats[0].aRfDevicePll = 1;
        else Variables.bandStruct[Variables.band].pathStats[0].aRfDevicePll = 0;
        if ((data[nCnt] & 0x20) == 0x20) Variables.bandStruct[Variables.band].pathStats[0].aInputUpper = 1;
        else Variables.bandStruct[Variables.band].pathStats[0].aInputUpper = 0;
        if ((data[nCnt] & 0x80) == 0x80) Variables.bandStruct[Variables.band].pathStats[0].aOutputUpper = 1;
        else Variables.bandStruct[Variables.band].pathStats[0].aOutputUpper = 0;
        nCnt++; // Lower byte for Alarm bit

//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sIsolOscGain: " + data[nCnt]);
        Variables.bandStruct[Variables.band].pathStats[0].sIsolOscGain = data[nCnt++];

        dummy = (short)((data[nCnt++] << 8) & 0xFF00);
        dummy |= (short)(data[nCnt++]&0xFF);
        Variables.bandStruct[Variables.band].pathStats[0].sInputPower = dummy;
//        Debug.logi(new Exception(),"=dhjung=======> sInputPower : " +Variables.bandStruct[Variables.band].pathStats[0].sInputPower + ", " + Variables.band + ": " + dummy);

        nCnt += 2;  // input power real
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sCurrentGain: " + data[nCnt]);
        Variables.bandStruct[Variables.band].pathStats[0].sCurrentGain = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sCurrentAtten: " + data[nCnt]);
        Variables.bandStruct[Variables.band].pathStats[0].sCurrentAtten = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sAlcAtten: " + data[nCnt]);
        Variables.bandStruct[Variables.band].pathStats[0].sAlcAtten = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sAlcMinAtten: " + data[nCnt]);
        Variables.bandStruct[Variables.band].pathStats[0].sAlcMinAtten = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sIsolMinGain: " + data[nCnt]);
        Variables.bandStruct[Variables.band].pathStats[0].sIsolMinGain = data[nCnt++];
        nCnt += 16; // reserve

        // GUI UL DSP&RF Category
        nCnt++; // Upper byte for Alarm bit
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "Ul Alarm: " + data[nCnt]);
        if ((data[nCnt] & 0x01) == 0x01) Variables.bandStruct[Variables.band].pathStats[1].aDspLinkFail = 1;
        else Variables.bandStruct[Variables.band].pathStats[1].aDspLinkFail = 0;
        if ((data[nCnt] & 0x02) == 0x02) Variables.bandStruct[Variables.band].pathStats[1].aIcsStatus = 1;
        else Variables.bandStruct[Variables.band].pathStats[1].aIcsStatus = 0;
        if ((data[nCnt] & 0x08) == 0x08) Variables.bandStruct[Variables.band].pathStats[1].aRfDevicePll = 1;
        else Variables.bandStruct[Variables.band].pathStats[1].aRfDevicePll = 0;
        if ((data[nCnt] & 0x10) == 0x10) Variables.bandStruct[Variables.band].pathStats[1].aSleepStatus = 1;
        else Variables.bandStruct[Variables.band].pathStats[1].aSleepStatus = 0;
        if ((data[nCnt] & 0x20) == 0x20) Variables.bandStruct[Variables.band].pathStats[1].aInputUpper = 1;
        else Variables.bandStruct[Variables.band].pathStats[1].aInputUpper = 0;
        if ((data[nCnt] & 0x80) == 0x80) Variables.bandStruct[Variables.band].pathStats[1].aOutputUpper = 1;
        else Variables.bandStruct[Variables.band].pathStats[1].aOutputUpper = 0;
        nCnt++; // Lower byte for Alarm bit

//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sIsolOscGain: " + data[nCnt]);
        Variables.bandStruct[Variables.band].pathStats[1].sIsolOscGain = data[nCnt++];

        dummy = (short)((data[nCnt++] << 8) & 0xFF00);
        dummy |= (short)(data[nCnt++]&0xFF);
        Variables.bandStruct[Variables.band].pathStats[1].sInputPower = dummy;
        nCnt += 2;  // input power real
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sCurrentGain: " + data[nCnt]);
        Variables.bandStruct[Variables.band].pathStats[1].sCurrentGain = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sCurrentAtten: " + data[nCnt]);
        Variables.bandStruct[Variables.band].pathStats[1].sCurrentAtten = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sAlcAtten: " + data[nCnt]);
        Variables.bandStruct[Variables.band].pathStats[1].sAlcAtten = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sAlcMinAtten: " + data[nCnt]);
        Variables.bandStruct[Variables.band].pathStats[1].sAlcMinAtten = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sIsolMinGain: " + data[nCnt]);
        Variables.bandStruct[Variables.band].pathStats[1].sIsolMinGain = data[nCnt++];
        nCnt += 16; // reserve

        // GUI DL Amp Category
        nCnt++; // Upper byte for Alarm bit
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "Dl Alarm: " + data[nCnt]);
        if ((data[nCnt] & 0x10) == 0x10) Variables.bandStruct[Variables.band].pathStats[0].aAmpOnoff = 1;
        else Variables.bandStruct[Variables.band].pathStats[0].aAmpOnoff = 0;
        nCnt++; // Lower byte for Alarm bit

        dummy = (short)((data[nCnt++] << 8) & 0xFF00);
        dummy |= (short)(data[nCnt++]&0xFF);
        Variables.bandStruct[Variables.band].pathStats[0].sOutputPowerX10 = dummy;
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "DL sOutputPowerX10: " + dummy);
        nCnt++; // amp maker
        nCnt++; // amp temperature

        dummy = (short)((data[nCnt++] << 8) & 0xFF00);
        dummy |= (short)(data[nCnt++]&0xFF);
        Variables.bandStruct[Variables.band].pathStats[0].sOutputPowerX10Sum = dummy;
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "DL sOutputPowerX10Sum: " + dummy);
        nCnt += 8; // reserve

        // GUI UL Amp Category
        nCnt++; // Upper byte for Alarm bit
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "Ul Alarm: " + data[nCnt]);
        if ((data[nCnt] & 0x10) == 0x10) Variables.bandStruct[Variables.band].pathStats[1].aAmpOnoff = 1;
        else Variables.bandStruct[Variables.band].pathStats[1].aAmpOnoff = 0;
        nCnt++; // Lower byte for Alarm bit

        dummy = (short)((data[nCnt++] << 8) & 0xFF00);
        dummy |= (short)(data[nCnt++]&0xFF);
        Variables.bandStruct[Variables.band].pathStats[1].sOutputPowerX10 = dummy;
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "UL sOutputPowerX10: " + dummy);
        nCnt++; // amp maker

        dummy = (short)((data[nCnt++] << 8) & 0xFF00);
        dummy |= (short)(data[nCnt++]&0xFF);
        Variables.bandStruct[Variables.band].pathStats[1].sOutputPowerX10Sum = dummy;
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "UL sOutputPowerX10Sum: " + dummy);
        nCnt += 9; // reserve

        Update_AlarmTab();
        Update_StatusTab();
    }

    private void Parse_SettingDataFromRepeaterKddiQn(byte[] data) {

        int nCnt = Variables.Protocol_MESSAGE_START;
        short dummy = 0;

        // GUI System Category
//        Debug.loge(new Exception(),"K-NETZ settingSendFlag11 : "+settingSendFlag);
        if (Variables.settingSendFlag) {
            sendSettingStopMsg();   // FrameInfo Clear
//            Debug.logi(new Exception(),"=dhjung=======> Parse_SettingDataFromRepeater stop");
            settingUpdateFlag = true;
        }
        else {
            nCnt += 2;    // Information (0x00, 0x7F Fixed!!)

//        Debug.loge(new Exception(),"[" + nCnt + "]" + "tTempUpper: " + data[nCnt]);
            Variables.bandStruct[Variables.band].sets.tTempUpper = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "Alarm Sum: " + data[nCnt]);
            if ((data[nCnt] & 0x01) == 0x01)
                Variables.bandStruct[Variables.band].sets.tAutoShutdown = 1;
            else Variables.bandStruct[Variables.band].sets.tAutoShutdown = 0;
            if ((data[nCnt] & 0x02) == 0x02)
                Variables.bandStruct[Variables.band].sets.tAutoRecovery = 1;
            else Variables.bandStruct[Variables.band].sets.tAutoRecovery = 0;
            if ((data[nCnt] & 0x04) == 0x04)
                Variables.bandStruct[Variables.band].sets.tSleepMode = 1;
            else Variables.bandStruct[Variables.band].sets.tSleepMode = 0;
            if ((data[nCnt] & 0x08) == 0x08) Variables.bandStruct[Variables.band].sets.tILC = 1;
            else Variables.bandStruct[Variables.band].sets.tILC = 0;
            nCnt++;
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "tCellSearch: " + data[nCnt]);
            Variables.bandStruct[Variables.band].sets.tCellSearch = data[nCnt++];

            if (data[nCnt] == 0) {
                Variables.bandStruct[Variables.band].sets.tFreqSelectAutoManual = 0;
                Variables.bandStruct[Variables.band].sets.tFreqSelect10M15M = 0;
            } else {
                if ((data[nCnt] & 0x01) == 0x01) {
                    Variables.bandStruct[Variables.band].sets.tFreqSelectAutoManual = 1;
                } else if ((data[nCnt] & 0x02) == 0x02) {
                    Variables.bandStruct[Variables.band].sets.tFreqSelect10M15M = 2;
                }
            }
            nCnt++;
            nCnt += 12;

            // GUI DL DSP&RF Category
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "tAtten: " + data[nCnt]);
            Variables.bandStruct[Variables.band].pathSets[0].tAtten = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "tIcsMode: " + data[nCnt]);
            Variables.bandStruct[Variables.band].pathSets[0].tIcsMode = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "tSystemGain: " + data[nCnt]);
            Variables.bandStruct[Variables.band].pathSets[0].tSystemGain = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "tAgcOffset: " + data[nCnt]);
            Variables.bandStruct[Variables.band].pathSets[0].tAgcOffset = data[nCnt++];
            dummy = (short)((data[nCnt++] << 8) & 0xFF00);
            dummy |= (short)(data[nCnt++]&0xFF);
            Variables.bandStruct[Variables.band].pathSets[0].tOutputUpper = dummy;
//            Debug.loge(new Exception(),"[" + nCnt + "]" + "tOutputUpper: " + dummy);
            dummy = (short)((data[nCnt++] << 8) & 0xFF00);
            dummy |= (short)(data[nCnt++]&0xFF);
            Variables.bandStruct[Variables.band].pathSets[0].tOutputLower = dummy;
//            Debug.loge(new Exception(),"[" + nCnt + "]" + "tOutputLower: " + dummy);
            dummy = (short)((data[nCnt++] << 8) & 0xFF00);
            dummy |= (short)(data[nCnt++]&0xFF);
            Variables.bandStruct[Variables.band].pathSets[0].tInputUpper = dummy;
//            Debug.loge(new Exception(),"[" + nCnt + "]" + "tInputUpper: " + dummy);
            dummy = (short)((data[nCnt++] << 8) & 0xFF00);
            dummy |= (short)(data[nCnt++]&0xFF);
            Variables.bandStruct[Variables.band].pathSets[0].tInputLower = dummy;
//            Debug.loge(new Exception(),"[" + nCnt + "]" + "tInputLower: " + dummy);
            Variables.bandStruct[Variables.band].pathSets[0].tIcsOff = data[nCnt++];
            nCnt += 11; // reserve

            // GUI UL DSP&RF Category
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "tAtten: " + data[nCnt]);
            Variables.bandStruct[Variables.band].pathSets[1].tAtten = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "tIcsMode: " + data[nCnt]);
            Variables.bandStruct[Variables.band].pathSets[1].tIcsMode = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "tSystemGain: " + data[nCnt]);
            Variables.bandStruct[Variables.band].pathSets[1].tSystemGain = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "tBalanceOffset: " + data[nCnt]);
            Variables.bandStruct[Variables.band].pathSets[1].tAgcOffset = data[nCnt++]; // tBalanceOffset
            dummy = (short)((data[nCnt++] << 8) & 0xFF00);
            dummy |= (short)(data[nCnt++]&0xFF);
            Variables.bandStruct[Variables.band].pathSets[1].tOutputUpper = dummy;
//            Debug.loge(new Exception(),"[" + nCnt + "]" + "tOutputUpper: " + dummy);
            dummy = (short)((data[nCnt++] << 8) & 0xFF00);
            dummy |= (short)(data[nCnt++]&0xFF);
            Variables.bandStruct[Variables.band].pathSets[1].tInputUpper = dummy;
//            Debug.loge(new Exception(),"[" + nCnt + "]" + "tInputUpper: " + dummy);
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "tPathSleepLevel: " + data[nCnt]);
            Variables.bandStruct[Variables.band].pathSets[1].tPathSleepLevel = data[nCnt++];
            Variables.bandStruct[Variables.band].pathSets[1].tIcsOff = data[nCnt++];
            nCnt += 14;

            // GUI Service FA
            nCnt += 2;
            Variables.bandStruct[Variables.band].sets.tRfPathOn = data[nCnt++];
            nCnt += 3;  // UL FA
            nCnt++;     // FA Allocation
            nCnt++;     // Band Select
            nCnt += 8; // reserve

            // GUI DL Amp
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "Dl Amp: " + data[nCnt]);
            Variables.bandStruct[Variables.band].pathSets[0].tAmpOnOff = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "Dl ALC: " + data[nCnt]);
            Variables.bandStruct[Variables.band].pathSets[0].tAlcOnOff = data[nCnt++];
            nCnt += 10;

            // GUI UL Amp
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "Ul Amp: " + data[nCnt]);
            Variables.bandStruct[Variables.band].pathSets[1].tAmpOnOff = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "Ul ALC: " + data[nCnt]);
            Variables.bandStruct[Variables.band].pathSets[1].tAlcOnOff = data[nCnt++];
            nCnt += 10;

            // GUI Common
            System.arraycopy(data, nCnt, Variables.bandStruct[Variables.band].sets.tSerialNum, 0, 20);
//            Debug.loge(new Exception(),"[" + nCnt + "]" + "tSerialNum: " + Arrays.toString(Variables.bandStruct[Variables.band].sets.tSerialNum));
            nCnt += 20;
            System.arraycopy(data, nCnt, Variables.bandStruct[Variables.band].sets.tModelName, 0, 20);
//            Debug.loge(new Exception(),"[" + nCnt + "]" + "tModelName: " + Arrays.toString(Variables.bandStruct[Variables.band].sets.tModelName));
            nCnt += 20;
            System.arraycopy(data, nCnt, Variables.bandStruct[Variables.band].sets.tOperatorName, 0, 20);
//            Debug.loge(new Exception(),"[" + nCnt + "]" + "tOperatorName: " + Arrays.toString(Variables.bandStruct[Variables.band].sets.tOperatorName));
            nCnt += 20;
            System.arraycopy(data, nCnt, Variables.bandStruct[Variables.band].sets.tSupplierName, 0, 20);
//            Debug.loge(new Exception(),"[" + nCnt + "]" + "tSupplierName: " + Arrays.toString(Variables.bandStruct[Variables.band].sets.tSupplierName));
            nCnt += 20;
            System.arraycopy(data, nCnt, Variables.bandStruct[Variables.band].sets.tInstallAddr, 0, 100);
//            Debug.loge(new Exception(),"[" + nCnt + "]" + "tInstallAddr: " + Arrays.toString(Variables.bandStruct[Variables.band].sets.tInstallAddr));
            nCnt += 100;
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "tPowerMode: " + data[nCnt]);
            Variables.bandStruct[Variables.band].sets.tPowerMode = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "tServiceBand: " + data[nCnt]);
            Variables.bandStruct[Variables.band].sets.tServiceBand = data[nCnt++];
            nCnt += 14;

        }
        Update_SettingTab();
//        Debug.loge(new Exception(),"K-NETZ settingSendFlag22 : "+settingSendFlag);
        if (settingUpdateFlag) {
            Variables.settingFlag = (byte) 0x99;
            settingUpdateFlag = false;
        }
    }


    private void Parse_ModemStatusDataFromRepeaterKddiQn(byte[] data)
    {
        int i, nCnt = Variables.Protocol_MESSAGE_START;
        short dummy = 0;

        // Modem Param
        nCnt++; // Upper byte for Alarm bit
        if ((data[nCnt] & 0x01) == 0x01) Variables.modemStruct.modem.aMoLink = 1;
        else Variables.modemStruct.modem.aMoLink = 0;
        nCnt++;

        System.arraycopy(data, nCnt, Variables.modemStruct.modem.pMoModel, 0, 16);
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "pMoModel: " + ByteUtil.toHexString(Variables.modemStruct.modem.pMoModel));
        nCnt += 16;
        System.arraycopy(data, nCnt, Variables.modemStruct.modem.pMoVer, 0, 16);
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "pMoVer: " + ByteUtil.toHexString(Variables.modemStruct.modem.pMoVer));
        nCnt += 16;
        System.arraycopy(data, nCnt, Variables.modemStruct.modem.pUsimId, 0, 22);
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "pUsimId: " + ByteUtil.toHexString(Variables.modemStruct.modem.pUsimId));
        nCnt += 22;
        System.arraycopy(data, nCnt, Variables.modemStruct.modem.pLocalTime, 0, 6);
//        Debug.loge(new Exception(), "[" + nCnt + "]" + "pLocalTime: " + ByteUtil.toHexString(Variables.modemStruct.modem.pLocalTime));
        nCnt += 6;
        System.arraycopy(data, nCnt, Variables.modemStruct.modem.pLocalIpAddr, 0, 50);
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "pLocalIpAddr: " + ByteUtil.toHexString(Variables.modemStruct.modem.pLocalIpAddr));
        nCnt += 50;
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "pUsimStatue: " + data[nCnt]);
        Variables.modemStruct.modem.pUsimStatue = data[nCnt++];

        nCnt += 9; // reserved

        // Cell Info x4
        for(i=0; i<4; i++) {
//            Debug.loge(new Exception(), "[" + nCnt + "]" + "cRegi: " + data[nCnt]);
            Variables.modemStruct.modem.cRegi[i] = data[nCnt++];
            System.arraycopy(data, nCnt, Variables.modemStruct.modem.cPlmn[i], 0, 10);
//            Debug.loge(new Exception(),"[" + nCnt + "]" + "cPlmn: " + ByteUtil.toHexString(Variables.modemStruct.modem.cPlmn[i]));
            nCnt += 10;

            dummy = (short)((data[nCnt++] << 8) & 0xFF00);
            dummy |= (short)(data[nCnt++]&0xFF);
            Variables.modemStruct.modem.cEarfcn[i] = dummy;

//            Debug.loge(new Exception(), "[" + nCnt + "]" + "cBandwidth: " + data[nCnt]);
            Variables.modemStruct.modem.cBandwidth[i] = data[nCnt++];

            dummy = (short)((data[nCnt++] << 8) & 0xFF00);
            dummy |= (short)(data[nCnt++]&0xFF);
            Variables.modemStruct.modem.cPci[i] = dummy;

//            Debug.loge(new Exception(), "[" + nCnt + "]" + "cRssi: " + data[nCnt]);
            Variables.modemStruct.modem.cRssi[i] = data[nCnt++];
//            Debug.loge(new Exception(), "[" + nCnt + "]" + "cRsrp: " + data[nCnt]);
            Variables.modemStruct.modem.cRsrp[i] = data[nCnt++];
//            Debug.loge(new Exception(), "[" + nCnt + "]" + "cRsrq: " + data[nCnt]);
            Variables.modemStruct.modem.cRsrq[i] = data[nCnt++];
//            Debug.loge(new Exception(), "[" + nCnt + "]" + "cCpich: " + data[nCnt]);
            Variables.modemStruct.modem.cCpich[i] = data[nCnt++];

            nCnt += 12; // reserved
        }

        if (Variables.modemSendFlag) {
            sendModemSettingStopMsg();   // FrameInfo Clear
            modemUpdateFlag = true;
        }

        // Information
        Variables.mtFrameInfo = data[nCnt++];

        // Remote
        System.arraycopy(data, nCnt, Variables.modemStruct.modem.rLocalPhoneNum, 0, 16);
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "rLocalPhoneNum: " + ByteUtil.toHexString(Variables.modemStruct.modem.rLocalPhoneNum));
        nCnt += 16;
        System.arraycopy(data, nCnt, Variables.modemStruct.modem.rRcsPhoneNum, 0, 16);
//        Debug.loge(new Exception(), "[" + nCnt + "]" + "rRcsPhoneNum: " + ByteUtil.toHexString(Variables.modemStruct.modem.rRcsPhoneNum));
        nCnt += 16;
        System.arraycopy(data, nCnt, Variables.modemStruct.modem.rPeriodicReport, 0, 3);
//        Debug.loge(new Exception(), "[" + nCnt + "]" + "rPeriodicReport: " + ByteUtil.toHexString(Variables.modemStruct.modem.rPeriodicReport));
        nCnt += 3;

        Variables.modemStruct.modem.rAutoResetTime = data[nCnt++];

        nCnt += 16; // reserved

        // SIM
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sLoopback: " + data[nCnt]);
        Variables.modemStruct.modem.sLoopback = data[nCnt++];
//        Debug.loge(new Exception(),"[" + nCnt + "]" + "sPinLock: " + data[nCnt]);
        Variables.modemStruct.modem.sPinLock = data[nCnt++];

        System.arraycopy(data, nCnt, Variables.modemStruct.modem.sPinPassword, 0, 4);
//        Debug.loge(new Exception(), "[" + nCnt + "]" + "rPeriodicReport: " + ByteUtil.toHexString(Variables.modemStruct.modem.sPinPassword));
        nCnt += 4;

        nCnt += 10; // reserved

        // Network
        System.arraycopy(data, nCnt, Variables.modemStruct.modem.nApn, 0, 30);
//        Debug.loge(new Exception(), "[" + nCnt + "]" + "nApn: " + ByteUtil.toHexString(Variables.modemStruct.modem.nApn));
        nCnt += 30;
        System.arraycopy(data, nCnt, Variables.modemStruct.modem.nUserId, 0, 30);
//        Debug.loge(new Exception(), "[" + nCnt + "]" + "nUserId: " + ByteUtil.toHexString(Variables.modemStruct.modem.nUserId));
        nCnt += 30;
        System.arraycopy(data, nCnt, Variables.modemStruct.modem.nPassword, 0, 20);
//        Debug.loge(new Exception(), "[" + nCnt + "]" + "nPassword: " + ByteUtil.toHexString(Variables.modemStruct.modem.nPassword));
        nCnt += 20;

        nCnt += 10; // reserved

        // EMS
        for(i=0; i<10; i++){
            System.arraycopy(data, nCnt, Variables.modemStruct.modem.eRcsIpAddr[i], 0, 50);
//            Debug.loge(new Exception(), "[" + nCnt + "]" + "eRcsIpAddr: " + ByteUtil.toHexString(Variables.modemStruct.modem.eRcsIpAddr[i]));
            nCnt += 50;
        }
        dummy = (short)((data[nCnt++] << 8) & 0xFF00);
        dummy |= (short)(data[nCnt++]&0xFF);
        Variables.modemStruct.modem.eRcsPort = dummy;

        nCnt += 10; // reserved

        Update_ModemTab();
        if (modemUpdateFlag) {
            Variables.modemSettingFlag = (byte) 0x99;
            modemUpdateFlag = false;
        }
    }


//    private void Parse_ReceiverStatusDataFromRepeater(byte[] data) {
////        int nCnt = 4;
//        int nCnt = Variables.Protocol_MESSAGE_START;
//
//        // Receiver Info
//        nCnt++; // Upper byte for Status bit
//        if ((data[nCnt] & 0x01) == 0x01) Variables.rsReceiveLink = 1;
//        else Variables.rsReceiveLink = 0;
//        nCnt++;
//
//        Variables.rsReceiveMaker = data[nCnt++];
//        Variables.rsReceiveType = data[nCnt++];
//
//        System.arraycopy(data, nCnt, Variables.rsReceiveVersion, 0, 12);
//        nCnt += 12;
//        nCnt += 16; // Receiver Phone Number
//        nCnt++;     // Service Status
//        nCnt += 10;
//
//        // RF Basic
//        nCnt += 2; // Status byte
//        nCnt++;   // Tx Power
//
//        Variables.rsBestRSSI = data[nCnt++];
//        Variables.rsBestRSCP = data[nCnt++];
//        nCnt += 2; // Block Error Rate
//        Variables.rsPathLoss[0] = data[nCnt++];
//        Variables.rsPathLoss[1] = data[nCnt++];
//        Variables.rsPathLoss[2] = data[nCnt++];
//        Variables.rsPathLoss[3] = data[nCnt++];
//
//        // BCH
//        nCnt += 2; // Status byte
//        System.arraycopy(data, nCnt, Variables.rsActivePLMN, 0, 6);
//        nCnt += 6;
//        System.arraycopy(data, nCnt, Variables.rsActiveChannel, 0, 2);
//        nCnt += 2;
//        System.arraycopy(data, nCnt, Variables.rsPLMNID[0], 0, 6);
//        nCnt += 6;
//        System.arraycopy(data, nCnt, Variables.rsChannel[0], 0, 2);
//        nCnt += 2;
//        System.arraycopy(data, nCnt, Variables.rsChannel[1], 0, 2);
//        nCnt += 2;
//        System.arraycopy(data, nCnt, Variables.rsChannel[2], 0, 2);
//        nCnt += 2;
//        System.arraycopy(data, nCnt, Variables.rsChannel[3], 0, 2);
//        nCnt += 2;
//        System.arraycopy(data, nCnt, Variables.rsPLMNID[1], 0, 6);
//        nCnt += 6;
//        nCnt += 8;
//        System.arraycopy(data, nCnt, Variables.rsPLMNID[2], 0, 6);
//        nCnt += 6;
//        nCnt += 8;
//        System.arraycopy(data, nCnt, Variables.rsPLMNID[3], 0, 6);
//        nCnt += 6;
//        nCnt += 8;
//
//        // P-SCH
//        nCnt += 2; // Status byte
//        System.arraycopy(data, nCnt, Variables.rsBestPathLoss, 0, 2);
//        nCnt += 2;
//        System.arraycopy(data, nCnt, Variables.rsPSC[0], 0, 2);
//        nCnt += 2;
//        Variables.rsRSCP[0] = data[nCnt++];
//        Variables.rsPSCEcIo[0] = data[nCnt++];
//        Variables.rsCPICHPower[0] = data[nCnt++];
//        System.arraycopy(data, nCnt, Variables.rsPSC[1], 0, 2);
//        nCnt += 2;
//        Variables.rsRSCP[1] = data[nCnt++];
//        Variables.rsPSCEcIo[1] = data[nCnt++];
//        Variables.rsCPICHPower[1] = data[nCnt++];
//        System.arraycopy(data, nCnt, Variables.rsPSC[2], 0, 2);
//        nCnt += 2;
//        Variables.rsRSCP[2] = data[nCnt++];
//        Variables.rsPSCEcIo[2] = data[nCnt++];
//        Variables.rsCPICHPower[2] = data[nCnt++];
//        System.arraycopy(data, nCnt, Variables.rsPSC[3], 0, 2);
//        nCnt += 2;
//        Variables.rsRSCP[3] = data[nCnt++];
//        Variables.rsPSCEcIo[3] = data[nCnt++];
//        Variables.rsCPICHPower[3] = data[nCnt++];
//
//        //       Update_ReceiverTab();
//
//    }
//
//    private void Parse_ReceiverSettingDataFromRepeater(byte[] data) {
////        int nCnt = 4;
//        int nCnt = Variables.Protocol_MESSAGE_START;
//
//        //Debug.loge(new Exception(),"ICTLAB receiverSendFlag 11 : "+receiverSendFlag);
//        if (Variables.receiverSendFlag) {
//            sendReceiverSettingStopMsg();   // FrameInfo Clear
//            receiverUpdateFlag = true;
//        }
//
//
//        Variables.rtFrameInfo = data[nCnt++];
//
//        // Receiver Info
//        nCnt += 2;  // Flag
//        nCnt++;     // Download On/Off
//        nCnt++;     // Reset Cycle
//        nCnt += 9;
//
//        // RF Basic
//        nCnt += 2;  // Flag
//        nCnt++;     // TxUpper
//        nCnt++;     // TxLower
//        nCnt++;     // RxUpper
//        nCnt++;     // RxLower
//        Variables.rtPathLossRef = data[nCnt++];
//        nCnt += 5;
//
//        // BCH
//        nCnt += 2;  // Flag
//        nCnt++;     // Mode
//        nCnt += 6;  // Select PLMN
//        nCnt += 2;  // Select Channel
//        nCnt += 6;
//
//        // P-SCH
//        nCnt += 2;  // Flag
//        nCnt++;     // Mode
//        nCnt += 2;  // Select PSC
//        nCnt++;     // RSCP Upper1
//        nCnt++;     // RSCP Lower1
//        nCnt++;     // PSC Ec/Io1 Lower
//        nCnt += 6;
//
////        Update_ReceiverTab();
//        //Debug.loge(new Exception(),"ICTLAB receiverSendFlag 22 : "+receiverSendFlag);
//        if (receiverUpdateFlag) {
//            Variables.receiverSettingFlag = (byte) 0x99;
//            receiverUpdateFlag = false;
//        }
//    }
//
//
//    private void Parse_TsyncStatusDataFromRepeater(byte[] data)
//    {
//        int nCnt = Variables.Protocol_MESSAGE_START;
//        short dummy16 = 0;
//        int dummy32 = 0;
//
//        // tsync alarm
//        if ((data[nCnt] & 0x01) == 0x01) Variables.bandStruct[Variables.band].tsync.aTsyncLink = 1;
//        else Variables.bandStruct[Variables.band].tsync.aTsyncLink = 0;
//        if ((data[nCnt] & 0x02) == 0x02) Variables.bandStruct[Variables.band].tsync.aTsyncLock = 1;
//        else Variables.bandStruct[Variables.band].tsync.aTsyncLock = 0;
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "alarm: " + data[nCnt]);
//        nCnt++;
//        // tsync status
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "iVendor: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].tsync.iVendor = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "iFpgaVer: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].tsync.iFpgaVer = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "iFwVer: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].tsync.iFwVer = data[nCnt++];
//
//        dummy16 = (short)(data[nCnt++] << 8);
//        dummy16 |= data[nCnt++];
//        Variables.bandStruct[Variables.band].tsync.iRxPower = dummy16;
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "iRxPower: " + dummy16);
//        dummy16 = (short)(data[nCnt++] << 8);
//        dummy16 |= data[nCnt++];
//        Variables.bandStruct[Variables.band].tsync.iPciValue = dummy16;
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "iPciValue: " + dummy16);
//        dummy16 = (short)(data[nCnt++] << 8);
//        dummy16 |= data[nCnt++];
//        Variables.bandStruct[Variables.band].tsync.iRssi = dummy16;
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "iRssi: " + dummy16);
//
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "iSSBindex: " + data[nCnt]);
//        Variables.bandStruct[Variables.band].tsync.iSSBindex = data[nCnt++];
//        nCnt++;     // Temperature
//
//        dummy16 = (short)(data[nCnt++] << 8);
//        dummy16 |= data[nCnt++];
//        Variables.bandStruct[Variables.band].tsync.iSSBrsrp = dummy16;
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "iSSBrsrp: " + dummy16);
//        dummy16 = (short)(data[nCnt++] << 8);
//        dummy16 |= data[nCnt++];
//        Variables.bandStruct[Variables.band].tsync.iInputPower = dummy16;
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "iInputPower: " + dummy16);
//
//        nCnt += 8;  // reserved
//
//        if (Variables.tsyncSendFlag) {
//            sendTsyncSettingStopMsg();   // FrameInfo Clear
////            Debug.logi(new Exception(),"=dhjung=======> Parse_TsyncStatusDataFromRepeater stop");
//            tsyncUpdateFlag = true;
//        }
//        else {
//            // tsync Configure
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "tTddMode: " + data[nCnt]);
//            Variables.bandStruct[Variables.band].tsync.tTddMode = data[nCnt++];
//
//            dummy32 = data[nCnt++] << 8;
//            dummy32 |= data[nCnt++];
//            Variables.bandStruct[Variables.band].tsync.tDlOffTime = dummy32;
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "tDlOffTime: " + dummy32);
//            dummy32 = data[nCnt++] << 8;
//            dummy32 |= data[nCnt++];
//            Variables.bandStruct[Variables.band].tsync.tUlOffTime = dummy32;
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "tUlOffTime: " + dummy32);
//            dummy32 = data[nCnt++] << 8;
//            dummy32 |= data[nCnt++];
//            Variables.bandStruct[Variables.band].tsync.tDlOnTime = dummy32;
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "tDlOnTime: " + dummy32);
//            dummy32 = data[nCnt++] << 8;
//            dummy32 |= data[nCnt++];
//            Variables.bandStruct[Variables.band].tsync.tUlOnTime = dummy32;
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "tUlOnTime: " + dummy32);
//
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "tDlUlConfigure: " + data[nCnt]);
//            Variables.bandStruct[Variables.band].tsync.tDlUlConfigure = data[nCnt++];
//
//            if ((data[nCnt] & 0x01) == 0x01)
//                Variables.bandStruct[Variables.band].tsync.tTsync1OutSel = 1;
//            else Variables.bandStruct[Variables.band].tsync.tTsync1OutSel = 0;
//            if ((data[nCnt] & 0x02) == 0x02)
//                Variables.bandStruct[Variables.band].tsync.tTsync2OutSel = 1;
//            else Variables.bandStruct[Variables.band].tsync.tTsync2OutSel = 0;
//            if ((data[nCnt] & 0x04) == 0x04)
//                Variables.bandStruct[Variables.band].tsync.tTsync3OutSel = 1;
//            else Variables.bandStruct[Variables.band].tsync.tTsync3OutSel = 0;
//            if ((data[nCnt] & 0x08) == 0x08)
//                Variables.bandStruct[Variables.band].tsync.tTsync4OutSel = 1;
//            else Variables.bandStruct[Variables.band].tsync.tTsync4OutSel = 0;
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "tSyncOutSel: " + data[nCnt]);
//            nCnt++;
//
//            dummy32 = (data[nCnt++] << 24) & 0xFF000000;
//            dummy32 |= (data[nCnt++] << 16) & 0xFF0000;
//            dummy32 |= (data[nCnt++] << 8) & 0xFF00;
//            dummy32 |= data[nCnt++] & 0xFF;
//            Variables.bandStruct[Variables.band].tsync.tCenterFreq = dummy32;
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "tCenterFreq: " + dummy32);
//
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "tBandSel: " + data[nCnt]);
//            Variables.bandStruct[Variables.band].tsync.tBandSel = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "tBandWidth: " + data[nCnt]);
//            Variables.bandStruct[Variables.band].tsync.tBandWidth = data[nCnt++];
////        Debug.loge(new Exception(),"[" + nCnt + "]" + "tSSBsearchMode: " + data[nCnt]);
//            Variables.bandStruct[Variables.band].tsync.tSSBsearchMode = data[nCnt++];
//        }
//
//        Update_TsyncTab();
//        if (tsyncUpdateFlag) {
//            Variables.tsyncSettingFlag = (byte) 0x99;
//            tsyncUpdateFlag = false;
//        }
//    }
//

    private void Parse_GuiSettingDataFromRepeater(byte[] data) {
//        Debug.logi(new Exception(),"K-NETZ data : "+HexDump.toHexString(data));

        int nCnt = Variables.Protocol_MESSAGE_START;

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

        System.arraycopy(data, nCnt, Variables.gSystemName, 0, 20);
        nCnt += 20;
//        System.arraycopy(data, nCnt, Variables.gSerialNum, 0, 10);
//        nCnt += 10;
//        System.arraycopy(data, nCnt, Variables.gPartNum, 0, 10);
//        nCnt += 10;

        System.arraycopy(data, nCnt, Variables.gModelName, 0, 20);
        nCnt += 20;
        System.arraycopy(data, nCnt, Variables.gOperatorName, 0, 20);
        nCnt += 20;
        System.arraycopy(data, nCnt, Variables.gSupplierName, 0, 20);
        nCnt += 20;

        Update_AlarmTab();
    }


    private void Update_AlarmTab() {

        DataType[] AlarmSystemInfo = subTitleNameClass.getAlarmSubTitle().getSystemInformationSubTitle();
        DataType[] AlarmPsuInfo = subTitleNameClass.getAlarmSubTitle().getPsuSubTitle();
        DataType[] AlarmDownDspRfInfo = subTitleNameClass.getAlarmSubTitle().getDownlinkDspRfSubTitle();
        DataType[] AlarmUpDspRfInfo = subTitleNameClass.getAlarmSubTitle().getUplinkDspRfSubTitle();
        DataType[] AlarmDownAmpInfo = subTitleNameClass.getAlarmSubTitle().getDownlinkAmpSubTitle();
        DataType[] AlarmUpAmpInfo = subTitleNameClass.getAlarmSubTitle().getUplinkAmpSubTitle();

        String strBuf = null;

        for (int i = 0; i < AlarmSystemInfo.length; i++) {
            switch (AlarmSystemInfo[i].getId()) {
                case Variables.DB_ALARM_TEMP_HIGH:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].stats.aTempHigh);
                    break;
                case Variables.DB_ALARM_DOOR_OPEN:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].stats.aDoorOpen);
                    break;
                case Variables.DB_ALARM_TSYNC_LOCK1:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].stats.aTsyncLock1);
                    break;
                case Variables.DB_ALARM_TSYNC_LOCK2:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].stats.aTsyncLock2);
                    break;
                case Variables.DB_ALARM_MODEM_LINK:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].stats.aModemLink);
                    break;
                case Variables.DB_ALARM_TSYNC_LINK1:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].stats.aTsyncLink1);
                    break;
                case Variables.DB_ALARM_TSYNC_LINK2:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].stats.aTsyncLink2);
                    break;
                default:
                    break;
            }
            AlarmSystemInfo[i].setValue(strBuf);
        }

        for (int i = 0; i < AlarmPsuInfo.length; i++) {
            switch (AlarmPsuInfo[i].getId()) {
                case Variables.DB_ALARM_AC_FAIL:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].stats.aAcFail);
                    break;
                case Variables.DB_ALARM_DC_FAIL:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].stats.aDcFail);
                    break;
                case Variables.DB_ALARM_BATTERY_ONOFF:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].stats.aBatterySwOnOff);
                    break;
                case Variables.DB_ALARM_DC_INPUT_FAIL:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].stats.aDcInputFail);
                    break;
                case Variables.DB_ALARM_UPS_STATUS:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].stats.aUpsStatus);
                    break;
                case Variables.DB_ALARM_LOW_BATTERY:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].stats.aLowBattery);
                    break;
                case Variables.DB_ALARM_ON_BATTERY:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].stats.aOnBattery);
                    break;
                case Variables.DB_ALARM_OVER_CURRENT:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].stats.aOverCurrent);
                    break;
                default:
                    break;
            }
            AlarmPsuInfo[i].setValue(strBuf);
        }

        for (int i = 0; i < AlarmDownDspRfInfo.length; i++) {
            switch (AlarmDownDspRfInfo[i].getId()) {
                case Variables.DB_ALARM_DSP_LINK_FAIL:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aDspLinkFail);
                    break;
                case Variables.DB_ALARM_ICS_STATUS:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aIcsStatus);
                    break;
                case Variables.DB_ALARM_ISOLATION_LACK:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aIsolationLack);
                    break;
                case Variables.DB_ALARM_RF_DEVICE_PLL:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aRfDevicePll);
                    break;
                case Variables.DB_ALARM_INPUT_LOWER:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aInputLower);
                    break;
                case Variables.DB_ALARM_INPUT_UPPER:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aInputUpper);
                    break;
                case Variables.DB_ALARM_OUTPUT_LOWER:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aOutputLower);
                    break;
                case Variables.DB_ALARM_OUTPUT_UPPER:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aOutputUpper);
                    break;
                default:
                    break;
            }
            AlarmDownDspRfInfo[i].setValue(strBuf);
        }

        for (int i = 0; i < AlarmUpDspRfInfo.length; i++) {
            switch (AlarmUpDspRfInfo[i].getId()) {
                case Variables.DB_ALARM_DSP_LINK_FAIL:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[1].aDspLinkFail);
                    break;
                case Variables.DB_ALARM_ICS_STATUS:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[1].aIcsStatus);
                    break;
                case Variables.DB_ALARM_ISOLATION_LACK:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[1].aIsolationLack);
                    break;
                case Variables.DB_ALARM_RF_DEVICE_PLL:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[1].aRfDevicePll);
                    break;
                case Variables.DB_ALARM_INPUT_UPPER:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[1].aInputUpper);
                    break;
                case Variables.DB_ALARM_OUTPUT_UPPER:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[1].aOutputUpper);
                    break;

                default:
                    break;
            }
            AlarmUpDspRfInfo[i].setValue(strBuf);
        }

        for (int i = 0; i < AlarmDownAmpInfo.length; i++) {
            switch (AlarmDownAmpInfo[i].getId()) {
                case Variables.DB_ALARM_OVER_TEMP:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aOverTemp);
                    break;
                case Variables.DB_ALARM_AMP_DC_FAIL:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aAmpDcFail);
                    break;
                case Variables.DB_ALARM_VSWR:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aVSWR);
                    break;
                case Variables.DB_ALARM_LOOP_FAIL:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aLoopFail);
                    break;
                case Variables.DB_ALARM_AMP_ONOFF:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aAmpOnoff);
                    break;
                case Variables.DB_ALARM_DEVICE_FAIL:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aDeviceFail);
                    break;
                case Variables.DB_ALARM_OVER_POWER:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aOverPower);
                    break;
                case Variables.DB_ALARM_LINK_FAIL:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[0].aLinkFail);
                    break;
                default:
                    break;
            }
            AlarmDownAmpInfo[i].setValue(strBuf);
        }

        for (int i = 0; i < AlarmUpAmpInfo.length; i++) {
            switch (AlarmUpAmpInfo[i].getId()) {
                case Variables.DB_ALARM_VSWR:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[1].aVSWR);
                    break;
                case Variables.DB_ALARM_AMP_ONOFF:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[1].aAmpOnoff);
                    break;
                case Variables.DB_ALARM_DEVICE_FAIL:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[1].aDeviceFail);
                    break;
                case Variables.DB_ALARM_OVER_POWER:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[1].aOverPower);
                    break;
                case Variables.DB_ALARM_LINK_FAIL:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathStats[1].aLinkFail);
                    break;
                default:
                    break;
            }
            AlarmUpAmpInfo[i].setValue(strBuf);
        }
    }


    private void Update_StatusTab() {

        DataType[] StatusSystemInfo = subTitleNameClass.getStatusSubTitle().getSystemInformationSubTitle();
        DataType[] StatusPsuInfo = subTitleNameClass.getStatusSubTitle().getPsuSubTitle();
        DataType[] StatusDownDspRfInfo = subTitleNameClass.getStatusSubTitle().getDownlinkDspRfSubTitle();
        DataType[] StatusUpDspRfInfo = subTitleNameClass.getStatusSubTitle().getUplinkDspRfSubTitle();
        DataType[] StatusDownAmpInfo = subTitleNameClass.getStatusSubTitle().getDownlinkAmpSubTitle();
        DataType[] StatusUpAmpInfo = subTitleNameClass.getStatusSubTitle().getUplinkAmpSubTitle();

        String strBuf = null;
        int arrOff = 0, dataLen = 0;

        for (int i = 0; i < StatusSystemInfo.length; i++) {
            switch (StatusSystemInfo[i].getId()) {
                case Variables.DB_DEFAULT_RPT_PRODUCT:
                    strBuf = ByteUtil.byteArrayToString(Variables.gSystemName);
                    break;
                case Variables.DB_DEFAULT_RPT_MODEL:
                    strBuf = ByteUtil.byteArrayToString(Variables.gModelName);
                    break;
                case Variables.DB_DEFAULT_RPT_OPERATOR:
                    strBuf = ByteUtil.byteArrayToString(Variables.gOperatorName);
                    break;
                case Variables.DB_DEFAULT_RPT_SUPPLIER:
                    strBuf = ByteUtil.byteArrayToString(Variables.gSupplierName);
                    break;
                case Variables.DB_STATUS_RPT_MAKER:
                    if (Variables.bandStruct[Variables.band].stats.sRepeaterMaker == 1) strBuf = "K-NETZ";
                    else strBuf = "None";
                    break;
                case Variables.DB_STATUS_SUPPLIER:
                    //strBuf = ByteUtil.byteArrayToString(Variables.bandStruct[Variables.band].stats.sSupplier);
                    if (Variables.bandStruct[Variables.band].stats.sSupplier == 1) strBuf = "K-NETZ";
                    else strBuf = "None";
                    break;
                case Variables.DB_STATUS_VERSION:
                    strBuf = String.format("v%x.%x", Variables.bandStruct[Variables.band].stats.sVersion / 0x10, Variables.bandStruct[Variables.band].stats.sVersion & 0x0F);
                    break;
                case Variables.DB_STATUS_TEMPERATURE:
                    strBuf = Variables.bandStruct[Variables.band].stats.sTemperature + "'C";
                    break;
                case Variables.DB_STATUS_CPU_USAGE:
                    strBuf = Variables.bandStruct[Variables.band].stats.sCpuUsage + "%";
                    break;
                default:
                    break;
            }
            StatusSystemInfo[i].setValue(strBuf);
        }

        for (int i = 0; i < StatusPsuInfo.length; i++) {
            switch (StatusPsuInfo[i].getId()) {
                case Variables.DB_STATUS_BATTERY_TYPE:
                    if (Variables.bandStruct[Variables.band].stats.sBatteryType == 1) strBuf = "Int";
                    else strBuf = "Ext";
                    break;
                case Variables.DB_STATUS_INPUT_VOLTAGE:
                    strBuf = Variables.bandStruct[Variables.band].stats.sInputVoltage + "V";
                    break;
                case Variables.DB_STATUS_OUTPUT_VOLTAGE:
                    strBuf = Variables.bandStruct[Variables.band].stats.sOutputVoltage + "V";
                    break;
                case Variables.DB_STATUS_BATTERY_VOLTAGE:
                    strBuf = Variables.bandStruct[Variables.band].stats.sBatteryVoltage + "V";
                    break;
                case Variables.DB_STATUS_DC_CURRENT:
                    strBuf = String.format("%d.%dA", Variables.bandStruct[Variables.band].stats.sDcCurrent / 100, Variables.bandStruct[Variables.band].stats.sDcCurrent % 100);
                    break;
                default:
                    break;
            }
            StatusPsuInfo[i].setValue(strBuf);
        }


        if((Variables.bandStruct[0].sets.tFreqSelect10M15M == 0) && (Variables.band == 4)) {
            arrOff = 7;
            dataLen = StatusDownDspRfInfo.length;
        }else {
            arrOff = 0;
            dataLen = StatusDownDspRfInfo.length - 7;
        }

        for (int i = arrOff; i < dataLen; i++) {
            switch (StatusDownDspRfInfo[i].getId()) {
                case Variables.DB_STATUS_ISOL_OSC_GAIN:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sIsolOscGain + "dB";
                    break;
                case Variables.DB_STATUS_INPUT_POWER:
                    strBuf = String.format("%d.%ddBm", Variables.bandStruct[Variables.band].pathStats[0].sInputPower / 10, Math.abs(Variables.bandStruct[Variables.band].pathStats[0].sInputPower % 10));
//                    Debug.logi(new Exception(),"=dhjung=======> sInputPower : " +Variables.bandStruct[Variables.band].pathStats[0].sInputPower + ", " + Variables.band );
//                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sInputPower + "dBm";
                    break;
                case Variables.DB_STATUS_CURRENT_ATTEN:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sCurrentAtten + "dB";
                    break;
                case Variables.DB_STATUS_CURRENT_GAIN:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sCurrentGain + "dB";
                    break;
                case Variables.DB_STATUS_ALC_ATTEN:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sAlcAtten + "dB";
                    break;
                case Variables.DB_STATUS_INPUT_POWER_REAL:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sInputPowerReal + "dBm";
                    break;
                case Variables.DB_STATUS_ALC_MIN_ATTEN:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sAlcMinAtten + "dB";
                    break;
                case Variables.DB_STATUS_ISOL_MIN_GAIN:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sIsolMinGain + "dB";
                    break;
                case Variables.DB_STATUS_INPUT_POWER_X10:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sInputPowerX10 + "dBm";
                    break;

                case Variables.DB_STATUS_ISOL_OSC_GAIN_CDMA:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sIsolOscGain + "dB";
                    break;
                case Variables.DB_STATUS_INPUT_POWER_CDMA:
                    strBuf = String.format("%d.%ddBm", Variables.bandStruct[Variables.band].pathStats[0].sInputPower / 10, Math.abs(Variables.bandStruct[Variables.band].pathStats[0].sInputPower % 10));
//                    Debug.logi(new Exception(),"=dhjung=======> sInputPower : " +Variables.bandStruct[Variables.band].pathStats[0].sInputPower + ", " + Variables.band );
                    break;
                case Variables.DB_STATUS_CURRENT_ATTEN_CDMA:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sCurrentAtten + "dB";
                    break;
                case Variables.DB_STATUS_CURRENT_GAIN_CDMA:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sCurrentGain + "dB";
                    break;
                case Variables.DB_STATUS_ALC_ATTEN_CDMA:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sAlcAtten + "dB";
                    break;
                case Variables.DB_STATUS_ALC_MIN_ATTEN_CDMA:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sAlcMinAtten + "dB";
                    break;
                case Variables.DB_STATUS_ISOL_MIN_GAIN_CDMA:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sIsolMinGain + "dB";
                    break;
                default:
                    break;
            }
            StatusDownDspRfInfo[i].setValue(strBuf);
        }

        if((Variables.bandStruct[0].sets.tFreqSelect10M15M == 0) && (Variables.band == 4)) {
            arrOff = 7;
            dataLen = StatusUpDspRfInfo.length;
        }else{
            arrOff = 0;
            dataLen = StatusUpDspRfInfo.length - 7;
        }

        for (int i = arrOff; i < dataLen; i++) {
            switch (StatusUpDspRfInfo[i].getId()) {
                case Variables.DB_STATUS_ISOL_OSC_GAIN:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sIsolOscGain + "dB";
                    break;
                case Variables.DB_STATUS_INPUT_POWER:
                    strBuf = String.format("%d.%ddBm", Variables.bandStruct[Variables.band].pathStats[1].sInputPower / 10, Math.abs(Variables.bandStruct[Variables.band].pathStats[1].sInputPower % 10));
//                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sInputPower + "dBm";
                    break;
                case Variables.DB_STATUS_CURRENT_ATTEN:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sCurrentAtten + "dB";
                    break;
                case Variables.DB_STATUS_CURRENT_GAIN:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sCurrentGain + "dB";
                    break;
                case Variables.DB_STATUS_ALC_ATTEN:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sAlcAtten + "dB";
                    break;
                case Variables.DB_STATUS_INPUT_POWER_REAL:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sInputPowerReal + "dBm";
                    break;
                case Variables.DB_STATUS_ALC_MIN_ATTEN:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sAlcMinAtten + "dB";
                    break;
                case Variables.DB_STATUS_ISOL_MIN_GAIN:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sIsolMinGain + "dB";
                    break;
                case Variables.DB_STATUS_INPUT_POWER_X10:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sInputPowerX10 + "dBm";
                    break;

                case Variables.DB_STATUS_ISOL_OSC_GAIN_CDMA:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sIsolOscGain + "dB";
                    break;
                case Variables.DB_STATUS_INPUT_POWER_CDMA:
                    strBuf = String.format("%d.%ddBm", Variables.bandStruct[Variables.band].pathStats[1].sInputPower / 10, Math.abs(Variables.bandStruct[Variables.band].pathStats[1].sInputPower % 10));
                    break;
                case Variables.DB_STATUS_CURRENT_ATTEN_CDMA:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sCurrentAtten + "dB";
                    break;
                case Variables.DB_STATUS_CURRENT_GAIN_CDMA:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sCurrentGain + "dB";
                    break;
                case Variables.DB_STATUS_ALC_ATTEN_CDMA:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sAlcAtten + "dB";
                    break;
                case Variables.DB_STATUS_ALC_MIN_ATTEN_CDMA:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sAlcMinAtten + "dB";
                    break;
                case Variables.DB_STATUS_ISOL_MIN_GAIN_CDMA:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sIsolMinGain + "dB";
                    break;
                default:
                    break;
            }
            StatusUpDspRfInfo[i].setValue(strBuf);
        }


        if((Variables.bandStruct[0].sets.tFreqSelect10M15M == 0) && (Variables.band == 4)) {
            arrOff = 1;
            dataLen = StatusDownAmpInfo.length;
        }else {
            arrOff = 0;
            dataLen = StatusDownAmpInfo.length - 1;
        }

        for (int i = arrOff; i < dataLen; i++) {
            switch (StatusDownAmpInfo[i].getId()) {
                case Variables.DB_STATUS_OUTPUT_POWER:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sOutputPower + "dBm";
                    break;
                case Variables.DB_STATUS_AMP_MAKER:
                    if (Variables.bandStruct[Variables.band].pathStats[0].sAmpMaker == 1)
                        strBuf = "xxx";
                    else strBuf = "???";
                    break;
                case Variables.DB_STATUS_AMP_TEMP:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sAmpTemp + "'C";
                    break;
                case Variables.DB_STATUS_OUTPUT_POWER_X10:
                    strBuf = String.format("%d.%ddBm", Variables.bandStruct[Variables.band].pathStats[0].sOutputPowerX10 / 10, Math.abs(Variables.bandStruct[Variables.band].pathStats[0].sOutputPowerX10 % 10));
//                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sOutputPowerX10 + "dBm";
                    break;

                case Variables.DB_STATUS_OUTPUT_POWER_CDMA:
                    strBuf = String.format("%d.%ddBm", Variables.bandStruct[Variables.band].pathStats[0].sOutputPowerX10 / 10, Math.abs(Variables.bandStruct[Variables.band].pathStats[0].sOutputPowerX10 % 10));
                    break;
                case Variables.DB_STATUS_OUTPUT_POWER_X10SUM:
                    strBuf = String.format("%d.%ddBm", Variables.bandStruct[Variables.band].pathStats[0].sOutputPowerX10Sum / 10, Math.abs(Variables.bandStruct[Variables.band].pathStats[0].sOutputPowerX10Sum % 10));
//                    strBuf = Variables.bandStruct[Variables.band].pathStats[0].sOutputPowerX10Sum + "dBm";
                    break;
                default:
                    break;
            }
            StatusDownAmpInfo[i].setValue(strBuf);
        }

        if((Variables.bandStruct[0].sets.tFreqSelect10M15M == 0) && (Variables.band == 4)) {
            arrOff = 1;
            dataLen = StatusUpAmpInfo.length;
        }else {
            arrOff = 0;
            dataLen = StatusUpAmpInfo.length - 1;
        }

        for (int i = arrOff; i < dataLen; i++) {
            switch (StatusUpAmpInfo[i].getId()) {
                case Variables.DB_STATUS_OUTPUT_POWER:
                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sOutputPower + "dBm";
                    break;
                case Variables.DB_STATUS_AMP_MAKER:
                    if (Variables.bandStruct[Variables.band].pathStats[1].sAmpMaker == 1)
                        strBuf = "xxx";
                    else strBuf = "???";
                    break;
                case Variables.DB_STATUS_OUTPUT_POWER_X10:
                    strBuf = String.format("%d.%ddBm", Variables.bandStruct[Variables.band].pathStats[1].sOutputPowerX10 / 10, Math.abs(Variables.bandStruct[Variables.band].pathStats[1].sOutputPowerX10 % 10));
//                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sOutputPowerX10 + "dBm";
                    break;

                case Variables.DB_STATUS_OUTPUT_POWER_CDMA:
                    strBuf = String.format("%d.%ddBm", Variables.bandStruct[Variables.band].pathStats[1].sOutputPowerX10 / 10, Math.abs(Variables.bandStruct[Variables.band].pathStats[1].sOutputPowerX10 % 10));
                    break;
                case Variables.DB_STATUS_OUTPUT_POWER_X10SUM:
                    strBuf = String.format("%d.%ddBm", Variables.bandStruct[Variables.band].pathStats[1].sOutputPowerX10Sum / 10, Math.abs(Variables.bandStruct[Variables.band].pathStats[1].sOutputPowerX10Sum % 10));
//                    strBuf = Variables.bandStruct[Variables.band].pathStats[1].sOutputPowerX10Sum + "dBm";
                    break;
                default:
                    break;
            }
            StatusUpAmpInfo[i].setValue(strBuf);
        }

    }


    private void Update_SettingTab() {

        DataType[] SettingSystemInfo = subTitleNameClass.getSettingSubTitle().getSystemInformationSubTitle();
        DataType[] SettingDownDspRfInfo = subTitleNameClass.getSettingSubTitle().getDownlinkDspRfSubTitle();
        DataType[] SettingUpDspRfInfo = subTitleNameClass.getSettingSubTitle().getUplinkDspRfSubTitle();
        DataType[] SettingDownAmpInfo = subTitleNameClass.getSettingSubTitle().getDownlinkAmpSubTitle();
        DataType[] SettingUpAmpInfo = subTitleNameClass.getSettingSubTitle().getUplinkAmpSubTitle();
        DataType[] SettingServiceFAInfo = subTitleNameClass.getSettingSubTitle().getServiceFaSubTitle();
        DataType[] SettingCommonInfo = subTitleNameClass.getSettingSubTitle().getCommonSubTitle();

        String strBuf = null;

        for (int i = 0; i < SettingSystemInfo.length; i++) {
            switch (SettingSystemInfo[i].getId()) {
                case Variables.DB_SETTING_TEMP_UPPER:
//                    strBuf = Variables.bandStruct[Variables.band].sets.tTempUpper + "'C";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].sets.tTempUpper);
                    break;
                case Variables.DB_SETTING_AUTO_SHUTDOWN:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].sets.tAutoShutdown);
                    break;
                case Variables.DB_SETTING_AUTO_RECOVERY:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].sets.tAutoRecovery);
                    break;
                case Variables.DB_SETTING_SLEEP_MODE:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].sets.tSleepMode);
                    break;
                case Variables.DB_SETTING_ILC:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].sets.tILC);
                    break;
                case Variables.DB_SETTING_SAW_BYPASS:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].sets.tSawBypass);
                    break;
                case Variables.DB_SETTING_FREQ_SEL_AUTO:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].sets.tFreqSelectAutoManual);
                    break;
                case Variables.DB_SETTING_FREQ_SEL:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].sets.tFreqSelect10M15M);
                    break;
                case Variables.DB_SETTING_CELL_SEARCH:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].sets.tCellSearch);
                    break;
                default:
                    break;
            }
            SettingSystemInfo[i].setValue(strBuf);
        }

        for (int i = 0; i < SettingDownDspRfInfo.length; i++) {
            switch (SettingDownDspRfInfo[i].getId()) {
                case Variables.DB_SETTING_ATTEN:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[0].tAtten + "dB";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tAtten);
                    break;
                case Variables.DB_SETTING_ICS_MODE:
                    if (Variables.bandStruct[Variables.band].pathSets[0].tIcsMode == 1) strBuf = "None";
                    else if(Variables.bandStruct[Variables.band].pathSets[0].tIcsMode == 2) strBuf = "TargetGain";
                    else  if(Variables.bandStruct[Variables.band].pathSets[0].tIcsMode == 3)strBuf = "AGC";
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tIcsMode);
                    break;
                case Variables.DB_SETTING_SYSTEM_GAIN:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[0].tSystemGain + "dB";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tSystemGain);
                    break;
                case Variables.DB_SETTING_OUTPUT_UPPER:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[0].tOutputUpper + "dBm";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tOutputUpper);
                    break;
                case Variables.DB_SETTING_OUTPUT_LOWER:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[0].tOutputLower + "dBm";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tOutputLower);
                    break;
                case Variables.DB_SETTING_INPUT_UPPER:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[0].tInputUpper + "dBm";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tInputUpper);
                    break;
                case Variables.DB_SETTING_INPUT_LOWER:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[0].tInputLower + "dBm";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tInputLower);
                    break;
                case Variables.DB_SETTING_AGC_OFFSET:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[0].tAgcOffset + "dB";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tAgcOffset);
                    break;
                case Variables.DB_SETTING_PATH_SLEEP_MODE:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[0].tPathSleepMode;
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tPathSleepMode);
                    break;
                case Variables.DB_SETTING_PATH_SLEEP_LEVEL:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[0].tPathSleepLevel + "dB";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tPathSleepLevel);
                    break;
                case Variables.DB_SETTING_INPUT_ALC_RECOVERY_TIME:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[0].tInputAlcRecoveryTime + "sec";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tInputAlcRecoveryTime);
                    break;
                case Variables.DB_SETTING_INPUT_ALC_PERIOD:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[0].tInputAlcPeriod + "sec/dB";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tInputAlcPeriod);
                    break;
                case Variables.DB_SETTING_INPUT_ALC_LEVEL:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[0].tInputAlcLevel + "dBm";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tInputAlcLevel);
                    break;
                case Variables.DB_SETTING_ICS_OFF:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[0].tIcsOff + "dBm";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tIcsOff);
                    break;
                default:
                    break;
            }
            SettingDownDspRfInfo[i].setValue(strBuf);
        }

        for (int i = 0; i < SettingUpDspRfInfo.length; i++) {
            switch (SettingUpDspRfInfo[i].getId()) {
                case Variables.DB_SETTING_ATTEN:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[1].tAtten +"dB";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[1].tAtten);
                    break;
                case Variables.DB_SETTING_ICS_MODE:
                    if (Variables.bandStruct[Variables.band].pathSets[1].tIcsMode == 1) strBuf = "None";
                    else if(Variables.bandStruct[Variables.band].pathSets[1].tIcsMode == 2) strBuf = "TargetGain";
                    else if(Variables.bandStruct[Variables.band].pathSets[1].tIcsMode == 3) strBuf = "Balance";
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[1].tIcsMode);
                    break;
                case Variables.DB_SETTING_SYSTEM_GAIN:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[1].tSystemGain + "dB";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[1].tSystemGain);
                    break;
                case Variables.DB_SETTING_AGC_OFFSET:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[1].tAgcOffset + "dB";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[1].tAgcOffset);
                    break;
                case Variables.DB_SETTING_OUTPUT_UPPER:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[1].tOutputUpper + "dBm";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[1].tOutputUpper);
                    break;
                case Variables.DB_SETTING_INPUT_UPPER:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[1].tInputUpper + "dBm";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[1].tInputUpper);
                    break;
                case Variables.DB_SETTING_PATH_SLEEP_MODE:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[1].tPathSleepMode;
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[1].tPathSleepMode);
                    break;
                case Variables.DB_SETTING_PATH_SLEEP_LEVEL:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[1].tPathSleepLevel + "dB";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[1].tPathSleepLevel);
                    break;
                case Variables.DB_SETTING_INPUT_ALC_RECOVERY_TIME:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[1].tInputAlcRecoveryTime + "sec";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[1].tInputAlcRecoveryTime);
                    break;
                case Variables.DB_SETTING_INPUT_ALC_PERIOD:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[1].tInputAlcPeriod + "sec/dB";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[1].tInputAlcPeriod);
                    break;
                case Variables.DB_SETTING_INPUT_ALC_LEVEL:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[1].tInputAlcLevel + "dBm";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[1].tInputAlcLevel);
                    break;
                case Variables.DB_SETTING_ICS_OFF:
//                    strBuf = Variables.bandStruct[Variables.band].pathSets[1].tIcsOff + "dBm";
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[1].tIcsOff);
                    break;
                default:
                    break;
            }
            SettingUpDspRfInfo[i].setValue(strBuf);
        }

        for (int i = 0; i < SettingDownAmpInfo.length; i++) {
            switch (SettingDownAmpInfo[i].getId()) {
                case Variables.DB_SETTING_AMP_ONOFF:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tAmpOnOff);
                    break;
                case Variables.DB_SETTING_ALC_ONOFF:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[0].tAlcOnOff);
                    break;
                default:
                    break;
            }
            SettingDownAmpInfo[i].setValue(strBuf);
        }

        for (int i = 0; i < SettingUpAmpInfo.length; i++) {
            switch (SettingUpAmpInfo[i].getId()) {
                case Variables.DB_SETTING_AMP_ONOFF:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[1].tAmpOnOff);
                    break;
                case Variables.DB_SETTING_ALC_ONOFF:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].pathSets[1].tAlcOnOff);
                    break;
                default:
                    break;
            }
            SettingUpAmpInfo[i].setValue(strBuf);
        }

        for (int i = 0; i < SettingServiceFAInfo.length; i++) {
            switch (SettingServiceFAInfo[i].getId()) {
                case Variables.DB_SETTING_DL_FA:
                    //strBuf = String.valueOf(Variables.bandStruct[Variables.band].sets.tDlFA);
                    break;
                case Variables.DB_SETTING_UL_FA:
                    //strBuf = String.valueOf(Variables.bandStruct[Variables.band].sets.tUlFA);
                    break;
                case Variables.DB_SETTING_FA_ALLOCATION:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].sets.tFAallocation);
                    break;
                case Variables.DB_SETTING_BAND_SELECT:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].sets.tBandSelect);
                    break;
                case Variables.DB_SETTING_RF_PATH_ON:
                    if(Variables.bandStruct[Variables.band].sets.tRfPathOn == 1) strBuf = "LTE Only";
                    else if(Variables.bandStruct[Variables.band].sets.tRfPathOn == 2) strBuf = "CDMA Only";
                    else if(Variables.bandStruct[Variables.band].sets.tRfPathOn == 3) strBuf = "LTE+CDMA";
                    break;

                default:
                    break;
            }
            SettingServiceFAInfo[i].setValue(strBuf);
        }

        for (int i = 0; i < SettingCommonInfo.length; i++) {
            switch (SettingCommonInfo[i].getId()) {
                case Variables.DB_SETTING_SERIAL_NUMBER:
                    strBuf = ByteUtil.byteArrayToString(Variables.bandStruct[Variables.band].sets.tSerialNum);
                    break;
                case Variables.DB_SETTING_MODEL_NAME:
                    strBuf = ByteUtil.byteArrayToString(Variables.bandStruct[Variables.band].sets.tModelName);
                    break;
                case Variables.DB_SETTING_OPERATOR_NAME:
                    strBuf = ByteUtil.byteArrayToString(Variables.bandStruct[Variables.band].sets.tOperatorName);
                    break;
                case Variables.DB_SETTING_SUPPLIER_NAME:
                    strBuf = ByteUtil.byteArrayToString(Variables.bandStruct[Variables.band].sets.tSupplierName);
                    break;
                case Variables.DB_SETTING_INSTALL_ADDR:
                    strBuf = ByteUtil.byteArrayToString(Variables.bandStruct[Variables.band].sets.tInstallAddr);
                    break;
                case Variables.DB_SETTING_POWER_MODE:
                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].sets.tPowerMode);
                    break;
                case Variables.DB_SETTING_SERVICE_BAND:
                    if (Variables.bandStruct[Variables.band].sets.tServiceBand == 0) strBuf = "4Band";
                    else if(Variables.bandStruct[Variables.band].sets.tServiceBand == 1) strBuf = "800MHz+2.1GHz";
                    else if(Variables.bandStruct[Variables.band].sets.tServiceBand == 2) strBuf = "800MHz";
                    break;
                default:
                    break;
            }
            SettingCommonInfo[i].setValue(strBuf);
        }

    }


    private void Update_ModemTab() {
        DataType[] ModemParam = subTitleNameClass.getModemSubTitle().getModemParamSubTitle();
        DataType[] ModemCellInfo = subTitleNameClass.getModemSubTitle().getModemCellInfoSubTitle();
        DataType[] ModemRemote = subTitleNameClass.getModemSubTitle().getModemRemoteSubTitle();
        DataType[] ModemSim = subTitleNameClass.getModemSubTitle().getModemSimSubTitle();
        DataType[] ModemNetwork = subTitleNameClass.getModemSubTitle().getModemNetworkSubTitle();
        DataType[] ModemEMS = subTitleNameClass.getModemSubTitle().getModemEMSSubTitle();

        String strBuf = null;

        for (int i = 0; i < ModemParam.length; i++) {
            switch (ModemParam[i].getId()) {
                case Variables.DB_MODEM_LINK: // Alarm
                    if(Variables.modemStruct.modem.aMoLink == 0) strBuf = "Link Success";
                    else if(Variables.modemStruct.modem.aMoLink == 1) strBuf = "Link Fail";
                    break;
                case Variables.DB_MODEM_MODEL:
                    strBuf = ByteUtil.byteArrayToString(Variables.modemStruct.modem.pMoModel);
                    break;
                case Variables.DB_MODEM_VERSION:
                    strBuf = ByteUtil.byteArrayToString(Variables.modemStruct.modem.pMoVer);
                    break;
                case Variables.DB_MODEM_USIM_ID:
                    strBuf = ByteUtil.byteArrayToString(Variables.modemStruct.modem.pUsimId);
                    break;
                case Variables.DB_MODEM_LOCAL_IP_ADDR:
                    strBuf = ByteUtil.byteArrayToString(Variables.modemStruct.modem.pLocalIpAddr);
                    break;
                case Variables.DB_MODEM_USIM_STATUS:
                    if (Variables.modemStruct.modem.pUsimStatue == 0) strBuf = "Ready";
                    else if (Variables.modemStruct.modem.pUsimStatue == 1) strBuf = "Not Insert";
                    else if (Variables.modemStruct.modem.pUsimStatue == 2) strBuf = "SIM PIN";
                    else strBuf = "SIM PUK";
                    break;
                default:
                    break;
            }
            ModemParam[i].setValue(strBuf);
        }

        for (int i = 0; i < ModemCellInfo.length; i++) {
            switch (ModemCellInfo[i].getId()) {
                case Variables.DB_MODEM_REGI:
                    if (Variables.modemStruct.modem.cRegi[Variables.band] == 0) strBuf = "No Service";
                    else if (Variables.modemStruct.modem.cRegi[Variables.band] == 1) strBuf = "Service deny";
                    else strBuf = "Available";
                    break;
                case Variables.DB_MODEM_PLMN:
                    strBuf = ByteUtil.byteArrayToString(Variables.modemStruct.modem.cPlmn[Variables.band]);
                    break;
                case Variables.DB_MODEM_EARFCN:
                    strBuf = String.valueOf(Variables.modemStruct.modem.cEarfcn[Variables.band]);
                    break;
                case Variables.DB_MDOEM_PCI:
                    strBuf = String.valueOf(Variables.modemStruct.modem.cPci[Variables.band]);
                    break;
                case Variables.DB_MODEM_RSSI:
                    strBuf = String.valueOf(Variables.modemStruct.modem.cRssi[Variables.band]);
                    break;
                case Variables.DB_MODEM_RSRP:
                    strBuf = String.valueOf(Variables.modemStruct.modem.cRsrp[Variables.band]);
                    break;
                case Variables.DB_MODEM_RSRQ:
                    strBuf = String.valueOf(Variables.modemStruct.modem.cRsrq[Variables.band]);
                    break;
                default:
                    break;
            }
            ModemCellInfo[i].setValue(strBuf);
        }

        for (int i = 0; i < ModemRemote.length; i++) {
            switch (ModemRemote[i].getId()) {
                case Variables.DB_MODEM_LOCAL_PHONE_NUM:
                    strBuf = ByteUtil.byteArrayToString(Variables.modemStruct.modem.rLocalPhoneNum);
                    break;
                case Variables.DB_MODEM_RCS_PHONE_NUM:
                    strBuf = ByteUtil.byteArrayToString(Variables.modemStruct.modem.rRcsPhoneNum);
                    break;
                default:
                    break;
            }
            ModemRemote[i].setValue(strBuf);
        }

        for (int i = 0; i < ModemSim.length; i++) {
            switch (ModemSim[i].getId()) {
                case Variables.DB_MODEM_PIN_LOCK:
                    strBuf = String.valueOf(Variables.modemStruct.modem.sPinLock);
                    break;
                default:
                    break;
            }
            ModemSim[i].setValue(strBuf);
        }

        for (int i = 0; i < ModemNetwork.length; i++) {
            switch (ModemNetwork[i].getId()) {
                case Variables.DB_MODEM_APN:
                    strBuf = ByteUtil.byteArrayToString(Variables.modemStruct.modem.nApn);
                    break;
                case Variables.DB_MODEM_USER_ID:
                    strBuf = ByteUtil.byteArrayToString(Variables.modemStruct.modem.nUserId);
                    break;
                case Variables.DB_MODEM_USER_PASSWORD:
                    strBuf = ByteUtil.byteArrayToString(Variables.modemStruct.modem.nPassword);
                    break;
                default:
                    break;
            }
            ModemNetwork[i].setValue(strBuf);
        }

        for (int i = 0; i < ModemEMS.length; i++) {
            switch (ModemEMS[i].getId()) {
                case Variables.DB_MODEM_RCS_IP_ADDR_1:
                case Variables.DB_MODEM_RCS_IP_ADDR_2:
                case Variables.DB_MODEM_RCS_IP_ADDR_3:
                case Variables.DB_MODEM_RCS_IP_ADDR_4:
                case Variables.DB_MODEM_RCS_IP_ADDR_5:
                case Variables.DB_MODEM_RCS_IP_ADDR_6:
                case Variables.DB_MODEM_RCS_IP_ADDR_7:
                case Variables.DB_MODEM_RCS_IP_ADDR_8:
                case Variables.DB_MODEM_RCS_IP_ADDR_9:
                case Variables.DB_MODEM_RCS_IP_ADDR_10:
                    strBuf = ByteUtil.byteArrayToString(Variables.modemStruct.modem.eRcsIpAddr[i]);
                    break;
                case Variables.DB_MODEM_RCS_PORT:
                    strBuf = String.valueOf(Variables.modemStruct.modem.eRcsPort);
//                    strBuf = String.valueOf(ByteUtil.toInt(Variables.bandStruct[Variables.band].modem.eRcsPort));
                    break;
                default:
                    break;
            }
            ModemEMS[i].setValue(strBuf);
        }
    }


/*
    private void Update_ReceiverTab() {
        DataType[] ReceiverSysInfo = subTitleNameClass.getModemSubTitle().getSystemInformationSubTitle();
        DataType[] ReceiverCommon = subTitleNameClass.getModemSubTitle().getCommonSubTitle();
        DataType[] ReceiverDownlink = subTitleNameClass.getModemSubTitle().getDownlinkCommonSubTitle();
        DataType[] ReceiverUplink = subTitleNameClass.getModemSubTitle().getUplinkCommonSubTitle();

        String strBuf = null;

        for (int i = 0; i < ReceiverSysInfo.length; i++) {
            switch (ReceiverSysInfo[i].getId()) {
                case Variables.DB_RECEIVER_LINK:
                    if (Variables.rsReceiveLink == 1) strBuf = "Linked";
                    else strBuf = "Fail";
                    break;
                case Variables.DB_RECEIVER_MAKER:
                    strBuf = ByteUtil.toHexString(Variables.rsReceiveMaker);
                    break;
                case Variables.DB_RECEIVER_TYPE:
                    strBuf = ByteUtil.toHexString(Variables.rsReceiveType);
                    break;
                case Variables.DB_RECEIVER_VERSION:
                    strBuf = ByteUtil.byteArrayToString(Variables.rsReceiveVersion);
                    break;
                default:
                    break;
            }
            ReceiverSysInfo[i].setValue(strBuf);
        }

        for (int i = 0; i < ReceiverCommon.length; i++) {
            switch (ReceiverCommon[i].getId()) {
                case Variables.DB_RX_POWER:
                    strBuf = String.valueOf(Variables.rsBestRSSI);
                    break;
                case Variables.DB_RSSI:
                    strBuf = String.valueOf(Variables.rsBestRSCP);
                    break;
                case Variables.DB_BEST_PATHLOSS:
                    strBuf = String.valueOf(ByteUtil.toInt(Variables.rsBestPathLoss));
                    break;
                case Variables.DB_PATHLOSS_REF:
                    strBuf = String.valueOf(Variables.rtPathLossRef);
                    break;
                default:
                    break;
            }
            ReceiverCommon[i].setValue(strBuf);
        }

        for (int i = 0; i < ReceiverDownlink.length; i++) {
            switch (ReceiverDownlink[i].getId()) {
                case Variables.DB_ACTIVE_PLMN:
                    strBuf = ByteUtil.byteArrayToString(Variables.rsActivePLMN);
                    break;
                case Variables.DB_PLMN_ID1:
                    strBuf = ByteUtil.byteArrayToString(Variables.rsPLMNID[0]);
                    break;
                case Variables.DB_PLMN_ID2:
                    strBuf = ByteUtil.byteArrayToString(Variables.rsPLMNID[1]);
                    break;
                case Variables.DB_PLMN_ID3:
                    strBuf = ByteUtil.byteArrayToString(Variables.rsPLMNID[2]);
                    break;
                case Variables.DB_PLMN_ID4:
                    strBuf = ByteUtil.byteArrayToString(Variables.rsPLMNID[3]);
                    break;
                case Variables.DB_ACTIVE_CHANNEL:
                    strBuf = String.valueOf(ByteUtil.toInt(Variables.rsActiveChannel));
                    break;
                case Variables.DB_CHANNEL1:
                    strBuf = String.valueOf(ByteUtil.toInt(Variables.rsChannel[0]));
                    break;
                case Variables.DB_CHANNEL2:
                    strBuf = String.valueOf(ByteUtil.toInt(Variables.rsChannel[1]));
                    break;
                case Variables.DB_CHANNEL3:
                    strBuf = String.valueOf(ByteUtil.toInt(Variables.rsChannel[2]));
                    break;
                case Variables.DB_CHANNEL4:
                    strBuf = String.valueOf(ByteUtil.toInt(Variables.rsChannel[3]));
                    break;
                default:
                    break;
            }
            ReceiverDownlink[i].setValue(strBuf);
        }

        for (int i = 0; i < ReceiverUplink.length; i++) {
            switch (ReceiverUplink[i].getId()) {
                case Variables.DB_PSC1:
                    strBuf = String.valueOf(ByteUtil.toInt(Variables.rsPSC[0]));
                    break;
                case Variables.DB_RSCP1:
                    strBuf = Variables.rsRSCP[0] + "dBm";
                    break;
                case Variables.DB_PSC_ECIO1:
                    strBuf = Variables.rsPSCEcIo[0] + "dB";
                    break;
                case Variables.DB_CPICH_POWER1:
                    strBuf = Variables.rsCPICHPower[0] + "dBm";
                    break;
                case Variables.DB_PATHLOSS1:
                    strBuf = Variables.rsPathLoss[0] + "dB";
                    break;
                case Variables.DB_PSC2:
                    strBuf = String.valueOf(ByteUtil.toInt(Variables.rsPSC[1]));
                    break;
                case Variables.DB_RSCP2:
                    strBuf = Variables.rsRSCP[1] + "dBm";
                    break;
                case Variables.DB_PSC_ECIO2:
                    strBuf = Variables.rsPSCEcIo[1] + "dB";
                    break;
                case Variables.DB_CPICH_POWER2:
                    strBuf = Variables.rsCPICHPower[1] + "dBm";
                    break;
                case Variables.DB_PATHLOSS2:
                    strBuf = Variables.rsPathLoss[1] + "dB";
                    break;
                case Variables.DB_PSC3:
                    strBuf = String.valueOf(ByteUtil.toInt(Variables.rsPSC[2]));
                    break;
                case Variables.DB_RSCP3:
                    strBuf = Variables.rsRSCP[2] + "dBm";
                    break;
                case Variables.DB_PSC_ECIO3:
                    strBuf = Variables.rsPSCEcIo[2] + "dB";
                    break;
                case Variables.DB_CPICH_POWER3:
                    strBuf = Variables.rsCPICHPower[2] + "dBm";
                    break;
                case Variables.DB_PATHLOSS3:
                    strBuf = Variables.rsPathLoss[2] + "dB";
                    break;
                case Variables.DB_PSC4:
                    strBuf = String.valueOf(ByteUtil.toInt(Variables.rsPSC[3]));
                    break;
                case Variables.DB_RSCP4:
                    strBuf = Variables.rsRSCP[3] + "dBm";
                    break;
                case Variables.DB_PSC_ECIO4:
                    strBuf = Variables.rsPSCEcIo[3] + "dB";
                    break;
                case Variables.DB_CPICH_POWER4:
                    strBuf = Variables.rsCPICHPower[3] + "dBm";
                    break;
                case Variables.DB_PATHLOSS4:
                    strBuf = Variables.rsPathLoss[3] + "dB";
                    break;
                default:
                    break;
            }
            ReceiverUplink[i].setValue(strBuf);
        }
    }
 */
//
//    private void Update_TsyncTab() {
//        DataType[] TsyncAlarm = subTitleNameClass.getTsyncSubTitle().getTsyncAlarmSubTitle();
//        DataType[] TsyncInfo = subTitleNameClass.getTsyncSubTitle().getTsyncInfoSubTitle();
//        DataType[] TsyncConf = subTitleNameClass.getTsyncSubTitle().getTsyncConfigureSubTitle();
//
//        String strBuf = null;
//
//        for (int i = 0; i < TsyncAlarm.length; i++) {
//            switch (TsyncAlarm[i].getId()) {
//                case Variables.DB_CRW_TSYNC_LINK: // Alarm
////                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.aTsyncLink);
//                    if (Variables.bandStruct[Variables.band].tsync.aTsyncLink == 1) strBuf = "Fail";
//                    else strBuf = "Linked";
//                    break;
//                case Variables.DB_CRW_TSYNC_LOCK:
////                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.aTsyncLock);
//                    if (Variables.bandStruct[Variables.band].tsync.aTsyncLock == 1) strBuf = "Unlock";
//                    else strBuf = "Lock";
//                    break;
//                default:
//                    break;
//            }
//            TsyncAlarm[i].setValue(strBuf);
//        }
//
//        for (int i = 0; i < TsyncInfo.length; i++) {
//            switch (TsyncInfo[i].getId()) {
//                case Variables.DB_CRW_TSYNC_VENDOR:
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.iVendor);
//                    break;
//                case Variables.DB_CRW_TSYNC_FPGA_VER:
//                    strBuf = String.format("v%x.%x", Variables.bandStruct[Variables.band].tsync.iFpgaVer / 0x10, Variables.bandStruct[Variables.band].tsync.iFpgaVer & 0x0F);
//                    break;
//                case Variables.DB_CRW_TSYNC_FW_VER:
//                    strBuf = String.format("v%x.%x", Variables.bandStruct[Variables.band].tsync.iFwVer / 0x10, Variables.bandStruct[Variables.band].tsync.iFwVer & 0x0F);
//                    break;
//                case Variables.DB_CRW_TSYNC_RX_POWER:
//                    strBuf = String.format("%d.%d", Variables.bandStruct[Variables.band].tsync.iRxPower / 10, Math.abs(Variables.bandStruct[Variables.band].tsync.iRxPower) % 10) + "dBm";
//                    break;
//                case Variables.DB_CRW_TSYNC_PCI_VALUE:
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.iPciValue);
//                    break;
//                case Variables.DB_CRW_TSYNC_RSSI:
//                    strBuf = Variables.bandStruct[Variables.band].tsync.iRssi + "dBm";
////                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.iRssi);
//                    break;
//                case Variables.DB_CRW_TSYNC_SSB_INDEX:
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.iSSBindex);
//                    break;
//                case Variables.DB_CRW_TSYNC_TEMP:
//                    strBuf = Variables.bandStruct[Variables.band].tsync.iTemp + "'C";
//                    break;
//                case Variables.DB_CRW_TSYNC_SSB_RSRP:
//                    strBuf = Variables.bandStruct[Variables.band].tsync.iSSBrsrp + "dBm";
////                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.iSSBrsrp);
//                    break;
//                case Variables.DB_CRW_TSYNC_INPUT_POWER:
//                    strBuf = String.format("%d.%d", Variables.bandStruct[Variables.band].tsync.iInputPower / 10, Math.abs(Variables.bandStruct[Variables.band].tsync.iInputPower) % 10) + "dBm";
//                    break;
//                default:
//                    break;
//            }
//            TsyncInfo[i].setValue(strBuf);
//        }
//
//        for (int i = 0; i < TsyncConf.length; i++) {
//            switch (TsyncConf[i].getId()) {
//                case Variables.DB_CRW_TSYNC_TDD_MODE:
//                    if (Variables.bandStruct[Variables.band].tsync.tTddMode == 0) strBuf = "DL Only";
//                    else if(Variables.bandStruct[Variables.band].tsync.tTddMode == 1) strBuf = "UL Only";
//                    else strBuf = "TDD";
////                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.tTddMode);
//                    break;
//                case Variables.DB_CRW_TSYNC_DLOFF_TIME:
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.tDlOffTime);
//                    break;
//                case Variables.DB_CRW_TSYNC_ULOFF_TIME:
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.tUlOffTime);
//                    break;
//                case Variables.DB_CRW_TSYNC_DLON_TIME:
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.tDlOnTime);
//                    break;
//                case Variables.DB_CRW_TSYNC_ULON_TIME:
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.tUlOnTime);
//                    break;
//                case Variables.DB_CRW_TSYNC_TDD_ULDL_CONF:
//                    switch(Variables.bandStruct[Variables.band].tsync.tDlUlConfigure){
//                        case 0: strBuf = "FR1.30-1 (7DS2U)"; break;
//                        case 1: strBuf = "FR1.30-2 (DDDSU)"; break;
//                        case 2: strBuf = "FR1.30-3 (DDDSUDDSUU)"; break;
//                        case 3: strBuf = "FR1.30-4 (DDDSUUDDDD)"; break;
//                        case 4: strBuf = "FR1.30-5 (DSUU)"; break;
//                        case 5: strBuf = "FR1.30-6 (DS1S2U)"; break;
//                        default: break;
//                    }
////                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.tDlUlConfigure);
//                    break;
//                case Variables.DB_CRW_TSYNC1_OUT_SEL:
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.tTsync1OutSel);
//                    break;
//                case Variables.DB_CRW_TSYNC2_OUT_SEL:
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.tTsync2OutSel);
//                    break;
//                case Variables.DB_CRW_TSYNC3_OUT_SEL:
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.tTsync3OutSel);
//                    break;
//                case Variables.DB_CRW_TSYNC4_OUT_SEL:
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.tTsync4OutSel);
//                    break;
//                case Variables.DB_CRW_TSYNC_CENTER_FREQ:
//                    strBuf = String.format("%d.%02d", Math.abs(Variables.bandStruct[Variables.band].tsync.tCenterFreq) / 100, Math.abs(Variables.bandStruct[Variables.band].tsync.tCenterFreq) % 100);
//                    break;
//                case Variables.DB_CRW_TSYNC_BAND_SELECT:
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.tBandSel);
//                    break;
//                case Variables.DB_CRW_TSYNC_BANDWIDTH:
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.tBandWidth);
//                    break;
//                case Variables.DB_CRW_TSYNC_SSB_SEARCH_MODE:
//                    strBuf = String.valueOf(Variables.bandStruct[Variables.band].tsync.tSSBsearchMode);
//                    break;
//                default:
//                    break;
//            }
//            TsyncConf[i].setValue(strBuf);
//        }
//    }


}
