# Firestore Migration Plan

## Goals
- Move teams/players/tasks/trains/concepts to Firestore, owned by the logged-in user.
- Keep UX simple; offline supported via Firestore cache.
- Decide what stays local (TrainingDraft) vs cloud.

## Data Model (per user)
- Collections: teams, players, tasks, trains, concepts (all with `userId`, `createdAt`, `updatedAt`).
- Fields:
  - team: name, userId
  - player: name, number, teamId, userId
  - task: name, description, variables, conceptIds, userId
  - train: date, time, teamId, taskIds, conceptIds, userId
  - concept: name, teamId, userId
- Optional `localId` during migration to map old Room IDs.
- Timestamps: use Firestore `Timestamp` for `createdAt`/`updatedAt`; set on upsert with `Timestamp.now()` and allow server overrides later if needed.

## Security Rules
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    function isOwner(userId) { return request.auth != null && request.auth.uid == userId; }
    match /{c}/{id} {
      allow create: if isOwner(request.resource.data.userId);
      allow read, update, delete: if isOwner(resource.data.userId);
    }
  }
}
```

## Indexes (composites)
- trains: userId Asc, teamId Asc; userId Asc, createdAt Desc
- tasks: userId Asc, createdAt Desc
- teams: userId Asc, name Asc
- Add others per query patterns.

## Strategy
- Online-first with Firestore offline cache (default). Last-write-wins via `updatedAt`.
- Migration: read existing Room data → write to Firestore with `userId`; mark migrated to avoid duplicates.
- Trim Room usage to drafts only; future removal once stable.

## Repositories/Use-Cases
- Implement Firestore repos for teams/players/tasks/trains/concepts. **Status: models + repos added with Timestamp and user scoping.**
- Update use-cases to read/write Firestore; deprecate Room paths for these entities.
- Keep TrainingDraft local; on final save:
  - For new tasks: create task in Firestore then link to train.
  - For reused tasks: only link existing taskId to train (no duplicate task creation).
  - Clear draft after save.

## UX/Auth
- If logged in: all saves go to Firestore; offline queued by SDK.
- If not logged in: allow draft; prompt sign-in before final save (or support anonymous upgrade later).

## Rollout Steps
1) Add Firestore models + repos with `userId`/timestamps. **DONE**
2) Add rules and indexes.
3) Update use-cases and UI to use Firestore data; disable/guard Room paths.
4) Build migration job: push Room data → Firestore for current user; flag migrated.
5) Adjust create-train save to reuse tasks when selected; keep drafts local.
6) Remove/limit legacy Room tables once migration verified.
