package com.moe.booru.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import java.util.List;
import com.moe.booru.empty.Artist;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder>
{

	private List<Artist> list;
	private OnItemClickListener oicl;
	public ArtistAdapter(List<Artist> list){
		this.list=list;
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{

		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(com.moe.booru.R.layout.pool_item_view,p1,false));
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, int p2)
	{
		Artist pool=list.get(vh.getPosition());
		vh.name.setText(pool.name);
		StringBuilder sb=new StringBuilder();
		for(String url:pool.urls){
			sb.append(url).append("\r\n");
		}
		vh.desc.setText(sb.toString());
		//vh.desc.setText(pool.description);
		vh.count.setText(String.valueOf(pool.id));
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
			//desc.setVisibility(View.GONE);
			count=(TextView) vg.getChildAt(2);
		}

		@Override
		public void onClick(View p1)
		{
			if(oicl!=null)oicl.onItemClick(ArtistAdapter.this,this);
		}


	}
	public void setOnItemClickListener(OnItemClickListener l){
		oicl=l;
	}
	public interface OnItemClickListener{
		void onItemClick(ArtistAdapter adapter,ViewHolder vh);
	}
}
