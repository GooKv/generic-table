package me.gookven.swingx.generictable.api;

import java.beans.PropertyDescriptor;
import java.util.EventListener;

public interface ModelPropertyChangeListener<T> extends EventListener {
    void onPropertyChanged(T object, PropertyDescriptor property);
}
