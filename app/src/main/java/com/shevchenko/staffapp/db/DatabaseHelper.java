package com.shevchenko.staffapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.shevchenko.staffapp.Model.Category;
import com.shevchenko.staffapp.Model.CompleteTask;
import com.shevchenko.staffapp.Model.CompltedTinTask;
import com.shevchenko.staffapp.Model.LogEvent;
import com.shevchenko.staffapp.Model.LogFile;
import com.shevchenko.staffapp.Model.Producto;
import com.shevchenko.staffapp.Model.Producto_RutaAbastecimento;
import com.shevchenko.staffapp.Model.TaskInfo;
import com.shevchenko.staffapp.Model.TaskType;
import com.shevchenko.staffapp.Model.TinTask;
import com.shevchenko.staffapp.Model.PendingTasks;
import com.shevchenko.staffapp.Model.User;

public class DatabaseHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "task.db";
	private final static int DB_VERSION = 3;

	/*
	private static DatabaseHelper sInstance;
	public static synchronized DatabaseHelper getInstance(Context context) {

		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (sInstance == null) {
			sInstance = new DatabaseHelper(context.getApplicationContext());
		}
		return sInstance;
	}*/
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}	

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String strQuery = "CREATE TABLE IF NOT EXISTS " + CompleteTask.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ CompleteTask.USERID + " TEXT, "
				+ CompleteTask.TASKID + " INTEGER, "
				+ CompleteTask.DATE + " TEXT, "
				+ CompleteTask.TASKTYPE + " TEXT, "
				+ CompleteTask.RUTAABASTEIMIENTO + " TEXT, "
				+ CompleteTask.TASKBUSINESSKEY + " TEXT, "
				+ CompleteTask.CUSTOMER + " TEXT, "
				+ CompleteTask.ADRESS + " TEXT, "
				+ CompleteTask.LOCATIONDESC + " TEXT, "
				+ CompleteTask.MODEL + " TEXT, "
				+ CompleteTask.LATITUDE + " TEXT, "
				+ CompleteTask.LONGITUDE + " TEXT, "
				+ CompleteTask.EPV + " TEXT, "
				+ CompleteTask.LOGLATITUDE + " TEXT, "
				+ CompleteTask.LOGLONGITUDE + " TEXT, "
				+ CompleteTask.ACTIONDATE + " TEXT, "
				+ CompleteTask.FILE1 + " TEXT, "
				+ CompleteTask.FILE2 + " TEXT, "
				+ CompleteTask.FILE3 + " TEXT, "
				+ CompleteTask.FILE4 + " TEXT, "
				+ CompleteTask.FILE5 + " TEXT, "
				+ CompleteTask.MACHINETYPE + " TEXT, "
				+ CompleteTask.SIGNATURE + " TEXT, "
				+ CompleteTask.NUMEROGUIA + " TEXT, "
				+ CompleteTask.GLOSA + " TEXT, "
				+ CompleteTask.AUX_VALOR1 + " TEXT);";
		db.execSQL(strQuery);

		String strQueryTin = "CREATE TABLE IF NOT EXISTS " + TinTask.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ TinTask.USERID + " TEXT, "
				+ TinTask.TASKID + " INTEGER, "
				+ TinTask.TASKTYPE + " TEXT, "
				+ TinTask.RUTAABASTEIMIENTO + " TEXT, "
				+ TinTask.CUS + " TEXT, "
				+ TinTask.NUS + " TEXT, "
				+ TinTask.QUANTITY + " TEXT);";
		db.execSQL(strQueryTin);

		String strQueryCompleteTin = "CREATE TABLE IF NOT EXISTS " + CompltedTinTask.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ CompltedTinTask.USERID + " TEXT, "
				+ CompltedTinTask.TASKID + " INTEGER, "
				+ CompltedTinTask.TASKTYPE + " TEXT, "
				+ CompltedTinTask.RUTAABASTEIMIENTO + " TEXT, "
				+ CompltedTinTask.CUS + " TEXT, "
				+ CompltedTinTask.NUS + " TEXT, "
				+ CompltedTinTask.QUANTITY + " TEXT);";
		db.execSQL(strQueryCompleteTin);

		String strQueryLog = "CREATE TABLE IF NOT EXISTS " + LogEvent.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ LogEvent.USERID + " TEXT, "
				+ LogEvent.TASKID + " TEXT, "
				+ LogEvent.DATETIME + " TEXT, "
				+ LogEvent.DESCRIPTION + " TEXT, "
				+ LogEvent.LATITUDE + " TEXT, "
				+ LogEvent.LONGITUDE + " TEXT);";
		db.execSQL(strQueryLog);

		String strQueryIncomplete = "CREATE TABLE IF NOT EXISTS " + TaskInfo.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ TaskInfo.USERID + " TEXT, "
				+ TaskInfo.TASKID + " INTEGER, "
				+ TaskInfo.DATE + " TEXT, "
				+ TaskInfo.TASKTYPE + " TEXT, "
				+ TaskInfo.RUATABASTECIMIENTO + " TEXT, "
				+ TaskInfo.TASKBUSINESSKEY + " TEXT, "
				+ TaskInfo.CUSTOMER + " TEXT, "
				+ TaskInfo.ADRESS + " TEXT, "
				+ TaskInfo.LOCATIONDESC + " TEXT, "
				+ TaskInfo.MODEL + " TEXT, "
				+ TaskInfo.LATITUDE + " TEXT, "
				+ TaskInfo.LONGITUDE + " TEXT, "
				+ TaskInfo.EPV + " TEXT, "
				+ TaskInfo.DISTANCE + " TEXT, "
				+ TaskInfo.MACHINETYPE + " TEXT, "
				+ TaskInfo.AUX_VALOR1 + " TEXT);";
		db.execSQL(strQueryIncomplete);

		String strQueryPending = "CREATE TABLE IF NOT EXISTS " + PendingTasks.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ PendingTasks.USERID + " TEXT, "
				+ PendingTasks.TASKID + " INTEGER, "
				+ PendingTasks.DATE + " TEXT, "
				+ PendingTasks.TASKTYPE + " TEXT, "
				+ PendingTasks.RUTAABASTEIMIENTO + " TEXT, "
				+ PendingTasks.TASKBUSINESSKEY + " TEXT, "
				+ PendingTasks.CUSTOMER + " TEXT, "
				+ PendingTasks.ADRESS + " TEXT, "
				+ PendingTasks.LOCATIONDESC + " TEXT, "
				+ PendingTasks.MODEL + " TEXT, "
				+ PendingTasks.LATITUDE + " TEXT, "
				+ PendingTasks.LONGITUDE + " TEXT, "
				+ PendingTasks.EPV + " TEXT, "
				+ PendingTasks.LOGLATITUDE + " TEXT, "
				+ PendingTasks.LOGLONGITUDE + " TEXT, "
				+ PendingTasks.ACTIONDATE + " TEXT, "
				+ PendingTasks.FILE1 + " TEXT, "
				+ PendingTasks.FILE2 + " TEXT, "
				+ PendingTasks.FILE3 + " TEXT, "
				+ PendingTasks.FILE4 + " TEXT, "
				+ PendingTasks.FILE5 + " TEXT, "
				+ PendingTasks.MACHINETYPE + " TEXT, "
				+ CompleteTask.SIGNATURE + " TEXT, "
				+ CompleteTask.NUMEROGUIA + " TEXT, "
				+ CompleteTask.GLOSA + " TEXT, "
				+ PendingTasks.AUX_VALOR1 + " TEXT);";
		db.execSQL(strQueryPending);

		String strQueryUser = "CREATE TABLE IF NOT EXISTS " + User.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ User.USERID + " TEXT, "
				+ User.PASSWORD + " TEXT);";
		db.execSQL(strQueryUser);

		String strQueryProducto = "CREATE TABLE IF NOT EXISTS " + Producto.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Producto.CUS + " TEXT, "
				+ Producto.NUS + " TEXT);";
		db.execSQL(strQueryProducto);

		String strQueryProductoRuta = "CREATE TABLE IF NOT EXISTS " + Producto_RutaAbastecimento.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Producto_RutaAbastecimento.TASKTYPE + " TEXT, "
				+ Producto_RutaAbastecimento.TASKBUSINESSKEY + " TEXT, "
				+ Producto_RutaAbastecimento.RUTAABASTECIMENTO + " TEXT, "
				+ Producto_RutaAbastecimento.CUS + " TEXT);";
		db.execSQL(strQueryProductoRuta);

		String strQueryCategory = "CREATE TABLE IF NOT EXISTS " + Category.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Category.CATEGORY + " TEXT);";
		db.execSQL(strQueryCategory);

		String strQuerytype = "CREATE TABLE IF NOT EXISTS " + TaskType.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ TaskType.TYPE + " TEXT, "
				+ TaskType.NAME + " TEXT);";
		db.execSQL(strQuerytype);

		String strQueryLogFile = "CREATE TABLE IF NOT EXISTS " + LogFile.TABLENAME + " (no INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ LogFile.TASKID + " INTEGER, "
				+ LogFile.CAPTURE_FILE + " TEXT, "
				+ LogFile.FILE_NAME + " TEXT);";

		db.execSQL(strQueryLogFile);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w(DatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS tb_task");
		onCreate(db);
	}

}
