package com.shevchenko.staffapp.connectivity;

import android.os.Bundle;

import com.shevchenko.staffapp.connectivity.bluetooth.BTPortBuilder;
import com.shevchenko.staffapp.connectivity.bluetooth.IOnBtOpenPort;
import com.shevchenko.staffapp.connectivity.helpers.FileHelper;
import com.shevchenko.staffapp.connectivity.protocols.ProtocolsBase;
import com.shevchenko.staffapp.connectivity.protocols.dex.DexCommunication;
import com.shevchenko.staffapp.connectivity.protocols.dex.DexProtocolReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AuditManagerDex
extends AuditManagerBase {

    private Timer mTimer;
    private static final int DELTA_TIME = 100;
    private static final int BAUDRATE = 9600;

    ProtocolsBase protocolsBase;

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
                protocolsBase = new DexProtocolReader(AuditManagerDex.this);

                ReferencesStorage.getInstance().comm = new DexCommunication();
                protocolsBase.startAudit();

                mTimer = new Timer("AuditManagerUpdate");
                mTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            protocolsBase.update(DELTA_TIME);
                        } catch (IOException e) {
                            e.printStackTrace();
                            protocolsBase.stopAudit();
                        }
                    }
                }, DELTA_TIME, DELTA_TIME);
            }

            @Override
            public void onBTOpenPortError() {
                stopErrorWithMessage("Can't initialize BT");
            }
        });
    }

    @Override
    public void stop() {
        if (mTimer != null)
            mTimer.cancel();

        if (protocolsBase != null) {
            protocolsBase.stopAudit();
            ReferencesStorage.getInstance().comm.setStop(true);
            ReferencesStorage.getInstance().btPort.closeSockets();
        }
    }

    @Override
    public void onAuditData(Bundle b) {
        final String a = b.getString("Data");

        List<String> data = new ArrayList<>();

        data.add(a);

        final String fileName = "dex.dat";
        final String f = FileHelper.saveFileWithDate(fileName, data);

        log("Bajando " + fileName);

        log("<a href=\"file://" + f + "\">" + fileName + "</a>");
        for (String s : data){
            log(s);
        }

        mStoredFiles.add(f);
    }
}
