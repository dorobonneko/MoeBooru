package com.moe.booru.utils;

public class FlowUtil
{
	public static String getSize(long size){
		if (size < 1024)
			return concat(size,"Bytes");
		float newSize=size / 1024f;
		if (newSize < 1024)
			return concat(format(newSize) , "KB");
		newSize /= 1024f;
		if (newSize < 1024)
			return concat(format(newSize) , "MB");
		newSize /= 1024f;
		return concat(format(newSize) , "GB");
	}
	public static String format(float value)
	{
		String number= String.valueOf(value+0.005);
		int index=number.indexOf(".");
		if (index == -1)return number;
		if (index + 3 >= number.length())
			return number;
		return number.substring(0, index + 3);
	}
	private static String concat(Object... values){
		StringBuilder sb=new StringBuilder();
		for(Object o:values)
		sb.append(o);
		return sb.toString();
	}
}
