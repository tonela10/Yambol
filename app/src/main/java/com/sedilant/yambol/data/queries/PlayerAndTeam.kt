package com.sedilant.yambol.data.queries

import androidx.room.Embedded
import androidx.room.Relation
import com.sedilant.yambol.data.PlayerEntity
import com.sedilant.yambol.data.TeamEntity

data class PlayerAndTeam(
    @Embedded val playerEntity: PlayerEntity,
    @Relation(
        parentColumn = "team_id",
        entityColumn = "id"
    )
    val teamEntity: TeamEntity
)
