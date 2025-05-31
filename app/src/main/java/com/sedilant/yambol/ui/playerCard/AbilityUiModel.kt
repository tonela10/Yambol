package com.sedilant.yambol.ui.playerCard

import com.sedilant.yambol.domain.models.AbilityDomainModel

data class AbilityUiModel(
    val name: String,
    val value: Float,
)

fun AbilityDomainModel.toUi(): AbilityUiModel {
    return AbilityUiModel(
        name = name,
        value = value
    )
}
