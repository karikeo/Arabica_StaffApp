package com.shevchenko.staffapp.connectivity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import com.shevchenko.staffapp.connectivity.protocols.IonAuditState;

public abstract class AuditManagerBase implements IonAuditState {
    protected IAuditManager callback;

    BluetoothDevice mDevice;
    public void setBTDevice(BluetoothDevice d){
        mDevice = d;
    }

    public AuditManagerBase(IAuditManager i){
        callback = i;
    }

    abstract public void go(String btType);
    abstract public void stop();


    @Override
    public void onAuditDone(Bundle b) {
        stopWithMessage("Done!");
    }

    @Override
    public void onAuditError(Bundle b) {
        final String s = b.getString("Message");
        stopWithMessage("Error! Stop with message: " + s);
    }

    @Override
    public void onAuditTimeOut(Bundle b) {
        final String s = b.getString("Message");
        stopWithMessage("Timeout! Stop with message: "+s);
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

    protected void stopWithMessage(String msg){
        stop();

        if (callback != null) {
            callback.onAuditStop();
            callback.onAuditLog(msg);
        }
    }

    protected void log(String msg){
        if (callback != null) {
            callback.onAuditLog(msg);
        }
    }


}