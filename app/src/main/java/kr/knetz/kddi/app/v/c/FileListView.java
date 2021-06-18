package kr.knetz.kddi.app.v.c;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import kr.knetz.kddi.app.v.t.OnFileSelectedListener;
import kr.knetz.kddi.app.v.t.OnPathChangedListener;


public class FileListView extends ListView {

    private Context mContext;
    private ArrayList<String> mList = new ArrayList<String>();
    private ArrayList<String> mFolderList = new ArrayList<String>();
    private ArrayList<String> mFileList = new ArrayList<String>();
    private ArrayAdapter<String> mAdapter = null;
    // Property
    private String mPath = "";
    //
    static File[] files;
    static File file;
    // Event
    private OnPathChangedListener mOnPathChangedListener = null;
    private OnFileSelectedListener mOnFileSelectedListener = null;


    public FileListView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(context);
    }

    public FileListView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public FileListView(Context context){
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setOnItemClickListener(mOnItemClick);
    }


    private boolean openPath(String path){
        mFolderList.clear();
        mFileList.clear();

        file = new File(path);
        files = file.listFiles();
        if(files == null) return false;

        for(int i=0; i<files.length; i++){
            if(files[i].isDirectory()){
                mFolderList.add("<" + files[i].getName() + ">");
            }else{
                mFileList.add(files[i].getName());
            }
        }

        Collections.sort(mFolderList);
        Collections.sort(mFileList);

        if(!path.equals("/")) mFolderList.add(0, "<Back>");

        return true;
    }

    private void updateAdapter(){
        mList.clear();
        mList.addAll(mFolderList);
        mList.addAll(mFileList);

        mAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, mList);
        setAdapter(mAdapter);
    }

    public void setPath(String value){
        if(value.length() == 0){
            value = "/";
        }else{
            String lastChar = value.substring(value.length()-1);
            if(lastChar.matches("/") == false) value = value + "/";
        }

        if(openPath(value)){
            mPath = value;
            updateAdapter();
            if(mOnPathChangedListener != null) mOnPathChangedListener.onChanged(value);
        }
    }

    public String getPath(){
        return mPath;
    }



    public void setOnPathChangedListener(OnPathChangedListener value){
        mOnPathChangedListener = value;
    }

    public OnPathChangedListener getOnPathChangedListener(){
        return mOnPathChangedListener;
    }

    public void setOnFileSelected(OnFileSelectedListener value){
        mOnFileSelectedListener = value;
    }

    public OnFileSelectedListener getOnFileSelected(){
        return mOnFileSelectedListener;
    }



    public String DeleteRight(String value, String border){
        String[] list = value.split(border);
        String result = "";

        for(int i=0; i<list.length; i++){
            result = result + list[i] + border;
        }

        return result;
    }

    private String deleteLastFolder(String value){
        String[] list = value.split("/");
        String result = "";

        for(int i=0; i<list.length-1; i++){
            result = result + list[i] + "/";
        }

        return result;
    }

    private String getRealPathName(String newPath, int position) {
        String path = newPath.substring(1, newPath.length() - 1);
        if (path.matches("Back") && position == 0) {
            return deleteLastFolder(mPath);
        } else {
            return mPath + path + "/";
        }
    }

    private AdapterView.OnItemClickListener mOnItemClick = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            String fileName = getItemAtPosition(position).toString();
            if(fileName.matches("<.*>")){
                setPath(getRealPathName(fileName, position));
            }else{
                if(mOnFileSelectedListener != null) mOnFileSelectedListener.onSelected(mPath, fileName);
            }
        }
    };
}
