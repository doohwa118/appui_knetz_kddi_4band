package kr.knetz.kddi.app.v.h;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import kr.knetz.kddi.app.KNetzApp;
import kr.knetz.kddi.app.R;
import kr.knetz.kddi.app.l.Debug;
import kr.knetz.kddi.app.v.x.Variables;


public class ConnectActivity extends Activity {
    public ConnectActivity(){
        super();
    }
    public static Activity mConnectActivity;

    KNetzApp application;

    Messenger mService = null;
    boolean mIsBound = false; // MainActivity <-> Service Binding Check
    final Messenger mMessenger = new Messenger(new IncomingHandler(ConnectActivity.this));

    static class IncomingHandler extends Handler {
        private final WeakReference<ConnectActivity> mActivity;
        IncomingHandler(ConnectActivity service){
            mActivity = new WeakReference<ConnectActivity>(service);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ConnectActivity activity = mActivity.get();
            if (activity != null){
                activity.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg){
        switch (msg.what) {
            case Variables.MSG_SERVICE_TO_CONNECTACTIVITY_DATA :
                Debug.loge(new Exception(),"MSG_SERVICE_TO_CONNECTACTIVITY_DATA.");
//                byte[] recvData = (byte[])msg.obj;
//                parseRcvData (recvData);
                break;
            case Variables.MSG_SERVICE_TO_CONNECTACTIVITY_CABLE_NOT_CONNECT :
                Debug.loge(new Exception(),"MSG_SERVICE_TO_CONNECTACTIVITY_CABLE_NOT_CONNECT.");
                Toast.makeText(getBaseContext(),"Cable Not Connected!!!",Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(1000L);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            Debug.loge(new Exception(), "Attached.");
            try {
                Message msg = Message.obtain(null, Variables.MSG_REGISTER_CLIENT_CONNECTACTIVITY);
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

    void doBindService(Intent intent) {
//        bindService(new Intent(ConnectActivity.this, DeviceConnectingService.class), mConnection, Context.BIND_AUTO_CREATE);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Debug.logi(new Exception(),"=dhjung=======> ConnectActivity onDestroy");

        doUnbindService();
    }

    private Handler confirmHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //완료 후 실행할 처리 삽입
//            Debug.logi(new Exception(),"=dhjung=======> ConnectActivity confirmHandler: "+ msg.what);

            mConnectActivity = ConnectActivity.this;
//            Intent intent = new Intent(ConnectActivity.this, DeviceConnectingService.class);
////            startService(new Intent(ConnectActivity.this, DeviceConnectingService.class));
//            if (!mIsBound) {
//                startService(intent);
//                doBindService(intent);
//            }
            Intent intent = new Intent(ConnectActivity.this, DeviceService.class);
            startService(intent);
            doBindService(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connect);
        application = (KNetzApp)getApplication();
        if (getActionBar() != null)
            getActionBar().hide();

//        Debug.logi(new Exception(),"=dhjung=======> ConnectActivity onCreate START: " + +application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle().length);

        if (application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle().length <= 0 ) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean flag = true;
                    int cnt = 0;
                    int nFlag = 0;
                    while (flag) {
//                        Debug.logi(new Exception(),"=dhjung=======> ConnectActivity run while: " + +application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle().length);
                        if (application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle().length > 0) {
                            break;
                        }
//                        Debug.logi(new Exception(),"=dhjung=======> ConnectActivity run len: " + application.getSubTitleNameClass().getAlarmSubTitle().getSystemInformationSubTitle().length);
//                        Debug.logi(new Exception(),"=dhjung=======> ConnectActivity run cnt: " + cnt);

                        if (cnt >= 15) {
                            break;
                        }
                        try {
                            cnt++;
                            Thread.sleep(500L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
//                    Debug.logi(new Exception(),"=dhjung=======> ConnectActivity run break ");

                    confirmHandler.sendEmptyMessage(0);
                }
            }).start();
        }else{
            confirmHandler.sendEmptyMessage(0);
        }
    }
}
