package kr.knetz.kddi.app;


import android.app.Application;
import android.util.Log;

import kr.knetz.kddi.app.l.Debug;
import kr.knetz.kddi.app.o.OtgappuiDB;
import kr.knetz.kddi.app.v.t.SettingsPreference;
import kr.knetz.kddi.app.v.t.SubTitleNameClass;
import kr.knetz.kddi.app.v.x.BandStruct;
import kr.knetz.kddi.app.v.x.ModemStruct;
import kr.knetz.kddi.app.v.x.Variables;


public class KNetzApp extends Application {

    private SubTitleNameClass subTitleNameClass;
    OtgappuiDB mDb;

    public KNetzApp() {
        super();
    }

    final SettingsPreference settingPreference = new SettingsPreference(this);
    boolean flag_init;

    @Override
    public void onCreate() {
        super.onCreate();
        Debug.logi(new Exception(),"=dhjung=======> KNetzApp onCreate");

        Log.e("BJH", "AppSQLiteHelper DB_NAME : ");
        Debug.loge(new Exception(), "onCreate !!Knetz");

        flag_init = SettingsPreference.getValue(SettingsPreference.INITIALIZE_VALUE, false);
        if (!flag_init) {
            Debug.loge(new Exception(), "[Knetz] flag_init : " + flag_init);
            SettingsPreference.put(SettingsPreference.INITIALIZE_VALUE, true);
            // default password
            SettingsPreference.put(SettingsPreference.OLD_PASSWORD, Variables.DEFAULT_PASSWORD);
            // default remember
            SettingsPreference.put(SettingsPreference.REMEMBER_PASSWORD_FLAG, Variables.DEFAULT_PASSWORD_REMEMBER_FLAG);
        }
        mDb = new OtgappuiDB(getBaseContext());

        Variables.SYSTEM_R_TYPE = Variables.SYSTEM_R_TYPE_KDDI_QN;
        getDBAllItemName(Variables.SYSTEM_R_TYPE);

        if(Variables.SYSTEM_R_TYPE == Variables.SYSTEM_R_TYPE_KDDI_QN){
            Variables.PROTO_SYSTEM_TYPE = Variables.SysType_ICS_QUAD;
            Variables.SYSTEM_MODEM_RECEIVER = Variables.SYSTEM_MODEM;
        }
        else if(Variables.SYSTEM_R_TYPE == Variables.SYSTEM_R_TYPE_SUB6_5G){
            Variables.PROTO_SYSTEM_TYPE = Variables.SysType_RF_DUAL;
            Variables.SYSTEM_MODEM_RECEIVER = Variables.SYSTEM_MODEM;
        }

        Variables.bandStruct = new BandStruct[5];   // single, dual, 3band, 4band, 800Mhz CDMA
        for(int i = 0; i < Variables.bandStruct.length;i++){
            Variables.bandStruct[i] = new BandStruct();
        }
        Variables.modemStruct = new ModemStruct();
    }


    public void getDBAllItemName(int type) {

        subTitleNameClass = mDb.getAllItemName(type);
        final int ttype = type;
        Debug.loge(new Exception(), "Knetz onCreate : " + subTitleNameClass.getAlarmSubTitle().getSystemInformationSubTitle().length);
        if (subTitleNameClass.getAlarmSubTitle().getSystemInformationSubTitle().length <= 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean flag = true;
                    int cnt = 0;
                    int nFlag = 0;
                    while (flag) {

                        Debug.loge(new Exception(), "cnt : " + cnt);
                        if (subTitleNameClass.getAlarmSubTitle().getSystemInformationSubTitle().length <= 0) {
                            subTitleNameClass = mDb.getAllItemName(ttype);
                        }
                        Debug.loge(new Exception(), "onCreate 222: " + subTitleNameClass.getAlarmSubTitle().getSystemInformationSubTitle().length);
                        if (subTitleNameClass.getAlarmSubTitle().getSystemInformationSubTitle().length > 0) {
                            break;
                        }

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

                }
            }).start();
        }
    }

    public int getDBRTypeUpdate(String name) {
        int rtype = 0;

        rtype = mDb.getR_Type(name);
        Debug.loge(new Exception(), "getDBRType : " + rtype);
        if (rtype != 0) {
            Variables.SYSTEM_R_TYPE = rtype;
            getDBAllItemName(rtype);
        }
        Debug.loge(new Exception(), "getDBRType222 : " + rtype);

        return rtype;
    }


    public SubTitleNameClass getSubTitleNameClass() {
        return subTitleNameClass;
    }

//    public void setSubTitleNameClass(SubTitleNameClass subTitleNameClass) {
//        OtgApplication.subTitleNameClass = subTitleNameClass;
//    }
}
