package com.brewbrew.listview.viewholder;


import android.widget.ImageView;
import android.widget.TextView;


import com.brewbrew.listview.view.InvokeView;
import com.brewbrew.R;

public class SideMenuViewHolder {

    @InvokeView(viewId = R.id.menu_name)
    public TextView text;

    @InvokeView(viewId = R.id.menu_img)
    public ImageView image;
}
