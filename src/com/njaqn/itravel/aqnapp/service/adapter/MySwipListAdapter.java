package com.njaqn.itravel.aqnapp.service.adapter;

import java.util.List;

import android.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baoyz.swipemenulistview.BaseSwipListAdapter;
import com.njaqn.itravel.aqnapp.service.bean.JSpotBean;
import com.njaqn.itravel.aqnapp.service.bean.MapResultBean;

public class MySwipListAdapter extends BaseSwipListAdapter {

	private List<MapResultBean> mList;
	private Context context;
	
	
	public MySwipListAdapter(List<MapResultBean> mList, Context context) {
		super();
		this.mList = mList;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.simple_list_item_1, null);
			new ViewHolder(convertView);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		String name = mList.get(position).getName();
		holder.tv_name.setText(name);
		return convertView;
	}
	
    class ViewHolder {
        TextView tv_name;

        public ViewHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.text1);
            view.setTag(this);
        }
    }
    @Override
    public boolean getSwipEnableByPosition(int position) {
    	if (mList.get(position).getType() == 0) {
			return false;
		}
    	return true;
    }

}
