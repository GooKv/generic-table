package me.gookven.swingx.generictable;

import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static me.gookven.swingx.generictable.Gender.FEMALE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class GenericTableModelTest {

    private GenericTableModel<DummyDto> genericTableModel;

    private DummyDto mike = new DummyDto("Mike", 26, Gender.MALE);
    private DummyDto helga = new DummyDto("Helga", 20, FEMALE);

    @Before
    public void setUp() {
        genericTableModel = new GenericTableModel<>(DummyDto.class, emptyList(), new DefaultColumnDescriptorFactory());

        genericTableModel.addItems(asList(mike, helga));
    }

    @Test
    public void testHiddenColumns() {
        assertThat("id column is hidden", genericTableModel.getColumnCount(), equalTo(5));
    }

    @Test
    public void testDtoCount() {
        assertThat("two dto's in the model", genericTableModel.getRowCount(), equalTo(2));

        genericTableModel.clear();

        assertThat(genericTableModel.getRowCount(), equalTo(0));
    }

    @Test(expected = PropertyAccessException.class)
    public void trySetUnsettable() {
        genericTableModel.setValueAt(FEMALE, 0, 2);
    }
}
