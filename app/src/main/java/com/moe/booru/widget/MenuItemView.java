package com.moe.booru.widget;
import android.view.View;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.util.AttributeSet;
import android.support.v4.view.ViewCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

public class MenuItemView extends View
{
	private CharSequence title;
	private Drawable icon;
	private Paint paint;
	private int checkedColor,unCheckedColor,checkedBackground;
	private float size;
	private boolean checked=false;
	public MenuItemView(Context context,AttributeSet attrs){
		super(context,attrs);
		paint=new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		TypedArray ta=context.obtainStyledAttributes(attrs,new int[]{android.R.attr.icon,android.R.attr.title,android.R.attr.colorControlNormal,android.R.attr.colorControlActivated,android.R.attr.textSize,android.R.attr.textScaleX,android.R.attr.colorControlHighlight,android.R.attr.selectableItemBackground});
		unCheckedColor=ta.getColor(2,0xffbbbbbb);
		checkedColor=ta.getColor(3,0xffffffff);
		try{icon=ta.getDrawable(0);}catch(Exception e){}
		title=ta.getString(1);
		paint.setTextSize(ta.getDimension(4,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,18,getResources().getDisplayMetrics())));
		paint.setTextScaleX(ta.getDimension(5,1));
		checkedBackground=ta.getColor(6,0x00000000);
		ViewCompat.setBackground(this,ta.getDrawable(7));
		ta.recycle();
		size=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16,getResources().getDisplayMetrics());
		setWillNotDraw(false);
	}
	public void setTitle(CharSequence title){
		this.title=title;
		if(isShown())
		invalidate();
	}
	public void setIcon(Drawable icon){
		if(this.icon!=null)
			this.icon.setCallback(null);
		this.icon=icon;
		if(this.icon!=null)
			this.icon.setCallback(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO: Implement this method
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec,(int)size*3);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(isEnabled()&&isChecked()){
			//paint.setColor(checkedBackground);
			canvas.drawColor(checkedBackground);
		}
		float x=size;
		if(icon!=null){
			icon.setBounds((int)size*2-icon.getIntrinsicWidth()/2,(canvas.getHeight()-icon.getIntrinsicHeight())/2,(int)size*2+icon.getIntrinsicWidth()/2,(canvas.getHeight()-icon.getIntrinsicHeight())/2+icon.getIntrinsicHeight());
			icon.setTint(isChecked()?checkedColor:unCheckedColor);
			icon.draw(canvas);
			x+=size*3;
		}
		if(title!=null){
			if(isChecked())
				paint.setColor(checkedColor);
				else
				paint.setColor(unCheckedColor);
		canvas.drawText(title.toString(),x,(canvas.getHeight()-paint.ascent()-paint.descent())/2.0f,paint);
		}
		
	}
	public boolean isChecked(){
		return checked;
	}
	public void setChecked(boolean checked){
		this.checked=checked;
		if(isShown())
		invalidate();
	}
}
