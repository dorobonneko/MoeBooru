package com.moe.booru.utils;
import android.content.Context;
import java.io.InputStream;
import com.moe.booru.core.Booru;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class JsonCache
{
	private static JsonCache mJsonCache;
	private File cache;
	private JsonCache(Context context){
		cache=context.getCacheDir();
	}
	public static JsonCache getInstance(Context context){
		if(mJsonCache==null){
			synchronized(JsonCache.class){
				if(mJsonCache==null)
					mJsonCache=new JsonCache(context);
			}
		}
		return mJsonCache;
	}
	public InputStream getInputStream(Booru booru,Booru.Item item){
		switch(item){
			case POSTS:
				File file=new File(cache,booru.getSite().getName().concat("/posts"));
				try
				{
					if (file.isFile() && file.canRead())
						return new FileInputStream(file);
				}
				catch (FileNotFoundException e)
				{}
				break;
			case POOLS:
				break;
		}
		return null;
	}
	public OutputStream getOutputStream(Booru booru,Booru.Item item){
		File dir=new File(cache,booru.getSite().getName());
		if(dir.isFile())dir.delete();
		if(!dir.exists())dir.mkdirs();
		switch(item){
			case POSTS:
				try
				{
					return new FileOutputStream(new File(dir, "posts"));
				}
				catch (FileNotFoundException e)
				{}
				break;
			case POOLS:
				try
				{
					return new FileOutputStream(new File(dir, "pools"));
				}
				catch (FileNotFoundException e)
				{}
				break;
		}
		return null;
	}
}
