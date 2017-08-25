package me.gookven.swingx.generictable.api;

import java.beans.PropertyVetoException;

public interface VetoHandler {
    void handleVeto(PropertyVetoException vetoException);
}
