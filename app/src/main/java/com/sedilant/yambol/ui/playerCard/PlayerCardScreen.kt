package com.sedilant.yambol.ui.playerCard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sedilant.yambol.ui.theme.YambolTheme

@Composable
fun PlayerCardScreen(
    modifier: Modifier = Modifier,
) {
    PlayerCardScreenStateless(
        modifier = modifier,
    )
}

@Composable
private fun PlayerCardScreenStateless(
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    ElevatedCard(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            PlayerInfoSection(
                name = "John",
                surname = "Doe",
                number = 12,
                position = "Base",
                description = "Jugador rápido y con buena visión de juego",
                shooting = 75,
                bouncing = 60,
                passing = 80,
                defense = 70
            )

            Spacer(modifier = Modifier.height(24.dp))

            ObjectivesSection(
                hasObjectives = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProgressGraphSection()
        }
    }
}

@Composable
fun PlayerInfoSection(
    name: String,
    surname: String,
    number: Int,
    position: String?,
    description: String?,
    shooting: Int,
    bouncing: Int,
    passing: Int,
    defense: Int,
) {
    Text("Información del jugador", fontSize = 18.sp, fontWeight = FontWeight.Bold)

    Text("Nombre: $name $surname")
    Text("Dorsal: $number")
    position?.let { Text("Posición: $it") }
    description?.let {
        Text("Descripción: $it")
    } ?: Button(onClick = { /* acción para añadir descripción */ }) {
        Text("Añadir descripción")
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text("Habilidades", fontWeight = FontWeight.Medium)
    // Puedes usar barras de progreso o estrellas si lo prefieres
    SkillBar("Tiro", shooting)
    SkillBar("Bote", bouncing)
    SkillBar("Pase", passing)
    SkillBar("Defensa", defense)
}

@Composable
fun SkillBar(label: String, value: Int) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label: $value/100")
        LinearProgressIndicator(
            progress = value / 100f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ObjectivesSection(hasObjectives: Boolean) {
    Text("Objetivos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    if (hasObjectives) {
        // Mostrar objetivos
        Text("• Mejorar en defensa")
        Text("• Participar más en el rebote")
    } else {
        Text("Sin objetivos actuales.")
    }
    Spacer(modifier = Modifier.height(8.dp))
    Button(onClick = { /* acción para añadir objetivo */ }) {
        Text("Añadir objetivo")
    }
}

@Composable
fun ProgressGraphSection() {
    Text("Progreso", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    // Aquí deberías usar una gráfica personalizada como un radar chart
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        PlayerRadarChart() // Tendrías que implementar este componente o usar una librería
    }
}

@Composable
fun PlayerRadarChart() {
}

@Preview(showBackground = true)
@Composable
private fun PlayerCardScreenPreview() {
    YambolTheme {
        PlayerCardScreen()
    }
}
