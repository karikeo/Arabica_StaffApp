package com.shevchenko.staffapp.connectivity.protocols.jofemar;

import android.os.Handler;
import android.os.Message;

import com.shevchenko.staffapp.connectivity.protocols.IProtocolsDataManagement;
import com.shevchenko.staffapp.connectivity.protocols.IonAuditState;
import com.shevchenko.staffapp.connectivity.protocols.ProtocolsBase;
import com.shevchenko.staffapp.connectivity.protocols.ProtocolsConstants;

import java.io.IOException;

public class JofemarDataReader  extends ProtocolsBase {

    public JofemarDataReader(final IonAuditState callback){

        IProtocolsDataManagement collectState = new JofemarCollectState<IProtocolsDataManagement>(this, this);

        addEdge(collectState, JofemarCollectState.UPDATE_STATE, collectState);

        state = collectState;

        JofemarInternalStorage.getInstance().handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (callback == null) return;

                switch(msg.what){
                    case ProtocolsConstants.MSG_ACTION_AUDIT_DONE:
                        callback.onAuditDone(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_DATA:
                        callback.onAuditData(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_ERROR:
                        callback.onAuditError(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_LOG:
                        callback.onAuditLog(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_TIMEOUT:
                        callback.onAuditTimeOut(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_UPDATE:
                        callback.onAuditUpdate(msg.getData());
                        break;
                }
            }
        };
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
        try {
            state.update(deltaTime);
        }catch (IOException e){
            e.printStackTrace();
            stopAudit();
        }
    }
}