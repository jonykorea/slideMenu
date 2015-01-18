package com.tws.common.listview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
        Menu menu = listMenu.get(i);
        viewHolder.menuName.setText(menu.name);
        viewHolder.menuPrice.setText(ConvertData.getPrice(menu.price));
        viewHolder.menuImage.setBackgroundResource(menu.image);
        viewHolder.menuCount.setText(Integer.toString(menu.count));

        final TextView cntView = viewHolder.menuCount;
        final int price = menu.price;
        final String name = menu.name;
        final int num = i;

        viewHolder.menuAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int cnt = listMenu.get(num).count;

                if(cnt < 10) {
                    cnt++;

                    cntView.setText(Integer.toString(cnt));

                    listMenu.get(num).count = cnt;

                    customListener.onChangeItem(listMenu);
                }

                Log.i("jony"," onClick menuAdd ");

            }
        });

        viewHolder.menuRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int cnt = listMenu.get(num).count;

                if(cnt > 0) {
                    cnt--;

                    cntView.setText(Integer.toString(cnt));

                    listMenu.get(num).count = cnt;

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
        public TextView menuCount;
        public ImageButton menuAdd;
        public ImageButton menuRemove;


        public ViewHolder(View itemView) {
            super(itemView);
            menuName = (TextView) itemView.findViewById(R.id.item_menu_info_name);
            menuPrice = (TextView) itemView.findViewById(R.id.item_menu_info_price);
            menuImage = (ImageView)itemView.findViewById(R.id.item_menu_info_image);

            menuAdd = (ImageButton) itemView.findViewById(R.id.item_menu_control_add_btn);
            menuRemove = (ImageButton) itemView.findViewById(R.id.item_menu_control_remove_btn);

            menuCount = (TextView) itemView.findViewById(R.id.item_menu_control_count);
        }

    }

}
