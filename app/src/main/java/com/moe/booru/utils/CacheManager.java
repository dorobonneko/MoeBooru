package com.moe.booru.utils;
import android.content.Context;
import java.io.InputStream;
import com.moe.booru.io.MoeInputStream;

public class CacheManager
{
	private static CacheManager mCacheManager;
	private CacheManager(Context context){
		
	}
	public static CacheManager getInstance(Context context){
		if(mCacheManager==null)
			synchronized(CacheManager.class){
				if(mCacheManager==null)
					mCacheManager=new CacheManager(context);
			}
		return mCacheManager;
	}
	public MoeInputStream get(String url){
		return null;
	}
}
