package com.swmansion.routinetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.swmansion.routinetracker.di.LocalAppContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        val appContainer = (application as RoutineTrackerApplication).appContainer

        setContent { CompositionLocalProvider(LocalAppContainer provides appContainer) { App() } }
    }
}
