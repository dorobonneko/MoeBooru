package com.moe.booru.drawable;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.drawable.StateListDrawable;
import android.graphics.PixelFormat;
import java.util.Arrays;
import android.animation.ValueAnimator;
import android.animation.Animator;

public class CircleRippleDrawable extends StateListDrawable implements ValueAnimator.AnimatorUpdateListener
{
	private Paint paint;
	private Paint animePaint;
	private ValueAnimator anime;
	private boolean pressed;
	public CircleRippleDrawable(int primary,int accent){
		paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
		paint.setColor(primary);
		animePaint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
		animePaint.setColor(accent);
		animePaint.setAlpha(0);
		if(anime==null){
			anime=new ValueAnimator();
			anime.setFloatValues(0,1);
			anime.addUpdateListener(this);
			anime.setDuration(300);
			}
	}
	@Override
	public void draw(Canvas p1)
	{
		Rect rect=getBounds();
		p1.drawCircle(rect.centerX(),rect.centerY(),Math.min(rect.width(),rect.height())/2f,paint);
		if(anime!=null){
			p1.drawCircle(rect.centerX(),rect.centerY(),Math.min(rect.width(),rect.height())/2f*(pressed?anime.getAnimatedFraction():1),animePaint);
		}
	}

	@Override
	public void setAlpha(int p1)
	{
		paint.setAlpha(p1);
	}

	@Override
	public void setColorFilter(ColorFilter p1)
	{
		paint.setColorFilter(p1);
	}

	@Override
	public int getOpacity()
	{
		// TODO: Implement this method
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	protected boolean onStateChange(int[] state)
	{
		if(hasPressed(state)){
		
			if(!pressed){
				if(anime.isStarted()){
					anime.end();
					anime.reverse();
					anime.resume();
				}else{
					anime.start();
				}
				pressed=true;
			}
			}else{
				if(pressed){
					anime.end();
					anime.reverse();
					anime.resume();
					pressed=false;
				}
			}
		return true;
	}
	private boolean hasPressed(int[] state){
		for(int i:state){
			if(i==android.R.attr.state_pressed)
			return true;
			}
		return false;
	}

	@Override
	public int getIntrinsicWidth()
	{
		// TODO: Implement this method
		return -1;
	}

	@Override
	public int getIntrinsicHeight()
	{
		// TODO: Implement this method
		return -1;
	}

	@Override
	public void onAnimationUpdate(ValueAnimator p1)
	{
		animePaint.setAlpha((int)(255*(float)p1.getAnimatedValue()));
		if(getCallback()!=null)
			getCallback().invalidateDrawable(this);
	}
	
}
