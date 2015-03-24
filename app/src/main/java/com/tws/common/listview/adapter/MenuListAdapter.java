package com.tws.common.listview.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tws.network.data.ArrayOptionData;
import com.tws.soul.soulbrown.R;
import com.tws.soul.soulbrown.data.Menu;
import com.tws.soul.soulbrown.lib.ConvertData;

import java.util.List;
/**
 * Created by Jony on 2015-01-11.
 */
public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ViewHolder>{


    // Activity 로 데이터를 전달할 커스텀 리스너
    private CuzOnClickListener customListener;

    // Activity 로 데이터를 전달할 커스텀 리스너의 인터페이스
    public interface CuzOnClickListener{
        public void onChangeItem(List<Menu> menu);
    }

    private List<Menu> listMenu;
    private int rowLayout;
    private Context mContext;

    private int itemCount = 0;

    public MenuListAdapter(List<Menu> listMenu, int rowLayout, Context context, CuzOnClickListener listener) {
        this.listMenu = listMenu;
        this.rowLayout = rowLayout;
        this.mContext = context;
        this.customListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        final int num = i;

        Menu menu = listMenu.get(i);

        // option S


        if( listMenu.get(num).option != null) {

            int cntOpt = listMenu.get(num).option.size();

            if( cntOpt == 1)
            {
                viewHolder.menuCountOpt01.setText(Integer.toString(listMenu.get(num).option.get(0).count));

                viewHolder.menuLayoutOpt01.setVisibility(View.VISIBLE);
                viewHolder.menuLayoutOpt02.setVisibility(View.GONE);
                viewHolder.menuDiv.setVisibility(View.GONE);

                viewHolder.menuTextOpt01.setText(listMenu.get(num).option.get(0).name + " : +"+listMenu.get(num).option.get(0).addprice+"원");
            }
            else
            {
                viewHolder.menuCountOpt01.setText(Integer.toString(listMenu.get(num).option.get(0).count));
                viewHolder.menuCountOpt02.setText(Integer.toString(listMenu.get(num).option.get(1).count));

                viewHolder.menuLayoutOpt01.setVisibility(View.VISIBLE);
                viewHolder.menuLayoutOpt02.setVisibility(View.VISIBLE);
                viewHolder.menuDiv.setVisibility(View.VISIBLE);

                viewHolder.menuTextOpt01.setText(listMenu.get(num).option.get(0).name + " : +"+listMenu.get(num).option.get(0).addprice+"원");
                viewHolder.menuTextOpt02.setText(listMenu.get(num).option.get(1).name + " : +"+listMenu.get(num).option.get(1).addprice+"원");
            }

        }


        viewHolder.menuName.setText(menu.name);

        // price
        int price = menu.price;
        int saleprice = menu.saleprice;

        if( price != saleprice)
        {

            viewHolder.menuPrice.setTextColor(Color.parseColor("#E52A19"));
            viewHolder.menuPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.menuPrice.setText(ConvertData.getPrice(menu.price));
            viewHolder.menuSalePrice.setVisibility(View.VISIBLE);
            viewHolder.menuSalePrice.setText(ConvertData.getPrice(menu.saleprice));
            viewHolder.menuArrow.setVisibility(View.VISIBLE);

        }
        else {
            viewHolder.menuPrice.setTextColor(Color.parseColor("#3D454C"));
            viewHolder.menuPrice.setText(ConvertData.getPrice(menu.price));
            viewHolder.menuSalePrice.setVisibility(View.GONE);
            viewHolder.menuArrow.setVisibility(View.GONE);
        }

        Glide.with(mContext).load(menu.image).into(viewHolder.menuImage);

        final TextView cntViewOpt01 = viewHolder.menuCountOpt01;
        final TextView cntViewOpt02 = viewHolder.menuCountOpt02;

        viewHolder.menuAddOpt01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int cnt = listMenu.get(num).option.get(0).count;

                if(cnt < 10) {
                    cnt++;

                    cntViewOpt01.setText(Integer.toString(cnt));

                    listMenu.get(num).option.get(0).count = cnt;

                    customListener.onChangeItem(listMenu);
                }

                Log.i("jony"," onClick menuAdd ");

            }
        });

        viewHolder.menuRemoveOpt01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int cnt = listMenu.get(num).option.get(0).count;

                if(cnt > 0) {
                    cnt--;

                    cntViewOpt01.setText(Integer.toString(cnt));

                    listMenu.get(num).option.get(0).count = cnt;

                    customListener.onChangeItem(listMenu);
                }


                Log.i("jony"," onClick menuRemove ");

            }
        });

        viewHolder.menuAddOpt02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int cnt = listMenu.get(num).option.get(1).count;

                if(cnt < 10) {
                    cnt++;

                    cntViewOpt02.setText(Integer.toString(cnt));

                    listMenu.get(num).option.get(1).count = cnt;

                    customListener.onChangeItem(listMenu);
                }

                Log.i("jony"," onClick menuAdd ");

            }
        });

        viewHolder.menuRemoveOpt02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int cnt = listMenu.get(num).option.get(1).count;

                if(cnt > 0) {
                    cnt--;

                    cntViewOpt02.setText(Integer.toString(cnt));

                    listMenu.get(num).option.get(1).count = cnt;

                    customListener.onChangeItem(listMenu);
                }


                Log.i("jony"," onClick menuRemove ");

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


        public ViewHolder(View itemView) {
            super(itemView);
            menuName = (TextView) itemView.findViewById(R.id.item_menu_info_name);
            menuPrice = (TextView) itemView.findViewById(R.id.item_menu_info_price);
            menuSalePrice = (TextView) itemView.findViewById(R.id.item_menu_info_saleprice);
            menuImage = (ImageView)itemView.findViewById(R.id.item_menu_info_image);

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
        }

    }

}
