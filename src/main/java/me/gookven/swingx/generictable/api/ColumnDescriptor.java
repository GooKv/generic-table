package me.gookven.swingx.generictable.api;

public interface ColumnDescriptor {
    /**
     * Column name, displayed in the table
     */
    String getDisplayName();

    /**
     * Excludes property from the table
     */
    boolean isVisible();

    /**
     * Sets editable property for the whole column
     */
    boolean isEditable();
}
