package com.moe.booru.internal;
import android.app.Activity;
import android.view.MenuItem;
import com.moe.booru.R;
import com.moe.booru.widget.MenuView;
import com.moe.booru.widget.MenuItemView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.content.SharedPreferences;

public class MainImpl implements MenuView.OnMenuItemClickListener,SharedPreferences.OnSharedPreferenceChangeListener
{
	private final static int EXIT=0;
	private static MainImpl impl;
	private Activity activity;
	private MenuView menu;
	private Toast toast;
	private SharedPreferences moe;
	private MainImpl(Activity activity){
		this.activity=activity;
		moe=activity.getSharedPreferences("moe",0);
		menu=activity.findViewById(R.id.menuview);
		menu.setOnMenuItemClickListener(this);
		moe.registerOnSharedPreferenceChangeListener(this);
	}

	public void onDestroy()
	{
		activity=null;
		moe.unregisterOnSharedPreferenceChangeListener(this);
		}

	public boolean finish()
	{
		if(toast==null)
			toast=Toast.makeText(activity,R.string.exit_msg,Toast.LENGTH_SHORT);
		if(handler.hasMessages(EXIT))
			return false;
		else{
			toast.show();
			handler.sendEmptyMessageDelayed(EXIT,2500);
		return true;
		}
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO: Implement this method
		return false;
	}
	public static MainImpl getInstance(Activity activity){
		if(impl==null)impl=new MainImpl(activity);
		return impl;
	}

	@Override
	public void onMenuItemClick(MenuItemView item)
	{
		switch(item.getId()){
			case R.id.account:
				break;
			case R.id.posts:
				break;
			case R.id.pools:
				break;
			case R.id.tags:
				break;
			case R.id.artists:
				break;
		}
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case EXIT:
					break;
			}
		}
	
	};

	@Override
	public void onSharedPreferenceChanged(SharedPreferences p1, String p2)
	{
		if(p2.equals("siteName")){
			
		}
	}


	
}
