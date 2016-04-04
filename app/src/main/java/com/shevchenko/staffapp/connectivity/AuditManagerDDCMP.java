package com.shevchenko.staffapp.connectivity;

import android.os.Bundle;

import com.shevchenko.staffapp.connectivity.bluetooth.BTPortBuilder;
import com.shevchenko.staffapp.connectivity.bluetooth.IOnBtOpenPort;
import com.shevchenko.staffapp.connectivity.helpers.FileHelper;
import com.shevchenko.staffapp.connectivity.protocols.ProtocolsBase;
import com.shevchenko.staffapp.connectivity.protocols.ddcmp.DDCMPCommunication;
import com.shevchenko.staffapp.connectivity.protocols.ddcmp.DDCMPDataReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AuditManagerDDCMP extends AuditManagerBase{

    private ProtocolsBase mProtocolsBase;

    private Timer mTimer;
    private static final int DELTA_TIME = 100;
    private static final int BAUDRATE = 9600;

    public AuditManagerDDCMP(IAuditManager i) {
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

                mProtocolsBase = new DDCMPDataReader(AuditManagerDDCMP.this);
                ReferencesStorage.getInstance().comm = new DDCMPCommunication();

                mProtocolsBase.startAudit();

                mTimer = new Timer("DDCMPAuditManagerUpdate");
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
    public void stop(){
        if (mTimer != null)
            mTimer.cancel();

        if (mProtocolsBase != null) {
            mProtocolsBase.stopAudit();
            ReferencesStorage.getInstance().comm.setStop(true);
            ReferencesStorage.getInstance().btPort.closeSockets();
        }

    }

    @Override
    public void onAuditData(Bundle b) {
        final byte[] d = b.getByteArray("Data");

        String s = "";

        for (byte sb : d)
                s+=(char)sb;

        List<String> vs = new ArrayList<>();
        vs.add(s);


        final String fileName = "ddcmp.dcp";
        final String f = FileHelper.saveFileWithDate(fileName, vs);

        log("Bajando " + fileName);

        log("<a href=\"file://" + f + "\">" + fileName + "</a>");

        mStoredFiles.add(f);
    }

}
