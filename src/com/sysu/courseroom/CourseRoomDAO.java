package com.sysu.courseroom;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

/**
 * @author chenzhg
 *
 */
public class CourseRoomDAO {
	private static String TAG = "CourseRoomDAO";
	private MySQLiteOpenHelper helper;
	private SQLiteDatabase db;
	private Context context;
	private static int[] startTime = {480, 535, 590, 645, 700, 755, 810, 865, 920, 975, 1030, 1085, 1140, 1195, 1250};
	private static int[] endTime = {525, 580, 635, 690, 745, 800, 855, 910, 965, 1020, 1075, 1130, 1185, 1240, 1295};
	private static int[] days = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	private static String[] ROOMS = {"B101", "B102", "B103", "B104", "B201", "B202", "B203", "B204", "B205", "B301", "B302", "B303", "B304", "B401", "B402", "B403", "B501", "B502", "B503", 
			"C101", "C102", "C103", "C104", "C105", "C201", "C202", "C203", "C204", "C205", "C206", "C301", "C302", "C303", "C304", "C305", "C401", "C402", "C403", "C404", "C501", "C502", "C503", "C504",
			"D101", "D102", "D103", "D104", "D201", "D202", "D203", "D204", "D205", "D301", "D302", "D303", "D304", "D401", "D402", "D403", "D501", "D502", "D503", 
			"E101", "E103", "E104", "E105", "E201", "E202", "E203", "E204", "E205", "E302", "E303", "E304", "E305", "E402", "E403", "E404", "E405", "E502", "E503", "E504", "E505"};

	private static int baseYear = 2015;
	private static int baseMonth = 3;
	private static int baseDay = 1;
	private HashMap<String, Integer> mp = new HashMap<String, Integer>();
	private static CourseRoomDAO instance = null;
	private CourseRoomDAO(Context c) {
		mp.put("一", 2);
		mp.put("二", 3);
		mp.put("三", 4);
		mp.put("四", 5);
		mp.put("五", 6);
		mp.put("六", 7);
		mp.put("日", 1);
		context = c;
		if(!c.getDatabasePath("course.db").exists()) {
			helper = new MySQLiteOpenHelper(c);
			readDataFromFile();
		}
		else {
			helper = new MySQLiteOpenHelper(c);
		}
	}
	public static CourseRoomDAO getInstance(Context c){
		if (instance == null) {
			instance = new CourseRoomDAO(c);
		}
		return instance;
	}
	
	public void addCourse(Course course) {
		db = helper.getWritableDatabase();
		db.execSQL("insert into course (id, name, type, credit, college, startWeek, endWeek," +
				"startTime, endTime, room, teacher, good, bad, people, time, quality)" +
				"values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				new Object[] {course.id, course.name, course.type, course.credit, course.college, course.startWeek, course.endWeek,
				course.startTime, course.endTime, course.room, course.teacher, course.good, course.bad, course.people, course.time, 1}
		);
	}
	
	/** 判断某年是否为闰年
	 * @param year  年份
	 * @return 是闰年返回true，不是闰年返回false
	 */
	private Boolean isLeapYear(int year) {
		if (year % 400 == 0) return true;
		if (year % 4 == 0 && year % 100 != 0) return true;
		return false;
	}
	
	/** 给定一个日期，判断这是一年中的第几天
	 * @param year   年份
	 * @param month  月份
	 * @param day    天
	 * @return  一年中的第几天
	 */
	private int getDaysOfYear(int year, int month, int day) {
		if (isLeapYear(year)) {
			days[2] = 29;
		}
		int sum = 0;
		for (int i = 1; i < month; ++i) {
			sum += days[i];
		}
		return sum + day;
	}
	
	
	/** 给定一个日期，返回这是一个学期的第几天
	 * @param year    年份
	 * @param month   月份
	 * @param day     天
	 * @return  一个学期的第几天
	 */
	private int getDaysOfSemester(int year, int month, int day) {
		if (year == baseYear) {
			return getDaysOfYear(year, month, day) - getDaysOfYear(baseYear, baseMonth, baseDay);
		}
		return getDaysOfYear(year, month, day) + getDaysOfYear(baseYear, 12, 31) - getDaysOfYear(baseYear, baseMonth, baseDay);
	}
	
	/** 从 res/raw/data文本文件中读取，生成数据库文件
	 * 
	 */
	public void readDataFromFile() {
		try {
			InputStream inputStream = context.getResources().openRawResource(R.raw.data);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader reader = new BufferedReader(inputStreamReader);
			String line;
			Course course = new Course();
			while ((line = reader.readLine()) != null) {
				String[] fields = line.split(",");
				course.id = fields[0];
				course.name = fields[1];
				course.college = fields[2];
				course.credit = Double.parseDouble(fields[3]);
				switch (Integer.parseInt(fields[4])) {
				case 21:
					course.type = "专选";
					break;
				case 11:
					course.type = "专必";
					break;
				case 10:
					course.type = "公必";
					break;
				case 30:
					course.type = "公选";
					break;
				}
				course.teacher = fields[5];
				course.people = Integer.parseInt(fields[6]);
				String[] strs = fields[7].split("-");
				course.startTime = (mp.get(strs[0]) - 1) * 24 * 60 + startTime[Integer.parseInt(strs[1]) - 1];
				course.endTime = (mp.get(strs[0]) - 1) * 24 * 60 + endTime[Integer.parseInt(strs[2]) - 1];
				course.room = strs[3];
				course.startWeek = Integer.parseInt(strs[4]);
				course.endWeek = Integer.parseInt(strs[5]);
				course.time = fields[8];
				addCourse(course);
			}
		}catch (IOException e) {
			
		}
	}
	
	/** 根据某个具体时间查询空教室
	 * @param year    年份
	 * @param month   月份
	 * @param day     天
	 * @param hour    小时
	 * @param minute  分钟
	 * @return        含有Room类型的ArrayList
	 */
	ArrayList<Room> queryEmptyRoom(int year, int month, int day, int hour, int minute) {
		int semesterDays = getDaysOfSemester(year, month, day);
		int theWeek = semesterDays / 7 + 1;
		int theDay = semesterDays % 7 + 1;
		int theTime = (theDay - 1) * 1440 + hour * 60 + minute;
		db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select distinct room from course where startWeek <= ? and endWeek >= ? and startTime <= ? and endTime >= ?",
				new String[]{String.valueOf(theWeek), String.valueOf(theWeek), String.valueOf(theTime), String.valueOf(theTime)});
		ArrayList<String> list = convertCursorToRoomList(cursor);
		ArrayList<Room> emptyRooms = new ArrayList<Room>();
		for (int i = 0; i < ROOMS.length; ++i) {
			if (list.contains(ROOMS[i]) == false) {
				Room room = new Room();
				room.id = ROOMS[i];
				emptyRooms.add(room);
			}
		}
		Cursor timeCursor = null;
		for (Room room : emptyRooms) {
			timeCursor = db.rawQuery("select distinct startTime,  endTime from course where room == ? and startWeek <= ? and endWeek >= ? and startTime > ? and endTime < ?", 
					new String[]{room.id, String.valueOf(theWeek), String.valueOf(theWeek), String.valueOf(theTime - 720), String.valueOf(theTime + 720)});
			ArrayList<Pair<Integer, Integer>> timePairs = new ArrayList<Pair<Integer,Integer>>();
			while (timeCursor.moveToNext()) {
				Pair<Integer, Integer> pair = new Pair<Integer, Integer>(timeCursor.getInt(0), timeCursor.getInt(1));
				timePairs.add(pair);
			}
			timePairs.add(new Pair<Integer, Integer>(theTime - 720, theTime - 720));
			timePairs.add(new Pair<Integer, Integer>(theTime + 720, theTime + 720));
			Collections.sort(timePairs, new TimePairComparator());
			int start = 0, end = 0;
			for (int i = 0; i < timePairs.size() - 1; ++i) {
				start = timePairs.get(i).second;
				end = timePairs.get(i+1).first;
				if (theTime > start && theTime < end) {
					break;
				}
			}
			if (start == theTime - 720) {
				start = (theDay - 1) * 1440 + 480;
			}
			if (end == theTime + 720) {
				end = (theDay - 1) * 1440 + 1295;
			}
			room.startTime = convertTime(start);
			room.endTime = convertTime(end);
			room.intervalTime = end - start;
		}
		return emptyRooms;
	}
	
	/**  按照某些关键字查询课程，如果不按此关键字，则参数为null
	 * @param teacher 按老师关键字查询课程，如果为null则表示不按老师查询
	 * @param college 按学院关键字查询课程，如果为null则表示不按学院查询
	 * @param type    按课程类型关键字(公必、专选、专必、公选)查询课程，如果为null则表示不按课程类型查询
	 * @param name    按课程名关键字查询课程，如果为null则表示不按课程名查询
	 * @return        内含Course的ArrayList
	 */
	ArrayList<Course> queryCourse(String teacher, String college, String type, String name) {
		StringBuilder arg = new StringBuilder();
		boolean isFirst = true;
		if (teacher != null) {
			if (isFirst) {
				arg.append(" teacher like '%" + teacher + "%'");
				isFirst = false;
			}
		}
		if (college != null) {
			if (isFirst) {
				arg.append(" college like '%" + college + "%'");
				isFirst = false;
			} else {
				arg.append(" and college like '%" + college + "%'");
			}
		}
		if (type != null) {
			if (isFirst) {
				arg.append(" type like '%" + type + "%'");
				isFirst = false;
			} else {
				arg.append(" and type like '%" + type + "%'");
			}
		}
		if (name != null) {
			if (isFirst) {
				arg.append(" name like '%" + name + "%'");
				isFirst = false;
			} else {
				arg.append(" and name like '%" + name + "%'");
			}
		}
		String sarg = arg.toString();
		db = helper.getWritableDatabase();
		Cursor result = db.rawQuery("select * from course where" + sarg, null);
		ArrayList<Course> ls = new ArrayList<Course>();
		while (result.moveToNext()) {
			Course course = new Course();
			course.id = result.getString(0);
			course.name = result.getString(1);
			course.type = result.getString(2);
			course.credit = result.getDouble(3);
			course.college = result.getString(4);
			course.startWeek = result.getInt(5);
			course.endWeek = result.getInt(6);
			course.startTime = result.getInt(7);
			course.endTime = result.getInt(8);
			course.room = result.getString(9);
			course.teacher = result.getString(10);
			course.good = result.getInt(11);
			course.bad = result.getInt(12);
			course.people = result.getInt(13);
			course.time = result.getString(14);
			course.quality = result.getInt(15);
			ls.add(course);
		}
		Log.i(TAG, String.valueOf(ls.size()));
		Collections.sort(ls, new CourseComparator());
		ArrayList<Course> re = new ArrayList<Course>();
		int size = ls.size();
		if (size != 0) {
			re.add(ls.get(0));
		}
		for (int i = 1; i < size; ++i) {
			Log.i(TAG, ls.get(i).id);
			if (ls.get(i).id.equals(ls.get(i-1).id) == false) {
				re.add(ls.get(i));
			}
		}
		return re;
	}
	
	/** 插入一条评论到comment这个表中
	 * @param comment 要插入的评论,其中time的时间格式为  "YYYY-MM-DD HH:MM"
	 */
	public void addComment(Comment comment) {
		db = helper.getWritableDatabase();
		db.execSQL("insert into comment (id, time, content) values(?, ?, ?)", 
				new Object[]{comment.id, comment.time, comment.content});
	}
	
	/** 查询某个课程的所有评论
	 * @param id 课程的id
	 * @return 含Comment类型的ArrayList
	 */
	public ArrayList<Comment> queryComments(String id) {
		db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from comment where id like ?", new String[]{id});
		ArrayList<Comment> re = new ArrayList<Comment>();
		while (cursor.moveToNext()) {
			Comment c = new Comment();
			c.id = cursor.getString(0);
			c.time = cursor.getString(1);
			c.content = cursor.getString(2);
			re.add(c);
		}
		return re;
	}
	
	/** 设置某个课程的good值
	 * @param id    课程id
	 * @param good  要设置的good值
	 */
	public void setGood(String id, int good) {
		db = helper.getWritableDatabase();
		db.execSQL("update course set good = ? where id like ?", 
				new Object[]{good, id});
	}
	
	/** 设置某个课程的bad值
	 * @param id   课程id
	 * @param bad  要设置的bad值
	 */
	public void setBad(String id, int bad) {
		db = helper.getWritableDatabase();
		db.execSQL("update course set bad = ? where id like ?", 
				new Object[]{bad, id});
	}
	
	/** 设置某个课程是否允许点赞/踩
	 * @param id        课程id
	 * @param quality   是否允许点赞/踩，1表示允许，0表示不允许
	 */
	public void setQuality(String id, int quality) {
		db = helper.getWritableDatabase();
		db.execSQL("update course set quality = ? where id like ?", 
				new Object[]{quality, id});
	}
	
	/** 查询某个课程的最新评论的时间
	 * @param id  课程id
	 * @return 若有，则返回最新的时间，若没有则返回null
	 */
	public String getLatestCommentTimeOfCourse(String id) {
		db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select time from comment where id like ? order by datetime(time) desc limit 1", 
				new String[] {id});
		if (cursor.getCount() == 0)
			return null;
		cursor.moveToNext();
		return cursor.getString(0);
	}
	
	/** 给定一个时间t，t是从周日开始到t时间的分钟数，如果是星期一 00:25，则t为24 * 60 + 25 = 1465
	 * @param t 从周日开始到t时间的分钟数
	 * @return 返回 HH:MM时间格式，星期几忽略
	 */
	private String convertTime(int t) {
		t = t % 1440;
		int hour = t / 60;
		String shour, sminute;
		if (hour < 10) {
			shour = "0" + String.valueOf(hour);
		} else {
			shour = String.valueOf(hour);
		}
		int minute = t % 60;
		if (minute < 10) {
			sminute = "0" + String.valueOf(minute);
		} else {
			sminute = String.valueOf(minute);
		}
		return shour + ":" + sminute;
	}
	
	/** 将一个Cursor对象内数据转成Arraylist
	 * @param cursor
	 * @return
	 */
	private ArrayList<String> convertCursorToRoomList(Cursor cursor) {
		ArrayList<String> list = new ArrayList<String>();
		while (cursor.moveToNext()) {
			list.add(cursor.getString(0));
		}
		return list;
	}
	
	class TimePairComparator implements Comparator<Pair<Integer, Integer>> {

		@Override
		public int compare(Pair<Integer, Integer> lhs,
				Pair<Integer, Integer> rhs) {
			if (lhs.first < rhs.first) return -1;
			return 1;
		}
	}
	
	/** Course类的比较器，按id字段的字典序比较
	 * @author chenzhg
	 *
	 */
	class CourseComparator implements Comparator<Course> {
		@Override
		public int compare(Course lhs, Course rhs) {
			return lhs.id.compareTo(rhs.id);
		}	
	}
}
