package com.moe.booru.fragments;
import android.view.View;
import android.os.Bundle;
import android.view.LayoutInflater;
import com.moe.booru.widget.SwipeLayout;
import com.moe.booru.R;
import android.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import com.moe.booru.adapter.PostAdapter;
import android.content.SharedPreferences;
import android.support.v7.widget.StaggeredGridLayoutManager;
import com.moe.booru.utils.StaggeredGridItemDecoration;
import com.moe.booru.core.BooruManager;
import okhttp3.Callback;
import android.os.Handler;
import com.moe.booru.utils.Filter;
import android.support.v7.widget.RecyclerView.Adapter;
import com.moe.booru.adapter.ImageAdapter.ViewHolder;
import com.moe.booru.adapter.ImageAdapter;
import com.moe.booru.core.Booru;
import okhttp3.Call;
import okhttp3.Response;
import java.io.IOException;
import com.moe.booru.utils.OkHttp;
import android.view.WindowInsets;
import android.support.v4.view.ViewCompat;
import java.io.InputStream;
import com.moe.booru.utils.JsonCache;
import com.moe.booru.utils.CacheManager;
import android.os.Message;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSON;
import java.util.List;
import com.moe.booru.empty.Post;
import android.widget.Toast;
import com.moe.booru.io.NetInputStream;
import okhttp3.Request;
import android.util.TypedValue;
import com.moe.booru.io.CacheInputStream;
import android.widget.CompoundButton;
import com.moe.booru.empty.SiteUser;

public class PostsListFragment extends FloatFragment implements BooruManager.OnBooruChangedListener,Callback,Handler.Callback,Filter<Integer>,PostAdapter.OnFavListener,PostAdapter.OnItemClickListener,SwipeRefreshLayout.OnRefreshListener,View.OnClickListener
{
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView mRecyclerView;
	//private static final int LOADCACHE=1;
	private static final int PROCESS=0;
	private Booru currentBooru;
	private OkHttp mOkHttp;
	private PostAdapter mPostAdapter;
	private int page=1;
	private Handler mHandler;
	private Call call;
	private SharedPreferences settings;
	private boolean canLoadMore=true;
	private Scroll scroll=new Scroll();
	private String tags;
	public void setTags(String tags)
	{
		this.tags = tags;
	}
	@Override
	public View onCreateContentView(LayoutInflater inflater, SwipeLayout container, Bundle saveInstanceState)
	{
		// TODO: Implement this method
		return inflater.inflate(R.layout.posts_list_view, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onViewCreated(view, savedInstanceState);
		Toolbar toolbar=view.findViewById(R.id.toolbar);
		toolbar.setTitle("Posts");
		toolbar.setSubtitle(tags);
		toolbar.setNavigationIcon(R.drawable.arrow_left);
		toolbar.setNavigationOnClickListener(this);
		//getActivity().setActionBar(toolbar);
		ViewGroup group=(ViewGroup) toolbar.getParent();
		group.setFitsSystemWindows(true);
		group.setOnApplyWindowInsetsListener(this);
		group.requestApplyInsets();
		mSwipeRefreshLayout = view.findViewById(R.id.refresh);
		mRecyclerView = view.findViewById(R.id.recyclerview);
		int spanCount=(int)(view.getResources().getDisplayMetrics().widthPixels / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, view.getResources().getDisplayMetrics()));
		getRecyclerView().setLayoutManager(new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL));
		getRecyclerView().addItemDecoration(new StaggeredGridItemDecoration((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, view.getResources().getDisplayMetrics()), spanCount));
		getRecyclerView().setAdapter(mPostAdapter = new PostAdapter());
		mPostAdapter.setShowBottomBar(settings.getBoolean("bottom_info_bar", false));
		//mPostAdapter.setSafe(settings.getBoolean("safe",true));
		mPostAdapter.setOnFavListener(this);
		mPostAdapter.setOnItemClickListener(this);
		mRecyclerView.setFitsSystemWindows(true);
		mRecyclerView.setOnApplyWindowInsetsListener(this);
		mRecyclerView.setClipToPadding(false);
		ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);
		mSwipeRefreshLayout.setColorSchemeColors(new int[]{0xffff0000,0xffff7f00,0xffcfcf00,0xff00ff00,0xff00ffff,0xff0000ff,0xff8b00ff});
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setFitsSystemWindows(true);
		mSwipeRefreshLayout.setOnApplyWindowInsetsListener(this);
		mRecyclerView.setItemAnimator(null);
		ViewCompat.setNestedScrollingEnabled(mSwipeRefreshLayout, false);
		mRecyclerView.addOnScrollListener(scroll);
		//getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		//getActivity().getActionBar().setHomeButtonEnabled(true);

	}
	public SwipeRefreshLayout getSwipeRefreshLayout()
	{
		return mSwipeRefreshLayout;
	}
	@Override
	public WindowInsets onApplyWindowInsets(View p1, WindowInsets p2)
	{
		int height=getActivity().findViewById(R.id.toolbar).getLayoutParams().height;
		if (p1 == mSwipeRefreshLayout)
		{
			mSwipeRefreshLayout.setProgressViewOffset(false, mSwipeRefreshLayout.getProgressViewStartOffset(), p2.getSystemWindowInsetTop() + height);
			mSwipeRefreshLayout.setProgressViewEndTarget(false, p2.getSystemWindowInsetTop());}
		else if (p1 == mRecyclerView)
		{
			mRecyclerView.setPaddingRelative(0, p2.getSystemWindowInsetTop() + height, 0, p2.getSystemWindowInsetBottom());
		}
		else
		{
			super.onApplyWindowInsets(p1, p2);
		}
		return p2;
	}
	public RecyclerView getRecyclerView()
	{
		return mRecyclerView;
	}
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		settings = getActivity().getSharedPreferences("moe", 0);
		super.onCreate(savedInstanceState);
		//setHasOptionsMenu(true);
		mHandler = new Handler(this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		// TODO: Implement this method
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onItemClick(RecyclerView.Adapter<ImageAdapter.ViewHolder> adapter, ImageAdapter.ViewHolder vh)
	{
		PostViewFragment pvf=new PostViewFragment();
		pvf.setList(mPostAdapter.getList(), vh.getPosition());
		getFragmentManager().beginTransaction().add(android.R.id.content, pvf).addToBackStack(null).commitAllowingStateLoss();
	}


	@Override
	public boolean accept(Integer t)
	{
		synchronized (mPostAdapter)
		{
			for (int i=0;i < mPostAdapter.getItemCount();i++)
			{
				if (mPostAdapter.get(i).id == t.intValue())
					return false;
			}
		}
		return true;
	}

	@Override
	public void onFav(RecyclerView.Adapter<ImageAdapter.ViewHolder> adapter, ImageAdapter.ViewHolder vh, CompoundButton checked)
	{

	}

	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case -1:
				getFragmentManager().popBackStack();
				break;
		}
	}




	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		BooruManager.getInstance(getActivity()).registerOnBooruChangedListener(this);
		currentBooru = BooruManager.getInstance(getActivity()).getBooru();
		mPostAdapter.setBooru(currentBooru);
		mOkHttp = OkHttp.getInstance(CacheManager.getInstance(getActivity()));
		mHandler.post(new Runnable(){
				public void run()
				{
					setRefreshing(true);
					onRefresh();
				}});
	}

	@Override
	public void onChanged(Booru booru)
	{
		mPostAdapter.setBooru(booru);
		if (call != null)call.cancel();
		setRefreshing(true);
		currentBooru = booru;
		onRefresh();
		//mHandler.sendEmptyMessageDelayed(LOADCACHE,200);
	}

	/*private void loadCache()
	 {
	 canLoadMore=true;
	 if(currentBooru==null)return;
	 final InputStream is=JsonCache.getInstance(getActivity()).getInputStream(currentBooru,Booru.Item.POSTS);
	 if(is!=null)
	 new com.moe.booru.lang.Thread(){
	 public void run()
	 {
	 loadData(is,-1,true);
	 }
	 }.start();
	 else
	 {
	 setRefreshing(true);
	 onRefresh();
	 }
	 }
	 */
	@Override
	public void onRefresh()
	{
		canLoadMore = true;
		page = 1;
		onLoadMore();
	}

	public void onLoadMore()
	{
		if (currentBooru == null || !canLoadMore)return;
		SiteUser user=(SiteUser) currentBooru.getSite().getUser();
		String url=null;
		if(user!=null)
			url=currentBooru.getPostsUrl(50, page,user.name,user.password_hash,tags);
		else
			url=currentBooru.getPostsUrl(50, page,null,null,tags);
		Request request=new Request.Builder().url(url).build();
		call = mOkHttp.getClient().newCall(request);
		call.enqueue(this);

	}

	@Override
	public void onResponse(final Call p1, final Response p2) throws IOException
	{
		new com.moe.booru.lang.Thread(){
			public void run()
			{
				loadData(new NetInputStream(p2), p2.body().contentLength(), false);
			}
		}.start();
	}

	@Override
	public void onFailure(Call p1, final IOException p2)
	{
		if (getView() != null)
			getView().post(new Runnable(){
					public void run()
					{
						getSwipeRefreshLayout().setRefreshing(false);
					}});
		if (p1.isCanceled())return;
		mHandler.post(new Runnable(){
				public void run()
				{
					if (getActivity() != null)
						Toast.makeText(getActivity().getApplicationContext(), p2.getMessage(), Toast.LENGTH_SHORT).show();
				}
			});
	}
	private void loadData(InputStream res, long length, boolean isCache)
	{

		try
		{
			List<Post> list=JSON.parseArray(OkHttp.readString(res, length), Post.class);
			res.close();
			//List<Post> list=//currentBooru.list(Booru.Item.POSTS, ja, page==1?null:this);
			mHandler.obtainMessage(PROCESS, list).sendToTarget();
		}
		catch (IOException e)
		{}
		/*XmlPullParser parser=Xml.newPullParser();
		 try
		 {
		 parser.setInput(res.body().charStream());
		 List<Post> list=currentBooru.list(Booru.Item.POSTS,parser,page==1?null:this);
		 mHandler.obtainMessage(PROCESS,list).sendToTarget();
		 }
		 catch (XmlPullParserException e)
		 {}
		 catch(IOException i){}*/
		// (OkHttp.readString(res));

	}

	@Override
	public boolean handleMessage(Message p1)
	{
		switch (p1.what)
		{
			case PROCESS:
				if (page == 1)
					mPostAdapter.clear();
				mPostAdapter.addAll((List<Post>)p1.obj);
				List list=((List<Post>)p1.obj);
				canLoadMore = list.size() > 0;
				list.clear();
				setRefreshing(false);
				page++;
				break;
				/*case LOADCACHE:
				 mPostAdapter.clear();
				 loadCache();
				 break;*/
		}
		return true;
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
		BooruManager.getInstance(getActivity()).unregisterOnBooruChangedListener(this);
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
					if (canLoadMore && !isRefreshing() && range >= e && e + o >= range - e / 2)
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
