package com.moe.booru.utils;
import android.animation.Animator;

public class AnimatorListener implements Animator.AnimatorListener
{

	@Override
	public void onAnimationStart(Animator p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onAnimationEnd(Animator p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onAnimationCancel(Animator p1)
	{
		onAnimationEnd(p1);
	}

	@Override
	public void onAnimationRepeat(Animator p1)
	{
		// TODO: Implement this method
	}
	
}
