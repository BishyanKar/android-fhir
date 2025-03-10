/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.search.filter

import ca.uhn.fhir.rest.gclient.QuantityClientParam
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.search.SearchDslMarker
import com.google.android.fhir.search.SearchQuery
import com.google.android.fhir.search.getConditionParamPair
import java.math.BigDecimal
import org.hl7.fhir.r4.model.ResourceType

/**
 * Represents a criterion for filtering [QuantityClientParam]. e.g.
 * filter(Observation.VALUE_QUANTITY,{value = BigDecimal("5.403")} )
 */
@SearchDslMarker
data class QuantityParamFilterCriterion(
  val parameter: QuantityClientParam,
  var prefix: ParamPrefixEnum? = null,
  var value: BigDecimal? = null,
  var system: String? = null,
  var unit: String? = null
) : FilterCriterion {
  override fun query(type: ResourceType): SearchQuery {
    val conditionParamPair = getConditionParamPair(prefix, value!!, system, unit)
    return SearchQuery(
      """
      SELECT resourceId FROM QuantityIndexEntity
      WHERE resourceType= ? AND index_name = ? 
      AND ${conditionParamPair.condition}
      """.trimIndent(),
      listOfNotNull<Any>(type.name, parameter.paramName) + conditionParamPair.params
    )
  }
}
