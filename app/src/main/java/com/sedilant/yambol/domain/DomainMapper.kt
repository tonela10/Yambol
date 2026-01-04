package com.sedilant.yambol.domain

import com.sedilant.yambol.data.team.TaskEntity
import com.sedilant.yambol.data.team.TeamEntity
import com.sedilant.yambol.data.team.player.PlayerEntity
import com.sedilant.yambol.domain.models.TeamDomainModel
import com.sedilant.yambol.domain.models.TaskDomain
import com.sedilant.yambol.ui.home.models.PlayerUiModel

fun TeamEntity.mapToDomain(): TeamDomainModel {
    return TeamDomainModel(
        name = name,
        id = id,
    )
}

fun PlayerEntity.mapToUI(): PlayerUiModel {
    return PlayerUiModel(
        name = name,
        number = number.toString(),
        id = id,
        teamId = teamId,
    )
}

fun PlayerUiModel.mapToEntity(): PlayerEntity {
    return PlayerEntity(
        name = name,
        number = number.toInt(),
        id = id,
        teamId = teamId,
    )
}


fun TaskEntity.mapToDomain(): TaskDomain {
    return TaskDomain(
        trainingTaskId = id,
        name = name,
        concepts = conceptsId,
        description = description,
        variables = variables,
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

