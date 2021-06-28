package kr.knetz.qn.app.v;

import kr.knetz.qn.app.R;
import kr.knetz.qn.app.l.Debug;
import kr.knetz.qn.app.v.h.ExpandableView;
import kr.knetz.qn.app.v.t.SettingGridAdapter;
import kr.knetz.qn.app.v.x.CustomDialog;
import kr.knetz.qn.app.v.c.DataType;
import kr.knetz.qn.app.v.t.SubTitleNameClass;
import kr.knetz.qn.app.v.t.ItemList;
import kr.knetz.qn.app.v.x.Variables;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


@SuppressLint("ValidFragment")
public class Tab3_Setting extends Fragment implements View.OnClickListener {

    Activity mainActivity;
    Context mContext;
    View mView;
    OnSettingSendListener mListener;
    boolean resultFlag = false;


    private ProgressDialog mProgressDialog;
//    private Runnable mRunnable;

    public interface OnSettingSendListener {
        void onSettingSend(int type);
    }

    private ArrayList<ItemList>[] list;
    private ArrayList<SubTitleNameClass.SettingType>[] settingTypeList;
    private ItemList mSettingList;

    private ExpandableView[] arrGridViewList;
    private SettingGridAdapter[] arrSettingGridAdapter;
    private Handler mHandler;

    int system, psu, dlDspRf, ulDspRf, dlAmp, ulAmp, serviceFa, common;

    static int[] arrTextViewSubTitle;
    static int[] arrGridViewItemList;
    int arrSize;

//    static final int[] arrTextViewSubTitle_sub6 = {
//            R.id.tv_sub_title1,
//            R.id.tv_sub_title3,
//            R.id.tv_sub_title4,
//            R.id.tv_sub_title5,
//            R.id.tv_sub_title6
//    };
//
//    static final int[] arrGridViewItemList_sub6 = {
//            R.id.gridview_body_sub1,
//            R.id.gridview_body_sub3,
//            R.id.gridview_body_sub4,
//            R.id.gridview_body_sub5,
//            R.id.gridview_body_sub6
//    };

    static final int[] arrTextViewSubTitle_kddi_qn = {
            R.id.tv_sub_title1,
            R.id.tv_sub_title3,
            R.id.tv_sub_title4,
            R.id.tv_sub_title5,
            R.id.tv_sub_title6,
            R.id.tv_sub_title7,
            R.id.tv_sub_title8
    };

    static final int[] arrGridViewItemList_kddi_qn = {
            R.id.gridview_body_sub1,
            R.id.gridview_body_sub3,
            R.id.gridview_body_sub4,
            R.id.gridview_body_sub5,
            R.id.gridview_body_sub6,
            R.id.gridview_body_sub7,
            R.id.gridview_body_sub8
    };

    public Tab3_Setting(Context context) {
        mContext = context;
    }

    public Tab3_Setting() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Debug.logi(new Exception(),"=dhjung=======> Tab3_Setting onCreate");

        if(Variables.SYSTEM_R_TYPE == Variables.SYSTEM_R_TYPE_KDDI_QN) {
            arrSize = 7;
            list = new ArrayList[arrSize];
            settingTypeList = new ArrayList[arrSize];
            arrTextViewSubTitle = new int[arrSize];
            arrGridViewItemList = new int[arrSize];

            arrTextViewSubTitle = Arrays.copyOf(arrTextViewSubTitle_kddi_qn, arrTextViewSubTitle_kddi_qn.length);
            arrGridViewItemList = Arrays.copyOf(arrGridViewItemList_kddi_qn, arrGridViewItemList_kddi_qn.length);

            system = 0; dlDspRf = 1; ulDspRf = 2; dlAmp = 3; ulAmp = 4; serviceFa = 5; common = 6;
        }
//        else if(Variables.SYSTEM_R_TYPE == Variables.SYSTEM_R_TYPE_SUB6_5G) {
//            arrSize = 5;
//            list = new ArrayList[arrSize];
//            settingTypeList = new ArrayList[arrSize];
//            arrTextViewSubTitle = new int[arrSize];
//            arrGridViewItemList = new int[arrSize];
//
//            arrTextViewSubTitle = Arrays.copyOf(arrTextViewSubTitle_sub6, arrTextViewSubTitle_sub6.length);
//            arrGridViewItemList = Arrays.copyOf(arrGridViewItemList_sub6, arrGridViewItemList_sub6.length);
//
//            system = 0; dlDspRf = 1; ulDspRf = 2; dlAmp = 3; ulAmp = 4;
//        }

        dataToArrayList();
        arrGridViewList = new ExpandableView[arrGridViewItemList.length];
        arrSettingGridAdapter = new SettingGridAdapter[arrGridViewItemList.length];

        mHandler = new Handler();
        mainActivity = getActivity();
    }

    private Handler confirmHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //완료 후 실행할 처리 삽입
            //Debug.loge(new Exception(),"Knetz ====handleMessage : "+msg.what);
            if (resultFlag) {
                Toast.makeText(getActivity().getBaseContext(), "Update Success!!", Toast.LENGTH_SHORT).show();
                doRefreshGridViewList();
            } else {
                Toast.makeText(getActivity().getBaseContext(), "Update Fail! Timeout....", Toast.LENGTH_SHORT).show();
            }
        }
    };

    CustomDialog.DialogReturnValueListener dialogReturnValueListener = new CustomDialog.DialogReturnValueListener() {
        @Override
        public void onFinishInputDialog(DataType dataType) {
//            Toast.makeText(getActivity().getBaseContext(),dataType.getId()+" : "+dataType.getName()+" : "+dataType.getValue(), Toast.LENGTH_SHORT).show();

        if(Variables.band == 4) {
            Variables.band = 0;
            Variables.PROTO_DEST_ID = 0xC1;
            Debug.logi(new Exception(),"=dhjung=======> band(DEST_ID): 4(0xC5) -------------> 0(0xC1)");
        }

        if(dataType.getId() == system){
            DataType[] systemInfo = MainActivity.subTitleNameClass.getSettingSubTitle().getSystemInformationSubTitle();
            MainActivity.settingFrameInfo = 0x01;
            for (int i = 0; i < systemInfo.length; i++) {
                if (systemInfo[i].getName().equals(dataType.getName())) {
                    systemInfo[i].setValue(dataType.getValue());
                    Debug.loge(new Exception(), "systemInfo getId() : " + systemInfo[i].getId());
                    switch (systemInfo[i].getId()) {
                        case Variables.DB_SETTING_TEMP_UPPER:
                            Variables.bandStruct[Variables.band].sets.tTempUpper = Byte.parseByte(dataType.getValue());
                            //Variables._arrBandStruct[0].set.aTempUpper = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_AUTO_SHUTDOWN:
                            Variables.bandStruct[Variables.band].sets.tAutoShutdown = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_AUTO_RECOVERY:
                            Variables.bandStruct[Variables.band].sets.tAutoRecovery = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_SLEEP_MODE:
                            Variables.bandStruct[Variables.band].sets.tSleepMode = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_ILC:
                            Variables.bandStruct[Variables.band].sets.tILC = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_SAW_BYPASS:
                            Variables.bandStruct[Variables.band].sets.tSawBypass = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_FREQ_SEL_AUTO:
                            Variables.bandStruct[Variables.band].sets.tFreqSelectAutoManual = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_FREQ_SEL:
                            Variables.bandStruct[Variables.band].sets.tFreqSelect10M15M = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_CELL_SEARCH:
                            Variables.bandStruct[Variables.band].sets.tCellSearch = Byte.parseByte(dataType.getValue());
                            break;
                    }
                    Debug.loge(new Exception(), "frameinfo 11: " + String.format("0x%02x", MainActivity.settingFrameInfo));
                }
            }
        }
        else if(dataType.getId() == dlDspRf){
            DataType[] downDspRfInfo = MainActivity.subTitleNameClass.getSettingSubTitle().getDownlinkDspRfSubTitle();
            MainActivity.settingFrameInfo = 0x02;
            String[] arrayList;
            int list = 0;
            for (int i = 0; i < downDspRfInfo.length; i++) {
                if (downDspRfInfo[i].getName().equals(dataType.getName())) {
                    downDspRfInfo[i].setValue(dataType.getValue());
                    Debug.loge(new Exception(), "downDspRfInfo getId() : " + downDspRfInfo[i].getId());
                    switch (downDspRfInfo[i].getId()) {
                        case Variables.DB_SETTING_ATTEN:
                            Variables.bandStruct[Variables.band].pathSets[0].tAtten = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_ICS_MODE:
//                            if (!Variables.HIDDEN_ENABLED)  list = R.array.set_dl_icsmode_hidden;
//                            else                           list = R.array.set_dl_icsmode;
                            list = R.array.set_dl_icsmode;
                            arrayList = mContext.getResources().getStringArray(list);
                            for(byte j=0; j< arrayList.length; j++){
                                if(dataType.getValue().equals(arrayList[j])){
//                                    if(Variables.HIDDEN_ENABLED) j++;
                                    Variables.bandStruct[Variables.band].pathSets[0].tIcsMode = (byte)(j+2);
                                    break;
                                }
                            }
//                            Variables.bandStruct[Variables.band].pathSets[0].tIcsMode = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_SYSTEM_GAIN:
                            Variables.bandStruct[Variables.band].pathSets[0].tSystemGain = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_OUTPUT_UPPER:
                            Variables.bandStruct[Variables.band].pathSets[0].tOutputUpper = Short.parseShort(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_OUTPUT_LOWER:
                            Variables.bandStruct[Variables.band].pathSets[0].tOutputLower = Short.parseShort(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_INPUT_UPPER:
                            Variables.bandStruct[Variables.band].pathSets[0].tInputUpper = Short.parseShort(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_INPUT_LOWER:
                            Variables.bandStruct[Variables.band].pathSets[0].tInputLower = Short.parseShort(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_AGC_OFFSET:
                            Variables.bandStruct[Variables.band].pathSets[0].tAgcOffset = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_PATH_SLEEP_MODE:
                            Variables.bandStruct[Variables.band].pathSets[0].tPathSleepMode = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_PATH_SLEEP_LEVEL:
                            Variables.bandStruct[Variables.band].pathSets[0].tPathSleepLevel = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_INPUT_ALC_RECOVERY_TIME:
                            Variables.bandStruct[Variables.band].pathSets[0].tInputAlcRecoveryTime = Integer.parseInt(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_INPUT_ALC_PERIOD:
                            Variables.bandStruct[Variables.band].pathSets[0].tInputAlcPeriod = Integer.parseInt(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_INPUT_ALC_LEVEL:
                            Variables.bandStruct[Variables.band].pathSets[0].tInputAlcLevel = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_ICS_OFF:
                            Variables.bandStruct[Variables.band].pathSets[0].tIcsOff = Byte.parseByte(dataType.getValue());
                            break;
                    }
                    Debug.loge(new Exception(), "frameinfo 22: " + String.format("0x%02x", MainActivity.settingFrameInfo));
                }
            }
        }
        else if(dataType.getId() == ulDspRf) {
            DataType[] upDspRfInfo = MainActivity.subTitleNameClass.getSettingSubTitle().getUplinkDspRfSubTitle();
            MainActivity.settingFrameInfo = 0x04;
            String[] arrayList;
            int list = 0;
            for (int i = 0; i < upDspRfInfo.length; i++) {
                if (upDspRfInfo[i].getName().equals(dataType.getName())) {
                    upDspRfInfo[i].setValue(dataType.getValue());
                    Debug.loge(new Exception(), "upDspRfInfo getId() : " + upDspRfInfo[i].getId());
                    switch (upDspRfInfo[i].getId()) {
                        case Variables.DB_SETTING_ATTEN:
                            Variables.bandStruct[Variables.band].pathSets[1].tAtten = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_ICS_MODE:
//                            if (!Variables.HIDDEN_ENABLED)  list = R.array.set_ul_icsmode_hidden;
//                            else                           list = R.array.set_ul_icsmode;
                            list = R.array.set_ul_icsmode;
                            arrayList = mContext.getResources().getStringArray(list);
                            for(byte j=0; j< arrayList.length; j++){
                                if(dataType.getValue().equals(arrayList[j])){
//                                    if(Variables.HIDDEN_ENABLED) j++;
                                    Variables.bandStruct[Variables.band].pathSets[1].tIcsMode = (byte)(j+2);
                                    break;
                                }
                            }
//                            Variables.bandStruct[Variables.band].pathSets[1].tIcsMode = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_SYSTEM_GAIN:
                            Variables.bandStruct[Variables.band].pathSets[1].tSystemGain = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_OUTPUT_UPPER:
                            Variables.bandStruct[Variables.band].pathSets[1].tOutputUpper = Short.parseShort(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_OUTPUT_LOWER:
                            Variables.bandStruct[Variables.band].pathSets[1].tOutputLower = Short.parseShort(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_INPUT_UPPER:
                            Variables.bandStruct[Variables.band].pathSets[1].tInputUpper = Short.parseShort(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_INPUT_LOWER:
                            Variables.bandStruct[Variables.band].pathSets[1].tInputLower = Short.parseShort(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_AGC_OFFSET:
                            Variables.bandStruct[Variables.band].pathSets[1].tAgcOffset = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_PATH_SLEEP_MODE:
                            Variables.bandStruct[Variables.band].pathSets[1].tPathSleepMode = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_PATH_SLEEP_LEVEL:
                            Variables.bandStruct[Variables.band].pathSets[1].tPathSleepLevel = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_INPUT_ALC_RECOVERY_TIME:
                            Variables.bandStruct[Variables.band].pathSets[1].tInputAlcRecoveryTime = Integer.parseInt(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_INPUT_ALC_PERIOD:
                            Variables.bandStruct[Variables.band].pathSets[1].tInputAlcPeriod = Integer.parseInt(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_INPUT_ALC_LEVEL:
                            Variables.bandStruct[Variables.band].pathSets[1].tInputAlcLevel = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_ICS_OFF:
                            Variables.bandStruct[Variables.band].pathSets[1].tIcsOff = Byte.parseByte(dataType.getValue());
                            break;
                    }
                    Debug.loge(new Exception(), "frameinfo 33: " + String.format("0x%02x", MainActivity.settingFrameInfo));
                }
            }
        }
        else if(dataType.getId() == dlAmp) {
            DataType[] downAmpInfo = MainActivity.subTitleNameClass.getSettingSubTitle().getDownlinkAmpSubTitle();
            MainActivity.settingFrameInfo = 0x10;

            for (int i = 0; i < downAmpInfo.length; i++) {
                if (downAmpInfo[i].getName().equals(dataType.getName())) {
                    downAmpInfo[i].setValue(dataType.getValue());
                    Debug.loge(new Exception(), "downAmpInfo getId() : " + downAmpInfo[i].getId());
                    switch (downAmpInfo[i].getId()) {
                        case Variables.DB_SETTING_AMP_ONOFF:
                            Variables.bandStruct[Variables.band].pathSets[0].tAmpOnOff = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_ALC_ONOFF:
                            Variables.bandStruct[Variables.band].pathSets[0].tAlcOnOff = Byte.parseByte(dataType.getValue());
                            break;
                    }
                    Debug.loge(new Exception(), "frameinfo 44: " + String.format("0x%02x", MainActivity.settingFrameInfo));
                }
            }
        }
        else if(dataType.getId() == ulAmp) {
            DataType[] upAmpInfo = MainActivity.subTitleNameClass.getSettingSubTitle().getUplinkAmpSubTitle();
            MainActivity.settingFrameInfo = 0x20;

            for (int i = 0; i < upAmpInfo.length; i++) {
                if (upAmpInfo[i].getName().equals(dataType.getName())) {
                    upAmpInfo[i].setValue(dataType.getValue());
                    Debug.loge(new Exception(), "upAmpInfo getId() : " + upAmpInfo[i].getId());
                    switch (upAmpInfo[i].getId()) {
                        case Variables.DB_SETTING_AMP_ONOFF:
                            Variables.bandStruct[Variables.band].pathSets[1].tAmpOnOff = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_ALC_ONOFF:
                            Variables.bandStruct[Variables.band].pathSets[1].tAlcOnOff = Byte.parseByte(dataType.getValue());
                            break;
                    }
                    Debug.loge(new Exception(), "frameinfo 55: " + String.format("0x%02x", MainActivity.settingFrameInfo));
                }
            }
        }
        else if(dataType.getId() == serviceFa) {
            DataType[] serviceFaInfo = MainActivity.subTitleNameClass.getSettingSubTitle().getServiceFaSubTitle();
            MainActivity.settingFrameInfo = 0x08;
            String[] arrayList;
            for (int i = 0; i < serviceFaInfo.length; i++) {
                if (serviceFaInfo[i].getName().equals(dataType.getName())) {
                    serviceFaInfo[i].setValue(dataType.getValue());
                    Debug.loge(new Exception(), "serviceFaInfo getId() : " + serviceFaInfo[i].getId());
                    switch (serviceFaInfo[i].getId()) {
                        case Variables.DB_SETTING_RF_PATH_ON:
                            arrayList = mContext.getResources().getStringArray(R.array.set_rfpath);
                            for(byte j=0; j< arrayList.length; j++){
                                if(dataType.getValue().equals(arrayList[j])){
                                    Variables.bandStruct[Variables.band].sets.tRfPathOn = (byte)(j+1);
//                                            Debug.loge(new Exception(),"dhjung --> tRfPathOn: " + Variables.bandStruct[Variables.band].sets.tRfPathOn);
                                    break;
                                }
                            }
//                            Variables.bandStruct[Variables.band].sets.tRfPathOn = Byte.parseByte(dataType.getValue());
                            break;
                    }
                    Debug.loge(new Exception(), "frameinfo 66: " + String.format("0x%02x", MainActivity.settingFrameInfo));
                }
            }
        }
        else if(dataType.getId() == common) {
            DataType[] commonInfo = MainActivity.subTitleNameClass.getSettingSubTitle().getCommonSubTitle();
            MainActivity.settingFrameInfo = 0x40;
            String[] arrayList;
            for (int i = 0; i < commonInfo.length; i++) {
                if (commonInfo[i].getName().equals(dataType.getName())) {
                    commonInfo[i].setValue(dataType.getValue());
                    Debug.loge(new Exception(), "commonInfo getId() : " + commonInfo[i].getId());
                    switch (commonInfo[i].getId()) {
                        case Variables.DB_SETTING_SERIAL_NUMBER:
                            dataType.getValue().getBytes(0, 20, Variables.bandStruct[Variables.band].sets.tSerialNum, 0);
                            break;
                        case Variables.DB_SETTING_MODEL_NAME:
                            dataType.getValue().getBytes(0, 20, Variables.bandStruct[Variables.band].sets.tModelName, 0);
                            break;
                        case Variables.DB_SETTING_OPERATOR_NAME:
                            dataType.getValue().getBytes(0, 20, Variables.bandStruct[Variables.band].sets.tOperatorName, 0);
                            break;
                        case Variables.DB_SETTING_SUPPLIER_NAME:
                            dataType.getValue().getBytes(0, 20, Variables.bandStruct[Variables.band].sets.tSupplierName, 0);
                            break;
                        case Variables.DB_SETTING_INSTALL_ADDR:
                            Arrays.fill(Variables.bandStruct[Variables.band].sets.tInstallAddr, 0, 100, (byte)0);
                            System.arraycopy(dataType.getValue().getBytes(), 0, Variables.bandStruct[Variables.band].sets.tInstallAddr, 0, dataType.getValue().length());
//                            Debug.loge(new Exception(), "dhjung --> Install: " + ByteUtil.byteArrayToString(Variables.bandStruct[Variables.band].sets.tInstallAddr));
                            break;
                        case Variables.DB_SETTING_POWER_MODE:
                            Variables.bandStruct[Variables.band].sets.tPowerMode = Byte.parseByte(dataType.getValue());
                            break;
                        case Variables.DB_SETTING_SERVICE_BAND:
                            arrayList = mContext.getResources().getStringArray(R.array.set_band);
                            for(byte j=0; j< arrayList.length; j++){
                                if(dataType.getValue().equals(arrayList[j])){
                                    Variables.bandStruct[Variables.band].sets.tServiceBand = j;
//                                            Debug.loge(new Exception(),"dhjung --> tServiceBand: " + Variables.bandStruct[Variables.band].sets.tServiceBand);
                                    break;
                                }
                            }
//                            Variables.bandStruct[Variables.band].sets.tServiceBand = Byte.parseByte(dataType.getValue());
                            break;
                    }
                    Debug.loge(new Exception(), "frameinfo 77: " + String.format("0x%02x", MainActivity.settingFrameInfo));
                }
            }
        }
        Variables.settingSendFlag = true;

        if (MainActivity.dspCtrlFrameInfo != 0) {
            mListener.onSettingSend(Variables.DIALOG_TYPE_SEEKBAR_DSP);
        } else {
            mListener.onSettingSend(Variables.DIALOG_TYPE_SEEKBAR);
        }

        // 5sec Timer Start
        // Looper UI Start
        // after 5sec Setting Fail Msg Dialog Show

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

                while (flag) {
//                    Debug.logi(new Exception(),"=dhjung=======> Variables.settingFlag : " + Variables.settingFlag);

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

                    if (Variables.settingFlag == -103) {
                        flag = false;
                        Variables.settingFlag = (byte) 0x00;
                        resultFlag = true;
                        break;
                    }

//                    Debug.logi(new Exception(),"=dhjung=======> Variables.settingFlag : " + Variables.settingFlag + ", " + cnt);

                }
//                Debug.logi(new Exception(),"=dhjung=======> while break");

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
//        Debug.logi(new Exception(),"=dhjung=======> onCreateView");

        int layout = R.layout.activity_tab3_setting;

        if(Variables.SYSTEM_R_TYPE == Variables.SYSTEM_R_TYPE_KDDI_QN) {
            layout = R.layout.activity_tab3_setting_kddi_qn;
        }
        else if(Variables.SYSTEM_R_TYPE == Variables.SYSTEM_R_TYPE_SUB6_5G) {
            layout = R.layout.activity_tab3_setting_sub6;
        }

        View view = inflater.inflate(layout, null);
        mView = view;

        for (int tvId : arrTextViewSubTitle) {
            TextView tv = view.findViewById(tvId);
            tv.setOnClickListener(this);
        }

        int gridCount = 0;
        for (int gridId : arrGridViewItemList) {
//            Debug.loge(new Exception(), "GridView gridCount : " + gridCount + " gridId : " + gridId);
            ExpandableView gv = view.findViewById(gridId);
            SettingGridAdapter adapter = new SettingGridAdapter(view.getContext(), gridId, list[gridCount], settingTypeList[gridCount], dialogReturnValueListener, gridCount);
            gv.setAdapter(adapter);
//            if (gridCount != 0)
            gv.setNumColumns(1);
            gv.setExpanded(true);
            arrSettingGridAdapter[gridCount] = adapter;
            arrGridViewList[gridCount] = gv;
            gridCount++;
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Debug.loge(new Exception(),"onAttach() ");
        try {
            mListener = (OnSettingSendListener) activity;
        } catch (Exception e) {
            throw new ClassCastException(activity.toString() + " must implement OnSettingSendListener");
        }
    }

    void printSettingTypeArrayList(ArrayList<SubTitleNameClass.SettingType> subSettingTypeArrayList) {
        //Debug.loge(new Exception(), "=====================================");
        for (int i = 0; i < subSettingTypeArrayList.size(); i++) {
            //Debug.logd(new Exception(),"type : "+subSettingTypeArrayList.get(i).type);
            if (subSettingTypeArrayList.get(i).type != 1) {
                //Debug.logd(new Exception(), "min : " + subSettingTypeArrayList.get(i).min_value);
                //Debug.logd(new Exception(), "max : " + subSettingTypeArrayList.get(i).max_value);
            }
        }
        //Debug.loge(new Exception(),"=====================================");
    }

    @Override
    public void onPause() {
        super.onPause();
//        Debug.loge(new Exception(),"======Knetz======Tab3_Setting onPause");
        // timer stop
        mHandler.removeCallbacks(mRunnable);
    }


    public void onListTextViewTextSizeChanged(int key) {
        if (arrSettingGridAdapter.length > 0) {
            for (int i = 0; i < arrSettingGridAdapter.length; i++) {
                arrSettingGridAdapter[i].setTextviewTextSize(key);
                arrSettingGridAdapter[i].notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Debug.logd(new Exception(), "===Knetz=====================onResume START ");
        doRefreshGridViewList();
        //Debug.logd(new Exception(), "===Knetz=====================onResume END ");
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, Variables.REFRESH_TIMEOUT_MILLIS);
    }

    public void doRefreshGridViewList() {
//        Debug.logi(new Exception(), "======================================KnetzH");
        dataToArrayList();
//        Debug.loge(new Exception(), "===Knetz=====================doRefreshGridViewList ");
        int gridCount = 0;
        for (int gridId : arrGridViewItemList) {
            arrSettingGridAdapter[gridCount] = new SettingGridAdapter(mView.getContext(), gridId, list[gridCount], settingTypeList[gridCount], dialogReturnValueListener, gridCount);
            arrGridViewList[gridCount].setAdapter(arrSettingGridAdapter[gridCount]);
            gridCount++;
        }
    }

    @Override
    public void onClick(View v) {
//        Debug.loge(new Exception(), "dhjung Setting OnClick");
        int count = 0;
        for (int tvId : arrTextViewSubTitle) {
            if (v.getId() == tvId) {
                if (arrGridViewList[count].isShown())
                    arrGridViewList[count].setVisibility(View.GONE);
                else arrGridViewList[count].setVisibility(View.VISIBLE);
            }
            count++;
        }
    }

    private void dataToArrayList() {

        DataType[] systemInfo = MainActivity.subTitleNameClass.getSettingSubTitle().getSystemInformationSubTitle();
        DataType[] downDspRfInfo = MainActivity.subTitleNameClass.getSettingSubTitle().getDownlinkDspRfSubTitle();
        DataType[] upDspRfInfo = MainActivity.subTitleNameClass.getSettingSubTitle().getUplinkDspRfSubTitle();
        DataType[] downAmpInfo = MainActivity.subTitleNameClass.getSettingSubTitle().getDownlinkAmpSubTitle();
        DataType[] upAmpInfo = MainActivity.subTitleNameClass.getSettingSubTitle().getUplinkAmpSubTitle();
        DataType[] serviceFaInfo = MainActivity.subTitleNameClass.getSettingSubTitle().getServiceFaSubTitle();
        DataType[] commonInfo = MainActivity.subTitleNameClass.getSettingSubTitle().getCommonSubTitle();

        for (int i = 0; i < arrGridViewItemList.length; i++) {
            list[i] = new ArrayList<ItemList>();
            settingTypeList[i] = new ArrayList<SubTitleNameClass.SettingType>();
        }

        for (int i = 0; i < systemInfo.length; i++) {
            String tmpBuf;

            if (Variables.band != 0 && Variables.band != 4){

                boolean flag = false;
                for (int v : Variables.TAB3_SYSTEM_CDMA_HIDDEN_MENU_ID) {
                    if (systemInfo[i].getId() == v) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    switch (systemInfo[i].getId()) {
                        case Variables.DB_SETTING_TEMP_UPPER:
                            tmpBuf = systemInfo[i].getValue() + " 'C";
                            break;

                        default:
                            tmpBuf = systemInfo[i].getValue();
                    }

                    mSettingList = new ItemList(systemInfo[i].getName(), tmpBuf);
                    list[system].add(mSettingList);
                    if (i == 0)
                        settingTypeList[system] = MainActivity.subTitleNameClass.getSettingSubTitle().getmSystemSettingType();
                }
            } else {

                switch (systemInfo[i].getId()) {
                    case Variables.DB_SETTING_TEMP_UPPER:
                        tmpBuf = systemInfo[i].getValue() + " 'C";
                        break;

                    default:
                        tmpBuf = systemInfo[i].getValue();
                }
                mSettingList = new ItemList(systemInfo[i].getName(), tmpBuf);
                list[system].add(mSettingList);
                if (i == 0)
                    settingTypeList[system] = MainActivity.subTitleNameClass.getSettingSubTitle().getmSystemSettingType();
            }
        }

//        Debug.logv(new Exception(),"downDspRfInfo length : "+downDspRfInfo.length+ " setting size  : "+MainActivity.subTitleNameClass.getSettingSubTitle().getmDownlinkDspRfSettingType().size());
        for (int i = 0; i < downDspRfInfo.length; i++) {
            String tmpBuf;
            if (Variables.HIDDEN_ENABLED) {
                boolean flag = false;
                for (int v : Variables.TAB3_DOWNLINK_HIDDEN_MENU_ID) {
                    if (downDspRfInfo[i].getId() == v) {
                        flag = true;
                        break;
                    }
                }

//                Debug.logv(new Exception(),"downDspRfInfo flag : "+flag+ " getId : "+downDspRfInfo[i].getId());
                if (!flag) {
                    switch (downDspRfInfo[i].getId()) {
                        case Variables.DB_SETTING_ATTEN:
                        case Variables.DB_SETTING_AGC_OFFSET:
                        case Variables.DB_SETTING_SYSTEM_GAIN:
                            tmpBuf = downDspRfInfo[i].getValue() + " dB";
                            break;
                        case Variables.DB_SETTING_OUTPUT_UPPER:
                        case Variables.DB_SETTING_OUTPUT_LOWER:
                        case Variables.DB_SETTING_INPUT_UPPER:
                        case Variables.DB_SETTING_INPUT_LOWER:
                        case Variables.DB_SETTING_INPUT_ALC_LEVEL:
                            tmpBuf = downDspRfInfo[i].getValue() + " dBm";
                            break;
                        case Variables.DB_SETTING_INPUT_ALC_RECOVERY_TIME:
                            tmpBuf = downDspRfInfo[i].getValue() + " sec";
                            break;
                        case Variables.DB_SETTING_INPUT_ALC_PERIOD:
                            tmpBuf = downDspRfInfo[i].getValue() + " sec/dB";
                            break;
                        default:
                            tmpBuf = downDspRfInfo[i].getValue();
                            break;
                    }
                    mSettingList = new ItemList(downDspRfInfo[i].getName(), tmpBuf);
                    list[dlDspRf].add(mSettingList);
                    settingTypeList[dlDspRf].add(MainActivity.subTitleNameClass.getSettingSubTitle().getmDownlinkDspRfSettingType().get(i));
//                    if (i == 0)
//                        settingTypeList[dlDspRf] = MainActivity.subTitleNameClass.getSettingSubTitle().getmDownlinkDspRfSettingType();
                }
            } else {
                switch (downDspRfInfo[i].getId()) {
                    case Variables.DB_SETTING_ATTEN:
                    case Variables.DB_SETTING_AGC_OFFSET:
                    case Variables.DB_SETTING_SYSTEM_GAIN:
                        tmpBuf = downDspRfInfo[i].getValue() + " dB";
                        break;
                    case Variables.DB_SETTING_OUTPUT_UPPER:
                    case Variables.DB_SETTING_OUTPUT_LOWER:
                    case Variables.DB_SETTING_INPUT_UPPER:
                    case Variables.DB_SETTING_INPUT_LOWER:
                    case Variables.DB_SETTING_INPUT_ALC_LEVEL:
                        tmpBuf = downDspRfInfo[i].getValue() + " dBm";
                        break;
                    case Variables.DB_SETTING_INPUT_ALC_RECOVERY_TIME:
                        tmpBuf = downDspRfInfo[i].getValue() + " sec";
                        break;
                    case Variables.DB_SETTING_INPUT_ALC_PERIOD:
                        tmpBuf = downDspRfInfo[i].getValue() + " sec/dB";
                        break;
                    default:
                        tmpBuf = downDspRfInfo[i].getValue();
                        break;
                }
                mSettingList = new ItemList(downDspRfInfo[i].getName(), tmpBuf);
                list[dlDspRf].add(mSettingList);
                if (i == 0)
                    settingTypeList[dlDspRf] = MainActivity.subTitleNameClass.getSettingSubTitle().getmDownlinkDspRfSettingType();
            }
    }

//        Debug.logv(new Exception(),"upDspRfInfo length : "+upDspRfInfo.length+ " setting size  : "+MainActivity.subTitleNameClass.getSettingSubTitle().getmUplinkDspRfSettingType().size());
        for (int i = 0; i < upDspRfInfo.length; i++) {
            String tmpBuf;
            if (Variables.HIDDEN_ENABLED) {
                boolean flag = false;
                for (int v : Variables.TAB3_UPLINK_HIDDEN_MENU_ID) {
                    if (upDspRfInfo[i].getId() == v) {
                        flag = true;
                        break;
                    }
                }
//                Debug.logv(new Exception(),"upDspRfInfo flag : "+flag+ " getId : "+upDspRfInfo[i].getId());
                if (!flag) {
                    switch (upDspRfInfo[i].getId()) {
                        case Variables.DB_SETTING_ATTEN:
                        case Variables.DB_SETTING_AGC_OFFSET:
                        case Variables.DB_SETTING_SYSTEM_GAIN:
                            tmpBuf = upDspRfInfo[i].getValue() + " dB";
                            break;
                        case Variables.DB_SETTING_OUTPUT_UPPER:
                        case Variables.DB_SETTING_INPUT_UPPER:
                        case Variables.DB_SETTING_INPUT_ALC_LEVEL:
                            tmpBuf = upDspRfInfo[i].getValue() + " dBm";
                            break;
                        case Variables.DB_SETTING_INPUT_ALC_RECOVERY_TIME:
                            tmpBuf = upDspRfInfo[i].getValue() + " sec";
                            break;
                        case Variables.DB_SETTING_INPUT_ALC_PERIOD:
                            tmpBuf = upDspRfInfo[i].getValue() + " sec/dB";
                            break;
                        default:
                            tmpBuf = upDspRfInfo[i].getValue();
                            break;
                    }
                    mSettingList = new ItemList(upDspRfInfo[i].getName(), tmpBuf);
                    list[ulDspRf].add(mSettingList);
                    settingTypeList[ulDspRf].add(MainActivity.subTitleNameClass.getSettingSubTitle().getmUplinkDspRfSettingType().get(i));
//                    if (i == 0)
//                        settingTypeList[ulDspRf] = MainActivity.subTitleNameClass.getSettingSubTitle().getmUplinkDspRfSettingType();
                }
            }else {
                switch (upDspRfInfo[i].getId()) {
                    case Variables.DB_SETTING_ATTEN:
                    case Variables.DB_SETTING_AGC_OFFSET:
                    case Variables.DB_SETTING_SYSTEM_GAIN:
                        tmpBuf = upDspRfInfo[i].getValue() + " dB";
                        break;
                    case Variables.DB_SETTING_OUTPUT_UPPER:
                    case Variables.DB_SETTING_INPUT_UPPER:
                    case Variables.DB_SETTING_INPUT_ALC_LEVEL:
                        tmpBuf = upDspRfInfo[i].getValue() + " dBm";
                        break;
                    case Variables.DB_SETTING_INPUT_ALC_RECOVERY_TIME:
                        tmpBuf = upDspRfInfo[i].getValue() + " sec";
                        break;
                    case Variables.DB_SETTING_INPUT_ALC_PERIOD:
                        tmpBuf = upDspRfInfo[i].getValue() + " sec/dB";
                        break;
                    default:
                        tmpBuf = upDspRfInfo[i].getValue();
                        break;
                }
                mSettingList = new ItemList(upDspRfInfo[i].getName(), tmpBuf);
                list[ulDspRf].add(mSettingList);
                if (i == 0)
                    settingTypeList[ulDspRf] = MainActivity.subTitleNameClass.getSettingSubTitle().getmUplinkDspRfSettingType();
            }
        }

//        Debug.logv(new Exception(),"downAmpInfo length : "+downAmpInfo.length+ " setting size  : "+MainActivity.subTitleNameClass.getSettingSubTitle().getmDownlinkAmpSettingType().size());
        for (int i = 0; i < downAmpInfo.length; i++) {
            String tmpBuf;
            if (Variables.HIDDEN_ENABLED) {
                boolean flag = false;
//                for (int v : Variables.TAB3_DOWNLINK_HIDDEN_MENU_ID) {
//                    if (downAmpInfo[i].getId() == v) {
//                        flag = true;
//                        break;
//                    }
//                }
//                Debug.logv(new Exception(), "downAmpInfo flag : " + flag + " getId : " + downAmpInfo[i].getId());
                if (!flag) {
                    mSettingList = new ItemList(downAmpInfo[i].getName(), downAmpInfo[i].getValue());
                    list[dlAmp].add(mSettingList);
                    settingTypeList[dlAmp].add(MainActivity.subTitleNameClass.getSettingSubTitle().getmDownlinkAmpSettingType().get(i));
//                    if (i == 0)
//                        settingTypeList[dlAmp] = MainActivity.subTitleNameClass.getSettingSubTitle().getmDownlinkAmpSettingType();
                }
            }else {
                mSettingList = new ItemList(downAmpInfo[i].getName(), downAmpInfo[i].getValue());
                list[dlAmp].add(mSettingList);
                if (i == 0)
                    settingTypeList[dlAmp] = MainActivity.subTitleNameClass.getSettingSubTitle().getmDownlinkAmpSettingType();
            }
        }

//        Debug.logv(new Exception(),"upAmpInfo length : "+upAmpInfo.length+ " setting size  : "+MainActivity.subTitleNameClass.getSettingSubTitle().getmUplinkAmpSettingType().size());
        for (int i = 0; i < upAmpInfo.length; i++) {
            String tmpBuf;
            if (Variables.HIDDEN_ENABLED) {
                boolean flag = false;
//                for (int v : Variables.TAB3_UPLINK_HIDDEN_MENU_ID) {
//                    if (upAmpInfo[i].getId() == v) {
//                        flag = true;
//                        break;
//                    }
//                }
//                Debug.logv(new Exception(), "upAmpInfo flag : " + flag + " getId : " + upAmpInfo[i].getId());
                if (!flag) {
                    mSettingList = new ItemList(upAmpInfo[i].getName(), upAmpInfo[i].getValue());
                    list[ulAmp].add(mSettingList);
                    settingTypeList[ulAmp].add(MainActivity.subTitleNameClass.getSettingSubTitle().getmUplinkAmpSettingType().get(i));
//                    if (i == 0)
//                        settingTypeList[ulAmp] = MainActivity.subTitleNameClass.getSettingSubTitle().getmUplinkAmpSettingType();
                }
            }else {
                mSettingList = new ItemList(upAmpInfo[i].getName(), upAmpInfo[i].getValue());
                list[ulAmp].add(mSettingList);
                if (i == 0)
                    settingTypeList[ulAmp] = MainActivity.subTitleNameClass.getSettingSubTitle().getmUplinkAmpSettingType();
            }
        }

        for (int i = 0; i < serviceFaInfo.length; i++) {
            String tmpBuf;
            if ((Variables.bandStruct[0].sets.tFreqSelect10M15M != 0) || (Variables.band != 0 && Variables.band != 4)) {

                boolean flag = false;
                for (int v : Variables.TAB3_SERVICEFA_CDMA_HIDDEN_MENU_ID) {
                    if (serviceFaInfo[i].getId() == v) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    switch (serviceFaInfo[i].getId()) {
                        case Variables.DB_SETTING_DL_FA:
                        case Variables.DB_SETTING_UL_FA:
                            tmpBuf = serviceFaInfo[i].getValue() + " ";
                            break;

                        default:
                            tmpBuf = serviceFaInfo[i].getValue();
                    }

                    mSettingList = new ItemList(serviceFaInfo[i].getName(), tmpBuf);
                    list[serviceFa].add(mSettingList);
                    if (i == 0)
                        settingTypeList[serviceFa] = MainActivity.subTitleNameClass.getSettingSubTitle().getmServiceFaSettingType();
                }
            } else {
                switch (serviceFaInfo[i].getId()) {
                    case Variables.DB_SETTING_DL_FA:
                    case Variables.DB_SETTING_UL_FA:
                        tmpBuf = serviceFaInfo[i].getValue() + " ";
                        break;

                    default:
                        tmpBuf = serviceFaInfo[i].getValue();
                }
                mSettingList = new ItemList(serviceFaInfo[i].getName(), tmpBuf);
                list[serviceFa].add(mSettingList);
                if (i == 0)
                    settingTypeList[serviceFa] = MainActivity.subTitleNameClass.getSettingSubTitle().getmServiceFaSettingType();
            }
        }

        for (int i = 0; i < commonInfo.length; i++) {
            String tmpBuf;
            if (Variables.bandStruct[Variables.band].sets.tPowerMode == 0) {

                boolean flag = false;
                for (int v : Variables.TAB3_COMMON_HIDDEN_MENU_ID) {
                    if (commonInfo[i].getId() == v) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    switch (commonInfo[i].getId()) {
                        case Variables.DB_SETTING_SERIAL_NUMBER:
                        case Variables.DB_SETTING_MODEL_NAME:
                        case Variables.DB_SETTING_OPERATOR_NAME:
                        case Variables.DB_SETTING_SUPPLIER_NAME:
                        case Variables.DB_SETTING_INSTALL_ADDR:
                            tmpBuf = commonInfo[i].getValue() + " ";
                            break;

                        default:
                            tmpBuf = commonInfo[i].getValue();
                    }
                    mSettingList = new ItemList(commonInfo[i].getName(), tmpBuf);
                    list[common].add(mSettingList);
                    if (i == 0)
                        settingTypeList[common] = MainActivity.subTitleNameClass.getSettingSubTitle().getmCommonSettingType();
                }
            }else{
                switch (commonInfo[i].getId()) {
                    case Variables.DB_SETTING_SERIAL_NUMBER:
                    case Variables.DB_SETTING_MODEL_NAME:
                    case Variables.DB_SETTING_OPERATOR_NAME:
                    case Variables.DB_SETTING_SUPPLIER_NAME:
                    case Variables.DB_SETTING_INSTALL_ADDR:
                        tmpBuf = commonInfo[i].getValue() + " ";
                        break;

                    default:
                        tmpBuf = commonInfo[i].getValue();
                }
                mSettingList = new ItemList(commonInfo[i].getName(), tmpBuf);
                list[common].add(mSettingList);
                if (i == 0)
                    settingTypeList[common] = MainActivity.subTitleNameClass.getSettingSubTitle().getmCommonSettingType();
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