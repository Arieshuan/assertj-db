package org.assertj.db.error;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

/**
 * Creates an error message indicating that an assertion that verifies that a value is greater than or equal to another
 * value.
 * 
 * @author Régis Pouiller
 * 
 */
public class ShouldBeGreaterOrEqual extends BasicErrorMessageFactory {

  /**
   * Creates a new <code>{@link ShouldBeGreaterOrEqual}</code>.
   * 
   * @param pActual The actual value in the failed assertion.
   * @param pExpected The expected value to compare to.
   * @return the created {@code ErrorMessageFactory}.
   */
  public static ErrorMessageFactory shouldBeGreaterOrEqual(Object pActual, Object pExpected) {
    return new ShouldBeGreaterOrEqual(pActual, pExpected);
  }

  /**
   * Constructor.
   * 
   * @param pActual The actual value in the failed assertion.
   * @param pExpected The expected value to compare to.
   */
  public ShouldBeGreaterOrEqual(Object pActual, Object pExpected) {
    super("\nExpecting:\n  <%s>\nto be greater than or equal to \n  <%s>", pActual, pExpected);
  }
}
