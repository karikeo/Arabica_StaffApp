package com.shevchenko.staffapp.Model;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.shevchenko.staffapp.R;

public class MenuListAdapter extends BaseAdapter {

	Activity 							act;
	ArrayList<MenuItemButton> dataList;
    LayoutInflater						inflater;
	public MenuListAdapter() {
		
	}
	
	public MenuListAdapter(Activity act, ArrayList<MenuItemButton> chatList) {
		this.act = act;
		this.dataList = chatList;
		inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return dataList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		View vi = convertView;
        vi = inflater.inflate(R.layout.drawer_listview_item, null);
        TextView txtTitle = (TextView) vi.findViewById(R.id.txtTitle);
        MenuItemButton item = dataList.get(position);
        txtTitle.setText(item.txtMenu);
        txtTitle.setTextColor(Color.parseColor("#FFFFFF"));
		ImageView img = (ImageView) vi.findViewById(R.id.imgMenu);
		if(item.imgDrawable != 0)
			img.setImageResource(item.imgDrawable);
		else
			img.setVisibility(View.GONE);
        return vi;
	}	
}
