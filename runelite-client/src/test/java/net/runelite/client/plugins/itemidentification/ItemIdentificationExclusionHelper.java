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

/**
 * Helper class to test the item exclusion logic in isolation.
 * This class encapsulates the exclusion matching logic from ItemIdentificationOverlay
 * to make it testable without requiring graphics or overlay infrastructure.
 */
public class ItemIdentificationExclusionHelper
{
	private final ItemIdentificationConfig config;

	public ItemIdentificationExclusionHelper(ItemIdentificationConfig config)
	{
		this.config = config;
	}

	/**
	 * Determines whether an item should be excluded from identification display.
	 * Matching is case-insensitive and whitespace-trimmed.
	 *
	 * @param iden The ItemIdentification to check
	 * @return true if the item is in the exclusion list, false otherwise
	 */
	public boolean isItemExcluded(ItemIdentification iden)
	{
		String excludedItems = config.excludedItems();
		if (excludedItems == null)
		{
			return false;
		}

		excludedItems = excludedItems.trim();
		if (excludedItems.isEmpty())
		{
			return false;
		}

		// Use medName as the fullName for comparison since fullName may not be explicitly set
		String itemName = iden.medName.toLowerCase();
		String[] excludedList = excludedItems.toLowerCase().split(",");

		for (String excluded : excludedList)
		{
			String trimmedExcluded = excluded.trim();
			if (!trimmedExcluded.isEmpty() && itemName.equals(trimmedExcluded))
			{
				return true;
			}
		}

		return false;
	}
}
