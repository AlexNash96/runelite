/*
 * Copyright (c) 2025, GitHub Copilot
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.itemidentification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for ItemIdentification plugin's exclusion feature.
 * Tests the config option for excluding specific items from being identified/labeled.
 */
public class ItemIdentificationExclusionTest
{
	private ItemIdentificationConfig config;
	private ItemIdentificationExclusionHelper helper;

	@Before
	public void setUp()
	{
		config = mock(ItemIdentificationConfig.class);
		helper = new ItemIdentificationExclusionHelper(config);
	}

	/**
	 * Test that no items are excluded when the exclusion list is empty.
	 */
	@Test
	public void testNoExclusionWithEmptyList()
	{
		when(config.excludedItems()).thenReturn("");

		ItemIdentification item = ItemIdentification.GUAM_SEED;
		assertFalse("Item should not be excluded when list is empty", 
			helper.isItemExcluded(item));
	}

	/**
	 * Test that no items are excluded when the exclusion config is null.
	 */
	@Test
	public void testNoExclusionWithNull()
	{
		when(config.excludedItems()).thenReturn(null);

		ItemIdentification item = ItemIdentification.GUAM_SEED;
		assertFalse("Item should not be excluded when config is null", 
			helper.isItemExcluded(item));
	}

	/**
	 * Test that an item is excluded when it matches an entry in the exclusion list.
	 */
	@Test
	public void testSingleItemExclusion()
	{
		when(config.excludedItems()).thenReturn("guam");

		ItemIdentification item = ItemIdentification.GUAM_SEED;
		assertTrue("Item should be excluded when in exclusion list", 
			helper.isItemExcluded(item));
	}

	/**
	 * Test that exclusion matching is case-insensitive.
	 */
	@Test
	public void testCaseInsensitiveMatching()
	{
		ItemIdentification item = ItemIdentification.GUAM_SEED;

		// Test uppercase
		when(config.excludedItems()).thenReturn("GUAM");
		assertTrue("Uppercase item name should match lowercase in list", 
			helper.isItemExcluded(item));

		// Test mixed case
		when(config.excludedItems()).thenReturn("GuAm");
		assertTrue("Mixed case item name should match", 
			helper.isItemExcluded(item));

		// Test lowercase
		when(config.excludedItems()).thenReturn("guam");
		assertTrue("Lowercase item name should match", 
			helper.isItemExcluded(item));
	}

	/**
	 * Test that whitespace is trimmed from exclusion entries.
	 */
	@Test
	public void testWhitespaceTrimming()
	{
		when(config.excludedItems()).thenReturn("  guam  ,  ranarr  ");

		ItemIdentification guamItem = ItemIdentification.GUAM_SEED;
		ItemIdentification ranarrItem = ItemIdentification.RANARR_SEED;

		assertTrue("Guam with surrounding spaces should be excluded", 
			helper.isItemExcluded(guamItem));
		assertTrue("Ranarr with surrounding spaces should be excluded", 
			helper.isItemExcluded(ranarrItem));
	}

	/**
	 * Test that multiple comma-separated items can be excluded.
	 */
	@Test
	public void testMultipleItemExclusion()
	{
		when(config.excludedItems()).thenReturn("guam,ranarr,toadflax");

		assertTrue("First item should be excluded", 
			helper.isItemExcluded(ItemIdentification.GUAM_SEED));
		assertTrue("Second item should be excluded", 
			helper.isItemExcluded(ItemIdentification.RANARR_SEED));
		assertTrue("Third item should be excluded", 
			helper.isItemExcluded(ItemIdentification.TOADFLAX_SEED));
	}

	/**
	 * Test that items not in the exclusion list are not excluded.
	 */
	@Test
	public void testNonExcludedItem()
	{
		when(config.excludedItems()).thenReturn("guam,ranarr");

		ItemIdentification item = ItemIdentification.MARRENTILL_SEED;
		assertFalse("Item not in exclusion list should not be excluded", 
			helper.isItemExcluded(item));
	}

	/**
	 * Test that partial matches don't cause false positives.
	 */
	@Test
	public void testNoPartialMatching()
	{
		when(config.excludedItems()).thenReturn("gua");

		ItemIdentification item = ItemIdentification.GUAM_SEED;
		assertFalse("Partial match should not exclude item", 
			helper.isItemExcluded(item));
	}

	/**
	 * Test exclusion with commas and spaces in various combinations.
	 */
	@Test
	public void testComplexFormatting()
	{
		// Mix of spaces and commas
		when(config.excludedItems()).thenReturn("guam , ranarr, toadflax ,  tarro");

		assertTrue("Guam should be excluded", 
			helper.isItemExcluded(ItemIdentification.GUAM_SEED));
		assertTrue("Ranarr should be excluded", 
			helper.isItemExcluded(ItemIdentification.RANARR_SEED));
		assertTrue("Toadflax should be excluded", 
			helper.isItemExcluded(ItemIdentification.TOADFLAX_SEED));
		assertTrue("Tarro should be excluded", 
			helper.isItemExcluded(ItemIdentification.TARROMIN_SEED));
	}

	/**
	 * Test that the exclusion list handles items from different categories.
	 */
	@Test
	public void testExclusionAcrossCategories()
	{
		when(config.excludedItems()).thenReturn("guam,redberry,oak");

		assertTrue("Herb seed should be excluded", 
			helper.isItemExcluded(ItemIdentification.GUAM_SEED));
		assertTrue("Berry seed should be excluded", 
			helper.isItemExcluded(ItemIdentification.REDBERRY_SEED));
		assertTrue("Tree seed should be excluded", 
			helper.isItemExcluded(ItemIdentification.ACORN));
	}

	/**
	 * Test that trailing commas don't cause issues.
	 */
	@Test
	public void testTrailingComma()
	{
		when(config.excludedItems()).thenReturn("guam,ranarr,");

		assertTrue("Item should be excluded despite trailing comma", 
			helper.isItemExcluded(ItemIdentification.GUAM_SEED));
		// Empty entry after trailing comma should not crash
		assertEquals("Exclusion check should handle trailing comma", 
			true, helper.isItemExcluded(ItemIdentification.GUAM_SEED));
	}

	/**
	 * Test that leading/trailing whitespace in the entire config is handled.
	 */
	@Test
	public void testConfigLevelWhitespaceTrimming()
	{
		when(config.excludedItems()).thenReturn("   guam,ranarr   ");

		ItemIdentification guamItem = ItemIdentification.GUAM_SEED;
		assertTrue("Config-level whitespace should be trimmed", 
			helper.isItemExcluded(guamItem));
	}

	/**
	 * Test that very long exclusion lists are handled correctly.
	 */
	@Test
	public void testLargeExclusionList()
	{
		// Create a comma-separated list of many items
		StringBuilder list = new StringBuilder();
		for (int i = 0; i < 50; i++)
		{
			if (i > 0) list.append(",");
			list.append("item").append(i);
		}
		list.append(",guam"); // Add guam at the end

		when(config.excludedItems()).thenReturn(list.toString());

		assertTrue("Item should be found in large exclusion list", 
			helper.isItemExcluded(ItemIdentification.GUAM_SEED));
	}

	/**
	 * Test that items with medName variations are excluded correctly.
	 * medName is what gets converted to fullName for comparison.
	 */
	@Test
	public void testExclusionByMediumName()
	{
		when(config.excludedItems()).thenReturn("redberry");

		// REDBERRY_SEED has medName "Redberry"
		assertTrue("Item should be excluded by medName", 
			helper.isItemExcluded(ItemIdentification.REDBERRY_SEED));
	}

	/**
	 * Test that an item with multiple ItemIDs is excluded correctly.
	 * Some items map to multiple game item IDs but are a single ItemIdentification entry.
	 */
	@Test
	public void testExclusionForMultiIdItem()
	{
		// Most enchanted jewellery items have multiple charges/IDs
		when(config.excludedItems()).thenReturn("glory");

		// Test that any variant of the item is excluded
		assertTrue("Item with multiple IDs should be excluded", 
			helper.isItemExcluded(ItemIdentification.AMULET_OF_GLORY));
	}

	/**
	 * Test case where exclusion list contains empty entries (e.g., "guam,,ranarr").
	 */
	@Test
	public void testHandleEmptyEntries()
	{
		when(config.excludedItems()).thenReturn("guam,,ranarr");

		assertTrue("First item should be excluded", 
			helper.isItemExcluded(ItemIdentification.GUAM_SEED));
		assertTrue("Second item should be excluded despite empty entry", 
			helper.isItemExcluded(ItemIdentification.RANARR_SEED));
		// Empty entry ("") should not crash the function
	}
}
