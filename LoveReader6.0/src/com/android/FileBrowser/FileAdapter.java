/**
 * 
 */
package com.android.FileBrowser;

import java.util.List;

import com.andorid.shu.love.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Kiritor
 * 文件列表ListView适配器,继承至BaseAdapter
 * 
 */
public class FileAdapter extends BaseAdapter {

	private LayoutInflater _inflater;
	private List<FileInfo> _files;

	public FileAdapter(Context context, List<FileInfo> files) {
		_files = files;
		_inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return _files.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return _files.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;

		if (convertView == null) { // convertView 可利用，如果传入为null，执行初始化操作
			// 载入xml文件为View
			convertView = _inflater.inflate(R.layout.file_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.file_name);
			holder.icon = (ImageView) convertView.findViewById(R.id.file_icon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 更新View信息
		FileInfo f = _files.get(position);
		holder.name.setText(f.Name);
		holder.icon.setImageResource(f.getIconResourceId());

		return convertView;
	}

	/* class ViewHolder */
	private class ViewHolder {
		TextView name;
		ImageView icon;
	}
}
