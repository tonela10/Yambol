package com.sedilant.yambol.ui.playerCard

import com.sedilant.yambol.domain.models.AbilityDomainModel

data class StatUiModel(
    val id : Int,
    val name: String,
    val value: Float,
)

fun AbilityDomainModel.toUi(): StatUiModel {
    return StatUiModel(
        name = name,
        value = value,
        id = id,
    )
}
