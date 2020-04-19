// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.fhirengine.resource;

import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;

import java.lang.reflect.InvocationTargetException;

/** Utilities for the HAPI FHIR resources. */
public class ResourceUtils {

  /** The HAPI Fhir package prefix for R4 resources. */
  public static final String R4_RESOURCE_PACKAGE_PREFIX = "org.hl7.fhir.r4.model.";

  /** Returns the FHIR resource type. */
  public static <R extends Resource> ResourceType getResourceType(Class<R> clazz) {
    try {
      return clazz.getConstructor().newInstance().getResourceType();
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("Cannot resolve resource type for " + clazz.getName(), e);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException("Cannot resolve resource type for " + clazz.getName(), e);
    } catch (InstantiationException e) {
      throw new IllegalArgumentException("Cannot resolve resource type for " + clazz.getName(), e);
    } catch (InvocationTargetException e) {
      throw new IllegalArgumentException("Cannot resolve resource type for " + clazz.getName(), e);
    }
  }

  /** Returns the {@link Class} object for the resource type. */
  public static <R extends Resource> Class<R> getResourceClass(String resourceType) {
    try {
      // Remove any curly brackets in the resource type string. This is to work around an issue with
      // JSON deserialization in the CQL engine on Android. The resource type string incorrectly
      // includes namespace prefix in curly brackets, e.g. "{http://hl7.org/fhir}Patient" instead of
      // "Patient".
      // TODO: remove this once a fix has been found for the CQL engine on Android.
      resourceType = resourceType.replaceAll("\\{[^}]*\\}", "");
      return (Class<R>) Class.forName(R4_RESOURCE_PACKAGE_PREFIX + resourceType);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }
}
