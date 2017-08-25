package me.gookven.swingx.generictable.api;

import java.beans.PropertyDescriptor;

public interface ColumnDescriptorFactory {
    ColumnDescriptor createColumnDescriptor(PropertyDescriptor propertyDescriptor);
}
