package com.sedilant.yambol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.sedilant.yambol.ui.NavigationBottomBar
import com.sedilant.yambol.ui.home.HomeScreen
import com.sedilant.yambol.ui.theme.YambolTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YambolTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    NavigationBottomBar(modifier = Modifier.padding(innerPadding))
                    HomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
