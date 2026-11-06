package com.example.eyetracker_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.eyetracker_app.ui.EyeTrackerApp
import com.example.eyetracker_app.ui.EyeTrackerViewModel
import com.example.eyetracker_app.ui.theme.EyeTrackerAppTheme

class MainActivity : ComponentActivity() {
    private val eyeTrackerViewModel: EyeTrackerViewModel by viewModels {
        EyeTrackerViewModel.Factory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EyeTrackerAppTheme {
                EyeTrackerApp(viewModel = eyeTrackerViewModel)
            }
        }
    }
}
