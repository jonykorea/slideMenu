package com.tws.common.listview.viewmapping;

import com.tws.common.listview.adapter.BaseView;
import com.tws.common.listview.domain.Nations;
import com.tws.common.listview.viewholder.NationsViewHolder;

public class NationsView extends BaseView<NationsViewHolder, Nations> {
	public NationsView(Nations mobile, int layoutId) {
		super(mobile, layoutId);
		this.viewHolder = new NationsViewHolder();
	}

	@Override
	public void mappingData(NationsViewHolder viewHolder, Nations entity) {
		viewHolder.text.setText(entity.name);
		viewHolder.image.setBackgroundResource(entity.image);
	}

}
