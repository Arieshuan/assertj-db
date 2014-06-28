package org.assertj.db.type;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.db.common.AbstractTest;
import org.junit.Test;

/**
 * Tests on the columns of {@code Request}.
 * <p>
 * These tests are on the values in the columns got from a {@code Request}.
 * </p>
 * 
 * @author Régis Pouiller
 * 
 */
public class Request_Columns_Test extends AbstractTest {

  /**
   * This method test the columns got from a {@code Source}.
   */
  @Test
  public void test_columns_with_source_set() {
    Request request = new Request(source, "SELECT actor.name, actor.firstname, movie.year, interpretation.character "
        + " FROM movie, actor, interpretation"
        + " WHERE movie.id = interpretation.id_movie"
        + " AND interpretation.id_actor = actor.id"
        + " ORDER BY actor.name, movie.year");

    Column columnFromIndex = request.getColumn(1);

    assertThat(columnFromIndex.getName()).isEqualTo("FIRSTNAME");
    assertThat(columnFromIndex.getValuesList()).containsExactly("Joaquim", "Sigourney", "Sigourney", "Sigourney", "Sam");
    assertThat(columnFromIndex.getRowValue(4)).isEqualTo("Sam");
  }

  /**
   * This method test the columns got from a {@code DataSource}.
   */
  @Test
  public void test_columns_with_datasource_set() {
    Request request = new Request(dataSource, "SELECT actor.name, actor.firstname, movie.year, interpretation.character "
        + " FROM movie, actor, interpretation"
        + " WHERE movie.id = interpretation.id_movie"
        + " AND interpretation.id_actor = actor.id"
        + " ORDER BY actor.name, movie.year");

    Column columnFromIndex = request.getColumn(1);

    assertThat(columnFromIndex.getName()).isEqualTo("FIRSTNAME");
    assertThat(columnFromIndex.getValuesList()).containsExactly("Joaquim", "Sigourney", "Sigourney", "Sigourney", "Sam");
    assertThat(columnFromIndex.getRowValue(4)).isEqualTo("Sam");
  }

}
