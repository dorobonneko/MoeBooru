package com.moe.booru.widget;
import android.widget.TextView;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.widget.Button;
import com.moe.booru.R;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.RippleDrawable;
import android.animation.ValueAnimator;
import android.animation.ObjectAnimator;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.animation.TypeEvaluator;
import android.animation.RectEvaluator;
import android.animation.Animator;
import android.view.animation.LinearInterpolator;

public class ProgressButton extends Button
{
	private BackgroundDrawable mBackgroundDrawable;
	private boolean progressing;
	private ProgressDrawable mProgressDrawable;
	public ProgressButton(Context context)
	{
		this(context, null);
	}
	public ProgressButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setBackground(mBackgroundDrawable = new BackgroundDrawable(context.getResources().getColor(R.color.accent, context.getTheme())));
		setForeground(mProgressDrawable = new ProgressDrawable(context.getResources().getColor(R.color.accent,context.getTheme()),3));
		setBackgroundColor(context.getResources().getColor(R.color.primary));
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if (!progressing)
			super.onDraw(canvas);
	}
	public void setProgressing(boolean progress)
	{
		if (progress == progressing)return;
		progressing = progress;
		if (progress)
			mProgressDrawable.start();
		else
			mProgressDrawable.stop();
	}
	public boolean isProgressing()
	{
		return progressing;
	}

	@Override
	public void setBackgroundColor(int color)
	{
		// TODO: Implement this method
		mBackgroundDrawable.setBackgroundColor(color);
	}

	class ProgressDrawable extends Drawable implements ValueAnimator.AnimatorUpdateListener
	{
		private Paint paint;
		private ValueAnimator anime;
		private int accent;
		public static final int MIN_SWEEP_ANGLE = 30;
		public ProgressDrawable(int accent,int size){
			paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
			this.accent=(accent);
			paint.setColor(accent);
			paint.setStrokeWidth(size*2);
			paint.setStyle(Paint.Style.STROKE);
			anime=ObjectAnimator.ofFloat(0,360);
			anime.setDuration(1000);
			anime.addUpdateListener(this);
			anime.setRepeatMode(anime.RESTART);
			anime.setRepeatCount(anime.INFINITE);
			anime.setInterpolator(new LinearInterpolator());
		}
		@Override
		public void draw(Canvas p1)
		{
			if(anime.isRunning()){
				Rect bounds=getBounds();
				p1.drawArc(bounds.centerX()-bounds.centerY()+paint.getStrokeWidth()/2,bounds.top+paint.getStrokeWidth()/2,bounds.centerX()+bounds.centerY()-paint.getStrokeWidth()/2,bounds.bottom-paint.getStrokeWidth()/2,anime.getAnimatedValue(),MIN_SWEEP_ANGLE,false,paint);
			}
		}

		@Override
		public void onAnimationUpdate(ValueAnimator p1)
		{
			if(getCallback()!=null)
				getCallback().invalidateDrawable(this);
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
			return 0;
		}

		public void start()
		{
			mBackgroundDrawable.setBounds((getWidth() - getHeight()) / 2, 0, (getWidth() - getHeight()) / 2 + getHeight(), getHeight());
			//setEnabled(false);
			anime.start();
		}
		public void stop()
		{
			mBackgroundDrawable.setBounds(0,0,getWidth(),getHeight());
			//setEnabled(true);
			anime.cancel();
		}


		
	}
	class BackgroundDrawable extends StateListDrawable implements ValueAnimator.AnimatorUpdateListener
	{
		public static final int RECT=0;
		public static final int CIRCLE=1;
		private Paint paint;
		private boolean pressed;
		private int accent;
		private ValueAnimator anime;
		private ValueAnimator boundsAnime;
		private Rect bounds=new Rect();
		public BackgroundDrawable(int accent)
		{
			paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
			this.accent = accent;
			anime = ObjectAnimator.ofFloat(0, 1);
			anime.setDuration(300);
			anime.addUpdateListener(this);
			boundsAnime = new ValueAnimator();
			boundsAnime.setDuration(300);
			boundsAnime.addUpdateListener(this);
		}
		@Override
		public void setBounds(int left, int top, int right, int bottom)
		{
			boundsAnime.setObjectValues(new Rect(bounds), new Rect(left, top, right, bottom));
			boundsAnime.setEvaluator(new RectEvaluator());
			boundsAnime.start();
			super.setBounds(left, top, right, bottom);
			//bounds.set(left,top,right,bottom);
		}
		@Override
		public void onAnimationUpdate(ValueAnimator p1)
		{
			if (p1 == boundsAnime)
				bounds.set((Rect)p1.getAnimatedValue());
			if (getCallback() != null)
				getCallback().invalidateDrawable(this);
		}




		public void setBackgroundColor(int color)
		{
			paint.setColor(color);
		}
		@Override
		public void draw(Canvas p1)
		{
			Rect bounds=this.bounds;
			float fraction=(bounds.width() - bounds.height()) / (float)(getWidth() - bounds.height());
			float radiu=(1 - fraction) * bounds.height();
			p1.drawRoundRect(new RectF(bounds), radiu, radiu, paint);
			int color=paint.getColor();
			paint.setColor(accent);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
			//int w=(int)(bounds.width()/2*(float)anime.getAnimatedValue());
			//int h=(int)(bounds.height()/2*(float)anime.getAnimatedValue());
			//p1.drawRoundRect((float)(bounds.centerX()-w),(float)(bounds.centerY()-h),(float)(bounds.centerX()+w),(float)(bounds.centerY()+h),0,0,paint);
			float radius=(((float)Math.sqrt(Math.pow(bounds.width(), 2) + Math.pow(bounds.height(), 2)) / 2f) - bounds.height() / 2f) * fraction + bounds.height() / 2f;
			//radiu*=(float)anime.getAnimatedValue();

			//p1.drawRoundRect(bounds.centerX()-w,bounds.centerY()-h,bounds.centerX()+w,bounds.centerY()+h,radiu,radiu,paint);
			p1.drawCircle(bounds.centerX(), bounds.centerY(), radius * (float)anime.getAnimatedValue(), paint);
			paint.setColor(color);
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
			return 0;
		}
		public void toggle(int type)
		{

		}

		@Override
		protected boolean onStateChange(int[] stateSet)
		{
			boolean hasPressed=false;
			for (int state:stateSet)
			{
				if (state == android.R.attr.state_pressed)
				{
					hasPressed = true;
					break;
				}
			}
			if (hasPressed)
			{
				if (!pressed)
				{
					if (anime.isStarted())
					{
						anime.end();
						anime.reverse();
						anime.resume();
					}
					else
						anime.start();
					pressed = true;
				}
			}
			else
			{
				if (pressed)
				{
					anime.end();
					anime.reverse();
					anime.resume();
					pressed = false;
				}
			}
			return true;
		}

	}
}
