package com.moe.booru.adapter;
import android.support.v7.widget.RecyclerView;

public abstract class ClickAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T>
{
	private OnItemClickListener l;
	public void setOnItemClickListener(OnItemClickListener l){
		this.l=l;
	}
	OnItemClickListener getOnItemClickListener(){
		return l;
	}
	boolean isClickable(){
		return l!=null;
	}
	public interface OnItemClickListener{
		void onItemClick(ClickAdapter click,RecyclerView.ViewHolder vh);
	}
}
