package com.shevchenko.staffapp;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapsInitializer;
import com.shevchenko.staffapp.Common.Common;
import com.shevchenko.staffapp.Model.CompleteDetailCounter;
import com.shevchenko.staffapp.Model.CompleteTask;
import com.shevchenko.staffapp.Model.CompltedTinTask;
import com.shevchenko.staffapp.Model.DetailCounter;
import com.shevchenko.staffapp.Model.GpsInfo;
import com.shevchenko.staffapp.Model.LocationLoader;
import com.shevchenko.staffapp.Model.LogFile;
import com.shevchenko.staffapp.Model.MachineCounter;
import com.shevchenko.staffapp.Model.MenuItemButton;
import com.shevchenko.staffapp.Model.MenuListAdapter;
import com.shevchenko.staffapp.Model.PendingTasks;
import com.shevchenko.staffapp.Model.Producto;
import com.shevchenko.staffapp.Model.TaskInfo;
import com.shevchenko.staffapp.Model.TinTask;
import com.shevchenko.staffapp.connectivity.AuditManagerJofemarRD;
import com.shevchenko.staffapp.db.DBManager;
import com.shevchenko.staffapp.net.NetworkManager;
import com.shevchenko.staffapp.viewholder.CaptureViewHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Pattern;

public class ReportActivity extends Activity implements View.OnClickListener {

    TextView txtTaskCount, txtCompleteTaskCount, txtPendingTaskCount, txtAbastecimiento, txtRecaudacion, txtTotalQuantity, txtNus1, txtNus2, txtNus3, txtNus4, txtNus5, txtNus6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        txtTaskCount = (TextView)findViewById(R.id.txtTaskCount);
        int taskCount = Common.getInstance().arrIncompleteTasks.size() + Common.getInstance().arrCompleteTasks.size();
        txtTaskCount.setText(String.valueOf(taskCount));

        txtCompleteTaskCount = (TextView)findViewById(R.id.txtCompleteTaskCount);
        txtCompleteTaskCount.setText(String.valueOf(Common.getInstance().arrCompleteTasks.size()));

        txtPendingTaskCount = (TextView)findViewById(R.id.txtPendingTaskCount);
        txtPendingTaskCount.setText(String.valueOf(Common.getInstance().arrIncompleteTasks.size()));

        txtAbastecimiento = (TextView)findViewById(R.id.txtAbastecimiento);
        txtAbastecimiento.setText("");
        txtRecaudacion = (TextView)findViewById(R.id.txtRecaudacion);
        txtRecaudacion.setText("");
        txtTotalQuantity = (TextView)findViewById(R.id.txtTotalQuantity);
        txtTotalQuantity.setText("");

        txtNus1 = (TextView)findViewById(R.id.txtNus1);
        txtNus1.setText("");
        txtNus2 = (TextView)findViewById(R.id.txtNus2);
        txtNus2.setText("");
        txtNus3 = (TextView)findViewById(R.id.txtNus3);
        txtNus3.setText("");
        txtNus4 = (TextView)findViewById(R.id.txtNus4);
        txtNus4.setText("");
        txtNus5 = (TextView)findViewById(R.id.txtNus5);
        txtNus5.setText("");
        txtNus6 = (TextView)findViewById(R.id.txtNus6);
        txtNus6.setText("");
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        }
    }
}
