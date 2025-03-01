package com.sedilant.yambol.ui.home.models

data class TeamUiModel (
    val name: String,
    val players: String, // TODO change this for a list of players
)

class FakeDataTeamsList(

){
    fun getTeams(): List<TeamUiModel>{
        return listOf(TeamUiModel("Utebo", "Tonela"), TeamUiModel("Olivar", "Luis")) }
}