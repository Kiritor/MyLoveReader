package com.android.FileBrowser;
/**
 * 用于文件的过滤操作的
 * 继承于ListActivity*/
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.andorid.shu.love.R;
import com.sqlite.DbHelper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;

public class Main extends ListActivity {
	private TextView _filePath;
	private List<FileInfo> _files = new ArrayList<FileInfo>();
	private String _rootPath = FileUtil.getSDPath();//根目录
	private String _currentPath = _rootPath;
	private final String TAG = "Main";
	private final int MENU_RENAME = Menu.FIRST;
	//Menu用于管理菜单项的接口
	private final int MENU_COPY = Menu.FIRST + 3;
	private final int MENU_MOVE = Menu.FIRST + 4;
	private final int MENU_DELETE = Menu.FIRST + 5;
	private final int MENU_INFO = Menu.FIRST + 6;
	private final int MENU_IMPORT = Menu.FIRST + 7;
	private BaseAdapter adapter = null;
	private String isImport = "0";
	private String targetPath = "/sdcard/lovereader/";//程序的默认的目录

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.file_main);

		_filePath = (TextView) findViewById(R.id.file_path);

		// 绑定长按事件
		// getListView().setOnItemClickListener(_onItemLongClickListener);

		// 注册上下文菜单,对于目录、文件进行一些相关的操作
		/**getListView得到listview*/
		registerForContextMenu(getListView());

		// 绑定数据,FileAdapter是自己继承与BaseAdapter实现的
		adapter = new FileAdapter(this, _files);
		setListAdapter(adapter);

		// 获取当前目录的文件列表
		viewFiles(_currentPath);
	}

	/** 上下文菜单 **/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = null;

		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return;
		}

		FileInfo f = _files.get(info.position);
		File file = new File(f.Path);
		menu.setHeaderTitle(f.Name);
		menu.add(0, MENU_RENAME, 1, getString(R.string.file_rename));
		menu.add(0, MENU_COPY, 2, getString(R.string.file_copy));
		menu.add(0, MENU_MOVE, 3, getString(R.string.file_move));
		menu.add(0, MENU_DELETE, 4, getString(R.string.file_delete));
		menu.add(0, MENU_INFO, 5, getString(R.string.file_info));
		if(!file.isDirectory()){
			//如果不是文件夹则添加一个导入的选项
			menu.add(0, MENU_IMPORT, 6, getString(R.string.importbook));
		}
	}
	 

	/** 上下文菜单事件处理 **/
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		FileInfo fileInfo = _files.get(info.position);
		File f = new File(fileInfo.Path);
		switch (item.getItemId()) {
		case MENU_RENAME:
			FileActivityHelper.renameFile(Main.this, f, renameFileHandler);
			return true;
		case MENU_COPY:
			pasteFile(f.getPath(), "COPY");
			return true;
		case MENU_MOVE:
			pasteFile(f.getPath(), "MOVE");
			return true;
		case MENU_DELETE:
			FileUtil.deleteFile(f);
			viewFiles(_currentPath);
			return true;
		case MENU_INFO:
			FileActivityHelper.viewFileInfo(Main.this, f);
			return true;
		case MENU_IMPORT://导入书架
			String src = fileInfo.Path;
			new File(targetPath).mkdirs();
			String tar = targetPath + f.getName();
			final File copyfile = new File(tar);
			if (copyfile.exists()) {
				Toast.makeText(getApplicationContext(), R.string.file_exists, Toast.LENGTH_SHORT)
						.show();
			}else{
				try {
					System.out.println(src+"   "+tar);
					FileUtil.copyFile(new File(src), new File(tar));
					DbHelper db = new DbHelper(this);
					db.insert(f.getName(), "0");
					db.close();
					isImport = "1";
					Toast.makeText ( Main.this , "导入成功" , Toast.LENGTH_SHORT ).show ();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	/** 行被单击事件处理 **/
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		FileInfo f = _files.get(position);

		if (f.IsDirectory) {
			viewFiles(f.Path);//是目录的话继续进入
		} else {
			//openFile(f.Path);
			super.openContextMenu(v);//直接就打开上下文菜单
		}
	}

	/** 重定义返回键事件 **/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 拦截back按键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			File f = new File(_currentPath);
			String parentPath = f.getParent();
			if (parentPath != null) {
				viewFiles(parentPath);
			} else {
				exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 获取从PasteFile传递过来的路径 **/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Activity.RESULT_OK == resultCode) {
			Bundle bundle = data.getExtras();
			if (bundle != null && bundle.containsKey("CURRENTPATH")) {
				viewFiles(bundle.getString("CURRENTPATH"));
			}
		}
	}

	/** 创建菜单 **/
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.file_menu, menu);
		return true;
	}

	/** 菜单事件 **/
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mainmenu_home:
			viewFiles(_rootPath);
			break;
		case R.id.mainmenu_refresh:
			viewFiles(_currentPath);
			break;
		case R.id.mainmenu_createdir:
			FileActivityHelper.createDir(Main.this, _currentPath, createDirHandler);
			break;
		case R.id.mainmenu_exit:
			exit();
			break;
		default:
			break;
		}
		return true;
	}

	/** 获取该目录下所有文件 **/
	private void viewFiles(String filePath) {
		ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(Main.this, filePath);
		if (tmp != null) {
			// 清空数据
			_files.clear();
			_files.addAll(tmp);
			tmp.clear();

			// 设置当前目录
			_currentPath = filePath;
			_filePath.setText(filePath);

			// this.onContentChanged();
			adapter.notifyDataSetChanged();
		}
	}

	/** 长按事件处理 **/
	/**
	 * private OnItemLongClickListener _onItemLongClickListener = new
	 * OnItemLongClickListener() {
	 * 
	 * @Override public boolean onItemLongClick(AdapterView<?> parent, View
	 *           view, int position, long id) { Log.e(TAG, "position:" +
	 *           position); return true; } };
	 **/

	/** 打开文件 **/
	private void openFile(String path) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		File f = new File(path);
		String type = FileUtil.getMIMEType(f.getName());
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}

	/** 重命名回调委托 **/
	private final Handler renameFileHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0)
				viewFiles(_currentPath);
		}
	};

	/** 创建文件夹回调委托 **/
	private final Handler createDirHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0)
				viewFiles(_currentPath);
		}
	};
	/** 粘贴文件 **/
	private void pasteFile(String path, String action) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("CURRENTPASTEFILEPATH", path);
		bundle.putString("ACTION", action);
		intent.putExtras(bundle);
		intent.setClass(Main.this, PasteFile.class);
		// 打开一个Activity并等待结果
		startActivityForResult(intent, 0);
	}
	/** 退出程序 **/
	private void exit() {

		Intent resultIntent = new Intent();
		resultIntent.putExtra("isImport", isImport); 
		setResult(222, resultIntent);

		Main.this.finish();

	}
}
