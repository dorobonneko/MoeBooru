package com.moe.booru.adapter;
import java.util.List;
import com.moe.booru.empty.Post;
import java.util.ArrayList;
import android.graphics.drawable.Drawable;
import com.moe.booru.R;
import com.moe.booru.glide.ImageViewTarget;
import com.moe.booru.utils.Glide;
import com.moe.booru.utils.MultiTableList;
import com.moe.booru.utils.Filter;
import com.moe.booru.utils.OkHttp;
import com.moe.booru.utils.CacheManager;
import com.moe.booru.core.Booru;
import java.util.Arrays;

public class PostAdapter extends ImageAdapter implements Filter<Post>
{
	private MultiTableList<Post> list;
	private boolean showInfoBar;
	private Booru booru;
	public PostAdapter(){
		this.list=new MultiTableList<>();
	}
	public void setBooru(Booru booru){
		this.booru=booru;
	}
	public List<Post> getList()
	{
		return list;
	}

	public void setSafe(boolean safe)
	{
		list.setFilter(safe?this:null);
		notifyDataSetChanged();
	}

	
	@Override
	public boolean accept(Post t)
	{
		if(t.rating=='s')return true;
		return false;
	}

	
	public void setShowBottomBar(boolean show){
		this.showInfoBar=show;
		notifyDataSetChanged();
	}
	public void addAll(List<Post> list){
		int size=getItemCount();
		for(Post post:list){
			int index=this.list.indexOf(post);
			if(index!=0)
				this.list.set(index,post);
			else
				this.list.add(post);
		}
		notifyItemRangeInserted(size,getItemCount()-size);
	}
	public void clear(){
		//int size=getItemCount();
		list.clear();notifyDataSetChanged();
		//notifyItemRangeRemoved(0,size);
	}
	public Post get(int podition){
		return list.get(podition);
	}
	@Override
	public void onBindViewHolder(ImageAdapter.ViewHolder vh, int p2)
	{
		Post post=get(vh.getPosition());
		vh.image.setImageMeta(post.width,post.height,showInfoBar);
		vh.info.setText(String.format("%dx%d",post.width,post.height));
		/*Drawable heart=vh.icon_heart.getDrawable();
		if(heart==null)
			vh.icon_heart.setImageDrawable(heart=vh.icon_heart.getResources().getDrawable(R.drawable.heart_outline,vh.icon_heart.getContext().getTheme()).mutate());*/
		vh.score.setText(String.valueOf(post.score));
		ImageViewTarget ivt=(ImageViewTarget) vh.icon_heart.getTag();
		if(ivt==null)
			vh.icon_heart.setTag(ivt=new ImageViewTarget(vh.icon));
		Glide.getGlide().load(post.getPreviewUrl()).into(ivt);
		vh.icon_heart.setChecked(false);
		if(booru.getSite().getUser()!=null){
		if(post.votes!=null){
			vh.icon_heart.setChecked(post.votes.contains(booru.getSite().getUser().name));
		}
		}
	}

	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return list.size();
	}

	@Override
	public int getItemViewType(int position)
	{
		// TODO: Implement this method
		return position;
	}
}
