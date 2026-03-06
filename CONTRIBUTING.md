# Contributing to Yambol

Thank you for your interest in contributing to Yambol! This document provides guidelines for contributing to the project, with a special focus on internationalization and localization.

## Table of Contents

- [Getting Started](#getting-started)
- [Translation & Localization](#translation--localization)
  - [Adding a New Language](#adding-a-new-language)
  - [String Resources](#string-resources)
  - [Plural Resources](#plural-resources)
  - [Formatted Strings](#formatted-strings)
  - [Date and Time Formatting](#date-and-time-formatting)
- [Code Contributions](#code-contributions)
- [Testing](#testing)

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/yourusername/yambol.git`
3. Create a feature branch: `git checkout -b feature/your-feature-name`
4. Make your changes and commit: `git commit -m "Add your feature"`
5. Push to your fork: `git push origin feature/your-feature-name`
6. Create a Pull Request

## Translation & Localization

Yambol currently supports **English** (default) and **Spanish**. We welcome contributions to add more languages!

### Adding a New Language

To add support for a new language:

1. **Create a new values folder** for your locale:
   ```
   app/src/main/res/values-{locale}/
   ```
   
   Examples:
   - French: `values-fr/`
   - German: `values-de/`
   - Portuguese (Brazil): `values-pt-rBR/`
   - Italian: `values-it/`

2. **Copy the base string files**:
   ```
   cp app/src/main/res/values/strings.xml app/src/main/res/values-{locale}/strings.xml
   cp app/src/main/res/values/plurals.xml app/src/main/res/values-{locale}/plurals.xml
   ```

3. **Translate all string values** while keeping the `name` attributes unchanged:
   ```xml
   <!--  DON'T change the name attribute -->
   <string name="app_name">Yambol</string>
   
   <!--  DO translate the value -->
   <string name="cancel">Annuler</string>  <!-- French -->
   <string name="cancel">Cancelar</string> <!-- Spanish -->
   ```

4. **Test your translations** by changing your device language to the new locale

### String Resources

#### Naming Conventions

- Use lowercase with underscores: `training_details_screen`
- Be descriptive: `error_loading_training` instead of `error1`
- Group related strings with prefixes:
  - `nav_*` for navigation items
  - `error_*` for error messages
  - `label_*` for form labels
  - `button_*` for button text
  - `title_*` for screen titles

#### Example

```xml
<resources>
    <!-- Navigation -->
    <string name="nav_home">Home</string>
    <string name="nav_profile">Profile</string>
    <string name="nav_training">Training</string>
    
    <!-- Error messages -->
    <string name="error_loading_training">Error loading training</string>
    <string name="error_unknown">Unknown error</string>
    
    <!-- Buttons -->
    <string name="button_save">Save</string>
    <string name="button_cancel">Cancel</string>
</resources>
```

### Plural Resources

Use plural resources when displaying countable items to ensure proper grammar in all languages.

#### English (values/plurals.xml)

```xml
<resources>
    <plurals name="teams_count">
        <item quantity="one">%d team</item>
        <item quantity="other">%d teams</item>
    </plurals>
    
    <plurals name="players_count">
        <item quantity="one">%d player</item>
        <item quantity="other">%d players</item>
    </plurals>
</resources>
```

#### Spanish (values-es/plurals.xml)

```xml
<resources>
    <plurals name="teams_count">
        <item quantity="one">%d equipo</item>
        <item quantity="other">%d equipos</item>
    </plurals>
    
    <plurals name="players_count">
        <item quantity="one">%d jugador</item>
        <item quantity="other">%d jugadores</item>
    </plurals>
</resources>
```

#### Usage in Code

```kotlin
// In Composable functions
val text = pluralStringResource(R.plurals.teams_count, count, count)
Text(text = text)

// In ViewModels or other classes
val text = context.resources.getQuantityString(R.plurals.teams_count, count, count)
```

### Formatted Strings

Use placeholders for dynamic content that needs to be inserted into strings.

#### Placeholder Types

- `%s` - String
- `%d` - Integer
- `%f` - Float/Double
- `%1$s`, `%2$s` - Positional arguments (when order might change between languages)

#### Examples

```xml
<!-- Simple placeholder -->
<string name="player_number">Player %d</string>

<!-- Multiple placeholders -->
<string name="progress_text">%d / %d players</string>

<!-- Positional placeholders (recommended for multiple arguments) -->
<string name="step_progress">Step %1$d of %2$d</string>

<!-- String placeholder -->
<string name="variation_label">Variation: %s</string>
```

#### Usage in Code

```kotlin
// In Composable functions
val text = stringResource(R.string.player_number, playerIndex)
val progress = stringResource(R.string.progress_text, current, total)

// Using String.format
val text = getString(R.string.variation_label, variationName)
```

### Date and Time Formatting

Yambol uses a **standardized date format** across the entire application: `"MMM dd, yyyy"` (e.g., "Jan 15, 2024").

#### Guidelines

1. **Always use `Locale.getDefault()`** for SimpleDateFormat:
   ```kotlin
   val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
   val formattedDate = formatter.format(date)
   ```

2. **Never hardcode locales** in date formatters:
   ```kotlin
   //  DON'T do this
   SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
   
   //  DO this
   SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
   ```

3. **Use consistent format** throughout the app for maintainability

## Code Contributions

### Code Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add comments for complex logic
- Keep functions small and focused

### Architecture Guidelines

- Follow Clean Architecture principles
- Use MVVM pattern for UI layer
- Keep business logic in use cases
- Repository pattern for data access

### UI Development

- Use Jetpack Compose for all UI components
- Follow Material Design 3 guidelines
- Always use `stringResource()` for text, never hardcode strings
- Ensure all `contentDescription` attributes are localized

## Testing

### Manual Testing for Translations

1. **Change device language**:
   - Settings → System → Languages & input → Languages
   - Add the language you want to test
   - Move it to the top of the list

2. **Verify translations**:
   - App should display in the new language immediately (no restart needed)
   - Check all screens for proper translations
   - Verify plural forms with different counts
   - Test formatted strings with various inputs
   - Ensure dates and times display correctly

3. **Test edge cases**:
   - Long text strings (some languages are more verbose)
   - RTL languages if supported
   - Special characters and accents

### Automated Testing

- Write unit tests for business logic
- Use Compose testing library for UI tests
- Ensure tests pass before submitting PR

## Pull Request Guidelines

1. **Create descriptive PR titles**: "Add French translation" or "Fix date formatting in TrainingScreen"
2. **Provide context**: Explain what changes were made and why
3. **Reference issues**: Link to related issues using `#issue-number`
4. **Test thoroughly**: Ensure your changes work on different devices and configurations
5. **Keep PRs focused**: One feature or fix per PR

## Questions?

If you have questions about contributing, feel free to:
- Open an issue for discussion
- Ask in pull request comments
- Contact the maintainers

Thank you for helping make Yambol better! 🏀

