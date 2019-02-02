package com.moe.booru.glide;
import com.bumptech.glide.request.target.ViewTarget;
import android.widget.ImageView;
import com.bumptech.glide.request.transition.Transition;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.Request;

public class ImageViewTarget extends com.bumptech.glide.request.target.SimpleTarget<Drawable>
{
	
	private ImageView iv;
	public ImageViewTarget(ImageView iv){
		this.iv=iv;
	}
	
	@Override
	public void onResourceReady(Drawable p1, Transition<? super Drawable> p2)
	{
		iv.setImageDrawable(p1);
	}

	@Override
	public void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		iv.setImageDrawable(null);
	}

	@Override
	public void onLoadStarted(Drawable placeholder)
	{
		// TODO: Implement this method
		super.onLoadStarted(placeholder);
		iv.setImageDrawable(null);
		
	}

}
