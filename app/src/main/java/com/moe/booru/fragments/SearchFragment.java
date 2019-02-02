package com.moe.booru.fragments;
import android.text.*;
import android.view.*;

import android.os.Bundle;
import android.widget.EditText;
import com.moe.booru.R;
import com.moe.booru.widget.SwipeLayout;
import com.moe.booru.database.SiteDatabase;
import android.widget.Spinner;
import com.moe.booru.empty.Search;
import java.util.List;
import android.widget.ListView;
import com.moe.booru.adapter.SearchAdapter;
import java.util.ArrayList;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Adapter;

public class SearchFragment extends FloatFragment implements View.OnClickListener,TextWatcher,
ListView.OnItemClickListener,
SearchAdapter.OnItemRemoveListener
{
	private EditText search_key;
	private View clear;
	private Spinner order,rating;
	private List<Search> list;
	private SearchAdapter mSearchAdapter;
	@Override
	public View onCreateContentView(LayoutInflater inflater, SwipeLayout container, Bundle saveInstanceState)
	{
		return inflater.inflate(R.layout.search_view, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onViewCreated(view, savedInstanceState);
		view.findViewById(R.id.search).setOnClickListener(this);
		clear = view.findViewById(R.id.clear);
		clear.setOnClickListener(this);
		search_key = view.findViewById(R.id.text);
		search_key.addTextChangedListener(this);
		View child=((ViewGroup)view).getChildAt(0);
		child.setFitsSystemWindows(true);
		child.setOnApplyWindowInsetsListener(this);
		child.requestApplyInsets();
		order = view.findViewById(R.id.order);
		rating = view.findViewById(R.id.rating);
		ListView listview=view.findViewById(R.id.listview);
		listview.setAdapter(mSearchAdapter = new SearchAdapter(list = new ArrayList<>()));
		mSearchAdapter.setOnItemRemoveListener(this);
		listview.setOnItemClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		list.addAll(SiteDatabase.getInstance(getActivity()).querySearch());
		mSearchAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		PostsListFragment plf=new PostsListFragment();
		plf.setTags(list.get(p3).tag.concat(" order:").concat(order.getSelectedItem().toString()).concat(rating.getSelectedItemPosition() == 0 ?"": " rating:" + rating.getSelectedItem().toString()));
		getFragmentManager().beginTransaction().add(android.R.id.content, plf).addToBackStack(null).commitAllowingStateLoss();

	}

	@Override
	public void onItemRemove(int pos)
	{
		SiteDatabase.getInstance(getActivity()).deleteSearch(list.get(pos).id);
		list.remove(pos);
		mSearchAdapter.notifyDataSetChanged();
	}



	@Override
	public void onSwiped(boolean show)
	{
		if (!show)
			getFragmentManager().beginTransaction().hide(this).commitAllowingStateLoss();
	}

	@Override
	public void onClick(View p1)
	{
		switch (p1.getId())
		{
			case R.id.clear:
				search_key.setText(null);
				break;
			case R.id.search:
				if (search_key.getWindowToken() != null)
				{
					InputMethodManager imm=(InputMethodManager) p1.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(search_key.getWindowToken(), imm.HIDE_NOT_ALWAYS);
				}
				final String key=search_key.getText().toString().trim();
				if (key.length() > 0)
				{
					new Thread(){
						public void run()
						{
							SiteDatabase.getInstance(search_key.getContext()).insertSearch(key);
						}
					}.start();
				}
				PostsListFragment plf=new PostsListFragment();
				plf.setTags(key.concat(" order:").concat(order.getSelectedItem().toString()).concat(rating.getSelectedItemPosition() == 0 ?"": " rating:" + rating.getSelectedItem().toString()));
				getFragmentManager().beginTransaction().add(android.R.id.content, plf).addToBackStack(null).commitAllowingStateLoss();
				break;
		}
	}

	@Override
	public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void afterTextChanged(Editable p1)
	{
		if (p1.length() > 0)
			clear.setVisibility(View.VISIBLE);
		else
			clear.setVisibility(View.GONE);
	}

	@Override
	public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}





}
