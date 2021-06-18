package kr.knetz.kddi.app.v.t;


import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import java.util.ArrayList;

import kr.knetz.kddi.app.R;
import kr.knetz.kddi.app.l.Debug;
import kr.knetz.kddi.app.v.c.DataType;
import kr.knetz.kddi.app.v.x.CustomDialog;
import kr.knetz.kddi.app.v.x.Variables;

public class TsyncGridAdapter extends GridAdapter implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    public TsyncGridAdapter(){
        super();
    }
    Context mContext;

    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
    LayoutParams textViewHeight = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    ArrayList<SubTitleNameClass.SettingType> settingTypeArrayList;
    int min;
    int max;

    boolean selectFlag = false;
    boolean isFirstBoot = true;
    boolean spinnerInitialized = false;

    CustomDialog.DialogReturnValueListener mListener;
    CustomDialog ddialog;
    ViewHolder gVh;

    int adapterId;

    public TsyncGridAdapter(Context context, int layout, ArrayList<ItemList> list){
        super(context, layout, list);
    }

    public TsyncGridAdapter(Context context, int layout, ArrayList<ItemList> list, ArrayList<SubTitleNameClass.SettingType> settingtypelist){
        super(context, layout, list);
        this.mContext = context;
        this.settingTypeArrayList = settingtypelist;
        this.selectFlag = false;
    }

    public TsyncGridAdapter(Context context, int layout, ArrayList<ItemList> list, ArrayList<SubTitleNameClass.SettingType> settingtypelist,CustomDialog.DialogReturnValueListener mListener) {
        super(context, layout, list);
        this.mContext = context;
        this.settingTypeArrayList = settingtypelist;
        this.selectFlag = true;
        this.mListener = mListener;
    }

    public TsyncGridAdapter(Context context, int layout, ArrayList<ItemList> list, ArrayList<SubTitleNameClass.SettingType> settingtypelist, CustomDialog.DialogReturnValueListener mListener, int adapterId){
        super(context, layout, list);
        this.mContext = context;
        this.settingTypeArrayList = settingtypelist;
        this.selectFlag = false;
        this.mListener = mListener;
        this.adapterId = adapterId;
    }

    public int getAdapterId(){return adapterId;}

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View local_view = super.getView(position, convertView, parent);
        textViewHeight.height = height;
        viewHolder.ll.setLayoutParams(textViewHeight);

        switch(layout){
            case R.id.gridview_body_sub1:
                setStatusDefaultIcon();
//            setAlarmSystemInfoIcon();
//            if (viewHolder.tv_value.getText().equals("0"))
////                    viewHolder.iv_icon_center.setImageResource(R.drawable.alarm_green_64x64);
//                viewHolder.iv_icon_center.setImageResource(R.drawable.icon_body_circle_gr_64x64);
//            else
////                    viewHolder.iv_icon_center.setImageResource(R.drawable.alarm_red_64x64);
//                viewHolder.iv_icon_center.setImageResource(R.drawable.icon_body_circle_red_64x64);

                if(position == 0) {
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
                                Toast.makeText(context, Variables.HIDDEN_ENABLED ? "히든 메뉴가 비활성화 되었습니다." : "히든 메뉴가 활성화 되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;

            case R.id.gridview_body_sub2:
                setStatusDefaultIcon();
                break;

            case R.id.gridview_body_sub3:
                setStatusDefaultIcon();

//                if ((position % 2) == 0) setLinearLayoutBackgroundStyle(false);
//                else setLinearLayoutBackgroundStyle(true);
                setLinearLayoutBackgroundStyleSet();

                switch (settingTypeArrayList.get(position).getType()) {
//                    case 1: // on/off
                    case Variables.DIALOG_TYPE_ONOFF:
//                            Debug.logd(new Exception(), "case 1: " + position);
                        viewHolder.tv_value.setVisibility(View.GONE);
                        viewHolder.ll_swc.setVisibility(View.VISIBLE);

//                        Debug.loge(new Exception(), "dhjung --> string :  "+viewHolder.tv_title.getText().toString());

                        if(viewHolder.tv_title.getText().toString().contains("Tsync")){
                            viewHolder.sw_onoff.setTextOn("UL");
                            viewHolder.sw_onoff.setTextOff("DL");
                        }
                        else if(viewHolder.tv_title.getText().toString().contains("SSB Search")){
                            viewHolder.sw_onoff.setTextOn("Auto");
                            viewHolder.sw_onoff.setTextOff("Manual");
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
//                            Debug.logd(new Exception(), "case 2: " + position);
                        viewHolder.position = position;
                        viewHolder.tv_value.setTag(viewHolder);
                        viewHolder.tv_value.setOnClickListener(this);
                        break;

//                    case 3: // spinner
                    case Variables.DIALOG_TYPE_SPINNER:

                        viewHolder.tv_value.setVisibility(View.GONE);
                        viewHolder.tv_value_spinner.setVisibility(View.VISIBLE);
                        viewHolder.tv_value_spinner.setTag(viewHolder);
                        viewHolder.tv_value_spinner.setOnClickListener(this);


//                        Debug.logd(new Exception(), "tsync spinner case 3: " + position);

//                        viewHolder.tv_value.setVisibility(View.GONE);
//                        viewHolder.ll_spinner.setVisibility(View.VISIBLE);
//                        viewHolder.spinner.setTag(viewHolder);
//
//                        ArrayAdapter<CharSequence> adapter;
//                        if(settingTypeArrayList.get(position).getType() == Variables.DIALOG_TYPE_SPINNER_TDD){
//                            adapter = ArrayAdapter.createFromResource(
//                                    parent.getContext(),
//                                    R.array.set_tddconf,
//                                    android.R.layout.simple_spinner_item
//                            );
//
//                        }
//                        else {
//                            adapter = ArrayAdapter.createFromResource(
//                                    parent.getContext(),
//                                    R.array.set_tddmode,
//                                    android.R.layout.simple_spinner_item
//                            );
//                        }
//
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        viewHolder.spinner.setAdapter(adapter);
////                    Debug.loge(new Exception(), "spinner tv_value : " + viewHolder.tv_value.getText());
//
//                        if (viewHolder.tv_value.getText() != null) {
//                            if (!viewHolder.tv_value.getText().toString().equals(""))
//                                viewHolder.spinner.setSelection(Integer.parseInt(viewHolder.tv_value.getText().toString()));
//                        }
////                    viewHolder.spinner.setSelection(position, false);
//                        viewHolder.spinner.setOnItemSelectedListener(this);
                        break;

                    case Variables.DIALOG_TYPE_BUTTON: // seekbar
                        viewHolder.tv_value.setVisibility(View.GONE);
                        viewHolder.ll_btn.setVisibility(View.VISIBLE);
                        viewHolder.button.setTag(viewHolder);
                        viewHolder.button.setOnClickListener(this);
                        break;

                    default:
                        break;
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ViewHolder vh = (ViewHolder)buttonView.getTag();
        gVh = vh;
        String str;

//        Toast.makeText(context,vh.tv_title.getText().toString()+" switch "+isChecked,Toast.LENGTH_SHORT).show();
        if(vh.tv_title.getText().toString().contains("Tsync")){
            if (vh.tv_value.getText().equals("0")) str = "UL";
            else str = "DL";
        }
        else {
            if (vh.tv_value.getText().equals("0")) str = "Auto";
            else str = "Manual";
        }

        if (ddialog != null) {
            if (!ddialog.isShowing()) {
                ddialog = new CustomDialog(context,
                        Variables.DIALOG_TYPE_ONOFF,
                        mContext.getString(R.string.dialog_title),
                        vh.tv_title.getText().toString() + " " + str + " Setting?",
                        isChecked,
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
                    vh.tv_title.getText().toString() + " " + str + " Setting?",
                    isChecked,
                    confirmOnClickListener,
                    cancelOnClickListener);
//            ddialog.setOnDismissListener(dialogOnDismiss);
            //Debug.loge(new Exception(), "ICTLAB onCheckedChanged ddialog : " + ddialog);
            ddialog.show();
        }
    }

    @Override
    public void onClick(View v) {
//        Debug.loge(new Exception(), "dhjung TsyncGrid OnClick");

        ViewHolder vh = (ViewHolder)v.getTag();
        gVh = vh;

        switch(v.getId()){
            case R.id.gridview_sub_value_spinner:
                ddialog = new CustomDialog(context,
                        Variables.DIALOG_TYPE_SPINNER,
                        mContext.getString(R.string.dialog_title),
                        vh.tv_title.getText().toString() + " Setting Change?",
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
                Debug.loge(new Exception(),"K-NETZ onClick ddialog : "+ddialog);
//                ddialog.setOnDismissListener(dialogOnDismiss);
                ddialog.show();
                break;

            case R.id.gridview_sub_button : // Cancelation
                ddialog = new CustomDialog(context,
                        Variables.DIALOG_TYPE_BUTTON,
                        mContext.getString(R.string.dialog_title),
                        vh.tv_title.getText().toString() + " OFF" + " Setting?",
                        1,
                        confirmOnClickListener,
                        cancelOnClickListener);

                ddialog.show();
                break;
        }
    }


    View.OnClickListener confirmOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DataType dataType;
            String str;
            int list = 0;

            switch (ddialog.getnDialogType()){
                case Variables.DIALOG_TYPE_SPINNER:

                    if(ddialog.getContentString().contains("TDD Mode")) list = R.array.set_tddmode;
                    else if(ddialog.getContentString().contains("UL-DL Configuration")) list = R.array.set_tddconf;

                    String[] arrayList = mContext.getResources().getStringArray(list);
                    str = arrayList[(int)ddialog.getCurrentSpinnerValue()];
//                    str = String.valueOf(ddialog.getCurrentSpinnerValue());
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
                case Variables.DIALOG_TYPE_INPUT:
                    dataType = new DataType(gVh.tv_title.getText().toString(),String.valueOf(ddialog.getEditTextValue()),getAdapterId());
                    mListener.onFinishInputDialog(dataType);
                    break;
                case Variables.DIALOG_TYPE_BUTTON:
                    str = "1";
                    dataType = new DataType(gVh.tv_title.getText().toString(),str,getAdapterId());
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
            case Variables.DIALOG_TYPE_BUTTON:
                break;
        }
        ddialog.dismiss();
        }
    };


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//        Debug.logd(new Exception(), "spinner tsync onitem: " + position);

        ViewHolder vh = (ViewHolder) parent.getTag();
        gVh = vh;


        // spinner를 2개 쓰기 때문에?? event 중복 발생??
        if(!Variables.HIDDEN_ENABLED) {
            if(isFirstBoot){
//                Debug.logd(new Exception(), "spinner tsync return ");
                isFirstBoot = false;
                return;
            }
        }

        if (selectFlag) {
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
                } else {
                    ddialog = new CustomDialog(context,
                            Variables.DIALOG_TYPE_SPINNER,
                            mContext.getString(R.string.dialog_title),
                            vh.tv_title.getText().toString() + " " + parent.getItemAtPosition(position).toString() + " Setting?",
                            position,
                            confirmOnClickListener,
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
//        Debug.logd(new Exception(), "spinner tsync nothing");
    }
}
