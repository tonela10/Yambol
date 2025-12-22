package com.sedilant.yambol.data.firestore

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getTrainsByTeam(teamId: String, onResult: (List<Train>) -> Unit) {
        db.collection("trains")
            .whereEqualTo("teamId", teamId)
            .get()
            .addOnSuccessListener { snapshot ->
                val trains = snapshot.documents.map {
                    it.toObject(Train::class.java)!!.copy(id = it.id)
                }
                onResult(trains)
            }
    }

    fun getTasksByIds(taskIds: List<String>, onResult: (List<Task>) -> Unit) {
        if (taskIds.isEmpty()) {
            onResult(emptyList())
            return
        }

        db.collection("tasks")
            .whereIn(FieldPath.documentId(), taskIds)
            .get()
            .addOnSuccessListener { snapshot ->
                val tasks = snapshot.documents.map {
                    it.toObject(Task::class.java)!!.copy(id = it.id)
                }
                onResult(tasks)
            }
    }
}
