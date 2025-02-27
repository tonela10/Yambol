package com.sedilant.yambol.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sedilant.yambol.ui.theme.YambolTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "HomeScreen")
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    YambolTheme {
        HomeScreen()
    }
}