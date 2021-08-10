package kr.knetz.qn.app.v;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import kr.knetz.qn.app.R;
import kr.knetz.qn.app.l.ByteUtil;
import kr.knetz.qn.app.l.Debug;
import kr.knetz.qn.app.v.h.ExpandableView;
import kr.knetz.qn.app.v.t.ModemGridAdapter;
import kr.knetz.qn.app.v.x.CustomDialog;
import kr.knetz.qn.app.v.c.DataType;
import kr.knetz.qn.app.v.t.ItemList;
import kr.knetz.qn.app.v.t.SubTitleNameClass;
import kr.knetz.qn.app.v.x.Variables;

@SuppressLint("ValidFragment")
public class Tab4_Modem extends Fragment implements View.OnClickListener {

    public interface OnModemSendListener {
        void onModemSettingSend(int type);
    }
    Activity mainActivity;
    Context mContext;
    View mView;
    OnModemSendListener mListener;
    boolean resultFlag = false;

    public Tab4_Modem(Context context) {
        mContext = context;
    }

    public Tab4_Modem() {
        super();
    }

    private ArrayList<ItemList>[] list = new ArrayList[arrGridViewItemList.length];
    private ArrayList<SubTitleNameClass.SettingType>[] settingTypeList = new ArrayList[arrGridViewItemList.length];
    private ItemList mModemList;

    private ExpandableView[] arrGridViewList;
    private ModemGridAdapter[] arrModemGridAdapter;

    private Handler mHandler;
    private ProgressDialog mProgressDialog;

//    public static final byte[][] arrHeartBeatSetting = {
//            {0x00,(byte)0x05,0x00}, // 5min
//            {0x00,(byte)0x10,0x00}, // 10min
//            {0x00,(byte)0x20,0x00}, // 20min
//            {0x00,(byte)0x30,0x00}, // 30min
//            {(byte)0x01,0x00,0x00}, // 1Hour
//            {(byte)0x02,0x00,0x00}, // 2Hour
//            {(byte)0x04,0x00,0x00}, // 4Hour
//            {(byte)0x06,0x00,0x00}, // 6Hour
//            {(byte)0x12,0x00,0x00}, // 12Hour
//            {(byte)0x24,0x00,0x00}, // 1 day
//            {(byte)0x48,0x00,0x00}, // 2 day
//            {(byte)0x96,0x00,0x00}, // 4 day
//
//    };

    static final int[] arrTextViewSubTitle = {
            R.id.tv_sub_title1,
            R.id.tv_sub_title2,
            R.id.tv_sub_title3,
            R.id.tv_sub_title4,
            R.id.tv_sub_title5,
            R.id.tv_sub_title6

    };
    static int[] arrTextViewSubTitleName = {};

    static final int[] arrTextViewSubTitleModemName = {
            R.string.modem_sub_title1,
            R.string.modem_sub_title2,
            R.string.modem_sub_title3,
            R.string.modem_sub_title4,
            R.string.modem_sub_title5,
            R.string.modem_sub_title6
    };
    static final int[] arrGridViewItemList = {
            R.id.gridview_body_sub1,
            R.id.gridview_body_sub2,
            R.id.gridview_body_sub3,
            R.id.gridview_body_sub4,
            R.id.gridview_body_sub5,
            R.id.gridview_body_sub6
    };

//    public static byte[][] getArrHeartBeatSetting() {
//        return arrHeartBeatSetting;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arrTextViewSubTitleName = arrTextViewSubTitleModemName;

        dataToArrayList();

        arrGridViewList = new ExpandableView[arrGridViewItemList.length];
        arrModemGridAdapter = new ModemGridAdapter[arrGridViewItemList.length];

        mHandler = new Handler();
        mainActivity = getActivity();
    }

    private Handler confirmHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //완료 후 실행할 처리 삽입
            if (resultFlag)
                Toast.makeText(getActivity().getBaseContext(),"Update Success!!",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity().getBaseContext(),"Update Fail! Timeout....",Toast.LENGTH_SHORT).show();
            doRefreshGridViewList();

        }
    };

    CustomDialog.DialogReturnValueListener dialogReturnValueListener = new CustomDialog.DialogReturnValueListener() {

        @Override
        public void onFinishInputDialog(DataType dataType) {
            switch (dataType.getId()) {
                case Variables.ARRAYLIST_MENUTYPE_QN_MODEM_PARAM:
                    break;
                case Variables.ARRAYLIST_MENUTYPE_QN_CELL_INFO:
                    break;
                case Variables.ARRAYLIST_MENUTYPE_QN_REMOTE:
                    DataType[] modemRemote = MainActivity.subTitleNameClass.getModemSubTitle().getModemRemoteSubTitle();
                    MainActivity.modemFrameInfo = 0x01;

                    for (int i = 0; i < modemRemote.length; i++){
                        if (modemRemote[i].getName().equals(dataType.getName())) {
                            modemRemote[i].setValue(dataType.getValue());
                            Debug.loge(new Exception(), "modemRemote getId() : " + modemRemote[i].getId());
                            switch (modemRemote[i].getId()) {
                                case Variables.DB_MODEM_LOCAL_PHONE_NUM:
                                    Debug.loge(new Exception(),"modem DB_LOCAL_PHONE_NUM : "+dataType.getValue() + ", Len: " + dataType.getValue().length());
                                    if (dataType.getValue().getBytes().length >= 0) {
                                        if (dataType.getValue().getBytes().length > Variables.modemStruct.modem.rLocalPhoneNum.length) {
                                            System.arraycopy(dataType.getValue().getBytes(), 0, Variables.modemStruct.modem.rLocalPhoneNum, 0, Variables.modemStruct.modem.rLocalPhoneNum.length);
                                        } else {
                                            Arrays.fill(Variables.modemStruct.modem.rLocalPhoneNum, 0, 16, (byte)0);
                                            System.arraycopy(dataType.getValue().getBytes(), 0, Variables.modemStruct.modem.rLocalPhoneNum, 0, dataType.getValue().length());
                                        }
                                        Debug.loge(new Exception(),"modem DB_LOCAL_PHONE_NUM 2 : "+ByteUtil.byteArrayToString(Variables.modemStruct.modem.rLocalPhoneNum));
                                    }
                                    break;
                                case Variables.DB_MODEM_RCS_PHONE_NUM:
                                    Debug.loge(new Exception(),"modem DB_RCS_PHONE_NUM : "+dataType.getValue() + ", Len: " + dataType.getValue().length());
                                    if (dataType.getValue().getBytes().length >= 0) {
                                        if (dataType.getValue().getBytes().length > Variables.modemStruct.modem.rRcsPhoneNum.length) {
                                            System.arraycopy(dataType.getValue().getBytes(), 0, Variables.modemStruct.modem.rRcsPhoneNum, 0, Variables.modemStruct.modem.rRcsPhoneNum.length);
                                        } else {
                                            Arrays.fill(Variables.modemStruct.modem.rRcsPhoneNum, 0, 16, (byte)0);
                                            System.arraycopy(dataType.getValue().getBytes(), 0, Variables.modemStruct.modem.rRcsPhoneNum, 0, dataType.getValue().length());
                                        }
                                        Debug.loge(new Exception(),"modem DB_RCS_PHONE_NUM 2 : "+ByteUtil.byteArrayToString(Variables.modemStruct.modem.rRcsPhoneNum));
                                    }
                                    break;
                                case Variables.DB_MODEM_PERIODIC_REPORT:
                                    Debug.loge(new Exception(),"modem DB_PERIODIC_REPORT : "+dataType.getValue());
                                    Variables.modemStruct.modem.rPeriodicReport = Variables.ARRAY_HEARTBEATTIME_SETTING[Integer.parseInt(dataType.getValue())];
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    break;

                case Variables.ARRAYLIST_MENUTYPE_QN_SIM:
                    DataType[] modemSim = MainActivity.subTitleNameClass.getModemSubTitle().getModemSimSubTitle();
                    MainActivity.modemFrameInfo = 0x02;
                    for (int i = 0; i < modemSim.length; i++) {
                        if (modemSim[i].getName().equals(dataType.getName())) {
                            modemSim[i].setValue(dataType.getValue());
                            Debug.loge(new Exception(), "modemSim getId() : " + modemSim[i].getId());
                            switch (modemSim[i].getId()) {
                                case Variables.DB_MODEM_PIN_LOCK:
                                    Debug.loge(new Exception(),"modem DB_PIN_LOCK : "+dataType.getValue());
                                    Variables.modemStruct.modem.sPinLock = Byte.parseByte(dataType.getValue());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    break;

                case Variables.ARRAYLIST_MENUTYPE_QN_NETWORK:
                    DataType[] modemNetwork = MainActivity.subTitleNameClass.getModemSubTitle().getModemNetworkSubTitle();
                    MainActivity.modemFrameInfo = 0x04;
                    for (int i = 0; i < modemNetwork.length; i++) {
                        if (modemNetwork[i].getName().equals(dataType.getName())) {
                            modemNetwork[i].setValue(dataType.getValue());
                            Debug.loge(new Exception(), "modemNetwork getId() : " + modemNetwork[i].getId());
                            switch (modemNetwork[i].getId()) {
                                case Variables.DB_MODEM_APN:
                                    Debug.loge(new Exception(),"modem DB_MODEM_APN : "+dataType.getValue() + ", Len: " + dataType.getValue().length());
                                    if (dataType.getValue().getBytes().length >= 0) {
                                        if (dataType.getValue().getBytes().length > Variables.modemStruct.modem.nApn.length) {
                                            System.arraycopy(dataType.getValue().getBytes(), 0, Variables.modemStruct.modem.nApn, 0, Variables.modemStruct.modem.nApn.length);
                                        } else {
                                            Arrays.fill(Variables.modemStruct.modem.nApn, 0, 30, (byte)0);
                                            System.arraycopy(dataType.getValue().getBytes(), 0, Variables.modemStruct.modem.nApn, 0, dataType.getValue().length());
                                        }
                                        Debug.loge(new Exception(),"modem DB_MODEM_APN 2 : "+ByteUtil.byteArrayToString(Variables.modemStruct.modem.nApn));
                                    }
                                    break;
                                case Variables.DB_MODEM_USER_ID:
                                    Debug.loge(new Exception(),"modem DB_USER_ID : "+dataType.getValue() + ", Len: " + dataType.getValue().length());
                                    if (dataType.getValue().getBytes().length >= 0) {
                                        if (dataType.getValue().getBytes().length > Variables.modemStruct.modem.nUserId.length) {
                                            System.arraycopy(dataType.getValue().getBytes(), 0, Variables.modemStruct.modem.nUserId, 0, Variables.modemStruct.modem.nUserId.length);
                                        } else {
                                            Arrays.fill(Variables.modemStruct.modem.nUserId, 0, 30, (byte)0);
                                            System.arraycopy(dataType.getValue().getBytes(), 0, Variables.modemStruct.modem.nUserId, 0, dataType.getValue().length());
                                        }
                                        Debug.loge(new Exception(),"modem DB_USER_ID 2 : "+ByteUtil.byteArrayToString(Variables.modemStruct.modem.nUserId));
                                    }
                                    break;
                                case Variables.DB_MODEM_USER_PASSWORD:
                                    Debug.loge(new Exception(),"modem DB_USER_PASSWORD : "+dataType.getValue() + ", Len: " + dataType.getValue().length());
                                    if (dataType.getValue().getBytes().length >= 0) {
                                        if (dataType.getValue().getBytes().length > Variables.modemStruct.modem.nPassword.length) {
                                            System.arraycopy(dataType.getValue().getBytes(), 0, Variables.modemStruct.modem.nPassword, 0, Variables.modemStruct.modem.nPassword.length);
                                        } else {
                                            Arrays.fill(Variables.modemStruct.modem.nPassword, 0, 20, (byte)0);
                                            System.arraycopy(dataType.getValue().getBytes(), 0, Variables.modemStruct.modem.nPassword, 0, dataType.getValue().length());
                                        }
                                        Debug.loge(new Exception(),"modem DB_USER_PASSWORD 2 : "+ByteUtil.byteArrayToString(Variables.modemStruct.modem.nPassword));
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    break;

                case Variables.ARRAYLIST_MENUTYPE_QN_EMS:
                    DataType[] modemEms = MainActivity.subTitleNameClass.getModemSubTitle().getModemEMSSubTitle();
                    MainActivity.modemFrameInfo = 0x08;
                    for (int i = 0; i < modemEms.length; i++) {
                        if (modemEms[i].getName().equals(dataType.getName())) {
                            modemEms[i].setValue(dataType.getValue());
                            Debug.loge(new Exception(), "modemEms getId() : " + modemEms[i].getId());
                            switch (modemEms[i].getId()) {
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
                                    Debug.loge(new Exception(),"modem DB_RCS_IP_ADDR : "+dataType.getValue() + ", Len: " + dataType.getValue().length());
                                    if (dataType.getValue().getBytes().length >= 0) {
                                        if (dataType.getValue().getBytes().length > Variables.modemStruct.modem.eRcsIpAddr[i].length) {
                                            System.arraycopy(dataType.getValue().getBytes(), 0, Variables.modemStruct.modem.eRcsIpAddr[i], 0, Variables.modemStruct.modem.eRcsIpAddr[i].length);
                                        } else {
                                            Arrays.fill(Variables.modemStruct.modem.eRcsIpAddr[i], 0, 50, (byte)0);
                                            System.arraycopy(dataType.getValue().getBytes(), 0, Variables.modemStruct.modem.eRcsIpAddr[i], 0, dataType.getValue().length());
                                        }
                                        Debug.loge(new Exception(),"modem DB_RCS_IP_ADDR 2 : "+ByteUtil.byteArrayToString(Variables.modemStruct.modem.eRcsIpAddr[i]));
                                    }
                                    break;
                                case Variables.DB_MODEM_RCS_PORT:
                                    Debug.loge(new Exception(),"modem DB_RCS_PORT : "+dataType.getValue());
                                    Variables.modemStruct.modem.eRcsPort = Short.parseShort(dataType.getValue());
                                    break;

                                default:
                                    break;
                            }
                        }
                    }
                    break;

                default:
                    break;
            }
            Variables.modemSendFlag = true;

            mListener.onModemSettingSend(Variables.DIALOG_TYPE_SEEKBAR);

            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("Updating.... Please wait....");
            mProgressDialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean flag = true;
                    int cnt = 0;

                    while (flag) {

                        if (cnt >= 10) {
                            resultFlag = false;
                            break;
                        }

                        try {
                            cnt++;
                            Thread.sleep(500L);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (Variables.modemSettingFlag == -103) {
                            flag = false;
                            Variables.modemSettingFlag = (byte) 0x00;
                            resultFlag = true;
                            break;
                        }
                        Debug.logi(new Exception(),"=dhjung=======> Variables.modemSettingFlag : " + Variables.modemSettingFlag + ", " + cnt);

                    }
                    Debug.logi(new Exception(),"=dhjung=======> while break");

                    confirmHandler.sendEmptyMessage(0);

                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }
            }).start();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Debug.logd(new Exception(), "onCreateView");

        View view = inflater.inflate(R.layout.activity_tab4_modem, null);
        mView = view;

//        int gridCount = 0;
        for(int tvId : arrTextViewSubTitle){
            TextView tv = view.findViewById(tvId);
//            tv.setText(arrTextViewSubTitleName[gridCount]);
            tv.setOnClickListener(this);
//            gridCount++;
        }
        Debug.loge(new Exception(), "onCreate View GridView START");
        int gridCount = 0;
        for(int gridId : arrGridViewItemList){
            Debug.loge(new Exception(), "GridView gridCount : " + gridCount + " gridId : " + gridId);
            ExpandableView gv = view.findViewById(gridId);
            ModemGridAdapter adapter = new ModemGridAdapter(view.getContext(), gridId, list[gridCount], settingTypeList[gridCount] ,dialogReturnValueListener,gridCount);
            gv.setAdapter(adapter);
            gv.setNumColumns(1);
            gv.setExpanded(true);
            arrModemGridAdapter[gridCount] = adapter;
            arrGridViewList[gridCount] = gv;
            gridCount++;
        }
        Debug.loge(new Exception(), "onCreate View GridView END");
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnModemSendListener) activity;
        }catch (Exception e){
            throw new ClassCastException(activity.toString()+" must implement OnSettingSendListener");
        }
    }

    @Override
    public void onClick(View v) {
        int count = 0;
        for (int tvId : arrTextViewSubTitle){
            if (v.getId() == tvId){
                if (arrGridViewList[count].isShown()) arrGridViewList[count].setVisibility(View.GONE);
                else arrGridViewList[count].setVisibility(View.VISIBLE);
            }
            count++;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        doRefreshGridViewList();

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, Variables.REFRESH_TIMEOUT_MILLIS);
    }

    public void doRefreshGridViewList(){

        dataToArrayList();
        int gridCount = 0;
        for(int gridId : arrGridViewItemList) {
            arrModemGridAdapter[gridCount] = new ModemGridAdapter(mView.getContext(), gridId, list[gridCount], settingTypeList[gridCount],dialogReturnValueListener,gridCount);
            arrGridViewList[gridCount].setAdapter(arrModemGridAdapter[gridCount]);
            gridCount++;
        }
    }

    private void dataToArrayList(){

        DataType[] modemParam = MainActivity.subTitleNameClass.getModemSubTitle().getModemParamSubTitle();
        DataType[] modemCellInfo = MainActivity.subTitleNameClass.getModemSubTitle().getModemCellInfoSubTitle();
        DataType[] modemRemote = MainActivity.subTitleNameClass.getModemSubTitle().getModemRemoteSubTitle();
        DataType[] modemSim = MainActivity.subTitleNameClass.getModemSubTitle().getModemSimSubTitle();
        DataType[] modemNetwork = MainActivity.subTitleNameClass.getModemSubTitle().getModemNetworkSubTitle();
        DataType[] modemEms = MainActivity.subTitleNameClass.getModemSubTitle().getModemEMSSubTitle();

        for(int i=0;i < arrGridViewItemList.length; i++){
            list[i] = new ArrayList<ItemList>();
            settingTypeList[i] = new ArrayList<SubTitleNameClass.SettingType>();
        }

        for(int i=0;i < modemParam.length; i++) {
            String tmpBuf;
            switch(modemParam[i].getId()){
                case Variables.DB_MODEM_LINK:
                case Variables.DB_MODEM_MODEL:
                case Variables.DB_MODEM_VERSION:
                case Variables.DB_MODEM_LOCAL_IP_ADDR:
                case Variables.DB_MODEM_USIM_STATUS:
                    tmpBuf = modemParam[i].getValue();
                    break;
                default:
                    tmpBuf = modemParam[i].getValue();
                    break;
            }
            mModemList = new ItemList(modemParam[i].getName(), tmpBuf);
            list[Variables.ARRAYLIST_MENUTYPE_QN_MODEM_PARAM].add(mModemList);
        }

        for(int i=0;i < modemCellInfo.length; i++) {
            String tmpBuf;
            switch(modemCellInfo[i].getId()){
                case Variables.DB_MODEM_REGI:
                case Variables.DB_MODEM_PLMN:
                case Variables.DB_MODEM_EARFCN:
                case Variables.DB_MDOEM_PCI:
                case Variables.DB_MODEM_RSSI:
                case Variables.DB_MODEM_RSRP:
                case Variables.DB_MODEM_RSRQ:
                    tmpBuf = modemCellInfo[i].getValue();
                    break;
                default:
                    tmpBuf = modemCellInfo[i].getValue();
                    break;
            }
            mModemList = new ItemList(modemCellInfo[i].getName(), tmpBuf);
            list[Variables.ARRAYLIST_MENUTYPE_QN_CELL_INFO].add(mModemList);
        }

        for(int i=0;i < modemRemote.length; i++) {
            String tmpBuf;
            if(Variables.HIDDEN_ENABLED) {
                boolean flag = false;
                for(int v : Variables.TAB4_REMOTE_HIDDEN_MENU_ID){
                    if(modemRemote[i].getId() == v){
                        flag = true;
                        break;
                    }
                }
                if(!flag) {
                    switch (modemRemote[i].getId()) {
                        case Variables.DB_MODEM_LOCAL_PHONE_NUM:
                        case Variables.DB_MODEM_RCS_PHONE_NUM:
                        case Variables.DB_MODEM_PERIODIC_REPORT:
                            tmpBuf = modemRemote[i].getValue();
                            break;
                        default:
                            tmpBuf = modemRemote[i].getValue();
                            break;
                    }
                    mModemList = new ItemList(modemRemote[i].getName(), tmpBuf);
                    list[Variables.ARRAYLIST_MENUTYPE_QN_REMOTE].add(mModemList);
                    settingTypeList[Variables.ARRAYLIST_MENUTYPE_QN_REMOTE].add(MainActivity.subTitleNameClass.getModemSubTitle().getmModemRemoteSettingType().get(i));
                }
            }else {
                switch (modemRemote[i].getId()) {
                    case Variables.DB_MODEM_LOCAL_PHONE_NUM:
                    case Variables.DB_MODEM_RCS_PHONE_NUM:
                    case Variables.DB_MODEM_PERIODIC_REPORT:
                        tmpBuf = modemRemote[i].getValue();
                        break;
                    default:
                        tmpBuf = modemRemote[i].getValue();
                        break;
                }
                mModemList = new ItemList(modemRemote[i].getName(), tmpBuf);
                list[Variables.ARRAYLIST_MENUTYPE_QN_REMOTE].add(mModemList);
                if (i == 0)
                    settingTypeList[Variables.ARRAYLIST_MENUTYPE_QN_REMOTE] = MainActivity.subTitleNameClass.getModemSubTitle().getmModemRemoteSettingType();
            }
        }

        for(int i=0;i < modemSim.length; i++) {
            String tmpBuf;
            if(Variables.HIDDEN_ENABLED) {
                boolean flag = false;
                for(int v : Variables.TAB4_SIM_HIDDEN_MENU_ID){
                    if(modemSim[i].getId() == v){
                        flag = true;
                        break;
                    }
                }
                if(!flag) {
                    switch (modemSim[i].getId()) {
                        case Variables.DB_MODEM_PIN_LOCK:
                            tmpBuf = modemSim[i].getValue();
                            break;
                        default:
                            tmpBuf = modemSim[i].getValue();
                            break;
                    }
                    mModemList = new ItemList(modemSim[i].getName(), tmpBuf);
                    list[Variables.ARRAYLIST_MENUTYPE_QN_SIM].add(mModemList);
                    settingTypeList[Variables.ARRAYLIST_MENUTYPE_QN_SIM].add(MainActivity.subTitleNameClass.getModemSubTitle().getmModemSimSettingType().get(i));
                }
            }else {
                switch (modemSim[i].getId()) {
                    case Variables.DB_MODEM_PIN_LOCK:
                        tmpBuf = modemSim[i].getValue();
                        break;
                    default:
                        tmpBuf = modemSim[i].getValue();
                        break;
                }
                mModemList = new ItemList(modemSim[i].getName(), tmpBuf);
                list[Variables.ARRAYLIST_MENUTYPE_QN_SIM].add(mModemList);
                if (i == 0)
                    settingTypeList[Variables.ARRAYLIST_MENUTYPE_QN_SIM] = MainActivity.subTitleNameClass.getModemSubTitle().getmModemSimSettingType();
            }
        }

        for(int i=0;i < modemNetwork.length; i++) {
            String tmpBuf;
            switch(modemNetwork[i].getId()){
                case Variables.DB_MODEM_APN:
                case Variables.DB_MODEM_USER_ID:
                    tmpBuf = modemNetwork[i].getValue();
                    break;
                case Variables.DB_MODEM_USER_PASSWORD:
                    tmpBuf = "****";
                    break;
                default:
                    tmpBuf = modemNetwork[i].getValue();
                    break;
            }
            mModemList = new ItemList(modemNetwork[i].getName(), tmpBuf);
            list[Variables.ARRAYLIST_MENUTYPE_QN_NETWORK].add(mModemList);
            settingTypeList[Variables.ARRAYLIST_MENUTYPE_QN_NETWORK].add(MainActivity.subTitleNameClass.getModemSubTitle().getmModemNetworkSettingType().get(i));
        }

        for(int i=0;i < modemEms.length; i++) {
            String tmpBuf;
            if(Variables.HIDDEN_ENABLED) {
                boolean flag = false;
                for(int v : Variables.TAB4_EMS_HIDDEN_MENU_ID){
                    if(modemEms[i].getId() == v){
                        flag = true;
                        break;
                    }
                }
                if(!flag) {
                    switch (modemEms[i].getId()) {
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
                        case Variables.DB_MODEM_RCS_PORT:
                            tmpBuf = modemEms[i].getValue();
                            break;
                        default:
                            tmpBuf = modemEms[i].getValue();
                            break;
                    }
                    mModemList = new ItemList(modemEms[i].getName(), tmpBuf);
                    list[Variables.ARRAYLIST_MENUTYPE_QN_EMS].add(mModemList);
                    settingTypeList[Variables.ARRAYLIST_MENUTYPE_QN_EMS].add(MainActivity.subTitleNameClass.getModemSubTitle().getmModemEMSSettingType().get(i));
                }
            }else {
                switch (modemEms[i].getId()) {
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
                    case Variables.DB_MODEM_RCS_PORT:
                        tmpBuf = modemEms[i].getValue();
                        break;
                    default:
                        tmpBuf = modemEms[i].getValue();
                        break;
                }
                mModemList = new ItemList(modemEms[i].getName(), tmpBuf);
                list[Variables.ARRAYLIST_MENUTYPE_QN_EMS].add(mModemList);
                if (i == 0)
                    settingTypeList[Variables.ARRAYLIST_MENUTYPE_QN_EMS] = MainActivity.subTitleNameClass.getModemSubTitle().getmModemEMSSettingType();
            }
        }
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //Debug.loge(new Exception(), "run");
            doRefreshGridViewList();
            mHandler.postDelayed(mRunnable, Variables.REFRESH_TIMEOUT_MILLIS);
        }
    };
}