package com.shevchenko.staffapp.connectivity;

import android.os.Bundle;

import com.shevchenko.staffapp.connectivity.bluetooth.BTPortBuilder;
import com.shevchenko.staffapp.connectivity.bluetooth.IOnBtOpenPort;
import com.shevchenko.staffapp.connectivity.helpers.FileHelper;
import com.shevchenko.staffapp.connectivity.protocols.IonAuditState;
import com.shevchenko.staffapp.connectivity.protocols.ProtocolsBase;
import com.shevchenko.staffapp.connectivity.protocols.spengler.SpenglerCommunication;
import com.shevchenko.staffapp.connectivity.protocols.spengler.FirstProtocolDataReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AuditManagerSpengler
        extends AuditManagerBase
        implements IonAuditState{


    private ProtocolsBase mProtocolBase;

    private Timer mTimer;
    private final int DELTA_TIME = 100;
    private final int BAUDRATE = 19200;

    public AuditManagerSpengler(IAuditManager callback){
        super(callback);
    }


    public void go(String btType){

        if (callback != null) {
            callback.onAuditStart();
        }

        ReferencesStorage.getInstance().btPort = BTPortBuilder.Build(btType, mDevice);
        ReferencesStorage.getInstance().btPort.openPort(BAUDRATE, new IOnBtOpenPort() {
            @Override
            public void onBTOpenPortDone() {
                ReferencesStorage.getInstance().comm = new SpenglerCommunication();

                ArrayList<String> files = new ArrayList<>();
                files.add("OPNCASH.DAT");
                files.add("ADMIN.28");
                mProtocolBase = new FirstProtocolDataReader(AuditManagerSpengler.this, files);
                mProtocolBase.startAudit();

                mTimer = new Timer("SpenglerAuditManagerUpdate");
                mTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            mProtocolBase.update(DELTA_TIME);
                        } catch (IOException e) {
                            e.printStackTrace();
                            mProtocolBase.stopAudit();
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

    public void stop(){
        if (mTimer != null)
            mTimer.cancel();

        if (mProtocolBase != null) {
            mProtocolBase.stopAudit();
            ReferencesStorage.getInstance().comm.setStop(true);
            ReferencesStorage.getInstance().btPort.closeSockets();
        }

    }


    @Override
    public void onAuditData(Bundle b) {
        //Here we have file!!! We must parse it, show and store

        final String fileName = b.getString("FileName");
        final byte[] d = b.getByteArray("Data");

        List<String> data;
        String f;
        if (fileName.equals("OPNCASH.DAT")){
            data = extractOPNCASH(d);
            f = FileHelper.saveFileWithDate(fileName, data);
        }else if (fileName.equals("ADMIN.28")){
            data = extractADMIN(d);
            f = FileHelper.saveFileWithDate(fileName, data);
        }else{
            log("Unknown file:" + fileName);
            return;
        }
        log("Bajando " + fileName);

        log("<a href=\"file://" + f + "\">" + fileName + "</a>");
        for (String s : data){
            log(s);
        }

        mStoredFiles.add(f);
    }


    private List<String> extractOPNCASH(byte[] d){
        final List<String> l = new ArrayList<>();
        String s;

        for (int j = 0; j < d.length ; j = j + 44)
        {
            s = "";
            for (int i = 0; i < 44; i = i + 4)
            {
                final int n = (0x00FF&d[j + i]) + 256 *(0x00FF&d[j + i + 1]) + 256 * 256 *(0x00FF&d[j + i + 2]) + 256 * 256 * 256 *(0x00FF&d[j + i + 3]);
                s = s + " " + Integer.toString(n);
            }
            l.add(s);
        }

        return l;
    }

    private List<String> extractADMIN(byte[] d){
        final List<String> l = new ArrayList<>();

        int category;
        for (category = 0; category <= 2; category++)
        {
            for (int position = 0; position <= 11; position++)
            {
                int offset = 72 * category + 2 * position;
                int count = (0x00FF&d[offset]) + 256 * (0x00FF&d[offset + 1]);
                offset = 72 * category + 24 + 4 * position;
                int amount = (0x00FF&d[offset]) + 256 * (0x00FF&d[offset + 1]) +
                        256 * 256 * (0x00FF&d[offset + 2]) + 256 * 256 * 256 * (0x00FF&d[offset + 3]);

                final String s = Integer.toString(position) + " " + Integer.toString(category) + " " +
                        Integer.toString(count) + " " + Integer.toString(amount);
                l.add(s);
            }
        }

        for (int position = 0; position <= 11; position++)
        {
            int offset = 72 * category + 2 * position;
            int count = d[offset] + 256 * d[offset + 1];

            int amount = 0;

            final String s = Integer.toString(position) + " " + Integer.toString(category) + " " +
                    Integer.toString(count) + " " + Integer.toString(amount);
            l.add(s);
        }

        return l;
    }
}
