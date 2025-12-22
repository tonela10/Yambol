package com.sedilant.yambol.data.firestore

data class Team(
    val id: String = "",
    val name: String = "",
    val category: String = ""
)

data class Player(
    val id: String = "",
    val name: String = "",
    val teamId: String = ""
)

data class Train(
    val id: String = "",
    val date: String = "",
    val teamId: String = "",
    val taskIds: List<String> = emptyList()
)

data class Task(
    val id: String = "",
    val title: String = "",
    val conceptIds: List<String> = emptyList()
)

data class Concept(
    val id: String = "",
    val name: String = ""
)
