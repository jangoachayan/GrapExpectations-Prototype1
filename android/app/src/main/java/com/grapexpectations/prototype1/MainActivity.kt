package com.grapexpectations.prototype1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.grapexpectations.prototype1.ui.theme.GrapExpectationsPrototype1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GrapExpectationsPrototype1Theme(darkTheme = true) { // Force dark theme for premium feel
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    com.grapexpectations.prototype1.ui.auth.LoginScreen(
                        onLoginClick = { email, password ->
                            // TODO: Handle login
                        },
                        onSignUpClick = { email, password ->
                            // TODO: Handle sign up
                        }
                    )
                }
            }
        }
    }
}
