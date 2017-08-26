package me.gookven.swingx.generictable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.List;

import static java.util.Arrays.asList;

public class GenericTableExample extends JFrame {

    public GenericTableModel<DummyDto> genericTableModel;
    public JTable table;
    public JScrollPane scrollPane;
    public DefaultEnumEditor<Gender> genderEnumEditor;

    public GenericTableExample(String title, List<DummyDto> dtos) throws HeadlessException {
        super(title);
        initialize(dtos);
    }

    private void initialize(List<DummyDto> dtos) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(640, 480));

        genericTableModel = new GenericTableModel<>(DummyDto.class);
        table = new JTable(genericTableModel);

        genderEnumEditor = new DefaultEnumEditor<>(Gender.class);
        table.setDefaultEditor(Gender.class, genderEnumEditor);

        UnboxedIntEditor unboxedIntEditor = new UnboxedIntEditor();
        table.setDefaultEditor(int.class, unboxedIntEditor);

        genericTableModel.addItems(dtos);

        genericTableModel.addModelPropertyChangeListener((item, prop) ->
                System.out.println("Changed property " + prop.getName() + " for object " + item));

        genericTableModel
                .getVetoableChangeSupport()
                .addVetoableChangeListener("age", evt -> {
                    int newAge = (int) evt.getNewValue();
                    if (newAge < 0) {
                        throw new PropertyVetoException("Age should not be less than 0", evt);
                    }
                });

        scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private static final DummyDto MIKE = new DummyDto("Mike", 26, Gender.MALE);
    private static final DummyDto HELGA = new DummyDto("Helga", 20, Gender.FEMALE);

    public static void main(String[] args) {
        new GenericTableExample("Generic table example", asList(MIKE, HELGA)).setVisible(true);
    }
}
