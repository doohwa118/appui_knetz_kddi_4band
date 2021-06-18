package kr.knetz.kddi.app.v.t;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

import android.widget.Toast;
import kr.knetz.kddi.app.R;
import kr.knetz.kddi.app.v.x.Variables;


public class StatusGridAdapter extends GridAdapter {

    public StatusGridAdapter() {
        super();
    }

    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
    LinearLayout.LayoutParams textViewHeight = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

    public StatusGridAdapter(Context context, int layout, ArrayList<ItemList> list) {
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
        //Debug.logd(new Exception(), "status getView position : " + position);
        if (layout == R.id.gridview_body_sub1){
            if (position == 0) {
                viewHolder.tv_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Debug.logv(new Exception(), "Hidden status title : " + viewHolder.tv_title.getText());
//                        Debug.logv(new Exception(), "Hidden value  : " + Variables.HIDDEN_ENABLED);
//                        Debug.logv(new Exception(), "Hidden count  : " + Variables.HIDDEN_ENABLED_COUNT);
                        ++Variables.HIDDEN_ENABLED_COUNT;
                        if (Variables.HIDDEN_ENABLED_COUNT > 6) {
                            Variables.HIDDEN_ENABLED = !Variables.HIDDEN_ENABLED;
                            Variables.HIDDEN_ENABLED_COUNT = 0;
                            Toast.makeText(context, Variables.HIDDEN_ENABLED ? "히든 메뉴가 비활성화 되었습니다." : "히든 메뉴가 활성화 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        setStatusDefaultIcon();
        textViewHeight.height = height;
        viewHolder.ll.setLayoutParams(textViewHeight);
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
