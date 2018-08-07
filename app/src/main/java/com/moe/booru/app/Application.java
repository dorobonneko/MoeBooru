package com.moe.booru.app;
import android.content.Intent;
import com.moe.booru.services.CacheService;
import com.tencent.bugly.Bugly;

public class Application extends android.app.Application
{

	@Override
	public void onCreate()
	{
		super.onCreate();
		if(getSharedPreferences("moe",0).getBoolean("cache",false))
			startService(new Intent(this,CacheService.class));
		//Bugly.init(this,"996429b34e",false);
		Thread.currentThread().setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){

				@Override
				public void uncaughtException(Thread p1, Throwable p2)
				{
					// TODO: Implement this method
					return;
				}
			});
	}
	
}
