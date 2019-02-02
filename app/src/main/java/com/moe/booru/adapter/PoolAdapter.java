package com.moe.booru.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import com.moe.booru.empty.Pool;
import android.view.LayoutInflater;
import com.moe.booru.R;
import android.widget.TextView;

public class PoolAdapter extends RecyclerView.Adapter<PoolAdapter.ViewHolder>
{

	private List<Pool> list;
	private OnItemClickListener oicl;
	public PoolAdapter(List<Pool> list){
		this.list=list;
	}
	@Override
	public PoolAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		
		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.pool_item_view,p1,false));
	}

	@Override
	public void onBindViewHolder(PoolAdapter.ViewHolder vh, int p2)
	{
		Pool pool=list.get(vh.getPosition());
		vh.name.setText(pool.name);
		vh.desc.setText(pool.description);
		vh.count.setText(String.valueOf(pool.post_count));
	}

	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return list.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		private TextView name,desc,count;
		public ViewHolder(View v){
			super(v);
			v.setOnClickListener(this);
			ViewGroup vg=(ViewGroup) v;
			name=(TextView) vg.getChildAt(0);
			desc=(TextView) vg.getChildAt(1);
			count=(TextView) vg.getChildAt(2);
		}

		@Override
		public void onClick(View p1)
		{
			if(oicl!=null)oicl.onItemClick(PoolAdapter.this,this);
		}

		
	}
	public void setOnItemClickListener(OnItemClickListener l){
		oicl=l;
	}
	public interface OnItemClickListener{
		void onItemClick(PoolAdapter adapter,ViewHolder vh);
	}
}
