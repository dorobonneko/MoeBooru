package com.moe.booru.widget;
import android.graphics.*;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.moe.booru.drawable.CircleRippleDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.moe.booru.R;

public class CircleImageView extends ImageView
{
	private Paint paint;
	public CircleImageView(Context context){
		this(context,null);
	}
	public CircleImageView(Context context,AttributeSet attrs){
		super(context,attrs);
		paint=new Paint();
		paint.setColor(0xffffffff);
		paint.setDither(true);
		paint.setAntiAlias(true);
		setBackground(new CircleRippleDrawable(context.getResources().getColor(R.color.primary,context.getTheme()),context.getResources().getColor(R.color.accent,context.getTheme())));
		setTintColor(0xffffffff);
		setScaleType(ScaleType.CENTER_INSIDE);
	}
	
	public void setTintColor(int color){
		Drawable image=getDrawable();
		if(image!=null)
			image.setTint(color);
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		//canvas.drawCircle(canvas.getWidth()/2f,canvas.getHeight()/2f,Math.min(canvas.getWidth(),canvas.getHeight())/2f,paint);
		super.onDraw(canvas);
	}
	
}
