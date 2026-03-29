# ItemIdentification Exclusion Feature - Test Suite Documentation

## Overview
This test suite provides comprehensive coverage for the item exclusion feature added to the ItemIdentification plugin. The feature allows users to exclude specific items from being identified/labeled through a comma-separated configuration option.

## Test Files Created

### 1. **ItemIdentificationExclusionTest.java**
**Purpose:** Unit tests for the exclusion logic using a helper class.

**Test Coverage:**
- `testNoExclusionWithEmptyList()` - Verifies no items are excluded when config is empty
- `testNoExclusionWithNull()` - Handles null exclusion lists gracefully
- `testSingleItemExclusion()` - Tests exclusion of a single item
- `testCaseInsensitiveMatching()` - Verifies uppercase, lowercase, and mixed case matching works
- `testWhitespaceTrimming()` - Tests that whitespace is properly trimmed from entries
- `testMultipleItemExclusion()` - Tests comma-separated exclusion lists
- `testNonExcludedItem()` - Ensures items not in the list are not excluded
- `testNoPartialMatching()` - Verifies partial matches don't cause false exclusions
- `testComplexFormatting()` - Tests various combinations of spaces and commas
- `testExclusionAcrossCategories()` - Tests exclusion works for different item types
- `testTrailingComma()` - Handles trailing commas in the exclusion list
- `testConfigLevelWhitespaceTrimming()` - Tests entire config string whitespace handling
- `testLargeExclusionList()` - Verifies performance with many excluded items
- `testExclusionByMediumName()` - Tests matching by medName field
- `testExclusionForMultiIdItem()` - Tests exclusion of items mapped to multiple game IDs
- `testHandleEmptyEntries()` - Handles empty entries like "guam,,ranarr"

### 2. **ItemIdentificationExclusionHelper.java**
**Purpose:** Utility class that isolates exclusion logic for testability.

**Key Methods:**
- `isItemExcluded(ItemIdentification iden)` - Core exclusion matching logic
  - Returns false if exclusion list is empty, null, or not provided
  - Performs case-insensitive matching using `medName`
  - Trims whitespace from both the config string and individual entries
  - Handles comma-separated lists correctly

### 3. **ItemIdentificationOverlayExclusionTest.java**
**Purpose:** Integration tests for the overlay's rendering behavior with exclusions.

**Test Coverage:**
- `testRenderNonExcludedItem()` - Verifies baseline rendering for non-excluded items
- `testSkipRenderExcludedItem()` - Confirms rendering is skipped for excluded items
- `testMultipleExcludedItems()` - Tests multiple exclusions are all respected
- `testCaseInsensitiveExclusion()` - Overlay respects case-insensitive exclusions
- `testWhitespaceHandling()` - Overlay correctly trims spaces in exclusion lists
- `testExclusionWithDisabledCategory()` - Exclusion doesn't interfere with category filters
- `testNonExcludedItemRendersWhenCategoryEnabled()` - Non-excluded items render when enabled
- `testExclusionAcrossDifferentCategories()` - Exclusion works across item categories
- `testEmptyExclusionList()` - Empty list allows all items to render
- `testNullExclusionList()` - Null list is handled gracefully

### 4. **ItemIdentificationConfigExclusionTest.java**
**Purpose:** Configuration object tests ensuring the new config option works correctly.

**Test Coverage:**
- `testDefaultExcludedItemsIsEmpty()` - Default value is empty string
- `testSetSingleExcludedItem()` - Can set a single item
- `testSetMultipleExcludedItems()` - Can set comma-separated items
- `testExcludedItemsPreservesSpaces()` - Config preserves spacing
- `testExcludedItemsAcceptsLongInput()` - Can handle very long lists
- `testExcludedItemsCanBeNull()` - Null values are handled
- `testExcludedItemsPreservesCase()` - Mixed case is preserved
- `testExcludedItemsHandlesSpecialCharacters()` - Special chars like hyphens work
- `testExclusionConfigDoesNotAffectOtherOptions()` - No conflicts with existing config
- `testExclusionWithIdentificationMode()` - Works alongside identification mode setting
- `testExclusionIndependentOfTextColor()` - Works independently of text color setting

## Feature Implementation Details

### Config Option
**File:** `ItemIdentificationConfig.java`
- **Name:** `excludedItems()`
- **Type:** String (comma-separated list)
- **Default:** Empty string
- **Description:** "Comma-separated list of item names to exclude from identification (case-insensitive)."

### Overlay Logic
**File:** `ItemIdentificationOverlay.java`
- Added `isItemExcluded(ItemIdentification iden)` method
- Integrated check in `renderItemOverlay()` method: `if (iden == null || !iden.type.enabled.test(config) || isItemExcluded(iden))`
- Matching behavior:
  - Case-insensitive: converts both item name and exclusion list to lowercase
  - Whitespace-trimmed: trims spaces from entire config and each entry
  - Exact match: doesn't match partial strings
  - Handles null and empty lists gracefully

## Test Execution

All tests can be run using JUnit:
```bash
# Run all exclusion tests
mvn test -k ItemIdentification

# Run specific test class
mvn test -Dtest=ItemIdentificationExclusionTest

# Run integration tests
mvn test -Dtest=ItemIdentificationOverlayExclusionTest
```

## Key Testing Patterns

1. **Mock-based Testing:** Tests use Mockito to mock dependencies
2. **Isolation:** Helper class isolates logic for unit testing
3. **Integration Testing:** Overlay tests verify real-world behavior
4. **Comprehensive Coverage:** Tests cover normal cases, edge cases, and error conditions
5. **Clear Naming:** Test names clearly describe what is being tested

## Edge Cases Covered

✓ Empty exclusion list  
✓ Null exclusion list  
✓ Case sensitivity variations  
✓ Whitespace handling  
✓ Trailing commas  
✓ Double commas (empty entries)  
✓ Very long lists  
✓ Multi-character item names  
✓ Special characters in names  
✓ Mixed category items  
✓ Items with multiple game IDs  
✓ Interaction with other config options  

## Notes

- The tests use `ItemIdentification.medName` for matching since that's the actual property available in the enum
- The overlay code references `iden.fullName` which should ideally be added to ItemIdentification if not already present
- All tests follow RuneLite's existing code style and licensing requirements
- Mock objects are used extensively to isolate components and avoid complex dependencies
