import { test, expect } from '@playwright/test';

test('Startseite laedt', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByRole('link', { name: 'List Students' })).toBeVisible();
  await expect(page.getByRole('link', { name: 'Add Students' })).toBeVisible();
});

test('Die Liste zeigt die Studenten', async ({ page }) => {
  await page.goto('/students');
  const tabelle = page.locator('table');
  await expect(tabelle).toContainText('Jonas');
  await expect(tabelle).toContainText('Patrick');
  await expect(tabelle).toContainText('Yves');
  await expect(tabelle).toContainText('Peter');
  await expect(tabelle).toContainText('Ann');
});

test('Die E-Mail ist ein Link', async ({ page }) => {
  await page.goto('/students');
  const link = page.getByRole('link', { name: 'jonas@tbz.ch' });
  await expect(link).toHaveAttribute('href', 'mailto:jonas@tbz.ch');
});

test('Der Knopf ist gesperrt wenn das Formular leer ist', async ({ page }) => {
  await page.goto('/addstudents');
  await expect(page.locator('button')).toBeDisabled();
});

test('Ein neuer Student wird angelegt', async ({ page }) => {
  const name = 'Test' + Date.now();
  await page.goto('/addstudents');
  await page.locator('#name').fill(name);
  await page.locator('#email').fill('test@tbz.ch');
  await page.locator('button').click();
  await expect(page.locator('table')).toContainText(name);
});