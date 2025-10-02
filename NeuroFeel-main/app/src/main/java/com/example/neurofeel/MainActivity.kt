package com.example.neurofeel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import com.example.neurofeel.ui.theme.NeuroFeelTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.neurofeel.ChatbotFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)      // Force light mode
        setContentView(R.layout.activity_main)

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)

        replaceFragment(HomeFragment())

        navView.setOnItemSelectedListener { menuItem ->
            val itemId = menuItem.itemId

            if (itemId == R.id.home) {
                replaceFragment(HomeFragment())
            }
            else if (itemId == R.id.chatBot){
                replaceFragment(ChatbotFragment())
            }
            else{
                replaceFragment(ProfileFragment())
            }

            true
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
