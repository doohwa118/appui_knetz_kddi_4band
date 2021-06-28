package kr.knetz.qn.app.v.x;

public class BandStruct {
    public BandStruct() {
        this.stats = new Stat();
        this.sets = new Set();

        this.tsync = new Tsync();

        this.pathStats = new PathStat[2];
        for(int i=0; i<this.pathStats.length; i++)
            this.pathStats[i] = new PathStat();

        this.pathSets = new PathSet[2];
        for(int i=0; i<this.pathSets.length; i++)
            this.pathSets[i] = new PathSet();
    }

    public PathStat[] pathStats;
    public PathSet[] pathSets;
    public Stat stats;
    public Set sets;

    public Tsync tsync;

    public class PathStat {
        // [ALARM] Downlink/Uplink DSP&RF ICM
        public byte aDspLinkFail;
        public byte aIcsStatus;
        public byte aIsolationLack;
        public byte aRfDevicePll;
        public byte aInputLower;
        public byte aInputUpper;
        public byte aOutputLower;
        public byte aOutputUpper;
        public byte aSleepStatus;
        // [ALARM] Downlink/Uplink AMP
        public byte aOverTemp;
        public byte aAmpDcFail;
        public byte aVSWR;
        public byte aLoopFail;
        public byte aAmpOnoff;
        public byte aDeviceFail;
        public byte aOverPower;
        public byte aLinkFail;

        // [STATUS] Downlink/Uplink DSP&RF ICM
        public byte sIsolOscGain;
        public short sInputPower;
        public byte sCurrentAtten;
        public byte sCurrentGain;
        public byte sAlcAtten;
        public byte sInputPowerReal;
        public byte sAlcMinAtten;
        public byte sIsolMinGain;
        public short sInputPowerX10;
        // [STATUS] Downlink/Uplink AMP
        public short sOutputPower;
        public byte sAmpMaker;
        public byte sAmpTemp;
        public short sOutputPowerX10;
        public short sOutputPowerX10Sum;
    }

    public class Stat {
//        public Stat() {
//            this.pathStats = new PathStat[2];
//            for(int i=0; i<this.pathStats.length; i++)
//                this.pathStats[i] = new PathStat();
//        }
        // [ALARM] System Info
        public byte aTempHigh;
        public byte aDoorOpen;
        public byte aTsyncLock1;
        public byte aTsyncLock2;
        public byte aModemLink;
        public byte aTsyncLink1;
        public byte aTsyncLink2;
        // [ALARM] PSU
        public byte aAcFail;
        public byte aDcFail;
        public byte aBatterySwOnOff;
        public byte aDcInputFail;
        public byte aUpsStatus;
        public byte aLowBattery;
        public byte aOnBattery;
        public byte aOverCurrent;

        // [STATUS] System Info
        public byte sRepeaterMaker;
        public byte sSupplier;
        public byte sVersion;
        public byte sTemperature;
        public byte sCpuUsage;
        // [STATUS] PSU
        public byte sBatteryType;
        public byte sInputVoltage;
        public byte sOutputVoltage;
        public byte sBatteryVoltage;
        public short sDcCurrent;

//        PathStat[] pathStats;
}

    public class PathSet {
        // [SETTING] Downlink/Uplink DSP&RF ICM
        public byte tAtten;
        public byte tIcsMode;
        public byte tSystemGain;
        public short tOutputUpper;
        public short tOutputLower;
        public short tInputUpper;
        public short tInputLower;
        public byte tAgcOffset; // tBalanceOffset
        public byte tPathSleepMode;
        public byte tPathSleepLevel;
        public int tInputAlcRecoveryTime;
        public int tInputAlcPeriod;
        public byte tInputAlcLevel;
        public byte tIcsOff;
        // [SETTING] Downlink/Uplink AMP
        public byte tAmpOnOff;
        public byte tAlcOnOff;
    }

    public class Set {
//        public Set() {
//            this.pathSets = new PathSet[2];
//            for(int i=0; i<this.pathSets.length; i++)
//                this.pathSets[i] = new PathSet();
//        }
        // [SETTING] System Info
        public byte tTempUpper;
        public byte tAutoShutdown;
        public byte tAutoRecovery;
        public byte tSleepMode;
        public byte tILC;
        public byte tSawBypass;
        public byte tFreqSelectAutoManual;
        public byte tFreqSelect10M15M;
        public byte tCellSearch;
        // [SETTING] Service FA
        public byte[] tDlFA = new byte[3];
        public byte[] tUlFA = new byte[3];
        public byte tFAallocation;
        public byte tBandSelect;
        public byte tRfPathOn;

//        PathSet[] pathSets;
        // [SETTING] Common
        public byte[] tSerialNum = new byte[20];
        public byte[] tModelName = new byte[20];
        public byte[] tOperatorName = new byte[20];
        public byte[] tSupplierName = new byte[20];
        public byte[] tInstallAddr = new byte[100];
        public byte tPowerMode;
        public byte tServiceBand;
    }

    public class Tsync{
        // [Tsync] Alarm
        public byte aTsyncLink;
        public byte aTsyncLock;
        // [Tsync] Info
        public byte iVendor;
        public byte iFpgaVer;
        public byte iFwVer;
        public short iRxPower;
        public short iPciValue;
        public short iRssi;
        public byte iSSBindex;
        public byte iTemp;
        public short iSSBrsrp;
        public short iInputPower;
        // [Tsync] Configure
        public byte tTddMode;
        public int tDlOffTime;
        public int tUlOffTime;
        public int tDlOnTime;
        public int tUlOnTime;
        public byte tDlUlConfigure;
        public byte tTsync1OutSel;
        public byte tTsync2OutSel;
        public byte tTsync3OutSel;
        public byte tTsync4OutSel;
        public int tCenterFreq;
        public byte tBandSel;
        public byte tBandWidth;
        public byte tSSBsearchMode;
    }
}
