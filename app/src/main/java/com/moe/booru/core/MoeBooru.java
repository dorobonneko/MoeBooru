package com.moe.booru.core;
import com.moe.booru.database.DataStore;
import java.util.List;
import com.moe.booru.core.Booru.Item;
import org.xmlpull.v1.XmlPullParser;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import java.util.ArrayList;
import com.moe.booru.empty.Post;
import java.lang.reflect.Field;
import com.moe.booru.utils.Filter;
import com.alibaba.fastjson.JSONArray;
import java.util.Iterator;
import com.alibaba.fastjson.JSONObject;
import java.util.Map;
import com.moe.booru.empty.Pool;
import com.alibaba.fastjson.JSON;
public class MoeBooru extends Booru
{

	@Override
	public String getUsersUrl(String name)
	{
		StringBuilder sb=new StringBuilder(getSite().getUrl());
		sb.append("/user.json?name=");
		if(name!=null)
		sb.append(name);
		return sb.toString();
	}


	@Override
	public <T extends Object> List<T> list(String text, Class<T> class_) throws IOException
	{
		return JSON.parseArray(text,class_);
	}
	

	@Override
	public String getPostUrl(int id)
	{
		StringBuilder sb=new StringBuilder(getSite().getUrl());
		sb.append("/post/show/");
		sb.append(id);
		return sb.toString();
	}


//	@Override
//	public <T extends Object> List<T> list(Booru.Item item, JSONArray js, Filter<Integer> filter) throws IOException
//	{
//		switch (item)
//		{
//			case POSTS:
//				{
//				List<T> list=new ArrayList<T>();
//				Iterator iterator=js.iterator();
//				while (iterator.hasNext())
//				{
//					JSONObject jo=(JSONObject) iterator.next();
//					if(filter!=null&&!filter.accept(jo.getIntValue("id")))continue;
//					Post post=new Post();
//					Field[] fields=post.getClass().getFields();
//					for (Field field:fields)
//					{
//						field.setAccessible(true);
//						try
//						{
//							switch (field.getType().getName())
//							{
//								case "java.lang.String":
//									field.set(post, jo.getString(field.getName()));
//									break;
//								case "int":
//									field.setInt(post, jo.getIntValue(field.getName()));
//									break;
//								case "long":
//									field.setLong(post, jo.getLongValue(field.getName()));
//
//									break;
//								case "boolean":
//									field.setBoolean(post, jo.getBooleanValue(field.getName()));
//
//									break;
//								case "char":
//									field.setChar(post,  jo.getString(field.getName()).charAt(0));
//
//									break;
//
//							}
//						}
//						catch (Exception e)
//						{}
//						list.add((T)post);
//
//					}
//				}
//				return list;}
//				case POOLS:{
//				List<T> list=new ArrayList<T>();
//				Iterator iterator=js.iterator();
//				while (iterator.hasNext())
//				{
//					JSONObject jo=(JSONObject) iterator.next();
//					if(filter!=null&&!filter.accept(jo.getIntValue("id")))continue;
//					Pool post=new Pool();
//					Field[] fields=post.getClass().getFields();
//					for (Field field:fields)
//					{
//						field.setAccessible(true);
//						try
//						{
//							switch (field.getType().getName())
//							{
//								case "java.lang.String":
//									field.set(post, jo.getString(field.getName()));
//									break;
//								case "int":
//									field.setInt(post, jo.getIntValue(field.getName()));
//									break;
//								case "long":
//									field.setLong(post, jo.getLongValue(field.getName()));
//
//									break;
//								case "boolean":
//									field.setBoolean(post, jo.getBooleanValue(field.getName()));
//
//									break;
//								case "char":
//									field.setChar(post,  jo.getString(field.getName()).charAt(0));
//
//									break;
//
//							}
//						}
//						catch (Exception e)
//						{}
//						list.add((T)post);
//
//					}
//				}
//				return list;}
//		}
//		return null;
//	}

	@Override
	public String favoutitesUrl(int id)
	{
		StringBuilder sb=new StringBuilder(getSite().getUrl());
		sb.append("/favorite/list_users.json?");
		sb.append("id=");
		sb.append(id);
		return sb.toString();
	}


	@Override
	public String getPostsUrl(int limit, int page,String login,String password_hash, String[] tags)
	{
		StringBuilder sb=new StringBuilder(getSite().getUrl());
		sb.append("/post.json?");
		sb.append("limit=");
		sb.append(limit);
		sb.append("&page=");
		sb.append(page);
		sb.append("&include_votes=3");
		sb.append("&include_tags=3");
		if(login!=null&&password_hash!=null){
			sb.append("&login=").append(login);
			sb.append("&password_hash=").append(password_hash);
		}
		sb.append("&tags=");
		for (String tag:tags)
		{
			sb.append(tag).append("+");
		}
		return sb.toString();
	}
	//score 1,-1
	@Override
	public String vote(int id, int score,String name,String password_hash)
	{

		StringBuilder sb=new StringBuilder(getSite().getUrl());
		sb.append("/post/vote.json?");
		sb.append("id=");
		sb.append(id);
		sb.append("&score=");
		sb.append(score);
		sb.append("&login=");
		sb.append(name);
		sb.append("&password_hash=");
		sb.append(password_hash);
		return sb.toString();
	}
	//id 指定id
	//order data,count,name
	//after_id 大于该id
	//name 指定name
	//name_pattern 搜索name
	@Override
	public String getTagsUrl(int limit, int page, String... params)
	{
		StringBuilder sb=new StringBuilder(getSite().getUrl());
		sb.append("/tag.json?");
		sb.append("limit=");
		sb.append(limit);
		sb.append("&page=");
		sb.append(page);
		for (int i=0;i < params.length;i ++)
		{
			sb.append("&").append(params[i]);
		}
		return sb.toString();
	}
	//order data,name
	@Override
	public String getArtistsUrl(String name, int page, String order)
	{
		StringBuilder sb=new StringBuilder(getSite().getUrl());
		sb.append("/artist.json?");
		sb.append("name=");
		if(name!=null)
		sb.append(name);
		sb.append("&page=");
		sb.append(page);
		sb.append("&order=");
		sb.append(order);
		return sb.toString();
	}

	@Override
	public String getPoolsUrl(int page, String query)
	{
		StringBuilder sb=new StringBuilder(getSite().getUrl());
		sb.append("/pool.json?");
		sb.append("query=");
		sb.append(query==null?"":query);
		sb.append("&page=");
		sb.append(page);
		return sb.toString();
	}

	@Override
	public String getPoolListUrl(int poolId, int page)
	{
		StringBuilder sb=new StringBuilder(getSite().getUrl());
		sb.append("/pool/show.json?");
		sb.append("id=");
		sb.append(poolId);
		sb.append("&page=");
		sb.append(page);
		return sb.toString();
	}


	@Override
	public <T extends Object> List<T> list(Booru.Item item, XmlPullParser ja, Filter<Integer> filter) throws IOException, XmlPullParserException 
	{
		switch (item)
		{
			case POSTS:
				List<T> list=new ArrayList<T>();
				int type=0;
				while ((type = ja.nextToken()) != ja.END_DOCUMENT)
				{
					switch (type)
					{
						case ja.START_TAG:
							if (ja.getName().intern().equals("post".intern()))
							{
								if (filter != null && !filter.accept(Integer.valueOf(ja.getAttributeValue(null, "id"))))break;
								Post post=new Post();
								Field[] fields=post.getClass().getFields();
								for (Field field:fields)
								{
									field.setAccessible(true);
									try
									{
										switch (field.getType().getName())
										{
											case "java.lang.String":
												field.set(post, ja.getAttributeValue(null, field.getName()));
												break;
											case "int":
												field.setInt(post, Integer.parseInt(ja.getAttributeValue(null, field.getName())));
												break;
											case "long":
												field.setLong(post, Long.parseLong(ja.getAttributeValue(null, field.getName())));

												break;
											case "boolean":
												field.setBoolean(post, Boolean.parseBoolean(ja.getAttributeValue(null, field.getName())));

												break;
											case "char":
												field.setChar(post,  ja.getAttributeValue(null, field.getName()).charAt(0));

												break;

										}
									}
									catch (Exception e)
									{}
								}
								list.add((T)post);
							}
							break;
					}
				}
				return list;
				//break;
		}
		return null;
	}

	public MoeBooru(DataStore.Site site)
	{
		super(site);
	}
}
