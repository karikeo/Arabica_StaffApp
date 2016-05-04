package com.shevchenko.staffapp.connectivity;

import android.os.Bundle;

import com.shevchenko.staffapp.connectivity.bluetooth.BTPortBuilder;
import com.shevchenko.staffapp.connectivity.bluetooth.IOnBtOpenPort;
import com.shevchenko.staffapp.connectivity.helpers.FileHelper;
import com.shevchenko.staffapp.connectivity.protocols.ProtocolsBase;
import com.shevchenko.staffapp.connectivity.protocols.dex.DexCommunication;
import com.shevchenko.staffapp.connectivity.protocols.dex.DexProtocolReader;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;

public class AuditManagerDex
extends AuditManagerBase {

    private Timer mTimer;
    private static final int DELTA_TIME = 100;
    private static final int BAUDRATE = 9600;

    private int countdown = 4*60*1000;

    ProtocolsBase mProtocolBase;

    public AuditManagerDex(IAuditManager callback){
        super(callback);
    }

    @Override
    public void go(String btType) {

        if(callback!= null){
            callback.onAuditStart();
        }

        ReferencesStorage.getInstance().btPort = BTPortBuilder.Build(btType, mDevice);
        ReferencesStorage.getInstance().btPort.openPort(BAUDRATE, new IOnBtOpenPort() {
            @Override
            public void onBTOpenPortDone() {
                mProtocolBase = new DexProtocolReader(AuditManagerDex.this);

                ReferencesStorage.getInstance().comm = new DexCommunication();
                mProtocolBase.startAudit();

                timerStart(DELTA_TIME, DELTA_TIME);
            }

            @Override
            public void onBTOpenPortError() {
                stopErrorWithMessage("Can't initialize BT");
            }
        });
    }

    //Callback function on timer tick
    @Override
    public void onTimerTick() {
        countdown -= DELTA_TIME;
        try {
            mProtocolBase.update(DELTA_TIME);
        } catch (IOException e) {
            e.printStackTrace();
            stopErrorWithMessage("Can't download data!");
        }

        if (countdown<0){
            stopErrorWithMessage("Time Out!");
        }
    }

    @Override
    public void stop() {
        timerStop();

        if (mProtocolBase != null) {
            mProtocolBase.stopAudit();
            ReferencesStorage.getInstance().comm.setStop(true);
            ReferencesStorage.getInstance().btPort.closeSockets();
        }
    }

    @Override
    public void onAuditData(Bundle b) {
        final String a = b.getString("Data");
        final String fileName = "dex.dat";

        log("Bajando " + fileName);

        final String f = FileHelper.saveFileWithDate(fileName, Collections.singletonList(a));

        mStoredFiles.add(f);
        mStoredFiles.add(a);
    }
}
