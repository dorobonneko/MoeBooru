package com.moe.booru.widget;
import android.widget.ImageView;
import android.content.Context;
import android.util.AttributeSet;
import java.io.InputStream;
import android.graphics.BitmapRegionDecoder;
import android.graphics.BitmapFactory;
import com.moe.booru.utils.BitmapUtils;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.widget.FrameLayout;
import com.moe.booru.R;

public class SimpleImageView extends FrameLayout
{
	private boolean showInfobar;
	private int w,h;
	public SimpleImageView(Context context){
		this(context,null);
	}
	public SimpleImageView(Context context,AttributeSet attrs){
		super(context,attrs);
		setBackgroundResource(R.color.primary);
		}
	
	public void setImageMeta(int w,int h,boolean showInfobar){
		this.w=w;
		this.h=h;
		this.showInfobar=showInfobar;
		setWillNotDraw(false);
		requestLayout();
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		if(w>0&&h>0){
		int width=MeasureSpec.getSize(widthMeasureSpec);
		int height=(int)(width/(double)w*h);
		super.onMeasure(MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
		setMeasuredDimension(width,(showInfobar?getChildAt(1).getMeasuredHeight():0)+ height);
		}
		
	}
}
