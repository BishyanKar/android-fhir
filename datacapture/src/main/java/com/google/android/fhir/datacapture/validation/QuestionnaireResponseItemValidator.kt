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

package com.google.android.fhir.datacapture.validation

import android.content.Context
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireResponseItemValidator {

  private val validators =
    mutableListOf(
      RequiredConstraintValidator,
      MaxValueConstraintValidator,
      MinValueConstraintValidator,
      PrimitiveTypeAnswerMaxLengthValidator,
      PrimitiveTypeAnswerMinLengthValidator,
      RegexValidator
    )

  /** Validates [questionnaireResponseItem] contains valid answer(s) to [questionnaireItem]. */
  fun validate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
    context: Context
  ): ValidationResult {
    val validationResults = mutableListOf<ConstraintValidator.ConstraintValidationResult>()
    validators.forEach {
      validationResults.add(it.validate(questionnaireItem, questionnaireResponseItem, context))
    }
    return ValidationResult(
      validationResults.all { it.isValid },
      validationResults.mapNotNull { it.message }.toList()
    )
  }
}

data class ValidationResult(var isValid: Boolean, val validationMessages: List<String>)

fun ValidationResult.getSingleStringValidationMessage() =
  this.validationMessages.joinToString(separator = "\n")
