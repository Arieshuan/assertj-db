/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2014 the original author or authors.
 */
package org.assertj.db.api.assertions;

import org.assertj.db.common.AbstractTest;
import org.assertj.db.type.Table;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests on {@code hasNumberOfRows} and {@code hasNumberOfColumns} methods for assertion on {@code Table}.
 * 
 * @author Régis Pouiller
 * 
 */
public class TableAssert_HasSize_Test extends AbstractTest {

  /**
   * This method test the assertion on the rows size.
   */
  @Test
  public void test_rows_size_assertion() {
    Table table = new Table(source, "movie");
    assertThat(table).hasNumberOfRows(3);
  }

  /**
   * This test should fail because the rows size is different (3).
   */
  @Test
  public void should_fail_beacause_rows_size_is_different() {
    try {
      Table table = new Table(source, "movie");
      assertThat(table).hasNumberOfRows(4);
      
      fail("An exception must be raised");
    }
    catch (AssertionError e) {
      assertThat(e.getLocalizedMessage()).isEqualTo("[movie table] \n" +
          "Expecting size (number of rows) to be equal to :\n" +
          "   <4>\n" +
          "but was:\n" +
          "   <3>");
    }
  }

  /**
   * This method test the assertion on the columns size.
   */
  @Test
  public void test_columns_size_assertion() {
    Table table = new Table(source, "movie");
    assertThat(table).hasNumberOfColumns(3);
  }

  /**
   * This test should fail because the columns size is different (3).
   */
  @Test
  public void should_fail_beacause_columns_size_is_different() {
    try {
      Table table = new Table(source, "movie");
      assertThat(table).hasNumberOfColumns(4);
      
      fail("An exception must be raised");
    }
    catch (AssertionError e) {
      assertThat(e.getLocalizedMessage()).isEqualTo("[movie table] \n" +
          "Expecting size (number of columns) to be equal to :\n" +
          "   <4>\n" +
          "but was:\n" +
          "   <3>");
    }
  }
}
