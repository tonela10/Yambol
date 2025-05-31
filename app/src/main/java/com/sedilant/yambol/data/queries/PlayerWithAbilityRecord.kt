package com.sedilant.yambol.data.queries

import androidx.room.Embedded
import androidx.room.Relation
import com.sedilant.yambol.data.entities.AbilityNameEntity
import com.sedilant.yambol.data.entities.AbilityRecordEntity
import com.sedilant.yambol.data.entities.PlayerEntity


data class PlayerWithAbilityRecords(
    @Embedded val player: PlayerEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "player_id",
        entity = AbilityRecordEntity::class,
        projection = ["id"]
    )
    val abilityRecordIds: List<Int> // optional, just IDs if you want lightweight
)

data class AbilityRecordWithName(
    @Embedded val record: AbilityRecordEntity,

    @Relation(
        parentColumn = "ability_id",
        entityColumn = "id"
    )
    val ability: AbilityNameEntity
)
