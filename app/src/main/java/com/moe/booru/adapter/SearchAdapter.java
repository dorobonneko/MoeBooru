package com.moe.booru.adapter;
import android.view.*;
import android.widget.*;

import com.moe.booru.R;
import com.moe.booru.empty.Search;
import java.util.List;
import android.util.TypedValue;
import com.moe.booru.fragments.SearchFragment;

public class SearchAdapter extends BaseAdapter
{
	private List<Search> list;
	private OnItemRemoveListener oirl;
	public SearchAdapter(List<Search> list){
		this.list=list;
	}

	public void setOnItemRemoveListener(OnItemRemoveListener l)
	{
		oirl=l;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		if(p2==null){
			RelativeLayout ll=new RelativeLayout(p3.getContext());
			TextView name=new TextView(p3.getContext());
			ImageView iv=new ImageView(p3.getContext());
			iv.setImageResource(R.drawable.close);
			iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(-2,-2);
			iv.setBackgroundResource(R.drawable.ripple_circle);
			params.addRule(RelativeLayout.ALIGN_PARENT_END);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			ll.addView(iv,params);
			iv.setId(R.id.close);
			params=new RelativeLayout.LayoutParams(-1,-2);
			params.addRule(RelativeLayout.START_OF,R.id.close);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			ll.addView(name,params);
			p2=ll;
			iv.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View p1)
					{
						if(oirl!=null)oirl.onItemRemove(p1.getTag());
					}
				});
			ll.setLayoutParams(new ViewGroup.LayoutParams(-1,(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,52,ll.getResources().getDisplayMetrics())));
		}
		Search search=list.get(p1);
		ViewGroup vg=(ViewGroup) p2;
		((TextView)vg.getChildAt(1)).setText(search.tag);
		vg.getChildAt(0).setTag(p1);
		return p2;
	}

	@Override
	public long getItemId(int p1)
	{
		// TODO: Implement this method
		return p1;
	}

	@Override
	public Object getItem(int p1)
	{
		// TODO: Implement this method
		return list.get(p1);
	}

	@Override
	public int getCount()
	{
		// TODO: Implement this method
		return list.size();
	}


public interface OnItemRemoveListener{
	void onItemRemove(int pos);
}

	
}
