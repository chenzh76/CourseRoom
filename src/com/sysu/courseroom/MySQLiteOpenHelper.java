package com.sysu.courseroom;

import java.util.HashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	private static String TAG = "MySQLiteOpenHelper";	
	private static String CREATE_COURSE_TABLE = "create table if not exists course (" +
			"id TEXT," +
			"name TEXT," +
			"type TEXT," +
			"credit DOUBLE," +
			"college TEXT," +
			"startWeek INT," +
			"endWeek INT," +
			"startTime INT," +
			"endTime INT," +
			"room TEXT," +
			"teacher TEXT," +
			"good INT," +
			"bad INT," +
			"people INT,"+
			"time TEXT," +
			"quality INT DEFAULT 1)";
	
	private static String CREATE_COMMENT_TABLE = "create table if not exists comment (" +
			"id TEXT," +
			"time TEXT," +
			"content TEXT)";
	
	private static String DATABASE_NAME = "course.db";
	private static int VERSION = 1;
	public MySQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "create table");
		db.execSQL(CREATE_COURSE_TABLE);
		db.execSQL(CREATE_COMMENT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
