package com.moe.booru.json;

public class JsonParseException extends RuntimeException
{
	public JsonParseException(){}
	public JsonParseException(String message){
		super(message);
	}
}
