package com.shevchenko.staffapp.connectivity;


import android.os.Bundle;
import android.util.Log;

import com.shevchenko.staffapp.connectivity.bluetooth.BTPortBuilder;
import com.shevchenko.staffapp.connectivity.bluetooth.IOnBtOpenPort;
import com.shevchenko.staffapp.connectivity.helpers.FileHelper;
import com.shevchenko.staffapp.connectivity.protocols.IonAuditState;
import com.shevchenko.staffapp.connectivity.protocols.ProtocolsBase;
import com.shevchenko.staffapp.connectivity.protocols.jofemar.JofemarCommunication;
import com.shevchenko.staffapp.connectivity.protocols.jofemar.JofemarDataReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AuditManagerJofemar extends AuditManagerBase implements IonAuditState {

    private ProtocolsBase mProtocolsBase;

    private Timer mTimer;
    private static final int DELTA_TIME = 100;
    private static final int BAUDRATE = 1200;

    private int attempsCounter = 0;


    public AuditManagerJofemar(IAuditManager i) {
        super(i);
    }

    @Override
    public void go(String btType) {

        if (callback != null) {
            callback.onAuditStart();
        }

        ReferencesStorage.getInstance().btPort = BTPortBuilder.Build(btType, mDevice);
        ReferencesStorage.getInstance().btPort.openPort(BAUDRATE, new IOnBtOpenPort() {
            @Override
            public void onBTOpenPortDone() {
                log("BT Port Opened.");

                mProtocolsBase = new JofemarDataReader(AuditManagerJofemar.this);
                mProtocolsBase.startAudit();

                ReferencesStorage.getInstance().comm = new JofemarCommunication();

                mTimer = new Timer("JofemarAuditManagerUpdate");
                mTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            mProtocolsBase.update(DELTA_TIME);
                        } catch (IOException e) {
                            e.printStackTrace();
                            mProtocolsBase.stopAudit();
                        }
                    }
                }, 100, DELTA_TIME);
            }

            @Override
            public void onBTOpenPortError() {
                stopErrorWithMessage("Can't initialize BT.");
            }
        });

    }

    @Override
    public void stop() {
        if (mTimer!= null){
            mTimer.cancel();
        }

        if (mProtocolsBase != null){
            mProtocolsBase.stopAudit();
            ReferencesStorage.getInstance().comm.setStop(true);
            ReferencesStorage.getInstance().btPort.closeSockets();
            mProtocolsBase = null;
        }

    }

    @Override
    public void onAuditData(Bundle b) {
        final byte[] a = b.getByteArray("Data");
        final String str = new String(a);

        List<String> data = new ArrayList<>();

        data.add(str);

        final String fileName = "jofemar.dat";
        final String f = FileHelper.saveFileWithDate(fileName, data);

        log("Bajando " + fileName);

        log("<a href=\"file://" + f + "\">" + fileName + "</a>");
        for (String s : data){
            log(s);
        }

        mStoredFiles.add(f);
    }

    @Override
    public void onAuditError(Bundle b) {
        if (attempsCounter == 1) {
            super.onAuditError(b);
            return;
        }

        log("Wrong log file. Try to read it again");

        //According to the issue with BT reconnection
        //We have wrong data in the buffer
        //So we close all and try to reinitialize all staff again
        attempsCounter++;

        stop();
        Log.d("AuditManagerJofemart", "BT Port closed");

        mProtocolsBase = new JofemarDataReader(AuditManagerJofemar.this);
        mProtocolsBase.startAudit();

        Log.d("AuditManagerJofemart" , "BT Port try to open");
        log("Try to  open BT Port.");
        ReferencesStorage.getInstance().btPort.openSocketsAsyncWithPause(new IOnBtOpenPort() {
            @Override
            public void onBTOpenPortDone() {
                log("BT Port Opened.");
                Log.d("AuditManagerJofemar" , "BT Port opened");
                ReferencesStorage.getInstance().comm = new JofemarCommunication();

                mTimer = new Timer("JofemarAuditManagerUpdate");
                mTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            mProtocolsBase.update(DELTA_TIME);
                        } catch (IOException e) {
                            e.printStackTrace();
                            mProtocolsBase.stopAudit();
                        }
                    }
                }, 100, DELTA_TIME);
            }

            @Override
            public void onBTOpenPortError() {
                stopErrorWithMessage("Can not re-initialize BT");
            }
        }, 10000);


    }
}
