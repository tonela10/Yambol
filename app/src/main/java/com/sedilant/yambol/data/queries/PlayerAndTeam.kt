package com.sedilant.yambol.data.queries

import androidx.room.Embedded
import androidx.room.Relation
import com.sedilant.yambol.data.team.PlayerEntity
import com.sedilant.yambol.data.team.TeamEntity

data class PlayerAndTeam(
    @Embedded val playerEntity: PlayerEntity,
    @Relation(
        parentColumn = "team_id",
        entityColumn = "id"
    )
    val teamEntity: TeamEntity
)
