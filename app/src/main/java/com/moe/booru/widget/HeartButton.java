package com.moe.booru.widget;
import android.widget.ToggleButton;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;

public class HeartButton extends ToggleButton
{
	private OnCheckedChangeListener l;
	public HeartButton(Context context,AttributeSet attrs){
		super(context,attrs,0,0);
	}

	@Override
	public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener)
	{
		l=listener;
	}

	@Override
	public void toggle()
	{
		// TODO: Implement this method
		super.toggle();
		if(l!=null)l.onCheckedChanged(this,isChecked());
	}
	 
}
