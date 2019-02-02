package com.moe.booru.utils;

public interface Filter<T extends Object> 
{
	boolean accept(T t);
}
