package me.gookven.swingx.generictable;

import me.gookven.swingx.generictable.api.ColumnDescriptor;
import me.gookven.swingx.generictable.api.ColumnDescriptorFactory;
import me.gookven.swingx.generictable.api.GenericTableColumn;

import java.beans.PropertyDescriptor;

public class DefaultColumnDescriptorFactory implements ColumnDescriptorFactory {
    @Override
    public ColumnDescriptor createColumnDescriptor(PropertyDescriptor property) {
        GenericTableColumn column = property.getReadMethod().getAnnotation(GenericTableColumn.class);

        if (column == null) {
            return new DefaultColumnDescriptor(
                    capitalize(property.getDisplayName()),
                    property.getReadMethod() != null,
                    property.getWriteMethod() != null
            );
        }

        return new DefaultColumnDescriptor(
                column.value().length() > 0 ? column.value() : capitalize(property.getDisplayName()),
                column.visible(),
                column.editable()
        );
    }

    private String capitalize(String string) {
        if (isEmpty(string)) {
            return string;
        }

        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    private boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    private static class DefaultColumnDescriptor implements ColumnDescriptor {
        private final String displayName;
        private final boolean visible;
        private final boolean editable;

        private DefaultColumnDescriptor(String displayName, boolean visible, boolean editable) {
            this.displayName = displayName;
            this.visible = visible;
            this.editable = editable;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public boolean isVisible() {
            return visible;
        }

        @Override
        public boolean isEditable() {
            return editable;
        }
    }
}
