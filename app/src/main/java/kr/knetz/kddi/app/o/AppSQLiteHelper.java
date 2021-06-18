package kr.knetz.kddi.app.o;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import kr.knetz.kddi.app.l.Debug;

public class AppSQLiteHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "knetz_kddi_qn.file";
    Context mContext;

    private static final int DATABASE_VERSION = 1;
    private static final String SP_KEY_DB_VER = "db_ver";

    public static final String TABLE_R_PRODUCT = "r_product";
    public static final String TABLE_R_MENU_NAME = "r_menu_name";

    public static final String PRODUCT_COLUMN_ID = "_id";
    public static final String PRODUCT_COLUMN_R_PRODUCT_NAME = "r_product_name";
    public static final String PRODUCT_COLUMN_R_CODE = "r_code";
    public static final String PRODUCT_COLUMN_R_TYPE = "r_type";
    public static final String PRODUCT_COLUMN_R_BAND = "r_band";

    public static final String MENUNAME_COLUMN_R_TYPE = "r_type";
    public static final String MENUNAME_COLUMN_TAP_TYPE = "tap_type";
    public static final String MENUNAME_COLUMN_MENU_TYPE = "menu_type";
    public static final String MENUNAME_COLUMN_MENU_SEQ = "menu_seq";
    public static final String MENUNAME_COLUMN_FIELD_NAME = "field_name";

//    public static String[] PRODUCT_COLUMNS = {
//            AppSQLiteHelper.PRODUCT_COLUMN_ID,
//            AppSQLiteHelper.PRODUCT_COLUMN_R_PRODUCT_NAME,
//            AppSQLiteHelper.PRODUCT_COLUMN_R_CODE,
//            AppSQLiteHelper.PRODUCT_COLUMN_R_TYPE,
//            AppSQLiteHelper.PRODUCT_COLUMN_R_BAND
//    };
//
//    public static String[] MENUNAME_COLUMNS = {
//            AppSQLiteHelper.MENUNAME_COLUMN_R_TYPE,
//            AppSQLiteHelper.MENUNAME_COLUMN_TAP_TYPE,
//            AppSQLiteHelper.MENUNAME_COLUMN_MENU_TYPE,
//            AppSQLiteHelper.MENUNAME_COLUMN_MENU_SEQ,
//            AppSQLiteHelper.MENUNAME_COLUMN_FIELD_NAME
//    };

    public AppSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        mContext = context;
        Log.e("BJH","AppSQLiteHelper DB_NAME : "+DB_NAME);
        Log.e("BJH","AppSQLiteHelper DB_NAME : "+DB_NAME);
        Log.e("BJH","AppSQLiteHelper DB_NAME : "+DB_NAME);
        Log.e("BJH","AppSQLiteHelper DB_NAME : "+DB_NAME);
        Log.e("BJH","AppSQLiteHelper DB_NAME : "+DB_NAME);
        Log.e("BJH","AppSQLiteHelper DB_NAME : "+DB_NAME);
    }

    public AppSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public AppSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE " +TABLE_R_PRODUCT+ " (" +
//                PRODUCT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                PRODUCT_COLUMN_R_PRODUCT_NAME + " CHAR(20) NOT NULL," +
//                PRODUCT_COLUMN_R_CODE +" INTEGER NOT NULL, " +
//                PRODUCT_COLUMN_R_TYPE+" INTEGER,"
//                );
//
//        db.execSQL("CREATE TABLE " +TABLE_R_MENU_NAME+ " (" +
//                MENUNAME_COLUMN_R_TYPE + " INTEGER NOT NULL, " +
//                MENUNAME_COLUMN_TAP_TYPE+ " INTEGER NOT NULL," +
//                MENUNAME_COLUMN_MENU_TYPE+" INTEGER NOT NULL," +
//                MENUNAME_COLUMN_MENU_SEQ+" INTEGER NOT NULL," +
//                MENUNAME_COLUMN_FIELD_NAME+" CHAR(20)," +
//                "PRIMARY KEY (" + MENUNAME_COLUMN_R_TYPE + ","+MENUNAME_COLUMN_TAP_TYPE+","+MENUNAME_COLUMN_MENU_TYPE+","+MENUNAME_COLUMN_MENU_SEQ+")"
//                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void initialize() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        int dbVersion = prefs.getInt(SP_KEY_DB_VER, 1);
        Debug.loge(new Exception(),"11 dbversion : "+dbVersion+" DATABASE_VERSION : "+DATABASE_VERSION);
        Debug.loge(new Exception(),"isCheckDB(mContext) : " +isCheckDB(mContext));

        if (isCheckDB(mContext)) {
//            int dbVersion = prefs.getInt(SP_KEY_DB_VER, 1);
            Debug.loge(new Exception(),"22 dbversion : "+dbVersion+" DATABASE_VERSION : "+DATABASE_VERSION);

            if (DATABASE_VERSION != dbVersion) {
                File dbFile = mContext.getDatabasePath(DB_NAME);
                if (!dbFile.delete()) {
                    Debug.logw(new Exception(), "Unable to update database");
                }
            }
        }
        if (!isCheckDB(mContext)) {
            createDatabase();
        }
    }

    public boolean isCheckDB(Context mContext) {
        File dbFile = mContext.getDatabasePath(DB_NAME);
        return dbFile.exists();
    }

//    public void copyDB(Context mContext) {
//        Debug.logd(new Exception(), "copyDB");
//        AssetManager manager = mContext.getAssets();
//
//        File folder = mContext.getDatabasePath(AppSQLiteHelper.DB_NAME).getParentFile();
//        File file = mContext.getDatabasePath(AppSQLiteHelper.DB_NAME);
//        FileOutputStream fos = null;
//        BufferedOutputStream bos = null;
//        try {
//            InputStream is = manager.open("database/" + AppSQLiteHelper.DB_NAME);
//            BufferedInputStream bis = new BufferedInputStream(is);
//
//            if (folder.exists()) {
//            } else {
//                folder.mkdirs();
//            }
//
//            if (file.exists()) {
//                file.delete();
//                file.createNewFile();
//            }
//
//            fos = new FileOutputStream(file);
//            bos = new BufferedOutputStream(fos);
//            int read = -1;
//            byte[] buffer = new byte[1024];
//            while ((read = bis.read(buffer, 0, 1024)) != -1) {
//                bos.write(buffer, 0, read);
//            }
//
//            bos.flush();
//
//            bos.close();
//            fos.close();
//            bis.close();
//            is.close();
//
//        } catch (IOException e) {
//            Debug.loge(new Exception(), e.getMessage());
//        }

    private void createDatabase() {
        String parentPath = mContext.getDatabasePath(DB_NAME).getParent();
        String path = mContext.getDatabasePath(DB_NAME).getPath();

        File file = new File(parentPath);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Debug.logw(new Exception(), "Unable to create database directory");
                return;
            }
        }

        InputStream is = null;
        OutputStream os = null;
        try {
            is = mContext.getAssets().open("database/"+DB_NAME);
            os = new FileOutputStream(path);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(SP_KEY_DB_VER, DATABASE_VERSION);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
