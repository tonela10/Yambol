package com.sedilant.yambol.domain

import com.sedilant.yambol.data.PlayerEntity
import com.sedilant.yambol.data.TeamEntity
import com.sedilant.yambol.ui.home.models.PlayerUiModel

fun TeamEntity.mapToDomain(): TeamDomainModel {
    return TeamDomainModel(
        name = name,
        id = id,
    )
}

// TODO create a PlayerDomainModel
fun PlayerEntity.mapToDomain(): PlayerUiModel {
    return PlayerUiModel(
        name = name,
        number = number.toString(),
        position = when (position) {
            1 -> Position.POINT_GUARD.name
            2 -> Position.SHOOTING_GUARD.name
            3 -> Position.SMALL_FORWARD.name
            4 -> Position.POWER_FORWARD.name
            5 -> Position.CENTER.name
            else -> ""
        }
    )
}

// TODO change the hardcode strings with resources
enum class Position(name: String, number: Int) {
    POINT_GUARD(name = "point guard", number = 1),
    SHOOTING_GUARD(name = "shooting guard", number = 2),
    SMALL_FORWARD(name = "small forward", number = 3),
    POWER_FORWARD(name = "power forward", number = 4),
    CENTER(name = "center", number = 5)
}

