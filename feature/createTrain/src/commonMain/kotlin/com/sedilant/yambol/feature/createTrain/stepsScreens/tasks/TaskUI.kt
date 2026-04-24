package com.sedilant.yambol.feature.createTrain.stepsScreens.tasks

import com.sedilant.yambol.feature.createTrain.stepsScreens.concepts.Concept

data class TaskUI(
    val id: String,
    val name: String,
    val concepts: List<Concept> = emptyList(),
    val description: String = "",
    val variation: String = "",
    val duration: String = ""
)
