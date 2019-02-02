package com.moe.booru.internal;
import android.os.*;
import android.view.*;
import com.moe.booru.widget.*;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.moe.booru.R;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.util.TypedValue;
import android.view.animation.OvershootInterpolator;
import com.moe.booru.fragments.PostsFragment;
import android.app.FragmentTransaction;
import com.moe.booru.fragments.Fragment;
import com.moe.booru.fragments.AccountFragment;
import com.moe.booru.fragments.PoolsFragment;
import com.moe.booru.fragments.TagsFragment;
import com.moe.booru.fragments.ArtistsFragment;
import com.moe.booru.app.BooruAddDialog;
import android.support.v4.widget.DrawerLayout;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.moe.booru.utils.BitmapUtils;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.booru.adapter.SiteAdapter;
import java.util.List;
import com.moe.booru.database.DataStore;
import java.util.ArrayList;
import com.moe.booru.database.SiteDatabase;
import com.moe.booru.adapter.ClickAdapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import com.moe.booru.core.BooruManager;
import com.moe.booru.core.Booru;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.moe.booru.database.DataStore.Site;
import com.moe.booru.fragments.SearchFragment;
import android.widget.Toolbar;
import android.widget.ViewFlipper;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.graphics.drawable.Drawable;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;

public class MainImpl implements MenuView.OnMenuItemClickListener,SharedPreferences.OnSharedPreferenceChangeListener,View.OnClickListener,DrawerLayout.DrawerListener,View.OnApplyWindowInsetsListener,
SiteAdapter.OnItemClickListener,
BooruManager.OnBooruChangedListener,
BooruAddDialog.OnChangedListener,
TextWatcher,
View.OnFocusChangeListener
{
	private final static int EXIT=0;
	private Activity activity;
	//private MenuView menu;
	private Toast toast;
	private SharedPreferences moe;
	private CircleImageView current;
	private View float_group;
	private ImageView clear;
	private Fragment currentFragment;
	private BooruAddDialog mBooruAddDialog;
	private ImageView head;
	private RecyclerView mRecyclerView;
	private SiteAdapter mSiteAdapter;
	private List<DataStore.Site> list;
	private DrawerLayout drawer;
	private ViewFlipper mViewFlipper;
	private InputMethodManager imm;
	public MainImpl(Activity activity)
	{
		this.activity = activity;
		moe = activity.getSharedPreferences("moe", 0);
		//menu = activity.findViewById(R.id.menuview);
		//menu.setOnMenuItemClickListener(this);
		moe.registerOnSharedPreferenceChangeListener(this);
		current = activity.findViewById(R.id.current);
		current.setOnClickListener(this);
		activity.findViewById(R.id.account).setOnClickListener(this);
		activity.findViewById(R.id.posts).setOnClickListener(this);
		activity.findViewById(R.id.pools).setOnClickListener(this);
		activity.findViewById(R.id.tags).setOnClickListener(this);
		activity.findViewById(R.id.artists).setOnClickListener(this);
		float_group=((View)current.getParent());
		float_group.setOnClickListener(this);
		float_group.setClickable(false);
		activity.findViewById(R.id.add).setOnClickListener(this);
		activity.findViewById(R.id.settings).setOnClickListener(this);
		drawer=((DrawerLayout)activity.findViewById(R.id.drawer));
		drawer.setOnApplyWindowInsetsListener(this);
		drawer.addDrawerListener(this);
		toggle(R.id.posts);
		mRecyclerView=drawer.findViewById(R.id.recyclerview);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext(),LinearLayoutManager.VERTICAL,false));
		mRecyclerView.setAdapter(mSiteAdapter=new SiteAdapter(list=new ArrayList<>()));
		mRecyclerView.setClipToPadding(false);
		mSiteAdapter.setOnItemClickListener(this);
		mRecyclerView.setItemAnimator(null);
		//mRecyclerView.setOnTouchListener(this);
		BooruManager.getInstance(activity).registerOnBooruChangedListener(this);
		//mItemTouchHelper=new ItemTouchHelper(new SiteItemTouchCallback());
		//mItemTouchHelper.attachToRecyclerView(mRecyclerView);
		int id=moe.getInt("selectId",-1);
		if(id!=-1){
		BooruManager.getInstance(activity).toggle(SiteDatabase.getInstance(activity).getSite(id));
		}
	Toolbar search=activity.findViewById(R.id.search_toolbar);
	search.setNavigationIcon(R.drawable.arrow_left);
	search.setNavigationOnClickListener(this);
	EditText key=search.findViewById(R.id.search_key);
	key.addTextChangedListener(this);
	key.setOnFocusChangeListener(this);
	clear=search.findViewById(R.id.clear);
	clear.setOnClickListener(this);
	Drawable clear_icon=clear.getDrawable().mutate();
	clear_icon.setTint(0xffffffff);
	clear.setImageDrawable(clear_icon);
	mViewFlipper=activity.findViewById(R.id.viewflipper);
	}


	@Override
	public void onInserted(DataStore.Site site)
	{
		/*int size=list.size();
		list.clear();
		mSiteAdapter.notifyItemRangeRemoved(0,size);
		list.addAll(SiteDatabase.getInstance(activity).querySites());
		mSiteAdapter.notifyItemRangeInserted(0,list.size());
		*/
		list.add(site);
		mSiteAdapter.notifyItemInserted(list.size()-1);
		if(mSiteAdapter.getSelect()==-1&&list.size()>0)
			BooruManager.getInstance(activity).toggle(list.get(0));
		
	}

	@Override
	public void onUpdated(DataStore.Site site)
	{
		mSiteAdapter.notifyItemChanged(list.indexOf(site));
	}
	



	@Override
	public void onEdit(SiteAdapter adapter, com.moe.booru.adapter.SiteAdapter.ViewHolder vh)
	{
		DataStore.Site site=list.get(vh.getPosition());
		if(mBooruAddDialog==null)
			mBooruAddDialog=new BooruAddDialog(activity,this);
			mBooruAddDialog.show(site);
	}

	@Override
	public void onDelete(SiteAdapter adapter, final com.moe.booru.adapter.SiteAdapter.ViewHolder vh)
	{
		final DataStore.Site site=list.get(vh.getPosition());
		new AlertDialog.Builder(activity, R.style.Theme_Dialog).setTitle("Delete").setMessage(site.getName()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					SiteDatabase.getInstance(activity).delete(site);
					list.remove(site);
					mSiteAdapter.notifyItemRemoved(vh.getPosition());
					if(list.size()>0)
						BooruManager.getInstance(activity).toggle(list.get(0));
						else
						BooruManager.getInstance(activity).toggle(null);
				}
			}).setNegativeButton(android.R.string.cancel, null).show();
	}



	@Override
	public void onChanged(Booru booru)
	{
		int index=booru==null?-1:booru.getSite().get_Id();
		mSiteAdapter.setSelect(index);
		moe.edit().putInt("selectId",index==-1?-1:booru.getSite().get_Id()).commit();
	}


	@Override
	public void onItemClick(ClickAdapter click, RecyclerView.ViewHolder vh)
	{
		drawer.closeDrawer(Gravity.START,true);
		BooruManager.getInstance(activity).toggle(list.get(vh.getPosition()));                                                      
		}                                                                                                    


	@Override
	public WindowInsets onApplyWindowInsets(View p1, WindowInsets p2)
	{
		p1.findViewById(R.id.navigation_space).setPadding(0,p2.getSystemWindowInsetTop(),0,0);
		((View)(p1.findViewById(R.id.toolbar).getParent().getParent())).setPadding(0,p2.getSystemWindowInsetTop(),0,p2.getSystemWindowInsetBottom());
		//((View)(current.getParent())).setPadding(0,0,0,p2.getSystemWindowInsetBottom());
		mRecyclerView.setPadding(0,0,0,p2.getSystemWindowInsetBottom());
		return p2;
	}


	public boolean onCreateOptionsMenu(Menu menu)
	{
		//activity.getMenuInflater().inflate(R.menu.menu,menu);
		return false;
	}
	public void toggle(int id)
	{
		toggle((CircleImageView)activity.findViewById(id));
	}
	private void toggle(CircleImageView view)
	{
		current.setImageDrawable(view.getDrawable());
		switch (view.getId())
		{
			case R.id.account:
				show(AccountFragment.class);
				break;
			case R.id.posts:
				show(PostsFragment.class);
				break;
			case R.id.pools:
				show(PoolsFragment.class);
				break;
			case R.id.tags:
				show(TagsFragment.class);
				break;
			case R.id.artists:
				show(ArtistsFragment.class);
				break;
				}
	}
	private Fragment show(Class class_){
		Fragment posts=(Fragment) activity.getFragmentManager().findFragmentByTag(class_.getName());
		try{          
		if(posts==null)         
		posts=(Fragment) class_.newInstance();
		}                        
		catch (InstantiationException e) {}
		catch (IllegalAccessException e) {}
		if(currentFragment==posts)return posts;
		FragmentTransaction ft=activity.getFragmentManager().beginTransaction();
		if(currentFragment!=null)
			ft.hide(currentFragment);
		if(posts.isAdded())
			ft.show(posts);
		else
			ft.add(R.id.content,posts,posts.getClass().getName());
		ft.commitAllowingStateLoss();
		currentFragment=posts;
		activity.getActionBar().setTitle(currentFragment.getTitle());
		return posts;
	}
	public void onDestroy()
	{
		activity = null;
		moe.unregisterOnSharedPreferenceChangeListener(this);
	}

	public boolean finish()
	{
		if(mViewFlipper.getDisplayedChild()==1){
			close(mViewFlipper);
			return true;
		}
		if(!(currentFragment instanceof PostsFragment)){
			toggle(R.id.posts);
			return true;
		}
		if (toast == null)
			toast = Toast.makeText(activity, R.string.exit_msg, Toast.LENGTH_SHORT);
		if (handler.hasMessages(EXIT))
			return false;
		else
		{
			toast.show();
			handler.sendEmptyMessageDelayed(EXIT, 2500);
			return true;
		}
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case android.R.id.home:
				activity.onBackPressed();
				return true;
		}
		return false;
	}

	@Override
	public void onClick(View p1)
	{
		switch (p1.getId())
		{
			case R.id.account:
			case R.id.posts:
			case R.id.pools:
			case R.id.tags:
			case R.id.artists:{
				toggle((CircleImageView)p1);
				float_group.setClickable(false);
				current.setTag(false);
				AnimatorSet animeSet=(AnimatorSet) current.getTag(R.id.current);
				animeSet.reverse();
				animeSet.resume();
				}break;
			case R.id.current:{
				Boolean show=(Boolean) p1.getTag();
				if (show == null || !show)
				{
					float_group.setClickable(true);
					p1.setTag(true);
					AnimatorSet animeSet=(AnimatorSet) p1.getTag(R.id.current);
					if (animeSet == null)
					{
						p1.setTag(R.id.current, animeSet = new AnimatorSet());
						float space=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,p1.getResources().getDisplayMetrics());
						ViewGroup group=(ViewGroup) p1.getParent();
						ObjectAnimator[] animes=new ObjectAnimator[group.getChildCount()-1];
						for (int i=0;i < group.getChildCount() - 1;i++)
						{
							ObjectAnimator anime=new ObjectAnimator();
							View v= group.getChildAt(i);
							anime.setTarget(v);
							anime.setPropertyName("TranslationY");
							anime.setFloatValues(0,-( (animes.length - i) * (v.getHeight() +  space)));
							animes[i]=anime;
						}
						animeSet.playTogether(animes);
						animeSet.setDuration(350);
						animeSet.setInterpolator(new OvershootInterpolator(0.9f));
					}
						animeSet.start();
				}else{
					float_group.setClickable(false);
					p1.setTag(false);
					AnimatorSet animeSet=(AnimatorSet) p1.getTag(R.id.current);
					animeSet.reverse();
					animeSet.resume();
				}
				}break;
				case R.id.add:
					if(mBooruAddDialog==null)
						mBooruAddDialog=new BooruAddDialog(activity,this);
						mBooruAddDialog.show(null);
					break;
				case R.id.settings:
					break;
				
				case -1:
					close(p1);
					break;
				case R.id.clear:
					((EditText)((View)clear.getParent()).findViewById(R.id.search_key)).setText(null);
					break;
			case R.id.float_button_view:
				float_group.setClickable(false);
				current.setTag(false);
				AnimatorSet animeSet=(AnimatorSet) current.getTag(R.id.current);
				animeSet.reverse();
				animeSet.resume();

				break;
		}
	}

private void close(View v){
	mViewFlipper.setDisplayedChild(0);
	if(currentFragment instanceof View.OnClickListener)
		((View.OnClickListener)currentFragment).onClick(v);
	
}
	@Override
	public void onMenuItemClick(MenuItemView item)
	{

	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case EXIT:
					break;
			}
		}

	};

	@Override
	public void onSharedPreferenceChanged(SharedPreferences p1, String p2)
	{
		if (p2.equals("siteName"))
		{

		}
	}

	@Override
	public void onDrawerSlide(View p1, float p2)
	{
		// TODO: Implement this method
	}

	@Override
	public void onDrawerOpened(View p1)
	{
		if(head==null){
			head=p1.findViewById(R.id.head_background);
			BitmapFactory.Options bo=new BitmapFactory.Options();
			bo.inJustDecodeBounds=true;
			BitmapFactory.decodeResource(activity.getResources(),R.drawable.bg_summer_rain,bo);
			bo.inSampleSize=BitmapUtils.calculateInSampleSize(bo.outWidth,bo.outHeight,head.getWidth(),head.getHeight());
			bo.inJustDecodeBounds=false;
			head.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(),R.drawable.bg_summer_rain,bo));
			list.addAll(SiteDatabase.getInstance(activity).querySites());
			mSiteAdapter.notifyItemRangeInserted(0,list.size());
			//mSiteAdapter.setSelect(moe.getInt("selectId",-1));
		}
	}

	@Override
	public void onDrawerClosed(View p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onDrawerStateChanged(int p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void afterTextChanged(Editable p1)
	{
		// TODO: Implement this method
		clear.setVisibility(p1.length()==0?View.GONE:View.VISIBLE);
	}

	@Override
	public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void onFocusChange(View p1, boolean p2)
	{
		if(imm==null)
			imm=(InputMethodManager) p1.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			if(p2)
				imm.showSoftInput(p1,imm.SHOW_IMPLICIT);
				else if(p1.getWindowToken()!=null)
				imm.hideSoftInputFromWindow(p1.getWindowToken(),imm.HIDE_NOT_ALWAYS);
	}





}
