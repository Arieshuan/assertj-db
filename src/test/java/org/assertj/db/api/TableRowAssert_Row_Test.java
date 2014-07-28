package org.assertj.db.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;

import org.assertj.db.common.AbstractTest;
import org.assertj.db.type.Table;
import org.junit.Test;

/**
 * Test on {@code row} methods on row assert from a table.
 * 
 * @author Régis Pouiller
 * 
 */
public class TableRowAssert_Row_Test extends AbstractTest {

  /**
   * This method tests the result of {@code row} methods on row assert from a table.
   */
  @Test
  public void test_with_table_and_row() {
    Table table = new Table(source, "test");
    
    TableAssert tableAssert = assertThat(table);
    TableRowAssert tableRowAssert = tableAssert.row(1);
    
    assertThat(tableRowAssert).isEqualTo(tableRowAssert.row(0).row());
    assertThat(tableRowAssert).isEqualTo(tableRowAssert.row(1));
  }
}
