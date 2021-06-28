package kr.knetz.qn.app.v.x;


public class Enums {
    public enum eMenuType {
        ITEM_1(1),
        ITEM_2(2),
        ITEM_3(3),
        ITEM_4(4),
        ITEM_5(5),
        ITEM_6(6),
        ITEM_7(7),
        ITEM_8(8);
        private int value;

        eMenuType(int value) {
            this.value = value;
        }

        public static eMenuType fromInteger(int x) {
            switch(x) {
                case 1: return ITEM_1;
                case 2: return ITEM_2;
                case 3: return ITEM_3;
                case 4: return ITEM_4;
                case 5: return ITEM_5;
                case 6: return ITEM_6;
                case 7: return ITEM_7;
                case 8: return ITEM_8;
            }
            return null;
        }
    }

    public enum eTapType {
        ALARM(1),
        STATUS(2),
        SETTING(3),
        MODEM(4),
        TSYNC(5);
        private int value;

        eTapType(int value) {
            this.value = value;
        }

        public static eTapType fromInteger(int x) {
            switch(x) {
                case 1: return ALARM;
                case 2: return STATUS;
                case 3: return SETTING;
                case 4: return MODEM;
                case 5: return TSYNC;
            }
            return null;
        }

    }
}
