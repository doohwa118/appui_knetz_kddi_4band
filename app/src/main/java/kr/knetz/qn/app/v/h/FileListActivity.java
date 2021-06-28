package kr.knetz.qn.app.v.h;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import kr.knetz.qn.app.R;
import kr.knetz.qn.app.l.Debug;
import kr.knetz.qn.app.v.c.FileListView;
import kr.knetz.qn.app.v.t.OnFileSelectedListener;
import kr.knetz.qn.app.v.t.OnPathChangedListener;
import kr.knetz.qn.app.v.x.Variables;



public class FileListActivity extends Activity {
    static int kill = 0;
    String FilePath = "/sdcard/";
    FileListView mFileList;
    Messenger mService = null;
    boolean mIsBound = false;

    String FileName;
    final Messenger mMessenger = new Messenger(new IncomingHandler(FileListActivity.this));

    static class IncomingHandler extends Handler {
        private final WeakReference<FileListActivity> mActivity;
        IncomingHandler(FileListActivity service){
            mActivity = new WeakReference<FileListActivity>(service);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FileListActivity activity = mActivity.get();
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
                break;
            case Variables.MSG_SERVICE_TO_LOGINACTIVITY_BLUETOOTH_CHECK :
                Debug.loge(new Exception(),"MSG_SERVICE_TO_LOGINACTIVITY_BLUETOOTH_CHECK.");
                break;

        }
    }

    private void sendToServiceFinish(){
        Message msg = Message.obtain(null, Variables.MSG_FILELISTACTIVITY_TO_SERVICE_FINISH);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
//        Debug.logi(new Exception(),"=dhjung=======> FileListActivity onUserLeaveHint");

  //      doUnbindService();
 //       finish();

 //       sendToServiceFinish();

  //      android.os.Process.killProcess(android.os.Process.myPid());

     ActivityCompat.finishAffinity(this);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Debug.logi(new Exception(),"=dhjung=======> FileListActivity onCreate");

        setContentView(R.layout.activity_file_explorer);

        doBindService();

        mFileList = new FileListView(this);

        mFileList.setOnPathChangedListener(new OnPathChangedListener() {
            @Override
            public void onChanged(String path) {

                ((TextView)findViewById(R.id.FilePath)).setText("Path: " + path);
                FilePath = path;
            }
        });

        mFileList.setOnFileSelected(new OnFileSelectedListener() {
            @Override
            public void onSelected(String path, String fileName) {

                ((TextView)findViewById(R.id.FilePath)).setText("Path: " + path + fileName);

                if(fileName.matches(".*bin")) {
                    Debug.loge(new Exception(), "FileName: " + fileName + ", Path: " + mFileList.getPath());
                    FileName = mFileList.getPath() + fileName;

                    final AlertDialog.Builder builder = new AlertDialog.Builder(FileListActivity.this);
                    builder.setTitle("Download File Selected!!");

                    builder.setMessage("Do you want to download this file?\n"+ "" +fileName);
                    builder.setCancelable(false);

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Message msg = Message.obtain(null, Variables.MSG_FILELISTACTIVITY_TO_SERVICE_PATH);
                                Bundle data = new Bundle();
                                data.putString("path", FileName);
                                msg.setData(data);
                                mService.send(msg);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                            FileListActivity.this.setResult(RESULT_OK);
                            //Debug.loge(new Exception(), "==> Service Unbind & Finish");

                            doUnbindService();
                            //moveTaskToBack(true);
                            finish();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();

                }
            }
        });


        LinearLayout layout = findViewById(R.id.FileExplorer);
        layout.addView(mFileList);

        mFileList.setPath(FilePath);
        mFileList.setFocusable(true);
        mFileList.setFocusableInTouchMode(true);
    }

    public void mFinish(){
        doUnbindService();
        //moveTaskToBack(true);
        finish();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            if(kill == 0){
                kill++;
                Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
            }else{
                mFinish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
//            Debug.loge(new Exception(),"dhjung FileList Attached.");
            try {
                Message msg = Message.obtain(null, Variables.MSG_REGISTER_CLIENT_FILELISTACTIVITY);
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
        bindService(new Intent(FileListActivity.this, DeviceService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }
}
