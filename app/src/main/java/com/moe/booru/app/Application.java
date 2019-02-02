package com.moe.booru.app;
import android.content.Intent;
import com.moe.booru.services.CacheService;

public class Application extends android.app.Application
{

	@Override
	public void onCreate()
	{
		super.onCreate();
		Thread.currentThread().setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){

				@Override
				public void uncaughtException(Thread p1, Throwable p2)
				{
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			});
	}
	
}
