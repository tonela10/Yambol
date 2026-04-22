# KMP Dependency Audit — Yambol

> Generated as part of Phase 0 prep. Cross-reference with [klibs.io](https://klibs.io) for KMP library discovery.

## Quick Summary

| Status | Count |
|--------|-------|
| Already KMP-compatible | 3 |
| Replaceable with KMP equivalent | 9 |
| Android-only (move to `:androidApp` source set) | 18 |
| Needs evaluation / no clear replacement yet | 2 |

---

## Dependencies by Status

### ✅ Already KMP-Compatible (keep as-is)

| Artifact | Version | Notes |
|----------|---------|-------|
| `org.jetbrains.kotlinx:kotlinx-serialization-json` | 1.9.0 | Native KMP |
| `org.jetbrains.kotlinx:kotlinx-datetime` | 0.6.1 | **Added in Phase 0** — replaces `java.util.Date` / `java.util.Calendar` |
| `com.google.devtools.ksp` (plugin) | 2.3.0 | KMP-compatible annotation processor |

---

### 🔄 Replace with KMP Equivalent

| Current | Version | Replace With | Phase | Notes |
|---------|---------|-------------|-------|-------|
| `com.google.dagger:hilt-android` + compiler | 2.57.2 | **Koin** (`io.insert-koin:koin-core`) | Phase 1 | Hilt is Android-only. Koin has stable KMP support. Single focused branch — clear `/build` before first compile. |
| `junit:junit` | 4.13.2 | **`kotlin.test`** | Phase 1 | Mechanically straightforward. Keep `androidx.test:junit` for instrumented tests only. |
| `com.google.firebase:firebase-auth-ktx` | BOM 32.5.0 | **GitLive `dev.gitlive:firebase-auth`** | Phase 1 | Includes removing `firebase-ui-auth` and building custom login UI. |
| `com.google.firebase:firebase-firestore-ktx` | BOM 32.5.0 | **GitLive `dev.gitlive:firebase-firestore`** | Phase 1 | |
| `com.google.firebase:firebase-analytics-ktx` | BOM 32.5.0 | No KMP equivalent | Phase 6 | Keep Android-only; use `expect/actual` stub for other platforms. |
| `com.firebaseui:firebase-ui-auth` | 9.1.1 | **Remove** (build custom login UI) | Phase 1 | No KMP equivalent exists. |
| `androidx.room:room-ktx` + compiler | 2.8.3 | **Room KMP** (same artifact, KMP mode) | Phase 2 | Per Android official guide — use `expect/actual` for database builders + `androidx.sqlite:sqlite-bundled`. |
| `androidx.datastore:datastore-preferences` | 1.1.7 | **`androidx.datastore:datastore-core`** (KMP) | Phase 2 | KMP variant available. |
| `org.jetbrains.kotlin.android` (plugin) | 2.2.21 | `org.jetbrains.kotlin.multiplatform` | Phase 1 | Switch when converting modules. |

---

### 📱 Android-Only — Move to `:androidApp` Source Set

These stay in the project but will live only in the Android-specific source set after modularisation. No replacement needed — they are valid in `androidMain`.

| Artifact | Version | Why Android-Only |
|----------|---------|-----------------|
| `androidx.core:core-ktx` | 1.17.0 | Android framework wrapper |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.9.4 | Android Lifecycle |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | 2.9.4 | Android ViewModel |
| `androidx.activity:activity-compose` | 1.11.0 | Android Activity |
| `androidx.navigation:navigation-compose` | 2.9.5 | No stable KMP navigation; evaluate Voyager or Decompose in Phase 3 |
| `androidx.hilt:hilt-navigation-compose` | 1.3.0 | Removed when Hilt → Koin (Phase 1) |
| `androidx.credentials:credentials` | 1.5.0 | Android credential manager |
| `androidx.credentials:credentials-play-services-auth` | 1.5.0 | Play Services auth |
| `com.google.android.gms:play-services-auth` | 21.5.0 | Google Play Services |
| `com.google.android.libraries.identity.googleid:googleid` | 1.1.1 | Google Identity |
| `androidx.media3:media3-common-ktx` | 1.8.0 | Android Media3 |
| `androidx.compose.ui:ui-text-google-fonts` | 1.9.4 | Android-only Google Fonts provider |
| `androidx.compose.*` (BOM 2025.10.01) | — | Migrate to **Compose Multiplatform** BOM in Phase 3 |
| `androidx.compose.material3` | 1.5.0-alpha09 | → `org.jetbrains.compose.material3` in Phase 3 |
| `androidx.compose.ui:ui-test-junit4` | BOM | Android instrumented test |
| `androidx.compose.ui:ui-test-manifest` | BOM | Android instrumented test |
| `androidx.test.ext:junit` | 1.3.0 | Android instrumented test |
| `androidx.test.espresso:espresso-core` | 3.7.0 | Android UI test |

---

### ⚠️ Needs Evaluation

| Artifact | Version | Issue | Recommendation |
|----------|---------|-------|---------------|
| `sh.calvin.reorderable:reorderable` | 3.0.0 | No KMP version; drag-and-drop is platform-specific | Evaluate `compose-reorderable` KMP fork or implement platform `expect/actual` in Phase 3 |
| `com.google.gms.google-services` (plugin) | 4.4.4 | Firebase-specific Android plugin | Keep for Android; excluded from KMP modules |

---

## Build Plugin Changes Required

| Current Plugin | KMP Replacement | When |
|---------------|----------------|------|
| `com.android.application` | `com.android.application` (stays in `:androidApp`) | Phase 1 |
| `org.jetbrains.kotlin.android` | `org.jetbrains.kotlin.multiplatform` | Phase 1 (per module) |
| `org.jetbrains.kotlin.plugin.compose` | `org.jetbrains.compose` (Compose Multiplatform) | Phase 3 |
| `com.google.dagger.hilt.android` | Remove | Phase 1 |

---

## Phase-by-Phase Migration Checklist

### Phase 0 (now) ✅
- [x] Add `kotlinx-datetime` — replaces `java.util.Date` / `java.util.Calendar`
- [x] Consolidate two Room databases into one `YambolDatabase`
- [x] Create module skeleton

### Phase 1 — Foundations
- [ ] Replace Hilt with Koin across entire app
- [ ] Replace `firebase-ui-auth` with custom login UI
- [ ] Migrate Firebase to GitLive KMP SDK (Auth + Firestore)
- [ ] Replace `junit` with `kotlin.test`
- [ ] Convert `:core:domain` to KMP module (`kotlin.multiplatform` plugin)
- [ ] Set up Sentry KMP (replaces Crashlytics)

### Phase 2 — commonMain
- [ ] Convert `:core:data` to KMP module
- [ ] Set up Room KMP (`expect/actual` for database builders)
- [ ] Migrate DataStore to `datastore-core` KMP

### Phase 3 — Compose Multiplatform
- [ ] Replace `androidx.compose.*` BOM with Compose Multiplatform
- [ ] Replace `androidx.navigation:navigation-compose` with KMP navigation (Voyager / Decompose)
- [ ] Move `strings.xml` + `plurals.xml` to `composeResources`
- [ ] Evaluate `reorderable` KMP replacement

### Phase 4 — iOS
- [ ] Add iOS target to KMP modules
- [ ] Set up Sentry Cocoa + dSYM upload

### Phase 5 — Features
- [ ] Move feature screens into `:feature:*` modules

### Phase 6 — Launch
- [ ] Release signing + keystore backup
- [ ] Privacy policy + terms of service
- [ ] Design and finalize Yambol logo + brand colors
- [ ] Account deletion flow (required by both stores)
- [ ] AdMob setup (v1.2)

---

## Resources

- KMP library discovery: [klibs.io](https://klibs.io)
- Room KMP guide: [Android official guide](https://developer.android.com/training/data-storage/room/room-kmp)
- GitLive Firebase KMP: [github.com/GitLiveApp/firebase-kotlin-sdk](https://github.com/GitLiveApp/firebase-kotlin-sdk)
- Koin KMP: [insert-koin.io](https://insert-koin.io/docs/setup/koin)
- Compose Multiplatform: [jb.gg/compose](https://jb.gg/compose)
