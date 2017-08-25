package me.gookven.swingx.generictable;

import javax.swing.*;

public class DefaultEnumEditor<T extends Enum<T>> extends DefaultCellEditor {

    public DefaultEnumEditor(Class<T> enumClass) {
        super(new JComboBox<>(enumClass.getEnumConstants()));
    }

}
