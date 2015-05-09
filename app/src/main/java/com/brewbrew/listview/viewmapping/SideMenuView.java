package com.brewbrew.listview.viewmapping;

import com.brewbrew.listview.adapter.BaseView;
import com.brewbrew.listview.domain.SideMenu;
import com.brewbrew.listview.viewholder.SideMenuViewHolder;

public class SideMenuView extends BaseView<SideMenuViewHolder, SideMenu> {
	public SideMenuView(SideMenu mobile, int layoutId) {
		super(mobile, layoutId);
		this.viewHolder = new SideMenuViewHolder();
	}

	@Override
	public void mappingData(SideMenuViewHolder viewHolder, SideMenu entity) {
		viewHolder.text.setText(entity.name);
		viewHolder.image.setBackgroundResource(entity.image);
	}

}
