package kr.knetz.kddi.app.v;

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

import kr.knetz.kddi.app.R;
import kr.knetz.kddi.app.l.Debug;
import kr.knetz.kddi.app.v.c.DataType;
import kr.knetz.kddi.app.v.h.ExpandableView;
import kr.knetz.kddi.app.v.t.ItemList;
import kr.knetz.kddi.app.v.t.SubTitleNameClass;
import kr.knetz.kddi.app.v.t.TsyncGridAdapter;
import kr.knetz.kddi.app.v.x.CustomDialog;
import kr.knetz.kddi.app.v.x.Variables;


@SuppressLint("ValidFragment")
public class Tab5_Tsync extends Fragment implements View.OnClickListener {

    public interface OnTsyncSendListener{
        void onTsyncSettingSend(int type);
    }

    Activity mainActivity;
    Context mContext;
    View mView;
    OnTsyncSendListener mListener;
    boolean resultFlag = false;


    public Tab5_Tsync(Context context){
        mContext = context;
    }

    public Tab5_Tsync(){
        super();
    }

    private ArrayList<ItemList>[] list = new ArrayList[arrGridViewItemList.length];
    private ArrayList<SubTitleNameClass.SettingType>[] settingTypeList = new ArrayList[arrGridViewItemList.length];
    private ItemList mTsyncList;

    private ExpandableView[] arrGridViewList;
    private TsyncGridAdapter[] arrTsyncGridAdapter;

    private Handler mHandler;
    private ProgressDialog mProgressDialog;

    static final int[] arrTextViewSubTitle = {
            R.id.tv_sub_title1,
            R.id.tv_sub_title2,
            R.id.tv_sub_title3
    };
    static int[] arrTextViewSubTitleName = {};

    static final int[] arrTextViewSubTitleTsyncName = {
            R.string.tsync_sub_title1,
            R.string.tsync_sub_title2,
            R.string.tsync_sub_title3
    };
    static final int[] arrGridViewItemList = {
            R.id.gridview_body_sub1,
            R.id.gridview_body_sub2,
            R.id.gridview_body_sub3
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arrTextViewSubTitleName = arrTextViewSubTitleTsyncName;

        dataToArrayList();

        arrGridViewList = new ExpandableView[arrGridViewItemList.length];
        arrTsyncGridAdapter = new TsyncGridAdapter[arrGridViewItemList.length];

        mHandler = new Handler();
        mainActivity = getActivity();
    }

    private Handler confirmHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //완료 후 실행할 처리 삽입
            if (resultFlag)
                Toast.makeText(getActivity().getBaseContext(), "Update Success!!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity().getBaseContext(), "Update Fail! Timeout....", Toast.LENGTH_SHORT).show();
            doRefreshGridViewList();
        }
    };

    CustomDialog.DialogReturnValueListener dialogReturnValueListener = new CustomDialog.DialogReturnValueListener() {

        @Override
        public void onFinishInputDialog(DataType dataType) {
            switch(dataType.getId()){
                case Variables.ARRAYLIST_MENUTYPE_TSYNC_ALARM:
                    break;
                case Variables.ARRAYLIST_MENUTYPE_TSYNC_INFO:
                    break;
                case Variables.ARRAYLIST_MENUTYPE_TSYNC_CONFIGURE:
                    DataType[] tsyncConfigure = MainActivity.subTitleNameClass.getTsyncSubTitle().getTsyncConfigureSubTitle();
                    MainActivity.tsyncFrameInfo = 0x01;
                    String[] arrayList;
                    for (int i = 0; i < tsyncConfigure.length; i++) {
                        if (tsyncConfigure[i].getName().equals(dataType.getName())) {
                            tsyncConfigure[i].setValue(dataType.getValue());
//                            Debug.loge(new Exception(), "tsyncConfigure getId() : " + tsyncConfigure[i].getId());
                            switch (tsyncConfigure[i].getId()) {
                                case Variables.DB_CRW_TSYNC_TDD_MODE:
                                    arrayList = mContext.getResources().getStringArray(R.array.set_tddmode);
                                    for(byte j=0; j< arrayList.length; j++){
                                        if(dataType.getValue().equals(arrayList[j])){
                                            Variables.bandStruct[Variables.band].tsync.tTddMode = j;
//                                            Debug.loge(new Exception(),"dhjung --> tTddMode: " + Variables.bandStruct[Variables.band].tsync.tTddMode);
                                            break;
                                        }
                                    }
//                                    Variables.bandStruct[Variables.band].tsync.tTddMode = Byte.parseByte(dataType.getValue());
                                    break;
                                case Variables.DB_CRW_TSYNC_DLOFF_TIME:
                                    Variables.bandStruct[Variables.band].tsync.tDlOffTime = Integer.parseInt(dataType.getValue());
                                    break;
                                case Variables.DB_CRW_TSYNC_ULOFF_TIME:
                                    Variables.bandStruct[Variables.band].tsync.tUlOffTime = Integer.parseInt(dataType.getValue());
                                    break;
                                case Variables.DB_CRW_TSYNC_DLON_TIME:
                                    Variables.bandStruct[Variables.band].tsync.tDlOnTime = Integer.parseInt(dataType.getValue());
                                    break;
                                case Variables.DB_CRW_TSYNC_ULON_TIME:
                                    Variables.bandStruct[Variables.band].tsync.tUlOnTime = Integer.parseInt(dataType.getValue());
                                    break;
                                case Variables.DB_CRW_TSYNC_TDD_ULDL_CONF:
                                    arrayList = mContext.getResources().getStringArray(R.array.set_tddconf);
                                    for(byte j=0; j< arrayList.length; j++){
                                        if(dataType.getValue().equals(arrayList[j])){
                                            // 6개 리스트 중 2개만 사용
                                            if(j == 0) j += 1;
                                            else if(j == 1) j += 2;
                                            Variables.bandStruct[Variables.band].tsync.tDlUlConfigure = j;
//                                            Debug.loge(new Exception(),"dhjung --> tDlUlConfigure: " + Variables.bandStruct[Variables.band].tsync.tDlUlConfigure);
                                            break;
                                        }
                                    }
//                                    Variables.bandStruct[Variables.band].tsync.tDlUlConfigure = Byte.parseByte(dataType.getValue());
                                    break;
                                case Variables.DB_CRW_TSYNC1_OUT_SEL:
                                    Variables.bandStruct[Variables.band].tsync.tTsync1OutSel = Byte.parseByte(dataType.getValue());
                                    break;
                                case Variables.DB_CRW_TSYNC2_OUT_SEL:
                                    Variables.bandStruct[Variables.band].tsync.tTsync2OutSel = Byte.parseByte(dataType.getValue());
                                    break;
                                case Variables.DB_CRW_TSYNC3_OUT_SEL:
                                    Variables.bandStruct[Variables.band].tsync.tTsync3OutSel = Byte.parseByte(dataType.getValue());
                                    break;
                                case Variables.DB_CRW_TSYNC4_OUT_SEL:
                                    Variables.bandStruct[Variables.band].tsync.tTsync4OutSel = Byte.parseByte(dataType.getValue());
                                    break;
                                case Variables.DB_CRW_TSYNC_CENTER_FREQ:
                                    Variables.bandStruct[Variables.band].tsync.tCenterFreq = Integer.parseInt(dataType.getValue());
                                    break;
                                case Variables.DB_CRW_TSYNC_SSB_SEARCH_MODE:
                                    Variables.bandStruct[Variables.band].tsync.tSSBsearchMode = Byte.parseByte(dataType.getValue());
                                    break;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
            Variables.tsyncSendFlag = true;

            mListener.onTsyncSettingSend(Variables.DIALOG_TYPE_SEEKBAR);

            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Updating.... Please wait....");
            mProgressDialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean flag = true;
                    int cnt = 0;

                    while(flag){
//                        Debug.logi(new Exception(),"=dhjung=======> Variables.tsyncSettingFlag : " + Variables.tsyncSettingFlag);

                        if (cnt >= 50) {
                            resultFlag = false;
                            break;
                        }

                        try {
                            cnt++;
                            Thread.sleep(100L);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (Variables.tsyncSettingFlag == -103) {
                            flag = false;
                            Variables.tsyncSettingFlag = (byte) 0x00;
                            resultFlag = true;
                            break;
                        }

//                        Debug.logi(new Exception(),"=dhjung=======> Variables.tsyncSettingFlag : " + Variables.tsyncSettingFlag + ", " + cnt);

                    }
//                    Debug.logi(new Exception(),"=dhjung=======> while break");

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

        View view = inflater.inflate(R.layout.activity_tab5_tsync, null);
        mView = view;

//        int gridCount = 0;
        for(int tvld : arrTextViewSubTitle){
            TextView tv = view.findViewById(tvld);
//            tv.setText(arrTextViewSubTitleName[gridCount]);
            tv.setOnClickListener(this);
//            gridCount++;
        }
        Debug.loge(new Exception(), "onCreate View GridView START");
        int gridCount = 0;
        for(int gridId : arrGridViewItemList){
            Debug.loge(new Exception(), "GridView gridCount : " + gridCount + " gridId : " + gridId);
            ExpandableView gv = view.findViewById(gridId);
            TsyncGridAdapter adapter = new TsyncGridAdapter(view.getContext(), gridId, list[gridCount], settingTypeList[gridCount] ,dialogReturnValueListener,gridCount);
            gv.setAdapter(adapter);
            gv.setNumColumns(1);
            gv.setExpanded(true);
            arrTsyncGridAdapter[gridCount] = adapter;
            arrGridViewList[gridCount] = gv;
            gridCount++;
        }
        Debug.loge(new Exception(), "onCreate View GridView END");
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (OnTsyncSendListener) activity;
        }catch (Exception e){
            throw new ClassCastException(activity.toString()+"must implement OnSettingSendListener");
        }
    }



    @Override
    public void onClick(View v) {
//        Debug.loge(new Exception(), "dhjung Tsync OnClick");
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
            arrTsyncGridAdapter[gridCount] = new TsyncGridAdapter(mView.getContext(), gridId, list[gridCount], settingTypeList[gridCount],dialogReturnValueListener,gridCount);
            arrGridViewList[gridCount].setAdapter(arrTsyncGridAdapter[gridCount]);
            gridCount++;
        }
    }

    private void dataToArrayList(){
        DataType[] tsyncAlarm = MainActivity.subTitleNameClass.getTsyncSubTitle().getTsyncAlarmSubTitle();
        DataType[] tsyncInfo = MainActivity.subTitleNameClass.getTsyncSubTitle().getTsyncInfoSubTitle();
        DataType[] tsyncConfigure = MainActivity.subTitleNameClass.getTsyncSubTitle().getTsyncConfigureSubTitle();


        for(int i=0;i < arrGridViewItemList.length; i++){
            list[i] = new ArrayList<ItemList>();
            settingTypeList[i] = new ArrayList<SubTitleNameClass.SettingType>();
        }

        for(int i=0;i < tsyncAlarm.length; i++) {
            mTsyncList = new ItemList(tsyncAlarm[i].getName(), tsyncAlarm[i].getValue());
            list[Variables.ARRAYLIST_MENUTYPE_TSYNC_ALARM].add(mTsyncList);
        }

        for(int i=0;i < tsyncInfo.length; i++) {
            if (Variables.HIDDEN_ENABLED) {
                boolean flag = false;
                for (int v : Variables.TAB5_INFO_HIDDEN_MENU_ID) {
                    if (tsyncInfo[i].getId() == v) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    mTsyncList = new ItemList(tsyncInfo[i].getName(), tsyncInfo[i].getValue());
                    list[Variables.ARRAYLIST_MENUTYPE_TSYNC_INFO].add(mTsyncList);
                }
            }
            else {
                mTsyncList = new ItemList(tsyncInfo[i].getName(), tsyncInfo[i].getValue());
                list[Variables.ARRAYLIST_MENUTYPE_TSYNC_INFO].add(mTsyncList);
            }
        }

        for(int i=0;i < tsyncConfigure.length; i++) {
            String tmpBuf;
            if (Variables.HIDDEN_ENABLED) {
                boolean flag = false;
                for (int v : Variables.TAB5_CONF_HIDDEN_MENU_ID) {
                    if (tsyncConfigure[i].getId() == v) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    switch (tsyncConfigure[i].getId()) {
                        case Variables.DB_CRW_TSYNC_DLOFF_TIME:
                        case Variables.DB_CRW_TSYNC_ULOFF_TIME:
                        case Variables.DB_CRW_TSYNC_DLON_TIME:
                        case Variables.DB_CRW_TSYNC_ULON_TIME:
                            tmpBuf = tsyncConfigure[i].getValue() + " (x100ns)";
                            break;
                        case Variables.DB_CRW_TSYNC_CENTER_FREQ:
                            tmpBuf = tsyncConfigure[i].getValue() + " MHz";
                            break;
                        default:
                            tmpBuf = tsyncConfigure[i].getValue();
                            break;
                    }
                    mTsyncList = new ItemList(tsyncConfigure[i].getName(), tmpBuf);
                    list[Variables.ARRAYLIST_MENUTYPE_TSYNC_CONFIGURE].add(mTsyncList);
                    settingTypeList[Variables.ARRAYLIST_MENUTYPE_TSYNC_CONFIGURE].add(MainActivity.subTitleNameClass.getTsyncSubTitle().getmTsyncConfigureSettingType().get(i));
                }
            }else {
                switch (tsyncConfigure[i].getId()) {
                    case Variables.DB_CRW_TSYNC_DLOFF_TIME:
                    case Variables.DB_CRW_TSYNC_ULOFF_TIME:
                    case Variables.DB_CRW_TSYNC_DLON_TIME:
                    case Variables.DB_CRW_TSYNC_ULON_TIME:
                        tmpBuf = tsyncConfigure[i].getValue() + " (x100ns)";
                        break;
                    case Variables.DB_CRW_TSYNC_CENTER_FREQ:
                        tmpBuf = tsyncConfigure[i].getValue() + " MHz";
                        break;
                    default:
                        tmpBuf = tsyncConfigure[i].getValue();
                        break;
                }
                mTsyncList = new ItemList(tsyncConfigure[i].getName(), tmpBuf);
                list[Variables.ARRAYLIST_MENUTYPE_TSYNC_CONFIGURE].add(mTsyncList);
                if (i == 0)
                    settingTypeList[Variables.ARRAYLIST_MENUTYPE_TSYNC_CONFIGURE] = MainActivity.subTitleNameClass.getTsyncSubTitle().getmTsyncConfigureSettingType();
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

