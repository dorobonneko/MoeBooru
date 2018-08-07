package com.moe.booru.database;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

public class SiteDatabase extends SQLiteOpenHelper
{
	private static SiteDatabase site;
	private SQLiteDatabase sql;
	private SiteDatabase(Context context){
		super(context,"site",null,3);
		sql=getReadableDatabase();
	}
	public static SiteDatabase getInstance(Context context){
		if(site==null){
			synchronized(SiteDatabase.class){
				if(site==null)site=new SiteDatabase(context.getApplicationContext());
			}
		}
		return site;
		}
	@Override
	public void onCreate(SQLiteDatabase p1)
	{
		sql.execSQL("create table sites(name TEXT,)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase p1, int p2, int p3)
	{
		// TODO: Implement this method
	}
	
}
