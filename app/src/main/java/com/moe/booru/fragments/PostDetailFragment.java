package com.moe.booru.fragments;
import android.view.View;
import android.os.Bundle;
import android.view.LayoutInflater;
import com.moe.booru.widget.SwipeLayout;
import com.moe.booru.R;
import android.view.ViewGroup;
import android.widget.Toolbar;
import com.moe.booru.empty.Post;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.moe.booru.utils.FlowUtil;
import android.widget.ListView;
import com.moe.booru.adapter.TagsListAdapter;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.util.TypedValue;

public class PostDetailFragment extends FloatFragment implements View.OnClickListener,ListView.OnItemClickListener 
{
	private Post post;
	@Override
	public View onCreateContentView(LayoutInflater inflater, SwipeLayout container, Bundle saveInstanceState)
	{
		// TODO: Implement this method
		return inflater.inflate(R.layout.post_detail_view,container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		view=((ViewGroup)view).getChildAt(0);
		view.setFitsSystemWindows(true);
		view.setOnApplyWindowInsetsListener(this);
		view.requestApplyInsets();
		Toolbar toolbar=view.findViewById(R.id.toolbar);
		toolbar.setTitle(String.valueOf(post.id));
		toolbar.setNavigationIcon(R.drawable.arrow_left);
		toolbar.setNavigationOnClickListener(this);
		((TextView)view.findViewById(R.id.id)).setText(String.valueOf(post.id));
		((TextView)view.findViewById(R.id.author)).setText(String.valueOf(post.author));
		//Calendar mCalendar=Calendar.getInstance();
		//mCalendar.setTimeInMillis(post.created_at);
		SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		((TextView)view.findViewById(R.id.create_at)).setText(dateformat.format(new Date(post.created_at*1000l)));
		((TextView)view.findViewById(R.id.creator)).setText(String.valueOf(post.creator_id));
		((TextView)view.findViewById(R.id.pixel)).setText(String.valueOf(post.width).concat("x").concat(String.valueOf(post.height)));
		TextView src=((TextView)view.findViewById(R.id.source));
		src.setText(String.valueOf(post.source));
		//src.setOnClickListener(this);
		((TextView)view.findViewById(R.id.rating)).setText(String.valueOf(post.rating).toUpperCase());
		View sample=view.findViewById(R.id.sample_down);
		sample.setOnClickListener(this);
		if(post.sample_url==null)
			((View)sample.getParent()).setVisibility(View.GONE);
		else
		((TextView)((ViewGroup)sample.getParent()).getChildAt(1)).setText(FlowUtil.getSize(post.sample_file_size));
		View large=view.findViewById(R.id.large_down);
		large.setOnClickListener(this);
		if(post.jpeg_url==null)
			((View)large.getParent()).setVisibility(View.GONE);
			else
		((TextView)((ViewGroup)large.getParent()).getChildAt(1)).setText(FlowUtil.getSize(post.jpeg_file_size));
		View origin=view.findViewById(R.id.origin_down);
		origin.setOnClickListener(this);
		if(post.file_url==null)
			((View)origin.getParent()).setVisibility(View.GONE);
		else
			((TextView)((ViewGroup)origin.getParent()).getChildAt(1)).setText(FlowUtil.getSize(post.file_size));
		ListView listview=view.findViewById(R.id.listview);
		listview.setAdapter(new TagsListAdapter(post.tags));
		listview.setOnItemClickListener(this);
		listview.measure(0,0);
		ViewGroup.LayoutParams p=listview.getLayoutParams();
		p.height=(int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,52,listview.getResources().getDisplayMetrics())+listview.getDividerHeight())* listview.getAdapter().getCount()-listview.getDividerHeight()+listview.getPaddingTop()*2;
		listview.setLayoutParams(p);
		listview.requestLayout();
	}
	public void setPost(Post post){
		this.post=post;
	}

	
	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.source:
				if(post.source==null)break;
				open(post.source);
				break;
			case R.id.sample_down:
				open(post.sample_url);
				break;
			case R.id.large_down:
				open(post.jpeg_url);
				break;
			case R.id.origin_down:
				open(post.file_url);
				break;
			case -1:
				getFragmentManager().popBackStack();
				break;
		}
	}

private void open(String url){
	Intent intent=new Intent(Intent.ACTION_VIEW);
	intent.setData(Uri.parse(url));
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	try{startActivity(intent);}catch(Exception e){}
	
}

@Override
public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
{
	PostsListFragment plf=new PostsListFragment();
	plf.setTags(p1.getItemAtPosition(p3).toString());
	getFragmentManager().beginTransaction().add(android.R.id.content,plf).addToBackStack(null).commitAllowingStateLoss();
}

	
	
}
