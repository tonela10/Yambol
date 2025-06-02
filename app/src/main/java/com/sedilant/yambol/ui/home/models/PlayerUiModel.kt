package com.sedilant.yambol.ui.home.models

import com.sedilant.yambol.domain.Position

data class PlayerUiModel(
    val name: String,
    val number: String,
    val position: Position,
    val id: Int = 0
)
