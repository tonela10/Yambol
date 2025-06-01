package com.sedilant.yambol.domain

import com.sedilant.yambol.data.entities.PlayerEntity
import com.sedilant.yambol.data.entities.TeamEntity
import com.sedilant.yambol.data.queries.AbilityRecordWithName
import com.sedilant.yambol.domain.models.AbilityDomainModel
import com.sedilant.yambol.domain.models.TeamDomainModel
import com.sedilant.yambol.ui.home.models.PlayerUiModel

fun TeamEntity.mapToDomain(): TeamDomainModel {
    return TeamDomainModel(
        name = name,
        id = id,
    )
}

fun PlayerEntity.mapToDomain(): PlayerUiModel {
    return PlayerUiModel(
        name = name,
        number = number.toString(),
        position = when (position) {
            1 -> Position.POINT_GUARD
            2 -> Position.SHOOTING_GUARD
            3 -> Position.SMALL_FORWARD
            4 -> Position.POWER_FORWARD
            5 -> Position.CENTER
            else -> Position.CENTER
        },
        id = id
    )
}

fun AbilityRecordWithName.mapToDomain(): AbilityDomainModel {
    return AbilityDomainModel(
        name = ability.name,
        value = record.value.toFloat()
    )
}

// TODO fix the mapper
enum class Position(name: String, number: Int) {
    POINT_GUARD(name = "point guard", number = 1),
    SHOOTING_GUARD(name = "shooting guard", number = 2),
    SMALL_FORWARD(name = "small forward", number = 3),
    POWER_FORWARD(name = "power forward", number = 4),
    CENTER(name = "center", number = 5)
}

