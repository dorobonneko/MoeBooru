package com.moe.booru.core;
import android.content.Context;
import com.moe.booru.database.DataStore;
import java.util.ArrayList;

public class BooruManager
{
	private static BooruManager mBooruManager;
	private Booru mBooru;
	private ArrayList<OnBooruChangedListener> mCallback=new ArrayList<>();
	private BooruManager(Context context)
	{

	}
	public static BooruManager getInstance(Context context)
	{
		if (mBooruManager == null)
			synchronized (BooruManager.class)
			{
				if (mBooruManager == null)
					mBooruManager = new BooruManager(context);
			}
		return mBooruManager;
	}
	public Booru getBooru()
	{
		return mBooru;
	}
	//切换站点
	public void toggle(DataStore.Site site)
	{
		if(site==null)
			mBooru=null;
			else
		switch (site.getType())
		{
			case site.TYPE_MOEBOORU:
				mBooru = new MoeBooru(site);
				break;
			case site.TYPE_GELBOORU:
				//mBooru = new GelBooru(site);
				break;
			case site.TYPE_DANBOORU1:
				//mBooru = new DanBooru1(site);
				break;
			case site.TYPE_DANBOORU2:
				//mBooru = new DanBooru2(site);
				break;
		}
		for (OnBooruChangedListener l:mCallback)
			l.onChanged(mBooru);
	}
	public void registerOnBooruChangedListener(OnBooruChangedListener l)
	{
		mCallback.add(l);
	}
	public void unregisterOnBooruChangedListener(OnBooruChangedListener l)
	{
		mCallback.remove(l);
	}
	public interface OnBooruChangedListener
	{
		void onChanged(Booru booru);
	}
}
