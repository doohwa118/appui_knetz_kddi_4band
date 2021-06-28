package kr.knetz.qn.app.v.t;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout.LayoutParams;

import java.util.ArrayList;

import kr.knetz.qn.app.R;
import kr.knetz.qn.app.l.ByteUtil;
import kr.knetz.qn.app.l.Debug;
import kr.knetz.qn.app.v.c.DataType;
import kr.knetz.qn.app.v.x.CustomDialog;
import kr.knetz.qn.app.v.x.Variables;


public class ModemGridAdapter extends GridAdapter implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    public ModemGridAdapter() {
        super();
    }

    Context mContext;
    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
    LayoutParams textViewHeight = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    ArrayList<SubTitleNameClass.SettingType> settingTypeArrayList;
    int min;
    int max;
    boolean selectFlag = false;

    CustomDialog.DialogReturnValueListener mListener;
    CustomDialog ddialog;
    ViewHolder gVh;

    int adapterId;
    public ModemGridAdapter(Context context, int layout, ArrayList<ItemList> list) {
        super(context, layout, list);
    }

    public ModemGridAdapter(Context context, int layout, ArrayList<ItemList> list, ArrayList<SubTitleNameClass.SettingType> settingtypelist) {
        super(context, layout, list);
        this.settingTypeArrayList = settingtypelist;
//        printSettingTypeArrayList(this.settingTypeArrayList);
    }

    public ModemGridAdapter(Context context, int layout, ArrayList<ItemList> list, ArrayList<SubTitleNameClass.SettingType> settingtypelist,CustomDialog.DialogReturnValueListener mListener, int adapterId) {
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
        //Debug.loge(new Exception(),"Knetz getItemId position : "+position);
        return super.getItemId(position);
    }

    @Override
    public Object getItem(int position) {
        //Debug.loge(new Exception(),"Knetz getItem position : "+position);
        return super.getItem(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View local_view = super.getView(position, convertView, parent);
        textViewHeight.height = height;
        viewHolder.ll.setLayoutParams(textViewHeight);

//        setStatusDefaultIcon();

        switch (layout) {
            case R.id.gridview_body_sub1:   // Modem Param
            case R.id.gridview_body_sub2:   // Cell Info
                setStatusDefaultIcon();
                break;
            case R.id.gridview_body_sub3:   // Remote
            case R.id.gridview_body_sub4:   // SIM
            case R.id.gridview_body_sub5:   // Network
            case R.id.gridview_body_sub6:   // EMS
                setStatusDefaultIcon();
//                if ((position % 2) == 0) setLinearLayoutBackgroundStyle(false);
//                else setLinearLayoutBackgroundStyle(true);
                setLinearLayoutBackgroundStyleSet();

                if (settingTypeArrayList != null && settingTypeArrayList.size() > 0) {
                    if (settingTypeArrayList.get(position) != null) {
//                        Debug.loge(new Exception(), "=dhjung=======> Knetz settingTypeArrayList.get(position).getType() : " + settingTypeArrayList.get(position).getType());
                        switch (settingTypeArrayList.get(position).getType()) {

                            case Variables.DIALOG_TYPE_ONOFF:
//                            Debug.logd(new Exception(), "case 1: " + position);
                                viewHolder.tv_value.setVisibility(View.GONE);
                                viewHolder.ll_swc.setVisibility(View.VISIBLE);

                                viewHolder.sw_onoff.setTextOn("ON");
                                viewHolder.sw_onoff.setTextOff("OFF");

                                viewHolder.sw_onoff.setVisibility(View.VISIBLE);
                                viewHolder.sw_onoff.setTag(viewHolder);
                                if (viewHolder.tv_value.getText().equals("0"))
                                    viewHolder.sw_onoff.setChecked(false);
                                else viewHolder.sw_onoff.setChecked(true);
                                viewHolder.sw_onoff.setOnCheckedChangeListener(this);
                                break;

                            case Variables.DIALOG_TYPE_INPUT: // seekbar
//                                Debug.logd(new Exception(), "=dhjung=======> case 6: " + position);
                                viewHolder.position = position;
                                viewHolder.tv_value.setTag(viewHolder);
                                viewHolder.tv_value.setOnClickListener(this);
                                break;

                            case Variables.DIALOG_TYPE_SPINNER: // spinner
//                                Debug.logd(new Exception(), "=dhjung=======> case 3: " + position);
                                viewHolder.tv_value.setVisibility(View.GONE);
                                viewHolder.ll_spinner.setVisibility(View.VISIBLE);
                                viewHolder.spinner.setTag(viewHolder);
                                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                        parent.getContext(),
                                        R.array.set_modem_heartbeattime,
                                        android.R.layout.simple_spinner_item
                                );
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                viewHolder.spinner.setAdapter(adapter);
//                                Debug.loge(new Exception(), "=dhjung=======> spinner tv_value : " + viewHolder.tv_value.getText());

                                if (viewHolder.tv_value.getText() != null) {
                                    for(int i = 0 ;i< Variables.ARRAY_HEARTBEATTIME_SETTING.length; i++){
                                        if (ByteUtil.compare(Variables.ARRAY_HEARTBEATTIME_SETTING[i],Variables.modemStruct.modem.rPeriodicReport)) {
                                            Debug.loge(new Exception(), "matching[" + i + "]: " + ByteUtil.toHexString(Variables.modemStruct.modem.rPeriodicReport));
                                            viewHolder.spinner.setSelection(i);
                                        }
                                    }
                                }
                                viewHolder.spinner.setOnItemSelectedListener(this);
                                break;
                        }
                    } else {
                        Debug.loge(new Exception(), "=dhjung=======> Knetz settingTypeArrayList null");
                    }

                }
                break;

            default:
                break;
        }
        return local_view;
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

    void setLinearLayoutBackgroundStyleSet() {
        viewHolder.ll.setBackgroundResource(R.drawable.grieview_style_set);
        viewHolder.ll_swc.setBackgroundResource(R.drawable.grieview_style_other_set);
        viewHolder.ll_spinner.setBackgroundResource(R.drawable.grieview_style_other_set);
        viewHolder.ll_btn.setBackgroundResource(R.drawable.grieview_style_other_set);
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

        if (vh.tv_value.getText().equals("0")) str = "ON";
        else str = "OFF";

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


    @Override
    public void onClick(View v) {
        Debug.loge(new Exception(),"=dhjung=======> modemGridAdapter !! onClick tv_value");

        ViewHolder vh = (ViewHolder) v.getTag();
        gVh = vh;
        String str;

        switch (v.getId()) {
            case R.id.gridview_sub_value_spinner:
                ddialog = new CustomDialog(context,
                        Variables.DIALOG_TYPE_SPINNER,
                        mContext.getString(R.string.dialog_title),
                        vh.tv_title.getText().toString() + " Setting Change?",
                        vh.tv_value_spinner.getText().toString(),
                        confirmOnClickListener,
                        cancelOnClickListener);
                Debug.loge(new Exception(), "K-NETZ onClick ddialog : " + ddialog);
//                ddialog.setOnDismissListener(dialogOnDismiss);
                ddialog.show();
                break;

            case R.id.gridview_sub_value: //
//                ViewHolder vh = (ViewHolder)v.getTag();
//                gVh = vh;
                str = vh.tv_title.getText().toString();
                Debug.loge(new Exception(),"=dhjung=======> "+str);

                switch(str) {

                    case "Local Phone":
                    case "RCS Phone":
                    case "APN":
                    case "User ID":
                    case "User Password":
                    case "RCS IP Addr #1":
                    case "RCS IP Addr #2":
                    case "RCS IP Addr #3":
                    case "RCS IP Addr #4":
                    case "RCS IP Addr #5":
                    case "RCS IP Addr #6":
                    case "RCS IP Addr #7":
                    case "RCS IP Addr #8":
                    case "RCS IP Addr #9":
                    case "RCS IP Addr #10":
                    case "RCS PORT":

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
                Debug.loge(new Exception(), "K-NETZ onClick ddialog : " + ddialog);
//                ddialog.setOnDismissListener(dialogOnDismiss);
                ddialog.show();
                break;

        }
    }

    View.OnClickListener confirmOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DataType dataType;
            String str;
            switch (ddialog.getnDialogType()){
                case Variables.DIALOG_TYPE_SPINNER :
                    dataType = new DataType(gVh.tv_title.getText().toString(),String.valueOf(ddialog.getnCurrentValue()),getAdapterId());
                    mListener.onFinishInputDialog(dataType);
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
                case Variables.DIALOG_TYPE_INPUT:
                    dataType = new DataType(gVh.tv_title.getText().toString(),String.valueOf(ddialog.getEditTextValue()),getAdapterId());
                    mListener.onFinishInputDialog(dataType);
                    break;

            }
            ddialog.dismiss();
        }
    };

    View.OnClickListener cancelOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (ddialog.getnDialogType()){
                case Variables.DIALOG_TYPE_SPINNER:
                    break;
                case Variables.DIALOG_TYPE_ONOFF:
                    if (gVh.tv_value.getText().equals("0")) gVh.sw_onoff.setChecked(false);
                    else gVh.sw_onoff.setChecked(true);
                    break;
                case Variables.DIALOG_TYPE_SEEKBAR:
                    break;
            }
            ddialog.dismiss();
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ViewHolder vh = (ViewHolder)parent.getTag();
        gVh = vh;
        //Debug.loge(new Exception(),"position : "+position);
        if(selectFlag) {
            if (vh.tv_value.getText() != null) {
                if (ddialog != null) {
                    if (!ddialog.isShowing()) {
                        ddialog = new CustomDialog(context,
                                Variables.DIALOG_TYPE_SPINNER,
                                mContext.getString(R.string.dialog_title),
                                vh.tv_title.getText().toString() + " " + parent.getItemAtPosition(position).toString() + " Setting?",
                                position,
                                confirmOnClickListener,
                                cancelOnClickListener);
//                        ddialog.setOnDismissListener(dialogOnDismiss);
                        ddialog.show();
                    }
                }else {
                    ddialog = new CustomDialog(context,
                            Variables.DIALOG_TYPE_SPINNER,
                            mContext.getString(R.string.dialog_title),
                            vh.tv_title.getText().toString() + " " + parent.getItemAtPosition(position).toString()  + " Setting?",
                            position, confirmOnClickListener,
                            cancelOnClickListener);
//                    ddialog.setOnDismissListener(dialogOnDismiss);
                    ddialog.show();
                }
                selectFlag = false;
            }
        }else {
            selectFlag = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
