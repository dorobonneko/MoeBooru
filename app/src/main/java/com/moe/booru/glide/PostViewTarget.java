package com.moe.booru.glide;
import android.widget.ImageView;
import android.view.View;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.request.transition.Transition;
import android.widget.ProgressBar;
import android.widget.Button;
import java.io.InputStream;
import android.graphics.BitmapFactory;
import java.io.File;
import com.moe.booru.widget.CropImageView;

public class PostViewTarget extends com.bumptech.glide.request.target.SimpleTarget<File>
{

	private CropImageView iv;
	private ProgressBar pb;
	private Button relink;
	public PostViewTarget(View iv){
		this.iv=iv.findViewById(android.R.id.icon);
		pb=iv.findViewById(android.R.id.progress);
		relink=iv.findViewById(android.R.id.redo);
	}

	@Override
	public void onResourceReady(File p1, Transition<? super File> p2)
	{
		try{
		iv.setImage(p1.getAbsolutePath());
		pb.setVisibility(View.GONE);
		relink.setVisibility(View.GONE);
		}catch(Exception e){
			onLoadFailed(null);
		}
	}

	@Override
	public void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		iv.setImage((String)null);
	}

	@Override
	public void onLoadStarted(Drawable placeholder)
	{
		// TODO: Implement this method
		super.onLoadStarted(placeholder);
		iv.setImage((String)null);
		relink.setVisibility(View.GONE);
		pb.setVisibility(View.VISIBLE);
	}

	@Override
	public void onLoadFailed(Drawable errorDrawable)
	{
		// TODO: Implement this method
		super.onLoadFailed(errorDrawable);
		relink.setVisibility(View.VISIBLE);
		pb.setVisibility(View.GONE);
	}

}
