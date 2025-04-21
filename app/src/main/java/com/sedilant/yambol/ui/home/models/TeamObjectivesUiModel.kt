package com.sedilant.yambol.ui.home.models

data class TeamObjectivesUiModel(
    val description: String,
    val isFinish: Boolean,
    val id: Int,
    val isEditMenuShown: Boolean = false,
)
