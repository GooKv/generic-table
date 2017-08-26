package me.gookven.swingx.generictable;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class UnboxedIntEditor extends DefaultCellEditor {
    private final int defaultValue;
    private Object value;

    public UnboxedIntEditor() {
        this(0);
    }

    public UnboxedIntEditor(int defaultValue) {
        super(new JTextField());
        this.defaultValue = defaultValue;
    }

    public boolean stopCellEditing() {
        String s = (String) super.getCellEditorValue();
        try {
            if (s == null || "".equals(s)) {
                value = defaultValue;
                return super.stopCellEditing();
            }

            value = Integer.parseInt(s);
        } catch (Exception e) {
            ((JComponent) getComponent()).setBorder(new LineBorder(Color.red));
            return false;
        }
        return super.stopCellEditing();
    }

    public Object getCellEditorValue() {
        return value;
    }
}
