package com.moe.booru;

import android.app.*;
import android.os.*;
import android.widget.Toolbar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.content.res.Configuration;
import android.view.MenuItem;
import android.support.v4.widget.SwipeRefreshLayout;
import android.content.res.TypedArray;
import com.moe.booru.internal.MainImpl;

public class MainActivity extends BaseActivity 
{
	private DrawerLayout drawer;
	private ActionBarDrawerToggle actionBarToggle;
    private SwipeRefreshLayout refresh;
	private MainImpl main;
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		setActionBar((Toolbar)findViewById(R.id.toolbar));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		//refresh=(SwipeRefreshLayout) findViewById(R.id.refresh);
		drawer=(DrawerLayout) findViewById(R.id.drawer);
		TypedArray ta=obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
		//refresh.setProgressViewEndTarget(false,getResources().getDimensionPixelOffset(getResources().getIdentifier("status_bar_height","dimen","android"))+ta.getDimensionPixelOffset(0,200)+refresh.getProgressCircleDiameter());
		ta.recycle();
		actionBarToggle=new ActionBarDrawerToggle(this,drawer,true,R.drawable.menu,R.string.drawer_open,R.string.drawer_close);
		drawer.addDrawerListener(actionBarToggle);
		main=MainImpl.getInstance(this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onPostCreate(savedInstanceState);
		actionBarToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		// TODO: Implement this method
		super.onConfigurationChanged(newConfig);
		actionBarToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(actionBarToggle.onOptionsItemSelected(item))
			return true;
		if(main.onOptionsItemSelected(item))
			return true;
			return super.onOptionsItemSelected(item);
	}

	@Override
	public void finish()
	{
		if(!main.finish())
			super.finish();
	}

	@Override
	protected void onDestroy()
	{
		if(main!=null)
			main.onDestroy();
		super.onDestroy();
		
	}
	
}
