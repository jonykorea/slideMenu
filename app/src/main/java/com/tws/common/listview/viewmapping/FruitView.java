package com.tws.common.listview.viewmapping;

import com.tws.common.listview.adapter.BaseView;
import com.tws.common.listview.domain.Fruit;
import com.tws.common.listview.viewholder.FruitViewHolder;

public class FruitView extends BaseView<FruitViewHolder, Fruit> {

	public FruitView(Fruit entity, int layoutId){
		super(entity, layoutId);
		this.viewHolder = new FruitViewHolder();
	}
	@Override
	protected void mappingData(FruitViewHolder viewHolder, Fruit entity) {
		viewHolder.txtFruit.setText(entity.fruitName);		
	}

}
