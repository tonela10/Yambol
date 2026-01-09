Plan: Firestore Migration Step 1

Define Firestore data models and repository contracts with userId and timestamps so future layers can switch from Room to Firestore cleanly.

Steps
1) Review current placeholders in app/src/main/java/com/sedilant/yambol/data/firestore/FireStoreModels.kt and align fields to the plan (team/player/task/train/concept) including userId, createdAt, updatedAt, optional localId.
2) Decide timestamp types (e.g., Timestamp vs Long) and date/time fields for trains/tasks; document the chosen convention in plan-migrateFirestore.prompt.md for consistency.
3) Add Firestore DTOs per collection (team, player, task, train, concept) with defaults for Firestore deserialization and validation hints (required vs optional fields).
4) Define repository interfaces under app/src/main/java/com/sedilant/yambol/data/firestore/ for each collection with CRUD/query signatures scoped to userId (list by user, upsert, batch fetch by ids).
5) Add collection name/constants and a base interface/helper for attaching userId/timestamps (e.g., FirestoreEntityMeta) to keep implementations consistent.

Further Considerations
- Prefer Timestamp (server-set) or client Long millis for createdAt/updatedAt? Option A: server FieldValue.serverTimestamp; Option B: client millis.

