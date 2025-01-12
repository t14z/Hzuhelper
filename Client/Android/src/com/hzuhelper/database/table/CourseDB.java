package com.hzuhelper.database.table;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hzuhelper.database.DALHelper;
import com.hzuhelper.model.receive.P6004;
import com.hzuhelper.utils.Common;

public class CourseDB {
	public static final String TABLE_NAME = "Course";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_WEEKTIME = "weektime";
	public static final String COLUMN_DAYTIME = "daytime";
	public static final String COLUMN_COURSETIME1 = "coursetime1";
	public static final String COLUMN_COURSETIME2 = "coursetime2";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_TEACHER = "teacher";
	public static final String COLUMN_SITE = "site";
	public static final String COLUMN_STATU = "statu";
	public static final String COLUMN_STARTTIME = "starttime";
	public static final String COLUMN_ENDTIME = "endtime";
	public static final String COLUMN_SERVERID = "serverid";

	private CourseDB() {}

	public static void save(P6004 course) {
		SQLiteDatabase db = DALHelper.getInstance().getWritableDatabase();
		db.execSQL(
				"insert into " + TABLE_NAME + " (" + COLUMN_WEEKTIME + ","
						+ COLUMN_DAYTIME + "," + COLUMN_COURSETIME1 + ","
						+ COLUMN_COURSETIME2 + "," + COLUMN_STARTTIME + ","
						+ COLUMN_ENDTIME + "," + COLUMN_NAME + ","
						+ COLUMN_TEACHER + "," + COLUMN_SITE + ","
						+ COLUMN_STATU + "," + COLUMN_SERVERID + ")"
						+ "  values (?,?,?,?,?,?,?,?,?,?,?)",
				new Object[] { course.getWeektime(), course.getDaytime(),
						course.getCoursetime1(), course.getCoursetime2(),
						course.getStarttime(), course.getEndtime(),
						course.getName(), course.getTeacher(),
						course.getSite(), course.getStatu(),
						course.getServerid() });
		db.close();
	}

	public static void delete(int id) {
		SQLiteDatabase db = DALHelper.getInstance().getWritableDatabase();
		db.execSQL("delete from " + TABLE_NAME + " where " + COLUMN_ID + "=?",
				new Object[] { id });
		db.close();
	}

	public static void delete() {
		SQLiteDatabase db = DALHelper.getInstance().getWritableDatabase();
		db.execSQL("delete from " + TABLE_NAME);
		db.close();
	}

	public static void update(P6004 course) {
		SQLiteDatabase db = DALHelper.getInstance().getWritableDatabase();
		db.execSQL(
				"update " + TABLE_NAME + " set " + COLUMN_WEEKTIME + "=?,"
						+ COLUMN_DAYTIME + "=?," + COLUMN_COURSETIME1 + "=?,"
						+ COLUMN_COURSETIME2 + "=?," + COLUMN_STARTTIME + "=?,"
						+ COLUMN_ENDTIME + "=?," + COLUMN_NAME + "=?,"
						+ COLUMN_TEACHER + "=?," + COLUMN_SITE + "=?,"
						+ COLUMN_STATU + "=?," + COLUMN_SERVERID + "=? where "
						+ COLUMN_ID + "=?",
				new Object[] { course.getWeektime(), course.getDaytime(),
						course.getCoursetime1(), course.getCoursetime2(),
						course.getStarttime(), course.getEndtime(),
						course.getName(), course.getTeacher(),
						course.getSite(), course.getStatu(),
						course.getServerid(), course.getId() });
		db.close();
	}

	public static int count() {
		SQLiteDatabase db = DALHelper.getInstance().getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from " + TABLE_NAME, null);
		int count = 0;
		if (cursor.moveToLast()) {
			count = cursor.getInt(0);
		}
		db.close();
		return count;
	}

	public static LinkedList<P6004> getList(Date date) {
		byte dayOfWeek = Common.getDayOfWeek(date);
		int WeekOfTerm = Common.getWeekOfTerm(date);
		int mono = WeekOfTerm % 2 == 0 ? 2 : 1;
		LinkedList<P6004> clist = new LinkedList<P6004>();
		SQLiteDatabase db = DALHelper.getInstance().getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select * from " + TABLE_NAME + " where "
								+ COLUMN_WEEKTIME + "=? and " + COLUMN_DAYTIME
								+ "=? and " + COLUMN_STARTTIME + "<=? "
								+ "and " + COLUMN_ENDTIME + ">=? ",
						new String[] { String.valueOf(mono),
								String.valueOf(dayOfWeek),
								String.valueOf(WeekOfTerm),
								String.valueOf(WeekOfTerm) });
		LinkedList<P6004> list = modelToLinkedList(cursor, clist);
		db.close();
		return list;
	}

	public static LinkedList<P6004> getLinkedList() {
		LinkedList<P6004> clist = new LinkedList<P6004>();
		SQLiteDatabase db = DALHelper.getInstance().getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_NAME
				+ " order by " + COLUMN_DAYTIME + "," + COLUMN_COURSETIME1,
				null);
		LinkedList<P6004> list = modelToLinkedList(cursor, clist);
		db.close();
		return list;
	}

	public static List<P6004> getList(String cmd) {
		LinkedList<P6004> clist = new LinkedList<P6004>();
		SQLiteDatabase db = DALHelper.getInstance().getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where "
				+ cmd + " order by " + COLUMN_DAYTIME + ","
				+ COLUMN_COURSETIME1, null);
		LinkedList<P6004> list = modelToLinkedList(cursor, clist);
		db.close();
		return list;
	}

	public static P6004 getModel(int id) {
		SQLiteDatabase db = DALHelper.getInstance().getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where "
				+ COLUMN_ID + "=?", new String[] { String.valueOf(id) });
		P6004 model;
		if (cursor.moveToFirst()) {
			model = cursorToModel(cursor);
		} else {
			model = null;
		}
		db.close();
		return model;
	}

	private static LinkedList<P6004> modelToLinkedList(Cursor cursor,
			LinkedList<P6004> clist) {
		while (cursor.moveToNext()) {
			clist.add(cursorToModel(cursor));
		}
		return clist;
	}

	private static P6004 cursorToModel(Cursor cursor) {
		P6004 model = new P6004();
		model.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
		model.setServerid(cursor.getInt(cursor.getColumnIndex(COLUMN_SERVERID)));
		model.setWeektime(cursor.getInt(cursor.getColumnIndex(COLUMN_WEEKTIME)));
		model.setDaytime(cursor.getInt(cursor.getColumnIndex(COLUMN_DAYTIME)));
		model.setCoursetime1(cursor.getInt(cursor
				.getColumnIndex(COLUMN_COURSETIME1)));
		model.setCoursetime2(cursor.getInt(cursor
				.getColumnIndex(COLUMN_COURSETIME2)));
		model.setStarttime(cursor.getInt(cursor
				.getColumnIndex(COLUMN_STARTTIME)));
		model.setEndtime(cursor.getInt(cursor.getColumnIndex(COLUMN_ENDTIME)));
		model.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
		model.setTeacher(cursor.getString(cursor.getColumnIndex(COLUMN_TEACHER)));
		model.setSite(cursor.getString(cursor.getColumnIndex(COLUMN_SITE)));
		model.setStatu(cursor.getInt(cursor.getColumnIndex(COLUMN_STATU)));
		return model;
	}

	public static String createTableCommander() {
		return "create table " + TABLE_NAME + " (" + COLUMN_ID
				+ " integer primary key AUTOINCREMENT, " + COLUMN_WEEKTIME
				+ " integer , " + COLUMN_DAYTIME + " integer , "
				+ COLUMN_COURSETIME1 + " integer , " + COLUMN_COURSETIME2
				+ " integer , " + COLUMN_NAME + " text , " + COLUMN_TEACHER
				+ " text , " + COLUMN_SITE + " text , " + COLUMN_STATU
				+ " integer,  " + COLUMN_STARTTIME + " integer , "
				+ COLUMN_ENDTIME + " integer , " + COLUMN_SERVERID
				+ " integer  )";
	}

}
