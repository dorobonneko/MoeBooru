package com.moe.booru.io;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class CacheInputStream extends InputStream
{
	private InputStream is;
	private OutputStream os;
	public CacheInputStream(InputStream res,OutputStream os){
		is=res;
		this.os=os;
	}
	@Override
	public int read() throws IOException
	{
		byte[] b=new byte[1];
		is.read(b);
		os.write(b,0,1);
		return b[0];
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		// TODO: Implement this method
		int l= is.read(b, off, len);
		if(l!=-1){
		os.write(b,off,l);
		os.flush();
		}
		return l;
	}

	@Override
	public int read(byte[] b) throws IOException
	{
		return read(b,0,b.length);
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
		os.flush();
		os.close();
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
