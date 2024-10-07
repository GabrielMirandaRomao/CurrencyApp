package com.gabriel_miranda.currencyapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.gabriel_miranda.currencyapp.di.initializeKoin
import com.gabriel_miranda.currencyapp.ui.HomeScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {

    initializeKoin()

    MaterialTheme() {
        Navigator(HomeScreen())
    }
}