package com.shevchenko.staffapp.viewholder;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
    public static final String SPENGLER = "Spengler";
    public static final String DEX = "DEX";
    public static final String JOFEMAR = "Jofemar";
    public static final String DDCMP = "DDCMP";

    private final Context mContext;

    private final ListView mDeviceList;
    private final ListView mPairingList;
    private final ListView mTypeList;
    private final View mDeviceListLayout;
    private final View mPairingListLayout;
    private final View mTypeListLayout;
    private final TextView mDeviceTitle;
    private final TextView mPairingTitle;
    private final TextView mTypeTitle;

    private AuditManagerBase mAuditManager;
    private BluetoothDevice mDevice;
    private String mType;

    public CaptureViewHolder(View view, final TaskInfo taskInfo) {
        mContext = view.getContext();

        mDeviceTitle = (TextView) view.findViewById(R.id.device);
        mPairingTitle = (TextView) view.findViewById(R.id.pairing);
        mTypeTitle = (TextView) view.findViewById(R.id.type);
        mDeviceList = (ListView) view.findViewById(R.id.device_list);
        mDeviceListLayout = view.findViewById(R.id.device_list_layout);
        mPairingListLayout = view.findViewById(R.id.pairing_list_layout);
        mPairingList = (ListView) view.findViewById(R.id.pairing_list);
        mTypeList = (ListView) view.findViewById(R.id.type_list);
        mTypeListLayout = view.findViewById(R.id.type_list_layout);

        mType = taskInfo.getAux_valor1();

        selectDevice();
    }

    private void selectDevice() {
        mDeviceListLayout.setVisibility(View.VISIBLE);
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final List<BluetoothDevice> pairedDevices = new ArrayList<>(mBluetoothAdapter.getBondedDevices());

        ArrayAdapter<BluetoothDevice> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, pairedDevices);
        mDeviceList.setAdapter(adapter);
        mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                mDevice = (BluetoothDevice) parent.getAdapter().getItem(position);
                mDeviceTitle.setBackgroundColor(ContextCompat.getColor(mContext, R.color.clr_green));
                mDeviceTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
                mDeviceListLayout.setVisibility(View.GONE);
                selectType();
            }
        });
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
                    mTypeTitle.setBackgroundColor(ContextCompat.getColor(mContext, R.color.clr_green));
                    mTypeTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
                    mTypeListLayout.setVisibility(View.GONE);
                    startAudit();
                }
            });
        } else {
            mTypeTitle.setBackgroundColor(ContextCompat.getColor(mContext, R.color.clr_green));
            startAudit();
        }
    }

    private void createAuditManager() {
        switch (mType) {
            case SPENGLER:
                mAuditManager = new AuditManagerSpengler(CaptureViewHolder.this);
            case DEX:
                mAuditManager = new AuditManagerDex(CaptureViewHolder.this);
            case DDCMP:
                mAuditManager = new AuditManagerDDCMP(CaptureViewHolder.this);
            case JOFEMAR:
                mAuditManager = new AuditManagerJofemar(CaptureViewHolder.this);
        }
    }

    private void startAudit() {
        createAuditManager();
        if (mAuditManager != null) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mAuditManager.setBTDevice(mDevice);
                    mAuditManager.go(getCurrentBtType());
                }
            });
        }
    }

    private String getCurrentBtType() {
        return "SENA";
    }

    @Override
    public void onAuditStart() {
        Log.d("AAA", "onAuditStart() called with: " + "");
    }

    @Override
    public void onAuditStop() {
        Log.d("AAA", "onAuditStop() called with: " + "");
    }

    @Override
    public void onAuditLog(String msg) {
        Log.d("AAA", "onAuditLog() called with: " + "msg = [" + msg + "]");
    }
}
