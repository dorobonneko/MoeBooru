package com.moe.booru.database;
import com.moe.booru.empty.User;

public class DataStore
{
	static abstract class Col{
		public static final String _ID="_id";
		private int _id;
		public void set_Id(int id)
		{
			this._id = id;
		}

		public int get_Id()
		{
			return _id;
		}
	}
	public static final class Search extends Col{
		public static final String TAG="tag";
		public static final String CHECK="selected";
		public static final String TABLE="search";
		private String tag;
		private boolean check;


		public void setTag(String tag)
		{
			this.tag = tag;
		}

		public String getTag()
		{
			return tag;
		}

		public void setCheck(boolean check)
		{
			this.check = check;
		}

		public boolean isCheck()
		{
			return check;
		}
	}
	public static final class Site extends Col{
		public static final String NAME="name";
		public static final String URL="url";
		public static final String TYPE="type";
		public static final String HASH="hash";
		public static final String LOGINID="loginid";
		public static final String TABLE="sites";
		public static final int TYPE_MOEBOORU=0;
		public static final int TYPE_GELBOORU=1;
		public static final int TYPE_DANBOORU1=2;
		public static final int TYPE_DANBOORU2=3;
		private String name,url,hash;
		private int type,loginId;
		private User user;

		public void setUser(User user)
		{
			this.user = user;
		}

		public User getUser()
		{
			return user;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		public void setUrl(String url)
		{
			this.url = url;
		}

		public String getUrl()
		{
			return url;
		}

		public void setHash(String hash)
		{
			this.hash = hash;
		}

		public String getHash()
		{
			return hash;
		}

		public void setType(int type)
		{
			this.type = type;
		}

		public int getType()
		{
			return type;
		}

		public void setLoginId(int loginId)
		{
			this.loginId = loginId;
		}

		public int getLoginId()
		{
			return loginId;
		}}
}
