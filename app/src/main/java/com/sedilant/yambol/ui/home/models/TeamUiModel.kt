package com.sedilant.yambol.ui.home.models

data class TeamUiModel(
    val name: String,
    val players: List<PlayerUiModel> = emptyList(),
    val tasks: List<TaskUiModel> = emptyList()
)
