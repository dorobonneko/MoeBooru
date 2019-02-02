package com.moe.booru.fragments;
import android.view.View;
import android.os.Bundle;
import com.moe.booru.adapter.ImageAdapter;
import android.support.v7.widget.StaggeredGridLayoutManager;
import com.moe.booru.utils.StaggeredGridItemDecoration;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.Menu;
import com.moe.booru.R;
import com.moe.booru.core.BooruManager;
import com.moe.booru.core.Booru;
import com.moe.booru.utils.OkHttp;
import com.moe.booru.utils.CacheManager;
import okhttp3.Request;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParserException;
import com.moe.booru.adapter.PostAdapter;
import java.util.List;
import com.moe.booru.empty.Post;
import android.os.Handler;
import android.os.Message;
import com.moe.booru.utils.Filter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import android.support.v7.widget.RecyclerView;
import com.moe.booru.io.CacheInputStream;
import com.moe.booru.utils.JsonCache;
import java.io.InputStream;
import com.moe.booru.io.NetInputStream;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.support.v7.widget.RecyclerView.Adapter;
import com.moe.booru.adapter.ImageAdapter.ViewHolder;
import android.app.FragmentTransaction;
import android.widget.CompoundButton;
import com.moe.booru.empty.SiteUser;
import android.net.Uri;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import java.util.Iterator;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
public class PostsFragment extends RecyclerViewFragment implements BooruManager.OnBooruChangedListener,Callback,Handler.Callback,Filter<Integer>,PostAdapter.OnFavListener,PostAdapter.OnItemClickListener
{
	private static final int LOADCACHE=1;
	private static final int PROCESS=0;
	private Booru currentBooru;
	private OkHttp mOkHttp;
	private PostAdapter mPostAdapter;
	private int page=1;
	private Handler mHandler;
	private Call call;
	private SharedPreferences settings;
	private boolean canLoadMore=true;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onViewCreated(view, savedInstanceState);
		int spanCount=(int)(view.getResources().getDisplayMetrics().widthPixels / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, view.getResources().getDisplayMetrics()));
		getRecyclerView().setLayoutManager(new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL));
		getRecyclerView().addItemDecoration(new StaggeredGridItemDecoration((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, view.getResources().getDisplayMetrics()), spanCount));
		getRecyclerView().setAdapter(mPostAdapter = new PostAdapter());
		mPostAdapter.setShowBottomBar(settings.getBoolean("bottom_info_bar", false));
		mPostAdapter.setSafe(settings.getBoolean("safe", true));
		mPostAdapter.setOnFavListener(this);
		mPostAdapter.setOnItemClickListener(this);
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
		Object tag=checked.getTag(R.id.fav);
		if(tag instanceof String){
			checked.setTag(R.id.fav,null);
			return;
		}
		Post post=mPostAdapter.get(vh.getPosition());
		SiteUser user=(SiteUser) currentBooru.getSite().getUser();
		Call call=(Call) checked.getTag(R.id.fav);
		if(call!=null)call.cancel();
		Uri uri=null;
		if (checked.isChecked())
		{
			uri=Uri.parse(currentBooru.vote(post.id, 3,user.name,user.password_hash));
		}
		else
		{
			uri=Uri.parse(currentBooru.vote(post.id, 0,user.name,user.password_hash));
		}
		Request request=new Request.Builder().url(uri.toString()).tag(checked).post(RequestBody.create(null,uri.getQuery())).build();
		checked.setTag(R.id.fav,mOkHttp.getClient().newCall(request));
		checked.setTag(R.id.posts,post);
		checked.setTag(R.id.id,vh.getPosition());
		((Call)checked.getTag(R.id.fav)).enqueue(this);
	}




	@Override
	public String getTitle()
	{
		// TODO: Implement this method
		return "Posts";
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mHandler = new Handler(this);
		settings = getActivity().getSharedPreferences("moe", 0);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		// TODO: Implement this method
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.posts, menu);
		menu.findItem(R.id.bottom_info_bar).setChecked(settings.getBoolean("bottom_info_bar", false));
		menu.findItem(R.id.safe).setChecked(settings.getBoolean("safe", true));

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.search:
				SearchFragment search=new SearchFragment();
				FragmentTransaction ft=getFragmentManager().beginTransaction();
				ft.add(android.R.id.content, search);
				ft.addToBackStack(null);
				ft.commitAllowingStateLoss();
				return true;
			case R.id.bottom_info_bar:
				item.setChecked(!item.isChecked());
				mPostAdapter.setShowBottomBar(item.isChecked());
				settings.edit().putBoolean("bottom_info_bar", item.isChecked()).commit();
				return true;
			case R.id.safe:
				item.setChecked(!item.isChecked());
				mPostAdapter.setSafe(item.isChecked());
				settings.edit().putBoolean("safe", item.isChecked()).commit();
				checkCount();
				return true;
		}
		return false;
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
		loadCache();
	}

	@Override
	public void onChanged(Booru booru)
	{
		mPostAdapter.setBooru(booru);
		if (call != null)call.cancel();
		setRefreshing(false);
		currentBooru = booru;
		mHandler.sendEmptyMessageDelayed(LOADCACHE, 200);
	}

	private void loadCache()
	{
		canLoadMore = true;
		if (currentBooru == null)return;
		final InputStream is=JsonCache.getInstance(getActivity()).getInputStream(currentBooru, Booru.Item.POSTS);
		if (is != null)
			new com.moe.booru.lang.Thread(){
				public void run()
				{
					page = 0;
					loadData(is, -1, true);
				}
			}.start();
		else
		{
			setRefreshing(true);
			onRefresh();
		}
	}

	@Override
	public void onRefresh()
	{
		canLoadMore = true;
		page = 1;
		onLoadMore();
	}

	@Override
	public void onLoadMore()
	{
		if (currentBooru == null || !canLoadMore)return;
		SiteUser user=(SiteUser) currentBooru.getSite().getUser();
		String url=null;
		if(user!=null)
			url=currentBooru.getPostsUrl(50, page,user.name,user.password_hash);
			else
			url=currentBooru.getPostsUrl(50, page,null,null);
		Request request=new Request.Builder().url(url).build();
		call = mOkHttp.getClient().newCall(request);
		call.enqueue(this);

	}

	@Override
	public void onResponse(final Call p1, final Response p2) throws IOException
	{
		final CompoundButton check=(CompoundButton) p1.request().tag();
		if (check == null)
		{
			new com.moe.booru.lang.Thread(){
				public void run()
				{
					loadData(new NetInputStream(p2), p2.body().contentLength(), false);
				}
			}.start();
		}
		else
		{
			final String msg=JSON.parseObject(OkHttp.readString(p2.body().byteStream(), p2.body().contentLength())).getString("reason");
			p2.close();
			check.setTag(R.id.fav,msg);
			boolean fav=Integer.parseInt(p1.request().url().queryParameter("score"))==3;
			if(p2.code()!=200){
				check.setChecked(!fav);
				mHandler.post(new Runnable(){
					public void run(){
				Toast.makeText(check.getContext(),msg,Toast.LENGTH_SHORT).show();
				}
				});
			}else{
					Post post=(Post) check.getTag(R.id.posts);
					if(fav){
					post.score++;
					if(post.votes!=null)
					post.votes.add(currentBooru.getSite().getUser().name);
					}else{
					post.score--;
					if(post.votes!=null)
					post.votes.remove(currentBooru.getSite().getUser().name);
					}mHandler.post(new Runnable(){
						public void run(){
					mPostAdapter.notifyItemChanged(check.getTag(R.id.id));
					}});
				}
			//String result=OkHttp.readString(p2.body().byteStream(), p2.body().contentLength());
		}
	}

	@Override
	public void onFailure(Call p1, final IOException p2)
	{
		final CompoundButton check=(CompoundButton) p1.request().tag();
		if (check != null)
		{
			check.setTag(null);
			if (p1.isCanceled())
				return;
			check.post(new Runnable(){

					@Override
					public void run()
					{
						check.setChecked(false);
					}
				});
		}
		else
		{
			mHandler.post(new Runnable(){
					public void run()
					{
						getSwipeRefreshLayout().setRefreshing(false);
					}});
			
		}
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
			List<Post> list=currentBooru.list(OkHttp.readString(page == 1 && !isCache ?new CacheInputStream(res, JsonCache.getInstance(getActivity()).getOutputStream(currentBooru, Booru.Item.POSTS)): res, length), Post.class);
			res.close();
			/*Iterator<Post> iterator=list.iterator();
			while(iterator.hasNext()){
				Post post=iterator.next();
				Response resp=mOkHttp.getClient().newCall(new Request.Builder().url(currentBooru.favoutitesUrl(post.id)).build()).execute();
				JSONObject jo=JSON.parseObject(OkHttp.readString(resp.body().byteStream(),resp.body().contentLength()));
				post.votes=Arrays.asList(jo.getString("favorited_users").split(","));
			}*/
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
				if (canLoadMore)
					setRefreshing(false);
				else
					getSwipeRefreshLayout().setRefreshing(false);
				page++;
				break;
			case LOADCACHE:
				mPostAdapter.clear();
				loadCache();
				break;
		}
		return true;
	}




}
