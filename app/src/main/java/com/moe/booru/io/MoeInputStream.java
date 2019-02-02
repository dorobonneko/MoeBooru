package com.moe.booru.io;
import java.io.InputStream;
import java.io.IOException;

public class MoeInputStream extends InputStream
{
	public long length(){
		return 0;
	}
	public long getLength(){
		return 0;
	}
	@Override
	public int read() throws IOException
	{
		// TODO: Implement this method
		return 0;
	}
	
}
