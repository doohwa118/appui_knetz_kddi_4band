package kr.knetz.kddi.app.v.x;

public class ModemStruct {

    public ModemStruct(){
        this.modem = new Modem();
    }

    public Modem modem;

    public class Modem{
        // [Modem] Param
        public byte aMoLink;
        public byte[] pMoModel = new byte[16];
        public byte[] pMoVer = new byte[16];
        public byte[] pUsimId = new byte[22];
        public byte[] pLocalTime = new byte[6];
        public byte[] pLocalIpAddr = new byte[50];  // ASC 값
        public byte pUsimStatue;
        // [Modem] Cell Info 4개 밴드 표현
        public byte[] cRegi = new byte[4];  // Service Status
        public byte[][] cPlmn = new byte[4][10];    // ASC 값
        public short[] cEarfcn = new short[4];
        public byte[] cBandwidth = new byte[4];
        public short[] cPci = new short[4];
        public byte[] cRssi = new byte[4];
        public byte[] cRsrp = new byte[4];
        public byte[] cRsrq = new byte[4];
        public byte[] cCpich = new byte[4];
        // [Modem] Remote
        public byte[] rLocalPhoneNum = new byte[16];    // ASC 값
        public byte[] rRcsPhoneNum = new byte[16];      // ASC 값
        public byte[] rPeriodicReport = new byte[3];    // 정기보고 시,분,초
        public byte rAutoResetTime;    // 1~254시간
        // [Modem] Sim
        public byte sLoopback;
        public byte sPinLock;
        public byte[] sPinPassword = new byte[4];   // ASC 값
        // [Modem] Network
        public byte[] nApn = new byte[30];      // ASC 값
        public byte[] nUserId = new byte[30];   // ASC 값
        public byte[] nPassword = new byte[20]; // ASC 값
        // [Modem] Ems
        public byte[][] eRcsIpAddr = new byte[10][50];  // ASC 값
        public short eRcsPort;  // 0~65534
    }
}
