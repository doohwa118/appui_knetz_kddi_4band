package kr.knetz.qn.app.o;

import android.os.Build;
import android.util.Log;

public class DeviceDependency {
	public static boolean shouldUseSecure() {
		if (Build.MANUFACTURER.equals("Xiaomi")) {
			if (Build.MODEL.equals("2013022") && Build.VERSION.RELEASE.equals("4.2.1")) {
				return true;
			}
		}
        return Build.MODEL.equals("Lenovo A820");
    }
	
	public static boolean isMTK() {
        return Build.MODEL.equals("Lenovo A820");
    }
	
	public static boolean shouldUseFixChannel() {
		if (Build.VERSION.RELEASE.startsWith("4.0.")) {
			if (Build.MANUFACTURER.equals("samsung")) {
				return true;
			}
			if (Build.MANUFACTURER.equals("HTC")) {
				return true;
			}
			if (Build.MANUFACTURER.equals("Sony")) {
				return true;
			}			
		}
		if (Build.VERSION.RELEASE.startsWith("4.1.")) {
			if (Build.MANUFACTURER.equals("samsung")) {
				return true;
			}
		}
		if (Build.MANUFACTURER.equals("Xiaomi")) {
            return Build.VERSION.RELEASE.equals("2.3.5");
		}
		return false;
	}

	public static void Print() {
	    String ANDROID         =   android.os.Build.VERSION.RELEASE;       //The current development codename, or the string "REL" if this is a release build.
	    String BOARD           =   android.os.Build.BOARD;                 //The name of the underlying board, like "goldfish".
	    String BOOTLOADER      =   android.os.Build.BOOTLOADER;            //  The system bootloader version number.
	    String BRAND           =   android.os.Build.BRAND;                 //The brand (e.g., carrier) the software is customized for, if any.
	    String CPU_ABI         =   android.os.Build.CPU_ABI;               //The name of the instruction set (CPU type + ABI convention) of native code.
	    String CPU_ABI2        =   android.os.Build.CPU_ABI2;              //  The name of the second instruction set (CPU type + ABI convention) of native code.
	    String DEVICE          =   android.os.Build.DEVICE;                //  The name of the industrial design.
	    String DISPLAY         =   android.os.Build.DISPLAY;               //A build ID string meant for displaying to the user
	    String FINGERPRINT     =   android.os.Build.FINGERPRINT;           //A string that uniquely identifies this build.
	    String HARDWARE        =   android.os.Build.HARDWARE;              //The name of the hardware (from the kernel command line or /proc).
	    String HOST            =   android.os.Build.HOST;
	    String ID              =   android.os.Build.ID;                    //Either a changelist number, or a label like "M4-rc20".
	    String MANUFACTURER    =   android.os.Build.MANUFACTURER;          //The manufacturer of the product/hardware.
	    String MODEL           =   android.os.Build.MODEL;                 //The end-user-visible name for the end product.
	    String PRODUCT         =   android.os.Build.PRODUCT;               //The name of the overall product.
	    String TAGS            =   android.os.Build.TAGS;                  //Comma-separated tags describing the build, like "unsigned,debug".
	    String TYPE            =   android.os.Build.TYPE;                  //The type of build, like "user" or "eng".
	    String USER            =   android.os.Build.USER;                  //
	    Log.i("Device Information", "ANDROID = " + ANDROID + " BOARD = " + BOARD + " BOOTLOADER = " + BOOTLOADER +
                " BRAND = " + BRAND + " CPU_ABI = " + CPU_ABI + " CPU_ABI2 = " + CPU_ABI2 + " DEVICE = " + DEVICE +
                " DISPLAY = " + DISPLAY + " FINGERPRINT = " + FINGERPRINT + " HARDWARE = " + HARDWARE + " HOST = " + HOST +
                " ID = " + ID + " MANUFACTURER = " + MANUFACTURER + " MODEL = " + MODEL + " PRODUCT = " + PRODUCT +
                " TAGS = " + TAGS + " TYPE = " + TYPE + " USER = " + USER);
	}
}
