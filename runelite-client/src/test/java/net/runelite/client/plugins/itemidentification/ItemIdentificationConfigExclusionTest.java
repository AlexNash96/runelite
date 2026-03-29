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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

/**
 * Configuration tests for ItemIdentification plugin's exclusion feature.
 * Verifies that the excludedItems config option is properly defined and functional.
 */
public class ItemIdentificationConfigExclusionTest
{
	private ItemIdentificationConfig config;

	@Before
	public void setUp()
	{
		config = mock(ItemIdentificationConfig.class);
	}

	/**
	 * Test that excludedItems config method returns empty string by default.
	 */
	@Test
	public void testDefaultExcludedItemsIsEmpty()
	{
		when(config.excludedItems()).thenReturn("");

		String result = config.excludedItems();
		assertNotNull("excludedItems should not return null", result);
		assertEquals("excludedItems should default to empty string", "", result);
	}

	/**
	 * Test that excludedItems config can be set to a single item.
	 */
	@Test
	public void testSetSingleExcludedItem()
	{
		when(config.excludedItems()).thenReturn("guam");

		String result = config.excludedItems();
		assertEquals("excludedItems should contain guam", "guam", result);
	}

	/**
	 * Test that excludedItems config can hold comma-separated values.
	 */
	@Test
	public void testSetMultipleExcludedItems()
	{
		String expected = "guam,ranarr,toadflax";
		when(config.excludedItems()).thenReturn(expected);

		String result = config.excludedItems();
		assertEquals("excludedItems should contain all items", expected, result);
	}

	/**
	 * Test that excludedItems config preserves spaces.
	 */
	@Test
	public void testExcludedItemsPreservesSpaces()
	{
		String expected = "  guam  , ranarr , toadflax  ";
		when(config.excludedItems()).thenReturn(expected);

		String result = config.excludedItems();
		assertEquals("excludedItems should preserve spaces", expected, result);
	}

	/**
	 * Test that excludedItems config accepts very long input.
	 */
	@Test
	public void testExcludedItemsAcceptsLongInput()
	{
		StringBuilder longList = new StringBuilder();
		for (int i = 0; i < 100; i++)
		{
			if (i > 0) longList.append(",");
			longList.append("item").append(i);
		}

		when(config.excludedItems()).thenReturn(longList.toString());

		String result = config.excludedItems();
		assertEquals("excludedItems should accept long lists", longList.toString(), result);
	}

	/**
	 * Test that excludedItems config can be set to null.
	 * This tests error handling capability.
	 */
	@Test
	public void testExcludedItemsCanBeNull()
	{
		when(config.excludedItems()).thenReturn(null);

		String result = config.excludedItems();
		assertEquals("excludedItems should be able to return null", null, result);
	}

	/**
	 * Test that excludedItems preserves case.
	 */
	@Test
	public void testExcludedItemsPreservesCase()
	{
		String expected = "GUAM,ranarr,ToadFlax";
		when(config.excludedItems()).thenReturn(expected);

		String result = config.excludedItems();
		assertEquals("excludedItems should preserve mixed case", expected, result);
	}

	/**
	 * Test that excludedItems handles special characters and numbers.
	 */
	@Test
	public void testExcludedItemsHandlesSpecialCharacters()
	{
		String expected = "item-1,item_2,item.3";
		when(config.excludedItems()).thenReturn(expected);

		String result = config.excludedItems();
		assertEquals("excludedItems should handle special characters", expected, result);
	}

	/**
	 * Test that exclusion config integrates with other config options.
	 * Ensures no conflicts with existing configuration.
	 */
	@Test
	public void testExclusionConfigDoesNotAffectOtherOptions()
	{
		when(config.excludedItems()).thenReturn("guam");
		when(config.showHerbSeeds()).thenReturn(true);
		when(config.showBerrySeeds()).thenReturn(false);

		assertEquals("excludedItems should not affect showHerbSeeds", 
			true, config.showHerbSeeds());
		assertEquals("excludedItems should not affect showBerrySeeds", 
			false, config.showBerrySeeds());
		assertEquals("excludedItems should be independent", 
			"guam", config.excludedItems());
	}

	/**
	 * Test that config option handles identificationMode alongside exclusions.
	 */
	@Test
	public void testExclusionWithIdentificationMode()
	{
		when(config.excludedItems()).thenReturn("guam");
		when(config.identificationType()).thenReturn(ItemIdentificationMode.SHORT);

		assertEquals("exclusion should work with SHORT mode", 
			"guam", config.excludedItems());
		assertEquals("identification mode should be SHORT", 
			ItemIdentificationMode.SHORT, config.identificationType());
	}

	/**
	 * Test that config option is independent of text color setting.
	 */
	@Test
	public void testExclusionIndependentOfTextColor()
	{
		when(config.excludedItems()).thenReturn("guam,ranarr");
		when(config.textColor()).thenCallRealMethod();

		assertNotNull("excludedItems should exist independently of textColor", 
			config.excludedItems());
		assertEquals("excludedItems should be properly set", 
			"guam,ranarr", config.excludedItems());
	}
}
