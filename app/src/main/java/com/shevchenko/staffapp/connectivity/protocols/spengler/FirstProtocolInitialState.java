package com.shevchenko.staffapp.connectivity.protocols.spengler;

import com.shevchenko.staffapp.connectivity.protocols.IProtocolsDataManagement;
import com.shevchenko.staffapp.connectivity.protocols.statemachine.Event;
import com.shevchenko.staffapp.connectivity.protocols.statemachine.IEventSync;
import com.shevchenko.staffapp.connectivity.protocols.statemachine.StateBase;

import java.io.IOException;

class FirstProtocolInitialState <AI extends IProtocolsDataManagement>
        extends StateBase<AI> implements IProtocolsDataManagement{

    public FirstProtocolInitialState(AI automation, IEventSync eventSync) {
        super(automation, eventSync);

    }

    public final static Event INITIAL_STATE = new Event("INITIAL_STATE", 1000);

    @Override
    public boolean startAudit() {
        return true;
    }

    @Override
    public void stopAudit() {
    }

    @Override
    public void update(int deltaTime) throws IOException {
        castEvent(INITIAL_STATE);
    }

}
