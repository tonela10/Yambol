# Training Tab Pager AI README

This document explains the implementation details for the Training tab redesign.

## Goal

Redesign the Training tab to:
- Use a pager with 2 text tabs: Sessions and Tasks.
- Keep filters only in the Sessions page.
- Keep current team persistence behavior for Sessions via `DataStoreManager`.
- Show Tasks in a separate page without team/date filters.
- Resolve task concept IDs into concept names before UI rendering.
- Hide missing concepts (no fallback chip text).
- Keep FAB behavior unchanged.

## Final UX Behavior

### Tabs and pager
- Top area has a text `TabRow` with:
  - Sessions
  - Tasks
- Content uses `HorizontalPager`.

### Sessions page
- Shows `Filters` section with:
  - Team selector (`TeamTabs`)
  - Date selector (`DateFilterSection`)
- Shows sessions list below (`TrainingList`).
- Team change persists in `DataStoreManager` exactly as before.

### Tasks page
- Does not render filter controls.
- Shows all tasks for the signed-in user.
- Uses elevated cards with:
  - Task name
  - Concept chips (names only)
  - Optional description
- If no tasks exist, shows an empty-state card with icon + helper text.

### Task details route
- Tapping a task card in the Tasks page navigates to a dedicated Task screen.
- Route uses `YambolScreen.TaskDetails(taskId: String)`.
- Screen supports:
  - Detail view (name, description, concepts, variables)
  - Edit mode for name, description, and variables
  - Save action to persist changes

### FAB
- Stays visible and unchanged for both tabs.
- Keeps existing action: navigate to create training with current team id.

## Architecture Changes

## 1) New use case for concept-name resolution

Added:
- `app/src/main/java/com/sedilant/yambol/domain/get/GetTaskConceptNameUseCase.kt`
- `app/src/main/java/com/sedilant/yambol/domain/get/GetTaskConceptNameUseCaseImpl.kt`

Behavior:
- Input: list of concept IDs used by tasks.
- Uses authenticated user id + `ConceptRepository.getConceptsByIds(...)`.
- Returns `Map<conceptId, conceptName>`.
- Drops invalid/blank names.
- Missing IDs are not returned, so UI naturally hides those chips.

## 2) Dependency injection

Updated:
- `app/src/main/java/com/sedilant/yambol/domain/di/UseCaseModule.kt`

Added bind:
- `GetTaskConceptNameUseCaseImpl -> GetTaskConceptNameUseCase`

## 3) ViewModel state extension

Updated:
- `app/src/main/java/com/sedilant/yambol/ui/training/TrainingViewModel.kt`

Changes:
- Injects:
  - `GetAllTaskUseCase`
  - `GetTaskConceptNameUseCase`
- Builds a tasks flow where concept IDs are transformed into concept names.
- Extends `TrainingUiState.Success` with `taskList`.
- Keeps existing team persistence behavior untouched.

## 4) UI redesign for Training screen

Updated:
- `app/src/main/java/com/sedilant/yambol/ui/training/TrainingScreen.kt`

Changes:
- Added `TabRow` and `HorizontalPager`.
- Sessions tab/page:
  - Renders Filters + Sessions list.
  - Applies team/date filters.
- Tasks tab/page:
  - Renders Tasks list only.
  - No filters rendered.

## 5) New tasks list composable

Added:
- `app/src/main/java/com/sedilant/yambol/ui/training/composables/TrainingTaskList.kt`

Contains:
- Elevated task cards.
- Concept chips (names only).
- Empty-state card with icon and helper text.

## 6) Localization updates

Updated:
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`

Added keys:
- `tasks`
- `filters`
- `no_tasks_tab_title`
- `no_tasks_tab_message`

## 7) Task details and edit flow

Added:
- `app/src/main/java/com/sedilant/yambol/ui/task/TaskScreen.kt`
- `app/src/main/java/com/sedilant/yambol/domain/get/GetTaskByIdUseCase.kt`
- `app/src/main/java/com/sedilant/yambol/domain/get/GetTaskByIdUseCaseImpl.kt`
- `app/src/main/java/com/sedilant/yambol/domain/UpdateTaskUseCase.kt`
- `app/src/main/java/com/sedilant/yambol/domain/UpdateTaskUseCaseImpl.kt`

Updated:
- `app/src/main/java/com/sedilant/yambol/YambolApp.kt` (new `TaskDetails` route)
- `app/src/main/java/com/sedilant/yambol/ui/training/TrainingScreen.kt` (task click navigation callback)
- `app/src/main/java/com/sedilant/yambol/ui/training/composables/TrainingTaskList.kt` (clickable cards)
- `app/src/main/java/com/sedilant/yambol/domain/di/UseCaseModule.kt` (new bindings)

Notes:
- Task concepts are rendered using resolved names.
- Missing concept IDs are not rendered as chips.
- Filters remain visible only in Sessions page.

## Edge cases and rules

- If user is not authenticated, concept-name map is empty and no concept chips are shown.
- If a concept id no longer exists, its chip is hidden.
- Filters are intentionally not shown on Tasks page to avoid confusion.

## Quick validation checklist

- Open Training tab and verify text tabs are visible.
- Switch tabs by tapping and by swiping pager.
- Sessions page shows Filters section and filtered sessions list.
- Tasks page does not show team/date filters.
- Task concept chips display names, not IDs.
- Missing concept IDs do not render chips.
- FAB still opens create-training flow.
- Team selection persists when returning to Training tab.

## Suggested future improvements

- Localize date filter labels currently hardcoded in `DateFilter` enum.
- Add loading/error UI for Training screen states.
- Consider caching concept name maps in memory for large task sets.

