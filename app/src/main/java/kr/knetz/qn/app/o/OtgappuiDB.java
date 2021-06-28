package kr.knetz.qn.app.o;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.Arrays;

import kr.knetz.qn.app.l.Debug;
import kr.knetz.qn.app.v.c.DataType;
import kr.knetz.qn.app.v.t.SubTitleNameClass;
import kr.knetz.qn.app.v.t.SubTitleNameClass.SettingType;
import kr.knetz.qn.app.v.x.Enums;


public class OtgappuiDB {
    SQLiteDatabase db;
    Context ctx;
    AppSQLiteHelper mHelper;

    public OtgappuiDB(Context ctx) {
        this.ctx = ctx;
        mHelper = new AppSQLiteHelper(ctx);
        mHelper.initialize();
    }

    public class SubTitleArrayList {
        MenuTypeArrayList mAlarm;
        MenuTypeArrayList mStatus;
        MenuTypeArrayList mSetting;
        MenuTypeArrayList mModem;
        MenuTypeArrayList mTsync;

        public SubTitleArrayList() {
            this.mAlarm = new MenuTypeArrayList();
            this.mSetting = new MenuTypeArrayList();
            this.mStatus = new MenuTypeArrayList();
            this.mModem = new MenuTypeArrayList();
            this.mTsync = new MenuTypeArrayList();
        }

        public class MenuTypeArrayList {
            public MenuTypeArrayList() {
                // common
                systemInfoList = new ArrayList<String>();
                psuList = new ArrayList<String>();
                downlinkDspRfList = new ArrayList<String>();
                uplinkDspRfList = new ArrayList<String>();
                downlinkAmpList = new ArrayList<String>();
                uplinkAmpList = new ArrayList<String>();
                serviceFaList = new ArrayList<String>();
                commonList = new ArrayList<String>();
                // modem
                modemParamList = new ArrayList<String>();
                modemCellInfoList = new ArrayList<String>();
                modemRemoteList = new ArrayList<String>();
                modemSimList = new ArrayList<String>();
                modemNetworkList = new ArrayList<String>();
                modemEMSList = new ArrayList<String>();
                // tsync
                tsyncAlarmList = new ArrayList<String>();
                tsyncInfoList = new ArrayList<String>();
                tsyncConfList = new ArrayList<String>();
            }
            // common
            private ArrayList<String> systemInfoList;
            private ArrayList<String> psuList;
            private ArrayList<String> downlinkDspRfList;
            private ArrayList<String> uplinkDspRfList;
            private ArrayList<String> downlinkAmpList;
            private ArrayList<String> uplinkAmpList;
            private ArrayList<String> serviceFaList;
            private ArrayList<String> commonList;
            // modem
            private ArrayList<String> modemParamList;
            private ArrayList<String> modemCellInfoList;
            private ArrayList<String> modemRemoteList;
            private ArrayList<String> modemSimList;
            private ArrayList<String> modemNetworkList;
            private ArrayList<String> modemEMSList;
            // tsync
            private ArrayList<String> tsyncAlarmList;
            private ArrayList<String> tsyncInfoList;
            private ArrayList<String> tsyncConfList;
        }
    }

    public class SubTitleArrayList2 {
        MenuTypeArrayList mAlarm;
        MenuTypeArrayList mStatus;
        MenuTypeArrayList mSetting;
        MenuTypeArrayList mModem;
        MenuTypeArrayList mTsync;

        public SubTitleArrayList2() {
            this.mAlarm = new MenuTypeArrayList();
            this.mSetting = new MenuTypeArrayList();
            this.mStatus = new MenuTypeArrayList();
            this.mModem = new MenuTypeArrayList();
            this.mTsync = new MenuTypeArrayList();
        }

        public class MenuTypeArrayList {
            public MenuTypeArrayList() {
                // common
                systemInfoList = new ArrayList<DataType>();
                psuList = new ArrayList<DataType>();
                downlinkDspRfList = new ArrayList<DataType>();
                uplinkDspRfList = new ArrayList<DataType>();
                downlinkAmpList = new ArrayList<DataType>();
                uplinkAmpList = new ArrayList<DataType>();
                serviceFaList = new ArrayList<DataType>();
                commonList = new ArrayList<DataType>();
                // modem
                modemParamList = new ArrayList<DataType>();
                modemCellInfoList = new ArrayList<DataType>();
                modemRemoteList = new ArrayList<DataType>();
                modemSimList = new ArrayList<DataType>();
                modemNetworkList = new ArrayList<DataType>();
                modemEMSList = new ArrayList<DataType>();
                // tsync
                tsyncAlarmList = new ArrayList<DataType>();
                tsyncInfoList = new ArrayList<DataType>();
                tsyncConfList = new ArrayList<DataType>();
            }
            // common
            private ArrayList<DataType> systemInfoList;
            private ArrayList<DataType> psuList;
            private ArrayList<DataType> downlinkDspRfList;
            private ArrayList<DataType> uplinkDspRfList;
            private ArrayList<DataType> downlinkAmpList;
            private ArrayList<DataType> uplinkAmpList;
            private ArrayList<DataType> serviceFaList;
            private ArrayList<DataType> commonList;
            // modem
            private ArrayList<DataType> modemParamList;
            private ArrayList<DataType> modemCellInfoList;
            private ArrayList<DataType> modemRemoteList;
            private ArrayList<DataType> modemSimList;
            private ArrayList<DataType> modemNetworkList;
            private ArrayList<DataType> modemEMSList;
            // tsync
            private ArrayList<DataType> tsyncAlarmList;
            private ArrayList<DataType> tsyncInfoList;
            private ArrayList<DataType> tsyncConfList;
        }
    }

    public class SubSettingTypeArrayList {
        // common
        ArrayList<SettingType> mPsu;
        ArrayList<SettingType> mDownlinkDspRf;
        ArrayList<SettingType> mUplinkDspRf;
        ArrayList<SettingType> mDownlinkAmp;
        ArrayList<SettingType> mUplinkAmp;
        ArrayList<SettingType> mServiceFa;
        ArrayList<SettingType> mCommon;
        // modem
        ArrayList<SettingType> mModemRemote;
        ArrayList<SettingType> mModemSim;
        ArrayList<SettingType> mModemNetwork;
        ArrayList<SettingType> mModemEMS;
        // tsync
        ArrayList<SettingType> mTsyncConf;


        public SubSettingTypeArrayList(){
            // common
            mPsu = new ArrayList<SettingType>();
            mDownlinkDspRf = new ArrayList<SettingType>();
            mUplinkDspRf = new ArrayList<SettingType>();
            mDownlinkAmp = new ArrayList<SettingType>();
            mUplinkAmp = new ArrayList<SettingType>();
            mServiceFa = new ArrayList<SettingType>();
            mCommon = new ArrayList<SettingType>();
            // modem
            mModemRemote = new ArrayList<SettingType>();
            mModemSim = new ArrayList<SettingType>();
            mModemNetwork = new ArrayList<SettingType>();
            mModemEMS = new ArrayList<SettingType>();
            // tsync
            mTsyncConf = new ArrayList<SettingType>();
        }
    }


    public String[] getItemName(int r_code, int tap_type, int menu_type ){
        String [] list = null;

        db = mHelper.getReadableDatabase();
        Cursor cursor;
        try {
            String query = "SELECT field_name FROM r_menu_name " +
                    "WHERE r_type = (SELECT r_type FROM r_product WHERE r_code = " + r_code + ") AND " +
                    "tap_type = " + tap_type + " AND " +
                    "menu_type = " + menu_type + " ORDER BY menu_seq";
            Debug.logd(new Exception(), query);
            cursor = db.rawQuery(query, null);

            list = new String[cursor.getCount()];

            int i = 0;
            while (cursor.moveToNext()) {
                list[i] = cursor.getString(cursor.getColumnIndex("field_name"));
                i++;
            }
            Debug.logd(new Exception(), Arrays.toString(list));

            cursor.close();
        } catch (SQLiteException e){
            e.printStackTrace();
        }
        mHelper.close();

        return list;
    }

    public int getR_Type(String name){
        int rtn = 0;
        db = mHelper.getReadableDatabase();
        Cursor cursor;

        try {
            String query = "SELECT r_type FROM r_product " + "WHERE r_product_name = '"+ name+"'";

            Debug.logd(new Exception(), query);
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()){
                rtn = cursor.getInt(cursor.getColumnIndex("r_type"));
                Debug.logd(new Exception(), "r_type rtn : " + rtn);
            }
            Debug.loge(new Exception(), "r_type : "+rtn);

            cursor.close();
        } catch (SQLiteException e){
            e.printStackTrace();
        }
        mHelper.close();
        return rtn;
    }


//    public SubTitleNameClass getAllItemName(int r_code ){
//
//        SubTitleArrayList subTitleArrayList = new SubTitleArrayList();
//        SubTitleNameClass subTitleNameClass = new SubTitleNameClass();
//        SubSettingTypeArrayList subSettingTypeArrayList = new SubSettingTypeArrayList();
//        SettingType settingType;
//
//        db = mHelper.getReadableDatabase();
//        Cursor cursor;
//
//        try {
//            String query = "SELECT * FROM r_menu_name WHERE r_type = (SELECT r_type FROM r_product WHERE r_code = "+r_code+") ORDER BY tap_type, menu_type, menu_seq";
//            cursor = db.rawQuery(query,null);
//            while (cursor.moveToNext()){
//                switch(cursor.getInt(cursor.getColumnIndex("tap_type"))){
//                    case 1: // alarm
//                        switch (cursor.getInt(cursor.getColumnIndex("menu_type"))) {
//                            case 1:
////                                Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                subTitleArrayList.mAlarm.systeminfolist.add(cursor.getString(cursor.getColumnIndex("field_name")));
//                                break;
//                            case 2:
////                                Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                subTitleArrayList.mAlarm.commonlist.add(cursor.getString(cursor.getColumnIndex("field_name")));
//                                break;
//                            case 3:
////                                Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                subTitleArrayList.mAlarm.downlinkCommonList.add(cursor.getString(cursor.getColumnIndex("field_name")));
//                                break;
//                            case 4:
////                                Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                subTitleArrayList.mAlarm.uplinkCommonList.add(cursor.getString(cursor.getColumnIndex("field_name")));
//                                break;
//                        }
//                        break;
//                    case 2: // status
//                        switch (cursor.getInt(cursor.getColumnIndex("menu_type"))) {
//                            case 1:
////                                Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                subTitleArrayList.mStatus.systeminfolist.add(cursor.getString(cursor.getColumnIndex("field_name")));
//                                break;
//                            case 2:
////                                Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                subTitleArrayList.mStatus.commonlist.add(cursor.getString(cursor.getColumnIndex("field_name")));
//                                break;
//                            case 3:
////                                Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                subTitleArrayList.mStatus.downlinkCommonList.add(cursor.getString(cursor.getColumnIndex("field_name")));
//                                break;
//                            case 4:
////                                Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                subTitleArrayList.mStatus.uplinkCommonList.add(cursor.getString(cursor.getColumnIndex("field_name")));
//                                break;
//                        }
//                        break;
//                    case 3: // setting
//                        switch (cursor.getInt(cursor.getColumnIndex("menu_type"))) {
//                            case 1:
////                                Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                subTitleArrayList.mSetting.systeminfolist.add(cursor.getString(cursor.getColumnIndex("field_name")));
//                                break;
//                            case 2: // common
////                                Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                subTitleArrayList.mSetting.commonlist.add(cursor.getString(cursor.getColumnIndex("field_name")));
//
////                                settingType.type = cursor.getInt(cursor.getColumnIndex("setting_type"));
////                                if (settingType.type != 1){
////                                    settingType.min_value = cursor.getInt(cursor.getColumnIndex("setting_min_value"));
////                                    settingType.max_value = cursor.getInt(cursor.getColumnIndex("setting_max_value"));
////                                }
//                                settingType = setSettingType(cursor);
////                                subSettingTypeArrayList.mCommon.add(settingType);
//                                subTitleNameClass.getSettingSubTitle().getmCommonSettingType().add(settingType);
//                                break;
//                            case 3: // downlink
////                                Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                subTitleArrayList.mSetting.downlinkCommonList.add(cursor.getString(cursor.getColumnIndex("field_name")));
//                                settingType = setSettingType(cursor);
////                                subSettingTypeArrayList.mDownlink.add(settingType);
//                                subTitleNameClass.getSettingSubTitle().getmDownlinkCommonSettingType().add(settingType);
//                                break;
//                            case 4: // uplink
////                                Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                subTitleArrayList.mSetting.uplinkCommonList.add(cursor.getString(cursor.getColumnIndex("field_name")));
//                                settingType = setSettingType(cursor);
////                                subSettingTypeArrayList.mUplink.add(settingType);
//                                subTitleNameClass.getSettingSubTitle().getmUplinkCommonSettingType().add(settingType);
//                                break;
//                        }
//                        break;
//                    case 4: // modem
//                        switch (cursor.getInt(cursor.getColumnIndex("menu_type"))) {
//                            case 1:
////                                Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                subTitleArrayList.mModem.systeminfolist.add(cursor.getString(cursor.getColumnIndex("field_name")));
//                                break;
//                            case 2: // common
//                                subTitleArrayList.mModem.commonlist.add(cursor.getString(cursor.getColumnIndex("field_name")));
//                                break;
//                            case 3: // downlink
//                                subTitleArrayList.mModem.downlinkCommonList.add(cursor.getString(cursor.getColumnIndex("field_name")));
//                                break;
//                            case 4: // uplink
//                                subTitleArrayList.mModem.uplinkCommonList.add(cursor.getString(cursor.getColumnIndex("field_name")));
//                                break;
//                        }
//                        break;
//                }
//            }
//
//            cursor.close();
//        } catch (SQLiteException e){
//            e.printStackTrace();
//        }
//
//        printSettingTypeArrayList(subTitleNameClass.getSettingSubTitle().getmCommonSettingType());
//        printSettingTypeArrayList(subTitleNameClass.getSettingSubTitle().getmDownlinkCommonSettingType());
//        printSettingTypeArrayList(subTitleNameClass.getSettingSubTitle().getmUplinkCommonSettingType());
//
////        printSubTitleNameArrayList(subTitleArrayList);
//        copyArrayListToStringArray(subTitleArrayList,subTitleNameClass);
//
////        printSubTitleNameStringArray(subTitleNameClass);
//
//        mHelper.close();
//
//        return subTitleNameClass;
//    }

        public SubTitleNameClass getAllItemName(int r_code ){
            Debug.loge(new Exception(),"Knetz getAllItemName r_code : "+r_code);

        SubTitleArrayList2 subTitleArrayList = new SubTitleArrayList2();
        SubTitleNameClass subTitleNameClass = new SubTitleNameClass();
        SubSettingTypeArrayList subSettingTypeArrayList = new SubSettingTypeArrayList();
        SettingType settingType;
        Boolean isSucess = true;
        db = mHelper.getReadableDatabase();
        Cursor cursor;

        try {
            String query = "SELECT * FROM r_menu_name WHERE r_type = (SELECT r_type FROM r_product WHERE r_code = "+r_code+") ORDER BY tap_type, menu_type, menu_seq";
            Debug.loge(new Exception(),"Knetz query : "+query);
            cursor = db.rawQuery(query,null);

            while (cursor.moveToNext()){
                Enums.eTapType eTapType = Enums.eTapType.fromInteger(cursor.getInt(cursor.getColumnIndex("tap_type")));
                Enums.eMenuType eMenuType = Enums.eMenuType.fromInteger(cursor.getInt(cursor.getColumnIndex("menu_type")));
                if (eTapType != null){
                    switch(eTapType){
                        case ALARM:
                            if (eMenuType != null){
                                try {
                                    switch (eMenuType) {
                                        case ITEM_1:
//                                            Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                            subTitleArrayList.mAlarm.systemInfoList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mAlarm.systemInfoList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            break;
                                        case ITEM_2:
//                                            Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                            subTitleArrayList.mAlarm.psuList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mAlarm.psuList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            break;
                                        case ITEM_3:
//                                            Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                            subTitleArrayList.mAlarm.downlinkDspRfList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mAlarm.downlinkDspRfList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            break;
                                        case ITEM_4:
//                                            Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                            subTitleArrayList.mAlarm.uplinkDspRfList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mAlarm.uplinkDspRfList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            break;
                                        case ITEM_5:
//                                            Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                            subTitleArrayList.mAlarm.downlinkAmpList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mAlarm.downlinkAmpList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            break;
                                        case ITEM_6:
//                                            Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                            subTitleArrayList.mAlarm.uplinkAmpList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mAlarm.uplinkAmpList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            break;
                                    }
                                }catch (Exception eAlarm){
                                    Debug.loge(new Exception(), "eAlarm exception : "+eAlarm.toString());
                                }
                            }
                            break;

                        case STATUS:
                            try {
                                if (eMenuType != null) {
                                    switch (eMenuType) {
                                        case ITEM_1:
//                                            Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                            subTitleArrayList.mStatus.systemInfoList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mStatus.systemInfoList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            break;
                                        case ITEM_2:
//                                            Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                            subTitleArrayList.mStatus.psuList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mStatus.psuList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            break;
                                        case ITEM_3:
//                                            Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                            subTitleArrayList.mStatus.downlinkDspRfList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mStatus.downlinkDspRfList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            break;
                                        case ITEM_4:
//                                            Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                            subTitleArrayList.mStatus.uplinkDspRfList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mStatus.uplinkDspRfList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            break;
                                        case ITEM_5:
//                                            Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                            subTitleArrayList.mStatus.downlinkAmpList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mStatus.downlinkAmpList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            break;
                                        case ITEM_6:
//                                            Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                            subTitleArrayList.mStatus.uplinkAmpList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mStatus.uplinkAmpList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            break;
                                        case ITEM_7:
//                                            Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                            subTitleArrayList.mStatus.serviceFaList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mStatus.serviceFaList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            break;
                                        case ITEM_8:
//                                            Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                            subTitleArrayList.mStatus.commonList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mStatus.commonList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            break;
                                    }
                                }
                            }catch (Exception eStatus){
                                Debug.loge(new Exception(), "eStatus exception : "+eStatus.toString());
                            }
                            break;

                        case SETTING:
                            try {
                                if (eMenuType != null) {
                                    switch (eMenuType) {
                                        case ITEM_1:
                                        Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                        subTitleArrayList.mSetting.systemInfoList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mSetting.systemInfoList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            settingType = setSettingType(cursor);
                                            subTitleNameClass.getSettingSubTitle().getmSystemSettingType().add(settingType);
                                            break;
                                        case ITEM_3:
                                        Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                        subTitleArrayList.mSetting.downlinkDspRfList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mSetting.downlinkDspRfList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            settingType = setSettingType(cursor);
                                            subTitleNameClass.getSettingSubTitle().getmDownlinkDspRfSettingType().add(settingType);
                                            break;
                                        case ITEM_4:
                                        Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                        subTitleArrayList.mSetting.uplinkDspRfList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mSetting.uplinkDspRfList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            settingType = setSettingType(cursor);
                                            subTitleNameClass.getSettingSubTitle().getmUplinkDspRfSettingType().add(settingType);
                                            break;
                                        case ITEM_5:
                                        Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                        subTitleArrayList.mSetting.downlinkAmpList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mSetting.downlinkAmpList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            settingType = setSettingType(cursor);
                                            subTitleNameClass.getSettingSubTitle().getmDownlinkAmpSettingType().add(settingType);
                                            break;
                                        case ITEM_6:
                                        Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                        subTitleArrayList.mSetting.uplinkAmpList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mSetting.uplinkAmpList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            settingType = setSettingType(cursor);
                                            subTitleNameClass.getSettingSubTitle().getmUplinkAmpSettingType().add(settingType);
                                            break;
                                        case ITEM_7:
                                        Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                        subTitleArrayList.mSetting.serviceFaList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mSetting.serviceFaList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            settingType = setSettingType(cursor);
                                            subTitleNameClass.getSettingSubTitle().getmServiceFaSettingType().add(settingType);
                                            break;
                                        case ITEM_8:
                                        Debug.loge(new Exception(),cursor.getString(cursor.getColumnIndex("field_name")));
//                                        subTitleArrayList.mSetting.commonList.add(cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mSetting.commonList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            settingType = setSettingType(cursor);
                                            subTitleNameClass.getSettingSubTitle().getmCommonSettingType().add(settingType);
                                            break;
                                    }
                                }
                            }catch (Exception eSetting){
                                Debug.loge(new Exception(), "eSetting exception : "+eSetting.toString());
                            }
                            break;

                        case MODEM: // modem
                            try {
                                if (eMenuType != null) {
                                    switch (eMenuType) {
                                        case ITEM_1:
                                            Debug.loge(new Exception(),"dhjung1 " + cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mModem.modemParamList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            settingType = setSettingType(cursor);
                                            subTitleNameClass.getModemSubTitle().getmModemParamSettingType().add(settingType);
                                            break;
                                        case ITEM_2:
                                            Debug.loge(new Exception(),"dhjung2 " + cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mModem.modemCellInfoList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            settingType = setSettingType(cursor);
                                            subTitleNameClass.getModemSubTitle().getmModemCellInfoSettingType().add(settingType);
                                            break;
                                        case ITEM_3:
                                            Debug.loge(new Exception(),"dhjung3 " + cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mModem.modemRemoteList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            settingType = setSettingType(cursor);
                                            subTitleNameClass.getModemSubTitle().getmModemRemoteSettingType().add(settingType);
                                            break;
                                        case ITEM_4:
                                            Debug.loge(new Exception(),"dhjung4 " + cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mModem.modemSimList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            settingType = setSettingType(cursor);
                                            subTitleNameClass.getModemSubTitle().getmModemSimSettingType().add(settingType);
                                            break;
                                        case ITEM_5:
                                            Debug.loge(new Exception(),"dhjung5 " + cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mModem.modemNetworkList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            settingType = setSettingType(cursor);
                                            subTitleNameClass.getModemSubTitle().getmModemNetworkSettingType().add(settingType);
                                            break;
                                        case ITEM_6:
                                            Debug.loge(new Exception(),"dhjung6 " + cursor.getString(cursor.getColumnIndex("field_name")));
                                            subTitleArrayList.mModem.modemEMSList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                            settingType = setSettingType(cursor);
                                            subTitleNameClass.getModemSubTitle().getmModemEMSSettingType().add(settingType);
                                            break;
                                    }
                                }
                            }catch (Exception eModem){
                                Debug.loge(new Exception(), "eModem exception : "+eModem.toString());
                            }
                            break;


                        case TSYNC: // tsync
                            if (eMenuType != null) {
                                switch (eMenuType) {
                                    case ITEM_1:
//                                        Debug.loge(new Exception(),"dhjung1 " + cursor.getString(cursor.getColumnIndex("field_name")));
                                        subTitleArrayList.mTsync.tsyncAlarmList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                        break;
                                    case ITEM_2:
//                                        Debug.loge(new Exception(),"dhjung2 " + cursor.getString(cursor.getColumnIndex("field_name")));
                                        subTitleArrayList.mTsync.tsyncInfoList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                        break;
                                    case ITEM_3:
//                                        Debug.loge(new Exception(),"dhjung3 " + cursor.getString(cursor.getColumnIndex("field_name")));
                                        subTitleArrayList.mTsync.tsyncConfList.add(new DataType(cursor.getString(cursor.getColumnIndex("field_name")), cursor.getInt(cursor.getColumnIndex("field_name_id"))));
                                        settingType = setSettingType(cursor);
                                        subTitleNameClass.getTsyncSubTitle().getmTsyncConfigureSettingType().add(settingType);
                                        break;
                                }
                            }
                            break;
                    }
                }
            }
            cursor.close();
        } catch (SQLiteException e){
            e.printStackTrace();
            isSucess = false;
        }catch (Exception ee){
            Debug.loge(new Exception()," exception : "+ee.toString());
            isSucess = false;
        }

    if (isSucess){
        printSettingTypeArrayList(subTitleNameClass.getSettingSubTitle().getmDownlinkDspRfSettingType());
        printSettingTypeArrayList(subTitleNameClass.getSettingSubTitle().getmUplinkDspRfSettingType());
        printSettingTypeArrayList(subTitleNameClass.getSettingSubTitle().getmDownlinkAmpSettingType());
        printSettingTypeArrayList(subTitleNameClass.getSettingSubTitle().getmUplinkAmpSettingType());
        printSettingTypeArrayList(subTitleNameClass.getSettingSubTitle().getmServiceFaSettingType());
        printSettingTypeArrayList(subTitleNameClass.getSettingSubTitle().getmCommonSettingType());
//        printSubTitleNameArrayList2(subTitleArrayList);

//    subTitleNameClass.getAlarmSubTitle().setSystemInformationSubTitle
//            (subTitleArrayList.mAlarm.systeminfolist.toArray(new String[subTitleArrayList.mAlarm.systeminfolist.size()]));subTitleNameClass.getAlarmSubTitle().setSystemInformationSubTitle
//    subTitleNameClass.getAlarmSubTitle().setSystemInformationSubTitle
//            (subTitleArrayList.mAlarm.systeminfolist.toArray(new DataType[subTitleArrayList.mAlarm.systeminfolist.size()]));

//    Debug.logd(new Exception(),"alarm sys"+ Arrays.toString(dataTypes));
//    copyArrayListToStringArray(subTitleArrayList,subTitleNameClass);

        copyArrayListToStringArray2(subTitleArrayList, subTitleNameClass);

//        printSubTitleNameStringArray(subTitleNameClass);
    }else{
        Debug.loge(new Exception(),"8888888888888888888888888888888888888888888888888888888888888888888888888888888888m");
    }

    mHelper.close();

    return subTitleNameClass;
}


    public SettingType setSettingType(Cursor cursor){
        SubTitleNameClass subNameClass = new SubTitleNameClass();
        SettingType settingType = subNameClass.new SettingType();
        settingType.type = cursor.getInt(cursor.getColumnIndex("setting_type"));
        if (settingType.type != 1){
            settingType.min_value = cursor.getInt(cursor.getColumnIndex("setting_min_value"));
            settingType.max_value = cursor.getInt(cursor.getColumnIndex("setting_max_value"));
        }
        return settingType;
    }

    void printSettingTypeArrayList(ArrayList<SettingType> subSettingTypeArrayList){
        Debug.loge(new Exception(),"=====================================");
        for (int i = 0; i < subSettingTypeArrayList.size(); i++){
            Debug.logd(new Exception(),"type : "+subSettingTypeArrayList.get(i).type);
            if (subSettingTypeArrayList.get(i).type != 1) {
                Debug.logd(new Exception(), "min : " + subSettingTypeArrayList.get(i).min_value);
                Debug.logd(new Exception(), "max : " + subSettingTypeArrayList.get(i).max_value);
            }
        }
        Debug.loge(new Exception(),"=====================================");
    }

//    void copyArrayListToStringArray(SubTitleArrayList subTitleArrayList, SubTitleNameClass subTitleNameClass){
//        subTitleNameClass.getAlarmSubTitle().setSystemInformationSubTitle
//                (subTitleArrayList.mAlarm.systeminfolist.toArray(new String[subTitleArrayList.mAlarm.systeminfolist.size()]));
//        subTitleNameClass.getAlarmSubTitle().setCommonSubTitle
//                (subTitleArrayList.mAlarm.commonlist.toArray(new String[subTitleArrayList.mAlarm.commonlist.size()]));
//        subTitleNameClass.getAlarmSubTitle().setDownlinkCommonSubTitle
//                (subTitleArrayList.mAlarm.downlinkCommonList.toArray(new String[subTitleArrayList.mAlarm.downlinkCommonList.size()]));
//        subTitleNameClass.getAlarmSubTitle().setUplinkCommonSubTitle
//                (subTitleArrayList.mAlarm.uplinkCommonList.toArray(new String[subTitleArrayList.mAlarm.uplinkCommonList.size()]));
//
//        subTitleNameClass.getStatusSubTitle().setSystemInformationSubTitle
//                (subTitleArrayList.mStatus.systeminfolist.toArray(new String[subTitleArrayList.mStatus.systeminfolist.size()]));
//        subTitleNameClass.getStatusSubTitle().setCommonSubTitle
//                (subTitleArrayList.mStatus.commonlist.toArray(new String[subTitleArrayList.mStatus.commonlist.size()]));
//        subTitleNameClass.getStatusSubTitle().setDownlinkCommonSubTitle
//                (subTitleArrayList.mStatus.downlinkCommonList.toArray(new String[subTitleArrayList.mStatus.downlinkCommonList.size()]));
//        subTitleNameClass.getStatusSubTitle().setUplinkCommonSubTitle
//                (subTitleArrayList.mStatus.uplinkCommonList.toArray(new String[subTitleArrayList.mStatus.uplinkCommonList.size()]));
//
//        subTitleNameClass.getSettingSubTitle().setSystemInformationSubTitle
//                (subTitleArrayList.mSetting.systeminfolist.toArray(new String[subTitleArrayList.mSetting.systeminfolist.size()]));
//        subTitleNameClass.getSettingSubTitle().setCommonSubTitle
//                (subTitleArrayList.mSetting.commonlist.toArray(new String[subTitleArrayList.mSetting.commonlist.size()]));
//        subTitleNameClass.getSettingSubTitle().setDownlinkCommonSubTitle
//                (subTitleArrayList.mSetting.downlinkCommonList.toArray(new String[subTitleArrayList.mSetting.downlinkCommonList.size()]));
//        subTitleNameClass.getSettingSubTitle().setUplinkCommonSubTitle
//                (subTitleArrayList.mSetting.uplinkCommonList.toArray(new String[subTitleArrayList.mSetting.uplinkCommonList.size()]));
//
//        subTitleNameClass.getModemSubTitle().setSystemInformationSubTitle
//                (subTitleArrayList.mModem.systeminfolist.toArray(new String[subTitleArrayList.mModem.systeminfolist.size()]));
//        subTitleNameClass.getModemSubTitle().setCommonSubTitle
//                (subTitleArrayList.mModem.commonlist.toArray(new String[subTitleArrayList.mModem.commonlist.size()]));
//        subTitleNameClass.getModemSubTitle().setDownlinkCommonSubTitle
//                (subTitleArrayList.mModem.downlinkCommonList.toArray(new String[subTitleArrayList.mModem.downlinkCommonList.size()]));
//        subTitleNameClass.getModemSubTitle().setUplinkCommonSubTitle
//                (subTitleArrayList.mModem.uplinkCommonList.toArray(new String[subTitleArrayList.mModem.uplinkCommonList.size()]));
//
//    }

    void copyArrayListToStringArray2(SubTitleArrayList2 subTitleArrayList, SubTitleNameClass subTitleNameClass){

        subTitleNameClass.getAlarmSubTitle().setSystemInformationSubTitle
                (subTitleArrayList.mAlarm.systemInfoList.toArray(new DataType[subTitleArrayList.mAlarm.systemInfoList.size()]));
        subTitleNameClass.getAlarmSubTitle().setPsuSubTitle
                (subTitleArrayList.mAlarm.psuList.toArray(new DataType[subTitleArrayList.mAlarm.psuList.size()]));
        subTitleNameClass.getAlarmSubTitle().setDownlinkDspRfSubTitle
                (subTitleArrayList.mAlarm.downlinkDspRfList.toArray(new DataType[subTitleArrayList.mAlarm.downlinkDspRfList.size()]));
        subTitleNameClass.getAlarmSubTitle().setUplinkDspRfSubTitle
                (subTitleArrayList.mAlarm.uplinkDspRfList.toArray(new DataType[subTitleArrayList.mAlarm.uplinkDspRfList.size()]));
        subTitleNameClass.getAlarmSubTitle().setDownlinkAmpSubTitle
                (subTitleArrayList.mAlarm.downlinkAmpList.toArray(new DataType[subTitleArrayList.mAlarm.downlinkAmpList.size()]));
        subTitleNameClass.getAlarmSubTitle().setUplinkAmpSubTitle
                (subTitleArrayList.mAlarm.uplinkAmpList.toArray(new DataType[subTitleArrayList.mAlarm.uplinkAmpList.size()]));

        subTitleNameClass.getStatusSubTitle().setSystemInformationSubTitle
                (subTitleArrayList.mStatus.systemInfoList.toArray(new DataType[subTitleArrayList.mStatus.systemInfoList.size()]));
        subTitleNameClass.getStatusSubTitle().setPsuSubTitle
                (subTitleArrayList.mStatus.psuList.toArray(new DataType[subTitleArrayList.mStatus.psuList.size()]));
        subTitleNameClass.getStatusSubTitle().setDownlinkDspRfSubTitle
                (subTitleArrayList.mStatus.downlinkDspRfList.toArray(new DataType[subTitleArrayList.mStatus.downlinkDspRfList.size()]));
        subTitleNameClass.getStatusSubTitle().setUplinkDspRfSubTitle
                (subTitleArrayList.mStatus.uplinkDspRfList.toArray(new DataType[subTitleArrayList.mStatus.uplinkDspRfList.size()]));
        subTitleNameClass.getStatusSubTitle().setDownlinkAmpSubTitle
                (subTitleArrayList.mStatus.downlinkAmpList.toArray(new DataType[subTitleArrayList.mStatus.downlinkAmpList.size()]));
        subTitleNameClass.getStatusSubTitle().setUplinkAmpSubTitle
                (subTitleArrayList.mStatus.uplinkAmpList.toArray(new DataType[subTitleArrayList.mStatus.uplinkAmpList.size()]));

        subTitleNameClass.getSettingSubTitle().setSystemInformationSubTitle
                (subTitleArrayList.mSetting.systemInfoList.toArray(new DataType[subTitleArrayList.mSetting.systemInfoList.size()]));
        subTitleNameClass.getSettingSubTitle().setDownlinkDspRfSubTitle
                (subTitleArrayList.mSetting.downlinkDspRfList.toArray(new DataType[subTitleArrayList.mSetting.downlinkDspRfList.size()]));
        subTitleNameClass.getSettingSubTitle().setUplinkDspRfSubTitle
                (subTitleArrayList.mSetting.uplinkDspRfList.toArray(new DataType[subTitleArrayList.mSetting.uplinkDspRfList.size()]));
        subTitleNameClass.getSettingSubTitle().setDownlinkAmpSubTitle
                (subTitleArrayList.mSetting.downlinkAmpList.toArray(new DataType[subTitleArrayList.mSetting.downlinkAmpList.size()]));
        subTitleNameClass.getSettingSubTitle().setUplinkAmpSubTitle
                (subTitleArrayList.mSetting.uplinkAmpList.toArray(new DataType[subTitleArrayList.mSetting.uplinkAmpList.size()]));
        subTitleNameClass.getSettingSubTitle().setServiceFaSubTitle
                (subTitleArrayList.mSetting.serviceFaList.toArray(new DataType[subTitleArrayList.mSetting.serviceFaList.size()]));
        subTitleNameClass.getSettingSubTitle().setCommonSubTitle
                (subTitleArrayList.mSetting.commonList.toArray(new DataType[subTitleArrayList.mSetting.commonList.size()]));

        subTitleNameClass.getModemSubTitle().setModemParamSubTitle
                (subTitleArrayList.mModem.modemParamList.toArray(new DataType[subTitleArrayList.mModem.modemParamList.size()]));
        subTitleNameClass.getModemSubTitle().setModemCellInfoSubTitle
                (subTitleArrayList.mModem.modemCellInfoList.toArray(new DataType[subTitleArrayList.mModem.modemCellInfoList.size()]));
        subTitleNameClass.getModemSubTitle().setModemRemoteSubTitle
                (subTitleArrayList.mModem.modemRemoteList.toArray(new DataType[subTitleArrayList.mModem.modemRemoteList.size()]));
        subTitleNameClass.getModemSubTitle().setModemSimSubTitle
                (subTitleArrayList.mModem.modemSimList.toArray(new DataType[subTitleArrayList.mModem.modemSimList.size()]));
        subTitleNameClass.getModemSubTitle().setModemNetworkSubTitle
                (subTitleArrayList.mModem.modemNetworkList.toArray(new DataType[subTitleArrayList.mModem.modemNetworkList.size()]));
        subTitleNameClass.getModemSubTitle().setModemEMSSubTitle
                (subTitleArrayList.mModem.modemEMSList.toArray(new DataType[subTitleArrayList.mModem.modemEMSList.size()]));

        subTitleNameClass.getTsyncSubTitle().setTsyncAlarmSubTitle
                (subTitleArrayList.mTsync.tsyncAlarmList.toArray(new DataType[subTitleArrayList.mTsync.tsyncAlarmList.size()]));
        subTitleNameClass.getTsyncSubTitle().setTsyncInfoSubTitle
                (subTitleArrayList.mTsync.tsyncInfoList.toArray(new DataType[subTitleArrayList.mTsync.tsyncInfoList.size()]));
        subTitleNameClass.getTsyncSubTitle().setTsyncConfigureSubTitle
                (subTitleArrayList.mTsync.tsyncConfList.toArray(new DataType[subTitleArrayList.mTsync.tsyncConfList.size()]));
    }

/*
    void printSubTitleNameStringArray(SubTitleNameClass subTitleNameClass){
        Debug.logd(new Exception(), "alarm sys" + Arrays.toString(subTitleNameClass.getAlarmSubTitle().getSystemInformationSubTitle()));
        Debug.logd(new Exception(), "alarm sta" + Arrays.toString(subTitleNameClass.getAlarmSubTitle().getCommonSubTitle()));
        Debug.logd(new Exception(), "alarm dow" + Arrays.toString(subTitleNameClass.getAlarmSubTitle().getDownlinkCommonSubTitle()));
        Debug.logd(new Exception(), "alarm upl" + Arrays.toString(subTitleNameClass.getAlarmSubTitle().getUplinkCommonSubTitle()));

        Debug.logd(new Exception(), "status sys" + Arrays.toString(subTitleNameClass.getStatusSubTitle().getSystemInformationSubTitle()));
        Debug.logd(new Exception(), "status sta" + Arrays.toString(subTitleNameClass.getStatusSubTitle().getCommonSubTitle()));
        Debug.logd(new Exception(), "status dow" + Arrays.toString(subTitleNameClass.getStatusSubTitle().getDownlinkCommonSubTitle()));
        Debug.logd(new Exception(), "status upl" + Arrays.toString(subTitleNameClass.getStatusSubTitle().getUplinkCommonSubTitle()));

        Debug.logd(new Exception(), "setting sys" + Arrays.toString(subTitleNameClass.getSettingSubTitle().getSystemInformationSubTitle()));
        Debug.logd(new Exception(), "setting sta" + Arrays.toString(subTitleNameClass.getSettingSubTitle().getCommonSubTitle()));
        Debug.logd(new Exception(), "setting dow" + Arrays.toString(subTitleNameClass.getSettingSubTitle().getDownlinkCommonSubTitle()));
        Debug.logd(new Exception(), "setting upl" + Arrays.toString(subTitleNameClass.getSettingSubTitle().getUplinkCommonSubTitle()));

        Debug.logd(new Exception(), "modem sys" + Arrays.toString(subTitleNameClass.getModemSubTitle().getSystemInformationSubTitle()));
        Debug.logd(new Exception(), "modem sta" + Arrays.toString(subTitleNameClass.getModemSubTitle().getCommonSubTitle()));
        Debug.logd(new Exception(), "modem dow" + Arrays.toString(subTitleNameClass.getModemSubTitle().getDownlinkCommonSubTitle()));
        Debug.logd(new Exception(), "modem upl" + Arrays.toString(subTitleNameClass.getModemSubTitle().getUplinkCommonSubTitle()));
    }

    void printSubTitleNameStringArray2(SubTitleNameClass subTitleNameClass){
        Debug.logd(new Exception(),"alarm sys"+ Arrays.toString(subTitleNameClass.getAlarmSubTitle().getSystemInformationSubTitle()));
        Debug.logd(new Exception(),"alarm sta"+ Arrays.toString(subTitleNameClass.getAlarmSubTitle().getCommonSubTitle()));
        Debug.logd(new Exception(),"alarm dow"+ Arrays.toString(subTitleNameClass.getAlarmSubTitle().getDownlinkCommonSubTitle()));
        Debug.logd(new Exception(),"alarm upl"+ Arrays.toString(subTitleNameClass.getAlarmSubTitle().getUplinkCommonSubTitle()));

        Debug.logd(new Exception(),"status sys"+ Arrays.toString(subTitleNameClass.getStatusSubTitle().getSystemInformationSubTitle()));
        Debug.logd(new Exception(),"status sta"+ Arrays.toString(subTitleNameClass.getStatusSubTitle().getCommonSubTitle()));
        Debug.logd(new Exception(),"status dow"+ Arrays.toString(subTitleNameClass.getStatusSubTitle().getDownlinkCommonSubTitle()));
        Debug.logd(new Exception(),"status upl"+ Arrays.toString(subTitleNameClass.getStatusSubTitle().getUplinkCommonSubTitle()));

        Debug.logd(new Exception(),"setting sys"+ Arrays.toString(subTitleNameClass.getSettingSubTitle().getSystemInformationSubTitle()));
        Debug.logd(new Exception(),"setting sta"+ Arrays.toString(subTitleNameClass.getSettingSubTitle().getCommonSubTitle()));
        Debug.logd(new Exception(),"setting dow"+ Arrays.toString(subTitleNameClass.getSettingSubTitle().getDownlinkCommonSubTitle()));
        Debug.logd(new Exception(),"setting upl"+ Arrays.toString(subTitleNameClass.getSettingSubTitle().getUplinkCommonSubTitle()));

        Debug.logd(new Exception(),"modem sys"+  Arrays.toString(subTitleNameClass.getModemSubTitle().getSystemInformationSubTitle()));
        Debug.logd(new Exception(),"modem sta"+  Arrays.toString(subTitleNameClass.getModemSubTitle().getCommonSubTitle()));
        Debug.logd(new Exception(),"modem dow"+  Arrays.toString(subTitleNameClass.getModemSubTitle().getDownlinkCommonSubTitle()));
        Debug.logd(new Exception(),"modem upl"+  Arrays.toString(subTitleNameClass.getModemSubTitle().getUplinkCommonSubTitle()));
    }
*/

/*
    void printSubTitleNameArrayList(SubTitleArrayList subTitleArrayList){
        Debug.loge(new Exception(),"alarm sys info size : "+subTitleArrayList.mAlarm.systeminfolist.size());
        for(int i = 0; i < subTitleArrayList.mAlarm.systeminfolist.size(); i++){
            Debug.logd(new Exception(), subTitleArrayList.mAlarm.systeminfolist.get(i));
        }
        Debug.loge(new Exception(),"alarm common size : "+subTitleArrayList.mAlarm.commonlist.size());
        for(int i = 0; i < subTitleArrayList.mAlarm.commonlist.size(); i++){
            Debug.logd(new Exception(), subTitleArrayList.mAlarm.commonlist.get(i));
        }
        Debug.loge(new Exception(),"alarm downlink size : "+subTitleArrayList.mAlarm.downlinkCommonList.size());
        for(int i = 0; i < subTitleArrayList.mAlarm.downlinkCommonList.size(); i++){
            Debug.logd(new Exception(), subTitleArrayList.mAlarm.downlinkCommonList.get(i));
        }
        Debug.loge(new Exception(),"alarm uplink size : "+subTitleArrayList.mAlarm.uplinkCommonList.size());
        for(int i = 0; i < subTitleArrayList.mAlarm.uplinkCommonList.size(); i++){
            Debug.logd(new Exception(), subTitleArrayList.mAlarm.uplinkCommonList.get(i));
        }
        Debug.loge(new Exception(),"=====================================================");
        Debug.loge(new Exception(),"status sys info size : "+subTitleArrayList.mStatus.systeminfolist.size());
        for(int i = 0; i < subTitleArrayList.mStatus.systeminfolist.size(); i++){
            Debug.logd(new Exception(), subTitleArrayList.mStatus.systeminfolist.get(i));
        }
        Debug.loge(new Exception(),"status common size : "+subTitleArrayList.mStatus.commonlist.size());
        for(int i = 0; i < subTitleArrayList.mStatus.commonlist.size(); i++){
            Debug.logd(new Exception(), subTitleArrayList.mStatus.commonlist.get(i));
        }
        Debug.loge(new Exception(),"status downlink size : "+subTitleArrayList.mStatus.downlinkCommonList.size());
        for(int i = 0; i < subTitleArrayList.mStatus.downlinkCommonList.size(); i++){
            Debug.logd(new Exception(), subTitleArrayList.mStatus.downlinkCommonList.get(i));
        }
        Debug.loge(new Exception(),"status uplink size : "+subTitleArrayList.mStatus.uplinkCommonList.size());
        for(int i = 0; i < subTitleArrayList.mStatus.uplinkCommonList.size(); i++){
            Debug.logd(new Exception(), subTitleArrayList.mStatus.uplinkCommonList.get(i));
        }
        Debug.loge(new Exception(),"=====================================================");
        Debug.loge(new Exception(),"setting  sys info size : "+subTitleArrayList.mSetting.systeminfolist.size());
        for(int i = 0; i < subTitleArrayList.mSetting.systeminfolist.size(); i++){
            Debug.logd(new Exception(), subTitleArrayList.mSetting.systeminfolist.get(i));
        }
        Debug.loge(new Exception(),"setting common size : "+subTitleArrayList.mSetting.commonlist.size());
        for(int i = 0; i < subTitleArrayList.mSetting.commonlist.size(); i++){
            Debug.logd(new Exception(), subTitleArrayList.mSetting.commonlist.get(i));
        }
        Debug.loge(new Exception(),"setting downlink size : "+subTitleArrayList.mSetting.downlinkCommonList.size());
        for(int i = 0; i < subTitleArrayList.mSetting.downlinkCommonList.size(); i++){
            Debug.logd(new Exception(), subTitleArrayList.mSetting.downlinkCommonList.get(i));
        }
        Debug.loge(new Exception(),"setting uplink size : "+subTitleArrayList.mSetting.uplinkCommonList.size());
        for(int i = 0; i < subTitleArrayList.mSetting.uplinkCommonList.size(); i++){
            Debug.logd(new Exception(), subTitleArrayList.mSetting.uplinkCommonList.get(i));
        }
        Debug.loge(new Exception(),"=====================================================");
    }
*/
//
//    void printSubTitleNameArrayList2(SubTitleArrayList2 subTitleArrayList){
//        Debug.loge(new Exception(),"alarm sys info size : "+subTitleArrayList.mAlarm.systemInfoList.size());
//        for(int i = 0; i < subTitleArrayList.mAlarm.systemInfoList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mAlarm.systemInfoList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mAlarm.systemInfoList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"alarm psu size : "+subTitleArrayList.mAlarm.psuList.size());
//        for(int i = 0; i < subTitleArrayList.mAlarm.psuList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mAlarm.psuList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mAlarm.psuList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"alarm downlink dsp&rf size : "+subTitleArrayList.mAlarm.downlinkDspRfList.size());
//        for(int i = 0; i < subTitleArrayList.mAlarm.downlinkDspRfList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mAlarm.downlinkDspRfList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mAlarm.downlinkDspRfList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"alarm uplink dsp&rf size : "+subTitleArrayList.mAlarm.uplinkDspRfList.size());
//        for(int i = 0; i < subTitleArrayList.mAlarm.uplinkDspRfList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mAlarm.uplinkDspRfList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mAlarm.uplinkDspRfList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"alarm downlink amp size : "+subTitleArrayList.mAlarm.downlinkAmpList.size());
//        for(int i = 0; i < subTitleArrayList.mAlarm.downlinkAmpList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mAlarm.downlinkAmpList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mAlarm.downlinkAmpList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"alarm uplink amp size : "+subTitleArrayList.mAlarm.uplinkAmpList.size());
//        for(int i = 0; i < subTitleArrayList.mAlarm.uplinkAmpList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mAlarm.uplinkAmpList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mAlarm.uplinkAmpList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"alarm service fa size : "+subTitleArrayList.mAlarm.serviceFaList.size());
//        for(int i = 0; i < subTitleArrayList.mAlarm.serviceFaList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mAlarm.serviceFaList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mAlarm.serviceFaList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"=====================================================");
//
//        Debug.loge(new Exception(),"status sys info size : "+subTitleArrayList.mStatus.systemInfoList.size());
//        for(int i = 0; i < subTitleArrayList.mStatus.systemInfoList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mStatus.systemInfoList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mStatus.systemInfoList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"status psu size : "+subTitleArrayList.mStatus.psuList.size());
//        for(int i = 0; i < subTitleArrayList.mStatus.psuList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mStatus.psuList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mStatus.psuList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"status downlink dsp&rf size : "+subTitleArrayList.mStatus.downlinkDspRfList.size());
//        for(int i = 0; i < subTitleArrayList.mStatus.downlinkDspRfList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mStatus.downlinkDspRfList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mStatus.downlinkDspRfList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"status uplink dsp&rf size : "+subTitleArrayList.mStatus.uplinkDspRfList.size());
//        for(int i = 0; i < subTitleArrayList.mStatus.uplinkDspRfList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mStatus.uplinkDspRfList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mStatus.uplinkDspRfList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"status downlink amp size : "+subTitleArrayList.mStatus.downlinkAmpList.size());
//        for(int i = 0; i < subTitleArrayList.mStatus.downlinkAmpList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mStatus.downlinkAmpList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mStatus.downlinkAmpList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"status uplink amp size : "+subTitleArrayList.mStatus.uplinkAmpList.size());
//        for(int i = 0; i < subTitleArrayList.mStatus.uplinkAmpList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mStatus.uplinkAmpList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mStatus.uplinkAmpList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"status service fa size : "+subTitleArrayList.mStatus.serviceFaList.size());
//        for(int i = 0; i < subTitleArrayList.mStatus.serviceFaList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mStatus.serviceFaList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mStatus.serviceFaList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"=====================================================");
//
//        Debug.loge(new Exception(),"setting sys info size : "+subTitleArrayList.mSetting.systemInfoList.size());
//        for(int i = 0; i < subTitleArrayList.mSetting.systemInfoList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mSetting.systemInfoList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mSetting.systemInfoList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"setting psu size : "+subTitleArrayList.mSetting.psuList.size());
//        for(int i = 0; i < subTitleArrayList.mSetting.psuList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mSetting.psuList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mSetting.psuList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"setting downlink dsp&rf size : "+subTitleArrayList.mSetting.downlinkDspRfList.size());
//        for(int i = 0; i < subTitleArrayList.mSetting.downlinkDspRfList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mSetting.downlinkDspRfList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mSetting.downlinkDspRfList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"setting uplink dsp&rf size : "+subTitleArrayList.mSetting.uplinkDspRfList.size());
//        for(int i = 0; i < subTitleArrayList.mSetting.uplinkDspRfList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mSetting.uplinkDspRfList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mSetting.uplinkDspRfList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"setting downlink amp size : "+subTitleArrayList.mSetting.downlinkAmpList.size());
//        for(int i = 0; i < subTitleArrayList.mSetting.downlinkAmpList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mSetting.downlinkAmpList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mSetting.downlinkAmpList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"setting uplink amp size : "+subTitleArrayList.mSetting.uplinkAmpList.size());
//        for(int i = 0; i < subTitleArrayList.mSetting.uplinkAmpList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mSetting.uplinkAmpList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mSetting.uplinkAmpList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"setting service fa size : "+subTitleArrayList.mSetting.serviceFaList.size());
//        for(int i = 0; i < subTitleArrayList.mSetting.serviceFaList.size(); i++){
//            Debug.logd(new Exception(), "name : "+subTitleArrayList.mSetting.serviceFaList.get(i).getName());
//            Debug.logd(new Exception(), "id : "+subTitleArrayList.mSetting.serviceFaList.get(i).getId());
//        }
//        Debug.loge(new Exception(),"=====================================================");
//    }
}
