package com.brewbrew.listview.viewmapping;

import com.brewbrew.listview.adapter.BaseView;
import com.brewbrew.listview.domain.OrderList;
import com.brewbrew.listview.viewholder.OrderListViewHolder;

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
