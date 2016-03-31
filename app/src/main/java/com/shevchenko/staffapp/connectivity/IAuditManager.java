package com.shevchenko.staffapp.connectivity;

import java.util.List;

public interface IAuditManager {
    public void onAuditStart();
    public void onError(String msg);
    public void onSuccess(List<String> filesList);
    public void onAuditLog(String msg);
}
