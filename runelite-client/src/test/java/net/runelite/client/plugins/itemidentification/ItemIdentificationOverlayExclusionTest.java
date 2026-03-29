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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration tests for ItemIdentificationOverlay's exclusion feature.
 * Tests the overlay's renderItemOverlay method with various exclusion configurations.
 */
public class ItemIdentificationOverlayExclusionTest
{
	private ItemIdentificationOverlay overlay;
	private ItemIdentificationConfig config;
	private ItemManager itemManager;
	private Graphics2D graphics;
	private WidgetItem widgetItem;

	@Before
	public void setUp()
	{
		config = mock(ItemIdentificationConfig.class);
		itemManager = mock(ItemManager.class);
		graphics = mock(Graphics2D.class);
		widgetItem = mock(WidgetItem.class);

		// Setup default config values
		when(config.identificationType()).thenReturn(ItemIdentificationMode.SHORT);
		when(config.textColor()).thenCallRealMethod();
		when(config.excludedItems()).thenReturn("");

		// Create overlay with mocked dependencies
		overlay = new ItemIdentificationOverlay(config, itemManager);
	}

	/**
	 * Test that overlay renders text for non-excluded items.
	 * This verifies the baseline rendering behavior works.
	 */
	@Test
	public void testRenderNonExcludedItem()
	{
		when(itemManager.canonicalize(ItemID.GUAM_SEED)).thenReturn(ItemID.GUAM_SEED);
		when(config.showHerbSeeds()).thenReturn(true);
		when(widgetItem.getCanvasBounds()).thenReturn(new Rectangle(100, 100, 32, 32));

		// This should call render since item is not excluded
		overlay.renderItemOverlay(graphics, ItemID.GUAM_SEED, widgetItem);

		// Verify that graphics methods were called (indicating text was rendered)
		verify(graphics).setFont(any());
	}

	/**
	 * Test that overlay skips rendering for excluded items.
	 */
	@Test
	public void testSkipRenderExcludedItem()
	{
		when(itemManager.canonicalize(ItemID.GUAM_SEED)).thenReturn(ItemID.GUAM_SEED);
		when(config.showHerbSeeds()).thenReturn(true);
		when(config.excludedItems()).thenReturn("guam");
		when(widgetItem.getCanvasBounds()).thenReturn(new Rectangle(100, 100, 32, 32));

		overlay.renderItemOverlay(graphics, ItemID.GUAM_SEED, widgetItem);

		// Verify that graphics rendering was NOT called (no setFont call)
		verify(graphics, times(0)).setFont(any());
	}

	/**
	 * Test that overlay handles multiple excluded items correctly.
	 */
	@Test
	public void testMultipleExcludedItems()
	{
		when(config.excludedItems()).thenReturn("guam,ranarr,toadflax");
		when(config.showHerbSeeds()).thenReturn(true);
		when(widgetItem.getCanvasBounds()).thenReturn(new Rectangle(100, 100, 32, 32));

		// Test that guam is excluded
		when(itemManager.canonicalize(ItemID.GUAM_SEED)).thenReturn(ItemID.GUAM_SEED);
		overlay.renderItemOverlay(graphics, ItemID.GUAM_SEED, widgetItem);
		verify(graphics, times(0)).setFont(any());

		// Test that ranarr is excluded
		when(itemManager.canonicalize(ItemID.RANARR_SEED)).thenReturn(ItemID.RANARR_SEED);
		overlay.renderItemOverlay(graphics, ItemID.RANARR_SEED, widgetItem);
		// Should still be 0 (from guam check)
		verify(graphics, times(0)).setFont(any());
	}

	/**
	 * Test that overlay respects case-insensitive exclusion.
	 */
	@Test
	public void testCaseInsensitiveExclusion()
	{
		when(itemManager.canonicalize(ItemID.GUAM_SEED)).thenReturn(ItemID.GUAM_SEED);
		when(config.showHerbSeeds()).thenReturn(true);
		when(config.excludedItems()).thenReturn("GUAM"); // uppercase
		when(widgetItem.getCanvasBounds()).thenReturn(new Rectangle(100, 100, 32, 32));

		overlay.renderItemOverlay(graphics, ItemID.GUAM_SEED, widgetItem);

		// Should NOT render because exclusion is case-insensitive
		verify(graphics, times(0)).setFont(any());
	}

	/**
	 * Test that overlay handles whitespace correctly in exclusion list.
	 */
	@Test
	public void testWhitespaceHandling()
	{
		when(itemManager.canonicalize(ItemID.GUAM_SEED)).thenReturn(ItemID.GUAM_SEED);
		when(config.showHerbSeeds()).thenReturn(true);
		when(config.excludedItems()).thenReturn("  guam  ,  ranarr  "); // with spaces
		when(widgetItem.getCanvasBounds()).thenReturn(new Rectangle(100, 100, 32, 32));

		overlay.renderItemOverlay(graphics, ItemID.GUAM_SEED, widgetItem);

		// Should NOT render despite spaces in the config
		verify(graphics, times(0)).setFont(any());
	}

	/**
	 * Test that excluded item feature doesn't affect category filtering.
	 * Both exclusion and type.enabled check should apply.
	 */
	@Test
	public void testExclusionWithDisabledCategory()
	{
		when(itemManager.canonicalize(ItemID.GUAM_SEED)).thenReturn(ItemID.GUAM_SEED);
		when(config.showHerbSeeds()).thenReturn(false); // Category disabled
		when(config.excludedItems()).thenReturn(""); // No exclusions
		when(widgetItem.getCanvasBounds()).thenReturn(new Rectangle(100, 100, 32, 32));

		overlay.renderItemOverlay(graphics, ItemID.GUAM_SEED, widgetItem);

		// Should NOT render because category is disabled
		verify(graphics, times(0)).setFont(any());
	}

	/**
	 * Test that enabling the category allows non-excluded items to render.
	 */
	@Test
	public void testNonExcludedItemRendersWhenCategoryEnabled()
	{
		when(itemManager.canonicalize(ItemID.MARRENTILL_SEED)).thenReturn(ItemID.MARRENTILL_SEED);
		when(config.showHerbSeeds()).thenReturn(true); // Category enabled
		when(config.excludedItems()).thenReturn("guam"); // Only guam excluded
		when(widgetItem.getCanvasBounds()).thenReturn(new Rectangle(100, 100, 32, 32));

		overlay.renderItemOverlay(graphics, ItemID.MARRENTILL_SEED, widgetItem);

		// Should render because marrentill is not in exclusion list
		verify(graphics, times(1)).setFont(any());
	}

	/**
	 * Test that overlay correctly renders items of different categories.
	 * Tests across seed types to ensure exclusion works across the board.
	 */
	@Test
	public void testExclusionAcrossDifferentCategories()
	{
		when(config.excludedItems()).thenReturn("guam,redberry,oak");
		when(widgetItem.getCanvasBounds()).thenReturn(new Rectangle(100, 100, 32, 32));

		// Test herb seed (guam)
		when(itemManager.canonicalize(ItemID.GUAM_SEED)).thenReturn(ItemID.GUAM_SEED);
		when(config.showHerbSeeds()).thenReturn(true);
		overlay.renderItemOverlay(graphics, ItemID.GUAM_SEED, widgetItem);
		verify(graphics, times(0)).setFont(any());

		// Test berry seed (redberry)
		when(itemManager.canonicalize(ItemID.REDBERRY_BUSH_SEED)).thenReturn(ItemID.REDBERRY_BUSH_SEED);
		when(config.showBerrySeeds()).thenReturn(true);
		overlay.renderItemOverlay(graphics, ItemID.REDBERRY_BUSH_SEED, widgetItem);
		verify(graphics, times(0)).setFont(any());

		// Test tree seed (oak)
		when(itemManager.canonicalize(ItemID.ACORN)).thenReturn(ItemID.ACORN);
		when(config.showTreeSeeds()).thenReturn(true);
		overlay.renderItemOverlay(graphics, ItemID.ACORN, widgetItem);
		verify(graphics, times(0)).setFont(any());
	}

	/**
	 * Test that empty exclusion list works as expected.
	 */
	@Test
	public void testEmptyExclusionList()
	{
		when(itemManager.canonicalize(ItemID.GUAM_SEED)).thenReturn(ItemID.GUAM_SEED);
		when(config.showHerbSeeds()).thenReturn(true);
		when(config.excludedItems()).thenReturn("");
		when(widgetItem.getCanvasBounds()).thenReturn(new Rectangle(100, 100, 32, 32));

		overlay.renderItemOverlay(graphics, ItemID.GUAM_SEED, widgetItem);

		// Should render because no items are excluded
		verify(graphics, times(1)).setFont(any());
	}

	/**
	 * Test that overlay handles null exclusion list gracefully.
	 */
	@Test
	public void testNullExclusionList()
	{
		when(itemManager.canonicalize(ItemID.GUAM_SEED)).thenReturn(ItemID.GUAM_SEED);
		when(config.showHerbSeeds()).thenReturn(true);
		when(config.excludedItems()).thenReturn(null);
		when(widgetItem.getCanvasBounds()).thenReturn(new Rectangle(100, 100, 32, 32));

		overlay.renderItemOverlay(graphics, ItemID.GUAM_SEED, widgetItem);

		// Should render because null is treated as no exclusions
		verify(graphics, times(1)).setFont(any());
	}
}
