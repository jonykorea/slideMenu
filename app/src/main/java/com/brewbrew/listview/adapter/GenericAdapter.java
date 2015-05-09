package com.brewbrew.listview.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GenericAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Adaptable> items;

	@SuppressWarnings("unchecked")
	public GenericAdapter(List<?> items, Context c) {
		this.items = (List<Adaptable>) items;
		inflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {		
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return items.get(position).buildView(convertView, inflater, parent);	}

}
