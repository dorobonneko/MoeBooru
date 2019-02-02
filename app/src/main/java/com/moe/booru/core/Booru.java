package com.moe.booru.core;
import com.moe.booru.database.DataStore;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import com.moe.booru.utils.Filter;
import com.alibaba.fastjson.JSONArray;

public abstract class Booru
{
	private DataStore.Site site;
	public Booru(DataStore.Site site){
		this.site=site;
	}
	public DataStore.Site getSite(){
		return site;
	}
	public abstract <T extends Object> List<T> list(String text,Class<T> class_) throws IOException;
	public abstract <T extends Object> List<T> list(Item item,XmlPullParser ja,Filter<Integer> filter) throws IOException, XmlPullParserException;
	public abstract String getPostsUrl(int limit,int page,String login,String password_hash,String... tags);
	public abstract String vote(int id,int score,String name,String password_hash);
	public abstract String getTagsUrl(int limit,int page,String... params);
	public String getArtistsUrl(String name,int page,String order){return null;};
	public abstract String getPoolsUrl(int page,String query);
	public String getPoolListUrl(int poolId,int page){return null;}
	public abstract String favoutitesUrl(int id);
	public abstract String getPostUrl(int id);
	public abstract String getUsersUrl(String name);
	public enum Item{
		POSTS,POOLS,TAGS,ARTISTD;
	}
}
