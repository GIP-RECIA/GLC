import { expect, test } from '@playwright/test';
import assert from 'assert';
import 'dotenv/config';

const { TEST_URL, TEST_LOGIN, TEST_PASSWORD } = process.env;
assert(TEST_URL && TEST_LOGIN && TEST_PASSWORD);

test.beforeEach(async ({ context, page }) => {
  await page.goto(TEST_URL);
  const pagePromise = context.waitForEvent('page');
  const page1 = await pagePromise;
  await page1.locator('#portalCASLoginLink').first().click();
  await page1.waitForURL(new RegExp(/\/cas\/login/));
  await page1.locator('.autres-publics').first().click();
  await page1.locator('input[name="username"]').fill(TEST_LOGIN);
  await page1.locator('#password').fill(TEST_PASSWORD);
  await page1.locator('button[name="submit"]').click();
  await page1.waitForURL(new RegExp(/\/portail/));
  await page1.close();
  await page.locator('#signIn').first().click();
});

test('visits the app root url', async ({ page }) => {
  await expect(true).toBe(true);
});
