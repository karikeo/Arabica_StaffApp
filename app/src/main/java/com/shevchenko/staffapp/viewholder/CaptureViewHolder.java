package com.shevchenko.staffapp.viewholder;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.shevchenko.staffapp.Model.LogFile;
import com.shevchenko.staffapp.Model.TaskInfo;
import com.shevchenko.staffapp.R;
import com.shevchenko.staffapp.connectivity.*;
import com.shevchenko.staffapp.db.DBManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CaptureViewHolder implements IAuditManager {
    public static final String BT_DEVICE = "BTDevice";

    public static final String SPENGLER = "Spengler";
    public static final String DEX = "DEX";
    public static final String JOFEMAR = "Jofemar";
    public static final String DDCMP = "DDCMP";
    public static final int BT_REQUEST_CODE = 11;

    private final Activity mContext;
    private final DBManager mDBManager;
    private final TaskInfo mTaskInfo;

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
    private ArrayAdapter<BluetoothDeviceWrapper> mDeviceAdapter;

    private final Drawable mProgressDrawable;
    private BluetoothAdapter mBluetoothAdapter;

    public CaptureViewHolder(Activity activity, View view, final TaskInfo taskInfo, DBManager dbManager) {
        mContext = activity;
        mDBManager = dbManager;
        mTaskInfo = taskInfo;

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

        mProgressDrawable = ContextCompat.getDrawable(mContext, android.R.drawable.ic_popup_sync);

        mDeviceTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                selectDevice();
            }
        });
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void start() {
        selectDevice();
    }

    private void selectDevice() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(mContext, "You don't have bluetooth :(", Toast.LENGTH_LONG).show();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivityForResult(intentBtEnabled, BT_REQUEST_CODE);
        } else {
            final List<BluetoothDevice> pairedDevices = new ArrayList<>(mBluetoothAdapter.getBondedDevices());
            restoreFromPrefs(pairedDevices);

            if (mDevice == null) {
                startDiscovery();
                mDeviceListLayout.setVisibility(View.VISIBLE);
                mDeviceAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, from(pairedDevices));
                mDeviceList.setAdapter(mDeviceAdapter);
                mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        mBluetoothAdapter.cancelDiscovery();
                        mDevice = ((BluetoothDeviceWrapper) parent.getAdapter().getItem(position)).getDevice();
                        saveToPrefs();
                        mDeviceListLayout.setVisibility(View.GONE);
                        setDone(mDeviceTitle, true);
                        selectType();
                    }
                });
            } else {
                setDone(mDeviceTitle, true);
                selectType();
            }
        }
    }

    private void selectType() {
        if (mType.isEmpty()) {
            mTypeListLayout.setVisibility(View.VISIBLE);
            String[] types = {SPENGLER, DEX, DDCMP, JOFEMAR};
            ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, types);
            mTypeList.setAdapter(typeAdapter);
            mTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mType = (String) parent.getAdapter().getItem(position);
                    mTypeListLayout.setVisibility(View.GONE);
                    setDone(mTypeTitle, true);
                    startAudit();
                }
            });
        } else {
            setDone(mTypeTitle, true);
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

    private void setDone(TextView view, boolean done) {
        if (view.getCompoundDrawables()[2] == mProgressDrawable) {
            if (mProgressDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable) mProgressDrawable).stop();
            }
        }
        view.setBackgroundColor(ContextCompat.getColor(mContext, done ? R.color.clr_green : R.color.clr_lightgraqy));
        view.setCompoundDrawablesWithIntrinsicBounds(0, 0, done ? R.drawable.check : 0, 0);
    }

    private void setProgress(TextView view, boolean progress) {
        if (progress) {
            view.setCompoundDrawablesWithIntrinsicBounds(null, null, mProgressDrawable, null);
            if (mProgressDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable)mProgressDrawable).start();
            }
        } else if (view.getCompoundDrawables()[2] == mProgressDrawable) {
            if (mProgressDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable) mProgressDrawable).stop();
            }
            view.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    private void saveToPrefs() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putString(BT_DEVICE, mDevice.getAddress());
        editor.apply();
    }

    private void reset() {
        setDone(mDeviceTitle, false);
        setDone(mTypeTitle, false);
        setDone(mPairingTitle, false);
        mTypeListLayout.setVisibility(View.GONE);
        mPairingLoading.setVisibility(View.GONE);
        mDevice = null;
        mType = "";
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        if (mAuditManager != null) {
            mAuditManager.stop();
        }

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.remove(BT_DEVICE);
        editor.apply();
    }

    private String getCurrentBtType() {
        return "SENA";
    }

    @Override
    public void onAuditStart() {
        Log.d("AAA", "onAuditStart() called with: " + "");
        mPairingLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(String msg) {
        Log.d("AAA", "onError() called with: " + "msg = [" + msg + "]");
        mPairingLoading.setVisibility(View.GONE);
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        reset();
        selectDevice();
    }

    @Override
    public void onSuccess(List<String> filesList) {
        mPairingLoading.setVisibility(View.GONE);
        setDone(mPairingTitle, true);
        Toast.makeText(mContext, "Files " + filesList + " saved", Toast.LENGTH_LONG).show();
        for (String msg : filesList) {
            mDBManager.insertLogFile(new LogFile(mTaskInfo.getTaskID(), msg, mType));
            Log.d("AAA", "onSuccess() called with: " + "msg = [" + msg + "]");
        }
    }

    @Override
    public void onAuditLog(String msg) {
        Log.d("AAA", "onAuditLog() called with: " + "msg = [" + msg + "]");
    }

    private void startDiscovery() {
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        mContext.registerReceiver(mReceiver, filter);
        mBluetoothAdapter.startDiscovery();
    }

    private List<BluetoothDeviceWrapper> from(List<BluetoothDevice> list) {
        List<BluetoothDeviceWrapper> res = new LinkedList<>();
        for (BluetoothDevice d : list) {
            res.add(new BluetoothDeviceWrapper(d));
        }
        return res;
    }

    private static class BluetoothDeviceWrapper {
        final private BluetoothDevice device;

        BluetoothDeviceWrapper(BluetoothDevice d) {
            device = d;
        }

        public BluetoothDevice getDevice() {
            return device;
        }

        @Override
        public String toString() {
            return device.getName();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d("AAA", "BT discovery started");
                    setProgress(mDeviceTitle, true);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d("AAA", "BT discovery finished");
                    setProgress(mDeviceTitle, false);
                    mContext.unregisterReceiver(this);
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    Log.d("AAA", "BT device found");
                    mDeviceAdapter.add(new BluetoothDeviceWrapper((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)));
                    break;
            }
        }
    };
}
