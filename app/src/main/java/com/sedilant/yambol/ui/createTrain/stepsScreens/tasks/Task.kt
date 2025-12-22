package com.sedilant.yambol.ui.createTrain.stepsScreens.tasks

data class TaskUI(
    val id: String,
    val name: String,
    val concepts: List<String> = emptyList(),
    val description: String = "",
    val variation: String = "",
    val duration: String = ""
)