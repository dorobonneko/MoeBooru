package com.moe.booru.internal;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.graphics.Canvas;
import com.moe.booru.widget.SwipeBarLayout;

public class SiteItemTouchCallback extends ItemTouchHelper.Callback
{

	@Override
	public int getMovementFlags(RecyclerView p1, RecyclerView.ViewHolder p2)
	{
		// TODO: Implement this method
		return makeMovementFlags(0,ItemTouchHelper.END|ItemTouchHelper.RIGHT);
	}

	@Override
	public boolean isItemViewSwipeEnabled()
	{
		return true;
	}

	@Override
	public boolean onMove(RecyclerView p1, RecyclerView.ViewHolder p2, RecyclerView.ViewHolder p3)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public void onSwiped(RecyclerView.ViewHolder p1, int p2)
	{
		// TODO: Implement this method
	}

	@Override
	public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
	{
		// TODO: Implement this method
		super.onChildDraw(c, recyclerView, viewHolder, 0, dY, actionState, isCurrentlyActive);
		
	}
	
}
