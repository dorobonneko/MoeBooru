package com.moe.booru.fragments;
import android.view.View;
import android.os.Bundle;
import android.view.LayoutInflater;
import com.moe.booru.widget.SwipeLayout;
import com.moe.booru.R;
import android.widget.Toolbar;
import android.view.WindowInsets;
import java.util.List;
import com.moe.booru.empty.Post;
import android.support.v4.view.ViewPager;
import com.moe.booru.adapter.PostPagerAdapter;
import java.util.ArrayList;
import com.bumptech.glide.Glide;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.moe.booru.core.BooruManager;
import com.moe.booru.core.Booru;
import android.content.Intent;
import android.net.Uri;

public class PostViewFragment extends FloatFragment implements ViewPager.OnPageChangeListener,View.OnClickListener,Toolbar.OnMenuItemClickListener
{

	
	private Toolbar toolbar;
	private List<Post> list=new ArrayList<>();
	private int selection;
	@Override
	public View onCreateContentView(LayoutInflater inflater, SwipeLayout container, Bundle saveInstanceState)
	{
		return inflater.inflate(R.layout.post_view,container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onViewCreated(view, savedInstanceState);
		toolbar=view.findViewById(R.id.toolbar);
		toolbar.setNavigationIcon(R.drawable.arrow_left);
		toolbar.setNavigationOnClickListener(this);
		toolbar.inflateMenu(R.menu.post_view_menu);
		toolbar.setOnMenuItemClickListener(this);
		View v=(View) toolbar.getParent();
		v.setFitsSystemWindows(true);
		v.setOnApplyWindowInsetsListener(this);
		v.requestApplyInsets();
		ViewPager vp=view.findViewById(R.id.viewpager);
		vp.setOnPageChangeListener(this);
		vp.setAdapter(new PostPagerAdapter(Glide.with(this),list));
		vp.setOffscreenPageLimit(3);
		vp.setCurrentItem(selection);
		vp.getAdapter().notifyDataSetChanged();
		view.setFitsSystemWindows(false);
		if(selection==0)
			onPageSelected(0);
		
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
	public WindowInsets onApplyWindowInsets(View p1, WindowInsets p2)
	{
		p1.setPadding(p2.getSystemWindowInsetLeft(),p2.getSystemWindowInsetTop(),p2.getStableInsetRight(),0);
		return p2;
	}
	public void setList(List<Post> list,int selection){
		this.list.addAll(list);
		this.selection=selection;
	}

	@Override
	public void onDestroyView()
	{
		list.clear();
		super.onDestroyView();
	}

	
	@Override
	public void onPageScrolled(int p1, float p2, int p3)
	{
		// TODO: Implement this method
	}

	@Override
	public void onPageSelected(int p1)
	{
		selection=p1;
		Post post=list.get(p1);
		toolbar.setTitle("Post:".concat(String.valueOf(post.id)));
		toolbar.setSubtitle(String.valueOf(p1+1).concat("/").concat(String.valueOf(list.size())));
		
	}

	@Override
	public void onPageScrollStateChanged(int p1)
	{
		// TODO: Implement this method
	}

	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.info:
				PostDetailFragment pdf=new PostDetailFragment();
				pdf.setPost(list.get(selection));
				getFragmentManager().beginTransaction().add(android.R.id.content,pdf).addToBackStack(null).commitAllowingStateLoss();
				break;
			case R.id.web:
				Booru booru=BooruManager.getInstance(getActivity()).getBooru();
				if(booru==null)break;
				Intent intent=new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(booru.getPostUrl(list.get(selection).id)));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				try{startActivity(intent);}catch(Exception e){}
				
				break;
		}
		return true;
	}
	
}
