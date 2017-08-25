package me.gookven.swingx.generictable;

import me.gookven.swingx.generictable.api.ModelPropertyChangeListener;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiQuery;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTableCellFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.mockito.Spy;

import java.beans.PropertyDescriptor;

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
        mikeAnimalCell.startEditing();
        mikeAnimalCell.enterValue("MALE");
        mikeAnimalCell.stopEditing();
        mikeAnimalCell.requireValue("MALE");

        verify(propertyChangeListener, only())
                .onPropertyChanged(eq(frame.mike), any(PropertyDescriptor.class));
    }

}