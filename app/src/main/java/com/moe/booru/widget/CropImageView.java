package com.moe.booru.widget;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import java.io.InputStream;
import android.graphics.BitmapRegionDecoder;
import java.io.IOException;
import java.io.FileDescriptor;
import android.view.ScaleGestureDetector;
import android.view.MotionEvent;
import android.graphics.Rect;
import android.graphics.Matrix;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.GestureDetector;
import android.view.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import android.net.Uri;
import java.io.File;
import android.graphics.Paint;
import android.graphics.DrawFilter;
import android.graphics.PaintFlagsDrawFilter;
import android.animation.ValueAnimator;
import android.animation.TypeEvaluator;
import android.animation.PropertyValuesHolder;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.TypedValue;
import android.graphics.Region;
import android.animation.Animator;
import com.moe.booru.utils.AnimatorListener;
import android.support.v4.view.ViewCompat;
import android.view.animation.DecelerateInterpolator;
import com.moe.booru.utils.BitmapUtils;
import android.widget.OverScroller;

public class CropImageView extends View implements ScaleGestureDetector.OnScaleGestureListener,android.view.GestureDetector.OnGestureListener,android.view.GestureDetector.OnDoubleTapListener,ValueAnimator.AnimatorUpdateListener
{

	private BitmapRegionDecoder mBitmapRegionDecoder;
	private ScaleGestureDetector mScaleGestureDetector;
	private GestureDetector mGestureDetector;
	private BitmapFactory.Options options;
	private float scale=1,backScale;
	private int width,height;
	private Rect imageRect;
	private Bitmap backgroundBitmap,foreBitmap;
	private DrawFilter filter;
	private Matrix bmatrix=new Matrix();
	private float[] matrix=new float[9];
	private ValueAnimator anime,scaleAnime;
	private Matrix foreMatrix=new Matrix();
	private float focusX,focusY;
	private int mode=0;
	private RectF viewRectf=new RectF();
	private TypeEvaluator mTypeEvaluator=new PositionEvaluator();
	private OverScroller mOverScroller;
	private boolean scrolling;
	public CropImageView(Context context)
	{
		this(context, null);
	}
	public CropImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setWillNotDraw(false);
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		mGestureDetector = new GestureDetector(context, this);
		mGestureDetector.setOnDoubleTapListener(this);
		mGestureDetector.setIsLongpressEnabled(false);
		options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		imageRect = new Rect();
		filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		anime = new ValueAnimator();
		anime.setDuration(150);
		anime.addUpdateListener(this);
		anime.addListener(new AnimeListener());
		scaleAnime = new ValueAnimator();
		scaleAnime.setDuration(300);
		scaleAnime.addUpdateListener(this);
		scaleAnime.addListener(new AnimeListener());
		mOverScroller=new OverScroller(context);
		}
	@Override
	protected void onDetachedFromWindow()
	{
		// TODO: Implement this method
		super.onDetachedFromWindow();
		if (foreBitmap != null && !foreBitmap.isRecycled())
			foreBitmap.recycle();
		if (backgroundBitmap != null && !backgroundBitmap.isRecycled())
			backgroundBitmap.recycle();
		if (mBitmapRegionDecoder != null && !mBitmapRegionDecoder.isRecycled())
			mBitmapRegionDecoder.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width=MeasureSpec.getSize(widthMeasureSpec);
		int height=MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
		//checkPoint();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		// TODO: Implement this method
		super.onLayout(changed, left, top, right, bottom);
		viewRectf.set(0,0,getWidth(),getHeight());
		checkPoint();
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.setDrawFilter(filter);
		if (backgroundBitmap != null)
			canvas.drawBitmap(backgroundBitmap, bmatrix, null);
		if (foreBitmap != null)
			canvas.drawBitmap(foreBitmap, foreMatrix, null);
		//canvas.drawPath(cropPath,pathPaint);
	}
	private void checkImagePosition(RectF rect, boolean anim)
	{
		if (backgroundBitmap == null)return;
		bmatrix.getValues(matrix);
		//显示的宽高
		float cWidth=backgroundBitmap.getWidth() * matrix[Matrix.MSCALE_X];
		float cheight=backgroundBitmap.getHeight() * matrix[Matrix.MSCALE_Y];
		//显示时x和y位置
		float sx=matrix[Matrix.MTRANS_X];
		float sy=matrix[Matrix.MTRANS_Y];
		float dx=sx,dy=sy;
		if(cWidth>rect.width()){
		if(sx>rect.left){
			dx=rect.left;
		}else if(sx+cWidth<rect.right){
			dx=rect.right-cWidth;
		}}else{
			dx=rect.left+(rect.width()-cWidth)/2f;
		}
		if(cheight>rect.height()){
		if(sy>rect.top){
			dy=rect.top;
		}else if(sy+cheight<rect.bottom){
			dy=rect.bottom-cheight;
		}
		}else{
			dy=rect.top+(rect.height()-cheight)/2f;
		}
		if (anim)
		{
			anime.setObjectValues(new float[]{sx,dx},new float[]{sy,dy});
			anime.setEvaluator(mTypeEvaluator);
			anime.start();
		}
		else
		{
			bmatrix.getValues(matrix);
			matrix[Matrix.MTRANS_X]=dx;
			matrix[Matrix.MTRANS_Y]=dy;
			bmatrix.setValues(matrix);
		}
		//判断宽或高是否越界
	}
	public float getMinScale()
	{
		return Math.min(getWidth() / (width * this.scale), getHeight() / (height * this.scale)) * 0.5f;

	}
	public float getMaxScale()
	{
		return Math.max(2 * Math.max(getWidth(), width) / (width * this.scale), 2 * Math.max(getHeight(), height) / (height * this.scale));
	}
	/*private void checkImageScale(float focusX, float focusY)
	{
		bmatrix.getValues(matrix);
		float scale=matrix[Matrix.MSCALE_X] / backScale;
		if (scale < getMinScale())
		{
			bmatrix.postScale(getMinScale() / scale, getMinScale() / scale, focusX, focusY);
			foreMatrix.postScale(getMinScale() / scale, getMinScale() / scale, focusX, focusY);

		}
	}*/
	public Bitmap getBitmap(Rect rect, float scale)
	{
		if (mBitmapRegionDecoder == null)return null;
		options.inSampleSize = calculateInSampleSize(width, height, scale);
		return mBitmapRegionDecoder.decodeRegion(rect, options);
	}
	public void setImage(String path)
	{
		if (path == null)
		{
			if (mBitmapRegionDecoder != null)mBitmapRegionDecoder.recycle();
			mBitmapRegionDecoder = null;
		}
		else
			try
			{
				setImage(getContext().getContentResolver().openFileDescriptor(Uri.fromFile(new File(path)), "r").getFileDescriptor());
			}
			catch (FileNotFoundException e)
			{}
	}

	public void setImage(FileDescriptor fd)
	{
		if (mBitmapRegionDecoder != null)
			mBitmapRegionDecoder.recycle();
		try
		{
			BitmapFactory.Options bo=new BitmapFactory.Options();
			bo.inJustDecodeBounds = true;
			BitmapFactory.decodeFileDescriptor(fd, null, bo);
			width = bo.outWidth;
			height = bo.outHeight;
			if (width <= 0 || height <= 0)throw new IOException();
			mBitmapRegionDecoder = BitmapRegionDecoder.newInstance(fd, false);

		}
		catch (IOException e)
		{mBitmapRegionDecoder = null;}
		if (mBitmapRegionDecoder != null)
		{
			checkPoint();
		}
		invalidate();
	}

	private void checkPoint()
	{
		if (isShown())
		{
			float sx=getWidth() / (float) width;
			float sy=getHeight() / (float) height;
			scale = Math.min(sx, sy);
			imageRect.set(0, 0, width, height);
			if (backgroundBitmap != null)backgroundBitmap.recycle();
			backgroundBitmap = getBitmap(imageRect, scale);
			if (backgroundBitmap == null)
			{
				if (mBitmapRegionDecoder != null)
					mBitmapRegionDecoder.recycle();
				mBitmapRegionDecoder = null;
			}
			else
			{
				backScale = width * scale / (float)backgroundBitmap.getWidth();
				bmatrix.setScale(backScale, backScale);
				bmatrix.postTranslate((getWidth() - width * scale) / 2f, (getHeight() - height * scale) / 2f);
			}
		}
	}
    public static int calculateInSampleSize(int width, int height, float scale)
	{
       /* final float reqHeight = height * scale;
        final float reqWidth = width * scale;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth)
		{
            final int halfHeight = height;
            final int halfWidth = width;
            while ((halfHeight / inSampleSize) > reqHeight
				   && (halfWidth / inSampleSize) > reqWidth)
			{
                inSampleSize *= 2;
            }
        }
        return inSampleSize;*/
		return BitmapUtils.calculateInSampleSize(width,height,(int)(width*scale),(int)(height*scale));
    }

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (backgroundBitmap == null)return false;
		boolean f= mGestureDetector.onTouchEvent(event);
		boolean s=mScaleGestureDetector.onTouchEvent(event);
		//mGestureDetector.onTouchEvent(event);
		if (!f&&(event.getAction()== event.ACTION_CANCEL || event.getAction()== event.ACTION_UP)&&!scaleAnime.isRunning())
		{
			//if (!scaleAnime.isRunning()&&mOverScroller.isFinished()){
				anime.setDuration(300);
				checkImagePosition(viewRectf, true);
				//}
		}
		return f || s;
	}
	@Override
	public boolean onSingleTapConfirmed(MotionEvent p1)
	{
		// TODO: Implement this method
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent p1)
	{
		focusX = p1.getX();
		focusY = p1.getY();
		bmatrix.getValues(matrix);
		if (mode == 0)
		{
			//放大两倍
			mode = 1;
			scaleAnime.setFloatValues(matrix[Matrix.MSCALE_X] / backScale, 2);
		}
		else
		{
			mode = 0;
			scaleAnime.setFloatValues(matrix[Matrix.MSCALE_X] / backScale, 1);
		}
		scaleAnime.start();
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onDown(MotionEvent p1)
	{
		if(!mOverScroller.isFinished())
		mOverScroller.forceFinished(false);
		scrolling=false;
		getParent().requestDisallowInterceptTouchEvent(true);
		return true;
	}

	@Override
	public void onShowPress(MotionEvent p1)
	{
		// TODO: Implement this method
	}

	@Override
	public boolean onSingleTapUp(MotionEvent p1)
	{
		// TODO: Implement this method
		return true;
	}
	@Override
	public boolean onScroll(MotionEvent p1, MotionEvent p2, float p3, float p4)
	{
		
		//判断是否在边界，越界后允许拦截
		bmatrix.getValues(matrix);
		float cWidth=backgroundBitmap.getWidth() * matrix[Matrix.MSCALE_X];
		float cHeight=backgroundBitmap.getHeight() * matrix[Matrix.MSCALE_Y];
		if(cWidth<=getWidth()){
			p3=0;
			getParent().requestDisallowInterceptTouchEvent(false);
			}
		if(cHeight<=getHeight())
			p4=0;
		float currentX=matrix[Matrix.MTRANS_X];
		float currentCX=currentX + cWidth;
		float currentY=matrix[Matrix.MTRANS_Y];
		float currentCY=currentY+cHeight;
		if (p3 < 0)
		{
			//右滑
			if (currentX < 0)
			{
				//if(currentX-p3>100)
				//p3=currentX-100;
				scrolling=true;
			}
			else
			{
				//p3=0;
				getParent().requestDisallowInterceptTouchEvent(scrolling);
			}
		}
		else if (p3 > 0)
		{
			if (currentCX > getWidth())
			{
				//if(currentCX-p3<getWidth()-100)
				//p3=currentCX-getWidth()+100;
				scrolling=true;
			}
			else
			{
				//p3=0;
				getParent().requestDisallowInterceptTouchEvent(scrolling);
			}
		}
		if (p4 < 0)
		{
			//右滑
			if (currentY < 100)
			{
				if(currentY-p4>100)
				p4=currentY-100;
			}
			else
			{
				p4=0;
			}
		}
		else if (p4 > 0)
		{
			if (currentCY > getHeight()-100)
			{
				if(currentCY-p4<getHeight()-100)
					p4=currentCY-getHeight()+100;
				
			}
			else
			{
				p4=0;
			}
		}
		bmatrix.postTranslate(-p3, -p4);
		foreMatrix.postTranslate(-p3, -p4);
		postInvalidateOnAnimation();
		//}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent p1)
	{
		// TODO: Implement this method
	}

	@Override
	public boolean onFling(MotionEvent p1, MotionEvent p2, float vx, float vy)
	{
		foreBitmap=null;
		bmatrix.getValues(matrix);
		float sx=matrix[Matrix.MTRANS_X];
		float sy=matrix[Matrix.MTRANS_Y];
		int cWidth=(int)(backgroundBitmap.getWidth() * matrix[Matrix.MSCALE_X]);
		int cHeight=(int)(backgroundBitmap.getHeight() * matrix[Matrix.MSCALE_Y]);
		if(!(sx>0||sy>0||sx+cWidth<getWidth()||sy+cHeight<getHeight())){
		mOverScroller.fling((int)sx,(int)sy,(int)vx,(int)vy,getWidth()-cWidth-50,50,getHeight()-cHeight-50,50);
		postInvalidateOnAnimation();
		return true;
		}else if(sx<0&&sx+cWidth>getWidth()){
			mOverScroller.fling((int)sx,(int)sy,(int)vx,0,getWidth()-cWidth-50,50,(int)sy,(int)sy);
			postInvalidateOnAnimation();
			return true;
			
		}else if(sy<0&&sy+cHeight>getHeight()){
			mOverScroller.fling((int)sx,(int)sy,0,(int)vy,(int)sx,(int)sx,getHeight()-cHeight-50,50);
			postInvalidateOnAnimation();
			return true;
			
		}
		return false;
	}

	@Override
	public void computeScroll()
	{
		if(!mOverScroller.isFinished()&& mOverScroller.computeScrollOffset())
		{
			bmatrix.getValues(matrix);
			matrix[Matrix.MTRANS_X]=mOverScroller.getCurrX();
			matrix[Matrix.MTRANS_Y]=mOverScroller.getCurrY();
			bmatrix.setValues(matrix);
			postInvalidateOnAnimation();
			if(mOverScroller.isFinished()){
				anime.setDuration(150);
				checkImagePosition(viewRectf,true);
				}
		}
	}

	@Override
	public boolean onScale(ScaleGestureDetector p1)
	{
		float scale=p1.getScaleFactor();
		bmatrix.getValues(matrix);
		float csx=matrix[Matrix.MSCALE_X] / backScale;
		//float csy=matrix[Matrix.MSCALE_Y]/scaleY;
		float minScale=Math.min(0.5f * Math.min(getWidth(), width) / (width * this.scale), 0.5f * Math.min(getHeight(), height) / (height * this.scale));
		float maxScale=Math.max(2 * Math.max(getWidth(), width) / (width * this.scale), 2 * Math.max(getHeight(), height) / (height * this.scale));
		if (csx * scale < minScale)
			scale = minScale / csx;
		else if (csx * scale > maxScale)
			scale = maxScale / csx;
		/*if(csx*width*this.scale*scale<0.5f*Math.min(getWidth(),width))
		 scale=0.5f*Math.min(getWidth(),width)/(width*this.scale)/csx;
		 else if(csx*width*this.scale*scale>2*Math.max(getWidth(),width))
		 scale=2*Math.max(getWidth(),width)/(width*this.scale)/csx;
		 else if(csy*height*this.scale*scale<0.5f*Math.min(getHeight(),height)){
		 scale=0.5f*Math.min(getHeight(),height)/(height*this.scale)/csy;
		 }else if(csy*height*this.scale*scale>2*Math.max(getHeight(),height))
		 scale=2*Math.max(getHeight(),height)/(height*this.scale)/csy;
		 */
		bmatrix.postScale(scale, scale, p1.getFocusX(), p1.getFocusY());
		foreMatrix.postScale(scale, scale, p1.getFocusX(), p1.getFocusY());
		postInvalidateOnAnimation();
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector p1)
	{
		getParent().requestDisallowInterceptTouchEvent(true);
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector p1)
	{
		//checkImageScale(p1.getFocusX(),p1.getFocusY());
	}

	@Override
	public void onAnimationUpdate(ValueAnimator p1)
	{
		if (p1 != scaleAnime)
		{
			float[] value=(float[]) p1.getAnimatedValue();
			bmatrix.getValues(matrix);
			matrix[Matrix.MTRANS_X]=value[0];
			matrix[Matrix.MTRANS_Y]=value[1];
			bmatrix.setValues(matrix);
			}
		else
		{
			float scale=(float)p1.getAnimatedValue() * backScale / matrix[Matrix.MSCALE_X];
			bmatrix.getValues(matrix);
			bmatrix.postScale(scale, scale, focusX, focusY);
			checkImagePosition(viewRectf,false);
			
		}
		postInvalidateOnAnimation();
	}


	class PositionEvaluator implements TypeEvaluator<float[]>
	{

		@Override
		public float[] evaluate(float p1, float[] p2, float[] p3)
		{
			
			return new float[]{p2[0]+(p2[1]-p2[0])*p1,p3[0]+(p3[1]-p3[0])*p1};
		}
		

		

	}
	class AnimeListener extends AnimatorListener
	{

		@Override
		public void onAnimationEnd(Animator p1)
		{
			/*if(p1==flingAnime)
				checkImagePosition(viewRectf,true);
			else*/
			if(mOverScroller.isFinished()&&!scaleAnime.isRunning()&&!anime.isRunning())
			new Thread(){
					public void run()
					{
						//计算图片显示区域矩阵
						bmatrix.getValues(matrix);
						//当前图片宽高
						float width=backgroundBitmap.getWidth() * matrix[Matrix.MSCALE_X];
						float height=backgroundBitmap.getHeight() * matrix[Matrix.MSCALE_Y];
						float scale=width / CropImageView.this.width;
						float offsetX = 0,offsetY = 0;
						if (matrix[Matrix.MTRANS_X] < 0)
						{
							imageRect.left = -Math.round(matrix[Matrix.MTRANS_X] / scale);

						}
						else
						{
							imageRect.left = 0;
							offsetX = matrix[Matrix.MTRANS_X];
						}
						if (matrix[Matrix.MTRANS_X] + width > getWidth())
							imageRect.right = Math.round((getWidth() - matrix[Matrix.MTRANS_X]) / scale);
						else
							imageRect.right = CropImageView.this.width;
						if (matrix[Matrix.MTRANS_Y] < 0)
						{
							imageRect.top = -Math.round(matrix[Matrix.MTRANS_Y] / scale);
						}
						else
						{
							imageRect.top = 0;
							offsetY = matrix[Matrix.MTRANS_Y];
						}
						if (matrix[Matrix.MTRANS_X] + height > getHeight())
						{
							imageRect.bottom = Math.round((getHeight() - matrix[Matrix.MTRANS_Y]) / scale);
						}
						else
						{
							imageRect.bottom = CropImageView.this.height;
						}
						//if(foreBitmap!=null)
						//foreBitmap.recycle();
						System.gc();
						foreBitmap = getBitmap(imageRect, scale);
						if(scaleAnime.isRunning()||anime.isRunning()||!mOverScroller.isFinished())
							foreBitmap=null;
						if (foreBitmap != null)
							{
								foreMatrix.setScale(imageRect.width() * scale / (float)foreBitmap.getWidth(), imageRect.height() * scale / (float)foreBitmap.getHeight());
								foreMatrix.postTranslate(offsetX, offsetY);
							}
							postInvalidateOnAnimation();
					}
				}.start();
		}

		@Override
		public void onAnimationStart(Animator p1)
		{
			foreBitmap = null;
			postInvalidateOnAnimation();
		}

	}
	
}
