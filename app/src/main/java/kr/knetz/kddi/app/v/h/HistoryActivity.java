package kr.knetz.kddi.app.v.h;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

import kr.knetz.kddi.app.R;
import kr.knetz.kddi.app.l.Debug;
import kr.knetz.kddi.app.v.t.SettingsPreference;


public class HistoryActivity extends Activity {

    String path = "";

    public HistoryActivity() {
        super();
    }
    SettingsPreference settingPreference = new SettingsPreference(this);
    String email = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Debug.loge(new Exception(), "=dhjung=======> HistoryActivity onCreate");
        setContentView(R.layout.activity_history);
        ListView lv;
        Debug.loge(new Exception(), Environment.getExternalStorageDirectory().getPath()+"/knetz");
        path = Environment.getExternalStorageDirectory().getPath()+"/knetz";
        ArrayList<String> FilesInFolder = GetFiles(path);
        lv = findViewById(R.id.listview_history);

        lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, FilesInFolder));
        email = SettingsPreference.getValue(SettingsPreference.EMAIL,"");

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Clicking on items
                String tv = (String) parent.getAdapter().getItem(position);

                Intent it = new Intent(Intent.ACTION_SEND);
                it.putExtra(Intent.EXTRA_SUBJECT, tv);

                if (email.length() > 0){
                    String[] mail = {email};
                    it.putExtra(Intent.EXTRA_EMAIL,mail);
                }
                Uri uri = Uri.parse("file://" + path + "/" + tv);
                it.putExtra(Intent.EXTRA_STREAM, uri);
                it.setType("text/plain");
                startActivity(Intent.createChooser(it, "Choose Email Client"));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Debug.loge(new Exception(), "=dhjung=======> HistoryActivity onResume");
        email = SettingsPreference.getValue(SettingsPreference.EMAIL,"");
    }


    public ArrayList<String> GetFiles(String DirectoryPath) {
        ArrayList<String> MyFiles = new ArrayList<String>();
        File f = new File(DirectoryPath);

        f.mkdir();
        File[] files = f.listFiles();
        if (files.length == 0)
            return null;
        else {
            for (int i=0; i<files.length; i++)
                MyFiles.add(files[i].getName());
        }
        return MyFiles;
    }

}
