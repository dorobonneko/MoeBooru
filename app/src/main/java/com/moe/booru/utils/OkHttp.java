package com.moe.booru.utils;
import okhttp3.OkHttpClient;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.Interceptor.Chain;
import java.io.IOException;
import okhttp3.Request;
import java.io.InputStream;
import com.moe.booru.io.MoeInputStream;
import okhttp3.Protocol;
import okhttp3.ResponseBody;
import okhttp3.MediaType;
import okio.Okio;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.io.Reader;
import okhttp3.Call;
import com.moe.booru.io.NetInputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.Iterator;
import java.net.SocketTimeoutException;
import java.io.InputStreamReader;
import okhttp3.Callback;

public class OkHttp
{
	private static OkHttpClient mOkHttpClient;
	private static OkHttp mOkHttp;
	private CacheManager mCacheManager;
	private OkHttp(CacheManager cm)
	{
		this.mCacheManager = cm;
		mOkHttpClient = new OkHttpClient.Builder().sslSocketFactory(getSslSocketFactory()).build();
	}
	public static OkHttp getInstance(CacheManager cm)
	{
		if (mOkHttp == null)
			synchronized (OkHttp.class)
			{
				if (mOkHttp == null)
					mOkHttp = new OkHttp(cm);
			}
		return mOkHttp;
	}
	public OkHttpClient getClient()
	{
		return mOkHttpClient;
	}
	public Call load(String url, final okhttp3.Callback callback)
	{
		Request.Builder rb=new Request.Builder();
		rb.url(url);
		Call call=mOkHttpClient.newCall(rb.build());
		call.enqueue(callback);
		return call;
	}

	
	public static SSLSocketFactory getSslSocketFactory()
	{
		try
		{
			/*TrustManagerFactory factory = TrustManagerFactory
			 .getInstance("SunX509",); 
			 factory.init(KeyStore.getInstance(KeyStore.getDefaultType()));*/
			SSLContext context=SSLContext.getInstance("TLS");
			context.init(null, null, new SecureRandom());
			return context.getSocketFactory();
		}
		catch (Exception e)
		{}
		return null;
	}

	public static String readString(InputStream input,long length) throws IOException
	{
		StringBuilder sb=new StringBuilder();
		int len=-1;
		long total=0;
		char[] buff=new char[4096];
		Reader is=new InputStreamReader(input);
		while ((len = is.read(buff)) != -1)
		{
			sb.append(buff, 0, len);
			total += len;
			if (length != -1 && total >= length)
				break;
		}
		return sb.toString();
	}
	
}
