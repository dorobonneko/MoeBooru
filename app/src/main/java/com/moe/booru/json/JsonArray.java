package com.moe.booru.json;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;

public class JsonArray
{
	private StringBuilder text;
	private List<Object> list=new ArrayList<>();
	private Vector<Character> vector=new Vector<>();
	public JsonArray(String text){
		StringBuilder sb=new StringBuilder(text.trim());
		if(sb.charAt(0)=='['&&sb.charAt(sb.length()-1)==']'){
			parser(sb);
		}else
		throw new JsonParseException("it's not a jsonarray!");
	}
	public int size(){
		return list.size();
	}
	public JsonArray getJsonArray(int index){
		return (JsonArray)list.get(index);
	}
	public JsonObject getJsonObject(int index){
		return (JsonObject)list.get(index);
	}
	void parser(StringBuilder sb){
		text=sb;
		char c=0;
		int start=1;
		int end=-1;
		for(int i=1;i<sb.length()-1;i++){
			c=sb.charAt(i);
			if(c=='{')
				vector.add('{');
			else if(c=='[')
				vector.add('[');
			else if(c=='}'){
				if(vector.lastElement()=='{'){
					vector.removeElementAt(vector.size()-1);
					if(vector.isEmpty())
					{
						end=i+1;
						list.add(new JsonObject(sb.substring(start,end)));
						start=end+1;
					}
				}else
				throw new JsonParseException("{} not start");
			}else if(c==']'){
				if(vector.lastElement()=='['){
					vector.removeElementAt(vector.size()-1);
					if(vector.isEmpty())
					{
						end=i+1;
						list.add(new JsonArray(sb.substring(start,end)));
						start=end+1;
					}
				}else
					throw new JsonParseException("[] not start");
				
			}
		}
	}
}
