package me.gookven.swingx.generictable;

import me.gookven.swingx.generictable.api.ModelPropertyChangeListener;
import me.gookven.swingx.generictable.api.VetoHandler;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiQuery;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTableCellFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.mockito.Spy;

import java.beans.PropertyDescriptor;
import java.beans.PropertyVetoException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class GenericTableTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private GenericTableExample frame;

    @Spy
    private ModelPropertyChangeListener<DummyDto> propertyChangeListener;

    @Spy
    private VetoHandler vetoHandler;

    @Override
    protected void onSetUp() {
        initMocks(this);

        frame = GuiActionRunner.execute(new GuiQuery<GenericTableExample>() {
            @Override
            protected GenericTableExample executeInEDT() throws Throwable {
                return new GenericTableExample("Generic table example");
            }
        });
        window = new FrameFixture(robot(), frame);
        window.show();
    }

    @Test
    public void checkModelItems() {
        assertThat(frame.genericTableModel.getItem(0), equalTo(frame.mike));
        assertThat(frame.genericTableModel.getItem(1), equalTo(frame.helga));
    }

    @Test
    public void testSimpleTableStructure() {
        JTableCellFixture helgaAge = window.table().cell(TableCell.row(1).column(0));
        helgaAge.requireEditable();
        helgaAge.requireValue("20");

        JTableCellFixture helgaGender = window.table().cell(TableCell.row(1).column(1));
        helgaGender.requireNotEditable();
        helgaGender.requireValue("FEMALE");

        JTableCellFixture helgaName = window.table().cell(TableCell.row(1).column(2));
        helgaName.requireEditable();
        helgaName.requireValue("Helga");

        JTableCellFixture mikeAnimalCell = window.table().cell(TableCell.row(0).column(3));
        mikeAnimalCell.requireEditable();
        mikeAnimalCell.requireValue("");

        JTableCellFixture mikeGenderCell = window.table().cell(TableCell.row(0).column(1));
        mikeGenderCell.requireNotEditable();
    }

    @Test
    public void testEditEnumField() {
        frame.genericTableModel.addModelPropertyChangeListener(propertyChangeListener);

        JTableCellFixture mikeAnimalCell = window.table().cell(TableCell.row(0).column(3));
        mikeAnimalCell.enterValue("MALE");
        mikeAnimalCell.requireValue("MALE");

        verify(propertyChangeListener, only())
                .onPropertyChanged(eq(frame.mike), any(PropertyDescriptor.class));
    }

    @Test
    public void testTrySetAge() {
        JTableCellFixture mikeAge = window.table().cell(TableCell.row(0).column(0));
        mikeAge.requireEditable();
        mikeAge.enterValue("71");
        mikeAge.requireValue("71");

        assertThat(frame.mike.getAge(), equalTo(71));
    }

    @Test
    public void testTrySetAgeWithRestrictions() {
        frame.genericTableModel
                .getVetoableChangeSupport()
                .addVetoableChangeListener("age", evt -> {
                    int newAge = (int) evt.getNewValue();
                    if (newAge < 0) {
                        throw new PropertyVetoException("Age should not be less than 0", evt);
                    }
                });

        frame.genericTableModel.setVetoHandler(vetoHandler);

        JTableCellFixture mikeAge = window.table().cell(TableCell.row(0).column(0));
        mikeAge.requireEditable();
        String mikeAgeValue = mikeAge.value();
        mikeAge.enterValue("-1");
        mikeAge.requireValue(mikeAgeValue);

        assertThat(frame.mike.getAge(), equalTo(26));

        verify(vetoHandler, only()).handleVeto(any(PropertyVetoException.class));
    }

}