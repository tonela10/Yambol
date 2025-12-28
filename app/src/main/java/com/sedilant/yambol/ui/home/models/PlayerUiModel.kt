package com.sedilant.yambol.ui.home.models

data class PlayerUiModel(
    val name: String,
    val number: String,
    val id: Long = 0,
    val teamId: Long // This should be only in the domainPlayerModel
)
