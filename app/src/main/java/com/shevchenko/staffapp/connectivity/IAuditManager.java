package com.shevchenko.staffapp.connectivity;

public interface IAuditManager {
    public void onAuditStart();
    public void onAuditStop();
    public void onAuditLog(String msg);
}
