package com.android.FileBrowser;

import java.util.Comparator;

/**@author Kiritor
 * 重新定义文件的排序问题*/
public class FileComparator implements Comparator<FileInfo> {

	public int compare(FileInfo file1, FileInfo file2) {
		// 文件夹排在前面
		if (file1.IsDirectory && !file2.IsDirectory) {
			return -1000;
		} else if (!file1.IsDirectory && file2.IsDirectory) {
			return 1000;
		}
		// 相同类型按名称排序
		return file1.Name.compareTo(file2.Name);
	}
}