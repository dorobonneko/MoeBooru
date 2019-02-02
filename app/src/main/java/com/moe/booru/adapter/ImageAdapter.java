package com.moe.booru.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.moe.booru.widget.SimpleImageView;
import android.widget.ImageView;
import android.view.Gravity;
import android.graphics.drawable.Drawable;
import com.moe.booru.R;
import android.widget.TextView;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import android.support.v4.widget.ImageViewCompat;
import android.widget.ToggleButton;
import android.widget.CompoundButton;
import com.moe.booru.widget.HeartButton;

public abstract class ImageAdapter extends RecyclerView.Adapter<com.moe.booru.adapter.ImageAdapter.ViewHolder> 
{
	private OnFavListener l;
	private OnItemClickListener oicl;
	@Override
	public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		int[] colors=new int[]{0xffdddddd,0xffe0dddd,0xffdde0dd,0xffdddde0};
		SimpleImageView simple=new SimpleImageView(p1.getContext());
		ImageView image=new ImageView(p1.getContext());
		image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		image.setId(android.R.id.icon);
		image.setBackgroundColor(colors[(int)(Math.random()*colors.length)]);
		image.setForeground(image.getResources().getDrawable(R.drawable.ripple_border,image.getContext().getTheme()));
		simple.addView(image,new SimpleImageView.LayoutParams(-1,-1));
		RelativeLayout layout=new RelativeLayout(p1.getContext());
		SimpleImageView.LayoutParams layout_params=new SimpleImageView.LayoutParams(SimpleImageView.LayoutParams.MATCH_PARENT,SimpleImageView.LayoutParams.WRAP_CONTENT);
		layout_params.gravity=Gravity.BOTTOM;
		layout.setLayoutParams(layout_params);
		simple.addView(layout);
		ToggleButton heart=new HeartButton(p1.getContext(),null);
		heart.setButtonDrawable(R.drawable.selector_heart);
		heart.setBackgroundDrawable(heart.getResources().getDrawable(R.drawable.ripple_circle,heart.getContext().getTheme()).mutate());
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(-2,-2);
		params.addRule(RelativeLayout.ALIGN_PARENT_END);
		heart.setLayoutParams(params);
		heart.setId(android.R.id.icon_frame);
		heart.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,p1.getResources().getDisplayMetrics()));
		layout.addView(heart);
		TextView info=new TextView(p1.getContext());
		info.setShadowLayer(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1,p1.getResources().getDisplayMetrics()),0,0,0x7f000000);
		info.setId(android.R.id.summary);
		info.setTextColor(0xfffefefe);
		info.setShadowLayer(2,0,0,0x9f000000);
		params=new RelativeLayout.LayoutParams(-2,-2);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		info.setLayoutParams(params);
		layout.addView(info);
		TextView score=new TextView(p1.getContext());
		params=new RelativeLayout.LayoutParams(-2,-2);
		params.addRule(RelativeLayout.START_OF,android.R.id.icon_frame);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		score.setLayoutParams(params);
		score.setTextColor(0xffffffff);
		score.setShadowLayer(2,0,0,0x8f000000);
		layout.addView(score);
		score.setId(android.R.id.text1);
		info.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
		score.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
		int cell=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4,p1.getResources().getDisplayMetrics());
		layout.setPadding(cell,cell/2,cell,0);
		return new ViewHolder(simple);
	}

	@Override
	public int getItemViewType(int position)
	{
		return 0;
	}
	public void setOnFavListener(OnFavListener l){
		this.l=l;
	}
	public void setOnItemClickListener(OnItemClickListener l){
		this.oicl=l;
	}
	public class ViewHolder extends RecyclerView.ViewHolder implements ToggleButton.OnCheckedChangeListener,View.OnClickListener{
		SimpleImageView image;
		//Drawable heart;
		ImageView icon;
		ToggleButton icon_heart;
		TextView info,score;
		public ViewHolder(View v){
			super(v);
			image=(SimpleImageView) v;
			icon=v.findViewById(android.R.id.icon);
			icon_heart=v.findViewById(android.R.id.icon_frame);
			info=v.findViewById(android.R.id.summary);
			score=v.findViewById(android.R.id.text1);
			icon_heart.setOnCheckedChangeListener(this);
			icon.setOnClickListener(this);
			icon_heart.setClickable(true);
		}

		@Override
		public void onCheckedChanged(CompoundButton p1, boolean p2)
		{
			if(l!=null)l.onFav(ImageAdapter.this,this,p1);
		}

		@Override
		public void onClick(View p1)
		{
			if(oicl!=null)oicl.onItemClick(ImageAdapter.this,this);
		}



		

		//public Drawable getHeart(){
			//return heart;
		//}
		/*public void setVisibility(boolean visible) {
		itemView.setVisibility(visible?View.VISIBLE:View.GONE);
		}*/
		
	}
	public interface OnFavListener{
		void onFav(RecyclerView.Adapter<ViewHolder> adapter,ViewHolder vh,CompoundButton checked);
	}
	public interface OnItemClickListener{
		void onItemClick(RecyclerView.Adapter<ViewHolder> adapter,ViewHolder vh);
	}
}
