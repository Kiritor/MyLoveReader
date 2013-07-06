package com.android.FileBrowser;

import com.andorid.shu.love.R;

/** 表示一个文件实体 **/
public class FileInfo {
	public String Name;
	public String Path;
	public long Size;
	public boolean IsDirectory = false;
	public int FileCount = 0;
	public int FolderCount = 0;

	public int getIconResourceId() {
		if (IsDirectory) {
			return R.drawable.folder;//如果是目录显示的图片是目录的形式
		}
		return R.drawable.doc;//如果是文件,显示的形式是文件的形式
	}
}