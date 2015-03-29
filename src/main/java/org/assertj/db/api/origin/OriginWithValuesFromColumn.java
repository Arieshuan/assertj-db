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
package org.assertj.db.api.origin;

import org.assertj.db.api.ChangeColumnValueAssert;
import org.assertj.db.api.navigation.ToValueFromColumn;

/**
 * Defines a class which is the origin of another and have values from a column of a change.
 *
 * @author Régis Pouiller
 */
public interface OriginWithValuesFromColumn
        extends OriginWithColumnsAndRowsFromChange,
                ToValueFromColumn<ChangeColumnValueAssert> {
}
