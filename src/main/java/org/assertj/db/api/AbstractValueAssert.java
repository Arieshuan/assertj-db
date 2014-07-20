package org.assertj.db.api;

import static org.assertj.core.error.ShouldBeEqual.shouldBeEqual;
import static org.assertj.core.error.ShouldNotBeEqual.shouldNotBeEqual;
import static org.assertj.db.error.ShouldBeAfter.shouldBeAfter;
import static org.assertj.db.error.ShouldBeType.shouldBeType;
import static org.assertj.db.error.ShouldBeTypeOfAny.shouldBeTypeOfAny;
import static org.assertj.db.error.ShouldBeBefore.shouldBeBefore;
import static org.assertj.db.util.Values.areEqual;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;

import org.assertj.core.api.Descriptable;
import org.assertj.core.api.WritableAssertionInfo;
import org.assertj.core.description.Description;
import org.assertj.core.internal.Failures;
import org.assertj.core.internal.Objects;
import org.assertj.db.error.AssertJDBException;
import org.assertj.db.type.AbstractDbData;
import org.assertj.db.type.DateTimeValue;
import org.assertj.db.type.DateValue;
import org.assertj.db.type.Row;
import org.assertj.db.type.TimeValue;

/**
 * Assertion methods about the value.
 * 
 * @author Régis Pouiller
 * 
 * @param <S> The class of the original assert (an sub-class of {@link AbstractDbAssert}).
 * @param <E> The class of the actual value (an sub-class of {@link AbstractDbData}).
 * @param <T> The class of which contains assertion methods about {@link Column} or {@link Row} (an sub-class of
 *          {@link AbstractSubAssert}).
 * @param <V> The class of this assert (an sub-class of {@link AbstractValueAssert}).
 */
public abstract class AbstractValueAssert<S extends AbstractDbAssert<E, S>, E extends AbstractDbData<E>, T extends AbstractSubAssert<E, S, T, V>, V extends AbstractValueAssert<S, E, T, V>>
    implements Descriptable<V> {

  /**
   * The original assert.
   */
  private final T originalAssert;
  /**
   * The actual value on which this assertion is.
   */
  private final Object value;
  /**
   * This assertion.
   */
  private final V myself;
  /**
   * The information about the assertion.
   */
  private final WritableAssertionInfo info;

  /**
   * To notice failures in the assertion.
   */
  private static Failures failures = Failures.instance();
  /**
   * Assertions for {@code Object}s provided by assertj-core.
   */
  private Objects objects = Objects.instance();

  /**
   * Constructor.
   * 
   * @param originalAssert The original assert.
   * @param selfType Class of this assert (the value assert) : a sub-class of {@code AbstractValueAssert}.
   * @param actualValue The value to assert.
   */
  AbstractValueAssert(T originalAssert, Class<V> selfType, Object actualValue) {
    myself = (V) selfType.cast(this);
    this.originalAssert = originalAssert;
    this.value = actualValue;
    info = new WritableAssertionInfo();
  }

  /** {@inheritDoc} */
  public V as(String description, Object... args) {
    return describedAs(description, args);
  }

  /** {@inheritDoc} */
  @Override
  public V as(Description description) {
    return describedAs(description);
  }

  /** {@inheritDoc} */
  @Override
  public V describedAs(String description, Object... args) {
    info.description(description, args);
    return myself;
  }

  /** {@inheritDoc} */
  @Override
  public V describedAs(Description description) {
    info.description(description);
    return myself;
  }

  /**
   * Returns assertion methods on the next value in the list of value of the original assertion.
   * 
   * @return An object to make assertions on the next value.
   * @throws AssertJDBException If the {@code index} is out of the bounds.
   */
  public V value() {
    return returnToSubAssert().value();
  }

  /**
   * Returns assertion methods on the value at the {@code index} in parameter in the list of value of the original
   * assertion.
   * 
   * @param index The index corresponding to the value.
   * @return An object to make assertions on the value.
   * @throws AssertJDBException If the {@code index} is out of the bounds.
   */
  public V value(int index) {
    return returnToSubAssert().value(index);
  }

  /**
   * Returns the original assertion (an instance of a sub-class of {@link AbstractSubAssert}.
   * 
   * @return The original assertion.
   */
  protected T returnToSubAssert() {
    return originalAssert;
  }

  /**
   * Returns the type of the value (text, boolean, number, date, ...).
   * 
   * @return The type of the value.
   */
  protected ValueType getType() {
    return ValueType.getType(value);
  }

  /**
   * Verifies that the type of the value is equal to the type in parameter.
   * <p>
   * Example where the assertion verifies that the value in the {@code Column} called "title" of the second {@code Row}
   * of the {@code Table} is of type {@code TEXT} :
   * </p>
   * 
   * <pre>
   * assertThat(table).row(1).value(&quot;title&quot;).isOfType(ValueType.TEXT);
   * </pre>
   * 
   * @param expected The expected type to compare to.
   * @return {@code this} assertion object.
   * @throws AssertionError If the type is different to the type in parameter.
   */
  public V isOfType(ValueType expected) {
    ValueType type = getType();
    if (type != expected) {
      throw failures.failure(info, shouldBeType(value, expected, type));
    }
    return myself;
  }

  /**
   * Verifies that the type of the value is equal to one of the types in parameters.
   * <p>
   * Example where the assertion verifies that the value in the {@code Column} called "title" of the second {@code Row}
   * of the {@code Table} is of type {@code TEXT} or of type {@code NUMBER} :
   * </p>
   * 
   * <pre>
   * assertThat(table).row(1).value(&quot;title&quot;).isOfType(ValueType.TEXT, ValueType.NUMBER);
   * </pre>
   * 
   * @param expected The expected types to compare to.
   * @return {@code this} assertion object.
   * @throws AssertionError If the type is different to all the types in parameters.
   */
  public V isOfAnyOfTypes(ValueType... expected) {
    ValueType type = getType();
    for (ValueType valueType : expected) {
      if (type == valueType) {
        return myself;
      }
    }
    throw failures.failure(info, shouldBeTypeOfAny(value, type, expected));
  }

  /**
   * Verifies that the value is a number.
   * <p>
   * Example where the assertion verifies that the value in the {@code Column} called "year" of the first {@code Row} of
   * the {@code Table} is a number :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value(&quot;year&quot;).isNumber();
   * </pre>
   * 
   * @return {@code this} assertion object.
   * @throws AssertionError If the type is not a number.
   */
  public V isNumber() {
    return isOfType(ValueType.NUMBER);
  }

  /**
   * Verifies that the value is a boolean.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is a boolean :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isBoolean();
   * </pre>
   * 
   * @return {@code this} assertion object.
   * @throws AssertionError If the type is not a number.
   */
  public V isBoolean() {
    return isOfType(ValueType.BOOLEAN);
  }

  /**
   * Verifies that the value is a date.
   * <p>
   * Example where the assertion verifies that the value in the {@code Column} called "birth" of the first {@code Row}
   * of the {@code Table} is a date :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value(&quot;birth&quot;).isDate();
   * </pre>
   * 
   * @return {@code this} assertion object.
   * @throws AssertionError If the type is not a number.
   */
  public V isDate() {
    return isOfType(ValueType.DATE);
  }

  /**
   * Verifies that the value is a time.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is a time :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isTime();
   * </pre>
   * 
   * @return {@code this} assertion object.
   * @throws AssertionError If the type is not a number.
   */
  public V isTime() {
    return isOfType(ValueType.TIME);
  }

  /**
   * Verifies that the value is a date/time.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is a date/time :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isDateTime();
   * </pre>
   * 
   * @return {@code this} assertion object.
   * @throws AssertionError If the type is not a number.
   */
  public V isDateTime() {
    return isOfType(ValueType.DATE_TIME);
  }

  /**
   * Verifies that the value is a array of bytes.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is a array of bytes :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isBytes();
   * </pre>
   * 
   * @return {@code this} assertion object.
   * @throws AssertionError If the type is not a number.
   */
  public V isBytes() {
    return isOfType(ValueType.BYTES);
  }

  /**
   * Verifies that the value is a text.
   * <p>
   * Example where the assertion verifies that the value in the {@code Column} called "title" of the first {@code Row}
   * of the {@code Table} is a text :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value(&quot;title&quot;).isText();
   * </pre>
   * 
   * @return {@code this} assertion object.
   * @throws AssertionError If the type is not a number.
   */
  public V isText() {
    return isOfType(ValueType.TEXT);
  }

  /**
   * Verifies that the value is a array of bytes.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is null :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isNull();
   * </pre>
   * 
   * @return {@code this} assertion object.
   * @throws AssertionError If the type is not a number.
   */
  public V isNull() {
    objects.assertNull(info, value);
    return myself;
  }

  /**
   * Verifies that the value is a array of bytes.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is not null :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isNotNull();
   * </pre>
   * 
   * @return {@code this} assertion object.
   * @throws AssertionError If the type is not a number.
   */
  public V isNotNull() {
    objects.assertNotNull(info, value);
    return myself;
  }

  /**
   * Verifies that the value is equal to a boolean.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is equal to true boolean :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isEqualTo(true);
   * </pre>
   * 
   * @param expected The expected boolean value.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not equal to the boolean in parameter.
   */
  public V isEqualTo(Boolean expected) {
    isBoolean();
    if (areEqual(value, expected)) {
      return myself;
    }
    throw failures.failure(info, shouldBeEqual(value, expected, info.representation()));
  }

  /**
   * Verifies that the value is equal to true boolean.
   * <p>
   * Example with the value in the first {@code Column} of the first {@code Row} of the {@code Table} :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isTrue();
   * </pre>
   * 
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not equal to true boolean.
   */
  public V isTrue() {
    return isEqualTo(true);
  }

  /**
   * Verifies that the value is equal to false boolean.
   * <p>
   * Example with the value in the first {@code Column} of the first {@code Row} of the {@code Table} :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isFalse();
   * </pre>
   * 
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not equal to false boolean.
   */
  public V isFalse() {
    return isEqualTo(false);
  }

  /**
   * Verifies that the value is equal to a number.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is equal to number 3 :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isEqualTo(3);
   * </pre>
   * 
   * @param expected The expected number value.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not equal to the number in parameter.
   */
  public V isEqualTo(Number expected) {
    isNumber();
    if (areEqual(value, expected)) {
      return myself;
    }
    throw failures.failure(info, shouldBeEqual(value, expected, info.representation()));
  }

  /**
   * Verifies that the value is equal to a array of bytes.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is equal to a array of bytes loaded from a file in the classpath :
   * </p>
   * 
   * <pre>
   * byte[] bytes = bytesContentFromClassPathOf(&quot;file.png&quot;);
   * assertThat(table).row().value().isEqualTo(bytes);
   * </pre>
   * 
   * @param expected The expected array of bytes value.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not equal to the array of bytes in parameter.
   */
  public V isEqualTo(byte[] expected) {
    isBytes();
    if (areEqual(value, expected)) {
      return myself;
    }
    throw failures.failure(info, shouldBeEqual(value, expected, info.representation()));
  }

  /**
   * Verifies that the value is equal to a text.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is equal to a text :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isEqualTo(&quot;text&quot;);
   * </pre>
   * 
   * @param expected The expected text value.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not equal to the text in parameter.
   */
  public V isEqualTo(String expected) {
    isOfAnyOfTypes(ValueType.TEXT, ValueType.NUMBER, ValueType.DATE, ValueType.TIME, ValueType.DATE_TIME);
    if (areEqual(value, expected)) {
      return myself;
    }
    throw failures.failure(info, shouldBeEqual(value, expected, info.representation()));
  }

  /**
   * Verifies that the value is equal to a date value.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is equal to a date value :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isEqualTo(DateValue.of(2014, 7, 7));
   * </pre>
   * 
   * @param expected The expected date value.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not equal to the date value in parameter.
   */
  public V isEqualTo(DateValue expected) {
    isOfAnyOfTypes(ValueType.DATE, ValueType.DATE_TIME);
    if (areEqual(value, expected)) {
      return myself;
    }
    if (getType() == ValueType.DATE) {
      throw failures.failure(info, shouldBeEqual(DateValue.from((Date) value), expected, info.representation()));
    }
    throw failures.failure(info,
        shouldBeEqual(DateTimeValue.from((Timestamp) value), DateTimeValue.of(expected), info.representation()));
  }

  /**
   * Verifies that the value is equal to a time value.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is equal to a time value :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isEqualTo(TimeValue.of(21, 29, 30));
   * </pre>
   * 
   * @param expected The expected time value.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not equal to the time value in parameter.
   */
  public V isEqualTo(TimeValue expected) {
    isTime();
    if (areEqual(value, expected)) {
      return myself;
    }
    throw failures.failure(info, shouldBeEqual(TimeValue.from((Time) value), expected, info.representation()));
  }

  /**
   * Verifies that the value is equal to a date/time value.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is equal to a date/time value :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isEqualTo(DateTimeValue.of(DateValue.of(2014, 7, 7), TimeValue.of(21, 29)));
   * </pre>
   * 
   * @param expected The expected date/time value.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not equal to the date/time value in parameter.
   */
  public V isEqualTo(DateTimeValue expected) {
    isDateTime();
    if (areEqual(value, expected)) {
      return myself;
    }
    throw failures.failure(info, shouldBeEqual(DateTimeValue.from((Timestamp) value), expected, info.representation()));
  }

  /**
   * Verifies that the value is not equal to a boolean.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is not equal to true boolean :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isNotEqualTo(true);
   * </pre>
   * 
   * @param expected The expected boolean value.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is equal to the boolean in parameter.
   */
  public V isNotEqualTo(Boolean expected) {
    isBoolean();
    if (!areEqual(value, expected)) {
      return myself;
    }
    throw failures.failure(info, shouldNotBeEqual(value, expected));
  }

  /**
   * Verifies that the value is not equal to a array of bytes.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is not equal to a array of bytes loaded from a file in the classpath :
   * </p>
   * 
   * <pre>
   * byte[] bytes = bytesContentFromClassPathOf(&quot;file.png&quot;);
   * assertThat(table).row().value().isNotEqualTo(bytes);
   * </pre>
   * 
   * @param expected The expected array of bytes value.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is equal to the array of bytes in parameter.
   */
  public V isNotEqualTo(byte[] expected) {
    isBytes();
    if (!areEqual(value, expected)) {
      return myself;
    }
    throw failures.failure(info, shouldNotBeEqual(value, expected));
  }

  /**
   * Verifies that the value is not equal to a date/time value.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is not equal to a date/time value :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isNotEqualTo(DateTimeValue.of(DateValue.of(2014, 7, 7), TimeValue.of(21, 29)));
   * </pre>
   * 
   * @param expected The expected date/time value.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is equal to the date/time value in parameter.
   */
  public V isNotEqualTo(DateTimeValue expected) {
    isDateTime();
    if (!areEqual(value, expected)) {
      return myself;
    }
    throw failures.failure(info, shouldNotBeEqual(DateTimeValue.from((Timestamp) value), expected));
  }

  /**
   * Verifies that the value is not equal to a date value.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is not equal to a date value :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isNotEqualTo(DateValue.of(2014, 7, 7));
   * </pre>
   * 
   * @param expected The expected date value.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is equal to the date value in parameter.
   */
  public V isNotEqualTo(DateValue expected) {
    isOfAnyOfTypes(ValueType.DATE, ValueType.DATE_TIME);
    if (!areEqual(value, expected)) {
      return myself;
    }
    if (getType() == ValueType.DATE) {
      throw failures.failure(info, shouldNotBeEqual(DateValue.from((Date) value), expected));
    }
    throw failures.failure(info, shouldNotBeEqual(DateTimeValue.from((Timestamp) value), DateTimeValue.of(expected)));
  }

  /**
   * Verifies that the value is not equal to a number.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is not equal to number 3 :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isNotEqualTo(3);
   * </pre>
   * 
   * @param expected The expected number value.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is equal to the number in parameter.
   */
  public V isNotEqualTo(Number expected) {
    isNumber();
    if (!areEqual(value, expected)) {
      return myself;
    }
    throw failures.failure(info, shouldNotBeEqual(value, expected));
  }

  /**
   * Verifies that the value is not equal to a text.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is not equal to a text :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isNotEqualTo(&quot;text&quot;);
   * </pre>
   * 
   * @param expected The expected text value.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is equal to the text in parameter.
   */
  public V isNotEqualTo(String expected) {
    isOfAnyOfTypes(ValueType.TEXT, ValueType.NUMBER, ValueType.DATE, ValueType.TIME, ValueType.DATE_TIME);
    if (!areEqual(value, expected)) {
      return myself;
    }
    throw failures.failure(info, shouldNotBeEqual(value, expected));
  }

  /**
   * Verifies that the value is not equal to a time value.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is not equal to a time value :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isNotEqualTo(TimeValue.of(21, 29, 30));
   * </pre>
   * 
   * @param expected The expected time value.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is equal to the time value in parameter.
   */
  public V isNotEqualTo(TimeValue expected) {
    isTime();
    if (!areEqual(value, expected)) {
      return myself;
    }
    throw failures.failure(info, shouldNotBeEqual(TimeValue.from((Time) value), expected));
  }

  /**
   * Verifies that the value is before a date value.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is before a date value :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isBefore(DateValue.of(2007, 12, 23));
   * </pre>
   * 
   * @param date The date value to compare to.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not before to the time value in parameter.
   */
  public V isBefore(DateValue date) {
    isOfAnyOfTypes(ValueType.DATE, ValueType.DATE_TIME);
    if (value instanceof Date) {
      if (DateValue.from((Date) value).isBefore(date)) {
        return myself;
      }
      throw failures.failure(info, shouldBeBefore(DateValue.from((Date) value), date));
    } else {
      DateTimeValue dateTimeValue = DateTimeValue.of(date);
      if (DateTimeValue.from((Timestamp) value).isBefore(dateTimeValue)) {
        return myself;
      }
      throw failures.failure(info, shouldBeBefore(DateTimeValue.from((Timestamp) value), dateTimeValue));
    }
  }

  /**
   * Verifies that the value is before a time value.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is before a time value :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isBefore(TimeValue.of(2007, 12, 23));
   * </pre>
   * 
   * @param time The time value to compare to.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not before to the time value in parameter.
   */
  public V isBefore(TimeValue time) {
    isTime();
    if (TimeValue.from((Time) value).isBefore(time)) {
      return myself;
    }
    throw failures.failure(info, shouldBeBefore(TimeValue.from((Time) value), time));
  }

  /**
   * Verifies that the value is before a date/time value.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is before a date/time value :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isBefore(DateTimeValue.of(DateValue.of(2007, 12, 23), TimeValue.of(21, 29)));
   * </pre>
   * 
   * @param dateTime The date/time value to compare to.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not before to the date/time value in parameter.
   */
  public V isBefore(DateTimeValue dateTime) {
    isOfAnyOfTypes(ValueType.DATE, ValueType.DATE_TIME);
    DateTimeValue dateTimeValue;
    if (value instanceof Date) {
      dateTimeValue = DateTimeValue.of(DateValue.from((Date) value));
    } else {
      dateTimeValue = DateTimeValue.from((Timestamp) value);
    }
    if (dateTimeValue.isBefore(dateTime)) {
      return myself;
    }
    throw failures.failure(info, shouldBeBefore(DateTimeValue.from((Timestamp) value), dateTime));
  }

  /**
   * Verifies that the value is before a date, time or date/time represented by a {@code String}.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is before a date represented by a {@code String} :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isBefore(&quot;2007-12-23&quot;);
   * </pre>
   * 
   * @param expected The {@code String} representing a date, time or date/time to compare to.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not before the date, time or date/time represented in parameter.
   */
  public V isBefore(String expected) {
    isOfAnyOfTypes(ValueType.DATE, ValueType.TIME, ValueType.DATE_TIME);

    // By considering the possible types, the class of the value is
    // java.sql.Date, java.sql.Time or java.sql.Timestamp

    // If the class is java.sql.Time then comparison by using TimeValue
    if (value instanceof Time) {
      TimeValue timeValue = TimeValue.from((Time) value);
      try {
        TimeValue expectedTimeValue = TimeValue.parse(expected);
        if (timeValue.isBefore(expectedTimeValue)) {
          return myself;
        }
        throw failures.failure(info, shouldBeBefore(timeValue, expectedTimeValue));
      } catch (ParseException e) {
        throw new AssertJDBException("Expected <%s> is not correct to compare to <%s>", expected, timeValue);
      }
    }

    // In the other case then comparison by using DateTimeValue
    DateTimeValue dateTimeValue;
    if (value instanceof Date) {
      dateTimeValue = DateTimeValue.of(DateValue.from((Date) value));
    } else {
      dateTimeValue = DateTimeValue.from((Timestamp) value);
    }

    try {
      DateTimeValue expectedDateTimeValue = DateTimeValue.parse(expected);
      if (dateTimeValue.isBefore(expectedDateTimeValue)) {
        return myself;
      }
      throw failures.failure(info, shouldBeBefore(dateTimeValue, expectedDateTimeValue));
    } catch (ParseException e) {
      throw new AssertJDBException("Expected <%s> is not correct to compare to <%s>", expected, dateTimeValue);
    }
  }

  /**
   * Verifies that the value is after a date value.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is after a date value :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isAfter(DateValue.of(2007, 12, 23));
   * </pre>
   * 
   * @param date The date value to compare to.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not after to the time value in parameter.
   */
  public V isAfter(DateValue date) {
    isOfAnyOfTypes(ValueType.DATE, ValueType.DATE_TIME);
    if (value instanceof Date) {
      if (DateValue.from((Date) value).isAfter(date)) {
        return myself;
      }
      throw failures.failure(info, shouldBeAfter(DateValue.from((Date) value), date));
    } else {
      DateTimeValue dateTimeValue = DateTimeValue.of(date);
      if (DateTimeValue.from((Timestamp) value).isAfter(dateTimeValue)) {
        return myself;
      }
      throw failures.failure(info, shouldBeAfter(dateTimeValue, dateTimeValue));
    }
  }

  /**
   * Verifies that the value is after a time value.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is after a time value :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isAfter(TimeValue.of(2007, 12, 23));
   * </pre>
   * 
   * @param time The time value to compare to.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not after to the time value in parameter.
   */
  public V isAfter(TimeValue time) {
    isTime();
    if (TimeValue.from((Time) value).isAfter(time)) {
      return myself;
    }
    throw failures.failure(info, shouldBeAfter(TimeValue.from((Time) value), time));
  }

  /**
   * Verifies that the value is after a date/time value.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is after a date/time value :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isAfter(DateTimeValue.of(DateValue.of(2007, 12, 23), TimeValue.of(21, 29)));
   * </pre>
   * 
   * @param dateTime The date/time value to compare to.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not after to the date/time value in parameter.
   */
  public V isAfter(DateTimeValue dateTime) {
    isOfAnyOfTypes(ValueType.DATE, ValueType.DATE_TIME);
    DateTimeValue dateTimeValue;
    if (value instanceof Date) {
      dateTimeValue = DateTimeValue.of(DateValue.from((Date) value));
    } else {
      dateTimeValue = DateTimeValue.from((Timestamp) value);
    }
    if (dateTimeValue.isAfter(dateTime)) {
      return myself;
    }
    throw failures.failure(info, shouldBeAfter(dateTimeValue, dateTime));
  }

  /**
   * Verifies that the value is after a date, time or date/time represented by a {@code String}.
   * <p>
   * Example where the assertion verifies that the value in the first {@code Column} of the first {@code Row} of the
   * {@code Table} is after a date represented by a {@code String} :
   * </p>
   * 
   * <pre>
   * assertThat(table).row().value().isAfter(&quot;2007-12-23&quot;);
   * </pre>
   * 
   * @param expected The {@code String} representing a date, time or date/time to compare to.
   * @return {@code this} assertion object.
   * @throws AssertionError If the value is not after the date, time or date/time represented in parameter.
   */
  public V isAfter(String expected) {
    isOfAnyOfTypes(ValueType.DATE, ValueType.TIME, ValueType.DATE_TIME);

    // By considering the possible types, the class of the value is
    // java.sql.Date, java.sql.Time or java.sql.Timestamp

    // If the class is java.sql.Time then comparison by using TimeValue
    if (value instanceof Time) {
      TimeValue timeValue = TimeValue.from((Time) value);
      try {
        TimeValue expectedTimeValue = TimeValue.parse(expected);
        if (timeValue.isAfter(expectedTimeValue)) {
          return myself;
        }
        throw failures.failure(info, shouldBeAfter(timeValue, expectedTimeValue));
      } catch (ParseException e) {
        throw new AssertJDBException("Expected <%s> is not correct to compare to <%s>", expected, timeValue);
      }
    }

    // In the other case then comparison by using DateTimeValue
    DateTimeValue dateTimeValue;
    if (value instanceof Date) {
      dateTimeValue = DateTimeValue.of(DateValue.from((Date) value));
    } else {
      dateTimeValue = DateTimeValue.from((Timestamp) value);
    }

    try {
      DateTimeValue expectedDateTimeValue = DateTimeValue.parse(expected);
      if (dateTimeValue.isAfter(expectedDateTimeValue)) {
        return myself;
      }
      throw failures.failure(info, shouldBeAfter(dateTimeValue, expectedDateTimeValue));
    } catch (ParseException e) {
      throw new AssertJDBException("Expected <%s> is not correct to compare to <%s>", expected, dateTimeValue);
    }
  }
}
