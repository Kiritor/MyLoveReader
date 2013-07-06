package com.andorid.shu.love;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.FileBrowser.Main;
import com.artifex.mupdf.MuPDFActivity;
import com.sqlite.DbHelper;
import com.wordandexcel.ExcelRead;
import com.wordandexcel.ViewFile;


/**
 * @author Kiritor 
 * 应用的入口界面
 */
public class LoveReaderActivity extends Activity {
	private Button local_button;
	/**用于判断是否推出*/
	private static Boolean isExit = false;
	private static Boolean hasTask = false;
	private Context myContext;
	private ShelfAdapter mAdapter;
	private Button shelf_image_button;
	private ListView shelf_list;
	private Button buttontt;
	int[] size = null;// 假设数据
	private final int SPLASH_DISPLAY_LENGHT = 5000; // 延迟五秒
	private final int MENU_RENAME = Menu.FIRST;
	DbHelper db; // 数据库操作对象
	List<BookInfo> books;
	int realTotalRow;
	int bookNumber; // 图书的数量
	final String[] font = new String[] { "6", "8", "10", "20", "24", "26",
			"30", "32", "36", "40", "46", "50", "56", "60", "66", "70" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shelf);
		db = new DbHelper(this);
		myContext = this;
		init();
		/************** 初始化书架图书 *********************/
		books = db.getAllBookInfo();// 取得所有的图书
		// System.out.println(books.size());
		db.close();
		bookNumber = books.size();// 书的本数
		int count = books.size();
		int totalRow = count / 3;// 通过书的本数计算需要多少层书架可以放
		if (count % 3 > 0) {
			totalRow = count / 3 + 1;
		}
		realTotalRow = totalRow;// 得到实际需要的书架数
		if (totalRow < 4) {
			totalRow = 4;// 默认有四层书架
		}
		size = new int[totalRow];
		/***********************************/
		mAdapter = new ShelfAdapter();// new adapter对象才能用
		shelf_list.setAdapter(mAdapter);
		// 注册ContextView到view中
		local_button = (Button) findViewById(R.id.local_button);
		local_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(LoveReaderActivity.this, Main.class);
				startActivityForResult(i, 222);
			}
		});
	}

	private void init() {
		shelf_image_button = (Button) findViewById(R.id.shelf_image_button);
		shelf_list = (ListView) findViewById(R.id.shelf_list);
	}

	public class ShelfAdapter extends BaseAdapter {

		public ShelfAdapter() {
		}

		@Override
		public int getCount() {
			if (size.length > 3) {
				return size.length;
			} else {
				return 3;// 这里至少得到三个ListView的项
			}
		}

		@Override
		public Object getItem(int position) {
			return size[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater layout_inflater = (LayoutInflater) LoveReaderActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = layout_inflater.inflate(R.layout.shelf_list_item,
					null);
			if (position < realTotalRow) {
				int buttonNum = (position + 1) * 3;
				if (bookNumber <= 3) {
					buttonNum = bookNumber;
				}
				for (int i = 0; i < buttonNum; i++) {
					if (i == 0) {
						BookInfo book = books.get(position * 3);
						String buttonName = book.bookname;
						buttonName = buttonName.substring(0,
								buttonName.indexOf("."));
						Button button = (Button) layout
								.findViewById(R.id.button_1);
						
						button.setText(buttonName);
						if(book.bookname.endsWith("pdf"))
						{
							button.setBackgroundResource(R.drawable.pdf);
						}
						button.setVisibility(View.VISIBLE);
						button.setId(book.id);
						button.setOnClickListener(new ButtonOnClick(book.bookname));
						button.setOnCreateContextMenuListener(listener);
					} else if (i == 1) {
						BookInfo book = books.get(position * 3 + 1);
						String buttonName = book.bookname;
						buttonName = buttonName.substring(0,
								buttonName.indexOf("."));
						Button button = (Button) layout
								.findViewById(R.id.button_2);
						//根据文件的后缀显示不同的图片信息
						if(book.bookname.endsWith("pdf"))
						{
							button.setBackgroundResource(R.drawable.pdf);
						}
						button.setVisibility(View.VISIBLE);
						button.setText(buttonName);
						button.setId(book.id);
						button.setOnClickListener(new ButtonOnClick(book.bookname));
						button.setOnCreateContextMenuListener(listener);
					} else if (i == 2) {
						BookInfo book = books.get(position * 3 + 2);
						String buttonName = book.bookname;
						buttonName = buttonName.substring(0,
								buttonName.indexOf("."));
						Button button = (Button) layout
								.findViewById(R.id.button_3);
						if(book.bookname.endsWith("pdf"))
						{
							button.setBackgroundResource(R.drawable.pdf);
						}
						button.setVisibility(View.VISIBLE);
						button.setText(buttonName);
						button.setId(book.id);
						button.setOnClickListener(new ButtonOnClick(book.bookname));
						button.setOnCreateContextMenuListener(listener);//注册上下文菜单的监听器
					}
				}
				bookNumber -= 3;
			} else {
				bookNumber += 3;//就这个逻辑搞了我以下午
			}

			/* 上面的逻辑存在问题!!!!! */
			return layout;
		}
	};

	// 添加长按点击
	OnCreateContextMenuListener listener = new OnCreateContextMenuListener() {
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			// menu.setHeaderTitle(String.valueOf(v.getId()));
			menu.add(0, 0, v.getId(), "详细信息");
			menu.add(0, 1, v.getId(), "删除本书");
		}
	};

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case 0:

			break;
		case 1:
			Dialog dialog = new AlertDialog.Builder(LoveReaderActivity.this)
					.setTitle("提示")
					.setMessage("确认要删除吗？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									BookInfo book = db.getBookInfo(item
											.getOrder());
									File dest = new File("/sdcard/lovereader/"
											+ book.bookname);
									db.delete(item.getOrder());
									if (dest.exists()) {
										dest.delete();
										Toast.makeText(myContext, "删除成功",
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(myContext, "磁盘文件删除失败",
												Toast.LENGTH_SHORT).show();
									}
									refreshShelf();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).create();// 创建按钮
			dialog.show();
			break;
		default:
			break;
		}
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 222) {
			String isImport = data.getStringExtra("isImport");
			if ("1".equals(isImport)) {
				refreshShelf();
			}
		}
	}

	// 重新加载书架
	public void refreshShelf() {
		/************** 初始化书架图书 *********************/
		books = db.getAllBookInfo();// 取得所有的图书
		bookNumber = books.size();
		int count = books.size();
		int totalRow = count / 3;
		if (count % 3 > 0) {
			totalRow = count / 3 + 1;
		}
		realTotalRow = totalRow;
		if (totalRow < 4) {
			totalRow = 4;
		}
		size = new int[totalRow];
		/***********************************/
		mAdapter = new ShelfAdapter();// new adapter对象才能用
		shelf_list.setAdapter(mAdapter);
	}
     //点击书本就开始观看!
	public class ButtonOnClick implements OnClickListener {
		private String string ;
		public ButtonOnClick(String name)
		{
			super();
			string = name;
		}
		public ButtonOnClick() {
		}
		@Override
		public void onClick(View v) {
			if(string.endsWith("xls"))
			{
				Intent i= new Intent();
				i.setClass(LoveReaderActivity.this, ExcelRead.class);
				Bundle bundle = new Bundle();				
				bundle.putString("name", "/sdcard/lovereader/"+string);
				i.putExtras(bundle);
				startActivity(i);
				
			}
			else if(string.endsWith("doc"))
			{
				Intent i= new Intent();
				i.setClass(LoveReaderActivity.this, ViewFile.class);
				Bundle bundle = new Bundle();				
				bundle.putString("name", "/sdcard/lovereader/"+string);
				i.putExtras(bundle);
				startActivity(i);
				
			}
			else if(string.endsWith("pdf"))
			{
				Uri uri = Uri.parse("/sdcard/lovereader/"+string);
				Intent intent = new Intent(LoveReaderActivity.this,MuPDFActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(uri);
				startActivity(intent);
			}
			else
			{
				Toast.makeText(LoveReaderActivity.this, "/sdcard/lovereader/"+string, Toast.LENGTH_SHORT)
				.show();
				Intent intent = new Intent();
				intent.setClass(LoveReaderActivity.this, BookActivity.class);
				intent.putExtra("bookid", String.valueOf(v.getId()));
				startActivity(intent);
				/*
				 * 
				 * adb pull /data/data/com.andorid.shu.love/databases/love_db D://*/
			}
			
		}
	}

	
	
	public class ButtonOnLongClick implements OnLongClickListener {
		@Override
		public boolean onLongClick(View v) {
			// Toast.makeText(myContext, "再按一次后退键退出应用程序",
			// Toast.LENGTH_SHORT).show();

			return true;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// pagefactory.createLog();
		// System.out.println("TabHost_Index.java onKeyDown");
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isExit == false) {
				isExit = true;
				Toast.makeText(this, "再按一次后退键退出应用程序", Toast.LENGTH_SHORT)
						.show();
			} else {
				finish();
				System.exit(0);
			}
		}
		return false;
	}

	public boolean onCreateOptionsMenu(Menu menu) {// 创建菜单
		super.onCreateOptionsMenu(menu);
		// 通过MenuInflater将XML 实例化为 Menu Object
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public boolean onOptionsItemSelected(MenuItem item) {// 操作菜单
		int ID = item.getItemId();
		switch (ID) {
		case R.id.mainexit:
			creatIsExit();
			break;
		case R.id.addbook:
			Intent i = new Intent();
			i.setClass(LoveReaderActivity.this, Main.class);
			startActivityForResult(i, 222);//返回结果
			break;
		default:
			break;

		}
		return true;
	}

	private void creatIsExit() {
		Dialog dialog = new AlertDialog.Builder(LoveReaderActivity.this)
				.setTitle("提示")
				.setMessage("是否要确认LoverReader？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// dialog.cancel();
						// finish();
						LoveReaderActivity.this.finish();
						android.os.Process.killProcess(android.os.Process
								.myPid());
						System.exit(0);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).create();// 创建按钮
		dialog.show();
	}
}