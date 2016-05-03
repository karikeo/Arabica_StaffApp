package com.shevchenko.staffapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapsInitializer;
import com.shevchenko.staffapp.Common.Common;
import com.shevchenko.staffapp.Model.CompleteTask;
import com.shevchenko.staffapp.Model.CompltedTinTask;
import com.shevchenko.staffapp.Model.GpsInfo;
import com.shevchenko.staffapp.Model.LocationLoader;
import com.shevchenko.staffapp.Model.PendingTasks;
import com.shevchenko.staffapp.Model.Producto;
import com.shevchenko.staffapp.Model.TaskInfo;
import com.shevchenko.staffapp.Model.TinTask;
import com.shevchenko.staffapp.db.DBManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class AbastecTinTaskActivity extends Activity implements View.OnClickListener {

    private LinearLayout lnContainer;
    private ProgressDialog mProgDlg;
    private DBManager dbManager;
    private ComponentName mService;
    private int nTaskID;
    private String latitude, longitude;
    private ArrayList<Producto> currentProductos = new ArrayList<Producto>();
    LocationLoader mLocationLoader;
    private Location mNewLocation;
    private RelativeLayout RnButtons;
    private Boolean isEnter = false;
    private TaskInfo mTaskInfo;
    //private final int DYNAMIC_EDIT_ID = 0x8000;
////////////2016--04-26 changes///////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abastectintask);
        Common.getInstance().signaturePath="";
        //Common.getInstance().arrAbastTinTasks.clear();
        nTaskID = getIntent().getIntExtra("taskid", 0);
        for (int i = 0; i < Common.getInstance().arrIncompleteTasks.size(); i++) {
            mTaskInfo = Common.getInstance().arrIncompleteTasks.get(i);
            if (mTaskInfo.getTaskID() == nTaskID) {
                String actiondate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                latitude = mTaskInfo.getLatitude();
                longitude = mTaskInfo.getLongitude();
                break;
            }
        }
        findViewById(R.id.btnSendForm).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
        lnContainer = (LinearLayout) findViewById(R.id.lnContainer);
        lnContainer.requestFocus();

        RnButtons = (RelativeLayout) findViewById(R.id.RnButtons);
        RnButtons.setVisibility(View.VISIBLE);

        mProgDlg = new ProgressDialog(this);
        mProgDlg.setCancelable(false);
        mProgDlg.setTitle("Posting Task!");
        mProgDlg.setMessage("Please Wait!");

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
        dbManager = new DBManager(this);

        new Thread(mRunnable_producto).start();

    }
    private void getLocation() {
        if (mNewLocation == null)
            return;
        Common.getInstance().latitude = String.valueOf(mNewLocation.getLatitude());
        Common.getInstance().longitude = String.valueOf(mNewLocation.getLongitude());
    }
    private Runnable mRunnable_producto = new Runnable() {

        @Override
        public void run() {
            //int nRet = NetworkManager.getManager().loadProducto(Common.getInstance().arrProducto, mRutaAbastecimiento, mTaskbusinesskey, tasktype);
            currentProductos.clear();
            ArrayList<String> lstCus = new ArrayList<String>();
            //lstCus = dbManager.getProductos_CUS(mTaskInfo.RutaAbastecimiento, mTaskInfo.TaskBusinessKey, mTaskInfo.taskType);
            lstCus = dbManager.getProductos_CUS(mTaskInfo.RutaAbastecimiento, mTaskInfo.MachineType, mTaskInfo.taskType);
            for(int i = 0;  i < Common.getInstance().arrProducto.size(); i++){
                for(int j = 0; j < lstCus.size(); j++){
                    if(Common.getInstance().arrProducto.get(i).cus.equals(lstCus.get(j))){
                        currentProductos.add(Common.getInstance().arrProducto.get(i));
                        break;
                    }
                }
            }
            mHandler_task.sendEmptyMessage(1);
        }
    };
    private Handler mHandler_task = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            mProgDlg.hide();
            if (msg.what == 1) {
                for (int i = 0; i < currentProductos.size(); i++) {
                    LinearLayout lnChild = new LinearLayout(AbastecTinTaskActivity.this);
                    final int a = i;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    params.leftMargin = (int) getResources().getDimension(R.dimen.space_10);
                    params.rightMargin = (int) getResources().getDimension(R.dimen.space_10);
                    params.topMargin = (int) getResources().getDimension(R.dimen.space_5);
                    lnChild.setLayoutParams(params);
                    lnChild.setOrientation(LinearLayout.HORIZONTAL);
                    lnContainer.addView(lnChild, i);

                    TextView txtContent = new TextView(AbastecTinTaskActivity.this);
                    LinearLayout.LayoutParams param_text = new LinearLayout.LayoutParams(0, (int) getResources().getDimension(R.dimen.space_40));
                    param_text.weight = 80;
                    param_text.gravity = Gravity.CENTER;
                    txtContent.setText(currentProductos.get(i).cus + "-" + currentProductos.get(i).nus + ":");
                    txtContent.setLayoutParams(param_text);
                    //txtContent.setTextSize((float) getResources().getDimension(R.dimen.space_15));
                    txtContent.setTextColor(getResources().getColor(R.color.clr_graqy));
                    lnChild.addView(txtContent);

                    final EditText edtContent = new EditText(AbastecTinTaskActivity.this);
                    LinearLayout.LayoutParams param_edt = new LinearLayout.LayoutParams(0, (int) getResources().getDimension(R.dimen.space_35));
                    param_edt.weight = 20;
                    param_edt.gravity = Gravity.CENTER;
                    param_edt.leftMargin = (int) getResources().getDimension(R.dimen.space_3);
                    edtContent.setLayoutParams(param_edt);
                    ///edtContent.setTextSize((float) getResources().getDimension(R.dimen.space_15));
                    edtContent.setTextColor(getResources().getColor(R.color.clr_edit));
                    //edtContent.setId(DYNAMIC_EDIT_ID + i + 1);
                    edtContent.setId(i + 1);
                    if( i == 0) {
                        edtContent.requestFocus();
                        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(edtContent.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    edtContent.setBackgroundResource(R.drawable.back_edit);
                    //edtContent.setFilters(new InputFilter[]{filterNum});
                    edtContent.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    //edtContent.setText("0");
                    edtContent.setHint("0");
                    lnChild.addView(edtContent);
                }
                if(Common.getInstance().arrAbastTinTasks.size() != 0){
                    for(int i = 0; i < Common.getInstance().arrAbastTinTasks.size(); i++){
                        EditText edtContent = (EditText) findViewById(i + 1);
                        edtContent.setText(Common.getInstance().arrAbastTinTasks.get(i).quantity);
                    }
                }
            } else if (msg.what == 0) {
                Toast.makeText(AbastecTinTaskActivity.this, "Load failed!", Toast.LENGTH_SHORT).show();
            } else if (msg.what == -1) {
                Toast.makeText(AbastecTinTaskActivity.this, "Load failed due to network problem! Please check your network status", Toast.LENGTH_SHORT).show();
            }
            //setTaskNumber();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected InputFilter filterNum = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        if(!Common.getInstance().signaturePath.equals(""))
            findViewById(R.id.btn_signature).setBackgroundColor(Color.GREEN);
    }

    private void addPendingTask() {

        TaskInfo taskInfo;
        for (int i = 0; i < Common.getInstance().arrIncompleteTasks.size(); i++) {
            taskInfo = Common.getInstance().arrIncompleteTasks.get(i);
            if (taskInfo.getTaskID() == nTaskID) {
                Common.getInstance().arrAbastTinTasks.clear();
                for (int j = 0; j < currentProductos.size(); j++) {
                    EditText edtContent = (EditText) findViewById(j + 1);
                    String quantity = String.valueOf(edtContent.getText().toString());
                    if(quantity.equals(""))
                        quantity = "0";
                    TinTask tinInfo = new TinTask(Common.getInstance().getUserID(), nTaskID, taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), currentProductos.get(j).cus, currentProductos.get(j).nus, quantity);
                    Common.getInstance().arrAbastTinTasks.add(tinInfo);
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent;
        switch (v.getId()) {
            case R.id.btnSendForm:
                setService("The user clicks the Send Form Button");
                addPendingTask();
                onBackPressed();
                break;
            case R.id.btnBack:
                setService("The user clicks the Volver Button");
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            Intent i = new Intent();
            i.setComponent(mService);
            stopService(i);
        }
    }
    public void setService(String description) {

        Intent service = new Intent(AbastecTinTaskActivity.this, LogService.class);
        service.putExtra("userid", Common.getInstance().getUserID());
        service.putExtra("taskid", String.valueOf(nTaskID));
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        service.putExtra("datetime", time);
        service.putExtra("description", description);
        service.putExtra("latitude", Common.getInstance().latitude);
        service.putExtra("longitude", Common.getInstance().longitude);
        mService = startService(service);
    }
}
