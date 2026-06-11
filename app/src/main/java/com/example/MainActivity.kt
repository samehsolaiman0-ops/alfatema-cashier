package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.di.AppModule
import com.example.ui.screens.FinalizeScreen
import com.example.ui.screens.HoldScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.SalesScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.SalesViewModel

class MainActivity : ComponentActivity() {

    private val salesViewModel: SalesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Base Initialization of Room Database + Settings pref references first
        AppModule.initialize(this)

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                // Enforce RTL layout direction universally for perfect Arabic POS alignment
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val uiState by salesViewModel.uiState.collectAsState()

                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        
                        // Local high-fidelity state router transitions
                        when (uiState.activeStep) {
                            "LOGIN" -> {
                                LoginScreen(
                                    viewModel = salesViewModel,
                                    onLoginSuccess = { salesViewModel.setStep("SALES") }
                                )
                            }
                            "SALES" -> {
                                SalesScreen(
                                    viewModel = salesViewModel,
                                    onNavigateSetting = { salesViewModel.setStep("SETTINGS") },
                                    onNavigateHoldList = { salesViewModel.setStep("HOLDS") },
                                    onNavigateSearch = { salesViewModel.setStep("SEARCH") },
                                    onTriggerFinalize = { salesViewModel.setStep("FINALIZE") },
                                    onTriggerLogout = { salesViewModel.setStep("LOGIN") }
                                )
                            }
                            "SETTINGS" -> {
                                SettingsScreen(
                                    viewModel = salesViewModel,
                                    onBack = { salesViewModel.setStep("SALES") }
                                )
                            }
                            "HOLDS" -> {
                                HoldScreen(
                                    viewModel = salesViewModel,
                                    onBack = { salesViewModel.setStep("SALES") }
                                )
                            }
                            "SEARCH" -> {
                                SearchScreen(
                                    viewModel = salesViewModel,
                                    onBack = { salesViewModel.setStep("SALES") }
                                )
                            }
                            "FINALIZE" -> {
                                FinalizeScreen(
                                    viewModel = salesViewModel,
                                    onBack = { salesViewModel.setStep("SALES") }
                                )
                            }
                            else -> {
                                // Default fallback inside login security gate
                                LoginScreen(
                                    viewModel = salesViewModel,
                                    onLoginSuccess = { salesViewModel.setStep("SALES") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
