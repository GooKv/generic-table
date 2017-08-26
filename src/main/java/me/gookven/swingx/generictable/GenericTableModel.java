package me.gookven.swingx.generictable;

import me.gookven.swingx.generictable.api.ColumnDescriptor;
import me.gookven.swingx.generictable.api.ColumnDescriptorFactory;
import me.gookven.swingx.generictable.api.ModelPropertyChangeListener;
import me.gookven.swingx.generictable.api.VetoHandler;

import javax.swing.table.AbstractTableModel;
import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;

public class GenericTableModel<T> extends AbstractTableModel {
    private final Column[] columns;
    private final List<T> items = new ArrayList<>();
    private final ColumnDescriptorFactory columnDescriptorFactory;
    private final VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this);
    private VetoHandler vetoHandler;

    public GenericTableModel(Class<T> clazz) {
        this(clazz, singletonList("class"), new DefaultColumnDescriptorFactory());
    }

    public GenericTableModel(Class<T> clazz, Collection<String> excludedProperties, ColumnDescriptorFactory columnDescriptorFactory) {
        this.columnDescriptorFactory = columnDescriptorFactory;
        final PropertyDescriptor[] properties = tryGetPropertyDescriptors(clazz);

        columns = Arrays.stream(properties)
                .filter(property -> !excludedProperties.contains(property.getName()))
                .map(this::createColumnFromProperty)
                .filter(column -> column.columnDescriptor.isVisible())
                .toArray(Column[]::new);
    }

    private PropertyDescriptor[] tryGetPropertyDescriptors(Class<T> clazz) {
        try {
            return Introspector.getBeanInfo(clazz).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            e.printStackTrace();
            return new PropertyDescriptor[0];
        }
    }

    private Column createColumnFromProperty(PropertyDescriptor property) {
        final ColumnDescriptor columnDescriptor = columnDescriptorFactory.createColumnDescriptor(property);
        return new Column(property, columnDescriptor);
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column].columnDescriptor.getDisplayName();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columns[columnIndex].property.getPropertyType();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final T item = items.get(rowIndex);
        final PropertyDescriptor property = columns[columnIndex].property;
        return tryGetValue(item, property);
    }

    private Object tryGetValue(T item, PropertyDescriptor property) {
        try {
            final Method readMethod = property.getReadMethod();
            if (readMethod == null) {
                throw new PropertyAccessException("Read method is missing for property " + property.getName());
            }
            return readMethod.invoke(item);
        } catch (IllegalAccessException e) {
            throw new PropertyAccessException("Could not access read method for property " + property.getName(), e);
        } catch (InvocationTargetException e) {
            throw new PropertyInvocationException("Error during invocation of read method for property " + property.getName(), e);
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);

        final T item = items.get(rowIndex);
        final PropertyDescriptor property = columns[columnIndex].property;

        if (objectsEqual(getValueAt(rowIndex, columnIndex), aValue)) {
            return;
        }

        try {
            vetoableChangeSupport.fireVetoableChange(property.getName(), getValueAt(rowIndex, columnIndex), aValue);
        } catch (PropertyVetoException vetoException) {
            handleVeto(vetoException);
            return;
        }

        trySetValue(item, property, aValue);

        fireTableCellUpdated(rowIndex, columnIndex);
        firePropertyUpdated(item, property);
    }

    private boolean objectsEqual(Object first, Object second) {
        return (first == null && second == null) || (first != null && second != null && first.equals(second));
    }

    private void trySetValue(T item, PropertyDescriptor property, Object value) {
        try {
            final Method writeMethod = property.getWriteMethod();
            if (writeMethod == null) {
                throw new PropertyAccessException("Write method is missing for property " + property.getName());
            }
            writeMethod.invoke(item, value);
        } catch (IllegalAccessException e) {
            throw new PropertyAccessException("Could not access write method for property " + property.getName(), e);
        } catch (InvocationTargetException e) {
            throw new PropertyInvocationException("Error during invocation of write method for property " + property.getName(), e);
        }
    }

    private void handleVeto(PropertyVetoException vetoException) {
        if (vetoHandler != null) {
            vetoHandler.handleVeto(vetoException);
        }
    }

    @SuppressWarnings("unchecked")
    private void firePropertyUpdated(T item, PropertyDescriptor property) {
        Arrays.stream(listenerList.getListeners(ModelPropertyChangeListener.class))
                .forEach(listener -> ((ModelPropertyChangeListener<T>) listener).onPropertyChanged(item, property));
    }

    public void addModelPropertyChangeListener(ModelPropertyChangeListener<T> modelPropertyChangeListener) {
        listenerList.add(ModelPropertyChangeListener.class, modelPropertyChangeListener);
    }

    public void removeModelPropertyChangeListener(ModelPropertyChangeListener<T> modelPropertyChangeListener) {
        listenerList.remove(ModelPropertyChangeListener.class, modelPropertyChangeListener);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columns[columnIndex].columnDescriptor.isEditable();
    }

    public void clear() {
        items.clear();
    }

    public void addItem(T item) {
        items.add(item);
    }

    public T getItem(int row) {
        return items.get(row);
    }

    public VetoableChangeSupport getVetoableChangeSupport() {
        return vetoableChangeSupport;
    }

    public void setVetoHandler(VetoHandler vetoHandler) {
        this.vetoHandler = vetoHandler;
    }

    public void addItems(Collection<T> toAdd) {
        items.addAll(toAdd);
    }

    private static class Column {
        final ColumnDescriptor columnDescriptor;
        final PropertyDescriptor property;

        private Column(PropertyDescriptor property, ColumnDescriptor columnDescriptor) {
            this.columnDescriptor = columnDescriptor;
            this.property = property;
        }
    }
}
