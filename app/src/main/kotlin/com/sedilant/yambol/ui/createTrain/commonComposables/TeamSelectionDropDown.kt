package com.sedilant.yambol.ui.createTrain.commonComposables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sedilant.yambol.R
import com.sedilant.yambol.domain.models.TeamDomainModel
import com.sedilant.yambol.ui.theme.YambolTheme

@Composable
fun TeamSelectionDropdown(
    teams: List<TeamDomainModel>,
    selectedTeamId: String,
    onTeamSelected: (String) -> Unit
) {
    if (teams.isNotEmpty()) {
        var expanded by remember { mutableStateOf(false) }
        val selectedTeam = teams.find { it.id == selectedTeamId }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.team_label),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .clickable { expanded = !expanded }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedTeam?.name ?: stringResource(R.string.select_team),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    teams.forEach { team ->
                        DropdownMenuItem(
                            text = { Text(text = team.name) },
                            onClick = {
                                onTeamSelected(team.id)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TeamSelectionDropdownPreview() {
    val mockTeams = listOf(
        TeamDomainModel(id = "1", name = "Cachos F.C."),
        TeamDomainModel(id = "2", name = "Yambol Warriors"),
        TeamDomainModel(id = "3", name = "Junior Team")
    )

    var selectedId by remember { mutableStateOf("1") }

    YambolTheme() {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            TeamSelectionDropdown(
                teams = mockTeams,
                selectedTeamId = selectedId,
                onTeamSelected = { newId -> selectedId = newId }
            )
        }
    }
}