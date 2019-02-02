package com.moe.booru.fragments;
import android.view.View;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.moe.booru.widget.SwipeLayout;
import android.view.WindowInsets;

public abstract class FloatFragment extends Fragment implements SwipeLayout.OnSwipedListener,View.OnApplyWindowInsetsListener
{
	
	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		SwipeLayout sl=new SwipeLayout(container.getContext());
		sl.addView(onCreateContentView(inflater, sl, savedInstanceState));
		return sl;
	}
	public abstract View onCreateContentView(LayoutInflater inflater,SwipeLayout container,Bundle saveInstanceState);
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		((SwipeLayout)view).setOnSwipedListener(this);
		
	}

	@Override
	public void onSwiped(boolean show)
	{
		if(!show)
			getFragmentManager().popBackStack();
	}

	@Override
	public WindowInsets onApplyWindowInsets(View p1, WindowInsets p2)
	{
		p1.setPadding(p2.getSystemWindowInsetLeft(),p2.getSystemWindowInsetTop(),p2.getSystemWindowInsetRight(),p2.getSystemWindowInsetBottom());
		return p2;
	}


	
}
