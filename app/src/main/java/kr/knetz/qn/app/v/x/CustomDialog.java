package kr.knetz.qn.app.v.x;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import kr.knetz.qn.app.R;
import kr.knetz.qn.app.l.Debug;
import kr.knetz.qn.app.v.c.DataType;


public class CustomDialog extends Dialog
        implements View.OnClickListener,
        View.OnLongClickListener,
        View.OnTouchListener,
        DiscreteSeekBar.OnProgressChangeListener,
        AdapterView.OnItemSelectedListener {

    private Context mContext;
    private ImageView titleIcon;
    private TextView titleText;
    private TextView ContentText;
    private TextView contentSeekBarValue;
//    private SeekBar seekBar;
    private DiscreteSeekBar seekBar;
    private Button btnSeekBarLeft;
    private Button btnSeekBarRight;
    private LinearLayout llBtnSeekBarLeft;
    private LinearLayout llBtnSeekBarRight;
    private Button btnPositive;
    private Button btnNagative;
    private EditText edtContentValue;
    private Spinner spinner;

    private LinearLayout ll_content_view_seek;
    private LinearLayout ll_content_view_other;
    private LinearLayout ll_content_view_value;
    private LinearLayout ll_content_view_time;

    private DataType __dataType;

    int     nDialogType;
    String  strTitleText;
    String  strContentOther;
    String  strContentValue;
    int     nMinValue;
    int     nMaxValue;
    int     nCurrentValue = 0;
    String  sCurrentValue = "";



    private int     nArgValue = 0;

    boolean bIsChecked = false;

    private View.OnClickListener mConfirmClickListener, mCancelClickListener;

    private Handler repeatUpdateHandler = new Handler();
    private boolean mAutoIncrement = false;
    private boolean mAutoDecrement = false;
    private final static long REP_DELAY = 400L;

//    private int __currentValue;

    public int getCurrentValue() {
        return Integer.valueOf(contentSeekBarValue.getText().toString());
    }
    public long getCurrentSpinnerValue() {
        return spinner.getSelectedItemId();
    }

    public String getContentString(){
        return strContentOther;
    }

    public int getnCurrentValue() {
        return nCurrentValue;
    }

    public String getsCurrentValue() {
        return sCurrentValue;
    }

    public int getnArgValue() {
return nArgValue;
}

    public DataType getDataType() {
        return __dataType;
    }

    public void setDataType(DataType __dataType) {
        this.__dataType = __dataType;
    }

    public int getnDialogType() {
        return nDialogType;
    }


    public interface DialogReturnValueListener {
        void onFinishInputDialog(DataType dataType);
    }

    public String getEditTextValue(){
        if (edtContentValue != null)
            return edtContentValue.getText().toString();
        else
            return "";
    }

    public CustomDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }
    // seekbar
    public CustomDialog(Context context,int type, String title, String content,
                        int min, int max, int currentValue) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = context;
        this.nDialogType = type;
        this.strTitleText = title;
        if (this.nDialogType != Variables.DIALOG_TYPE_SEEKBAR) {
            this.strContentOther = content;
        }else {
            this.nMinValue = min;
            this.nMaxValue = max;
            this.nCurrentValue = currentValue;
            this.nArgValue = currentValue;
        }

    }
    public CustomDialog(Context context,int type, String title, String content,
                        int min, int max, int currentValue,
                        View.OnClickListener mConfirmClickListener,
                        View.OnClickListener mCancelClickListener)
    {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = context;
        this.nDialogType = type;
        this.strTitleText = title;
        if (this.nDialogType != Variables.DIALOG_TYPE_SEEKBAR) {
            this.strContentOther = content;
        }else {
            this.nMinValue = min;
            this.nMaxValue = max;
            this.nCurrentValue = currentValue;
            this.nArgValue = currentValue;
        }
        this.mConfirmClickListener = mConfirmClickListener;
        this.mCancelClickListener = mCancelClickListener;
    }

    // switch
    public CustomDialog(Context context,int type, String title, String content,
                        boolean isChecked) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = context;
        this.nDialogType = type;
        this.strTitleText = title;
        this.strContentOther = content;
        this.bIsChecked = isChecked;

    }

    public CustomDialog(Context context,int type, String title, String content,
                        boolean isChecked,
                        View.OnClickListener mConfirmClickListener,
                        View.OnClickListener mCancelClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = context;
        this.nDialogType = type;
        this.strTitleText = title;
        this.strContentOther = content;
        this.bIsChecked = isChecked;
        this.mConfirmClickListener = mConfirmClickListener;
        this.mCancelClickListener = mCancelClickListener;
    }

                        //spinner
    public CustomDialog(Context context,int type, String title, String content,
                        int nCurrentValue) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = context;
        this.nDialogType = type;
        this.strTitleText = title;
        this.strContentOther = content;
        this.nCurrentValue = nCurrentValue;
//        btnPositive = (Button) findViewById(R.id.btn_dialog_confirm);
//        btnNagative = (Button) findViewById(R.id.btn_dialog_cancel);
    }
    public CustomDialog(Context context,int type, String title, String content,
                        int nCurrentValue, View.OnClickListener mConfirmClickListener, View.OnClickListener mCancelClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = context;
        this.nDialogType = type;
        this.strTitleText = title;
        this.strContentOther = content;
        this.nCurrentValue = nCurrentValue;
        this.mConfirmClickListener = mConfirmClickListener;
        this.mCancelClickListener = mCancelClickListener;
    }

    public CustomDialog(Context context,int type, String title, String content,
                        String sCurrentValue, View.OnClickListener mConfirmClickListener, View.OnClickListener mCancelClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = context;
        this.nDialogType = type;
        this.strTitleText = title;
        this.strContentOther = content;
        this.sCurrentValue = sCurrentValue;
        this.mConfirmClickListener = mConfirmClickListener;
        this.mCancelClickListener = mCancelClickListener;
    }


    public CustomDialog(Context context,int type, String title, String value,
        View.OnClickListener mConfirmClickListener, View.OnClickListener mCancelClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = context;
        this.nDialogType = type;
        this.strTitleText = title;
        this.strContentValue = value;
        this.mConfirmClickListener = mConfirmClickListener;
        this.mCancelClickListener = mCancelClickListener;
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    protected CustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    class RptUpdater implements Runnable {
        @Override
        public void run() {
            if (mAutoIncrement){
                increment();
                repeatUpdateHandler.postDelayed(new RptUpdater(), REP_DELAY);
            }else if (mAutoDecrement){
                decrement();
                repeatUpdateHandler.postDelayed(new RptUpdater(), REP_DELAY);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Debug.logi(new Exception(),"=dhjung=======> CustomDialog onCreate");

        setContentView(R.layout.dialog_setting);


        // background Dimming
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        titleText = findViewById(R.id.tv_dialog_title);
        titleText.setText(strTitleText);
//        Debug.loge(new Exception(),"nDialogType : "+nDialogType);
        if (nDialogType == Variables.DIALOG_TYPE_SEEKBAR){
            ll_content_view_seek = findViewById(R.id.ll_dialog_content_seek);
            ll_content_view_seek.setVisibility(View.VISIBLE);
            contentSeekBarValue = findViewById(R.id.tv_dialog_value);

            seekBar = findViewById(R.id.seekbar_dialog_value);
            seekBar.setMin(nMinValue);
            seekBar.setMax(nMaxValue);

            if (nCurrentValue != -1000){
                seekBar.setProgress(nCurrentValue);
                contentSeekBarValue.setText(String.valueOf(nCurrentValue));
            }else {
                seekBar.setProgress(nMinValue);
                contentSeekBarValue.setText(String.valueOf(nMinValue));
            }

            seekBar.setOnProgressChangeListener(this);

            llBtnSeekBarLeft = findViewById(R.id.ll_dialog_left);
            llBtnSeekBarRight = findViewById(R.id.ll_dialog_right);

            btnSeekBarLeft = findViewById(R.id.btn_dialog_arrow_left);
            btnSeekBarRight = findViewById(R.id.btn_dialog_arrow_right);
            btnSeekBarLeft.setOnClickListener(this);
            btnSeekBarRight.setOnClickListener(this);
            llBtnSeekBarLeft.setOnClickListener(this);
            llBtnSeekBarRight.setOnClickListener(this);

            btnSeekBarLeft.setOnLongClickListener(this);
            btnSeekBarRight.setOnLongClickListener(this);
            llBtnSeekBarLeft.setOnLongClickListener(this);
            llBtnSeekBarRight.setOnLongClickListener(this);

            btnSeekBarLeft.setOnTouchListener(this);
            btnSeekBarRight.setOnTouchListener(this);
            llBtnSeekBarLeft.setOnTouchListener(this);
            llBtnSeekBarRight.setOnTouchListener(this);

        }else if (nDialogType == Variables.DIALOG_TYPE_INPUT){
            ll_content_view_value = findViewById(R.id.ll_dialog_content_value);
            ll_content_view_value.setVisibility(View.VISIBLE);
            edtContentValue = findViewById(R.id.et_dialog_content_value);
            if (strContentValue != null){
                edtContentValue.setText(strContentValue);
            }
        }


        else if (nDialogType == Variables.DIALOG_TYPE_SPINNER){
            ll_content_view_other = findViewById(R.id.ll_dialog_content_spinner);
            ll_content_view_other.setVisibility(View.VISIBLE);
            ContentText = findViewById(R.id.tv_dialog_content_spinner);
            if (strContentOther != null){
                ContentText.setText(strContentOther);
            }
            spinner = findViewById(R.id.sp_dialog_content_spinner);

            ArrayAdapter<CharSequence> adapter;
            int list = 0;
            int index = 0;

            if(strContentOther.contains("TDD Mode"))  list = R.array.set_tddmode;
            else if(strContentOther.contains("UL-DL Configuration")) list = R.array.set_tddconf;
            else if(strContentOther.contains("Service Band")) list = R.array.set_band;
            else if(strContentOther.contains("RF Path On")) list = R.array.set_rfpath;
            else if(strContentOther.contains("DL Gain Mode")) {
//                if(!Variables.HIDDEN_ENABLED) list = R.array.set_dl_icsmode_hidden;
//                else                         list = R.array.set_dl_icsmode;
                list = R.array.set_dl_icsmode;
            }
            else if(strContentOther.contains("UL Gain Mode")){
//                if(!Variables.HIDDEN_ENABLED) list = R.array.set_ul_icsmode_hidden;
//                else                         list = R.array.set_ul_icsmode;
                list = R.array.set_ul_icsmode;
            }

            String[] arrayList = mContext.getResources().getStringArray(list);
            for(index=0; index< arrayList.length; index++){
                if(sCurrentValue.equals(arrayList[index])) break;
            }
//            Debug.loge(new Exception(), "dhjung --> current string :  "+sCurrentValue + " " + index);

            adapter = ArrayAdapter.createFromResource(
                    spinner.getContext(),
                    list,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);
            spinner.setSelection(index);
        }


        else { // onoff
            ll_content_view_other = findViewById(R.id.ll_dialog_content_other);
            ll_content_view_other.setVisibility(View.VISIBLE);
//            Debug.loge(new Exception(), "dhjung --> current value :  "+nCurrentValue);
            ContentText = findViewById(R.id.tv_dialog_content_other);
            if (strContentOther != null){
                ContentText.setText(strContentOther);
            }
        }
        btnPositive = findViewById(R.id.btn_dialog_confirm);
        btnNagative = findViewById(R.id.btn_dialog_cancel);
        if (mConfirmClickListener != null)
            btnPositive.setOnClickListener(mConfirmClickListener);
        else
            btnPositive.setOnClickListener(this);
        if (mCancelClickListener != null)
            btnNagative.setOnClickListener(mCancelClickListener);
        else
            btnNagative.setOnClickListener(this);

//        Debug.loge(new Exception(),"onCreate END");
    }

    void increment(){
        Debug.loge(new Exception(),"increment : "+seekBar.getProgress());
        int tmp = seekBar.getProgress();
        if(tmp < nMaxValue) {
            seekBar.setProgress(tmp + 1);
            contentSeekBarValue.setText(String.valueOf(tmp + 1));
        }
    }
    void decrement(){
        Debug.loge(new Exception(),"decrement : "+seekBar.getProgress());
        int tmp = seekBar.getProgress();
        if(tmp > nMinValue) {
            seekBar.setProgress(tmp - 1);
            contentSeekBarValue.setText(String.valueOf(tmp - 1));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_dialog_left:
            case R.id.btn_dialog_arrow_left:
                decrement();
                break;
            case R.id.ll_dialog_right:
            case R.id.btn_dialog_arrow_right:
                increment();
                break;
//            case R.id.btn_dialog_confirm:
//                Debug.loge(new Exception(),"=======ICTLAB==========onClick Confirm");
//                __currentValue = Integer.valueOf(contentSeekBarValue.getText().toString());
////                activity.onFinishInputDialog("test");
//                dismiss();
//                break;
//            case R.id.btn_dialog_cancel:
//                Debug.loge(new Exception(),"=======ICTLAB==========onClick Cancel");
////                activity.onFinishInputDialog("test2");
//                dismiss();
//                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.ll_dialog_right:
            case R.id.btn_dialog_arrow_right:
                mAutoIncrement = true;
                repeatUpdateHandler.post(new RptUpdater());
                break;
            case R.id.ll_dialog_left:
            case R.id.btn_dialog_arrow_left:
                mAutoDecrement = true;
                repeatUpdateHandler.post(new RptUpdater());
                break;
        }

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.ll_dialog_right:
            case R.id.btn_dialog_arrow_right:
                if ( (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) && mAutoIncrement){
                    mAutoIncrement = false;
                }
                break;
            case R.id.ll_dialog_left:
            case R.id.btn_dialog_arrow_left:
                if ( (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) && mAutoDecrement){
                    mAutoDecrement= false;
                }
                break;
        }
        return false;
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {
        contentSeekBarValue.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {
        contentSeekBarValue.setVisibility(View.VISIBLE);
        contentSeekBarValue.setText(String.valueOf(discreteSeekBar.getProgress()));

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
