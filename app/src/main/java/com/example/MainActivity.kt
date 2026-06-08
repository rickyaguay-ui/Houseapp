package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.ui.MainScreen
import com.example.ui.HouseViewModel
import com.example.ui.theme.TheHouseTheme
import com.example.ui.theme.parseColorHex

class MainActivity : ComponentActivity() {
    
    private val viewModel: HouseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable Edge-to-Edge full content bleeding
        enableEdgeToEdge()
        
        setContent {
            val state by viewModel.uiState.collectAsState()
            val church = state.selectedChurch
            
            val primaryColor = remember(church.brandColorHex) {
                parseColorHex(church.brandColorHex)
            }
            val backgroundColor = remember(church.bgHex) {
                parseColorHex(church.bgHex)
            }
            val surfaceColor = remember(backgroundColor, primaryColor) {
                // Determine beautiful card blend based on theme background and primary color
                val r = ((backgroundColor.red * 0.85f + primaryColor.red * 0.15f) * 255).toInt().coerceIn(10, 40)
                val g = ((backgroundColor.green * 0.85f + primaryColor.green * 0.15f) * 255).toInt().coerceIn(10, 40)
                val b = ((backgroundColor.blue * 0.85f + primaryColor.blue * 0.15f) * 255).toInt().coerceIn(10, 40)
                androidx.compose.ui.graphics.Color(r, g, b)
            }

            TheHouseTheme(
                primaryColor = primaryColor,
                backgroundColor = backgroundColor,
                surfaceColor = surfaceColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
