package com.moe.booru.utils;

public class BitmapUtils
{
	public static int calculateInSampleSize(int width, int height,int reqWidth,int reqHeight)
	{
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth)
		{
            final int halfHeight = height;
            final int halfWidth = width;
            while ((halfHeight / inSampleSize) > reqHeight
				   && (halfWidth / inSampleSize) > reqWidth)
			{
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
