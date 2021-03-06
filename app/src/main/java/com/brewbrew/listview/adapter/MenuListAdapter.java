package com.brewbrew.listview.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tws.common.lib.utils.TimeUtil;
import com.tws.common.lib.views.CuzToast;
import com.brewbrew.R;
import com.brewbrew.managers.data.Menu;
import com.brewbrew.managers.lib.ConvertData;

import java.util.List;

/**
 * Created by Jony on 2015-01-11.
 */
public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ViewHolder> {


    // Activity 로 데이터를 전달할 커스텀 리스너
    private CuzOnClickListener customListener;

    // Activity 로 데이터를 전달할 커스텀 리스너의 인터페이스
    public interface CuzOnClickListener {
        public void onChangeItem(List<Menu> menu);
    }

    private List<Menu> listMenu;
    private int rowLayout;
    private Context mContext;

    private int mSumCnt = 0;

    private int itemCount = 0;
    //private BitmapPool mPool;
    private CuzToast mCuzToast;

    public MenuListAdapter(List<Menu> listMenu, int rowLayout, Context context, CuzOnClickListener listener) {
        this.listMenu = listMenu;
        this.rowLayout = rowLayout;
        //this.mContext = context;
        this.customListener = listener;
       // this.mPool = Glide.get(mContext).getBitmapPool();
        mCuzToast = new CuzToast(context);





    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);

    }

    public void selectPos(int pos)
    {
        notifyItemChanged(pos);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);

        this.mContext = viewGroup.getContext();

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        final int num = i;

        Menu menu = listMenu.get(i);

        // option S


        if (listMenu.get(num).option != null) {

            int cntOpt = listMenu.get(num).option.size();

            // timesale
            if(listMenu.get(num).evtflag == 1)
            {
                viewHolder.menuTimeSaleLayout.setVisibility(View.VISIBLE);
                viewHolder.menuTimeSaleText.setText("판매가능개수 : "+listMenu.get(num).evtcount);
            }
            else
            {
                viewHolder.menuTimeSaleLayout.setVisibility(View.GONE);
            }

            if (cntOpt == 1) {
                viewHolder.menuCountOpt01.setText(Integer.toString(listMenu.get(num).option.get(0).count));

                viewHolder.menuLayoutOpt01.setVisibility(View.VISIBLE);
                viewHolder.menuLayoutOpt02.setVisibility(View.GONE);
                viewHolder.menuDiv.setVisibility(View.GONE);

                int addPrice = listMenu.get(num).option.get(0).addprice;

                if (addPrice != 0) {
                    viewHolder.menuTextOpt01.setText(listMenu.get(num).option.get(0).name + " +" + addPrice + "원");
                } else {
                    viewHolder.menuTextOpt01.setText(listMenu.get(num).option.get(0).name);
                }

            } else {
                viewHolder.menuCountOpt01.setText(Integer.toString(listMenu.get(num).option.get(0).count));
                viewHolder.menuCountOpt02.setText(Integer.toString(listMenu.get(num).option.get(1).count));

                viewHolder.menuLayoutOpt01.setVisibility(View.VISIBLE);
                viewHolder.menuLayoutOpt02.setVisibility(View.VISIBLE);
                viewHolder.menuDiv.setVisibility(View.VISIBLE);

                int addPrice = listMenu.get(num).option.get(0).addprice;

                if (addPrice != 0) {
                    viewHolder.menuTextOpt01.setText(listMenu.get(num).option.get(0).name + " +" + addPrice + "원");
                } else {
                    viewHolder.menuTextOpt01.setText(listMenu.get(num).option.get(0).name);
                }

                addPrice = listMenu.get(num).option.get(1).addprice;

                if (addPrice != 0) {
                    viewHolder.menuTextOpt02.setText(listMenu.get(num).option.get(1).name + " +" + addPrice + "원");
                } else {
                    viewHolder.menuTextOpt02.setText(listMenu.get(num).option.get(1).name);
                }

            }


        }

        if(menu.evtflag == 1)
        {
            String eventEendTime = TimeUtil.getNewSimpleDateFormat("HH:mm", Long.toString(menu.evtetime));

            String name = menu.name + "("+eventEendTime+"까지 할인)";

            viewHolder.menuName.setText(name);
        }
        else
        {
            viewHolder.menuName.setText(menu.name);
        }


        // price
        int price = menu.price;
        int saleprice = menu.saleprice;

        if (price != saleprice) {

            viewHolder.menuPrice.setTextColor(Color.parseColor("#E52A19"));
            viewHolder.menuPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.menuPrice.setText(ConvertData.getPrice(menu.price));
            viewHolder.menuSalePrice.setVisibility(View.VISIBLE);
            viewHolder.menuSalePrice.setText(ConvertData.getPrice(menu.saleprice));
            viewHolder.menuArrow.setVisibility(View.VISIBLE);

        } else {
            viewHolder.menuPrice.setTextColor(Color.parseColor("#3D454C"));
            viewHolder.menuPrice.setText(ConvertData.getPrice(menu.price));
            viewHolder.menuSalePrice.setVisibility(View.GONE);
            viewHolder.menuArrow.setVisibility(View.GONE);
        }

        viewHolder.menuCommnet.setText(menu.comment);

        viewHolder.menuCommnetWriter.setText(menu.comment_write);

        viewHolder.menuImage.setImageDrawable(null);

/*
        Glide.with(viewHolder.menuImage.getContext()).load(menu.image_thumb)
                //.bitmapTransform(new RoundedCornersTransformation(mPool, 15, 0))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into((viewHolder.menuImage));

*/
        final ImageView imageView = viewHolder.menuImage;
        Glide.with(mContext).load(menu.image_thumb)
                //.bitmapTransform(new RoundedCornersTransformation(Glide.get(viewHolder.menuImage.getContext()).getBitmapPool(), 15, 0))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {

                        imageView.setImageDrawable(resource);

                    }
                });

        final String imageUrl = menu.image;

        viewHolder.menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendSlidingImage(imageUrl);
            }
        });


        final TextView cntViewOpt01 = viewHolder.menuCountOpt01;
        final TextView cntViewOpt02 = viewHolder.menuCountOpt02;

        viewHolder.menuAddOpt01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int cnt = listMenu.get(num).option.get(0).count;

                // time sale
                if(listMenu.get(num).evtflag == 1)
                {
                    int evtcnt = listMenu.get(num).evtcount;

                    if (cnt >= evtcnt)
                    {
                        mCuzToast.showToast(mContext.getString(R.string.time_sale_count_check), Toast.LENGTH_SHORT);
                        return;
                    }
                }


                if (cnt < 10) {
                    if (mSumCnt < 10) {
                        mSumCnt++;
                        cnt++;

                        cntViewOpt01.setText(Integer.toString(cnt));


                        listMenu.get(num).option.get(0).count = cnt;

                        customListener.onChangeItem(listMenu);
                    } else {
                        mCuzToast.showToast(mContext.getString(R.string.max_item), Toast.LENGTH_SHORT);
                    }
                } else {
                    mCuzToast.showToast(mContext.getString(R.string.max_item), Toast.LENGTH_SHORT);

                }


            }
        });

        viewHolder.menuRemoveOpt01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int cnt = listMenu.get(num).option.get(0).count;


                if (cnt > 0) {

                    if (mSumCnt > 0) {
                        mSumCnt--;
                        cnt--;


                        cntViewOpt01.setText(Integer.toString(cnt));

                        listMenu.get(num).option.get(0).count = cnt;

                        customListener.onChangeItem(listMenu);
                    } else {
                    }

                }

            }
        });

        viewHolder.menuAddOpt02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int cnt = listMenu.get(num).option.get(1).count;


                if (cnt < 10) {
                    if (mSumCnt < 10) {
                        mSumCnt++;
                        cnt++;

                        cntViewOpt02.setText(Integer.toString(cnt));

                        listMenu.get(num).option.get(1).count = cnt;

                        customListener.onChangeItem(listMenu);
                    } else {
                        mCuzToast.showToast(mContext.getString(R.string.max_item), Toast.LENGTH_SHORT);
                    }
                } else {
                    mCuzToast.showToast(mContext.getString(R.string.max_item), Toast.LENGTH_SHORT);

                }


            }
        });

        viewHolder.menuRemoveOpt02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int cnt = listMenu.get(num).option.get(1).count;


                if (cnt > 0) {
                    if (mSumCnt > 0) {
                        mSumCnt--;
                        cnt--;

                        cntViewOpt02.setText(Integer.toString(cnt));

                        listMenu.get(num).option.get(1).count = cnt;

                        customListener.onChangeItem(listMenu);
                    } else {

                    }
                }

            }
        });


    }


    @Override
    public int getItemCount() {
        return listMenu == null ? 0 : listMenu.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView menuName;
        public ImageView menuImage;
        public TextView menuPrice;
        public TextView menuSalePrice;
        public TextView menuCommnet;
        public TextView menuCommnetWriter;

        public TextView menuCountOpt01;
        public TextView menuTextOpt01;
        public ImageButton menuAddOpt01;
        public ImageButton menuRemoveOpt01;

        public TextView menuCountOpt02;
        public TextView menuTextOpt02;
        public ImageButton menuAddOpt02;
        public ImageButton menuRemoveOpt02;

        public RelativeLayout menuLayoutOpt01;
        public RelativeLayout menuLayoutOpt02;

        public LinearLayout menuDiv;
        public ImageView menuArrow;

        public RelativeLayout menuTimeSaleLayout;
        public TextView menuTimeSaleText;

        public ViewHolder(View itemView) {
            super(itemView);
            menuName = (TextView) itemView.findViewById(R.id.item_menu_info_name);
            menuPrice = (TextView) itemView.findViewById(R.id.item_menu_info_price);
            menuSalePrice = (TextView) itemView.findViewById(R.id.item_menu_info_saleprice);
            menuImage = (ImageView) itemView.findViewById(R.id.item_menu_info_image);

            menuCommnet = (TextView) itemView.findViewById(R.id.item_menu_info_txt);
            menuCommnetWriter = (TextView) itemView.findViewById(R.id.item_menu_info_txt_writer);

            // option

            menuArrow = (ImageView) itemView.findViewById(R.id.item_menu_info_arrow);
            menuDiv = (LinearLayout) itemView.findViewById(R.id.item_menu_control_div_layout);
            // opt1
            menuLayoutOpt01 = (RelativeLayout) itemView.findViewById(R.id.item_menu_control_opt1_layout);
            menuAddOpt01 = (ImageButton) itemView.findViewById(R.id.item_menu_control_opt1_add_btn);
            menuTextOpt01 = (TextView) itemView.findViewById(R.id.item_menu_control_opt1_txt);
            menuRemoveOpt01 = (ImageButton) itemView.findViewById(R.id.item_menu_control_opt1_remove_btn);
            menuCountOpt01 = (TextView) itemView.findViewById(R.id.item_menu_control_opt1_count);

            // opt2
            menuLayoutOpt02 = (RelativeLayout) itemView.findViewById(R.id.item_menu_control_opt2_layout);
            menuAddOpt02 = (ImageButton) itemView.findViewById(R.id.item_menu_control_opt2_add_btn);
            menuTextOpt02 = (TextView) itemView.findViewById(R.id.item_menu_control_opt2_txt);
            menuRemoveOpt02 = (ImageButton) itemView.findViewById(R.id.item_menu_control_opt2_remove_btn);
            menuCountOpt02 = (TextView) itemView.findViewById(R.id.item_menu_control_opt2_count);

            // timesale
            menuTimeSaleLayout = (RelativeLayout) itemView.findViewById(R.id.item_menu_control_timesale_layout);
            menuTimeSaleText = (TextView) itemView.findViewById(R.id.item_menu_control_timesale_txt);

        }

    }

    private void sendSlidingImage(String url) {
        Intent intentSliding = new Intent("image_url");

        intentSliding.putExtra("url", url);

        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intentSliding);
    }
}
