package com.shevchenko.staffapp.viewholder;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shevchenko.staffapp.Model.TaskInfo;
import com.shevchenko.staffapp.R;
import com.shevchenko.staffapp.connectivity.AuditManagerBase;
import com.shevchenko.staffapp.connectivity.AuditManagerDDCMP;
import com.shevchenko.staffapp.connectivity.AuditManagerDex;
import com.shevchenko.staffapp.connectivity.AuditManagerJofemar;
import com.shevchenko.staffapp.connectivity.AuditManagerSpengler;
import com.shevchenko.staffapp.connectivity.IAuditManager;

import java.util.ArrayList;
import java.util.List;

public class CaptureViewHolder implements IAuditManager {
    public static final String BT_DEVICE = "BTDevice";

    public static final String SPENGLER = "Spengler";
    public static final String DEX = "DEX";
    public static final String JOFEMAR = "Jofemar";
    public static final String DDCMP = "DDCMP";

    private final Activity mContext;

    private final ListView mDeviceList;
    private final ListView mPairingList;
    private final ListView mTypeList;
    private final View mDeviceListLayout;
    private final View mPairingListLayout;
    private final View mTypeListLayout;
    private final TextView mDeviceTitle;
    private final TextView mPairingTitle;
    private final TextView mTypeTitle;
    private final View mPairingLoading;

    private AuditManagerBase mAuditManager;
    private BluetoothDevice mDevice;
    private String mType;

    public CaptureViewHolder(Activity activity, View view, final TaskInfo taskInfo) {
        mContext = activity;

        mDeviceTitle = (TextView) view.findViewById(R.id.device);
        mPairingTitle = (TextView) view.findViewById(R.id.pairing);
        mTypeTitle = (TextView) view.findViewById(R.id.type);
        mDeviceList = (ListView) view.findViewById(R.id.device_list);
        mDeviceListLayout = view.findViewById(R.id.device_list_layout);
        mPairingListLayout = view.findViewById(R.id.pairing_list_layout);
        mPairingList = (ListView) view.findViewById(R.id.pairing_list);
        mTypeList = (ListView) view.findViewById(R.id.type_list);
        mTypeListLayout = view.findViewById(R.id.type_list_layout);
        mPairingLoading = view.findViewById(R.id.pairing_loading);

        mType = taskInfo.getAux_valor1();

    }

    public void start() {
        selectDevice();
    }

    private void selectDevice() {
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(mContext, "You don't have bluetooth :(", Toast.LENGTH_LONG).show();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()){
            Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivityForResult(intentBtEnabled, 1);
        } else {
            final List<BluetoothDevice> pairedDevices = new ArrayList<>(mBluetoothAdapter.getBondedDevices());
            restoreFromPrefs(pairedDevices);

            if (mDevice == null) {
                mDeviceListLayout.setVisibility(View.VISIBLE);
                ArrayAdapter<BluetoothDevice> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, pairedDevices);
                mDeviceList.setAdapter(adapter);
                mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        mDevice = (BluetoothDevice) parent.getAdapter().getItem(position);
                        saveToPrefs();
                        mDeviceListLayout.setVisibility(View.GONE);
                        setDone(mDeviceTitle);
                        selectType();
                    }
                });
            } else {
                setDone(mDeviceTitle);
                selectType();
            }
        }
    }

    private void selectType() {
        if (mType.isEmpty()) {
            mTypeListLayout.setVisibility(View.VISIBLE);
            String[] types = {"Spengler", "DEX", "DDCMP", "Jofemar"};
            ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, types);
            mTypeList.setAdapter(typeAdapter);
            mTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mType = (String) parent.getAdapter().getItem(position);
                    mTypeListLayout.setVisibility(View.GONE);
                    setDone(mTypeTitle);
                    startAudit();
                }
            });
        } else {
            setDone(mTypeTitle);
            startAudit();
        }
    }

    private void createAuditManager() {
        switch (mType) {
            case SPENGLER:
                mAuditManager = new AuditManagerSpengler(CaptureViewHolder.this);
                break;
            case DEX:
                mAuditManager = new AuditManagerDex(CaptureViewHolder.this);
                break;
            case DDCMP:
                mAuditManager = new AuditManagerDDCMP(CaptureViewHolder.this);
                break;
            case JOFEMAR:
                mAuditManager = new AuditManagerJofemar(CaptureViewHolder.this);
                break;
        }
    }

    private void startAudit() {
        createAuditManager();
        if (mAuditManager != null) {
            mAuditManager.setBTDevice(mDevice);
            mAuditManager.go(getCurrentBtType());
        }
    }

    private void restoreFromPrefs(List<BluetoothDevice> pairedDevices) {
        SharedPreferences editor = PreferenceManager.getDefaultSharedPreferences(mContext);
        String device = editor.getString(BT_DEVICE, "");

        if (!device.isEmpty()) {
            for (BluetoothDevice d : pairedDevices) {
                if (d.getAddress().equals(device)) {
                    mDevice = d;
                }
            }
        }
    }

    private void setDone(TextView view) {
        view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.clr_green));
        view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
    }

    private void saveToPrefs() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putString(BT_DEVICE, mDevice.getAddress());
        editor.apply();
    }

    private String getCurrentBtType() {
        return "SENA";
    }

    @Override
    public void onAuditStart() {
        mPairingLoading.setVisibility(View.VISIBLE);
        Log.d("AAA", "onAuditStart() called with: " + "");
    }

    @Override
    public void onError(String msg) {
        Log.d("AAA", "onError() called with: " + "msg = [" + msg + "]");
    }

    @Override
    public void onSuccess(List<String> filesList) {
        for (String msg : filesList) {
            Log.d("AAA", "onError() called with: " + "msg = [" + msg + "]");
        }
    }
    public void onAuditStop() {
        Log.d("AAA", "onAuditStop() called with: " + "");
    }

    @Override
    public void onAuditLog(String msg) {
        Log.d("AAA", "onAuditLog() called with: " + "msg = [" + msg + "]");
    }
}
