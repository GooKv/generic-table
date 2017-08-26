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

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class GenericTableTest extends AssertJSwingJUnitTestCase {

    private static final int MIKE_ROW = 0;
    private static final int HELGA_ROW = 1;
    private FrameFixture window;
    private GenericTableExample frame;
    private DummyDto mike = new DummyDto("Mike", 26, Gender.MALE);
    private DummyDto helga = new DummyDto("Helga", 20, Gender.FEMALE);

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
                return new GenericTableExample("Generic table test", asList(mike, helga));
            }
        });
        window = new FrameFixture(robot(), frame);
        window.show();
    }

    @Test
    public void checkModelItems() {
        assertThat(frame.genericTableModel.getItem(MIKE_ROW), equalTo(mike));
        assertThat(frame.genericTableModel.getItem(HELGA_ROW), equalTo(helga));
    }

    @Test
    public void testSimpleTableStructure() {
        JTableCellFixture helgaAge = window.table().cell(TableCell.row(HELGA_ROW).column(0));
        helgaAge.requireEditable();
        helgaAge.requireValue("20");

        JTableCellFixture helgaGender = window.table().cell(TableCell.row(HELGA_ROW).column(1));
        helgaGender.requireNotEditable();
        helgaGender.requireValue("FEMALE");

        JTableCellFixture helgaName = window.table().cell(TableCell.row(HELGA_ROW).column(2));
        helgaName.requireEditable();
        helgaName.requireValue("Helga");

        JTableCellFixture mikeAnimalCell = window.table().cell(TableCell.row(MIKE_ROW).column(3));
        mikeAnimalCell.requireEditable();
        mikeAnimalCell.requireValue("");

        JTableCellFixture mikeGenderCell = window.table().cell(TableCell.row(MIKE_ROW).column(1));
        mikeGenderCell.requireNotEditable();
    }

    @Test
    public void testEditEnumField() {
        frame.genericTableModel.addModelPropertyChangeListener(propertyChangeListener);

        JTableCellFixture mikeAnimalCell = window.table().cell(TableCell.row(MIKE_ROW).column(3));
        mikeAnimalCell.enterValue("MALE");
        mikeAnimalCell.requireValue("MALE");

        frame.genericTableModel.removeModelPropertyChangeListener(propertyChangeListener);

        mikeAnimalCell.enterValue("MALE");
        mikeAnimalCell.enterValue("FEMALE");
        mikeAnimalCell.requireValue("FEMALE");

        // not called after property change listener is removed
        verify(propertyChangeListener, only())
                .onPropertyChanged(eq(mike), any(PropertyDescriptor.class));
    }

    @Test
    public void testTrySetAge() {
        JTableCellFixture mikeAge = window.table().cell(TableCell.row(MIKE_ROW).column(0));
        mikeAge.requireEditable();
        mikeAge.enterValue("71");
        mikeAge.requireValue("71");

        assertThat(mike.getAge(), equalTo(71));
    }

    @Test
    public void testTrySetAgeWithRestrictions() {
        frame.genericTableModel.setVetoHandler(vetoHandler);

        JTableCellFixture mikeAge = window.table().cell(TableCell.row(MIKE_ROW).column(0));
        mikeAge.requireEditable();
        String mikeAgeValue = mikeAge.value();
        mikeAge.enterValue("-1");
        mikeAge.requireValue(mikeAgeValue);

        assertThat(mike.getAge(), equalTo(26));

        verify(vetoHandler, only()).handleVeto(any(PropertyVetoException.class));
    }

}