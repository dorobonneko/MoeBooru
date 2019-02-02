package com.moe.booru.fragments;
import android.view.*;

import android.os.Bundle;
import com.moe.booru.R;
import android.widget.TextView;
import com.moe.booru.widget.TextInput;
import com.moe.booru.widget.ProgressButton;
import android.widget.Toast;
import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.Response;
import java.io.IOException;
import com.moe.booru.utils.OkHttp;
import com.moe.booru.utils.CacheManager;
import com.moe.booru.core.Booru;
import com.moe.booru.core.BooruManager;
import okhttp3.Request;
import com.alibaba.fastjson.JSON;
import com.moe.booru.empty.User;
import java.util.List;
import com.moe.booru.database.SiteDatabase;
import com.moe.booru.database.DataStore;
import com.moe.booru.utils.SHA1;
import com.moe.booru.empty.SiteUser;
import android.widget.ViewFlipper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;

public class AccountFragment extends Fragment implements View.OnClickListener,Callback,BooruManager.OnBooruChangedListener,
View.OnApplyWindowInsetsListener
{
	private ProgressButton login;
	private TextInput name,password;
	private OkHttp okhttp;
	private ViewFlipper mViewFlipper;
	private User user;
	private Call call;
	private Booru booru;
	private TextView userName,userId;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		okhttp = OkHttp.getInstance(CacheManager.getInstance(getActivity()));
	}

	@Override
	public void onChanged(Booru booru)
	{
		if (call != null)
			call.cancel();
		this.booru=booru;
		onHiddenChanged(isHidden());
	}

	@Override
	public void onDestroyView()
	{
		BooruManager.getInstance(getActivity()).unregisterOnBooruChangedListener(this);
		super.onDestroyView();
	}


	@Override
	public String getTitle()
	{
		// TODO: Implement this method
		return "Account";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		return inflater.inflate(R.layout.login_view, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onViewCreated(view, savedInstanceState);
		login = view.findViewById(R.id.login);
		name = view.findViewById(R.id.name);
		password = view.findViewById(R.id.password);
		login.setOnClickListener(this);
		mViewFlipper = view.findViewById(R.id.viewflipper);
		userName=mViewFlipper.getChildAt(1).findViewById(R.id.name);
		userId=mViewFlipper.getChildAt(1).findViewById(R.id.id);
		mViewFlipper.setFitsSystemWindows(true);
		mViewFlipper.setOnApplyWindowInsetsListener(this);
		mViewFlipper.requestApplyInsets();
		mViewFlipper.findViewById(R.id.fav).setOnClickListener(this);
		mViewFlipper.findViewById(R.id.logout).setOnClickListener(this);
	}

	@Override
	public WindowInsets onApplyWindowInsets(View p1, WindowInsets p2)
	{
		p1.setPadding(p2.getSystemWindowInsetLeft(),p2.getSystemWindowInsetTop()+getActivity().findViewById(R.id.toolbar).getMeasuredHeight(),p2.getSystemWindowInsetRight(),p2.getSystemWindowInsetBottom());
		return p2;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		BooruManager bm=BooruManager.getInstance(getActivity());
		booru = bm.getBooru();
		bm.registerOnBooruChangedListener(this);
		onHiddenChanged(isHidden());
		
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		// TODO: Implement this method
		super.onHiddenChanged(hidden);
		if (!hidden)
		{
			if (booru == null)
			{
				mViewFlipper.setDisplayedChild(0);
			}
			else
			{
				user = booru.getSite().getUser();
				if (user == null)
					mViewFlipper.setDisplayedChild(0);
				else
				{
					mViewFlipper.setDisplayedChild(1);
					//设置信息
					userName.setText(user.name);
					userId.setText(String.valueOf(user.id));
				}
			}
		}
	}

	@Override
	public void onClick(final View view)
	{
		switch (view.getId())
		{
			case R.id.login:
				if (booru == null)break;
				String name=this.name.getText().toString().trim();
				String password=this.password.getText().toString().trim();
				if (name.length() == 0)
				{
					this.name.requestTextFocus();
					break;
				}
				if (password.length() == 0)
				{
					this.password.requestTextFocus();
					break;
				}
				this.name.clearFocus();
				this.password.clearFocus();
				login.setProgressing(true);
				login.setEnabled(!login.isProgressing());
				call = okhttp.load(booru.getUsersUrl(name), AccountFragment.this);
				break;
			case R.id.fav:
				PostsListFragment plf=new PostsListFragment();
				plf.setTags("vote:3:".concat(user.name));
				getFragmentManager().beginTransaction().add(android.R.id.content,plf).addToBackStack(null).commitAllowingStateLoss();
				break;
			case R.id.logout:
				new AlertDialog.Builder(getActivity(), R.style.Theme_Dialog).setTitle(R.string.logout).setMessage(user.name).setPositiveButton(R.string.logout, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p0, int p2)
						{
							SiteDatabase.getInstance(view.getContext()).deleteUser(booru.getSite().get_Id());
							booru.getSite().setUser(null);
							user=null;
							onHiddenChanged(isHidden());
						}
					}).setNegativeButton(android.R.string.cancel, null).show();
				break;
		}
	}

	@Override
	public void onResponse(Call p1, Response p2) throws IOException
	{
		final List<User> users=JSON.parseArray(OkHttp.readString(p2.body().byteStream(), p2.body().contentLength()), User.class);
		p2.close();
		login.post(new Runnable(){
			public void run(){
		if (users.size() == 0)
		{
			name.requestTextFocus();
			Toast.makeText(getActivity(), R.string.account_not_found, Toast.LENGTH_SHORT).show();
			login.setProgressing(false);
			login.setEnabled(true);
		}
		else
		{
			Booru booru=BooruManager.getInstance(getActivity()).getBooru();
			User user=users.get(0);
			SiteUser siteUser=new SiteUser();
			siteUser.id = user.id;
			siteUser.blacklisted_tags = user.blacklisted_tags;
			siteUser.name = user.name;
			String pass=AccountFragment.this.password.getText().toString().trim();
			if (!TextUtils.isEmpty(booru.getSite().getHash()))
				pass = booru.getSite().getHash().replace("your-password", pass);
			String passwd_hash=SHA1.sha1(pass);
			siteUser.password_hash = (passwd_hash);
			SiteDatabase.getInstance(getActivity()).update(siteUser, booru.getSite().get_Id());
			booru.getSite().setUser(siteUser);
			login.setProgressing(false);
			login.setEnabled(true);
			onHiddenChanged(isHidden());
		}
		}
		});
	}

	@Override
	public void onFailure(Call p1, final IOException p2)
	{
		login.post(new Runnable(){
				public void run()
				{
					login.setProgressing(false);
					login.setEnabled(true);
					Toast.makeText(login.getContext(),p2.getMessage(),Toast.LENGTH_SHORT).show();
				}});
	}




}
