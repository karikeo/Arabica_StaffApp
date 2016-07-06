package com.shevchenko.staffapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapsInitializer;
import com.shevchenko.staffapp.Common.Common;
import com.shevchenko.staffapp.Model.LocationLoader;
import com.shevchenko.staffapp.Model.Producto;
import com.shevchenko.staffapp.Model.TaskInfo;
import com.shevchenko.staffapp.Model.TinTask;
import com.shevchenko.staffapp.db.DBManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Pattern;

public class AbastecTinTaskEditActivity extends Activity implements View.OnClickListener {

    private ComponentName mService;
    private int nTaskID;
    private ArrayList<Producto> currentProductos = new ArrayList<Producto>();
    LocationLoader mLocationLoader;
    private Location mNewLocation;
    private RelativeLayout RnButtons;
    private Boolean isEnter = false;
    private TextView txtNus;
    private EditText edtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abastectinedit);
        txtNus = (TextView)findViewById(R.id.txtNus);
        txtNus.setText(getIntent().getStringExtra("nus"));
        edtContent = (EditText)findViewById(R.id.edtContent);
        if(getIntent().getStringExtra("quantity").equals("0"))
            edtContent.setText("");
        else
            edtContent.setText(getIntent().getStringExtra("quantity"));
        edtContent.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        findViewById(R.id.btnSendForm).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);

        Common.getInstance().selectedNus = "";
        Common.getInstance().selectedQuantity = "";

        RnButtons = (RelativeLayout) findViewById(R.id.RnButtons);
        RnButtons.setVisibility(View.VISIBLE);
    }
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
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent;
        switch (v.getId()) {
            case R.id.btnSendForm:
                setService("The user clicks the Send Form Button");
                Common.getInstance().selectedQuantity = edtContent.getText().toString();
                Common.getInstance().selectedNus = txtNus.getText().toString();
                //addPendingTask();
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

        Intent service = new Intent(AbastecTinTaskEditActivity.this, LogService.class);
        service.putExtra("userid", Common.getInstance().getLoginUser().getUserId());
        service.putExtra("taskid", String.valueOf(nTaskID));
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        service.putExtra("datetime", time);
        service.putExtra("description", description);
        service.putExtra("latitude", Common.getInstance().latitude);
        service.putExtra("longitude", Common.getInstance().longitude);
        mService = startService(service);
    }
}
