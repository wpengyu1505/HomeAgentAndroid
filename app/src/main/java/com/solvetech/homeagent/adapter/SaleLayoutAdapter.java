package com.solvetech.homeagent.adapter;

import java.util.ArrayList;

import com.solvetech.homeagent.model.ProjectSummary;
import com.solvetech.homeagent.R;
import com.solvetech.homeagent.model.PropertyInfo;
import com.solvetech.homeagent.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SaleLayoutAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<PropertyInfo> properties;
	private LayoutInflater inflater;

    private TextView text_layout;
    private TextView text_area;
    private TextView text_price;
	
	public SaleLayoutAdapter(Activity activity, ArrayList<PropertyInfo> properties) {
		this.activity = activity;
		this.properties = properties;
		this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return properties.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = inflater.inflate(R.layout.sale_row, null);
		}

		PropertyInfo info = properties.get(position);
		text_layout = (TextView) view.findViewById(R.id.text_layout);
		text_area = (TextView) view.findViewById(R.id.text_area);
		text_price = (TextView) view.findViewById(R.id.text_price);

		text_layout.setText(info.getPropertyLayout() + "(" + info.getPropertyId() + ")");
		text_area.setText(Double.toString(info.getPropertyArea()) + " 平方米");
		text_price.setText(Utils.formatPrice(info.getPropertyPrice()));
		return view;
	}

}
