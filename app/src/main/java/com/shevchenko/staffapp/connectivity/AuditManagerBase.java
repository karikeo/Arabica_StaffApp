package com.shevchenko.staffapp.connectivity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import com.shevchenko.staffapp.connectivity.protocols.IonAuditState;

import java.util.ArrayList;
import java.util.List;

public abstract class AuditManagerBase implements IonAuditState {
    protected IAuditManager callback;
    protected List<String> mStoredFiles;

    BluetoothDevice mDevice;
    public void setBTDevice(BluetoothDevice d){
        mDevice = d;
    }

    public AuditManagerBase(IAuditManager i){
        callback = i;
        mStoredFiles = new ArrayList<>();
    }

    abstract public void go(String btType);
    abstract public void stop();


    @Override
    public void onAuditDone(Bundle b) {
        //stopWithMessage("Done!");
        stopSuccess(mStoredFiles);
    }

    @Override
    public void onAuditError(Bundle b) {
        final String s = b.getString("Message");
        stopErrorWithMessage("Error! Stop with message: " + s);
    }

    @Override
    public void onAuditTimeOut(Bundle b) {
        final String s = b.getString("Message");
        stopErrorWithMessage("Timeout! Stop with message: " + s);
    }

    @Override
    public void onAuditUpdate(Bundle b) {

    }

    @Override
    public void onAuditLog(Bundle b) {
        final String s = b.getString("Message");
        if (callback!= null){
            callback.onAuditLog(s);
        }
    }

    protected void stopErrorWithMessage(String msg){
        stop();

        if (callback != null) {
            callback.onError(msg);
            callback.onAuditLog(msg);
        }
    }

    protected void stopSuccess(List<String> filesList){
        stop();

        if (callback != null){
            callback.onSuccess(filesList);
        }
    }

    protected void log(String msg){
        if (callback != null) {
            callback.onAuditLog(msg);
        }
    }


}
