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
 * Ӧ�õ���ڽ���
 */
public class LoveReaderActivity extends Activity {
	private Button local_button;
	/**�����ж��Ƿ��Ƴ�*/
	private static Boolean isExit = false;
	private Context myContext;
	private ShelfAdapter mAdapter;
	private ListView shelf_list;
	int[] size = null;// ��������
	DbHelper db; // ���ݿ��������
	List<BookInfo> books;
	int realTotalRow;
	int bookNumber; // ͼ�������
	final String[] font = new String[] { "6", "8", "10", "20", "24", "26",
			"30", "32", "36", "40", "46", "50", "56", "60", "66", "70" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shelf);
		db = new DbHelper(this);
		myContext = this;
		init();
		/************** ��ʼ�����ͼ�� *********************/
		books = db.getAllBookInfo();// ȡ�����е�ͼ��
		// System.out.println(books.size());
		db.close();
		bookNumber = books.size();// ��ı���
		int count = books.size();
		int totalRow = count / 3;// ͨ����ı���������Ҫ���ٲ���ܿ��Է�
		if (count % 3 > 0) {
			totalRow = count / 3 + 1;
		}
		realTotalRow = totalRow;// �õ�ʵ����Ҫ�������
		if (totalRow < 4) {
			totalRow = 4;// Ĭ�����Ĳ����
		}
		size = new int[totalRow];
		/***********************************/
		mAdapter = new ShelfAdapter();// new adapter���������
		shelf_list.setAdapter(mAdapter);
		// ע��ContextView��view��
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
				return 3;// �������ٵõ�����ListView����
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
						//�����ļ��ĺ�׺��ʾ��ͬ��ͼƬ��Ϣ
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
						button.setOnCreateContextMenuListener(listener);//ע�������Ĳ˵��ļ�����
					}
				}
				bookNumber -= 3;
			} else {
				bookNumber += 3;//������߼�������������
			}

			/* ������߼���������!!!!! */
			return layout;
		}
	};

	// ��ӳ������
	OnCreateContextMenuListener listener = new OnCreateContextMenuListener() {
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			// menu.setHeaderTitle(String.valueOf(v.getId()));
			menu.add(0, 0, v.getId(), "��ϸ��Ϣ");
			menu.add(0, 1, v.getId(), "ɾ������");
		}
	};

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case 0:

			break;
		case 1:
			Dialog dialog = new AlertDialog.Builder(LoveReaderActivity.this)
					.setTitle("��ʾ")
					.setMessage("ȷ��Ҫɾ����")
					.setPositiveButton("ȷ��",
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
										Toast.makeText(myContext, "ɾ���ɹ�",
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(myContext, "�����ļ�ɾ��ʧ��",
												Toast.LENGTH_SHORT).show();
									}
									refreshShelf();
								}
							})
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).create();// ������ť
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

	// ���¼������
	public void refreshShelf() {
		/************** ��ʼ�����ͼ�� *********************/
		books = db.getAllBookInfo();// ȡ�����е�ͼ��
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
		mAdapter = new ShelfAdapter();// new adapter���������
		shelf_list.setAdapter(mAdapter);
	}
     //����鱾�Ϳ�ʼ�ۿ�!
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
			// Toast.makeText(myContext, "�ٰ�һ�κ��˼��˳�Ӧ�ó���",
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
				Toast.makeText(this, "�ٰ�һ�κ��˼��˳�Ӧ�ó���", Toast.LENGTH_SHORT)
						.show();
			} else {
				finish();
				System.exit(0);
			}
		}
		return false;
	}

	public boolean onCreateOptionsMenu(Menu menu) {// �����˵�
		super.onCreateOptionsMenu(menu);
		// ͨ��MenuInflater��XML ʵ����Ϊ Menu Object
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public boolean onOptionsItemSelected(MenuItem item) {// �����˵�
		int ID = item.getItemId();
		switch (ID) {
		case R.id.mainexit:
			creatIsExit();
			break;
		case R.id.addbook:
			Intent i = new Intent();
			i.setClass(LoveReaderActivity.this, Main.class);
			startActivityForResult(i, 222);//���ؽ��
			break;
		default:
			break;

		}
		return true;
	}

	private void creatIsExit() {
		Dialog dialog = new AlertDialog.Builder(LoveReaderActivity.this)
				.setTitle("��ʾ")
				.setMessage("�Ƿ�Ҫȷ��LoverReader��")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// dialog.cancel();
						// finish();
						LoveReaderActivity.this.finish();
						android.os.Process.killProcess(android.os.Process
								.myPid());
						System.exit(0);
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).create();// ������ť
		dialog.show();
	}
}