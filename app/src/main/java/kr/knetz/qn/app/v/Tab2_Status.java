package kr.knetz.qn.app.v;

import kr.knetz.qn.app.R;
import kr.knetz.qn.app.l.Debug;
import kr.knetz.qn.app.v.h.ExpandableView;
import kr.knetz.qn.app.v.t.StatusGridAdapter;
import kr.knetz.qn.app.v.c.DataType;
import kr.knetz.qn.app.v.t.ItemList;
import kr.knetz.qn.app.v.x.Variables;

import android.annotation.SuppressLint;
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
public class Tab2_Status extends Fragment implements OnClickListener {

    Context mContext;
    View mView;

    private ArrayList<ItemList>[] list;
    private ItemList mStatusList;

    private ExpandableView[] arrGridViewList;
    private StatusGridAdapter[] arrStatusGridAdapter;
    private Handler mHandler;

    int system, psu, dlDspRf, ulDspRf, dlAmp, ulAmp, serviceFa, common;

    static int[] arrTextViewSubTitle;
    static int[] arrGridViewItemList;
    int arrSize;

    static final int[] arrTextViewSubTitle_sub6 = {
            R.id.tv_sub_title1,
            R.id.tv_sub_title3,
            R.id.tv_sub_title4,
            R.id.tv_sub_title5,
            R.id.tv_sub_title6
    };

    static final int[] arrGridViewItemList_sub6 = {
            R.id.gridview_body_sub1,
            R.id.gridview_body_sub3,
            R.id.gridview_body_sub4,
            R.id.gridview_body_sub5,
            R.id.gridview_body_sub6
    };

    public Tab2_Status(Context context) {
        mContext = context;
        Debug.loge(new Exception(), "Tab2_Status !!!!! ");
    }

    public Tab2_Status() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Debug.logi(new Exception(),"=dhjung=======> Tab2_Status onCreate");

        if(Variables.SYSTEM_R_TYPE == Variables.SYSTEM_R_TYPE_SUB6_5G || Variables.SYSTEM_R_TYPE == Variables.SYSTEM_R_TYPE_KDDI_QN) {
            arrSize = 5;
            list = new ArrayList[arrSize];
            arrTextViewSubTitle = new int[arrSize];
            arrGridViewItemList = new int[arrSize];

            arrTextViewSubTitle = Arrays.copyOf(arrTextViewSubTitle_sub6, arrTextViewSubTitle_sub6.length);
            arrGridViewItemList = Arrays.copyOf(arrGridViewItemList_sub6, arrGridViewItemList_sub6.length);

            system = 0; dlDspRf = 1; ulDspRf = 2; dlAmp = 3; ulAmp = 4;
        }
        else {

        }


        dataToArrayList();
        arrGridViewList = new ExpandableView[arrGridViewItemList.length];
        arrStatusGridAdapter = new StatusGridAdapter[arrGridViewItemList.length];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Debug.logi(new Exception(),"=dhjung=======> onCreateView");

        int layout = R.layout.activity_tab2_status;

        if(Variables.SYSTEM_R_TYPE == Variables.SYSTEM_R_TYPE_SUB6_5G || Variables.SYSTEM_R_TYPE == Variables.SYSTEM_R_TYPE_KDDI_QN ) {
            layout = R.layout.activity_tab2_status_sub6;
        }

        View view = inflater.inflate(layout, null);
        mView = view;

        for (int tvId : arrTextViewSubTitle) {
            TextView tv = view.findViewById(tvId);
            tv.setOnClickListener(this);
        }

        int gridCount = 0;
        for (int gridId : arrGridViewItemList) {
//            Debug.loge(new Exception(), "gridId : " + gridId + ", gridCount : " + gridCount);
            ExpandableView gv = view.findViewById(gridId);
            StatusGridAdapter adapter = new StatusGridAdapter(view.getContext(), gridId, list[gridCount]);
//            gv.setAdapter(new StatusGridAdapter(view.getContext(), gridId, list[gridCount]));
            gv.setAdapter(adapter);
            gv.setNumColumns(1);
            gv.setExpanded(true);
            arrStatusGridAdapter[gridCount] = adapter;
            arrGridViewList[gridCount] = gv;
            gridCount++;
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
//        Debug.loge(new Exception(),"======Knetz======Tab2_Status onPause");
        // timer stop
//        Variables.REFRESH_TIMEOUT_MILLIS = 3000;
        mHandler.removeCallbacks(mRunnable);
    }


    @Override
    public void onResume() {
        super.onResume();
//        Debug.logd(new Exception(), "===Knetz=====================onResume START ");
        doRefreshGridViewList();
//        Debug.logd(new Exception(), "===Knetz=====================onResume END ");
        // timer setting
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, Variables.REFRESH_TIMEOUT_MILLIS);
    }

    public void doRefreshGridViewList() {
//        Debug.logi(new Exception(),"======================================KnetzH");
        dataToArrayList();
//        Debug.logd(new Exception(), "===Knetz=====================onResume START 222");
        int gridCount = 0;
        for (int gridId : arrGridViewItemList) {
            arrStatusGridAdapter[gridCount] = new StatusGridAdapter(mView.getContext(), gridId, list[gridCount]);
            arrGridViewList[gridCount].setAdapter(arrStatusGridAdapter[gridCount]);
            gridCount++;
        }
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

        DataType[] systemInfo = MainActivity.subTitleNameClass.getStatusSubTitle().getSystemInformationSubTitle();
        DataType[] psuInfo = MainActivity.subTitleNameClass.getStatusSubTitle().getPsuSubTitle();
        DataType[] downDspRfInfo = MainActivity.subTitleNameClass.getStatusSubTitle().getDownlinkDspRfSubTitle();
        DataType[] upDspRfInfo = MainActivity.subTitleNameClass.getStatusSubTitle().getUplinkDspRfSubTitle();
        DataType[] downAmpInfo = MainActivity.subTitleNameClass.getStatusSubTitle().getDownlinkAmpSubTitle();
        DataType[] upAmpInfo = MainActivity.subTitleNameClass.getStatusSubTitle().getUplinkAmpSubTitle();

        for (int i = 0; i < arrGridViewItemList.length; i++) {
            list[i] = new ArrayList<ItemList>();
        }

        for (int i = 0; i < systemInfo.length; i++) {
            mStatusList = new ItemList(systemInfo[i].getName(), systemInfo[i].getValue());
            list[system].add(mStatusList);
        }
        for (int i = 0; i < psuInfo.length; i++) {
            if (Variables.HIDDEN_ENABLED) {
                boolean flag = false;
                for (int v : Variables.TAB2_PSU_HIDDEN_MENU_ID) {
                    if (psuInfo[i].getId() == v) {
                        flag = true;
                        break;
                    }
                }
//                Debug.logv(new Exception(),"PSU flag : "+flag+ " getId : "+psuInfo[i].getId());
                if (!flag) {
                    mStatusList = new ItemList(psuInfo[i].getName(), psuInfo[i].getValue());
                    list[psu].add(mStatusList);
                }
            }
            else {
                mStatusList = new ItemList(psuInfo[i].getName(), psuInfo[i].getValue());
                list[psu].add(mStatusList);
            }
        }
        for (int i = 0; i < downDspRfInfo.length; i++) {
//            Debug.logi(new Exception(), "getName() : " + downDspRfInfo[i].getName() + " value : " + downDspRfInfo[i].getValue() + "id : " + downDspRfInfo[i].getId());

            if (Variables.HIDDEN_ENABLED) {
                boolean flag = false;
                for (int v : Variables.TAB2_DOWNLINK_HIDDEN_MENU_ID) {
                    if (downDspRfInfo[i].getId() == v) {
                        flag = true;
                        break;
                    }
                }
                if ((Variables.bandStruct[0].sets.tFreqSelect10M15M != 0) || (Variables.band != 0 && Variables.band != 4)){
                    for (int v : Variables.TAB2_DOWNLINK_CDMA_HIDDEN_MENU_ID) {
                        if (downDspRfInfo[i].getId() == v) {
                            flag = true;
                            break;
                        }
                    }
                 }
//                Debug.logv(new Exception(),"Downlink flag : "+flag+ " getId : "+downDspRfInfo[i].getId());
                if (!flag) {
                    mStatusList = new ItemList(downDspRfInfo[i].getName(), downDspRfInfo[i].getValue());
                    list[dlDspRf].add(mStatusList);
                }
            } else {
                boolean flag = false;
                if ((Variables.bandStruct[0].sets.tFreqSelect10M15M != 0) || (Variables.band != 0 && Variables.band != 4)){
                    for (int v : Variables.TAB2_DOWNLINK_CDMA_HIDDEN_MENU_ID) {
                        if (downDspRfInfo[i].getId() == v) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (!flag) {
                    mStatusList = new ItemList(downDspRfInfo[i].getName(), downDspRfInfo[i].getValue());
                    list[dlDspRf].add(mStatusList);
                }
            }
        }
        for (int i = 0; i < upDspRfInfo.length; i++) {
//            Debug.logi(new Exception(), "getName() : " + upDspRfInfo[i].getName() + " value : " + upDspRfInfo[i].getValue() + "id : " + upDspRfInfo[i].getId());
            if (Variables.HIDDEN_ENABLED) {
                boolean flag = false;
                for (int v : Variables.TAB2_UPLINK_HIDDEN_MENU_ID) {
                    if (upDspRfInfo[i].getId() == v) {
                        flag = true;
                        break;
                    }
                }
                if ((Variables.bandStruct[0].sets.tFreqSelect10M15M != 0) || (Variables.band != 0 && Variables.band != 4)){
                    for (int v : Variables.TAB2_UPLINK_CDMA_HIDDEN_MENU_ID) {
                        if (upDspRfInfo[i].getId() == v) {
                            flag = true;
                            break;
                        }
                    }
                }
//                Debug.logv(new Exception(),"Uplink flag : "+flag+ " getId : "+upDspRfInfo[i].getId());
                if (!flag) {
                    mStatusList = new ItemList(upDspRfInfo[i].getName(), upDspRfInfo[i].getValue());
                    list[ulDspRf].add(mStatusList);
                }
            } else {
                boolean flag = false;
                if ((Variables.bandStruct[0].sets.tFreqSelect10M15M != 0) || (Variables.band != 0 && Variables.band != 4)){
                    for (int v : Variables.TAB2_UPLINK_CDMA_HIDDEN_MENU_ID) {
                        if (upDspRfInfo[i].getId() == v) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (!flag) {
                    mStatusList = new ItemList(upDspRfInfo[i].getName(), upDspRfInfo[i].getValue());
                    list[ulDspRf].add(mStatusList);
                }
            }
        }
        for (int i = 0; i < downAmpInfo.length; i++) {
//            Debug.logi(new Exception(), "getName() : " + downAmpInfo[i].getName() + " value : " + downAmpInfo[i].getValue() + "id : " + downAmpInfo[i].getId());
            if (Variables.HIDDEN_ENABLED) {
                boolean flag = false;
//                for (int v : Variables.TAB2_DOWNLINK_HIDDEN_MENU_ID) {
//                    if (downAmpInfo[i].getId() == v) {
//                        flag = true;
//                        break;
//                    }
//                }
                if ((Variables.bandStruct[0].sets.tFreqSelect10M15M != 0) || (Variables.band != 0 && Variables.band != 4)){
                    for (int v : Variables.TAB2_DOWNLINK_AMP_CDMA_HIDDEN_MENU_ID) {
                        if (downAmpInfo[i].getId() == v) {
                            flag = true;
                            break;
                        }
                    }
                }
//                Debug.logv(new Exception(),"Downlink flag : "+flag+ " getId : "+downAmpInfo[i].getId());
                if (!flag) {
                    mStatusList = new ItemList(downAmpInfo[i].getName(), downAmpInfo[i].getValue());
                    list[dlAmp].add(mStatusList);
                }
            } else {
                boolean flag = false;
                if ((Variables.bandStruct[0].sets.tFreqSelect10M15M != 0) || (Variables.band != 0 && Variables.band != 4)){
                    for (int v : Variables.TAB2_DOWNLINK_AMP_CDMA_HIDDEN_MENU_ID) {
                        if (downAmpInfo[i].getId() == v) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (!flag) {
                    mStatusList = new ItemList(downAmpInfo[i].getName(), downAmpInfo[i].getValue());
                    list[dlAmp].add(mStatusList);
                }
            }
        }
        for (int i = 0; i < upAmpInfo.length; i++) {
//            Debug.logi(new Exception(), "getName() : " + upAmpInfo[i].getName() + " value : " + upAmpInfo[i].getValue() + "id : " + upAmpInfo[i].getId());
            if (Variables.HIDDEN_ENABLED) {
                boolean flag = false;
//                for (int v : Variables.TAB2_UPLINK_HIDDEN_MENU_ID) {
//                    if (upAmpInfo[i].getId() == v) {
//                        flag = true;
//                        break;
//                    }
//                }
                if ((Variables.bandStruct[0].sets.tFreqSelect10M15M != 0) || (Variables.band != 0 && Variables.band != 4)){
                    for (int v : Variables.TAB2_UPLINK_AMP_CDMA_HIDDEN_MENU_ID) {
                        if (upAmpInfo[i].getId() == v) {
                            flag = true;
                            break;
                        }
                    }
                }
//                Debug.logv(new Exception(),"Uplink flag : "+flag+ " getId : "+upAmpInfo[i].getId());
                if (!flag) {
                    mStatusList = new ItemList(upAmpInfo[i].getName(), upAmpInfo[i].getValue());
                    list[ulAmp].add(mStatusList);
                }
            } else {
                boolean flag = false;
                if ((Variables.bandStruct[0].sets.tFreqSelect10M15M != 0) || (Variables.band != 0 && Variables.band != 4)){
                    for (int v : Variables.TAB2_UPLINK_AMP_CDMA_HIDDEN_MENU_ID) {
                        if (upAmpInfo[i].getId() == v) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (!flag) {
                    mStatusList = new ItemList(upAmpInfo[i].getName(), upAmpInfo[i].getValue());
                    list[ulAmp].add(mStatusList);
                }
            }
        }
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