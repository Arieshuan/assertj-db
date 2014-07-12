package org.assertj.db.error;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;
import org.assertj.db.api.ValueType;

/**
 * Creates an error message indicating that an assertion that verifies that a value is of any types.
 * 
 * @author Régis Pouiller
 * 
 */
public class ShouldBeTypeOfAny extends BasicErrorMessageFactory {

  /**
   * Creates a new <code>{@link ShouldBeTypeOfAny}</code>.
   * 
   * @param actual The actual value in the failed assertion.
   * @param tested The tested type.
   * @param expected The expected types.
   * @return the created {@code ErrorMessageFactory}.
   */
  public static ErrorMessageFactory shouldBeTypeOfAny(final Object actual, final ValueType tested,
      final ValueType... expected) {

    return new ShouldBeTypeOfAny(actual, tested, expected);
  }

  /**
   * Constructor.
   * 
   * @param actual The actual value in the failed assertion.
   * @param tested The tested type.
   * @param expected The expected types.
   */
  public ShouldBeTypeOfAny(final Object actual, final ValueType tested, final ValueType... expected) {
    super("\nExpecting:\n  <%s>\nto be of type\n" + "  <%s>\nbut was of type\n  <%s>", actual, tested, expected);
  }

}