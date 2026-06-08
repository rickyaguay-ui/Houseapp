package com.example.ui.rooms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.HouseViewModel
import com.example.ui.RoomView

@Composable
fun BattlePlanScreen(viewModel: HouseViewModel) {
    val state by viewModel.uiState.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "DAILY BATTLE PLAN",
            color = primaryColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.battlePlans) { plan ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = plan.title, fontWeight = FontWeight.Bold, color = primaryColor)
                        Text(text = plan.verse, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = plan.action, fontSize = 14.sp)
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.navigateTo(RoomView.FOYER) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("RETURN TO FOYER")
        }
    }
}
