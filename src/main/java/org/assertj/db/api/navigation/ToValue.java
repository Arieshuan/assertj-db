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
package org.assertj.db.api.navigation;

/**
 * Defines methods to navigate to a value.
 * <p>The different methods return an assertion on one value {@link org.assertj.db.api.navigation.ValueAssert}.</p>
 *
 * @author Régis Pouiller
 *
 * @param <V> The class of a assertion on a value (an sub-class of {@link org.assertj.db.api.navigation.ValueAssert}).
 */
public interface ToValue<V extends ValueAssert> {

  /**
   * Returns assertion methods on the next value in the list of values.
   *
   * @return An object to make assertions on the next value.
   * @throws org.assertj.db.exception.AssertJDBException If the {@code index} is out of the bounds.
   */
  public V value();

  /**
   * Returns assertion methods on the value at the {@code index} in parameter.
   *
   * @param index The index corresponding to the value.
   * @return An object to make assertions on the value.
   * @throws org.assertj.db.exception.AssertJDBException If the {@code index} is out of the bounds.
   */
  public V value(int index);
}
