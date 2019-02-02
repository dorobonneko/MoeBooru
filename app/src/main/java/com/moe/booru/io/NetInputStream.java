package com.moe.booru.io;
import java.io.*;
import okhttp3.Response;

public class NetInputStream extends InputStream
{
	private Response res;
	private InputStream is;
	public NetInputStream(Response res){
		this.res=res;
		is=res.body().byteStream();
	}
	@Override
	public int read() throws IOException
	{
		// TODO: Implement this method
		return is.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		// TODO: Implement this method
		return is.read(b, off, len);
	}

	@Override
	public int read(byte[] b) throws IOException
	{
		// TODO: Implement this method
		return is.read(b);
	}

	@Override
	public void reset() throws IOException
	{
		// TODO: Implement this method
		is.reset();
	}

	@Override
	public void close() throws IOException
	{
		// TODO: Implement this method
		is.close();
		res.close();
	}

	@Override
	public int available() throws IOException
	{
		// TODO: Implement this method
		return is.available();
	}

	@Override
	public void mark(int readlimit)
	{
		// TODO: Implement this method
		is.mark(readlimit);
	}

	@Override
	public boolean markSupported()
	{
		// TODO: Implement this method
		return is.markSupported();
	}
	
}
