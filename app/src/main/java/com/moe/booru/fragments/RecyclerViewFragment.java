package com.moe.booru.fragments;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.os.Bundle;
import com.moe.booru.R;
import android.support.v7.widget.RecyclerView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.WindowInsets;
import android.util.TypedValue;
import android.support.v4.view.ViewCompat;

public class RecyclerViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,View.OnApplyWindowInsetsListener
{
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView mRecyclerView;
	private Scroll scroll=new Scroll();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		return inflater.inflate(R.layout.list_view, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onViewCreated(view, savedInstanceState);
		mRecyclerView = view.findViewById(R.id.recyclerview);
		mRecyclerView.setFitsSystemWindows(true);
		mRecyclerView.setOnApplyWindowInsetsListener(this);
		mRecyclerView.setClipToPadding(false);
		ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);
		mSwipeRefreshLayout = view.findViewById(R.id.refresh);
		mSwipeRefreshLayout.setColorSchemeColors(new int[]{0xffff0000,0xffff7f00,0xffcfcf00,0xff00ff00,0xff00ffff,0xff0000ff,0xff8b00ff});
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setFitsSystemWindows(true);
		mSwipeRefreshLayout.setOnApplyWindowInsetsListener(this);
		mRecyclerView.setItemAnimator(null);
		ViewCompat.setNestedScrollingEnabled(mSwipeRefreshLayout, false);
		mRecyclerView.addOnScrollListener(scroll);

	}

	@Override
	public WindowInsets onApplyWindowInsets(View p1, WindowInsets p2)
	{
		int height=getActivity().findViewById(R.id.toolbar).getLayoutParams().height;
		if (p1 == mSwipeRefreshLayout)
		{
			mSwipeRefreshLayout.setProgressViewOffset(false, mSwipeRefreshLayout.getProgressViewStartOffset(), p2.getSystemWindowInsetTop() + height);
			mSwipeRefreshLayout.setProgressViewEndTarget(false, p2.getSystemWindowInsetTop());}
		else
		{
			mRecyclerView.setPaddingRelative(0, p2.getSystemWindowInsetTop() + height, 0, p2.getSystemWindowInsetBottom());
		}
		return p2;
	}


	public RecyclerView getRecyclerView()
	{
		return mRecyclerView;
	}
	public SwipeRefreshLayout getSwipeRefreshLayout()
	{
		return mSwipeRefreshLayout;
	}
	@Override
	public void onRefresh()
	{

	}
	public void onLoadMore()
	{

	}
	public void setRefreshing(boolean refresh)
	{
		mSwipeRefreshLayout.setRefreshing(refresh);
		if (!refresh)checkCount();
	}
	public boolean isRefreshing()
	{
		return mSwipeRefreshLayout.isRefreshing();
	}
	@Override
	public void onDestroyView()
	{
		getRecyclerView().removeOnScrollListener(scroll);
		super.onDestroyView();
	}

	protected void checkCount()
	{
		mRecyclerView.postDelayed(new Runnable(){
				public void run()
				{
					int range=mRecyclerView.computeVerticalScrollRange();
					int e=mRecyclerView.computeVerticalScrollExtent();
					int o=mRecyclerView.computeVerticalScrollOffset();
					if (!isRefreshing()&& range >= e && e + o >= range - e / 2)
					{
						setRefreshing(true);
						onLoadMore();
					}
				}
			}, 100);
	}

	class Scroll extends RecyclerView.OnScrollListener
	{

		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState)
		{
			// TODO: Implement this method
			super.onScrollStateChanged(recyclerView, newState);
		}



		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy)
		{
			if (dy >= 0)
			{
				//StaggeredGridLayoutManager layout=(StaggeredGridLayoutManager) recyclerView.getLayoutManager();
				checkCount();
			}


		}

	}
}
