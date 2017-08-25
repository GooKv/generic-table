package me.gookven.swingx.generictable;

import java.lang.reflect.InvocationTargetException;

public class PropertyInvocationException extends RuntimeException {
    public PropertyInvocationException(String s, InvocationTargetException e) {
        super(s, e);
    }
}
