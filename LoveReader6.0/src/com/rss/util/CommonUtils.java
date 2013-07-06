package com.rss.util;

import java.io.File;
import java.util.HashMap;

import android.os.Environment;
import android.os.StatFs;
/**
 * @author Kiritor
 * 通用工具类
 * 得到sdcard的相关信息*/
public class CommonUtils {

	private void sd() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) { // 判断是否插入SD卡
			File filePath = Environment.getExternalStorageDirectory(); // 获得sd
																		// card的路径
			StatFs stat = new StatFs(filePath.getPath()); // 创建StatFs对象，这个对象很重要SD卡的信息就靠它获取了
			long blockSize = stat.getBlockSize(); // 获得block的大小
			float totalBlocks = stat.getBlockCount(); // 获得总容量
			int sizeInMb = (int) (blockSize * totalBlocks) / 1024 / 1024; // 转换成单位是兆的
			long availableBlocks = stat.getAvailableBlocks(); // 获得可用容量
			float percent = availableBlocks / totalBlocks; // 获得可用比例
			percent = (int) (percent * 1000); // 舍去多余小数位数
			String a = "SD Card使用情况：\n总容量：" + sizeInMb + "M。\n已用"
					+ (1000 - percent) / 10.0f + "% 可用" + percent / 10.f + "%。";
		} else {
		}
	}
}
