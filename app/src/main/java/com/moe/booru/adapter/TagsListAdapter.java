package com.moe.booru.adapter;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.TypedValue;
import android.view.Gravity;

public class TagsListAdapter extends BaseAdapter
{
	private String[] tags;
	public TagsListAdapter(String tags){
		this.tags=tags.split(" ");
	}
	@Override
	public int getCount()
	{
		// TODO: Implement this method
		return tags.length;
	}

	@Override
	public Object getItem(int p1)
	{
		// TODO: Implement this method
		return tags[p1];
	}

	@Override
	public long getItemId(int p1)
	{
		// TODO: Implement this method
		return p1;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		if(p2==null){
			p2=new TextView(p3.getContext());
			p2.setLayoutParams(new ViewGroup.LayoutParams(-1,(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,52,p3.getResources().getDisplayMetrics())));
			((TextView)p2).setGravity(Gravity.CENTER_VERTICAL);
			//p2.setPaddingRelative((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,p3.getResources().getDisplayMetrics()),0,0,0);
			}((TextView)p2).setText(tags[p1]);
		return p2;
	}
	
}
