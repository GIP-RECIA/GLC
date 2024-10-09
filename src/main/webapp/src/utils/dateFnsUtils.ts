/**
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
import { type Duration, intervalToDuration, isAfter, isBefore } from 'date-fns'

type dateType = string | number | Date

function dateToDuration(date: dateType): Duration {
  return intervalToDuration({
    start: date,
    end: Date.now(),
  })
}

const isBeforeOrEqual = (date: dateType, dateToCompare: dateType): boolean => !isAfter(date, dateToCompare)

const isAfterOrEqual = (date: dateType, dateToCompare: dateType): boolean => !isBefore(date, dateToCompare)

function isBetween(
  date: dateType,
  dateToCompareMin: dateType,
  dateToCompareMax: dateType,
  include: boolean = true,
): boolean {
  return include
    ? isAfterOrEqual(date, dateToCompareMin) && isBeforeOrEqual(date, dateToCompareMax)
    : isAfter(date, dateToCompareMin) && isBefore(date, dateToCompareMax)
}

export { dateToDuration, isAfterOrEqual, isBeforeOrEqual, isBetween }
