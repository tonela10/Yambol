package com.sedilant.yambol.data.queries

import androidx.room.Embedded
import androidx.room.Relation
import com.sedilant.yambol.data.team.TeamEntity
import com.sedilant.yambol.data.team.TeamObjectivesEntity

data class ObjectivesAndTeam (
    @Embedded val objectivesEntity: TeamObjectivesEntity,
    @Relation(
        parentColumn = "team_id",
        entityColumn = "id"
    )
    val teamEntity: TeamEntity,
)
