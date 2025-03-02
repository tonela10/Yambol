package com.sedilant.yambol.ui.home.models

data class TeamUiModel (
    val name: String,
    val players: String, // TODO change this for a list of players
)

class FakeDataTeamsList(

){
    fun getTeams(): List<TeamUiModel>{
        return listOf(TeamUiModel("Team 1", "Tonela"), TeamUiModel("Team 2", "Luis")) }
}