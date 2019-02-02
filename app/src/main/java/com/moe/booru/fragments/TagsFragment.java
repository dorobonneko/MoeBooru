package com.moe.booru.fragments;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.moe.booru.core.*;
import com.moe.booru.empty.*;
import com.moe.booru.utils.*;
import java.util.*;
import okhttp3.*;

import android.support.v7.widget.LinearLayoutManager;
import com.moe.booru.R;
import com.moe.booru.adapter.PoolAdapter;
import java.io.IOException;
import com.moe.booru.adapter.TagAdapter;
import com.moe.booru.database.SiteDatabase;

public class TagsFragment extends RecyclerViewFragment implements BooruManager.OnBooruChangedListener,Callback,Handler.Callback,TagAdapter.OnItemClickListener,
View.OnClickListener
{
	private OkHttp okhttp;
	private int page=1;
	private Booru currentBooru;
	private Handler mHandler;
	private List<Tag> list;
	private TagAdapter mTagAdapter;
	private String query;
	private boolean canloadMore=true;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		mHandler=new Handler(this);
		setHasOptionsMenu(true);
	}

	@Override
	public String getTitle()
	{
		// TODO: Implement this method
		return "Tags";
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onViewCreated(view, savedInstanceState);
		getRecyclerView().setAdapter(mTagAdapter=new TagAdapter(list=new ArrayList<>()));
		getRecyclerView().setLayoutManager(new LinearLayoutManager(view.getContext()));
		getRecyclerView().addItemDecoration(new StaggeredGridItemDecoration(10,1));
		view.requestApplyInsets();
		mTagAdapter.setOnItemClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		okhttp=OkHttp.getInstance(CacheManager.getInstance(getActivity().getApplicationContext()));
		BooruManager bm=BooruManager.getInstance(getActivity().getApplicationContext());
		currentBooru=bm.getBooru();
		bm.registerOnBooruChangedListener(this);
		getView().post(new Runnable(){

				@Override
				public void run()
				{
					setRefreshing(true);
					onRefresh();
				}
			});
	}

	@Override
	public void onRefresh()
	{
		page=1;
		canloadMore=true;
		if(currentBooru==null)
			setRefreshing(false);
		else
			onLoadMore();
	}

	@Override
	public void onLoadMore()
	{
		if(currentBooru==null)return;
		if(canloadMore){
			Request request=new Request.Builder().url(currentBooru.getTagsUrl(50,page,"order=name","name=".concat(query==null?"":query))).build();
			okhttp.getClient().newCall(request).enqueue(this);
		}else{
			getSwipeRefreshLayout().setRefreshing(false);
		}
	}

	@Override
	public void onChanged(Booru booru)
	{
		query=null;
		currentBooru=booru;
		list.clear();
		mTagAdapter.notifyDataSetChanged();
		setRefreshing(true);
		onRefresh();
	}

	@Override
	public void onResponse(Call p1, final Response p2) throws IOException
	{
		new Thread(){
			public void run(){
				try
				{
					List<Tag> list=currentBooru.list(OkHttp.readString(p2.body().byteStream(), p2.body().contentLength()),Tag.class);
					p2.close();
					mHandler.obtainMessage(0,list).sendToTarget();
				}
				catch (IOException e)
				{}
			}
		}.start();
	}

	@Override
	public void onFailure(Call p1, final IOException p2)
	{
		mHandler.post(new Runnable(){

				@Override
				public void run()
				{
					getSwipeRefreshLayout().setRefreshing(false);
					Toast.makeText(getActivity().getApplicationContext(),p2.getMessage(),Toast.LENGTH_SHORT).show();
				}
			});
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		switch(msg.what){
			case 0:
				if(page==1){
					list.clear();
				}
				List<Tag> tmp=(List<Tag>) msg.obj;
				canloadMore=tmp.size()>0;
				Iterator<Tag> iterator=tmp.iterator();
				while(iterator.hasNext()){
					Tag pool=iterator.next();
					if(!list.contains(pool))
						list.add(pool);
					iterator.remove();
				}
				page++;
				mTagAdapter.notifyDataSetChanged();
				if(canloadMore)
					setRefreshing(false);
				else
					getSwipeRefreshLayout().setRefreshing(false);
				break;
		}
		return true;
	}

	@Override
	public void onItemClick(TagAdapter adapter, final TagAdapter.ViewHolder vh)
	{
		final String tag=String.valueOf(list.get(vh.getPosition()).name);
		PostsListFragment plf=new PostsListFragment();
		plf.setTags(tag);
		getFragmentManager().beginTransaction().add(android.R.id.content,plf).addToBackStack(null).commitAllowingStateLoss();
		new Thread(){
			public void run(){
				SiteDatabase.getInstance(vh.itemView.getContext()).insertSearch(tag);
			}
		}.start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.search:
				ViewFlipper viewflipper=getActivity().findViewById(R.id.viewflipper);
				viewflipper.setDisplayedChild(1);
				Toolbar search_toolbar=viewflipper.findViewById(R.id.search_toolbar);
				search_toolbar.findViewById(R.id.search).setOnClickListener(this);
				EditText key=search_toolbar.findViewById(R.id.search_key);
				key.setText(null);
				key.requestFocusFromTouch();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.search,menu);
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		// TODO: Implement this method
		super.onHiddenChanged(hidden);
		if(hidden){
			ViewFlipper viewflipper=getActivity().findViewById(R.id.viewflipper);
			viewflipper.setDisplayedChild(0);
			if(query!=null){
				query=null;
				setRefreshing(true);
				onRefresh();
			}
		}
	}

	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.search:
				EditText key=((EditText)((View)p1.getParent()).findViewById(R.id.search_key));
				key.clearFocus();
				query=key.getText().toString().trim();
				setRefreshing(true);
				onRefresh();

				break;
			case R.id.viewflipper:
			case -1:
				if(query!=null){
					query=null;
					setRefreshing(true);
					onRefresh();
				}
				break;
		}
	}
}



