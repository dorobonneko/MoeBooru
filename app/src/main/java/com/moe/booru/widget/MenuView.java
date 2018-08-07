package com.moe.booru.widget;
import android.widget.FrameLayout;
import android.content.Context;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.Menu;
import android.content.res.XmlResourceParser;
import android.content.res.TypedArray;
import android.widget.LinearLayout;
import android.util.Xml;
import android.widget.ScrollView;
import android.view.View;
import android.view.ContextThemeWrapper;

public class MenuView extends ScrollView
{
	private LinearLayout group;
	private OnMenuItemClickListener omicl;
	private OnClickListener ocl=new OnClickListener(){

		@Override
		public void onClick(View p1)
		{
			for(int i=0;i<group.getChildCount();i++)
			((MenuItemView)group.getChildAt(i)).setChecked(false);
			((MenuItemView)p1).setChecked(true);
			if(omicl!=null)
				omicl.onMenuItemClick((MenuItemView)p1);
		}
	};
	public MenuView(Context context){
		this(context,null);
	}
	public MenuView(Context context,AttributeSet attrs){
		super(context,attrs);
		setFillViewport(true);
		group=new LinearLayout(context);
		group.setOrientation(LinearLayout.VERTICAL);
		addView(group,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		TypedArray ta=context.obtainStyledAttributes(attrs,new int[]{com.moe.booru.R.attr.menu});
		int id=ta.getResourceId(0,0);
		if(id!=0)
			inflaterMenu(id);
		ta.recycle();
	}
	public void inflaterMenu(int res){
		XmlResourceParser xml=getResources().getLayout(res);
		try
		{
			final AttributeSet attrs=Xml.asAttributeSet(xml);
			int tag;
			ContextThemeWrapper theme = null;
			while ((tag=xml.next())!= xml.END_DOCUMENT)
			{
				switch(tag){
				case XmlPullParser.START_TAG:
					if(xml.getName().equals("menu")){
						TypedArray ta=getContext().obtainStyledAttributes(attrs,new int[]{android.R.attr.theme});
						theme=new ContextThemeWrapper(getContext(),ta.getResourceId(0,0));
						ta.recycle();
					}else
					if(xml.getName().equals("item")){
					MenuItemView item=new MenuItemView(theme,attrs);
					item.setOnClickListener(ocl);
					group.addView(item,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
					}
					break;
					}
			}
		}
		catch (XmlPullParserException e)
		{}
		catch (IOException e)
		{}
	}
	public void setOnMenuItemClickListener(OnMenuItemClickListener o){
		this.omicl=o;
	}
	public abstract interface OnMenuItemClickListener{
		public void onMenuItemClick(MenuItemView item);
	}
}
