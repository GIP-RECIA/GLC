/*
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.glc.configuration.bean.validator;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddressString;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

public class IpAddressValidator implements ConstraintValidator<IpAddress, Object> {
  @Override
  public void initialize(IpAddress constraintAnnotation) {
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext cvContext) {
    if (value instanceof Collection) {
      for (Object val : (Collection<?>) value) {
        if (!(val instanceof String) || !isIpAddressValid((String) val)) return false;
      }
      return true;
    } else if (value instanceof String) {
      return isIpAddressValid((String) value);
    }
    return false;
  }

  private boolean isIpAddressValid(String value) {
    try {
      new IPAddressString(value).toAddress();
    } catch (AddressStringException e) {
      return false;
    }
    return true;
  }
}
