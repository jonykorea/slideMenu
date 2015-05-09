package com.brewbrew.listview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface Adaptable {
	public View buildView(View v, LayoutInflater inflater, ViewGroup parent);
}
