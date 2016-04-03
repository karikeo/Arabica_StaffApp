package com.shevchenko.staffapp.connectivity.protocols.jofemar;

import android.os.Bundle;
import android.util.Log;

import com.shevchenko.staffapp.connectivity.ReferencesStorage;
import com.shevchenko.staffapp.connectivity.helpers.ArraysHelper;
import com.shevchenko.staffapp.connectivity.protocols.IProtocolsDataManagement;
import com.shevchenko.staffapp.connectivity.protocols.ProtocolsConstants;
import com.shevchenko.staffapp.connectivity.protocols.ddcmp.Crc16;
import com.shevchenko.staffapp.connectivity.protocols.statemachine.Event;
import com.shevchenko.staffapp.connectivity.protocols.statemachine.IEventSync;
import com.shevchenko.staffapp.connectivity.protocols.statemachine.StateBase;

import java.io.IOException;
import java.util.Arrays;

public class JofemarCollectState  <AI extends IProtocolsDataManagement>
        extends StateBase<AI> implements IProtocolsDataManagement {

    public static final Event UPDATE_STATE = new Event("UPDATE_STATE", 4000);

    enum states {
        INITIAL,
        COLLECTION,
        DONE,
        TIMEOUT,
    }

    private states currentState = states.INITIAL;

    private byte data[];

    private final int BUF_SIZE = 256;
    private byte buf[] = new byte[BUF_SIZE];

    private int logSizeCounter = 0;

    public JofemarCollectState(AI automation, IEventSync eventSync) {
        super(automation, eventSync);
    }

    @Override
    public boolean startAudit() {
        return false;
    }

    @Override
    public void stopAudit() {

    }

    @Override
    public void update(int deltaTime) throws IOException {
        JofemarCommunication comm = (JofemarCommunication) ReferencesStorage.getInstance().comm;

        if (timeOut <0){
            if (data == null) {
                Log.d("JofemarCollectState", "TimeOut");
                sendTimeOutMessage("Timeout!");
                final String s = currentState.toString();
                currentState = states.TIMEOUT;
            }else{
                if (new String(Arrays.copyOf(data, 50)).indexOf("\n\r") == -1){
                    onError();
                    return;
                }
                if (new String(Arrays.copyOf(data, 5)).indexOf("***") == -1){
                //if (data[1]!= (byte)0x2A || data[2]!= (byte)0x2A){
                    onError();
                    return;
                }

                auditDone();
            }
        }

        switch (currentState) {
            case INITIAL:
                    castEvent(UPDATE_STATE);
                    currentState = states.COLLECTION;
                    data = null;
                    Log.d("JofemarCollectState", "Initial");
                    log("Start read log\n");
                    logSizeCounter = 0;
                    castEvent(UPDATE_STATE);
                break;
            case COLLECTION:
                int l = comm.available();
                if ( l == 0)
                    break;

                l = (l > 256) ?  BUF_SIZE : l;

                comm.read(buf ,l);
                data = ArraysHelper.appendData(data, Arrays.copyOf(buf, l));
                Log.d("JofemarCollectState", "read:" + Integer.toString(l) + " bytes");
                castEvent(UPDATE_STATE);

                final int tCalc = data.length / 400;
                if (tCalc > logSizeCounter){
                    logSizeCounter = tCalc;
                    log("Read:" + tCalc*400 + " bytes");
                }
                break;
            case DONE:

                castEvent(UPDATE_STATE);
                break;
            case TIMEOUT:

                castEvent(UPDATE_STATE);
                break;
        }

        timeOut = timeOut - deltaTime;
    }

    private void onError() {
        sendErrorMessage("Some error occured while downloading data.");
        currentState = states.TIMEOUT;
        castEvent(UPDATE_STATE);
    }

    private void auditDone() {
        Log.d("JofemarCollectState", "Done");
        sendDataMessage(data);
        sendDoneMessage();
        currentState = states.DONE;
        castEvent(UPDATE_STATE);
    }

    void sendErrorMessage(String msg){
        final Bundle b = new Bundle();
        b.putString("Message", msg);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_ERROR, b);
    }

    void sendDataMessage(byte[] data){
        final Bundle b = new Bundle();
        b.putByteArray("Data", data);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_DATA, b);
    }

    void sendTimeOutMessage(String s){
        final Bundle b = new Bundle();
        b.putString("Message", s);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_TIMEOUT, b);
    }

    void sendDoneMessage(){
        final Bundle b = new Bundle();
        b.putString("Message", "Done.");

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_DONE, b);
    }

    void log(String msg){
        final Bundle b = new Bundle();
        b.putString("Message", msg);
        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_LOG, b);
    }

    void sendMessage(int what, Bundle b){
        b.putString("Protocol", "Jofemar");

        final android.os.Handler h = JofemarInternalStorage.getInstance().handler;
        android.os.Message m = h.obtainMessage();
        m.what = what;
        m.setData(b);
        h.sendMessage(m);
    }
}
