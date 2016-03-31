package com.shevchenko.staffapp.connectivity.protocols;

import android.os.Bundle;

public interface IonAuditState {
    void onAuditDone(Bundle b);
    void onAuditError(Bundle b);
    void onAuditTimeOut(Bundle b);
    void onAuditUpdate(Bundle b);
    void onAuditLog(Bundle b);
    void onAuditData(Bundle b);
}
