package com.sqlite;
/**
 * @author Kiritor
 * 数据库操作*/
import java.util.ArrayList;
import java.util.List;

import com.andorid.shu.love.BookInfo;
import com.andorid.shu.love.SetupInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "love_db";//数据库名字
	private final static int DATABASE_VERSION = 1;//数据库版本
	private final static String TABLE_NAME = "book_mark";//存储图书信息的表
	private final static String TABLE_SETUP = "book_setup";
	public final static String FIELD_ID = "_id";
	public final static String FIELD_FILENAME = "filename";//图书名称
	public final static String FIELD_BOOKMARK = "bookmark";//书签
	public final static String FONT_SIZE = "fontsize";//字体大小
	public final static String ROW_SPACE = "rowspace";//行间距
	public final static String COLUMN_SPACE = "columnspace";//字间距

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		StringBuffer sqlCreateCountTb = new StringBuffer();
		sqlCreateCountTb.append("create table ").append(TABLE_NAME)
		   .append("(_id integer primary key autoincrement,")		   
		   .append(" filename text,")   
		   .append(" bookmark text);");
		db.execSQL(sqlCreateCountTb.toString());
		String sql = "insert into " + TABLE_NAME + "(filename,bookmark) values('糗事百科.txt','0')";
		//插入一条测试数据
	//	db.execSQL(sql);
		//系统设置表，存储一些系统信息
		StringBuffer setupTb = new StringBuffer();
		setupTb.append("create table ").append(TABLE_SETUP)
		   .append("(_id integer primary key autoincrement,")	
		   .append(" fontsize text,")  
		   .append(" rowspace text,")  
		   .append(" columnspace text);");
		db.execSQL(setupTb.toString());
		//插入一条测试数据
		String setup = "insert into " + TABLE_SETUP + "(fontsize,rowspace,columnspace) values('6','0','0')";
		db.execSQL(setup);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		String sql = " DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}
    //得到一个降序的游标
	public Cursor select() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				" _id desc");
		return cursor;
	}
    //通过id获取图书信息
	public BookInfo getBookInfo(int id){
		BookInfo book = new BookInfo();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		cursor = db.query(TABLE_NAME, null, "_id=" + id, null, null, null, null);
		cursor.moveToPosition(0);
		book.id = id;
		book.bookname = cursor.getString(1);
		book.bookmark = cursor.getInt(2);
		db.close();
		return book;
	}
	//得到系统相关信息
	public SetupInfo getSetupInfo(){
		SetupInfo setup = new SetupInfo();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		cursor = db.query(TABLE_SETUP, null, null, null, null, null, null);
		cursor.moveToPosition(0);
		setup.id = cursor.getInt(0); 
		setup.fontsize = cursor.getInt(1);
		setup.rowspace = cursor.getInt(2);
		setup.columnspace = cursor.getInt(3);
		cursor.close();
		db.close();
		return setup;
	}
	//得到所有的图书，也就是图书列表
	public List<BookInfo> getAllBookInfo(){
		List<BookInfo> books = new ArrayList<BookInfo>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, " _id desc");
		int count = cursor.getCount();
		for (int i = 0; i < count; i++) {
			cursor.moveToPosition(i);
			BookInfo book = new BookInfo();
			book.id = cursor.getInt(0);
			book.bookname = cursor.getString(1);
			book.bookmark = cursor.getInt(2);
			books.add(book);
		}
		cursor.close();
		return books;
	}
	//加入书签的功能
	public long insert(String Title) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		//就是以键值对的形式加入到特定数据库
		cv.put(FIELD_BOOKMARK, Title);
		long row = db.insert(TABLE_NAME, null, cv);
		return row;
	}
	
	public long insert(String filename, String bookmark) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(FIELD_FILENAME, filename);
		cv.put(FIELD_BOOKMARK, bookmark);
		long row = db.insert(TABLE_NAME, null, cv);
		return row;
	}
	//通过id删除一本书
	public void delete(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = FIELD_ID + "=?";
		String[] whereValue = { Integer.toString(id) };
		db.delete(TABLE_NAME, where, whereValue);
	}
    //更新一本书的信息
	public void update(int id, String filename, String bookmark) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = FIELD_ID + "=?";
		String[] whereValue = { Integer.toString(id) };
		ContentValues cv = new ContentValues();
		cv.put(FIELD_FILENAME, filename);
		cv.put(FIELD_BOOKMARK, bookmark);
		db.update(TABLE_NAME, cv, where, whereValue);
	}
	//更新系统信息
	public void updateSetup(int id, String fontsize, String rowspace,String columnspace) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = FIELD_ID + "=?";
		String[] whereValue = { Integer.toString(id) };
		ContentValues cv = new ContentValues();
		cv.put(FONT_SIZE, fontsize);
		cv.put(ROW_SPACE, rowspace);
		cv.put(COLUMN_SPACE, columnspace);
		db.update(TABLE_SETUP, cv, where, whereValue);
	}
}
