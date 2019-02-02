package com.moe.booru.empty;
import com.bumptech.glide.load.model.GlideUrl;
import java.util.Map;
import java.util.HashMap;
import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.Response;
import java.io.IOException;
import com.alibaba.fastjson.JSON;
import com.moe.booru.utils.OkHttp;
import com.alibaba.fastjson.JSONObject;
import java.util.List;

public class Post
{
	public int id;
	public String tags;
	public long created_at;
	public long updated_at;
	public int creator_id;
	public int approver_id;
	public String author;
	public int change;
	public String source;
	public int score;
	public String md5;
	public long file_size;
	public String file_ext;
	public String file_url;
	public boolean is_show_in_index;
	public String preview_url;
	public int preview_width,preview_height;
	public int actual_preview_width,actual_preview_height;
	public String sample_url;
	public int sample_width,sample_height;
	public long sample_file_size;
	public String jpeg_url;
	public int jpeg_width,jpeg_height;
	public long jpeg_file_size;
	public char rating;
	public boolean is_rating_locked;
	public boolean has_children;
	public int parent_id;
	public String status;
	public boolean is_pending;
	public int width,height;
	public boolean is_held;
	public String frames_pending_string;
	public String frames_string;
	public boolean is_note_locked;
	private GlideUrl previewUrl,sampleurl,jpegurl,fileurl;
	public List<String> votes;
	public long last_noted_at,last_commented_at;

	public Call voteCall;

	@Override
	public boolean equals(Object obj)
	{
		if(this==obj)return true;
		if(obj instanceof Post)
			return id==((Post)obj).id;
		return false;
	}
	public GlideUrl getPreviewUrl(){
		if(preview_url==null)return null;
		if(previewUrl==null)
			previewUrl=new GlideUrl(preview_url){
				@Override
				public Map<String,String> getHeaders(){
					Map<String,String> map=new HashMap<>();
					map.put("User-Agent","Glide:Post-".concat(String.valueOf(id)));
					return map;
				}
			};
		return previewUrl;
	}
	public GlideUrl getSampleUrl(){
		if(sample_url==null)return null;
		if(sampleurl==null)
			sampleurl=new GlideUrl(sample_url){
				@Override
				public Map<String,String> getHeaders(){
					Map<String,String> map=new HashMap<>();
					map.put("User-Agent","Glide:Post-".concat(String.valueOf(id+sample_url.hashCode())));
					return map;
				}
			};
		return sampleurl;
	}
	public GlideUrl getJpegUrl(){
		if(jpeg_url==null)return null;
		if(jpegurl==null)
			jpegurl=new GlideUrl(jpeg_url){
				@Override
				public Map<String,String> getHeaders(){
					Map<String,String> map=new HashMap<>();
					map.put("User-Agent","Glide:Post-".concat(String.valueOf(id+jpeg_url.hashCode())));
					return map;
				}
			};
		return jpegurl;
	}
	public GlideUrl getFileUrl(){
		if(file_url==null)return null;
		if(fileurl==null)
			fileurl=new GlideUrl(file_url){
				@Override
				public Map<String,String> getHeaders(){
					Map<String,String> map=new HashMap<>();
					map.put("User-Agent","Glide:Post-".concat(String.valueOf(id+file_url.hashCode())));
					return map;
				}
			};
		return fileurl;
	}

	
}
