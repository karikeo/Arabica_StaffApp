package com.shevchenko.staffapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapsInitializer;
import com.shevchenko.staffapp.Common.Common;
import com.shevchenko.staffapp.Model.*;
import com.shevchenko.staffapp.db.DBManager;
import com.shevchenko.staffapp.viewholder.CaptureViewHolder;

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

public class AbaTaskActivity extends Activity implements View.OnClickListener {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private Uri mImageCaptureUri = null;
    private int nCurIndex = 0;
    private LinearLayout lnImages;
    private ProgressDialog mProgDlg;
    private TextView txtCustomer, txtSecond, txtMachine;
    private String[] mArrPhotos;
    private DBManager dbManager;
    private ComponentName mService;
    private int nTaskID;
    private String date;
    private String tasktype;
    private String latitude, longitude;
    private ImageView imgWaze;
    private int cnt = 0;
    private String mRutaAbastecimiento = "";
    private String mTaskbusinesskey = "";
    private String mMachineType = "";
    private ArrayList<Producto> currentProductos = new ArrayList<Producto>();
    private String strFileName = "";
    LocationLoader mLocationLoader;
    private Location mNewLocation;
    private Button btnPhoto, btnAbastec, btnCapturar;
    private View captureLayout;
    private TaskInfo currentTask;
    private CaptureViewHolder captureViewHolder;
////////////2016--04-26 changes///////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abatask);

        Common.getInstance().arrAbastTinTasks.clear();
        nTaskID = getIntent().getIntExtra("taskid", 0);
        date = getIntent().getStringExtra("date");
        tasktype = getIntent().getStringExtra("tasktype");
        mRutaAbastecimiento = getIntent().getStringExtra("RutaAbastecimiento");
        mTaskbusinesskey = getIntent().getStringExtra("Taskbusinesskey");
        mMachineType = getIntent().getStringExtra("MachineType");
        btnPhoto = (Button) findViewById(R.id.btnTomar);
        btnPhoto.setOnClickListener(this);
        btnAbastec = (Button) findViewById(R.id.btnAbastec);
        btnAbastec.setOnClickListener(this);
        btnCapturar = (Button) findViewById(R.id.btnCapture);
        btnCapturar.setOnClickListener(this);

        txtCustomer = (TextView) findViewById(R.id.txtCustomer);
        txtSecond = (TextView) findViewById(R.id.txtSecond);
        txtMachine = (TextView) findViewById(R.id.txtMachine);
        imgWaze = (ImageView) findViewById(R.id.waze);
        imgWaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //String url = "waze://?q=Hawaii";
                    String url = "waze://?ll=" + latitude + "," + longitude + "&navigate=yes";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent =
                            new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.btnSendForm).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);

        lnImages = (LinearLayout) findViewById(R.id.lnImages);

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

        mArrPhotos = new String[]{"", "", "", "", ""};
        dbManager = new DBManager(this);
        setTitleAndSummary();
        captureLayout = findViewById(R.id.capture_layout);
        captureViewHolder = new CaptureViewHolder(this, captureLayout, currentTask, dbManager);
        invalidateCaptureButton();

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
            currentProductos.clear();
            ArrayList<String> lstCus = new ArrayList<String>();
            //lstCus = dbManager.getProductos_CUS(mRutaAbastecimiento, mTaskbusinesskey, tasktype);
            lstCus = dbManager.getProductos_CUS(mRutaAbastecimiento, mMachineType, tasktype);
            for (int i = 0; i < Common.getInstance().arrProducto.size(); i++) {
                for (int j = 0; j < lstCus.size(); j++) {
                    if (Common.getInstance().arrProducto.get(i).cus.equals(lstCus.get(j))) {
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
                ArrayList<String> arrNus = new ArrayList<String>();
                for (int i = 0; i < currentProductos.size(); i++)
                    arrNus.add(currentProductos.get(i).nus);
                ArrayAdapter<String> adapterSelect = new ArrayAdapter<String>(AbaTaskActivity.this, android.R.layout.simple_spinner_dropdown_item, arrNus);
            } else if (msg.what == 0) {
                Toast.makeText(AbaTaskActivity.this, "Load failed!", Toast.LENGTH_SHORT).show();
            } else if (msg.what == -1) {
                Toast.makeText(AbaTaskActivity.this, "Load failed due to network problem! Please check your network status", Toast.LENGTH_SHORT).show();
            }
            //setTaskNumber();
        }
    };

    private void setTitleAndSummary() {
        TaskInfo taskInfo;
        for (int i = 0; i < Common.getInstance().arrIncompleteTasks.size(); i++) {
            taskInfo = Common.getInstance().arrIncompleteTasks.get(i);
            if (taskInfo.getTaskID() == nTaskID) {
                currentTask = taskInfo;
                txtCustomer.setText(taskInfo.getCustomer());
                txtSecond.setText(taskInfo.getAdress() + ", " + taskInfo.getLocationDesc());
                txtMachine.setText(taskInfo.getTaskBusinessKey() + ", " + taskInfo.getModel() + ", " + taskInfo.getMachineType());
                String actiondate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                //txtDate.setText("Fecha: " + actiondate);
                latitude = taskInfo.getLatitude();
                longitude = taskInfo.getLongitude();
                break;
            }
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureViewHolder.BT_REQUEST_CODE) {
            captureViewHolder.start();
            return;
        }
        if (resultCode != RESULT_OK) {
            return;
        }
        final String strFilePath =  Environment.getExternalStorageDirectory() + "/staffapp/"+ strFileName;
        String strFilePath1 =  Environment.getExternalStorageDirectory() + "/staffapp/"+ "strFileName.jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(strFilePath, options);
        try {
            FileOutputStream fos = new FileOutputStream(strFilePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        }catch (Exception e){

        }
        /*
        File imgFile = new File(strFilePath);
        if (nCurIndex == 0)
            lnImages.setVisibility(View.VISIBLE);
        if (imgFile.exists()) {
            ImageView imgPhoto = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.space_80), (int) getResources().getDimension(R.dimen.space_80));
            params.leftMargin = (int) getResources().getDimension(R.dimen.space_10);
            params.gravity = Gravity.CENTER_VERTICAL;

            Bitmap myBitmap = loadLargeBitmapFromFile(imgFile.getAbsolutePath(), this);
            imgPhoto.setImageBitmap(myBitmap);
            imgPhoto.setLayoutParams(params);
            lnImages.addView(imgPhoto);
            mArrPhotos[nCurIndex] = strFilePath;
            nCurIndex++;
        }*/
        //final String strFilePath = Environment.getExternalStorageDirectory() + "/staffapp/" + strFileName;
        File imgFile = new File(strFilePath);
        if (nCurIndex == 0)
            lnImages.setVisibility(View.VISIBLE);
        if (imgFile.exists()) {
            ImageView imgPhoto = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.space_50), (int) getResources().getDimension(R.dimen.space_50));
            params.leftMargin = (int) getResources().getDimension(R.dimen.space_10);
            params.gravity = Gravity.CENTER_VERTICAL;

            Bitmap myBitmap = loadLargeBitmapFromFile(imgFile.getAbsolutePath(), this);
            imgPhoto.setImageBitmap(myBitmap);
            imgPhoto.setLayoutParams(params);
            imgPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoDialog dlg = new PhotoDialog(AbaTaskActivity.this);
                    dlg.setImage(strFilePath);
                    dlg.show();
                }
            });
            lnImages.addView(imgPhoto);
            mArrPhotos[nCurIndex] = strFilePath;
            nCurIndex++;
            btnPhoto.setBackgroundColor(getResources().getColor(R.color.clr_button_on));

        }
    }

    private void addPendingTask() {

        TaskInfo taskInfo;
        for (int i = 0; i < Common.getInstance().arrIncompleteTasks.size(); i++) {
            taskInfo = Common.getInstance().arrIncompleteTasks.get(i);
            if (taskInfo.getTaskID() == nTaskID) {
                String actiondate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                PendingTasks task = new PendingTasks(Common.getInstance().getUserID(), nTaskID, taskInfo.getDate(), taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), taskInfo.getTaskBusinessKey(), taskInfo.getCustomer(), taskInfo.getAdress(), taskInfo.getLocationDesc(), taskInfo.getModel(), taskInfo.getLatitude(), taskInfo.getLongitude(), taskInfo.getepv(), Common.getInstance().latitude, Common.getInstance().longitude, actiondate, mArrPhotos[0], mArrPhotos[1], mArrPhotos[2], mArrPhotos[3], mArrPhotos[4], taskInfo.getMachineType(), Common.getInstance().signaturePath, "", "", taskInfo.getAux_valor1());
                dbManager.insertPendingTask(task);
                Common.getInstance().arrPendingTasks.add(task);

                CompleteTask comtask = new CompleteTask(Common.getInstance().getUserID(), nTaskID, taskInfo.getDate(), taskInfo.getTaskType(), taskInfo.getRutaAbastecimiento(), taskInfo.getTaskBusinessKey(), taskInfo.getCustomer(), taskInfo.getAdress(), taskInfo.getLocationDesc(), taskInfo.getModel(), taskInfo.getLatitude(), taskInfo.getLongitude(), taskInfo.getepv(), Common.getInstance().latitude, Common.getInstance().longitude, actiondate, mArrPhotos[0], mArrPhotos[1], mArrPhotos[2], mArrPhotos[3], mArrPhotos[4], taskInfo.getMachineType(), Common.getInstance().signaturePath, "", "", taskInfo.getAux_valor1());
                dbManager.insertCompleteTask(comtask);
                Common.getInstance().arrCompleteTasks.add(comtask);

                for (int j = 0; j < Common.getInstance().arrAbastTinTasks.size(); j++) {
                    //EditText edtContent = (EditText)findViewById(DYNAMIC_EDIT_ID + j);
                    TinTask tinInfo = new TinTask();
                    tinInfo = Common.getInstance().arrAbastTinTasks.get(j);///2016
                    dbManager.insertPendingTinTask(tinInfo);
                    Common.getInstance().arrTinTasks.add(tinInfo);

                    CompltedTinTask comtinInfo = new CompltedTinTask(Common.getInstance().arrAbastTinTasks.get(j).userid, Common.getInstance().arrAbastTinTasks.get(j).taskid, Common.getInstance().arrAbastTinTasks.get(j).tasktype, Common.getInstance().arrAbastTinTasks.get(j).RutaAbastecimiento, Common.getInstance().arrAbastTinTasks.get(j).cus, Common.getInstance().arrAbastTinTasks.get(j).nus, Common.getInstance().arrAbastTinTasks.get(j).quantity);
                    dbManager.insertCompleteTinTask(comtinInfo);
                    Common.getInstance().arrCompleteTinTasks.add(comtinInfo);
                }
                break;
            }
        }
        for (int i = 0; i < Common.getInstance().arrIncompleteTasks.size(); i++) {
            if (Common.getInstance().arrIncompleteTasks.get(i).getTaskID() == nTaskID) {
                dbManager.deleteInCompleteTask(Common.getInstance().getUserID(), nTaskID);
                Common.getInstance().arrIncompleteTasks.remove(i);
            }
        }
        Intent intentMain = new Intent(AbaTaskActivity.this, MainActivity.class);
        Common.getInstance().arrAbastTinTasks.clear();
        intentMain.putExtra("position", 0);
        startActivity(intentMain);
    }

    private void addProduct(int i) {
        LinearLayout lnChild = new LinearLayout(AbaTaskActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = (int) getResources().getDimension(R.dimen.space_15);
        params.rightMargin = (int) getResources().getDimension(R.dimen.space_15);
        params.topMargin = (int) getResources().getDimension(R.dimen.space_5);
        lnChild.setLayoutParams(params);
        lnChild.setOrientation(LinearLayout.HORIZONTAL);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent;
        switch (v.getId()) {
            case R.id.btnTomar:
                setService("The user takes some pictures.");
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mImageCaptureUri = createSaveCropFile();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
                break;
            case R.id.btnSendForm:
                if ((Common.getInstance().arrAbastTinTasks.size() == 0) || (mArrPhotos[0] == "")) {
                    Toast.makeText(AbaTaskActivity.this, "Please input the full informations.", Toast.LENGTH_SHORT).show();
                } else {
                    setService("The user clicks the Send Form Button");
                    addPendingTask();
                }
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnCapture:
                setCaptureMode(true);
                break;
            case R.id.btnAbastec:
                intent = new Intent(AbaTaskActivity.this, AbastecTinTaskActivity.class);
                intent.putExtra("taskid", nTaskID);
                startActivity(intent);
                break;
        }
    }

    private void setCaptureMode(boolean captureMode) {
        if (captureMode) {
            captureViewHolder.start();
        }
        captureLayout.setVisibility(captureMode ? View.VISIBLE : View.GONE);
        btnCapturar.setVisibility(captureMode ? View.GONE : View.VISIBLE);
        btnAbastec.setVisibility(captureMode ? View.GONE : View.VISIBLE);
        btnPhoto.setVisibility(captureMode ? View.GONE : View.VISIBLE);
        invalidateCaptureButton();
    }

    private void invalidateCaptureButton() {
        final ArrayList<LogFile> logs = dbManager.getLogs(nTaskID);
        btnCapturar.setBackgroundColor(ContextCompat.getColor(this, logs.isEmpty() ? R.color.clr_button_off : R.color.clr_green));
        btnCapturar.setEnabled(logs.isEmpty());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Common.getInstance().arrAbastTinTasks.size() != 0) {
            btnAbastec.setBackgroundColor(getResources().getColor(R.color.clr_button_on));
        }
    }

    @Override
    public void onBackPressed() {
        if (captureLayout.getVisibility() == View.VISIBLE) {
            setCaptureMode(false);
        } else {
            super.onBackPressed();
        }
    }

    private void DialogShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Please select the value.")
                .setMessage("Please select a product and quantity before add a new product.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void DialogExists() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Please select the value.")
                .setMessage("Please select the product again because you already added the product.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
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

        GpsInfo info = new GpsInfo(AbaTaskActivity.this);
        Intent service = new Intent(AbaTaskActivity.this, LogService.class);
        service.putExtra("userid", Common.getInstance().getUserID());
        service.putExtra("taskid", String.valueOf(nTaskID));
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        service.putExtra("datetime", time);
        service.putExtra("description", description);
        service.putExtra("latitude", Common.getInstance().latitude);
        service.putExtra("longitude", Common.getInstance().longitude);
        mService = startService(service);
    }

    private Bitmap loadLargeBitmapFromFile(String strPath, Context context) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strPath, opt);

        int REQUIRED_SIZE = 480;
        int width_tmp = opt.outWidth, height_tmp = opt.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options opt1 = new BitmapFactory.Options();
        opt1.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(strPath, opt1);
        return bitmap;

    }

    private Uri createSaveCropFile() {
        Uri uri;
        strFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + String.valueOf(nCurIndex) + ".jpg";
        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/staffapp", strFileName));
        return uri;
    }


    private File getImageFile(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(uri, projection, null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (mCursor == null || mCursor.getCount() < 1) {
            return null; // no cursor or no record
        }
        int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        return new File(path);
    }


    public boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    private boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
