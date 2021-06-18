package kr.knetz.kddi.app.v;

import kr.knetz.kddi.app.R;
import kr.knetz.kddi.app.l.Debug;
import kr.knetz.kddi.app.v.h.ExpandableView;
import kr.knetz.kddi.app.v.t.AlarmGridAdapter;
import kr.knetz.kddi.app.v.c.DataType;
import kr.knetz.kddi.app.v.t.ItemList;
import kr.knetz.kddi.app.v.x.Variables;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressLint("ValidFragment")
public class Tab1_Alarm extends Fragment implements OnClickListener {

    Context mContext;
    View mView;

    private ProgressDialog mProgressDialog;
    private boolean isFirstBoot = true;

    private ArrayList<ItemList>[] list;
    private ItemList mAlarmList;

    private ExpandableView[] arrGridViewList;
    private AlarmGridAdapter[] arrAlarmGridAdapter;
    private Handler mHandler;

    int system, psu, dlDspRf, ulDspRf, dlAmp, ulAmp, serviceFa, common;

    static int[] arrTextViewSubTitle;
    static int[] arrGridViewItemList;
    int arrSize;
/*
    static final int[] arrTextViewSubTitle_sub6 = {
            R.id.tv_sub_title1,
            R.id.tv_sub_title2,
            R.id.tv_sub_title3,
            R.id.tv_sub_title4
    };

    static final int[] arrGridViewItemList_sub6 = {
            R.id.gridview_body_sub1,
            R.id.gridview_body_sub2,
            R.id.gridview_body_sub3,
            R.id.gridview_body_sub4
    };
*/
    static final int[] arrTextViewSubTitle_kddi_qn = {
            R.id.tv_sub_title1,
            R.id.tv_sub_title3,
            R.id.tv_sub_title4
    };

    static final int[] arrGridViewItemList_kddi_qn = {
            R.id.gridview_body_sub1,
            R.id.gridview_body_sub3,
            R.id.gridview_body_sub4
    };

    public Tab1_Alarm(Context context) {
        mContext = context;
    }

    public Tab1_Alarm() {
        super();
    }

//    private Handler confirmHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            //완료 후 실행할 처리 삽입
//            //Debug.loge(new Exception(),"Knetz ====handleMessage : "+msg.what);
//            doRefreshGridViewList();
//        }
//    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Debug.logi(new Exception(),"=dhjung=======> Tab1_Alarm onCreate");

        if(Variables.SYSTEM_R_TYPE == Variables.SYSTEM_R_TYPE_KDDI_QN) {
            arrSize = 3;
            list = new ArrayList[arrSize];
            arrTextViewSubTitle = new int[arrSize];
            arrGridViewItemList = new int[arrSize];

            arrTextViewSubTitle = Arrays.copyOf(arrTextViewSubTitle_kddi_qn, arrTextViewSubTitle_kddi_qn.length);
            arrGridViewItemList = Arrays.copyOf(arrGridViewItemList_kddi_qn, arrGridViewItemList_kddi_qn.length);

            system = 0; dlDspRf = 1; ulDspRf = 2;
        }
//        else if(Variables.SYSTEM_R_TYPE == Variables.SYSTEM_R_TYPE_SUB6_5G) {
//            arrSize = 4;
//            list = new ArrayList[arrSize];
//            arrTextViewSubTitle = new int[arrSize];
//            arrGridViewItemList = new int[arrSize];
//
//            arrTextViewSubTitle = Arrays.copyOf(arrTextViewSubTitle_sub6, arrTextViewSubTitle_sub6.length);
//            arrGridViewItemList = Arrays.copyOf(arrGridViewItemList_sub6, arrGridViewItemList_sub6.length);
//        }

        dataToArrayList();
        arrGridViewList = new ExpandableView[arrGridViewItemList.length];
        arrAlarmGridAdapter = new AlarmGridAdapter[arrGridViewItemList.length];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Debug.logi(new Exception(),"=dhjung=======> onCreateView");

        int layout = R.layout.activity_tab1_alarm;

        if(Variables.SYSTEM_R_TYPE == Variables.SYSTEM_R_TYPE_KDDI_QN) {
            layout = R.layout.activity_tab1_alarm_kddi_qn;
        }
//        else if(Variables.SYSTEM_R_TYPE == Variables.SYSTEM_R_TYPE_SUB6_5G) {
//            layout = R.layout.activity_tab1_alarm_sub6;
//        }

        View view = inflater.inflate(layout, container, false);
        mView = view;

        for (int tvId : arrTextViewSubTitle) {
            TextView tv = view.findViewById(tvId);
            tv.setOnClickListener(this);
        }

        int gridCount = 0;
        for (int gridId : arrGridViewItemList) {
            //Debug.loge(new Exception(),"gridCount : "+gridCount);
            ExpandableView gv = view.findViewById(gridId);
            AlarmGridAdapter adapter = new AlarmGridAdapter(view.getContext(), gridId, list[gridCount]);
            gv.setAdapter(adapter);
//            if (gridCount != 0)
            gv.setNumColumns(1);
            gv.setExpanded(true);
            arrAlarmGridAdapter[gridCount] = adapter;
            arrGridViewList[gridCount] = gv;
            gridCount++;
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
//        Debug.loge(new Exception(),"======Knetz======Tab1_Alarm onPause");
        // timer stop
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Debug.loge(new Exception(), "===Knetz=====================onResume START ");
        doRefreshGridViewList();
        //Debug.loge(new Exception(), "===Knetz=====================onResume END ");
        // timer setting
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, Variables.REFRESH_TIMEOUT_MILLIS);
    }

    public void doRefreshGridViewList() {
//        Debug.logi(new Exception(),"======================================KnetzH");
        dataToArrayList();
        int gridCount = 0;
        for (int gridId : arrGridViewItemList) {
            arrAlarmGridAdapter[gridCount] = new AlarmGridAdapter(mView.getContext(), gridId, list[gridCount]);
            arrGridViewList[gridCount].setAdapter(arrAlarmGridAdapter[gridCount]);
            gridCount++;
        }
        //Debug.loge(new Exception(), "===Knetz=====================doRefreshGridViewList");
    }

    @Override
    public void onClick(View v) {
        int count = 0;
        for (int tvId : arrTextViewSubTitle) {
            if (v.getId() == tvId) {
                if (arrGridViewList[count].isShown())
                    arrGridViewList[count].setVisibility(View.GONE);
                else arrGridViewList[count].setVisibility(View.VISIBLE);
            }
            count++;
        }
    }

    private void dataToArrayList() {

        DataType[] systemInfo = MainActivity.subTitleNameClass.getAlarmSubTitle().getSystemInformationSubTitle();
//        DataType[] psuInfo = MainActivity.subTitleNameClass.getAlarmSubTitle().getPsuSubTitle();
        DataType[] downDspRfInfo = MainActivity.subTitleNameClass.getAlarmSubTitle().getDownlinkDspRfSubTitle();
        DataType[] upDspRfInfo = MainActivity.subTitleNameClass.getAlarmSubTitle().getUplinkDspRfSubTitle();
//        DataType[] downAmpInfo = MainActivity.subTitleNameClass.getAlarmSubTitle().getDownlinkAmpSubTitle();
//        DataType[] upAmpInfo = MainActivity.subTitleNameClass.getAlarmSubTitle().getUplinkAmpSubTitle();

        for (int i = 0; i < arrGridViewItemList.length; i++) {
            list[i] = new ArrayList<ItemList>();
        }

        for (int i = 0; i < systemInfo.length; i++) {
//            if(Variables.band == 0) {
//                if((systemInfo[i].getId() != Variables.DB_ALARM_TSYNC_LINK2) && (systemInfo[i].getId() != Variables.DB_ALARM_TSYNC_LOCK2)){
//                    mAlarmList = new ItemList(systemInfo[i].getName(), systemInfo[i].getValue());
//                    list[system].add(mAlarmList);
//                }
//            }
//            else if(Variables.band == 1){
//                if((systemInfo[i].getId() != Variables.DB_ALARM_TSYNC_LINK1) && (systemInfo[i].getId() != Variables.DB_ALARM_TSYNC_LOCK1)){
//                    mAlarmList = new ItemList(systemInfo[i].getName(), systemInfo[i].getValue());
//                    list[system].add(mAlarmList);
//                }
//            }
            mAlarmList = new ItemList(systemInfo[i].getName(), systemInfo[i].getValue());
//            Debug.loge(new Exception(),systemInfo[i].getName()+" : "+systemInfo[i].getValue());
            list[system].add(mAlarmList);
        }
//        for (int i = 0; i < psuInfo.length; i++) {
//            mAlarmList = new ItemList(psuInfo[i].getName(), psuInfo[i].getValue());
//            Debug.loge(new Exception(),psuInfo[i].getName()+" : "+psuInfo[i].getValue());
//            list[psu].add(mAlarmList);
//        }
        for (int i = 0; i < downDspRfInfo.length; i++) {
            mAlarmList = new ItemList(downDspRfInfo[i].getName(), downDspRfInfo[i].getValue());
//            Debug.loge(new Exception(),downDspRfInfo[i].getName()+" : "+downDspRfInfo[i].getValue());
            list[dlDspRf].add(mAlarmList);
        }
        for (int i = 0; i < upDspRfInfo.length; i++) {
            mAlarmList = new ItemList(upDspRfInfo[i].getName(), upDspRfInfo[i].getValue());
//            Debug.loge(new Exception(),upDspRfInfo[i].getName()+" : "+upDspRfInfo[i].getValue());
            list[ulDspRf].add(mAlarmList);
        }
//        for (int i = 0; i < downAmpInfo.length; i++) {
//            mAlarmList = new ItemList(downAmpInfo[i].getName(), downAmpInfo[i].getValue());
//            Debug.loge(new Exception(),downAmpInfo[i].getName()+" : "+downAmpInfo[i].getValue());
//            list[dlAmp].add(mAlarmList);
//        }
//        for (int i = 0; i < upAmpInfo.length; i++) {
//            mAlarmList = new ItemList(upAmpInfo[i].getName(), upAmpInfo[i].getValue());
//            Debug.loge(new Exception(),upAmpInfo[i].getName()+" : "+upAmpInfo[i].getValue());
//            list[ulAmp].add(mAlarmList);
//        }
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //Debug.loge(new Exception(), "run");
            doRefreshGridViewList();

            mHandler.postDelayed(mRunnable, Variables.REFRESH_TIMEOUT_MILLIS);
        }
    };
}