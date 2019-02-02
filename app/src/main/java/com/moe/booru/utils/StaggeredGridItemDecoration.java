package com.moe.booru.utils;

import android.support.v7.widget.RecyclerView;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView.State;
import android.graphics.Rect;
import android.view.View;

public class StaggeredGridItemDecoration extends RecyclerView.ItemDecoration
{
	private int space=10,spanSize=3;
	public StaggeredGridItemDecoration(int space,int spanSize){
		this.space=space/2;
		this.spanSize=spanSize;
	}
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
	{
		/*int position=parent.getChildAdapterPosition(view);
		int count=parent.getAdapter().getItemCount();
		int lines=(int)Math.ceil(count/(double)spanSize);
		int line=(int)Math.ceil((position+1)/(double)spanSize);
		if(line>1)
			position=position-(line-1)*spanSize;
		if(line==1){
			if(position<spanSize-1){
				outRect.right=space;
			}
			if(position>0)
				outRect.left=space;
				outRect.bottom=space;
		}else if(line==lines){
			if(position<spanSize-1){
				outRect.right=space;
				}
				outRect.top=space;
		}else{
			if(position<spanSize-1){
				outRect.right=space;
				outRect.bottom=space;
			}else if(position==0){
				outRect.bottom=space;
			}
		}*/
		outRect.set(space,space,space,space);
		}

	

	
	
}
