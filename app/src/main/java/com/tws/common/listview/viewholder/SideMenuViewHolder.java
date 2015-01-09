package com.tws.common.listview.viewholder;


import android.widget.ImageView;
import android.widget.TextView;


import com.tws.common.listview.view.InvokeView;
import com.tws.soul.soulbrown.R;

public class SideMenuViewHolder {

    @InvokeView(viewId = R.id.menu_name)
    public TextView text;

    @InvokeView(viewId = R.id.menu_img)
    public ImageView image;
}
