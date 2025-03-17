package com.weatherapp.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.weatherapp.core.MockPreferencesManager
import com.weatherapp.core.PreferencesManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun NotificationsScreen(
    viewModel: NotificationsStateManager = hiltViewModel<NotificationsViewModel>(),
    preferencesManager: PreferencesManager,
    onBack: () -> Unit
) {
    val backgroundColor = Color(0xff6b9cff)
    val state by viewModel.state.collectAsState()
    var windThreshold by remember { mutableStateOf(state.windThreshold.toString()) }

    LaunchedEffect(windThreshold) {
        viewModel.onIntent(
            NotificationsIntent.ToggleWindAlert(
                state.windAlertEnabled,
                windThreshold.toFloatOrNull() ?: 0f
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onBack() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Notifications",
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rain Alert",
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = state.rainAlertEnabled,
                onCheckedChange = { viewModel.onIntent(NotificationsIntent.ToggleRainAlert(it)) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Wind Alert",
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = state.windAlertEnabled,
                onCheckedChange = {
                    viewModel.onIntent(
                        NotificationsIntent.ToggleWindAlert(
                            it,
                            windThreshold.toFloatOrNull() ?: 0f
                        )
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.windAlertEnabled) {
            OutlinedTextField(
                value = windThreshold,
                onValueChange = { windThreshold = it },
                label = { Text("Wind Speed Threshold (m/s)", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.LightGray
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    NotificationsScreen(
        viewModel = object : NotificationsStateManager {
            override val state = MutableStateFlow(NotificationsState(windAlertEnabled = true))
            override val effect = MutableSharedFlow<NotificationsEffect>()
            override fun onIntent(intent: NotificationsIntent) {}
        },
        preferencesManager = MockPreferencesManager(),
        onBack = {}
    )
}
