package kr.knetz.kddi.app.v.t;

import java.util.ArrayList;
import kr.knetz.kddi.app.v.c.DataType;


public class SubTitleNameClass {

    private MenuTypeSubTitle AlarmSubTitle;
    private MenuTypeSubTitle StatusSubTitle;
    private MenuTypeSubTitle SettingSubTitle;
    private MenuTypeSubTitle ModemSubTitle;
    private MenuTypeSubTitle TsyncSubTitle;

    public SubTitleNameClass(){
        this.AlarmSubTitle = new MenuTypeSubTitle();
        this.StatusSubTitle = new MenuTypeSubTitle();
        this.SettingSubTitle = new MenuTypeSubTitle();
        this.ModemSubTitle = new MenuTypeSubTitle();
        this.TsyncSubTitle = new MenuTypeSubTitle();
    }

    public class MenuTypeSubTitle {
        public MenuTypeSubTitle(){
            // common
            systemInformationSubTitle = null;
            psuSubTitle = null;
            downlinkDspRfSubTitle = null;
            uplinkDspRfSubTitle = null;
            downlinkAmpSubTitle = null;
            uplinkAmpSubTitle = null;
            serviceFaSubTitle = null;
            commonSubTitle = null;

            mSystemSettingType = new ArrayList<SettingType>();
            mDownlinkDspRfSettingType = new ArrayList<SettingType>();
            mUplinkDspRfSettingType = new ArrayList<SettingType>();
            mDownlinkAmpSettingType = new ArrayList<SettingType>();
            mUplinkAmpSettingType = new ArrayList<SettingType>();
            mServiceFaSettingType = new ArrayList<SettingType>();
            mCommonSettingType = new ArrayList<SettingType>();

            // modem
            modemParamSubTitle = null;
            modemRFBasicSubTitle = null;
            modemPSCSubTitle = null;
            modemACHSubTitle = null;
            modemLinkSubTitle = null;
            modemNetworkSubTitle = null;
            modemLoopBackSubTitle = null;
            modemEMSSubTitle = null;
            modemCellInfoSubTitle = null;
            modemRemoteSubTitle = null;
            modemSimSubTitle = null;

            mModemParamSettingType = new ArrayList<SettingType>();
            mModemRfBasicSettingType = new ArrayList<SettingType>();
            mModemPscSettingType = new ArrayList<SettingType>();
            mModemNetworkSettingType = new ArrayList<SettingType>();
            mModemLoopBackSettingType = new ArrayList<SettingType>();
            mModemEMSSettingType = new ArrayList<SettingType>();
            mModemCellInfoSettingType = new ArrayList<SettingType>();
            mModemRemoteSettingType = new ArrayList<SettingType>();
            mModemSimSettingType = new ArrayList<SettingType>();

            // tsync
            tsyncAlarmSubTitle = null;
            tsyncInfoSubTitle = null;
            tsyncConfigureSubTitle = null;

            mTsyncInfoSettingType = new ArrayList<SettingType>();
            mTsyncConfigureSettingType = new ArrayList<SettingType>();
        }

        // common
        DataType[] systemInformationSubTitle;
        DataType[] psuSubTitle;
        DataType[] downlinkDspRfSubTitle;
        DataType[] uplinkDspRfSubTitle;
        DataType[] downlinkAmpSubTitle;
        DataType[] uplinkAmpSubTitle;
        DataType[] serviceFaSubTitle;
        DataType[] commonSubTitle;

        ArrayList<SettingType> mSystemSettingType;
        ArrayList<SettingType> mDownlinkDspRfSettingType;
        ArrayList<SettingType> mUplinkDspRfSettingType;
        ArrayList<SettingType> mDownlinkAmpSettingType;
        ArrayList<SettingType> mUplinkAmpSettingType;
        ArrayList<SettingType> mServiceFaSettingType;
        ArrayList<SettingType> mCommonSettingType;

        // modem
        DataType[] modemParamSubTitle;
        DataType[] modemRFBasicSubTitle;
        DataType[] modemPSCSubTitle;
        DataType[] modemACHSubTitle;
        DataType[] modemLinkSubTitle;
        DataType[] modemNetworkSubTitle;
        DataType[] modemLoopBackSubTitle;
        DataType[] modemEMSSubTitle;
        DataType[] modemCellInfoSubTitle;
        DataType[] modemRemoteSubTitle;
        DataType[] modemSimSubTitle;


        ArrayList<SettingType> mModemParamSettingType;
        ArrayList<SettingType> mModemRfBasicSettingType;
        ArrayList<SettingType> mModemPscSettingType;
        ArrayList<SettingType> mModemNetworkSettingType;
        ArrayList<SettingType> mModemLoopBackSettingType;
        ArrayList<SettingType> mModemEMSSettingType;
        ArrayList<SettingType> mModemCellInfoSettingType;
        ArrayList<SettingType> mModemRemoteSettingType;
        ArrayList<SettingType> mModemSimSettingType;

        // tsync
        DataType[] tsyncAlarmSubTitle;
        DataType[] tsyncInfoSubTitle;
        DataType[] tsyncConfigureSubTitle;

        ArrayList<SettingType> mTsyncInfoSettingType;
        ArrayList<SettingType> mTsyncConfigureSettingType;

        // common title
        public DataType[] getSystemInformationSubTitle() {
            return systemInformationSubTitle;
        }
        public void setSystemInformationSubTitle(DataType[] systemInformationSubTitle) {
            this.systemInformationSubTitle = systemInformationSubTitle;
        }
        public DataType[] getPsuSubTitle() {
            return psuSubTitle;
        }
        public void setPsuSubTitle(DataType[] psuSubTitle) {
            this.psuSubTitle = psuSubTitle;
        }
        public DataType[] getDownlinkDspRfSubTitle() {
            return downlinkDspRfSubTitle;
        }
        public void setDownlinkDspRfSubTitle(DataType[] downlinkDspRfSubTitle) {
            this.downlinkDspRfSubTitle = downlinkDspRfSubTitle;
        }
        public DataType[] getUplinkDspRfSubTitle() {
            return uplinkDspRfSubTitle;
        }
        public void setUplinkDspRfSubTitle(DataType[] uplinkDspRfSubTitle) {
            this.uplinkDspRfSubTitle = uplinkDspRfSubTitle;
        }
        public DataType[] getDownlinkAmpSubTitle() {
            return downlinkAmpSubTitle;
        }
        public void setDownlinkAmpSubTitle(DataType[] downlinkAmpSubTitle) {
            this.downlinkAmpSubTitle = downlinkAmpSubTitle;
        }
        public DataType[] getUplinkAmpSubTitle() {
            return uplinkAmpSubTitle;
        }
        public void setUplinkAmpSubTitle(DataType[] uplinkAmpSubTitle) {
            this.uplinkAmpSubTitle = uplinkAmpSubTitle;
        }
        public DataType[] getServiceFaSubTitle() {
            return serviceFaSubTitle;
        }
        public void setServiceFaSubTitle(DataType[] serviceFaSubTitle) {
            this.serviceFaSubTitle = serviceFaSubTitle;
        }
        public DataType[] getCommonSubTitle() {
            return commonSubTitle;
        }
        public void setCommonSubTitle(DataType[] commonSubTitle) {
            this.commonSubTitle = commonSubTitle;
        }

        // modem title
        public DataType[] getModemParamSubTitle() {
            return modemParamSubTitle;
        }
        public void setModemParamSubTitle(DataType[] modemParamSubTitle) {
            this.modemParamSubTitle = modemParamSubTitle;
        }
        public DataType[] getModemRFBasicSubTitle() {
            return modemRFBasicSubTitle;
        }
        public void setModemRFBasicSubTitle(DataType[] modemRFBasicSubTitle) {
            this.modemRFBasicSubTitle = modemRFBasicSubTitle;
        }
        public DataType[] getModemPSCSubTitle() {
            return modemPSCSubTitle;
        }
        public void setModemPSCSubTitle(DataType[] modemPSCSubTitle) {
            this.modemPSCSubTitle = modemPSCSubTitle;
        }
        public DataType[] getModemACHSubTitle() {
            return modemACHSubTitle;
        }
        public void setModemACHSubTitle(DataType[] modemACHSubTitle) {
            this.modemACHSubTitle = modemACHSubTitle;
        }
        public DataType[] getModemLinkSubTitle() {
            return modemLinkSubTitle;
        }
        public void setModemLinkSubTitle(DataType[] modemLinkSubTitle) {
            this.modemLinkSubTitle = modemLinkSubTitle;
        }
        public DataType[] getModemNetworkSubTitle() {
            return modemNetworkSubTitle;
        }
        public void setModemNetworkSubTitle(DataType[] modemNetworkSubTitle) {
            this.modemNetworkSubTitle = modemNetworkSubTitle;
        }
        public DataType[] getModemLoopBackSubTitle() {
            return modemLoopBackSubTitle;
        }
        public void setModemLoopBackSubTitle(DataType[] modemLoopBackSubTitle) {
            this.modemLoopBackSubTitle = modemLoopBackSubTitle;
        }
        public DataType[] getModemEMSSubTitle() {
            return modemEMSSubTitle;
        }
        public void setModemEMSSubTitle(DataType[] modemEMSSubTitle) {
            this.modemEMSSubTitle = modemEMSSubTitle;
        }
        public DataType[] getModemCellInfoSubTitle() {
            return modemCellInfoSubTitle;
        }
        public void setModemCellInfoSubTitle(DataType[] modemCellInfoSubTitle) {
            this.modemCellInfoSubTitle = modemCellInfoSubTitle;
        }
        public DataType[] getModemRemoteSubTitle() {
            return modemRemoteSubTitle;
        }
        public void setModemRemoteSubTitle(DataType[] modemRemoteSubTitle) {
            this.modemRemoteSubTitle = modemRemoteSubTitle;
        }
        public DataType[] getModemSimSubTitle() {
            return modemSimSubTitle;
        }
        public void setModemSimSubTitle(DataType[] modemSimSubTitle) {
            this.modemSimSubTitle = modemSimSubTitle;
        }

        // tsync title
        public DataType[] getTsyncAlarmSubTitle() {
            return tsyncAlarmSubTitle;
        }
        public void setTsyncAlarmSubTitle(DataType[] tsyncAlarmSubTitle) {
            this.tsyncAlarmSubTitle = tsyncAlarmSubTitle;
        }
        public DataType[] getTsyncInfoSubTitle() {
            return tsyncInfoSubTitle;
        }
        public void setTsyncInfoSubTitle(DataType[] tsyncInfoSubTitle) {
            this.tsyncInfoSubTitle = tsyncInfoSubTitle;
        }
        public DataType[] getTsyncConfigureSubTitle() {
            return tsyncConfigureSubTitle;
        }
        public void setTsyncConfigureSubTitle(DataType[] tsyncConfigureSubTitle) {
            this.tsyncConfigureSubTitle = tsyncConfigureSubTitle;
        }

        // common
        public ArrayList<SettingType> getmSystemSettingType(){
            return mSystemSettingType;
        }
        public void setmSystemSettingType(ArrayList<SettingType> mSystemSettingType){
            this.mSystemSettingType = mSystemSettingType;
        }
        public ArrayList<SettingType> getmDownlinkDspRfSettingType() {
            return mDownlinkDspRfSettingType;
        }
        public void setmDownlinkDspRfSettingType(ArrayList<SettingType> mDownlinkDspRfSettingType) {
            this.mDownlinkDspRfSettingType = mDownlinkDspRfSettingType;
        }
        public ArrayList<SettingType> getmUplinkDspRfSettingType() {
            return mUplinkDspRfSettingType;
        }
        public void setmUplinkDspRfSettingType(ArrayList<SettingType> mUplinkDspRfSettingType) {
            this.mUplinkDspRfSettingType = mUplinkDspRfSettingType;
        }
        public ArrayList<SettingType> getmDownlinkAmpSettingType() {
            return mDownlinkAmpSettingType;
        }
        public void setmDownlinkAmpSettingType(ArrayList<SettingType> mDownlinkAmpSettingType) {
            this.mDownlinkAmpSettingType = mDownlinkAmpSettingType;
        }
        public ArrayList<SettingType> getmUplinkAmpSettingType() {
            return mUplinkAmpSettingType;
        }
        public void setmUplinkAmpSettingType(ArrayList<SettingType> mUplinkAmpSettingType) {
            this.mUplinkAmpSettingType = mUplinkAmpSettingType;
        }
        public ArrayList<SettingType> getmServiceFaSettingType() {
            return mServiceFaSettingType;
        }
        public void setmServiceFaSettingType(ArrayList<SettingType> mServiceFaSettingType) {
            this.mServiceFaSettingType = mServiceFaSettingType;
        }
        public ArrayList<SettingType> getmCommonSettingType() {
            return mCommonSettingType;
        }
        public void setmCommonSettingType(ArrayList<SettingType> mCommonSettingType) {
            this.mCommonSettingType = mCommonSettingType;
        }

        // modem
        public ArrayList<SettingType> getmModemParamSettingType() {
            return mModemParamSettingType;
        }
        public void setmModemParamSettingType(ArrayList<SettingType> mModemParamSettingType) {
            this.mModemParamSettingType = mModemParamSettingType;
        }
        public ArrayList<SettingType> getmModemRfBasicSettingType() {
            return mModemRfBasicSettingType;
        }
        public void setmModemRfBasicSettingType(ArrayList<SettingType> mModemRfBasicSettingType) {
            this.mModemRfBasicSettingType = mModemRfBasicSettingType;
        }
        public ArrayList<SettingType> getmModemPscSettingType() {
            return mModemPscSettingType;
        }
        public void setmModemPscSettingType(ArrayList<SettingType> mModemPscSettingType) {
            this.mModemPscSettingType = mModemPscSettingType;
        }
        public ArrayList<SettingType> getmModemNetworkSettingType() {
            return mModemNetworkSettingType;
        }
        public void setmModemNetworkSettingType(ArrayList<SettingType> mModemNetworkSettingType) {
            this.mModemNetworkSettingType = mModemNetworkSettingType;
        }
        public ArrayList<SettingType> getmModemLoopBackSettingType() {
            return mModemLoopBackSettingType;
        }
        public void setmModemLoopBackSettingType(ArrayList<SettingType> mModemLoopBackSettingType) {
            this.mModemLoopBackSettingType = mModemLoopBackSettingType;
        }
        public ArrayList<SettingType> getmModemEMSSettingType() {
            return mModemEMSSettingType;
        }
        public void setmModemEMSSettingType(ArrayList<SettingType> mModemEMSSettingType) {
            this.mModemEMSSettingType = mModemEMSSettingType;
        }
        public ArrayList<SettingType> getmModemCellInfoSettingType() {
            return mModemCellInfoSettingType;
        }
        public void setmModemCellInfoSettingType(ArrayList<SettingType> mModemCellInfoSettingType) {
            this.mModemCellInfoSettingType = mModemCellInfoSettingType;
        }
        public ArrayList<SettingType> getmModemRemoteSettingType() {
            return mModemRemoteSettingType;
        }
        public void setmModemRemoteSettingType(ArrayList<SettingType> mModemRemoteSettingType) {
            this.mModemRemoteSettingType = mModemRemoteSettingType;
        }
        public ArrayList<SettingType> getmModemSimSettingType() {
            return mModemSimSettingType;
        }
        public void setmModemSimSettingType(ArrayList<SettingType> mModemSimSettingType) {
            this.mModemSimSettingType = mModemSimSettingType;
        }

        // tsync
        public ArrayList<SettingType> getmTsyncConfigureSettingType() {
            return mTsyncConfigureSettingType;
        }
        public void setmTsyncConfigureSettingType(ArrayList<SettingType> mTsyncConfigureSettingType) {
            this.mTsyncConfigureSettingType = mTsyncConfigureSettingType;
        }
    }


    public class SettingType {
        public SettingType(){
        }

        public int getType() {
            return type;
        }
        public void setType(int type) {
            this.type = type;
        }
        public int getMin_value() {
            return min_value;
        }
        public void setMin_value(int min_value) {
            this.min_value = min_value;
        }
        public int getMax_value() {
            return max_value;
        }
        public void setMax_value(int max_value) {
            this.max_value = max_value;
        }

        public int type;
        public int min_value;
        public int max_value;
    }

    public MenuTypeSubTitle getAlarmSubTitle() {
        return AlarmSubTitle;
    }
    public void setAlarmSubTitle(MenuTypeSubTitle alarmSubTitle) {
        AlarmSubTitle = alarmSubTitle;
    }

    public MenuTypeSubTitle getStatusSubTitle() {
        return StatusSubTitle;
    }
    public void setStatusSubTitle(MenuTypeSubTitle statusSubTitle) {
        StatusSubTitle = statusSubTitle;
    }

    public MenuTypeSubTitle getSettingSubTitle() {
        return SettingSubTitle;
    }
    public void setSettingSubTitle(MenuTypeSubTitle settingSubTitle) {
        SettingSubTitle = settingSubTitle;
    }

    public MenuTypeSubTitle getModemSubTitle() {
        return ModemSubTitle;
    }
    public void setModemSubTitle(MenuTypeSubTitle modemSubTitle) {
        ModemSubTitle = modemSubTitle;
    }

    public MenuTypeSubTitle getTsyncSubTitle() {
        return TsyncSubTitle;
    }
    public void setTsyncSubTitle(MenuTypeSubTitle tsyncSubTitle) {
        TsyncSubTitle = tsyncSubTitle;
    }
}
