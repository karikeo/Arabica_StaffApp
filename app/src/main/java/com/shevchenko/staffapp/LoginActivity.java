package com.shevchenko.staffapp;

/**
 * Created by shevchenko on 2015-11-26.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.Task;
import com.google.android.gms.maps.MapsInitializer;
import com.shevchenko.staffapp.Common.Common;
import com.shevchenko.staffapp.Model.CompleteTask;
import com.shevchenko.staffapp.Model.LocationLoader;
import com.shevchenko.staffapp.Model.LogEvent;
import com.shevchenko.staffapp.Model.PendingTasks;
import com.shevchenko.staffapp.Model.TinTask;
import com.shevchenko.staffapp.Model.User;
import com.shevchenko.staffapp.Model.CompltedTinTask;
import com.shevchenko.staffapp.Model.TaskInfo;
import com.shevchenko.staffapp.db.DBManager;
import com.shevchenko.staffapp.net.NetworkManager;

import android.content.SharedPreferences;
import android.provider.Settings;
import java.util.ArrayList;

public class LoginActivity extends Activity implements View.OnClickListener {

    private ProgressDialog mProgDlg;
    private EditText txtID, txtPassword;
    private DBManager dbManager;
    public static Activity loginActivity;
    android.content.SharedPreferences.Editor ed;
    SharedPreferences sp;
    private String userid = "";
    private String password = "";
    LocationLoader mLocationLoader;
    private Location mNewLocation;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //////////////111
        setContentView(R.layout.activity_login);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        txtID = (EditText) findViewById(R.id.txtUserID);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        loginActivity = LoginActivity.this;

        MapsInitializer.initialize(getApplicationContext());
        mLocationLoader = new LocationLoader(this, false);
        mLocationLoader
                .SetOnLoadEventListener(new LocationLoader.OnLoadEventListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mNewLocation = location;
                        getLocation();
                        // mUpdateLocationHandler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onAddressChanged(String strAddress) {

                    }

                    @Override
                    public void onError(int iErrorCode) {
                        getLocation();
                    }
                });
        mLocationLoader.Start();

        dbManager = new DBManager(LoginActivity.this);
        Common.getInstance().arrIncompleteTasks.clear();
        Common.getInstance().arrCompleteTasks.clear();
        Common.getInstance().arrCategory.clear();
        Common.getInstance().arrProducto.clear();
        Common.getInstance().arrPendingTasks.clear();
        Common.getInstance().arrTaskTypes.clear();

        mProgDlg = new ProgressDialog(this);
        mProgDlg.setCancelable(false);
        mProgDlg.setMessage("Wait a little!");
        sp = getSharedPreferences("userinfo", 1);
        ed = sp.edit();

        if (sp.getBoolean("login", false)) {

            new Thread(mRunnable_offline).start();
        }
    }
    private Runnable mRunnable_offline = new Runnable() {

        @Override
        public void run() {
            startActivity(new Intent(LoginActivity.this, LoadingActivity.class));

            try{
                Thread.sleep(2000);
            }catch (Throwable a){

            }
            mHandler_offline.sendEmptyMessage(1);

        }
    };
    private Handler mHandler_offline = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            mProgDlg.hide();
            if (msg.what > 0) {
                //Toast.makeText(LoginActivity.this, "LoginSuccess!", Toast.LENGTH_LONG).show();
                userid = sp.getString("userid", "");
                password = sp.getString("password", "");
                User info = dbManager.getUser(userid);
                if (info != null) {
                    Common.getInstance().setUserID(userid);
                    Toast.makeText(LoginActivity.this, "Load Success!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("position", 0);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "You can`t login now!!!", Toast.LENGTH_SHORT).show();
                }

            } else if (msg.what == 0) {
                Toast.makeText(LoginActivity.this, "Login failed!Please check id and password", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, "Login failed due to netwrok problem", Toast.LENGTH_LONG).show();
            }
        }

    };
    private void getLocation() {
        if (mNewLocation == null) {
            //DialogSelectOption();
            return;
        }

        Common.getInstance().latitude = String.valueOf(mNewLocation.getLatitude());
        Common.getInstance().longitude = String.valueOf(mNewLocation.getLongitude());
    }
    private void DialogSelectOption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Upload the pending tasks.")
                .setMessage("Will you upload the pending tasks?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                        intent.putExtra("enabled", true);
                        sendBroadcast(intent);

                        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                        if(!provider.contains("gps")){ //if gps is disabled
                            final Intent poke = new Intent();
                            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                            poke.setData(Uri.parse("3"));
                            sendBroadcast(poke);


                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private Runnable mRunnable_auto = new Runnable() {

        @Override
        public void run() {
            startActivity(new Intent(LoginActivity.this, LoadingActivity.class));
            userid = sp.getString("userid", "");
            password = sp.getString("password", "");
            mHandler.sendEmptyMessage(NetworkManager.getManager().login(userid, password));

        }
    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.btnLogin) {
            if (checkField()) {
                //mProgDlg.show();
                new Thread(mRunnable).start();
            }

        }
    }

    private int postAllPendingTask() {
        ArrayList<PendingTasks> tasks = dbManager.getPendingTask(Common.getInstance().getUserID());
        int sum = 0;
        for (int i = 0; i < tasks.size(); i++) {
            String[] arrPhotos = new String[]{null, null, null, null, null};
            int nCurIndex = 0;
            if (tasks.get(i).file1 != null) {
                arrPhotos[nCurIndex] = tasks.get(i).file1;
                nCurIndex++;
            }
            if (tasks.get(i).file2 != null) {
                arrPhotos[nCurIndex] = tasks.get(i).file2;
                nCurIndex++;
            }
            if (tasks.get(i).file3 != null) {
                arrPhotos[nCurIndex] = tasks.get(i).file3;
                nCurIndex++;
            }
            if (tasks.get(i).file4 != null) {
                arrPhotos[nCurIndex] = tasks.get(i).file4;
                nCurIndex++;
            }
            if (tasks.get(i).file5 != null) {
                arrPhotos[nCurIndex] = tasks.get(i).file5;
                nCurIndex++;
            }
            Boolean bRet1 = NetworkManager.getManager().postTask(tasks.get(i).taskid, tasks.get(i).date, tasks.get(i).tasktype, tasks.get(i).RutaAbastecimiento, tasks.get(i).TaskBusinessKey, tasks.get(i).Customer, tasks.get(i).Adress, tasks.get(i).LocationDesc, tasks.get(i).Model, tasks.get(i).latitude, tasks.get(i).longitude, tasks.get(i).epv, tasks.get(i).logLatitude, tasks.get(i).logLongitude, tasks.get(i).ActionDate, tasks.get(i).MachineType, tasks.get(i).Signature, tasks.get(i).NumeroGuia, tasks.get(i).Aux_valor1, tasks.get(i).Glosa, arrPhotos, nCurIndex);
            if (!bRet1)
                return 0;
            dbManager.deletePendingTask(Common.getInstance().getUserID(), tasks.get(i).taskid);
        }
        return 1;
    }

    private int postAllLogEvents() {
        ArrayList<LogEvent> logs = dbManager.getLogEvents(Common.getInstance().getUserID());
        int sum = 0;
        for (int i = 0; i < logs.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postLogEvent(logs.get(i));
            if (bRet1)
                dbManager.deleteLogEvent(Common.getInstance().getUserID(), logs.get(i).datetime);
            else
                return 0;
        }
        return 1;
    }

    private int postAllTinPendingTask() {
        ArrayList<TinTask> tasks = dbManager.getTinPendingTask(Common.getInstance().getUserID());
        int sum = 0;
        for (int i = 0; i < tasks.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postTinTask(tasks.get(i));
            if (bRet1)
                dbManager.deletePendingTinTask(Common.getInstance().getUserID(), tasks.get(i).taskid);
            else
                return 0;
        }
        return 1;
    }

    private void postPendingTask() {
        mProgDlg.show();
        new Thread(mRunnable_pendingtasks).start();

    }

    private Runnable mRunnable_pendingtasks = new Runnable() {
        @Override
        public void run() {
            postAllPendingTask();
            postAllTinPendingTask();
            postAllLogEvents();
            mHandler_pendingtasks.sendEmptyMessage(1);

        }
    };
    private Handler mHandler_pendingtasks = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            mProgDlg.hide();
            if (msg.what == 1) {
                Toast.makeText(LoginActivity.this, "Pending Tasks was uploaded!!!", Toast.LENGTH_SHORT).show();
                loadTasks();
            } else if (msg.what == 0) {
                Toast.makeText(LoginActivity.this, "Pending Tasks was failed to upload!!!", Toast.LENGTH_SHORT).show();
                loadTasks();
            }
        }
    };

    @Override
    public void onBackPressed() {
        //LoginActivity.this.finish();
        moveTaskToBack(true);
        System.exit(0);
    }

    private void loadTasks() {
        mProgDlg.show();
        new Thread(mRunnable_tasks).start();
    }

    private Runnable mRunnable_tasks = new Runnable() {

        @Override
        public void run() {

            int nRet = NetworkManager.getManager().loadTasks(Common.getInstance().arrIncompleteTasks, Common.getInstance().arrCompleteTasks, Common.getInstance().arrCompleteTinTasks);
            NetworkManager.getManager().loadCategory(Common.getInstance().arrCategory, Common.getInstance().arrProducto, Common.getInstance().arrProducto_Ruta, Common.getInstance().arrUsers, Common.getInstance().arrTaskTypes);
            dbManager.deleteAllIncompleteTask(Common.getInstance().getUserID());
            dbManager.deleteAllCompleteTask(Common.getInstance().getUserID());
            dbManager.deleteAllCompleteTinTask(Common.getInstance().getUserID());
            dbManager.deleteAllProducto();
            dbManager.deleteAllProducto_Ruta();
            dbManager.deleteAllCategory();
            dbManager.deleteAllUser();
            dbManager.deleteAllTypes();

            for (int i = 0; i < Common.getInstance().arrIncompleteTasks.size(); i++) {
                dbManager.insertInCompleteTask(Common.getInstance().arrIncompleteTasks.get(i));
            }
            for (int i = 0; i < Common.getInstance().arrCompleteTasks.size(); i++) {
                dbManager.insertCompleteTask(Common.getInstance().arrCompleteTasks.get(i));
            }
            for (int i = 0; i < Common.getInstance().arrCompleteTinTasks.size(); i++) {
                dbManager.insertCompleteTinTask(Common.getInstance().arrCompleteTinTasks.get(i));
            }
            for (int i = 0; i < Common.getInstance().arrCategory.size(); i++) {
                dbManager.insertCategory(Common.getInstance().arrCategory.get(i));
            }
            for (int i = 0; i < Common.getInstance().arrProducto.size(); i++) {
                dbManager.insertProducto(Common.getInstance().arrProducto.get(i));
            }
            for (int i = 0; i < Common.getInstance().arrProducto_Ruta.size(); i++) {
                dbManager.insertProducto_Ruta(Common.getInstance().arrProducto_Ruta.get(i));
            }
            for (int i = 0; i < Common.getInstance().arrUsers.size(); i++) {
                dbManager.insertUser(Common.getInstance().arrUsers.get(i));
            }
            for(int i = 0; i < Common.getInstance().arrTaskTypes.size(); i++){
                dbManager.insertType(Common.getInstance().arrTaskTypes.get(i));
            }
            //NetworkManager.getManager().loadProducto(Common.getInstance().arrProducto);
            Common.getInstance().arrPendingTasks = dbManager.getPendingTask(Common.getInstance().getUserID());
            Common.getInstance().arrTinTasks = dbManager.getTinPendingTask(Common.getInstance().getUserID());
            mHandler_task.sendEmptyMessage(nRet);
        }
    };
    private Handler mHandler_task = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            mProgDlg.hide();
            if (msg.what == 0) {
                Toast.makeText(LoginActivity.this, "Load Success!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("position", 0);
                startActivity(intent);
            } else if (msg.what == 1) {
                Toast.makeText(LoginActivity.this, "Load failed!", Toast.LENGTH_SHORT).show();
            } else if (msg.what == -1) {
                Toast.makeText(LoginActivity.this, "Load failed due to network problem! Please check your network status", Toast.LENGTH_SHORT).show();
            }
            //setTaskNumber();
        }
    };
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            mProgDlg.hide();
            if (msg.what > 0) {
                //Toast.makeText(LoginActivity.this, "LoginSuccess!", Toast.LENGTH_LONG).show();
                ed.putBoolean("login", true);
                ed.putString("userid", userid);
                ed.putString("password", password);
                ed.commit();
                Common.getInstance().setUserID(userid);
                postPendingTask();
                //loadTasks();

            } else if (msg.what == 0) {
                Toast.makeText(LoginActivity.this, "Login failed!Please check id and password", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, "Login failed due to netwrok problem", Toast.LENGTH_LONG).show();
            }
        }

    };
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            userid = txtID.getText().toString();
            password = txtPassword.getText().toString();
            startActivity(new Intent(LoginActivity.this, LoadingActivity.class));
            mHandler.sendEmptyMessage(NetworkManager.getManager().login(userid, password));

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean checkField() {
        EditText txtId = (EditText) findViewById(R.id.txtUserID);
        EditText txtPwd = (EditText) findViewById(R.id.txtPassword);
        if (txtId.getText().toString().isEmpty() || txtPwd.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.insert_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean getConnectivityStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }
}
