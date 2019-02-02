package com.moe.booru.widget;
import android.widget.FrameLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.moe.booru.R;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.View;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateInterpolator;
import android.animation.FloatEvaluator;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;

public class TextInput extends FrameLayout implements View.OnFocusChangeListener,ValueAnimator.AnimatorUpdateListener
{
	private String digits;
	private EditText edit;
	private Paint paint;
	private int accentColor,hintColor;
	private float accentTextSize,hintTextSize,fractor=0,hintY,hintX;
	private int accentTextHeight,hintTextHeight;
	private String hint;
	private ArgbEvaluator mArgbEvaluator;
	private ValueAnimator mValueAnimator;
	private FloatEvaluator mFloatEvaluator;
	public TextInput(Context context)
	{
		this(context, null);
	}
	public TextInput(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.text_input);
		hint = ta.getString(R.styleable.text_input_hint);
		accentColor = ta.getColor(R.styleable.text_input_accentColor, 0xff000000);
		accentTextSize = ta.getDimension(R.styleable.text_input_accentSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
		hintColor = ta.getColor(R.styleable.text_input_hintColor, 0xffbbbbbb);
		hintTextSize = ta.getDimension(R.styleable.text_input_hintSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
		digits = ta.getString(R.styleable.text_input_digits);
		ta.recycle();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		paint.setTextSize(accentTextSize);
		paint.setLinearText(true);
		Rect rect=new Rect();
		paint.getTextBounds(hint, 0, hint.length(), rect);
		accentTextHeight = rect.height();
		paint.setTextSize(hintTextSize);
		paint.getTextBounds(hint, 0, hint.length(), rect);
		hintTextHeight = rect.height();
		hintX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
		edit = new EditText(context);
		TextInput.LayoutParams tl=new TextInput.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		tl.setMargins(0, accentTextHeight, 0, 0);
		edit.setLayoutParams(tl);
		addView(edit);
		edit.setOnFocusChangeListener(this);
		edit.setSingleLine();
		if (digits != null)
			edit.setFilters(new InputFilter[]{new InputFilter(){

									@Override
									public CharSequence filter(CharSequence p1, int p2, int p3, Spanned p4, int p5, int p6)
									{
										for (;p2 < p3;p2++)
										{
											if (digits.indexOf(p1.charAt(p2)) == -1)return "";
										}
										return p1;
									}
								}});
		mArgbEvaluator = new ArgbEvaluator();
		mFloatEvaluator = new FloatEvaluator();
		setWillNotDraw(false);
	}

	public boolean requestTextFocus()
	{
		return edit.requestFocus();
	}

	@Override
	public void clearFocus()
	{
		edit.clearFocus();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO: Implement this method
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		hintY = accentTextHeight * 2 + (edit.getMeasuredHeight() - hintTextHeight) / 2f;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if (getText().length() == 0)
		{
			paint.setColor(mArgbEvaluator.evaluate(fractor, hintColor, accentColor));
			paint.setTextSize(mFloatEvaluator.evaluate(fractor, hintTextSize, accentTextSize));
			canvas.drawText(hint, mFloatEvaluator.evaluate(fractor, hintX, hintX / 2), mFloatEvaluator.evaluate(fractor, hintY, accentTextHeight), paint);
		}
		else
		{
			paint.setColor(accentColor);
			paint.setTextSize(accentTextSize);
			canvas.drawText(hint, hintX/2, accentTextHeight, paint);
		}
	}

	@Override
	public void onFocusChange(View p1, boolean p2)
	{
		if (mValueAnimator == null)
		{
			mValueAnimator = new ValueAnimator();
			mValueAnimator.setDuration(100);
			mValueAnimator.setInterpolator(new AccelerateInterpolator());
			mValueAnimator.addUpdateListener(this);
		}
		if (p2)
		{
			mValueAnimator.setFloatValues(0, 1);
		}
		else
		{
			mValueAnimator.setFloatValues(1, 0);
		}
		mValueAnimator.start();
	}

	@Override
	public void onAnimationUpdate(ValueAnimator p1)
	{
		fractor = p1.getAnimatedValue();
		postInvalidateOnAnimation();
	}

	public Editable getText()
	{
		return edit.getText();
	}
	public void setText(CharSequence text)
	{
		edit.setText(text);
	}
}
