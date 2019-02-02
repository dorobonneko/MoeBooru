package com.moe.booru.widget;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.util.AttributeSet;
import android.content.res.TypedArray;
import android.util.TypedValue;
import com.moe.booru.R;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.support.v4.widget.ViewDragHelper;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.widget.HorizontalScrollView;
import android.widget.Scroller;

public class SwipeBarLayout extends ViewGroup
{
	private Callback mCallback;
	private Scroller mScroller;
	private boolean mOpen;
	public SwipeBarLayout(Context context)
	{
		this(context, null);
	}
	public SwipeBarLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mScroller=new Scroller(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width=MeasureSpec.getSize(widthMeasureSpec);
		int height=MeasureSpec.getSize(heightMeasureSpec);
		for(int i=0;i<getChildCount();i++){
			View child=getChildAt(i);
			LayoutParams l=(SwipeBarLayout.LayoutParams) child.getLayoutParams();
			int wmode=l.width==-1?MeasureSpec.EXACTLY:l.width==-2?MeasureSpec.AT_MOST:MeasureSpec.EXACTLY;
			int hmode=l.height==-1?MeasureSpec.EXACTLY:l.height==-2?MeasureSpec.AT_MOST:MeasureSpec.EXACTLY;
			int cwidth=l.width>=0?l.width:width;
			int cheight=l.height>=0?l.height:height;
			child.measure(MeasureSpec.makeMeasureSpec(cwidth,wmode),MeasureSpec.makeMeasureSpec(cheight,hmode));
		}
		setMeasuredDimension(width,height);
	}

	@Override
	protected void onLayout(boolean p1, int p2, int p3, int p4, int p5)
	{
		if (getChildCount() == 2)
		{

			View main=getChildAt(1);
			main.layout(0, 0, main.getMeasuredWidth(), main.getMeasuredHeight());
			View child=getChildAt(0);
			LayoutParams params=(SwipeBarLayout.LayoutParams) child.getLayoutParams();
			if (params.direction == LayoutParams.LEFT)
			{
				child.layout(-child.getMeasuredWidth(), 0, 0, child.getMeasuredHeight());
			}
			else
			{
				child.layout(main.getMeasuredWidth(), 0, main.getMeasuredWidth() + child.getMeasuredWidth(), child.getMeasuredHeight());
			}
		}
	}
	public boolean isOpen(){
		return mOpen;
	}
	public void setOpen(boolean open){
		mScroller.startScroll(getScrollX(),0,(open?-getChildAt(0).getMeasuredWidth():0)-getScrollX(),0,150);
		postInvalidateOnAnimation();
	}
	public void setOpenNoAnime(boolean open){
		scrollTo(open?-getChildAt(0).getMeasuredWidth():0,0);
	}
	@Override
	public void computeScroll()
	{
		if(mScroller.computeScrollOffset()){
			scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
			postInvalidateOnAnimation();
			}else{
			mOpen=getScrollX()!=0;
			if(mCallback!=null)
				mCallback.onSwiped(this,mOpen);
			}
	}
	
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs)
	{
		LayoutParams l=(SwipeBarLayout.LayoutParams) generateLayoutParams(super.generateLayoutParams(attrs));
		TypedArray ta=getContext().obtainStyledAttributes(attrs, R.styleable.swipe_layout);
		l.direction = ta.getInt(R.styleable.swipe_layout_direction, LayoutParams.RIGHT);
		ta.recycle();
		return l;
	}
	@Override
	protected LayoutParams generateDefaultLayoutParams()
	{
		// TODO: Implement this method
		return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	@Override
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p)
	{
		if (p instanceof LayoutParams)
			return (LayoutParams)p;
		return new LayoutParams(p.width, p.height);
	}

	public static class LayoutParams extends ViewGroup.LayoutParams
	{
		public static final int LEFT=0;
		public static final int RIGHT=1;
		public int direction=RIGHT;
		public LayoutParams(int w, int h)
		{
			super(w, h);
		}
		public LayoutParams(int w, int h, int d)
		{
			this(w, h);
			this.direction = d;
		}
	}
	public void setCallback(Callback call)
	{
		mCallback = call;
	}
	public interface Callback
	{
		void onSwiped(SwipeBarLayout layout, boolean isOpen);
	}
	
}
