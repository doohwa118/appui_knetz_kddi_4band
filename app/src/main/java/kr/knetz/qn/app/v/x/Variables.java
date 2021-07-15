package kr.knetz.qn.app.v.x;


public class Variables {

    public static final String DEFAULT_PASSWORD = "repqn";
    public static final boolean DEFAULT_PASSWORD_REMEMBER_FLAG = false;

    public static boolean USB_OR_BLUETOOTH_FLAG = true; // true : usb / false : bluetooth
    public static boolean ALLINONE_FLAG = true;

    public static boolean HIDDEN_ENABLED = true;
    public static int HIDDEN_ENABLED_COUNT = 0;

    public static float TEXT_SIZE = 15.0f;

    public static int dataSendCnt = 0;

    public static int band = 0;

//    public static BandStruct bandStruct = new BandStruct();
    public static BandStruct[] bandStruct;
    public static ModemStruct modemStruct;

    public static boolean DEMO_ENABLED = false;

    public static boolean ACTIVITY_LOGIN_FLAG = false;
    public static boolean BACKKEY_PRESSED_FLAG = false;

    public static final String ACTION_USB_PERMISSION = "kr.knetz.qn.app.usb.USB_PERMISSION";
    public static final String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    public static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String SERVICE_NAME = "kr.knetz.qn.app.v.h.DeviceService";
    // ActionBar Display Icon Choice!!
    public static final int DISPLAY_ACTIONBAR_HISTORY = 0x1;
    public static final int DISPLAY_ACTIONBAR_SAVE = 0x2;
    public static final int DISPLAY_ACTIONBAR_DOWNLOAD = 0x4;
    public static final int DISPLAY_ACTIONBAR_CONFIGURATION = 0x8;


    // Menu Type Define!!
    // 장비마다 표시하는 항목을 달리하기 위해 별도 변수로 처리 함
    // int system, psu, dlDspRf, ulDspRf, dlAmp, ulAmp, serviceFa, common;
    public static final int ARRAYLIST_MENUTYPE_SYSTEMINFORMATION = 0;
    public static final int ARRAYLIST_MENUTYPE_PSU = 1;
    public static final int ARRAYLIST_MENUTYPE_DOWNLINK_DSPRF = 2;
    public static final int ARRAYLIST_MENUTYPE_UPLINK_DSPRF = 3;
    public static final int ARRAYLIST_MENUTYPE_DOWNLINK_AMP = 4;
    public static final int ARRAYLIST_MENUTYPE_UPLINK_AMP = 5;
    public static final int ARRAYLIST_MENUTYPE_SERVICE_FA = 6;
    public static final int ARRAYLIST_MENUTYPE_COMMON = 7;

    // modem (kddi)
    public static final int ARRAYLIST_MENUTYPE_QN_MODEM_PARAM = 0;
    public static final int ARRAYLIST_MENUTYPE_QN_CELL_INFO = 1;
    public static final int ARRAYLIST_MENUTYPE_QN_REMOTE = 2;
    public static final int ARRAYLIST_MENUTYPE_QN_SIM = 3;
    public static final int ARRAYLIST_MENUTYPE_QN_NETWORK = 4;
    public static final int ARRAYLIST_MENUTYPE_QN_EMS = 5;
    // tsync
    public static final int ARRAYLIST_MENUTYPE_TSYNC_ALARM = 0; // Alarm
    public static final int ARRAYLIST_MENUTYPE_TSYNC_INFO = 1;  // Status
    public static final int ARRAYLIST_MENUTYPE_TSYNC_CONFIGURE = 2; // Setting


    // Handler 화면 Refresh 주기
    public static long REFRESH_TIMEOUT_MILLIS = 1500L;


    public static int SYSTEM_R_TYPE;
    public static final int SYSTEM_R_TYPE_KNETZ = 1;
    public static final int SYSTEM_R_TYPE_SUB6_5G = 2;
    public static final int SYSTEM_R_TYPE_3BAND = 3;
    public static final int SYSTEM_R_TYPE_KDDI_QN = 4;

    public static int SYSTEM_MODEM_RECEIVER;
    public static final int SYSTEM_MODEM = 1;
    public static final int SYSTEM_RECEIVER = 2;

    public static int SYSTEM_TSYNC_MAKER;
    public static final int TSYNC_MAKER_JSC = 1;
    public static final int TSYNC_MAKER_CROSSWORKS = 2;


    // Service to MainActivity Msg
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_REGISTER_CLIENT_CONNECTACTIVITY = 3;
    public static final int MSG_REGISTER_CLIENT_LOGINACTIVITY = 4;
    public static final int MSG_REGISTER_CLIENT_FILELISTACTIVITY = 5;

    public static final int MSG_SERVICE_TO_MAINACTIVITY_DATA = 100;
    public static final int MSG_SERVICE_TO_MAINACTIVITY_STOP = 101;
    public static final int MSG_SERVICE_TO_MAINACTIVITY_FIRMWARE_DOWNLOAD = 102;
    public static final int MSG_SERVICE_TO_MAINACTIVITY_FIRMWARE_DOWNLOAD_FAIL = 103;
    public static final int MSG_SERVICE_TO_MAINACTIVITY_INIT_DATA = 104;

    public static final int MSG_MAINACTIVITY_TO_SERVICE_SETTING = 200;
    public static final int MSG_MAINACTIVITY_TO_SERVICE_SETTING_STOP = 201;
    public static final int MSG_MAINACTIVITY_TO_SERVICE_RECEIVER_SETTING = 202;
    public static final int MSG_MAINACTIVITY_TO_SERVICE_RECEIVER_SETTING_STOP = 203;
    public static final int MSG_MAINACTIVITY_TO_SERVICE_DSP_CTRL = 204;
    public static final int MSG_MAINACTIVITY_TO_SERVICE_DSP_CTRL_STOP = 205;
    public static final int MSG_MAINACTIVITY_TO_SERVICE_MODEM_SETTING = 206;
    public static final int MSG_MAINACTIVITY_TO_SERVICE_MODEM_SETTING_STOP = 207;
    public static final int MSG_MAINACTIVITY_TO_SERVICE_TSYNC_SETTING = 208;
    public static final int MSG_MAINACTIVITY_TO_SERVICE_TSYNC_SETTING_STOP = 209;

    public static final int MSG_FILELISTACTIVITY_TO_SERVICE_PATH = 300;
    public static final int MSG_FILELISTACTIVITY_TO_SERVICE_FINISH = 301;

    public static final int MSG_SERVICE_TO_CONNECTACTIVITY_DATA = 400;
    public static final int MSG_SERVICE_TO_CONNECTACTIVITY_CABLE_NOT_CONNECT = 401;

    public static final int MSG_SERVICE_TO_LOGINACTIVITY_DATA = 500;
    public static final int MSG_SERVICE_TO_LOGINACTIVITY_FINISH = 501;
    public static final int MSG_SERVICE_TO_LOGINACTIVITY_CABLE_NOT_CONNECT = 502;
    public static final int MSG_SERVICE_TO_LOGINACTIVITY_BLUETOOTH_CHECK = 503;

    public static final int MSG_LOGINACTIVITY_TO_SERVICE_START = 550;
    public static final int MSG_LOGINACTIVITY_TO_SERVICE_DATA = 551;
    public static final int MSG_LOGINACTIVITY_TO_SERVICE_BLUETOOTH_INIT = 552;
    public static final int MSG_LOGINACTIVITY_TO_SERVICE_FINISH= 553;

    // Setting 타입
    public static final int DIALOG_TYPE_ONOFF = 1;
    public static final int DIALOG_TYPE_SEEKBAR = 2;
    public static final int DIALOG_TYPE_SPINNER = 3;
    public static final int DIALOG_TYPE_SEEKBAR_DSP = 4;
    public static final int DIALOG_TYPE_BUTTON = 5;
    public static final int DIALOG_TYPE_INPUT = 6;


    public static final int REQUEST_CODE_LOGIN = 100;
    public static final int RESULT_CODE_LOGIN_FAIL = 0;
    public static final int RESULT_CODE_LOGIN_OK = 1;


    public static final byte[][] ARRAY_HEARTBEATTIME_SETTING = {
            {0x00, (byte) 0x05, 0x00}, // 5min
            {0x00, (byte) 0x10, 0x00}, // 10min
            {0x00, (byte) 0x20, 0x00}, // 20min
            {0x00, (byte) 0x30, 0x00}, // 30min
            {(byte) 0x01, 0x00, 0x00}, // 1Hour
            {(byte) 0x02, 0x00, 0x00}, // 2Hour
            {(byte) 0x04, 0x00, 0x00}, // 4Hour
            {(byte) 0x06, 0x00, 0x00}, // 6Hour
            {(byte) 0x12, 0x00, 0x00}, // 12Hour
            {(byte) 0x24, 0x00, 0x00}, // 1 day
            {(byte) 0x48, 0x00, 0x00}, // 2 day
            {(byte) 0x96, 0x00, 0x00}, // 4 day
    };

    // Hidden 메뉴 장비마다 상이함
    // TAB2 STATUS
    public static final int[] TAB2_PSU_HIDDEN_MENU_ID = {
            Variables.DB_STATUS_DC_CURRENT
    };
    public static final int[] TAB2_DOWNLINK_HIDDEN_MENU_ID = {
            Variables.DB_STATUS_CURRENT_ATTEN,
            Variables.DB_STATUS_ALC_ATTEN,
            Variables.DB_STATUS_ALC_MIN_ATTEN,
            Variables.DB_STATUS_ISOL_MIN_GAIN,
            Variables.DB_STATUS_CURRENT_ATTEN_CDMA,
            Variables.DB_STATUS_ALC_ATTEN_CDMA,
            Variables.DB_STATUS_ALC_MIN_ATTEN_CDMA,
            Variables.DB_STATUS_ISOL_MIN_GAIN_CDMA
    };
    public static final int[] TAB2_DOWNLINK_CDMA_HIDDEN_MENU_ID = {
            Variables.DB_STATUS_ISOL_OSC_GAIN_CDMA,
            Variables.DB_STATUS_INPUT_POWER_CDMA,
            Variables.DB_STATUS_CURRENT_ATTEN_CDMA,
            Variables.DB_STATUS_CURRENT_GAIN_CDMA,
            Variables.DB_STATUS_ALC_ATTEN_CDMA,
            Variables.DB_STATUS_ALC_MIN_ATTEN_CDMA,
            Variables.DB_STATUS_ISOL_MIN_GAIN_CDMA
    };
    public static final int[] TAB2_DOWNLINK_AMP_CDMA_HIDDEN_MENU_ID = {
            Variables.DB_STATUS_OUTPUT_POWER_CDMA,
            Variables.DB_STATUS_OUTPUT_POWER_X10SUM
    };
    public static final int[] TAB2_UPLINK_HIDDEN_MENU_ID = {
            Variables.DB_STATUS_CURRENT_ATTEN,
            Variables.DB_STATUS_ALC_ATTEN,
            Variables.DB_STATUS_ALC_MIN_ATTEN,
            Variables.DB_STATUS_ISOL_MIN_GAIN,
            Variables.DB_STATUS_CURRENT_ATTEN_CDMA,
            Variables.DB_STATUS_ALC_ATTEN_CDMA,
            Variables.DB_STATUS_ALC_MIN_ATTEN_CDMA,
            Variables.DB_STATUS_ISOL_MIN_GAIN_CDMA
    };
    public static final int[] TAB2_UPLINK_CDMA_HIDDEN_MENU_ID = {
            Variables.DB_STATUS_ISOL_OSC_GAIN_CDMA,
            Variables.DB_STATUS_INPUT_POWER_CDMA,
            Variables.DB_STATUS_CURRENT_ATTEN_CDMA,
            Variables.DB_STATUS_CURRENT_GAIN_CDMA,
            Variables.DB_STATUS_ALC_ATTEN_CDMA,
            Variables.DB_STATUS_ALC_MIN_ATTEN_CDMA,
            Variables.DB_STATUS_ISOL_MIN_GAIN_CDMA,
    };
    public static final int[] TAB2_UPLINK_AMP_CDMA_HIDDEN_MENU_ID = {
            Variables.DB_STATUS_OUTPUT_POWER_CDMA,
            Variables.DB_STATUS_OUTPUT_POWER_X10SUM
    };
    // TAB3 SETTING
    public static final int[] TAB3_SYSTEM_CDMA_HIDDEN_MENU_ID = {
            Variables.DB_SETTING_FREQ_SEL_AUTO,
            Variables.DB_SETTING_FREQ_SEL
    };
    public static final int[] TAB3_DOWNLINK_HIDDEN_MENU_ID = {
            Variables.DB_SETTING_ATTEN,
//            Variables.DB_SETTING_ICS_MODE
    };
    public static final int[] TAB3_UPLINK_HIDDEN_MENU_ID = {
            Variables.DB_SETTING_ATTEN
    };
    public static final int[] TAB3_SERVICEFA_CDMA_HIDDEN_MENU_ID = {
            Variables.DB_SETTING_RF_PATH_ON
    };
    public static final int[] TAB3_COMMON_HIDDEN_MENU_ID = {
            Variables.DB_SETTING_SERVICE_BAND
    };
    // TAB4 MODEM
    public static final int[] TAB4_REMOTE_HIDDEN_MENU_ID = {
            Variables.DB_MODEM_LOCAL_PHONE_NUM,
            Variables.DB_MODEM_RCS_PHONE_NUM
    };
    public static final int[] TAB4_SIM_HIDDEN_MENU_ID = {
//            Variables.DB_MODEM_PIN_LOCK
    };
    public static final int[] TAB4_EMS_HIDDEN_MENU_ID = {
            Variables.DB_MODEM_RCS_PORT
    };
    // TAB5 TSYNC (실제론 4번째 탭)
    public static final int[] TAB5_INFO_HIDDEN_MENU_ID = {
            Variables.DB_CRW_TSYNC_RX_POWER,
            Variables.DB_CRW_TSYNC_PCI_VALUE,
            Variables.DB_CRW_TSYNC_RSSI,
            Variables.DB_CRW_TSYNC_INPUT_POWER
    };
    public static final int[] TAB5_CONF_HIDDEN_MENU_ID = {
            Variables.DB_CRW_TSYNC_DLOFF_TIME,
            Variables.DB_CRW_TSYNC_ULOFF_TIME,
            Variables.DB_CRW_TSYNC_DLON_TIME,
            Variables.DB_CRW_TSYNC_ULON_TIME,
            Variables.DB_CRW_TSYNC_TDD_ULDL_CONF,
            Variables.DB_CRW_TSYNC1_OUT_SEL,
            Variables.DB_CRW_TSYNC2_OUT_SEL,
            Variables.DB_CRW_TSYNC3_OUT_SEL,
            Variables.DB_CRW_TSYNC4_OUT_SEL,
            Variables.DB_CRW_TSYNC_CENTER_FREQ,
            Variables.DB_CRW_TSYNC_SSB_SEARCH_MODE
    };


    // DB Item ID
    // [ALARM] System Info
    public static final int DB_ALARM_TEMP_HIGH = 1;
    public static final int DB_ALARM_DOOR_OPEN = 2;
    public static final int DB_ALARM_TSYNC_LOCK1 = 3;
    public static final int DB_ALARM_TSYNC_LOCK2 = 4;
    public static final int DB_ALARM_MODEM_LINK = 5;
    public static final int DB_ALARM_TSYNC_LINK1 = 6;
    public static final int DB_ALARM_TSYNC_LINK2 = 7;
    // [ALARM] PSU
    public static final int DB_ALARM_AC_FAIL = 1;
    public static final int DB_ALARM_DC_FAIL = 2;
    public static final int DB_ALARM_BATTERY_ONOFF = 3;
    public static final int DB_ALARM_DC_INPUT_FAIL = 4;
    public static final int DB_ALARM_UPS_STATUS = 5;
    public static final int DB_ALARM_LOW_BATTERY = 6;
    public static final int DB_ALARM_ON_BATTERY = 7;
    public static final int DB_ALARM_OVER_CURRENT = 8;
    // [ALARM] Downlink/Uplink DSP&RF ICM
    public static final int DB_ALARM_DSP_LINK_FAIL = 1;
    public static final int DB_ALARM_ICS_STATUS = 2;
    public static final int DB_ALARM_ISOLATION_LACK = 3;
    public static final int DB_ALARM_RF_DEVICE_PLL = 4;
    public static final int DB_ALARM_INPUT_LOWER = 5;
    public static final int DB_ALARM_INPUT_UPPER = 6;
    public static final int DB_ALARM_OUTPUT_LOWER = 7;
    public static final int DB_ALARM_OUTPUT_UPPER = 8;
    public static final int DB_ALARM_SLEEP_STATUS = 9;
    // [ALARM] Downlink/Uplink AMP
    public static final int DB_ALARM_OVER_TEMP = 1;
    public static final int DB_ALARM_AMP_DC_FAIL = 2;
    public static final int DB_ALARM_VSWR = 3;
    public static final int DB_ALARM_LOOP_FAIL = 4;
    public static final int DB_ALARM_AMP_ONOFF = 5;
    public static final int DB_ALARM_DEVICE_FAIL = 6;
    public static final int DB_ALARM_OVER_POWER = 7;
    public static final int DB_ALARM_LINK_FAIL = 8;

    // [STATUS] System Info
    public static final int DB_DEFAULT_RPT_PRODUCT = 1;
    public static final int DB_DEFAULT_RPT_MODEL = 2;
    public static final int DB_DEFAULT_RPT_OPERATOR = 3;
    public static final int DB_DEFAULT_RPT_SUPPLIER = 4;

    public static final int DB_STATUS_RPT_MAKER = 5;
    public static final int DB_STATUS_SUPPLIER = 6;
    public static final int DB_STATUS_VERSION = 7;
    public static final int DB_STATUS_TEMPERATURE = 8;
    public static final int DB_STATUS_CPU_USAGE = 9;
    // [STATUS] PSU
    public static final int DB_STATUS_BATTERY_TYPE = 1;
    public static final int DB_STATUS_INPUT_VOLTAGE = 2;
    public static final int DB_STATUS_OUTPUT_VOLTAGE = 3;
    public static final int DB_STATUS_BATTERY_VOLTAGE = 4;
    public static final int DB_STATUS_DC_CURRENT = 5;
    // [STATUS] Downlink/Uplink DSP&RF ICM
    public static final int DB_STATUS_ISOL_OSC_GAIN = 1;
    public static final int DB_STATUS_INPUT_POWER = 2;
    public static final int DB_STATUS_CURRENT_ATTEN = 3;
    public static final int DB_STATUS_CURRENT_GAIN = 4;
    public static final int DB_STATUS_ALC_ATTEN = 5;
    public static final int DB_STATUS_INPUT_POWER_REAL = 6;
    public static final int DB_STATUS_ALC_MIN_ATTEN = 7;
    public static final int DB_STATUS_ISOL_MIN_GAIN = 8;
    public static final int DB_STATUS_INPUT_POWER_X10 = 9;
    public static final int DB_STATUS_ISOL_OSC_GAIN_CDMA = 10;
    public static final int DB_STATUS_INPUT_POWER_CDMA = 11;
    public static final int DB_STATUS_CURRENT_ATTEN_CDMA = 12;
    public static final int DB_STATUS_CURRENT_GAIN_CDMA = 13;
    public static final int DB_STATUS_ALC_ATTEN_CDMA = 14;
    public static final int DB_STATUS_ALC_MIN_ATTEN_CDMA = 15;
    public static final int DB_STATUS_ISOL_MIN_GAIN_CDMA = 16;
    // [STATUS] Downlink/Uplink AMP
    public static final int DB_STATUS_OUTPUT_POWER = 1;
    public static final int DB_STATUS_AMP_MAKER = 2;
    public static final int DB_STATUS_AMP_TEMP = 3;
    public static final int DB_STATUS_OUTPUT_POWER_X10 = 4;
    public static final int DB_STATUS_OUTPUT_POWER_CDMA = 5;
    public static final int DB_STATUS_OUTPUT_POWER_X10SUM = 6;

    // [SETTING] System Info
    public static final int DB_SETTING_TEMP_UPPER = 1;
    public static final int DB_SETTING_AUTO_SHUTDOWN = 2;
    public static final int DB_SETTING_AUTO_RECOVERY = 3;
    public static final int DB_SETTING_SLEEP_MODE = 4;
    public static final int DB_SETTING_ILC = 5;
    public static final int DB_SETTING_SAW_BYPASS = 6;
    public static final int DB_SETTING_FREQ_SEL_AUTO = 7;
    public static final int DB_SETTING_FREQ_SEL = 8;
    public static final int DB_SETTING_CELL_SEARCH = 9;
    // [SETTING] Downlink/Uplink DSP&RF ICM
    public static final int DB_SETTING_ATTEN = 1;
    public static final int DB_SETTING_ICS_MODE = 2;
    public static final int DB_SETTING_SYSTEM_GAIN = 3;
    public static final int DB_SETTING_OUTPUT_UPPER = 4;
    public static final int DB_SETTING_OUTPUT_LOWER = 5;
    public static final int DB_SETTING_INPUT_UPPER = 6;
    public static final int DB_SETTING_INPUT_LOWER = 7;
    public static final int DB_SETTING_AGC_OFFSET = 8;
    public static final int DB_SETTING_PATH_SLEEP_MODE = 9;
    public static final int DB_SETTING_PATH_SLEEP_LEVEL = 10;
    public static final int DB_SETTING_INPUT_ALC_RECOVERY_TIME = 11;
    public static final int DB_SETTING_INPUT_ALC_PERIOD = 12;
    public static final int DB_SETTING_INPUT_ALC_LEVEL = 13;
    public static final int DB_SETTING_ICS_OFF = 14;
    // [SETTING] Downlink/Uplink AMP
    public static final int DB_SETTING_AMP_ONOFF = 1;
    public static final int DB_SETTING_ALC_ONOFF = 2;
    // [SETTING] SERVICE FA
    public static final int DB_SETTING_DL_FA = 1;
    public static final int DB_SETTING_UL_FA = 2;
    public static final int DB_SETTING_FA_ALLOCATION = 3;
    public static final int DB_SETTING_BAND_SELECT = 4;
    public static final int DB_SETTING_RF_PATH_ON = 5;

    // [SETTING] COMMON
    public static final int DB_SETTING_SERIAL_NUMBER = 1;
    public static final int DB_SETTING_MODEL_NAME = 2;
    public static final int DB_SETTING_OPERATOR_NAME = 3;
    public static final int DB_SETTING_SUPPLIER_NAME = 4;
    public static final int DB_SETTING_INSTALL_ADDR = 5;
    public static final int DB_SETTING_POWER_MODE = 6;
    public static final int DB_SETTING_SERVICE_BAND = 7;


    // [Modem] Param
    public static final int DB_MODEM_LINK = 1;
    public static final int DB_MODEM_MODEL = 2;
    public static final int DB_MODEM_VERSION = 3;
    public static final int DB_MODEM_USIM_ID = 4;
    public static final int DB_MODEM_LOCAL_TIME = 5;
    public static final int DB_MODEM_LOCAL_IP_ADDR = 6;
    public static final int DB_MODEM_USIM_STATUS = 7;
    // [Modem] Cell Info
    public static final int DB_MODEM_REGI = 1;
    public static final int DB_MODEM_PLMN = 2;
    public static final int DB_MODEM_EARFCN = 3;
    public static final int DB_MODEM_BANDWIDTH = 4;
    public static final int DB_MDOEM_PCI = 5;
    public static final int DB_MODEM_RSSI = 6;
    public static final int DB_MODEM_RSRP = 7;
    public static final int DB_MODEM_RSRQ = 8;
    public static final int DB_MODEM_CPICH = 9;
    // [Modem] Remote
    public static final int DB_MODEM_LOCAL_PHONE_NUM = 1;
    public static final int DB_MODEM_RCS_PHONE_NUM = 2;
    public static final int DB_MODEM_PERIODIC_REPORT = 3;
    public static final int DB_MODEM_AUTO_RESET_TIME = 4;
    // [Modem] Sim
    public static final int DB_MODEM_LOOP_BACK= 1;
    public static final int DB_MODEM_PIN_LOCK = 2;
    public static final int DB_MODEM_PIN_LOCK_PASSWORD = 3;
    // [Modem] Network
    public static final int DB_MODEM_APN = 1;
    public static final int DB_MODEM_USER_ID = 2;
    public static final int DB_MODEM_USER_PASSWORD = 3;
    // [Modem] EMS
    public static final int DB_MODEM_RCS_IP_ADDR_1 = 1;
    public static final int DB_MODEM_RCS_IP_ADDR_2 = 2;
    public static final int DB_MODEM_RCS_IP_ADDR_3 = 3;
    public static final int DB_MODEM_RCS_IP_ADDR_4 = 4;
    public static final int DB_MODEM_RCS_IP_ADDR_5 = 5;
    public static final int DB_MODEM_RCS_IP_ADDR_6 = 6;
    public static final int DB_MODEM_RCS_IP_ADDR_7 = 7;
    public static final int DB_MODEM_RCS_IP_ADDR_8 = 8;
    public static final int DB_MODEM_RCS_IP_ADDR_9 = 9;
    public static final int DB_MODEM_RCS_IP_ADDR_10 = 10;
    public static final int DB_MODEM_RCS_PORT = 11;


    // [TSYNC] Alarm
    public static final int DB_CRW_TSYNC_LINK = 1;
    public static final int DB_CRW_TSYNC_LOCK = 2;
    // [TSYNC] Info
    public static final int DB_CRW_TSYNC_VENDOR = 1;
    public static final int DB_CRW_TSYNC_FPGA_VER = 2;
    public static final int DB_CRW_TSYNC_FW_VER = 3;
    public static final int DB_CRW_TSYNC_RX_POWER = 4;
    public static final int DB_CRW_TSYNC_PCI_VALUE = 5;
    public static final int DB_CRW_TSYNC_RSSI = 6;
    public static final int DB_CRW_TSYNC_SSB_INDEX = 7;
    public static final int DB_CRW_TSYNC_TEMP = 8;
    public static final int DB_CRW_TSYNC_SSB_RSRP = 9;
    public static final int DB_CRW_TSYNC_INPUT_POWER = 10;
    // [TSYNC] Configure
    public static final int DB_CRW_TSYNC_TDD_MODE = 1;
    public static final int DB_CRW_TSYNC_DLON_TIME = 2;
    public static final int DB_CRW_TSYNC_DLOFF_TIME = 3;
    public static final int DB_CRW_TSYNC_ULON_TIME = 4;
    public static final int DB_CRW_TSYNC_ULOFF_TIME = 5;
    public static final int DB_CRW_TSYNC_TDD_ULDL_CONF = 6;
    public static final int DB_CRW_TSYNC1_OUT_SEL = 7;
    public static final int DB_CRW_TSYNC2_OUT_SEL = 8;
    public static final int DB_CRW_TSYNC3_OUT_SEL = 9;
    public static final int DB_CRW_TSYNC4_OUT_SEL = 10;
    public static final int DB_CRW_TSYNC_CENTER_FREQ = 11;
    public static final int DB_CRW_TSYNC_BAND_SELECT = 12;
    public static final int DB_CRW_TSYNC_BANDWIDTH = 13;
    public static final int DB_CRW_TSYNC_SSB_SEARCH_MODE = 14;


    public static byte mtFrameInfo;

    public static boolean settingSendFlag;
    public static boolean receiverSendFlag;
    public static boolean modemSendFlag;
    public static boolean tsyncSendFlag;
    public static boolean dspSendFlag;

    public static byte settingFlag;
    public static byte receiverSettingFlag;
    public static byte modemSettingFlag;
    public static byte tsyncSettingFlag;

    // GUI Icon Default Setting
    public static byte[] gSystemName = new byte[20];
    public static byte[] gSerialNum = new byte[10];
    public static byte[] gPartNum = new byte[10];
    public static byte[] gModelName = new byte[20];
    public static byte[] gOperatorName = new byte[20];
    public static byte[] gSupplierName = new byte[20];

    // GUI Protocol
    public static final int DATA_PACKET_SIZE = 512;
    public static final int DATA_BUFFER_SIZE = 1200;

    public static final int Proto_START_PACKET = 0x9E;
    public static final int Proto_END_PACKET = 0x9F;
    public static final int Proto_ESCAPE_MASK = 0x9D;
    public static final int Proto_EXCEPTION_MASK = 0x20;

    // 프로토콜 상 Data Definition 배열 인덱스 값
    public static final int Protocol_LENGTH = 1;
    public static final int Protocol_SOURCE_ID = 3;
    public static final int Protocol_DESTINATION_ID = 4;
    public static final int Protocol_SYSTEM_TYPE = 5;
    public static final int Protocol_COMMAND = 6;
    public static final int Protocol_MESSAGE_TYPE = 7;
    public static final int Protocol_MESSAGE_START = 8;

    // 프로토콜 상 Download Progress의 Response format 배열 인덱스 값
    public static final int Protocol_DL_PRGGRESS_PACKET_NUM_MSB = 8;
    public static final int Protocol_DL_PRGGRESS_PACKET_NUM_LSB = 9;
    public static final int Protocol_DL_PRGGRESS_STATUS = 10;

    public static int PROTO_DEST_ID;
    public static final int Dest_ID_RF = 0xC0;
    public static final int Dest_ID_ICS_B1_800 = 0xC1;
    public static final int Dest_ID_ICS_B2_2100 = 0xC2;
    public static final int Dest_ID_ICS_B3_700 = 0xC3;
    public static final int Dest_ID_ICS_B4_1700 = 0xC4;
    public static final int Dest_ID_ICS_CDMA = 0xC5;

    public static int PROTO_SYSTEM_TYPE;
    public static final int SysType_ICS_CDMA = 0x81;
    public static final int SysType_ICS_WCDMA = 0x82;
    public static final int SysType_ICS_GSM = 0x83;
    public static final int SysType_ICS_LTE = 0x84;
    public static final int SysType_ICS_LTE_TDD = 0x85;
    public static final int SysType_ICS_DUAL = 0x86;
    public static final int SysType_ICS_TRIPLE = 0x87;
    public static final int SysType_ICS_QUAD = 0x88;
    public static final int SysType_ICS_FIFTH = 0x89;

    public static final int SysType_RF_CDMA = 0x91;
    public static final int SysType_RF_WCDMA = 0x92;
    public static final int SysType_RF_GSM = 0x93;
    public static final int SysType_RF_LTE = 0x94;
    public static final int SysType_RF_LTE_TDD = 0x95;
    public static final int SysType_RF_DUAL = 0x96;


    public static final int Type_REPORT = 0x10;
    public static final int Type_SET = 0x20;
    public static final int Type_REQUEST = 0x01;
    public static final int Type_RESPONSE = 0x02;

    public static final int Cmd_STATUS = 0x01;
    public static final int Cmd_SETTING = 0x02;
    public static final int Cmd_STATUS_KDDI_QN = 0x03;
    public static final int Cmd_SETTING_KDDI_QN = 0x04;


    public static final int Cmd_MODEM_SETTING = 0x07;
    public static final int Cmd_MODEM_STATUS = 0x09;
    public static final int Cmd_MODEM_STATUS_KDDI_QN = 0x0A;
    public static final int Cmd_MODEM_SETTING_KDDI_QN = 0x0B;

    public static final int Cmd_RECEIVER_STATUS = 0xA2;
    public static final int Cmd_RECEIVER_SETTING = 0xA3;

    public static final int Cmd_TSYNC_STATUS_JSC = 0xA9;
    public static final int Cmd_TSYNC_CONTROL_JSC = 0xAA;
    public static final int Cmd_TSYNC_STATUS_CROSSWORKS = 0xAB;
    public static final int Cmd_TSYNC_CONTROL_CROSSWORKS = 0xAC;

    public static final int Cmd_DL_START = 0x10;
    public static final int Cmd_DL_PROGRESS = 0x11;
    public static final int Cmd_DL_END = 0x12;

    public static final int Cmd_DSP_CONTROL = 0x52;
    public static final int Cmd_FDSP_SIGNAL = 0x53;
    public static final int Cmd_RDSP_SIGNAL = 0x54;
    public static final int Cmd_DSP_DELAY = 0x56;

    public static final int Cmd_GUI_SETTING = 0xF1;
    public static final int Cmd_FW_VER = 0xF4;
    public static int CmdState = Cmd_GUI_SETTING;

    public static final int Flag_SEND_REQ = 0x01;
    public static final int Flag_SEND_RES = 0x02;
    public static final int Flag_SEND_CHK = 0x03;
    public static int FlagBitCheck = 0;

    public static final int SEND_RETRY_CNT = 3;
    public static int SendRetryCnt = 0;
    public static final int Total_SEND_RETRY_CNT = 3;
    public static int TotalSendRetryCnt = 0;

    public static int Flag_Action_Download = 0;

    // Firmware Download
    public static int Dl_FileSize;
    public static int Dl_OnePacketSize;
    public static int Dl_TotalPacketCnt;
    public static int Dl_ExpPacketNum;
    public static int Dl_LastPacketNum;
    public static int Dl_lastPacketLen;
}
