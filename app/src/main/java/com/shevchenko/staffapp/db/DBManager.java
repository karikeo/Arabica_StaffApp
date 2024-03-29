package com.shevchenko.staffapp.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.gcm.Task;
import com.shevchenko.staffapp.Model.Category;
import com.shevchenko.staffapp.Model.CompleteTask;
import com.shevchenko.staffapp.Model.CompltedTinTask;
import com.shevchenko.staffapp.Model.LogEvent;
import com.shevchenko.staffapp.Model.PendingTasks;
import com.shevchenko.staffapp.Model.Producto;
import com.shevchenko.staffapp.Model.Producto_RutaAbastecimento;
import com.shevchenko.staffapp.Model.TaskInfo;
import com.shevchenko.staffapp.Model.TaskType;
import com.shevchenko.staffapp.Model.TinTask;
import com.shevchenko.staffapp.Model.User;
import com.shevchenko.staffapp.PendingTask;

public class DBManager {
	/*
	private Context mContext;
	public DBManager(Context context) {
		mContext = context;
	}*/
	private DatabaseHelper mDBHelper;
	public DBManager(Context context) {
		mDBHelper = new DatabaseHelper(context);
	}


	public long insertLogEvent(String userid, String taskid, String strDateTime, String strDescription, String strLatitude, String strLongitude) {
		ContentValues values = new ContentValues();
		values.put(LogEvent.USERID, userid);
		values.put(LogEvent.TASKID, taskid);
		values.put(LogEvent.DATETIME, strDateTime);
		values.put(LogEvent.DESCRIPTION, strDescription);
		values.put(LogEvent.LATITUDE, strLatitude);
		values.put(LogEvent.LONGITUDE, strLongitude);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(LogEvent.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	public long updateInCompleteTaskDistance(TaskInfo task) {
		ContentValues values = new ContentValues();
		values.put(TaskInfo.DISTANCE, task.distance);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.update(TaskInfo.TABLENAME, values, TaskInfo.USERID + "=" + task.userid + " AND " + TaskInfo.TASKID + "=" + task.taskID, null);
			//long lRet = db.insert(TaskInfo.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	public long insertInCompleteTask(TaskInfo task) {
		ContentValues values = new ContentValues();
		values.put(TaskInfo.USERID, task.userid);
		values.put(TaskInfo.TASKID, task.taskID);
		values.put(TaskInfo.DATE, task.date);
		values.put(TaskInfo.TASKTYPE, task.taskType);
		values.put(TaskInfo.RUATABASTECIMIENTO, task.RutaAbastecimiento);
		values.put(TaskInfo.TASKBUSINESSKEY, task.TaskBusinessKey);
		values.put(TaskInfo.CUSTOMER, task.Customer);
		values.put(TaskInfo.ADRESS, task.Adress);
		values.put(TaskInfo.LOCATIONDESC, task.LocationDesc);
		values.put(TaskInfo.MODEL, task.Model);
		values.put(TaskInfo.LATITUDE, task.latitude);
		values.put(TaskInfo.LONGITUDE, task.longitude);
		values.put(TaskInfo.EPV, task.epv);
		values.put(TaskInfo.DISTANCE, task.distance);
		values.put(TaskInfo.MACHINETYPE, task.MachineType);
		values.put(TaskInfo.AUX_VALOR1, task.Aux_valor1);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(TaskInfo.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	public long insertCompleteTask(CompleteTask task) {
		ContentValues values = new ContentValues();
		values.put(CompleteTask.USERID, task.userid);
		values.put(CompleteTask.TASKID, task.taskid);
		values.put(CompleteTask.DATE, task.date);
		values.put(CompleteTask.TASKTYPE, task.tasktype);
		values.put(CompleteTask.RUTAABASTEIMIENTO, task.RutaAbastecimiento);
		values.put(CompleteTask.TASKBUSINESSKEY, task.TaskBusinessKey);
		values.put(CompleteTask.CUSTOMER, task.Customer);
		values.put(CompleteTask.ADRESS, task.Adress);
		values.put(CompleteTask.LOCATIONDESC, task.LocationDesc);
		values.put(CompleteTask.MODEL, task.Model);
		values.put(CompleteTask.LATITUDE, task.latitude);
		values.put(CompleteTask.LONGITUDE, task.longitude);
		values.put(CompleteTask.EPV, task.epv);
		values.put(CompleteTask.LOGLATITUDE, task.logLatitude);
		values.put(CompleteTask.LOGLONGITUDE, task.logLongitude);
		values.put(CompleteTask.ACTIONDATE, task.ActionDate);
		values.put(CompleteTask.FILE1, task.file1);
		values.put(CompleteTask.FILE2, task.file2);
		values.put(CompleteTask.FILE3, task.file3);
		values.put(CompleteTask.FILE4, task.file4);
		values.put(CompleteTask.FILE5, task.file5);
		values.put(CompleteTask.MACHINETYPE, task.MachineType);
		values.put(CompleteTask.SIGNATURE, task.Signature);
		values.put(CompleteTask.NUMEROGUIA, task.NumeroGuia);
		values.put(CompleteTask.GLOSA, task.Glosa);
		values.put(CompleteTask.AUX_VALOR1, task.Aux_valor1);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(CompleteTask.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	public long insertPendingTask(PendingTasks task) {
		ContentValues values = new ContentValues();
		values.put(PendingTasks.USERID, task.userid);
		values.put(PendingTasks.TASKID, task.taskid);
		values.put(PendingTasks.DATE, task.date);
		values.put(PendingTasks.TASKTYPE, task.tasktype);
		values.put(PendingTasks.RUTAABASTEIMIENTO, task.RutaAbastecimiento);
		values.put(PendingTasks.TASKBUSINESSKEY, task.TaskBusinessKey);
		values.put(PendingTasks.CUSTOMER, task.Customer);
		values.put(PendingTasks.ADRESS, task.Adress);
		values.put(PendingTasks.LOCATIONDESC, task.LocationDesc);
		values.put(PendingTasks.MODEL, task.Model);
		values.put(PendingTasks.LATITUDE, task.latitude);
		values.put(PendingTasks.LONGITUDE, task.longitude);
		values.put(PendingTasks.EPV, task.epv);
		values.put(PendingTasks.LOGLATITUDE, task.logLatitude);
		values.put(PendingTasks.LOGLONGITUDE, task.logLongitude);
		values.put(PendingTasks.ACTIONDATE, task.ActionDate);
		values.put(PendingTasks.FILE1, task.file1);
		values.put(PendingTasks.FILE2, task.file2);
		values.put(PendingTasks.FILE3, task.file3);
		values.put(PendingTasks.FILE4, task.file4);
		values.put(PendingTasks.FILE5, task.file5);
		values.put(PendingTasks.MACHINETYPE, task.MachineType);
		values.put(PendingTasks.SIGNATURE, task.Signature);
		values.put(PendingTasks.NUMEROGUIA, task.NumeroGuia);
		values.put(PendingTasks.GLOSA, task.Glosa);
		values.put(PendingTasks.AUX_VALOR1, task.Aux_valor1);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(PendingTasks.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;		
	}
	public long insertPendingTinTask(TinTask task) {
		ContentValues values = new ContentValues();
		values.put(TinTask.USERID, task.userid);
		values.put(TinTask.TASKID, task.taskid);
		values.put(TinTask.TASKTYPE, task.tasktype);
		values.put(TinTask.RUTAABASTEIMIENTO, task.RutaAbastecimiento);
		values.put(TinTask.CUS, task.cus);
		values.put(TinTask.NUS, task.nus);
		values.put(TinTask.QUANTITY, task.quantity);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(TinTask.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	public long insertCompleteTinTask(CompltedTinTask task) {
		ContentValues values = new ContentValues();
		values.put(CompltedTinTask.USERID, task.userid);
		values.put(CompltedTinTask.TASKID, task.taskid);
		values.put(CompltedTinTask.TASKTYPE, task.tasktype);
		values.put(CompltedTinTask.RUTAABASTEIMIENTO, task.RutaAbastecimiento);
		values.put(CompltedTinTask.CUS, task.cus);
		values.put(CompltedTinTask.NUS, task.nus);
		values.put(CompltedTinTask.QUANTITY, task.quantity);

		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(CompltedTinTask.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	public long insertCategory(Category cat) {
		ContentValues values = new ContentValues();
		values.put(Category.CATEGORY, cat.category);
		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(Category.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	public long insertType(TaskType info) {
		ContentValues values = new ContentValues();
		values.put(TaskType.TYPE, info.type);
		values.put(TaskType.NAME, info.name);
		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(TaskType.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	public long insertProducto(Producto info) {
		ContentValues values = new ContentValues();
		values.put(Producto.CUS, info.cus);
		values.put(Producto.NUS, info.nus);
		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(Producto.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	public long insertProducto_Ruta(Producto_RutaAbastecimento info) {
		ContentValues values = new ContentValues();
		values.put(Producto_RutaAbastecimento.TASKTYPE, info.TaskType);
		values.put(Producto_RutaAbastecimento.TASKBUSINESSKEY, info.TaskBusinessKey);
		values.put(Producto_RutaAbastecimento.RUTAABASTECIMENTO, info.RutaAbastecimento);
		values.put(Producto_RutaAbastecimento.CUS, info.cus);
		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(Producto_RutaAbastecimento.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	public long insertUser(User info) {
		ContentValues values = new ContentValues();
		values.put(User.USERID, info.userid);
		values.put(User.PASSWORD, info.password);
		try {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			long lRet = db.insert(User.TABLENAME, null, values);
			//db.close();
			return lRet;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	public User getUser(String userid){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		User info = new User();
		Cursor cursor = db.query(User.TABLENAME, new String[] {
				User.USERID,
				User.PASSWORD,
		}, User.USERID + "=" + userid, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			info.userid = cursor.getString(0);
			info.password = cursor.getString(1);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return info;
	}
	public ArrayList<CompltedTinTask> getCompleteTinTask(String userid){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<CompltedTinTask> lstTasks = new ArrayList<CompltedTinTask>();
		Cursor cursor = db.query(CompltedTinTask.TABLENAME, new String[] {
				CompltedTinTask.USERID,
				CompltedTinTask.TASKID,
				CompltedTinTask.TASKTYPE,
				CompltedTinTask.RUTAABASTEIMIENTO,
				CompltedTinTask.CUS,
				CompltedTinTask.NUS,
				CompltedTinTask.QUANTITY,
		}, CompltedTinTask.USERID + "=" + userid, null, null, null, CompltedTinTask.TASKID + " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CompltedTinTask task = new CompltedTinTask();
			task.userid = cursor.getString(0);
			task.taskid = cursor.getInt(1);
			task.tasktype = cursor.getString(2);
			task.RutaAbastecimiento = cursor.getString(3);
			task.cus = cursor.getString(4);
			task.nus = cursor.getString(5);
			task.quantity = cursor.getString(6);

			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}
	public ArrayList<TinTask> getTinPendingTask(String userid){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<TinTask> lstTasks = new ArrayList<TinTask>();
		Cursor cursor = db.query(TinTask.TABLENAME, new String[] {
				TinTask.USERID,
				TinTask.TASKID,
				TinTask.TASKTYPE,
				TinTask.RUTAABASTEIMIENTO,
				TinTask.CUS,
				TinTask.NUS,
				TinTask.QUANTITY,
		}, TinTask.USERID + "=" + userid, null, null, null, TinTask.TASKID + " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TinTask task = new TinTask();
			task.userid = cursor.getString(0);
			task.taskid = cursor.getInt(1);
			task.tasktype = cursor.getString(2);
			task.RutaAbastecimiento = cursor.getString(3);
			task.cus = cursor.getString(4);
			task.nus = cursor.getString(5);
			task.quantity = cursor.getString(6);

			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}
	public ArrayList<TaskInfo> getInCompleteTask(String userid){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<TaskInfo> lstTasks = new ArrayList<TaskInfo>();
		Cursor cursor = db.query(TaskInfo.TABLENAME, new String[] {
				TaskInfo.USERID,
				TaskInfo.TASKID,
				TaskInfo.DATE,
				TaskInfo.TASKTYPE,
				TaskInfo.RUATABASTECIMIENTO,
				TaskInfo.TASKBUSINESSKEY,
				TaskInfo.CUSTOMER,
				TaskInfo.ADRESS,
				TaskInfo.LOCATIONDESC,
				TaskInfo.MODEL,
				TaskInfo.LATITUDE,
				TaskInfo.LONGITUDE,
				TaskInfo.EPV,
				TaskInfo.DISTANCE,
				TaskInfo.MACHINETYPE,
				TaskInfo.AUX_VALOR1,
		}, TaskInfo.USERID + "=" + userid, null, null, null, TaskInfo.DISTANCE + " ASC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TaskInfo task = new TaskInfo();
			task.userid = cursor.getString(0);
			task.taskID = cursor.getInt(1);
			task.date = cursor.getString(2);
			task.taskType = cursor.getString(3);
			task.RutaAbastecimiento = cursor.getString(4);
			task.TaskBusinessKey = cursor.getString(5);
			task.Customer = cursor.getString(6);
			task.Adress = cursor.getString(7);
			task.LocationDesc = cursor.getString(8);
			task.Model = cursor.getString(9);
			task.latitude = cursor.getString(10);
			task.longitude = cursor.getString(11);
			task.epv = cursor.getString(12);
			task.distance = cursor.getString(13);
			task.MachineType = cursor.getString(14);
			task.Aux_valor1 = cursor.getString(15);

			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}
	public ArrayList<CompleteTask> getCompleteTask(String userid){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<CompleteTask> lstTasks = new ArrayList<CompleteTask>();
		Cursor cursor = db.query(CompleteTask.TABLENAME, new String[] {
				CompleteTask.USERID,
				CompleteTask.TASKID,
				CompleteTask.DATE,
				CompleteTask.TASKTYPE,
				CompleteTask.RUTAABASTEIMIENTO,
				CompleteTask.TASKBUSINESSKEY,
				CompleteTask.CUSTOMER,
				CompleteTask.ADRESS,
				CompleteTask.LOCATIONDESC,
				CompleteTask.MODEL,
				CompleteTask.LATITUDE,
				CompleteTask.LONGITUDE,
				CompleteTask.EPV,
				CompleteTask.LOGLATITUDE,
				CompleteTask.LOGLONGITUDE,
				CompleteTask.ACTIONDATE,
				CompleteTask.FILE1,
				CompleteTask.FILE2,
				CompleteTask.FILE3,
				CompleteTask.FILE4,
				CompleteTask.FILE5,
				CompleteTask.MACHINETYPE,
				CompleteTask.SIGNATURE,
				CompleteTask.NUMEROGUIA,
				CompleteTask.GLOSA,
				CompleteTask.AUX_VALOR1,
		}, CompleteTask.USERID + "=" + userid, null, null, null, CompleteTask.TASKID + " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CompleteTask task = new CompleteTask();
			task.userid = cursor.getString(0);
			task.taskid = cursor.getInt(1);
			task.date = cursor.getString(2);
			task.tasktype = cursor.getString(3);
			task.RutaAbastecimiento = cursor.getString(4);
			task.TaskBusinessKey = cursor.getString(5);
			task.Customer = cursor.getString(6);
			task.Adress = cursor.getString(7);
			task.LocationDesc = cursor.getString(8);
			task.Model = cursor.getString(9);
			task.latitude = cursor.getString(10);
			task.longitude = cursor.getString(11);
			task.epv = cursor.getString(12);
			task.logLatitude = cursor.getString(13);
			task.logLongitude = cursor.getString(14);
			task.ActionDate = cursor.getString(15);
			task.file1 = cursor.getString(16);
			task.file2 = cursor.getString(17);
			task.file3 = cursor.getString(18);
			task.file4 = cursor.getString(19);
			task.file5 = cursor.getString(20);
			task.MachineType = cursor.getString(21);
			task.Signature = cursor.getString(22);
			task.NumeroGuia = cursor.getString(23);
			task.Glosa = cursor.getString(24);
			task.Aux_valor1 = cursor.getString(25);

			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}
	public ArrayList<PendingTasks> getPendingTask(String userid){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<PendingTasks> lstTasks = new ArrayList<PendingTasks>();
		Cursor cursor = db.query(PendingTasks.TABLENAME, new String[] {
				PendingTasks.USERID,
				PendingTasks.TASKID,
				PendingTasks.DATE,
				PendingTasks.TASKTYPE,
				PendingTasks.RUTAABASTEIMIENTO,
				PendingTasks.TASKBUSINESSKEY,
				PendingTasks.CUSTOMER,
				PendingTasks.ADRESS,
				PendingTasks.LOCATIONDESC,
				PendingTasks.MODEL,
				PendingTasks.LATITUDE,
				PendingTasks.LONGITUDE,
				PendingTasks.EPV,
				PendingTasks.LOGLATITUDE,
				PendingTasks.LOGLONGITUDE,
				PendingTasks.ACTIONDATE,
				PendingTasks.FILE1,
				PendingTasks.FILE2,
				PendingTasks.FILE3,
				PendingTasks.FILE4,
				PendingTasks.FILE5,
				PendingTasks.MACHINETYPE,
				PendingTasks.SIGNATURE,
				PendingTasks.NUMEROGUIA,
				PendingTasks.GLOSA,
				PendingTasks.AUX_VALOR1,
		}, PendingTasks.USERID + "=" + userid, null, null, null, PendingTasks.TASKID + " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			PendingTasks task = new PendingTasks();
			task.userid = cursor.getString(0);
			task.taskid = cursor.getInt(1);
			task.date = cursor.getString(2);
			task.tasktype = cursor.getString(3);
			task.RutaAbastecimiento = cursor.getString(4);
			task.TaskBusinessKey = cursor.getString(5);
			task.Customer = cursor.getString(6);
			task.Adress = cursor.getString(7);
			task.LocationDesc = cursor.getString(8);
			task.Model = cursor.getString(9);
			task.latitude = cursor.getString(10);
			task.longitude = cursor.getString(11);
			task.epv = cursor.getString(12);
			task.logLatitude = cursor.getString(13);
			task.logLongitude = cursor.getString(14);
			task.ActionDate = cursor.getString(15);
			task.file1 = cursor.getString(16);
			task.file2 = cursor.getString(17);
			task.file3 = cursor.getString(18);
			task.file4 = cursor.getString(19);
			task.file5 = cursor.getString(20);
			task.MachineType = cursor.getString(21);
			task.Signature = cursor.getString(22);
			task.NumeroGuia = cursor.getString(23);
			task.Glosa = cursor.getString(24);
			task.Aux_valor1 = cursor.getString(25);

			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}
	public ArrayList<Category> getAllCategory(){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<Category> lstTasks = new ArrayList<Category>();
		Cursor cursor = db.query(Category.TABLENAME, new String[] {
				Category.CATEGORY,
		}, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Category task = new Category();
			task.category = cursor.getString(0);
			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}
	public ArrayList<TaskType> getAllTypes(){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<TaskType> lstTasks = new ArrayList<TaskType>();
		Cursor cursor = db.query(TaskType.TABLENAME, new String[] {
				TaskType.TYPE,
				TaskType.NAME
		}, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TaskType task = new TaskType();
			task.type = cursor.getString(0);
			task.name = cursor.getString(1);
			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}
	public ArrayList<Producto> getAllProducto(){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<Producto> lstTasks = new ArrayList<Producto>();
		Cursor cursor = db.query(Producto.TABLENAME, new String[] {
				Producto.CUS,
				Producto.NUS,
		}, null, null, null, null, Producto.CUS + " ASC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Producto task = new Producto();
			task.cus = cursor.getString(0);
			task.nus = cursor.getString(1);
			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}
	public ArrayList<Producto_RutaAbastecimento> getAllProducto_Ruta(){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<Producto_RutaAbastecimento> lstTasks = new ArrayList<Producto_RutaAbastecimento>();
		Cursor cursor = db.query(Producto_RutaAbastecimento.TABLENAME, new String[] {
				Producto_RutaAbastecimento.TASKTYPE,
				Producto_RutaAbastecimento.TASKBUSINESSKEY,
				Producto_RutaAbastecimento.RUTAABASTECIMENTO,
				Producto_RutaAbastecimento.CUS,
		}, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Producto_RutaAbastecimento task = new Producto_RutaAbastecimento();
			task.TaskType = cursor.getString(0);
			task.TaskBusinessKey = cursor.getString(1);
			task.RutaAbastecimento = cursor.getString(2);
			task.cus = cursor.getString(3);
			lstTasks.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}
	public ArrayList<LogEvent> getLogEvents(String userid){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<LogEvent> lstTasks = new ArrayList<LogEvent>();
		Cursor cursor = db.query(LogEvent.TABLENAME, new String[] {
				LogEvent.USERID,
				LogEvent.TASKID,
				LogEvent.DATETIME,
				LogEvent.DESCRIPTION,
				LogEvent.LATITUDE,
				LogEvent.LONGITUDE,
		}, LogEvent.USERID + "=" + userid, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			LogEvent log = new LogEvent();
			log.userid = cursor.getString(0);
			log.taskid = cursor.getString(1);
			log.datetime = cursor.getString(2);
			log.description = cursor.getString(3);
			log.latitude = cursor.getString(4);
			log.longitude = cursor.getString(5);

			lstTasks.add(log);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstTasks;
	}
	public ArrayList<String> getProductos_CUS(String RutaAbastecimiento, String Taskbusinesskey, String tasktype){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		ArrayList<String> lstCUS = new ArrayList<String>();
		Cursor cursor = db.query(Producto_RutaAbastecimento.TABLENAME, new String[] {
				Producto_RutaAbastecimento.TASKTYPE,
				Producto_RutaAbastecimento.TASKBUSINESSKEY,
				Producto_RutaAbastecimento.RUTAABASTECIMENTO,
				Producto_RutaAbastecimento.CUS,
		}, Producto_RutaAbastecimento.RUTAABASTECIMENTO + "=" + "'" + RutaAbastecimiento + "'" + " AND " + Producto_RutaAbastecimento.TASKBUSINESSKEY + "=" + "'" + Taskbusinesskey + "'" + " AND " + Producto_RutaAbastecimento.TASKTYPE + "=" + "'" + tasktype + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String cus = "";
			cus = cursor.getString(3);
			lstCUS.add(cus);
			cursor.moveToNext();
		}
		cursor.close();
		//db.close();
		return lstCUS;
	}
	public void deleteLogEvent(String userid, String dateTime) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(LogEvent.TABLENAME, LogEvent.USERID + "=" + "'" + userid + "'" + " AND " + LogEvent.DATETIME + "=" + "'" + dateTime + "'", null);
		//db.close();
	}
	public void deletePendingTask(String userid, int taskid) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(PendingTasks.TABLENAME, PendingTasks.USERID + "=" + userid + " AND " + PendingTasks.TASKID + "=" + taskid, null);
		//db.close();		
	}
	public void deleteInCompleteTask(String userid, int taskid) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(TaskInfo.TABLENAME, TaskInfo.USERID + "=" + userid + " AND " + TaskInfo.TASKID + "=" + taskid, null);
		//db.close();
	}
	public void deletePendingTinTask(String userid, int taskid) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(TinTask.TABLENAME, TinTask.USERID + "=" + userid + " AND " + TinTask.TASKID + "=" + taskid, null);
		//db.close();
	}
	public void deleteAllIncompleteTask(String userid) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(TaskInfo.TABLENAME, TaskInfo.USERID + "=" + userid, null);
		//db.close();
	}

	public void deleteAllCompleteTask(String userid) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(CompleteTask.TABLENAME, CompleteTask.USERID + "=" + userid, null);
		//db.close();
	}
	public void deleteAllCompleteTinTask(String userid) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(CompltedTinTask.TABLENAME, CompltedTinTask.USERID + "=" + userid, null);
		//db.close();
	}
	public void deleteAllProducto() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(Producto.TABLENAME, null, null);
		//db.close();
	}
	public void deleteAllProducto_Ruta() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(Producto_RutaAbastecimento.TABLENAME, null, null);
		//db.close();
	}
	public void deleteAllCategory() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(Category.TABLENAME, null, null);
		//db.close();
	}
	public void deleteAllTypes() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(TaskType.TABLENAME, null, null);
		//db.close();
	}
	public void deleteAllUser() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(User.TABLENAME, null, null);
		//db.close();
	}
}
