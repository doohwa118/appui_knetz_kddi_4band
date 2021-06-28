package kr.knetz.qn.app.v.t;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

import kr.knetz.qn.app.R;


public class AlarmGridAdapter extends GridAdapter {

    public AlarmGridAdapter() {
        super();
    }

    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
    LinearLayout.LayoutParams textViewHeight = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

    public AlarmGridAdapter(Context context, int layout, ArrayList<ItemList> list) {
        super(context, layout, list);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
            case R.id.gridview_body_sub1 : // system information
                setAlarmSystemInfoIcon();
                if (viewHolder.tv_value.getText().equals("0"))
//                    viewHolder.iv_icon_center.setImageResource(R.drawable.alarm_green_64x64);
                    viewHolder.iv_icon_center.setImageResource(R.drawable.icon_body_circle_gr_64x64);
                else
//                    viewHolder.iv_icon_center.setImageResource(R.drawable.alarm_red_64x64);
                    viewHolder.iv_icon_center.setImageResource(R.drawable.icon_body_circle_red_64x64);
                break;
            case R.id.gridview_body_sub2 : // psu
                setAlarmPsuIcon();
                if (viewHolder.tv_value.getText().equals("0"))
//                    viewHolder.iv_icon_center.setImageResource(R.drawable.alarm_green_64x64);
                    viewHolder.iv_icon_center.setImageResource(R.drawable.icon_body_circle_gr_64x64);
                else
//                    viewHolder.iv_icon_center.setImageResource(R.drawable.alarm_red_64x64);
                    viewHolder.iv_icon_center.setImageResource(R.drawable.icon_body_circle_red_64x64);
                break;
            case R.id.gridview_body_sub3 : // downlink dsp&rf
            case R.id.gridview_body_sub4 : // uplink dsp&rf
            case R.id.gridview_body_sub5 : // downlink amp
            case R.id.gridview_body_sub6 : // uplink amp
                setAlarmDownlinkUplinkIcon();
//                if (((position / 2) % 2) == 0) viewHolder.iv_icon_center.setImageResource(R.drawable.alarm_green_64x64);
//                else viewHolder.iv_icon_center.setImageResource(R.drawable.alarm_red_64x64);
                if (viewHolder.tv_value.getText().equals("0")) viewHolder.iv_icon_center.setImageResource(R.drawable.icon_body_circle_gr_64x64);
                else viewHolder.iv_icon_center.setImageResource(R.drawable.icon_body_circle_red_64x64);
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
}
