package me.gookven.swingx.generictable;

import javax.swing.*;
import java.awt.*;

public class GenericTableExample extends JFrame {

    public GenericTableModel<DummyDto> genericTableModel;
    public JTable table;
    public JScrollPane scrollPane;
    public DefaultEnumEditor<Gender> genderEnumEditor;
    public DummyDto mike;
    public DummyDto helga;

    public GenericTableExample(String title) throws HeadlessException {
        super(title);
        initialize();
    }

    public static void main(String[] args) {
        new GenericTableExample("Generic table example").setVisible(true);
    }

    private void initialize() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(640, 480));

        genericTableModel = new GenericTableModel<>(DummyDto.class);
        table = new JTable(genericTableModel);

        genderEnumEditor = new DefaultEnumEditor<>(Gender.class);
        table.setDefaultEditor(Gender.class, genderEnumEditor);

        mike = new DummyDto("Mike", 26, Gender.MALE);
        helga = new DummyDto("Helga", 20, Gender.FEMALE);

        genericTableModel.addItem(mike);
        genericTableModel.addItem(helga);

        genericTableModel.addModelPropertyChangeListener((item, prop) ->
                System.out.println("Changed property " + prop.getName() + " for object " + item));

        scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
}
