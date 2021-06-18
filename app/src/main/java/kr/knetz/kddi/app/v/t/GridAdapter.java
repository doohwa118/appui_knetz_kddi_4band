package kr.knetz.kddi.app.v.t;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import kr.knetz.kddi.app.R;
import kr.knetz.kddi.app.v.x.Variables;


public abstract class GridAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<ItemList> listSrc;
    int layout;
    public ViewHolder viewHolder;
    float textSize;
    public GridAdapter(Context context, int layout, ArrayList<ItemList> list) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listSrc = list;
        this.layout = layout;
        this.context = context;
        textSize = Variables.TEXT_SIZE;
    }

    public GridAdapter() {
        super();
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getCount() {
        return listSrc.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null){
            view = inflater.inflate(R.layout.gridview_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ll = view.findViewById(R.id.row_gridview_layout);
            viewHolder.tv_title = view.findViewById(R.id.gridview_sub_title);
            viewHolder.tv_value = view.findViewById(R.id.gridview_sub_value);
            viewHolder.tv_value_spinner = view.findViewById(R.id.gridview_sub_value_spinner);
            viewHolder.iv_icon_mini = view.findViewById(R.id.gridview_sub_icon);
            viewHolder.iv_icon_center = view.findViewById(R.id.gridview_center_icon);
            viewHolder.ll_swc = view.findViewById(R.id.ll_swc);
            viewHolder.sw_onoff = view.findViewById(R.id.gridview_sub_sw);
            viewHolder.ll_spinner = view.findViewById(R.id.ll_spinner);
            viewHolder.spinner = view.findViewById(R.id.gridview_sub_spinner);
            viewHolder.ll_btn = view.findViewById(R.id.ll_button);
            viewHolder.button = view.findViewById(R.id.gridview_sub_button);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.tv_value.setTextSize(TypedValue.COMPLEX_UNIT_DIP,textSize);
        viewHolder.tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,textSize);
        viewHolder.tv_title.setText(listSrc.get(position).getName());
        viewHolder.tv_value.setText(listSrc.get(position).getValue());

        viewHolder.tv_value_spinner.setTextSize(TypedValue.COMPLEX_UNIT_DIP,textSize);
        viewHolder.tv_value_spinner.setText(listSrc.get(position).getValue());

        if (((position / 2) % 2) == 0) viewHolder.ll.setBackgroundResource(R.drawable.grieview_style_odd);
//        else viewHolder.ll.setBackgroundResource(R.drawable.grieview_style_even);
        else viewHolder.ll.setBackgroundResource(R.drawable.grieview_style_odd);

        return view;
    }

    @Override
    public Object getItem(int position) {
        return listSrc.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSystemInformationIcon(){
        viewHolder.tv_value.setVisibility(View.VISIBLE);
        viewHolder.iv_icon_center.setVisibility(View.GONE);
        viewHolder.iv_icon_mini.setVisibility(View.GONE);
    }

    public void setAlarmSystemInfoIcon(){
        viewHolder.iv_icon_center.setVisibility(View.VISIBLE);
        viewHolder.tv_value.setVisibility(View.GONE);
        viewHolder.iv_icon_mini.setVisibility(View.GONE);
    }

    public void setAlarmPsuIcon(){
        viewHolder.iv_icon_center.setVisibility(View.VISIBLE);
        viewHolder.tv_value.setVisibility(View.GONE);
        viewHolder.iv_icon_mini.setVisibility(View.GONE);
    }

    public void setAlarmDownlinkUplinkIcon(){
        viewHolder.iv_icon_center.setVisibility(View.VISIBLE);
        viewHolder.tv_value.setVisibility(View.GONE);
        viewHolder.iv_icon_mini.setVisibility(View.GONE);
    }

    public void setStatusDefaultIcon(){
        viewHolder.tv_value.setVisibility(View.VISIBLE);
        viewHolder.iv_icon_center.setVisibility(View.GONE);
        viewHolder.iv_icon_mini.setVisibility(View.GONE);
    }

    // on off switch
    public abstract void onCheckedChanged(CompoundButton buttonView, boolean isChecked);

    public static class ViewHolder {
        public int position;
        public int value;
        public LinearLayout ll;
        public TextView tv_title;
        public TextView tv_value;
        public TextView tv_value_spinner;
        public ImageView iv_icon_mini;
        public ImageView iv_icon_center;
        public LinearLayout ll_swc;
        public Switch sw_onoff;
        public LinearLayout ll_spinner;
        public Spinner spinner;
        public LinearLayout ll_btn;
        public Button button;
    }
}
