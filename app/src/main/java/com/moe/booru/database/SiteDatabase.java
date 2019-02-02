package com.moe.booru.database;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import java.util.List;
import android.database.sqlite.SQLiteStatement;
import android.database.Cursor;
import java.util.Collections;
import java.util.ArrayList;
import com.moe.booru.empty.Search;
import com.moe.booru.empty.User;
import com.moe.booru.empty.SiteUser;

public class SiteDatabase extends SQLiteOpenHelper
{
	private static SiteDatabase site;
	private SQLiteDatabase sql;
	private SiteDatabase(Context context){
		super(context,"site",null,2);
		setWriteAheadLoggingEnabled(false);
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
	public void close(){
		sql.close();
		site=null;
	}
	@Override
	public void onCreate(SQLiteDatabase p1)
	{
		p1.execSQL("create table sites(_id INTEGER primary key,name TEXT,url TEXT UNIUE,type INTEGER,hash TEXT,loginid INTEGER,login_id INTEGER,login_name TEXT,login_hash TEXT,blacklisted_tags TEXT)");
		p1.execSQL("create table search(_id INTEGER PRIMARY KEY,tag TEXT UNIQUE,selected INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase p1, int p2, int p3)
	{
		// TODO: Implement this method
	}
	public void insertSearch(String key){
		sql.beginTransaction();
		SQLiteStatement state=sql.compileStatement("insert into search(tag) values(?)");
		state.acquireReference();
		state.bindString(1,key);
		try{
			state.executeInsert();
			sql.setTransactionSuccessful();
		}catch(Exception e){}
		state.releaseReference();
		state.close();
		sql.endTransaction();
	}
	public List<Search> querySearch(){
		Cursor cursor=sql.query(DataStore.Search.TABLE,null,null,null,null,null,null);

		if(cursor!=null){
			List<Search> list=new ArrayList<>();
			for(int i=cursor.getCount()-1;i>=0;i--){
				cursor.moveToPosition(i);
				Search site=new Search();
				site.id=(cursor.getInt(0));
				site.tag=(cursor.getString(1));
				site.selected=cursor.getInt(2)==1;
				list.add(site);
			}
			cursor.close();
			return list;
		}
		return Collections.EMPTY_LIST;
		
	}
	public void deleteSearch(int id){
		sql.beginTransaction();
		SQLiteStatement state=sql.compileStatement("delete from search where _id=?");
		state.acquireReference();
		state.bindLong(1,id);
		try{
			state.executeUpdateDelete();
			sql.setTransactionSuccessful();
		}catch(Exception e){}
		state.releaseReference();
		state.close();
		sql.endTransaction();
	}
	public boolean insert(DataStore.Site site){
		sql.beginTransaction();
		SQLiteStatement state=sql.compileStatement("insert into sites(name,url,type,hash,loginid) values(?,?,?,?,?)");
		state.acquireReference();
		state.bindString(1,site.getName());
		state.bindString(2,site.getUrl());
		state.bindLong(3,site.getType());
		state.bindString(4,site.getHash());
		state.bindLong(5,site.getLoginId());
		long row=0;
		try{
		row=state.executeInsert();
		sql.setTransactionSuccessful();
		}catch(Exception e){}
		state.releaseReference();
		state.close();
		sql.endTransaction();
		site.set_Id((int)row);
		return row>0;
	}
	public boolean update(DataStore.Site site){
		sql.beginTransaction();
		SQLiteStatement state=sql.compileStatement("update sites set name=?,url=?,type=?,hash=?,loginid=? where _id=?");
		state.acquireReference();
		state.bindString(1,site.getName());
		state.bindString(2,site.getUrl());
		state.bindLong(3,site.getType());
		state.bindString(4,site.getHash());
		state.bindLong(5,site.getLoginId());
		state.bindLong(6,site.get_Id());
		int size=0;
		try{
			size=state.executeUpdateDelete();
			sql.setTransactionSuccessful();
		}catch(Exception e){}
		state.releaseReference();
		state.close();
		sql.endTransaction();
		return size>0;
	}
	public void delete(DataStore.Site site){
		sql.beginTransaction();
		SQLiteStatement state=sql.compileStatement("delete from users where siteid=?");
		state.acquireReference();
		state.bindLong(1,site.get_Id());
		try{
			state.executeUpdateDelete();}catch(Exception e){}
			state.releaseReference();
			state.close();
		state=sql.compileStatement("delete from sites where _id=?");
		state.acquireReference();
		state.bindLong(1,site.get_Id());
		try{
			state.executeUpdateDelete();
			sql.setTransactionSuccessful();
		}catch(Exception e){}
		state.releaseReference();
		state.close();
		sql.endTransaction();
	}
	public DataStore.Site getSite(int id){
		Cursor cursor=sql.query(DataStore.Site.TABLE,null,"_id=?",new String[]{String.valueOf(id)},null,null,null);
		DataStore.Site site=null;
		if(cursor!=null){
			if(cursor.moveToNext()){
				site=new DataStore.Site();
				site.set_Id(cursor.getInt(0));
				site.setName(cursor.getString(1));
				site.setHash(cursor.getString(4));
				site.setLoginId(cursor.getInt(5));
				site.setType(cursor.getInt(3));
				site.setUrl(cursor.getString(2));
				int userid=cursor.getInt(6);
				if(userid!=0){
					SiteUser user=new SiteUser();
					user.id=userid;
					user.name=cursor.getString(7);
					user.password_hash=cursor.getString(8);
					user.blacklisted_tags=cursor.getString(9).split(",");
					site.setUser(user);
				}
			}
			cursor.close();
			return site;
		}
		return null;
	}
	public List<DataStore.Site> querySites(){
		Cursor cursor=sql.query(DataStore.Site.TABLE,null,null,null,null,null,null);
		
		if(cursor!=null){
			List<DataStore.Site> list=new ArrayList<>();
			while(cursor.moveToNext()){
				DataStore.Site site=new DataStore.Site();
				site.set_Id(cursor.getInt(0));
				site.setName(cursor.getString(1));
				site.setHash(cursor.getString(4));
				site.setLoginId(cursor.getInt(5));
				site.setType(cursor.getInt(3));
				site.setUrl(cursor.getString(2));
				int userid=cursor.getInt(6);
				if(userid!=0){
					SiteUser user=new SiteUser();
					user.id=userid;
					user.name=cursor.getString(7);
					user.password_hash=cursor.getString(8);
					user.blacklisted_tags=cursor.getString(9).split(",");
					site.setUser(user);
				}
				list.add(site);
			}
			cursor.close();
			return list;
		}
		return Collections.EMPTY_LIST;
	}
	public boolean update(SiteUser user,int siteid){
		sql.beginTransaction();
		SQLiteStatement state=sql.compileStatement("update sites set login_id=?,login_name=?,login_hash=?,blacklisted_tags=? where _id=?");
		state.acquireReference();
		state.bindLong(1,user.id);
		state.bindString(2,user.name);
		state.bindString(3,user.password_hash);
		StringBuilder sb=new StringBuilder();
		for(String black:user.blacklisted_tags)
		sb.append(black).append(",");
		sb.deleteCharAt(sb.length()-1);
		state.bindString(4,sb.toString());
		state.bindLong(5,siteid);
		long row=0;
		try{
			row=state.executeInsert();
			sql.setTransactionSuccessful();
		}catch(Exception e){}
		state.releaseReference();
		state.close();
		sql.endTransaction();
		return row>0;
	}
	
	public void deleteUser(int siteId){
		sql.beginTransaction();
		SQLiteStatement state=sql.compileStatement("update sites set login_id=0,login_name='',login_hash='',blacklisted_tags='' where _id=?");
		state.acquireReference();
		state.bindLong(1,siteId);
		try{
			state.executeUpdateDelete();
			sql.setTransactionSuccessful();
		}catch(Exception e){}
		state.releaseReference();
		state.close();
		sql.endTransaction();
	}
	
}
