package com.moe.booru.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.moe.booru.database.DataStore;
import java.util.List;
import android.widget.TextView;
import android.util.TypedValue;
import android.view.Gravity;
import com.moe.booru.R;
import android.graphics.drawable.ColorDrawable;
import android.widget.LinearLayout;
import com.moe.booru.widget.SwipeBarLayout;
import android.widget.ImageView;
import android.view.MotionEvent;

public class SiteAdapter extends ClickAdapter<SiteAdapter.ViewHolder>
{
	private List<DataStore.Site> list;
	private int selected=-1;
	private ViewHolder openedPosition;
	public SiteAdapter(List<DataStore.Site> list){
		this.list=list;
	}
	public ViewHolder getOpened(){
		return openedPosition;
	}
	public void close(){
		if(openedPosition!=null)
			openedPosition.close();
			openedPosition=null;
	}
	public int getSelect(){
		return selected;
	}
	public void setSelect(int position){
		selected=position;
		notifyDataSetChanged();
	}
	@Override
	public SiteAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		int height=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,54,p1.getResources().getDisplayMetrics());
		LinearLayout buttonGroup=new LinearLayout(p1.getContext());
		buttonGroup.setBackgroundColor(0x10000000);
		SwipeBarLayout.LayoutParams sl=new SwipeBarLayout.LayoutParams(-2,-1);
		sl.direction=sl.LEFT;
		buttonGroup.setLayoutParams(sl);
		ImageView edit=new ImageView(p1.getContext());
		edit.setLayoutParams(new LinearLayout.LayoutParams(height,height));
		edit.setImageDrawable(edit.getResources().getDrawable(R.drawable.pencil,edit.getContext().getTheme()));
		edit.setBackground(edit.getResources().getDrawable(R.drawable.ripple_circle,edit.getContext().getTheme()));
		edit.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		edit.setId(android.R.id.edit);
		ImageView delete=new ImageView(p1.getContext());
		delete.setLayoutParams(new LinearLayout.LayoutParams(height,height));
		delete.setImageDrawable(edit.getResources().getDrawable(R.drawable.delete,edit.getContext().getTheme()));
		delete.setBackground(edit.getResources().getDrawable(R.drawable.ripple_circle,edit.getContext().getTheme()));
		delete.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		delete.setId(android.R.id.custom);
		buttonGroup.addView(edit);
		buttonGroup.addView(delete);
		TextView title=new TextView(p1.getContext());
		title.setLayoutParams(new SwipeBarLayout.LayoutParams(-1,height));
		title.setGravity(Gravity.CENTER_VERTICAL);
		title.setPaddingRelative((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16,p1.getResources().getDisplayMetrics()),0,0,0);
		title.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
		title.setBackground(p1.getResources().getDrawable(R.drawable.ripple_border,p1.getContext().getTheme()));
		ColorDrawable color=new ColorDrawable();
		color.setColor(p1.getResources().getColor(R.color.accent,p1.getContext().getTheme()));
		title.setForeground(color);
		title.setId(android.R.id.title);
		SwipeBarLayout swipe=new SwipeBarLayout(p1.getContext());
		swipe.setLayoutParams(new RecyclerView.LayoutParams(-1,height));
		swipe.addView(buttonGroup);
		swipe.addView(title);
		return new ViewHolder(swipe);
	}

	@Override
	public void onBindViewHolder(SiteAdapter.ViewHolder p1, int p2)
	{
		DataStore.Site site=list.get(p1.getPosition());
		p1.title.setTextColor(site.getName().hashCode()|0xff000000);
		p1.title.setText(site.getName());
		if(selected==site.get_Id())
			((ColorDrawable)p1.title.getForeground()).setAlpha(0x7f);
			else
			((ColorDrawable)p1.title.getForeground()).setAlpha(0);
	}

	@Override
	public int getItemViewType(int position)
	{
		// TODO: Implement this method
		return position;
	}

	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return list.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener,SwipeBarLayout.Callback,View.OnTouchListener{
		TextView title;
		SwipeBarLayout swipe;
		boolean open;

		@Override
		public boolean onTouch(View p1, MotionEvent p2)
		{
			if(openedPosition!=null&&openedPosition!=this&&p2.getAction()==p2.ACTION_DOWN)
			{
				SiteAdapter.this.close();
				return true;}
			return false;
		}

		public ViewHolder(View v){
			super(v);
			swipe=(SwipeBarLayout) v;
			title= v.findViewById(android.R.id.title);
			title.setOnClickListener(this);
			title.setOnLongClickListener(this);
			v.findViewById(android.R.id.edit).setOnClickListener(this);
			v.findViewById(android.R.id.custom).setOnClickListener(this);
			swipe.setCallback(this);
			title.setOnTouchListener(this);
		}
		public void close(){
			swipe.setOpen(false);
		}
		@Override
		public void onSwiped(SwipeBarLayout layout, boolean isOpen)
		{
			if(isOpen)
				openedPosition=this;
				else{
					if(openedPosition==this)
						openedPosition=null;
				}
			/*if(isOpen&&this!=openedPosition){
				if(openedPosition!=null){
					openedPosition.open=false;
					notifyItemChanged(openedPosition.getAdapterPosition());
				}
				openedPosition=this;
				openedPosition.open=true;
			}else if(!isOpen&&openedPosition==this){
				openedPosition.open=false;
				openedPosition=null;
			}*/
		}


		@Override
		public void onClick(View p1)
		{
			switch(p1.getId()){
				case android.R.id.title:
			if(swipe.isOpen())
				swipe.setOpen(false);
				else
			if(isClickable())
				getOnItemClickListener().onItemClick(SiteAdapter.this,this);
				break;
				case android.R.id.edit:
					if(isClickable())
						((OnItemClickListener)getOnItemClickListener()).onEdit(SiteAdapter.this,this);
						swipe.setOpen(false);
					break;
				case android.R.id.custom:
				if(isClickable())
					((OnItemClickListener)getOnItemClickListener()).onDelete(SiteAdapter.this,this);
					swipe.setOpen(false);
					break;
			}
		}

		@Override
		public boolean onLongClick(View p1)
		{
			if(!swipe.isOpen())
				swipe.setOpen(true);
			return true;
		}
	}
	
	public interface OnItemClickListener extends ClickAdapter.OnItemClickListener{
		void onEdit(SiteAdapter adapter,ViewHolder vh);
		void onDelete(SiteAdapter adapter,ViewHolder vh);
	}
}
