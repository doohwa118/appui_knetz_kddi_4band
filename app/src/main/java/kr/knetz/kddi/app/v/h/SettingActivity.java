package kr.knetz.kddi.app.v.h;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.app.ActivityCompat;

import kr.knetz.kddi.app.R;
import kr.knetz.kddi.app.v.t.SettingsPreference;



public class SettingActivity extends PreferenceActivity {
    SettingsPreference settingPreference = new SettingsPreference(this);

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
//        Debug.logi(new Exception(),"=dhjung=======> SettingActivity onUserLeaveHint");

//            finish();
//            finishAndRemoveTask();
  //       android.os.Process.killProcess(android.os.Process.myPid());
        ActivityCompat.finishAffinity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Debug.logi(new Exception(),"=dhjung=======> SettingActivity onCreate");

        final ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setTitle("Configuration");

        addPreferencesFromResource(R.xml.pref_general);
        final EditTextPreference passwordSetting = (EditTextPreference) findPreference("password");
//        passwordSetting.setSummary(this.settingPreference.getValue(SettingsPreference.OLD_PASSWORD,"rfwindow123$"));
        passwordSetting.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SettingsPreference.put(SettingsPreference.OLD_PASSWORD,(String)newValue);
//                preference.setSummary((String)newValue);
                return true;
            }
        });

//        final EditTextPreference emailSetting = (EditTextPreference) findPreference("email");
////        emailSetting.setSummary(SettingsPreference.getValue(SettingsPreference.EMAIL,""));
////        emailSetting.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
////            @Override
////            public boolean onPreferenceChange(Preference preference, Object newValue) {
////                SettingsPreference.put(SettingsPreference.EMAIL,(String)newValue);
////                preference.setSummary((String)newValue);
////                return true;
////            }
////        });

    }
}
