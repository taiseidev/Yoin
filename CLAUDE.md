# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Yoin（余韻）** is a travel photo app that brings the "delayed gratification" of film photography to digital travel experiences. Photos are taken during a trip but only "developed" (revealed) on the morning after the trip ends, creating anticipation and sharing memories with travel companions.

## Tech Stack

- **UI**: Compose Multiplatform (Android + iOS)
- **Architecture**: Clean Architecture with KMP
- **Backend**: Supabase (Auth, Database, Storage, Edge Functions)
- **Navigation**: Voyager
- **DI**: Koin
- **Networking**: Ktor Client
- **Database**: SQLDelight (local), Supabase PostgreSQL (remote)

## Build Commands

### Building the Project

```bash
# Clean build
./gradlew clean build

# Build specific module
./gradlew :app:build
./gradlew :core:build
./gradlew :domain:build
./gradlew :data:build
./gradlew :feature:auth:build

# Android debug build
./gradlew :app:assembleDebug

# Install Android debug build on device
./gradlew :app:installDebug
```

### Running Tests

```bash
# All tests
./gradlew allTests

# Android unit tests
./gradlew :shared:testDebugUnitTest

# iOS simulator tests
./gradlew :shared:iosSimulatorArm64Test

# Test specific module
./gradlew :domain:test
./gradlew :data:test
```

### iOS-Specific Builds

```bash
# Compile iOS framework
./gradlew :app:compileKotlinIosArm64

# Build without daemon (for memory issues)
./gradlew build --no-daemon

# Stop gradle daemon
./gradlew --stop
```

### Common Build Issues

If you encounter `OutOfMemoryError` during builds:
- The project has optimized Gradle/Kotlin memory settings in `gradle.properties`
- Use `--no-daemon` flag if needed
- Run `./gradlew --stop` between builds

## Architecture

### Module Structure

The project uses a **4-layer Clean Architecture** with unified core/domain/data modules and separate feature modules:

```
YoinApp/
├── app/                    # Application entry point, navigation
├── core/                   # Unified core (UI components, theme, utils)
├── domain/                 # Unified domain (models, repositories, use cases)
├── data/                   # Unified data (repository implementations)
└── feature/                # Feature modules (independent)
    ├── onboarding/
    ├── auth/
    ├── home/
    ├── room/
    ├── camera/
    ├── timeline/
    ├── map/
    ├── profile/
    ├── settings/
    ├── shop/
    └── notifications/
```

### Dependency Flow

```
app → feature/* → domain → core
         ↓
       data → domain → core
```

**Rules:**
- Features depend on `domain` and `core` (NOT on other features)
- `data` implements interfaces from `domain`
- `domain` contains business logic only (no Android/iOS dependencies)
- `core` contains shared UI components and design system

### Key Architectural Patterns

#### 1. MVI Pattern (Model-View-Intent)

All ViewModels follow the Contract pattern:

```kotlin
// Contract defines State, Event, and Effect
object FeatureContract {
    data class State(...)
    sealed class Event { ... }
    sealed class Effect { ... }
}

// ViewModel implements the contract
class FeatureViewModel : ViewModel() {
    val state: StateFlow<State>
    fun onEvent(event: Event)
}

// Screen observes and renders
@Composable
fun FeatureScreen(viewModel: FeatureViewModel) {
    val state by viewModel.state.collectAsState()
    // Render based on state
}
```

#### 2. Navigation with Voyager

All screens are defined as `Screen` classes in `app/src/commonMain/kotlin/com/yoin/app/navigation/Screens.kt`:

```kotlin
data class FeatureScreenVoyager(val param: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: FeatureViewModel = koinScreenModel()

        FeatureScreen(
            viewModel = viewModel,
            onNavigateBack = { navigator.pop() }
        )
    }
}
```

**Navigation rules:**
- `navigator.push()` - Add to stack
- `navigator.pop()` - Go back one screen
- `navigator.replace()` - Replace current screen
- `navigator.replaceAll()` - Clear stack and navigate

#### 3. Dependency Injection with Koin

All feature modules provide a DI module in `feature/*/di/*Module.kt`:

```kotlin
val featureModule = module {
    factory { FeatureViewModel(get(), get()) }
    factory { SomeUseCase(get()) }
}
```

Register in `app/src/commonMain/kotlin/com/yoin/app/App.kt`:

```kotlin
KoinApplication(
    application = {
        modules(
            dataClientModule,
            localDataModule,
            featureModule,  // Add your module here
            // ...
        )
    }
)
```

## Design System

### Theme Colors

The app uses a **coral/peach color scheme** defined in `core/src/commonMain/kotlin/com/yoin/core/design/theme/YoinTheme.kt`:

- **Primary**: `#FF8B7A` (Coral Pink)
- **Background**: `#FFFBF8` (Off-white)
- **Surface**: `#FFFFFF` (White cards)
- **Accent**: `#FFCCB8` (Peach), `#CBB485` (Gold Beige)

**Usage:**
```kotlin
YoinColors.Primary
YoinColors.Background
YoinColors.TextPrimary
```

### UI Components

Common components in `core/src/commonMain/kotlin/com/yoin/core/ui/component/`:
- `YoinAppBar` - Standard top app bar
- `YoinBottomNavigationBar` - Bottom navigation

### Preview Annotations

Use `@PhonePreview` for consistent previews:

```kotlin
@PhonePreview
@Composable
private fun FeatureScreenPreview() {
    YoinTheme {
        FeatureScreen(...)
    }
}
```

## Backend Integration

### Supabase Configuration

Supabase is initialized in `app/src/commonMain/kotlin/com/yoin/app/App.kt`:

```kotlin
SupabaseConfig.initialize(
    url = "https://...",
    anonKey = "..."
)
```

**Access Supabase client:**
```kotlin
val client = SupabaseClientProvider.getClient()
```

### Repository Pattern

All repositories follow this pattern:

1. **Interface in domain layer**: `domain/src/commonMain/kotlin/com/yoin/domain/*/repository/*Repository.kt`
2. **Implementation in data layer**: `data/src/commonMain/kotlin/com/yoin/data/repository/*RepositoryImpl.kt`

Example:
```kotlin
// domain/
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
}

// data/
class AuthRepositoryImpl(
    private val supabase: SupabaseClient
) : AuthRepository {
    override suspend fun login(...) = runCatching {
        // Supabase implementation
    }
}
```

## Development Guidelines

### Adding a New Feature

1. **Create feature module** if needed: `feature/newfeature/`
2. **Define domain models**: `domain/src/.../newfeature/model/`
3. **Create repository interface**: `domain/src/.../newfeature/repository/`
4. **Implement repository**: `data/src/.../repository/`
5. **Create use cases**: `domain/src/.../newfeature/usecase/`
6. **Create Contract**: `feature/newfeature/viewmodel/FeatureContract.kt`
7. **Create ViewModel**: `feature/newfeature/viewmodel/FeatureViewModel.kt`
8. **Create UI**: `feature/newfeature/ui/FeatureScreen.kt`
9. **Create Screen wrapper**: Add to `app/.../navigation/Screens.kt`
10. **Register DI**: Create `feature/newfeature/di/FeatureModule.kt` and register in `App.kt`

### Working with ViewModels

All ViewModels must:
- Use the Contract pattern (State/Event/Effect)
- Expose `StateFlow<State>` for UI state
- Use `onEvent(event: Event)` for user actions
- Use `LaunchedEffect` in UI for one-time effects

### Material Icons

The project uses **Material Icons Extended** instead of emojis:
- Import: `import androidx.compose.material.icons.Icons`
- Example: `Icons.Filled.Star`, `Icons.Filled.Camera`, `Icons.Filled.Person`

All feature modules have access to `compose.materialIconsExtended`.

### Commit Message Convention

```
feat: New feature
fix: Bug fix
refactor: Code refactoring
design: UI/UX improvements
docs: Documentation updates
test: Test additions/modifications
chore: Build configuration, dependencies
perf: Performance improvements
```

Always end commits with:
```
🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

## Common Patterns

### State Management

```kotlin
// ViewModel
private val _state = MutableStateFlow(State())
val state: StateFlow<State> = _state.asStateFlow()

fun onEvent(event: Event) {
    when (event) {
        is Event.Something -> _state.update {
            it.copy(field = newValue)
        }
    }
}
```

### Loading States

```kotlin
data class State(
    val isLoading: Boolean = false,
    val error: String? = null,
    val data: Data? = null
)
```

### Navigation with Parameters

```kotlin
// Define screen with parameters
data class DetailScreenVoyager(val id: String) : Screen {
    @Composable
    override fun Content() {
        val viewModel: DetailViewModel = koinScreenModel {
            parametersOf(id)  // Pass parameters to ViewModel
        }
        // ...
    }
}

// Navigate with parameters
navigator.push(DetailScreenVoyager(id = "123"))
```

### Koin ViewModel with Parameters

```kotlin
// DI module
val module = module {
    factory { (id: String) ->
        DetailViewModel(id = id, useCase = get())
    }
}

// Screen
val viewModel: DetailViewModel = koinScreenModel {
    parametersOf(id)
}
```

## iOS-Specific Notes

### Framework Export

The `app` module exports all feature modules as an iOS framework:

```kotlin
iosTarget.binaries.framework {
    baseName = "YoinApp"
    export(project(":feature:onboarding"))
    export(project(":feature:auth"))
    // ...
}
```

### expect/actual Pattern

For platform-specific implementations, use expect/actual:

```kotlin
// commonMain
expect class PlatformSpecificClass()

// androidMain
actual class PlatformSpecificClass actual constructor() {
    // Android implementation
}

// iosMain
actual class PlatformSpecificClass actual constructor() {
    // iOS implementation
}
```

## Related Documentation

The project has additional documentation in Obsidian notes:
- Path: `/Users/t-z/workspace/obsidian-notes/08_個人開発/Yoin. - 余韻 -`
- Contains detailed specs, design decisions, and feature planning

Refer to the README.md for:
- Full feature list
- Database schema details
- Setup instructions
- External service configuration (Firebase, Supabase)