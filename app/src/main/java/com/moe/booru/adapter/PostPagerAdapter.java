package com.moe.booru.adapter;
import android.view.*;
import android.widget.*;

import android.support.v4.view.PagerAdapter;
import com.moe.booru.R;
import com.moe.booru.empty.Post;
import com.moe.booru.glide.PostViewTarget;
import java.util.List;
import com.bumptech.glide.load.model.GlideUrl;
import java.io.InputStream;
import com.moe.booru.widget.CropImageView;
import com.bumptech.glide.request.RequestListener;
import java.io.File;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

public class PostPagerAdapter extends PagerAdapter
{
	private List<Post> list;
	private int definition=0;
	private RequestManager glide;
	public PostPagerAdapter(RequestManager glide,List<Post> list){
		this.list=list;
		this.glide=glide;
		}
	public void setDefinition(int definition){
		this.definition=definition;
	}
	@Override
	public int getCount()
	{
		// TODO: Implement this method
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View p1, Object p2)
	{
		return p1==p2;
	}
	private GlideUrl getUrl(Post post){
		GlideUrl url=null;
		switch(definition){
			case 2:
				url=post.getFileUrl();
				if(url!=null)return url;
			case 1:
				url=post.getJpegUrl();
				if(url!=null)return url;
			case 0:
				url=post.getSampleUrl();
				if(url!=null)return url;
			default:
				url=post.getPreviewUrl();
				if(url!=null)return url;
		}
		return null;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		final GlideUrl url=getUrl(list.get(position));
		FrameLayout fl=new FrameLayout(container.getContext());
		fl.setLayoutParams(new ViewGroup.LayoutParams(-1,-1));
		CropImageView iv=new CropImageView(container.getContext());
		iv.setId(android.R.id.icon);
		fl.addView(iv,new FrameLayout.LayoutParams(-1,-1));
		ProgressBar pb=new ProgressBar(container.getContext());
		pb.setId(android.R.id.progress);
		FrameLayout.LayoutParams pl=new FrameLayout.LayoutParams(-2,-2);
		pl.gravity=Gravity.CENTER;
		fl.addView(pb,pl);
		Button b=new Button(container.getContext());
		b.setId(android.R.id.redo);
		FrameLayout.LayoutParams bl=new FrameLayout.LayoutParams(-2,-2);
		bl.gravity=Gravity.CENTER;
		b.setText(R.string.redo);
		fl.addView(b,bl);
		b.setTag(position);
		final PostViewTarget pvt=new PostViewTarget(fl);
		glide.asFile().load(url).downloadOnly(pvt);
		container.addView(fl);
		b.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					glide.asFile().load(url).downloadOnly(pvt);
				}
			});
		return fl;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		container.removeView((View)object);
	}
	
}
