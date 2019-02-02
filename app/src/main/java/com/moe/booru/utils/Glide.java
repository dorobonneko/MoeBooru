package com.moe.booru.utils;
import android.content.Context;
import android.app.Activity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.Priority;

public class Glide
{
	private static RequestManager mRequest;
	public static void init(Activity context){
		RequestOptions rb=new RequestOptions();
		rb.priorityOf(Priority.LOW);
		rb.timeoutOf(3000);
		mRequest=Glide.with(context).setDefaultRequestOptions(rb);
	}
	public static RequestManager getGlide(){
		return mRequest;
	}
}
