package com.swmansion.routinetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.swmansion.routinetracker.di.DataFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        val dataFactory = DataFactory(application as android.app.Application)
        globalDatabase = dataFactory.createRoomDatabase()

        setContent {
            App()
        }
    }
}