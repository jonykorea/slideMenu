package com.tws.common.listview.viewmapping;

import com.tws.common.listview.adapter.BaseView;
import com.tws.common.listview.domain.OrderList;
import com.tws.common.listview.viewholder.OrderListViewHolder;

public class OrderListView extends BaseView<OrderListViewHolder, OrderList> {

	public OrderListView(OrderList entity, int layoutId){
		super(entity, layoutId);
		this.viewHolder = new OrderListViewHolder();
	}
	@Override
	protected void mappingData(OrderListViewHolder viewHolder, OrderList entity) {
		viewHolder.txtFruit.setText(entity.fruitName);		
	}

}
