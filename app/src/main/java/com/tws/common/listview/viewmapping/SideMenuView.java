package com.tws.common.listview.viewmapping;

import com.tws.common.listview.adapter.BaseView;
import com.tws.common.listview.domain.SideMenu;
import com.tws.common.listview.viewholder.SideMenuViewHolder;

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
