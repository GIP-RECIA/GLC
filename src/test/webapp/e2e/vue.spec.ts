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

import assert from 'node:assert'
import { expect, test } from '@playwright/test'
import 'dotenv/config'

const { TEST_URL, TEST_LOGIN, TEST_PASSWORD } = process.env
assert(TEST_URL && TEST_LOGIN && TEST_PASSWORD)

test.beforeEach(async ({ context, page }) => {
  await page.goto(TEST_URL)
  const pagePromise = context.waitForEvent('page')
  const page1 = await pagePromise
  await page1.locator('#portalCASLoginLink').first().click()
  await page1.waitForURL(/\/cas\/login/)
  await page1.locator('.autres-publics').first().click()
  await page1.locator('input[name="username"]').fill(TEST_LOGIN)
  await page1.locator('#password').fill(TEST_PASSWORD)
  await page1.locator('button[name="submit"]').click()
  await page1.waitForURL(/\/portail/)
  await page1.close()
  await page.locator('#signIn').first().click()
})

test('visits the app root url', async () => {
  await expect(true).toBe(true)
})
