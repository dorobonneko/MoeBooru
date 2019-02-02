package com.moe.booru.app;
import android.content.Context;
import com.moe.booru.database.DataStore;
import android.app.Dialog;
import com.moe.booru.R;
import android.widget.TextView;
import android.view.View;
import com.moe.booru.widget.TextInput;
import android.widget.Spinner;
import android.widget.Toast;
import com.moe.booru.database.SiteDatabase;
import android.net.Uri;

public class BooruAddDialog implements View.OnClickListener
{
	private DataStore.Site site;
	private Dialog dialog;
	private TextView addon;
	private OnChangedListener l;
	private TextInput name,host,hash;
	private Spinner scheme,booru;
	public BooruAddDialog(Context context,OnChangedListener l){
		this.l=l;
		dialog=new Dialog(context,R.style.Theme_Dialog);
		dialog.setContentView(R.layout.site_addon_view);
		dialog.findViewById(R.id.close).setOnClickListener(this);
		addon=dialog.findViewById(R.id.addon);
		addon.setOnClickListener(this);
		name=dialog.findViewById(R.id.name);
		host=dialog.findViewById(R.id.host);
		hash=dialog.findViewById(R.id.passwd_hash);
		scheme=dialog.findViewById(R.id.scheme);
		booru=dialog.findViewById(R.id.booru);
	}
	public void show(DataStore.Site site){
		this.site=site;
		dialog.show();
		addon.setText(site==null?"Add":"Save");
		if(site==null){
			name.setText(null);
			host.setText(null);
			hash.setText(null);
			scheme.setSelection(0);
			booru.setSelection(0);
		}else{
			name.setText(site.getName());
			Uri uri=Uri.parse(site.getUrl());
			host.setText(uri.getHost());
			hash.setText(site.getHash());
			scheme.setSelection(uri.getScheme().equalsIgnoreCase("https")?0:2);
			booru.setSelection(site.getType());
		}
	}

	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.close:
				dialog.dismiss();
				break;
			case R.id.addon:
				String name=this.name.getText().toString().trim();
				if(name.length()==0){
					Toast.makeText(dialog.getContext(),"Name is Empty",Toast.LENGTH_SHORT).show();
					break;
				}
				String host=this.host.getText().toString();
				if(host.length()==0){
					Toast.makeText(dialog.getContext(),"Host is Empty",Toast.LENGTH_SHORT).show();
					break;
				}
				String scheme=this.scheme.getSelectedItem().toString();
				int type=this.booru.getSelectedItemPosition();
				String hash=this.hash.getText().toString();
				boolean success=false;
				if(site==null){
					DataStore.Site site=new DataStore.Site();
					site.setName(name);
					site.setUrl(scheme.concat(host));
					site.setType(type);
					site.setHash(hash);
					success=SiteDatabase.getInstance(dialog.getContext()).insert(site);
					if(success&&l!=null)l.onInserted(site);
				}else{
					site.setName(name);
					site.setUrl(scheme.concat(host));
					site.setType(type);
					site.setHash(hash);
					success=SiteDatabase.getInstance(dialog.getContext()).update(site);
					if(success&&l!=null)l.onUpdated(site);
					}
				if(success)
				dialog.dismiss();
				break;
		}
		}
	public interface OnChangedListener{
		void onInserted(DataStore.Site site);
		void onUpdated(DataStore.Site site);
	}
	
}
