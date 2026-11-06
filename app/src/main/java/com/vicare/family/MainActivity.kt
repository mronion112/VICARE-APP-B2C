package com.vicare.family

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.vicare.family.ui.VicareFamilyApp
import com.vicare.family.ui.VicareFamilyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VicareFamilyTheme {
                VicareFamilyApp()
            }
        }
    }
}
