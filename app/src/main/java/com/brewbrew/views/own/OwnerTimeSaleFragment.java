package com.brewbrew.views.own;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.define.LOG;
import com.brewbrew.R;
import com.brewbrew.activities.base.BaseFragment;
import com.brewbrew.managers.data.Menu;
import com.brewbrew.managers.pref.PrefOrderInfo;
import com.brewbrew.managers.pref.PrefUserInfo;
import com.brewbrew.network.data.RetCode;
import com.brewbrew.network.data.RetMenuList;
import com.brewbrew.network.data.RetOrderMenu;
import com.brewbrew.network.data.ServerDefineCode;
import com.brewbrew.network.lib.ApiAgent;
import com.brewbrew.views.SoulBrownMainActivity;
import com.brewbrew.views.viewpager.ViewPagerAdapter;
import com.tws.common.lib.dialog.CuzDialog;
import com.tws.common.lib.utils.TimeUtil;
import com.tws.common.lib.views.CuzToast;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.List;

public class OwnerTimeSaleFragment extends BaseFragment {

    private CuzToast mCuzToast;
    private EditText mSalePrice;
    private TextView mSaleCount;
    private long mSaleStartTime;
    private long mSaleEndTime;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCuzToast = new CuzToast(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_timesale, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSalePrice = (EditText) view.findViewById(R.id.sale_price_edt);
        mSaleCount = (TextView) view.findViewById(R.id.item_count_txt);

        ImageButton imgBtnCountAdd = (ImageButton) view.findViewById(R.id.item_count_add_btn);
        ImageButton imgBtnCountRemove = (ImageButton) view.findViewById(R.id.item_count_remove_btn);

        imgBtnCountAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int evtcount = Integer.parseInt(mSaleCount.getText().toString());

                evtcount++;

                mSaleCount.setText(Integer.toString(evtcount));

            }
        });

        imgBtnCountRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int evtcount = Integer.parseInt(mSaleCount.getText().toString());

                if (evtcount != 0) {
                    evtcount--;
                    mSaleCount.setText(Integer.toString(evtcount));
                }


            }
        });

        // 시작 / 종료 시간.
        // 10분 단위.
        final int TEN_MIN = 60 * 10;
        final int ONE_HOUR = 60 * 60;

        final long nowUnixTime = System.currentTimeMillis() / 1000L;

        String nowTime = TimeUtil.getNewSimpleDateFormat("HH mm", Long.toString(nowUnixTime));

        mSaleStartTime = nowUnixTime;
        mSaleEndTime = nowUnixTime + ONE_HOUR;

        // 현재 시간.
        final TextView tvStartHour = (TextView) view.findViewById(R.id.time_start_hour_txt);
        final TextView tvStartMin= (TextView) view.findViewById(R.id.time_start_min_txt);

        setTimeView(mSaleStartTime,tvStartHour, tvStartMin );

        // 종료시간
        final TextView tvEndHour = (TextView) view.findViewById(R.id.time_end_hour_txt);
        final TextView tvEndMin= (TextView) view.findViewById(R.id.time_end_min_txt);

        setTimeView(mSaleEndTime, tvEndHour, tvEndMin);

        ImageButton imgBtnStartAdd= (ImageButton) view.findViewById(R.id.time_start_add_btn);
        ImageButton imgBtnStartRemove= (ImageButton) view.findViewById(R.id.time_start_remove_btn);


        imgBtnStartAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSaleStartTime = mSaleStartTime + TEN_MIN;

                setTimeView(mSaleStartTime, tvStartHour, tvStartMin);

                if( mSaleStartTime + ONE_HOUR > mSaleEndTime)
                {
                    mSaleEndTime =  mSaleStartTime + ONE_HOUR;

                    setTimeView(mSaleEndTime, tvEndHour, tvEndMin);
                }
            }
        });

        imgBtnStartRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long tmpTime = mSaleStartTime - TEN_MIN;
                if (nowUnixTime <= tmpTime) {
                    mSaleStartTime = tmpTime;
                    setTimeView(mSaleStartTime, tvStartHour, tvStartMin);
                }
            }
        });





        ImageButton imgBtnEndAdd= (ImageButton) view.findViewById(R.id.time_end_add_btn);
        ImageButton imgBtnEndRemove= (ImageButton) view.findViewById(R.id.time_end_remove_btn);


        imgBtnEndAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSaleEndTime = mSaleEndTime + TEN_MIN;

                setTimeView(mSaleEndTime, tvEndHour, tvEndMin);
            }
        });

        imgBtnEndRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long tmpTime = mSaleEndTime - TEN_MIN;
                // 시작시간보다 최소 한시간 차이.
                if( mSaleStartTime + ONE_HOUR <= tmpTime )
                {
                    mSaleEndTime = tmpTime;
                    setTimeView(mSaleEndTime, tvEndHour, tvEndMin);
                }
            }
        });




        long EndUnixTime = System.currentTimeMillis() / 1000L;

        EndUnixTime = EndUnixTime + TEN_MIN;

        //mSaleStartTime = startUnixTime;
        //mSaleEndTime = EndUnixTime;



       // Log.i("jony", "startUnixTime : "+ startUnixTime+" EndUnixTime : "+ EndUnixTime);

        Button confirmBtn = (Button) view.findViewById(R.id.confirm_btn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String price = mSalePrice.getText().toString();
                int saleprice = 0;
                if( !TextUtils.isEmpty(price) )
                {
                    saleprice = Integer.parseInt(mSalePrice.getText().toString());
                }

                int evtcount = Integer.parseInt( mSaleCount.getText().toString());
                long evtstime = mSaleStartTime;
                long evtetime = mSaleEndTime;

                if( saleprice == 0)
                {
                    mCuzToast.showToast(getString(R.string.time_sale_price_error), Toast.LENGTH_SHORT);
                    return;
                }

                if( evtcount == 0)
                {
                    mCuzToast.showToast( getString(R.string.time_sale_count_error),Toast.LENGTH_SHORT);
                    return;
                }

                // 시작 시간


                // 종료 시간
                apiOrderMenu(saleprice, evtcount, evtstime, evtetime);



            }
        });

    }

    private void setTimeView(long time, TextView tvHour, TextView tvMin)
    {
        String nowTime = TimeUtil.getNewSimpleDateFormat("HH mm", Long.toString(time));

        String[] arrayStartTime = nowTime.split(" ");

        // 현재 시간.
        tvHour.setText(arrayStartTime[0]);
        tvMin.setText(arrayStartTime[1]);
    }

    public void apiOrderMenu(int saleprice,int evtcount,long evtstime, long evtetime ) {

        ApiAgent api = new ApiAgent();

        if (api != null) {

            String store = "PGA010002";
            String code = "PGA010002M0011";
            String name = "타임세일샌드위치";
            int price = 3300;

            if( !mBaseProgressDialog.isShowing() )
                mBaseProgressDialog.show();

            api.apiRegMenuEvent(getActivity(), store, code, name,
             evtstime,  evtetime,  evtcount, price, saleprice, new Response.Listener<RetCode>() {
                @Override
                public void onResponse(RetCode retCode) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("retCode.result : " + retCode.ret);
                    LOG.d("retCode.errormsg : " + retCode.msg);


                    if (retCode.ret == ServerDefineCode.NET_RESULT_SUCC) {

                        // success
                        LOG.d("apiRegMenuEvent Succ");

                        if( mBaseDialog == null || !mBaseDialog.isShowing()) {
                            mBaseDialog = new CuzDialog(getActivity(),
                                    getString(R.string.confirm),getString(R.string.time_sale_succ));

                            mBaseDialog.show();

                            mBaseDialog.setCancelable(true);

                            mBaseDialog.getButtonAccept().setText(getString(R.string.confirm));
                            mBaseDialog.getButtonCancel().setVisibility(View.INVISIBLE);
                        }
                    }
                    else
                    {
                        // fail
                        LOG.d("apiRegMenuEvent Fail " + retCode.ret);

                        mCuzToast.showToast(getString(R.string.order_fail) + " : " + retCode.msg + "[" + retCode.ret + "]", Toast.LENGTH_SHORT);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("apiRegMenuEvent VolleyError " + volleyError.getMessage());

                    mCuzToast.showToast( getString(R.string.network_fail),Toast.LENGTH_SHORT);

                }
            });
        }
    }


}
