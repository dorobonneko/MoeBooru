package com.moe.booru.widget;
import android.widget.FrameLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.support.v4.widget.ViewDragHelper;
import android.view.View;
import android.view.MotionEvent;

public class SwipeLayout extends FrameLayout
{
	private OnSwipedListener osl;
	private ViewDragHelper mViewDragHelper;
	private ViewDragHelper.Callback call=new DragCallback();
	public SwipeLayout(Context c){
		this(c,null);
	}
	public SwipeLayout(Context c,AttributeSet attrs){
		super(c,attrs);
		mViewDragHelper=ViewDragHelper.create(this,call);
		mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
	}
	public void setOnSwipedListener(OnSwipedListener l){
		osl=l;
	}
	public interface OnSwipedListener{
		void onSwiped(boolean show);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if(ev.getAction()==ev.ACTION_DOWN)
			mViewDragHelper.abort();
		return mViewDragHelper.shouldInterceptTouchEvent(ev);
		}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		//if(event.getAction()==event.ACTION_UP)
		//	mViewDragHelper.abort();
		mViewDragHelper.processTouchEvent(event);
		return true;
	}
	@Override
    public void computeScroll() {
        if (mViewDragHelper != null && mViewDragHelper.continueSettling(true)) {
            postInvalidateOnAnimation();
        }else if(osl!=null){
			osl.onSwiped(getChildAt(0).getLeft()!=getWidth());
			}
    }

	class DragCallback extends ViewDragHelper.Callback
	{

		@Override
		public void onEdgeTouched(int edgeFlags, int pointerId)
		{
			if((edgeFlags&ViewDragHelper.EDGE_LEFT)==ViewDragHelper.EDGE_LEFT)
				mViewDragHelper.captureChildView(getChildAt(0),pointerId);
		}

		@Override
		public boolean tryCaptureView(View p1, int p2)
		{
			return false;
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx)
		{
			return Math.min(Math.max(left,0),child.getWidth());
		}

		@Override
		public int getViewHorizontalDragRange(View child)
		{
			return getWidth();
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel)
		{
			if(xvel>1000){
				mViewDragHelper.settleCapturedViewAt(releasedChild.getWidth(),releasedChild.getTop());
			}else if(xvel<-1000){
				mViewDragHelper.settleCapturedViewAt(0,releasedChild.getTop());
			}else{
				if(releasedChild.getLeft()>releasedChild.getWidth()/2)
					mViewDragHelper.settleCapturedViewAt(releasedChild.getWidth(),releasedChild.getTop());
				else
					mViewDragHelper.settleCapturedViewAt(0,releasedChild.getTop());
				
			}
			postInvalidateOnAnimation();
		}
	}
}
