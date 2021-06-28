package kr.knetz.qn.app.v.t;

import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import java.util.ArrayList;

import kr.knetz.qn.app.R;
import kr.knetz.qn.app.l.Debug;
import kr.knetz.qn.app.v.c.DataType;
import kr.knetz.qn.app.v.x.CustomDialog;
import kr.knetz.qn.app.v.x.Variables;


public class SettingGridAdapter extends GridAdapter implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    public SettingGridAdapter() {
        super();
    }
    Context mContext;

    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
    LayoutParams textViewHeight = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    ArrayList<SubTitleNameClass.SettingType> settingTypeArrayList;
    int min;
    int max;

    boolean selectFlag = false;
    boolean cancelFlag = false;


    CustomDialog.DialogReturnValueListener mListener;
    CustomDialog ddialog;
    ViewHolder gVh;

    int adapterId;

    public SettingGridAdapter(Context context, int layout, ArrayList<ItemList> list) {
        super(context, layout, list);
    }

    public SettingGridAdapter(Context context, int layout, ArrayList<ItemList> list, ArrayList<SubTitleNameClass.SettingType> settingtypelist) {
        super(context, layout, list);
        this.mContext = context;
        this.settingTypeArrayList = settingtypelist;
        this.selectFlag = false;
//        printSettingTypeArrayList(this.settingTypeArrayList);
    }

    public SettingGridAdapter(Context context, int layout, ArrayList<ItemList> list, ArrayList<SubTitleNameClass.SettingType> settingtypelist,CustomDialog.DialogReturnValueListener mListener) {
        super(context, layout, list);
        this.mContext = context;
        this.settingTypeArrayList = settingtypelist;
        this.selectFlag = false;
        this.mListener = mListener;
    }

    public SettingGridAdapter(Context context, int layout, ArrayList<ItemList> list, ArrayList<SubTitleNameClass.SettingType> settingtypelist,CustomDialog.DialogReturnValueListener mListener, int adapterId) {
        super(context, layout, list);
        this.mContext = context;
        this.settingTypeArrayList = settingtypelist;
        this.selectFlag = false;
        this.mListener = mListener;
        this.adapterId = adapterId;
    }

    public int getAdapterId() {
        return adapterId;
    }

    @Override
    public long getItemId(int position) {
        Debug.loge(new Exception(),"=dhjung=======> getItemId position : "+position);
        return super.getItemId(position);
    }

    @Override
    public Object getItem(int position) {
        Debug.loge(new Exception(),"=dhjung=======> getItem position : "+position);
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View local_view = super.getView(position, convertView, parent);

        boolean flagUplink = false;
        setStatusDefaultIcon();

        switch(layout){
            case R.id.gridview_body_sub1 :
                if (position == 0) {
                    viewHolder.tv_title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            Debug.logv(new Exception(), "Hidden setting title : " + viewHolder.tv_title.getText());asdasd
//                            Debug.logv(new Exception(), "Hidden value  : " + Variables.HIDDEN_ENABLED);
//                            Debug.logv(new Exception(), "Hidden count  : " + Variables.HIDDEN_ENABLED_COUNT);
                            ++Variables.HIDDEN_ENABLED_COUNT;
                            if (Variables.HIDDEN_ENABLED_COUNT > 6) {
                                Variables.HIDDEN_ENABLED = !Variables.HIDDEN_ENABLED;
                                Variables.HIDDEN_ENABLED_COUNT = 0;
                                Toast.makeText(context,Variables.HIDDEN_ENABLED ? "히든 메뉴가 비활성화 되었습니다." : "히든 메뉴가 활성화 되었습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
//                break;
            case R.id.gridview_body_sub2 : // psu
            case R.id.gridview_body_sub3 : // downlink dsp&rf
            case R.id.gridview_body_sub4 : // uplink dsp&rf
            case R.id.gridview_body_sub5 : // downlink amp
            case R.id.gridview_body_sub6 : // uplink amp
            case R.id.gridview_body_sub7 : // service fa
            case R.id.gridview_body_sub8 : // common

                textViewHeight.height = height;
                viewHolder.ll.setLayoutParams(textViewHeight);

 //               if (layout != R.id.gridview_body_sub1)
                {

                    if ((position % 2) == 0) setLinearLayoutBackgroundStyle(false);
                    else setLinearLayoutBackgroundStyle(true);

                    if ((layout == R.id.gridview_body_sub4) || (layout == R.id.gridview_body_sub6))
                        flagUplink = true;

                    switch (settingTypeArrayList.get(position).getType()) {

//                    case 1: // on/off
                        case Variables.DIALOG_TYPE_ONOFF:
//                            Debug.logd(new Exception(), "=dhjung=======> case 1: " + position);
                            viewHolder.tv_value.setVisibility(View.GONE);
                            viewHolder.ll_swc.setVisibility(View.VISIBLE);

                            if(viewHolder.tv_title.getText().toString().contains("Filter Auto") || viewHolder.tv_title.getText().toString().contains("Cell Search")){
                                viewHolder.sw_onoff.setTextOn("Auto");
                                viewHolder.sw_onoff.setTextOff("Manual");
                            }
                            else if(viewHolder.tv_title.getText().toString().contains("Filter Select")){
                                viewHolder.sw_onoff.setTextOn("LTE 15M");
                                viewHolder.sw_onoff.setTextOff("LTE+CDMA");
                            }
                            else if(viewHolder.tv_title.getText().toString().contains("Antenna Type")){
                                viewHolder.sw_onoff.setTextOn("分離型");
                                viewHolder.sw_onoff.setTextOff("一体型");
                            }
                            else{
                                viewHolder.sw_onoff.setTextOn("ON");
                                viewHolder.sw_onoff.setTextOff("OFF");
                            }

                            viewHolder.sw_onoff.setVisibility(View.VISIBLE);
                            viewHolder.sw_onoff.setTag(viewHolder);
                            if (viewHolder.tv_value.getText().equals("0"))
                                viewHolder.sw_onoff.setChecked(false);
                            else viewHolder.sw_onoff.setChecked(true);
                            viewHolder.sw_onoff.setOnCheckedChangeListener(this);
                            break;

//                    case 2: // seekbar
                        case Variables.DIALOG_TYPE_SEEKBAR:  // seekbar
//                            Debug.logd(new Exception(), "=dhjung=======> case 2: " + position);
                            viewHolder.position = position;
                            viewHolder.tv_value.setTag(viewHolder);
                            viewHolder.tv_value.setOnClickListener(this);
                            break;

//                    case 3: // spinner
                        case Variables.DIALOG_TYPE_SPINNER:
//                            Debug.logd(new Exception(), "=dhjung=======> setting spinner case 3: " + position);
                            viewHolder.tv_value.setVisibility(View.GONE);
                            viewHolder.tv_value_spinner.setVisibility(View.VISIBLE);
                            viewHolder.tv_value_spinner.setTag(viewHolder);
                            viewHolder.tv_value_spinner.setOnClickListener(this);

//                            viewHolder.tv_value.setVisibility(View.GONE);
//                            viewHolder.ll_spinner.setVisibility(View.VISIBLE);
//                            viewHolder.spinner.setTag(viewHolder);
//
//                            ArrayAdapter<CharSequence> adapter;
//                            if (flagUplink) { // uplink
//                                adapter = ArrayAdapter.createFromResource(
//                                        parent.getContext(),
//                                        R.array.set_ul_icsmode,
//                                        android.R.layout.simple_spinner_item
//                                );
//                            }else { // downlink
//                                adapter = ArrayAdapter.createFromResource(
//                                        parent.getContext(),
//                                        R.array.set_dl_icsmode,
//                                        android.R.layout.simple_spinner_item
//                                );
//                            }
//                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                            viewHolder.spinner.setAdapter(adapter);
////                            Debug.loge(new Exception(), "spinner tv_value : " + viewHolder.tv_value.getText());
//                            if (viewHolder.tv_value.getText() != null) {
//                                if (!viewHolder.tv_value.getText().equals("0")) {
//                                    if (!viewHolder.tv_value.getText().toString().equals("")) {
//                                        viewHolder.spinner.setSelection(Integer.parseInt(viewHolder.tv_value.getText().toString()) - 2);
//                                    }
//                                } else {
//                                    if (!viewHolder.tv_value.getText().toString().equals(""))
//                                        viewHolder.spinner.setSelection(Integer.parseInt(viewHolder.tv_value.getText().toString()));
//                                }
//                            }
//
//                            viewHolder.spinner.setOnItemSelectedListener(this);
                            break;

                        case Variables.DIALOG_TYPE_BUTTON: // seekbar
//                            Debug.logd(new Exception(), "=dhjung=======> case 5: " + position);
                            viewHolder.tv_value.setVisibility(View.GONE);
                            viewHolder.ll_btn.setVisibility(View.VISIBLE);
                            viewHolder.button.setTag(viewHolder);
                            viewHolder.button.setOnClickListener(this);
                            break;

                        case Variables.DIALOG_TYPE_INPUT:
//                            Debug.logd(new Exception(), "=dhjung=======> case 6: " + position);
                            viewHolder.position = position;
                            viewHolder.tv_value.setTag(viewHolder);
                            viewHolder.tv_value.setOnClickListener(this);
                            break;

                        default:
                            break;
                    }
                }
                break;

            default:
                break;
        }
        return local_view;
    }

    public void setTextviewTextSize(int key)
    {
//        if (gVh != null){
//            float size = this.gVh.tv_title.getTextSize();
//            if (key == 0){
//                if (key < 20){
//                    this.gVh.tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size+1);
//                    this.gVh.tv_value.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size+1);
//                }else{
//                    this.gVh.tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
//                    this.gVh.tv_value.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
//                }
//            }else{
//                if (key > 10) {
//                    this.gVh.tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size - 1);
//                    this.gVh.tv_value.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size - 1);
//                }else{
//                    this.gVh.tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
//                    this.gVh.tv_value.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
//                }
//            }
//        }else{
//            Debug.logi(new Exception(),"gVh null!!");
//        }
        float size = viewHolder.tv_title.getTextSize();
        Debug.loge(new Exception(),"!!!!!!!!!!!!!!! size !!!!!!! : "+size);
//        if (key == 0){
//            if (size < 20){
//                viewHolder.tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size+1);
//                viewHolder.tv_value.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size+1);
//            }else{
//                viewHolder.tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
//                viewHolder.tv_value.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
//            }
//        }else{
//            if (size > 10) {
//                viewHolder.tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size - 1);
//                viewHolder.tv_value.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size - 1);
//            }else{
//                viewHolder.tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
//                viewHolder.tv_value.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
//            }
//        }
        if (key == KeyEvent.KEYCODE_VOLUME_UP){
            if (size < 20){
                textSize = size + 1.0f;
            }else{
                textSize = size;
            }
        }else{
            if (size > 10) {
                textSize = size - 1.0f;
            }else{
                textSize = size;
            }
        }
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    void setLinearLayoutBackgroundStyle(boolean flag){
        if (!flag) {
            viewHolder.ll.setBackgroundResource(R.drawable.grieview_style_odd);
            viewHolder.ll_swc.setBackgroundResource(R.drawable.grieview_style_other_odd);
            viewHolder.ll_spinner.setBackgroundResource(R.drawable.grieview_style_other_odd);
            viewHolder.ll_btn.setBackgroundResource(R.drawable.grieview_style_other_odd);

        }else{
            viewHolder.ll.setBackgroundResource(R.drawable.grieview_style_even);
            viewHolder.ll_swc.setBackgroundResource(R.drawable.grieview_style_other_even);
            viewHolder.ll_spinner.setBackgroundResource(R.drawable.grieview_style_other_even);
            viewHolder.ll_btn.setBackgroundResource(R.drawable.grieview_style_other_even);
        }
    }

    void printSettingTypeArrayList(ArrayList<SubTitleNameClass.SettingType> subSettingTypeArrayList){
        //Debug.loge(new Exception(), "=====================================");
        for (int i = 0; i < subSettingTypeArrayList.size(); i++){
            //Debug.logd(new Exception(),"type : "+subSettingTypeArrayList.get(i).type);
            if (subSettingTypeArrayList.get(i).type != 1) {
                //Debug.logd(new Exception(), "min : " + subSettingTypeArrayList.get(i).min_value);
                //Debug.logd(new Exception(), "max : " + subSettingTypeArrayList.get(i).max_value);
            }
        }
        //Debug.loge(new Exception(),"=====================================");
    }

    // on off switch
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ViewHolder vh = (ViewHolder)buttonView.getTag();
        gVh = vh;
        String str;
//        Toast.makeText(context,vh.tv_title.getText().toString()+" switch "+isChecked,Toast.LENGTH_SHORT).show();
        if(vh.tv_title.getText().toString().contains("Filter Auto") || vh.tv_title.getText().toString().contains("Cell Search")){
            if (vh.tv_value.getText().equals("0")) str = "Auto";
            else str = "Manual";
        }
        else if(vh.tv_title.getText().toString().contains("Filter Select")){
            if (vh.tv_value.getText().equals("0")) str = "LTE 15M";
            else str = "LTE+CDMA";
        }
        else if(vh.tv_title.getText().toString().contains("Antenna Type")){
            if (vh.tv_value.getText().equals("0")) str = "分離型";
            else str = "一体型";
        }
        else {
            if (vh.tv_value.getText().equals("0")) str = "ON";
            else str = "OFF";
        }

        if (ddialog != null) {
            if (!ddialog.isShowing()) {
                ddialog = new CustomDialog(context,
                        Variables.DIALOG_TYPE_ONOFF,
                        mContext.getString(R.string.dialog_title),
                        vh.tv_title.getText().toString() + " " + str + " Setting?", isChecked,
                        confirmOnClickListener,
                        cancelOnClickListener);
//                ddialog.setOnDismissListener(dialogOnDismiss);
//                Debug.loge(new Exception(), "ICTLAB onCheckedChanged ddialog : " + ddialog);
                ddialog.show();
            }
        }else{
            ddialog = new CustomDialog(context,
                    Variables.DIALOG_TYPE_ONOFF,
                    mContext.getString(R.string.dialog_title),
                    vh.tv_title.getText().toString() + " " + str + " Setting?", isChecked,
                    confirmOnClickListener,
                    cancelOnClickListener);
//            ddialog.setOnDismissListener(dialogOnDismiss);
            //Debug.loge(new Exception(), "ICTLAB onCheckedChanged ddialog : " + ddialog);
            ddialog.show();
        }
    }

    // textview click
    @Override
    public void onClick(View v) {

        Debug.loge(new Exception(),"=dhjung=======> settingGridAdapter !! onClick tv_value");
        ViewHolder vh = (ViewHolder)v.getTag();
        gVh = vh;
        String str;

        switch(v.getId()){
            case R.id.gridview_sub_value_spinner:
                ddialog = new CustomDialog(context,
                        Variables.DIALOG_TYPE_SPINNER,
                        mContext.getString(R.string.dialog_title),
                        vh.tv_title.getText().toString() +  " Setting Change?",
                        vh.tv_value_spinner.getText().toString(),
                        confirmOnClickListener,
                        cancelOnClickListener);
                Debug.loge(new Exception(),"K-NETZ onClick ddialog : "+ddialog);
//                ddialog.setOnDismissListener(dialogOnDismiss);
                ddialog.show();
                break;

            case R.id.gridview_sub_value : //
//                ViewHolder vh = (ViewHolder)v.getTag();
//                gVh = vh;
                str = vh.tv_title.getText().toString();
                Debug.loge(new Exception(),"=dhjung=======> "+str);

                switch(str) {

                    case "Install Address":
                        ddialog = new CustomDialog(context,
                                Variables.DIALOG_TYPE_INPUT,
                                vh.tv_title.getText().toString(),
                                "",
                                confirmOnClickListener,
                                cancelOnClickListener
                        );
                        break;

                    default:
                        min = settingTypeArrayList.get(vh.position).getMin_value();
                        max = settingTypeArrayList.get(vh.position).getMax_value();

                        ddialog = new CustomDialog(context,
                                Variables.DIALOG_TYPE_SEEKBAR,
                                vh.tv_title.getText().toString(),
                                "",
                                min,
                                max,
                                Integer.valueOf(vh.tv_value.getText().toString().split(" ")[0]),
                                confirmOnClickListener,
                                cancelOnClickListener
                        );
                        break;
                }

                Debug.loge(new Exception(),"K-NETZ onClick ddialog : "+ddialog);
//                ddialog.setOnDismissListener(dialogOnDismiss);
                ddialog.show();
                break;

            case R.id.gridview_sub_button : // Cancelation
                ddialog = new CustomDialog(context,
                        Variables.DIALOG_TYPE_BUTTON,
                        mContext.getString(R.string.dialog_title),
                        vh.tv_title.getText().toString() + " OFF" + " Setting?",
                        1, confirmOnClickListener,
                        cancelOnClickListener);
                //Debug.loge(new Exception(),"ICTLAB onClick button ddialog : "+ddialog);
                ddialog.show();
                break;
        }
    }


    DialogInterface.OnDismissListener dialogOnDismiss = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
//            Debug.loge(new Exception(),"=====================================================");
//            Debug.loge(new Exception(),"positive button : "+ddialog.getCurrentValue());
//            Debug.loge(new Exception(),"gVh.position : "+gVh.position);
////            Debug.loge(new Exception(),"positive button : "+ddialog.getDataType().getValue());
//            Debug.loge(new Exception(),"=====================================================");
////                    ddialog.getCurrentValue()
//            DataType dataType = new DataType(gVh.tv_title.getText().toString(),String.valueOf(ddialog.getCurrentValue()),getAdapterId());
//            mListener.onFinishInputDialog(dataType);
        }
    };

    View.OnClickListener confirmOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Debug.loge(new Exception(),"===============================================");
//            Debug.loge(new Exception(),"--------------confirmOnClickListener---getDialogType()- : "+ddialog.getnDialogType());
//            Debug.loge(new Exception(),"===============================================gVh.sw_onoff.isChecked() : "+gVh.sw_onoff.isChecked());
            DataType dataType;
            String str;
            int list = 0;
            switch (ddialog.getnDialogType()){
                case Variables.DIALOG_TYPE_SPINNER:
//                    if (gVh.tv_value.getText().equals("2")) str = "3";
//                    else str = "2";
//                    if(!Variables.HIDDEN_ENABLED) {
//                        if (ddialog.getContentString().contains("DL Gain Mode"))
//                            list = R.array.set_dl_icsmode_hidden;
//                        else if (ddialog.getContentString().contains("UL Gain Mode"))
//                            list = R.array.set_ul_icsmode_hidden;
//                    }else {
                        if (ddialog.getContentString().contains("DL Gain Mode"))
                            list = R.array.set_dl_icsmode;
                        else if (ddialog.getContentString().contains("UL Gain Mode"))
                            list = R.array.set_ul_icsmode;
                        else if (ddialog.getContentString().contains("Service Band"))
                            list = R.array.set_band;
                        else if (ddialog.getContentString().contains("RF Path On"))
                            list = R.array.set_rfpath;

//                    }
                    String[] arrayList = mContext.getResources().getStringArray(list);
                    str = arrayList[(int)ddialog.getCurrentSpinnerValue()];
//                str = String.valueOf(ddialog.getCurrentSpinnerValue());
//                    Debug.loge(new Exception(), "dhjung --> current string :  "+gVh.tv_value_spinner.getText().toString() + " " + str + " " + ddialog.getCurrentSpinnerValue());

                    dataType = new DataType(gVh.tv_title.getText().toString(), str, getAdapterId());
                    mListener.onFinishInputDialog(dataType);
                    selectFlag = true;
                    break;
                case Variables.DIALOG_TYPE_ONOFF:
                    if (gVh.tv_value.getText().equals("0")) str = "1";
                    else str = "0";
                    dataType = new DataType(gVh.tv_title.getText().toString(),str,getAdapterId());
                    mListener.onFinishInputDialog(dataType);
                    break;
                case Variables.DIALOG_TYPE_SEEKBAR:
                    dataType = new DataType(gVh.tv_title.getText().toString(),String.valueOf(ddialog.getCurrentValue()),getAdapterId());
                    mListener.onFinishInputDialog(dataType);
                    break;
                case Variables.DIALOG_TYPE_BUTTON:
                    str = "1";
                    dataType = new DataType(gVh.tv_title.getText().toString(),str,getAdapterId());
                    mListener.onFinishInputDialog(dataType);
                    break;
                case Variables.DIALOG_TYPE_INPUT:
                    dataType = new DataType(gVh.tv_title.getText().toString(),String.valueOf(ddialog.getEditTextValue()),getAdapterId());
                    mListener.onFinishInputDialog(dataType);
                    break;
            }
//            Debug.loge(new Exception(),"------=============END=================-----------");
            ddialog.dismiss();
        }
    };

    View.OnClickListener cancelOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Debug.loge(new Exception(),"===============================================");
//            Debug.loge(new Exception(),"--------------cancelOnClickListener-----------");
//            Debug.loge(new Exception(),"ICTLAB cancelOnClickListener ddialog : "+ddialog);
//            Debug.loge(new Exception(),"-getCurrentValue() : "+ddialog.getCurrentValue()+" getnArgValue : "+ddialog.getnArgValue());
//            Debug.loge(new Exception(),"===============================================");
            switch (ddialog.getnDialogType()){
                case Variables.DIALOG_TYPE_SPINNER:
//                    Debug.loge(new Exception(),"-------------gVh.tv_value.getText().toString()-: "+gVh.tv_value.getText().toString());
//                    Debug.loge(new Exception(),"-------------viewHolder.tv_value.getText().toString()-: "+viewHolder.tv_value.getText().toString());
//                    if (gVh.tv_value.getText() != null) {
//                        if (!gVh.tv_value.getText().equals("0")) {
//                            if (!gVh.tv_value.getText().toString().equals(""))
//                                gVh.spinner.setSelection(Integer.parseInt(gVh.tv_value.getText().toString()) - 2);
//                        } else {
//                            if (!gVh.tv_value.getText().toString().equals(""))
//                                gVh.spinner.setSelection(Integer.parseInt(gVh.tv_value.getText().toString()));
//                        }
//                        selectFlag = false;
//                    }
                    break;
                case Variables.DIALOG_TYPE_ONOFF:
//                    viewHolder.tv_value.
                    //Debug.loge(new Exception(),"-------------gVh.tv_value.getText().toString()-: "+gVh.tv_value.getText().toString());
                    //Debug.loge(new Exception(),"-------------viewHolder.tv_value.getText().toString()-: "+viewHolder.tv_value.getText().toString());
//                    gVh.sw_onoff.setChecked(Boolean.parseBoolean(gVh.tv_value.getText().toString()));
                    //Debug.loge(new Exception(),"------ICTLAB-------gVh.tv_value.getText().toString()-: "+gVh.sw_onoff.getText().toString());
                    if (gVh.tv_value.getText().equals("0")) gVh.sw_onoff.setChecked(false);
                    else gVh.sw_onoff.setChecked(true);
                    //Debug.loge(new Exception(),"------ICTLAB----gVh.tv_value.getText().toString()-: "+gVh.sw_onoff.getText().toString());
                    break;
                case Variables.DIALOG_TYPE_SEEKBAR:
//                    DataType dataType = new DataType(gVh.tv_title.getText().toString(),String.valueOf(ddialog.getCurrentValue()),getAdapterId());
//                    mListener.onFinishInputDialog(dataType);
                    break;
                case Variables.DIALOG_TYPE_BUTTON:
                    break;
            }
            ddialog.dismiss();
        }
    };


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//        Debug.logd(new Exception(), "spinner setting onitem: " + position);

        ViewHolder vh = (ViewHolder) parent.getTag();
        gVh = vh;

        if (selectFlag) {
            if (vh.tv_value.getText() != null) {
                if (ddialog != null) {
                    if (!ddialog.isShowing()) {
                        ddialog = new CustomDialog(context,
                                Variables.DIALOG_TYPE_SPINNER,
                                mContext.getString(R.string.dialog_title),
                                vh.tv_title.getText().toString() + " " + parent.getItemAtPosition(position).toString() + " Setting?",
                                position, confirmOnClickListener,
                                cancelOnClickListener);
//                        ddialog.setOnDismissListener(dialogOnDismiss);
                        ddialog.show();
                    }
                } else {
                    ddialog = new CustomDialog(context,
                            Variables.DIALOG_TYPE_SPINNER,
                            mContext.getString(R.string.dialog_title),
                            vh.tv_title.getText().toString() + " " + parent.getItemAtPosition(position).toString() + " Setting?",
                            position, confirmOnClickListener,
                            cancelOnClickListener);
//                    ddialog.setOnDismissListener(dialogOnDismiss);
                    ddialog.show();
                }
                selectFlag = false;
            }
        } else {
            selectFlag = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}
