package me.gookven.swingx.generictable;

public class PropertyAccessException extends RuntimeException {
    public PropertyAccessException(String s, ReflectiveOperationException e) {
        super(s, e);
    }

    public PropertyAccessException(String s) {
        super(s);
    }
}
